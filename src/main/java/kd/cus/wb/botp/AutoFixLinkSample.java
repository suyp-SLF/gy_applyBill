package kd.cus.wb.botp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.BillEntityType;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.botp.runtime.TableDefine;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class AutoFixLinkSample {
    /**
     * 手动修复下游单据与源单的关联关系
     *
     * @param targetEntityNumber *下游单据标识，如 demo_bill1
     * @param targetEntryKey	*下游单据的关联主单据体：下游单据可能有多个单据体，本函数只能修复其中一个单据体的关联关系
     * @param srcBillTypeFld 	下游单据头上，记录源单类型的字段名：据此确定每张单据的源单类型；实际应用中可以改为直接传入源单类型
     * @param srcBillNoFld		下游单据头上，记录源单单据编号的字段名：据此确定每张单据的源单
     * @param srcEntryFld		下游单据头上，记录源单单据体标识的字段名：据此确定源单关联主单据体；实际应用中可以改为直接传入源单单据体名
     * @param srcRowSeqFld		下游单据体上，记录源单单据体行号的字段名：据此定位源单行
     * @param targetBillId		*下游单据内码集合
     *
     * @remark
     * 1. 为了实现本函数的演示效果，需要在目标单上，建立有源单类型、源单编号、源单单据体名、源单行号等四个字段，以定位源单行；
     *    实际应用中，可能源单类型、源单单据体名是固定的，不需要增加字段记录
     * 2. 为了避免演示代码过于复杂，本函数只处理单张目标单；
     *    实际应用中需要调整为支持批量处理
     */
    public void fixRowLink(String targetEntityNumber,
                           String targetEntryKey,
                           String srcBillTypeFld, String srcBillNoFld,
                           String srcEntryFld, String srcRowSeqFld,
			Object targetBillId) {

        // TODO 参数检查：检查各参数是否传入了合法的值，本演示代码，各参数必填，不允许空

        // 读取需修复的目标单
        DynamicObject targetBillObj = BusinessDataServiceHelper.loadSingle(targetBillId, targetEntityNumber);

        // 获取目标单单据体的实体表格定义：主实体 + 单据体 -> 对应一个唯一的实体表格定义
        TableDefine targetTableDefine = EntityMetadataCache.loadTableDefine(targetEntityNumber, targetEntryKey);

        // 获取源单单据体的表格定义：记录关联关系时，需要用到此对象中的tableId值，用一个长整数值唯一标识源单及单据体
        TableDefine srcTableDefine = this.loadSrcTableDefine(targetBillObj, srcBillTypeFld, srcEntryFld);

        String srcBillNo = targetBillObj.getString(srcBillNoFld);

        // 根据源单编号，读取源单数据
        DynamicObject[] sourceBillObjs = this.loadSourceBill(srcTableDefine, srcBillNo);

        this.createLinkEntity(targetTableDefine, targetBillObj, srcTableDefine, sourceBillObjs, srcRowSeqFld);

        // 调用目标单的保存操作，保存维护好的关联子实体行数据，并自动调用反写引擎，创建关联关系及反写：
        SaveServiceHelper.saveOperate(
                targetEntityNumber, 					// 目标单主实体编码
                new DynamicObject[] {targetBillObj}, 	// 目标单数据包
        OperateOption.create());				// 操作参数，可通过option传入各种自定义参数
    }

    /**
     * 从目标单单据头字段中，获取源单类型、源单单据体字段值，据此访问数据库，获取源单单据体表格定义
     *
     * @param targetBillObj 目标单数据包
     * @param srcBillTypeFld 目标单上，记录源单单据类型的字段名
     * @param srcEntryFld 目标单上，记录源单单据体标识的字段名
     *
     * @return 关联的源单及单据体表格定义
     */
    private TableDefine loadSrcTableDefine( DynamicObject targetBillObj, String srcBillTypeFld, String srcEntryFld) {
        // 提取源单类型、源单单据编号字段值
		Object srcBillTypeFldValue = targetBillObj.get(srcBillTypeFld);
        String srcEntityNumber = "";
        if (srcBillTypeFldValue instanceof DynamicObject) {
            srcEntityNumber = (String)((DynamicObject)srcBillTypeFldValue).getPkValue();
        }
		else {
            srcEntityNumber = (String)srcBillTypeFldValue;
        }
        String srcEntryKey = targetBillObj.getString(srcEntryFld);
        if (StringUtils.isBlank(srcEntityNumber)
                || StringUtils.isBlank(srcEntryKey)) {
            return null;
        }

        // 获取源单单据体的表格编码：记录关联关系时，需要用此编码，标识源单及单据体
        TableDefine srcTableDefine = EntityMetadataCache.loadTableDefine(srcEntityNumber, srcEntryKey);

        return srcTableDefine;
    }

    /**
     * 从目标单中提取源单编号，读取出源单数据包
     *
     * @param srcTableDefine 源单单据体实体表格定义：对象中包含了单据主实体标识、单据体标识、单据体tableId等内容
     * @param srcBillNo 源单单据编号
     *
     * @return 源单数据包，仅包含源单内码、源单编号、源单行内码、行号
     */
    private DynamicObject[] loadSourceBill(TableDefine srcTableDefine, String srcBillNo) {

        BillEntityType sourceMainType = (BillEntityType)EntityMetadataCache.getDataEntityType(srcTableDefine.getEntityNumber());

        // 读取源单：仅读取需要用到的字段：源单内码、源单单据编号、源单行内码、源单行号
        Set<String> selectFields = new HashSet<>();
        selectFields.add("id");
        selectFields.add(sourceMainType.getBillNo());
        selectFields.add(srcTableDefine.getEntityKey() + ".id");
        selectFields.add(srcTableDefine.getEntityKey() + ".seq");

        QFilter filter = new QFilter(sourceMainType.getBillNo(), QCP.equals, srcBillNo);
        DynamicObject[] sourceBillObjs = BusinessDataServiceHelper.load(
                srcTableDefine.getEntityNumber(),
                StringUtils.join(selectFields.toArray(), ","),
                new QFilter[] {filter});
        return sourceBillObjs;
    }

    /**
     * 修改目标单，为每行单据体，添加关联子实体数据行，记录关联的源单行内码
     *
     * @param targetTableDefine 目标单单据体表格定义：包含了目标单标识、单据体标识
     * @param targetBillObj 目标单
     * @param srcTableDefine 源单单据体表格定义：包含了源单标识、单据体标识、tableid
     * @param sourceBillObjs 源单
     * @param srcRowSeqFld 目标单单据体上，记录源单行号的字段名：需要基于此字段值定位源单行
     *
     * @remark
     * 根据目标单分录行上，记录的源单行号字段值，匹配找到源单行，把源单行内码，记录在目标单分录行上
     */
    private void createLinkEntity(
            TableDefine targetTableDefine, DynamicObject targetBillObj,
            TableDefine srcTableDefine, DynamicObject[] sourceBillObjs,
            String srcRowSeqFld) {

        // 循环分析源单行，提取源单行号、源单内码、分录行内码的关系，放在字典中备用: key = 源单行号； value = [源单内码、分录行内码]
        Map<Integer, Object[]> srcRowIds = new HashMap<>();
        for(DynamicObject srcObj : sourceBillObjs) {
            DynamicObjectCollection srcRows = srcObj.getDynamicObjectCollection(srcTableDefine.getEntityKey());
            for (DynamicObject srcRow : srcRows) {
                Integer srcRowSeq = srcRow.getInt("seq");
				Object[] srcRowId = new Object[] {srcObj.getPkValue(), srcRow.getPkValue()};
                srcRowIds.put(srcRowSeq, srcRowId);
            }
        }

        Long srcTableId = srcTableDefine.getTableId();

        // 拼接处关联子实体标识：如果是单据头下的lk子表，固定使用billhead_lk；如果是单据体下的lk子表，用单据体标识+lk
        String lkEntryKey = targetTableDefine.getEntityKey() + "_lk";

        // 循环分析下游单的单据体行，逐一建立起与源单行的关联关系
        DynamicObjectCollection targetRows = targetBillObj.getDynamicObjectCollection(targetTableDefine.getEntityKey());
        for(DynamicObject targetRow : targetRows) {
            // 获取下级_lk子实体行
            DynamicObjectCollection linkRows = targetRow.getDynamicObjectCollection(lkEntryKey);
            if (!linkRows.isEmpty()) {
                // 已经有关联源单，无需再重建
                continue;
            }

            // 寻找匹配的源单行：提取本行的匹配字段值，和源单行进行比对
            Integer srcRowSeq = targetRow.getInt(srcRowSeqFld);
            if (srcRowIds.containsKey(srcRowSeq)) {
                // 找到了匹配的行，创建一条_lk子实体上数据，记录源单内码
                DynamicObject linkRow = new DynamicObject(linkRows.getDynamicObjectType());
                linkRows.add(linkRow);

                // 在lk行中，记录源单分录表格编码、源单内码、源单分录内码
				Object[] srcRowId = srcRowIds.get(srcRowSeq);
                linkRow.set(lkEntryKey + "_stableid", srcTableId);		// 源单分录表格编码：以此标识源单类型及单据体
                linkRow.set(lkEntryKey + "_sbillid", srcRowId[0]);		// 源单内码
                linkRow.set(lkEntryKey + "_sid", srcRowId[1]);			// 源单分录行内码
            }
        }
    }
}
