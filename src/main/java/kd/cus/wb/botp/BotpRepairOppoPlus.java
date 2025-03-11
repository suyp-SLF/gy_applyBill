package kd.cus.wb.botp;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.LinkSetElement;
import kd.bos.entity.botp.runtime.TableDefine;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.form.IPageCache;
import kd.bos.mvc.form.ClientViewProxy;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.botp.BFTrackerServiceHelper;
import kd.bos.servicehelper.botp.ConvertServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.fi.arapcommon.helper.BotpRelationHelper;

import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 修复botp操作插件
 * @author suyp
 *
 */
public class BotpRepairOppoPlus extends AbstractOperationServicePlugIn {

    @Override
    public void beforeExecuteOperationTransaction(BeforeOperationArgs e) {
    	
    	
    	//1219630887340081152 1219631141498127360
//    	DynamicObject pro_dy1 = BusinessDataServiceHelper.loadSingle("bfgy_proj_wb_pmb","bfgy_proj_wb_pmb_lk.seq",new QFilter[] {new QFilter("bfgy_projno", QCP.equals, "LM5790")});
//    	DynamicObject pro_dy = BusinessDataServiceHelper.loadSingle("1219630887340081152","bfgy_proj_wb_pmb");
//    	DynamicObject con_dy1 = BusinessDataServiceHelper.loadSingle("bfgy_wbexportcontract","",new QFilter[] {new QFilter("billno", QCP.equals, "MK123456")});
//    	DynamicObject con_dy = BusinessDataServiceHelper.loadSingle("1219631141498127360","bfgy_wbexportcontract");
//    	
//    	TableDefine targetTableDefine = EntityMetadataCache.loadTableDefine("bfgy_proj_wb_pmb", "bfgy_proj_wb_pmb");
//    	String name = targetTableDefine.getEntityKey();
//    	
//    	TableDefine sourceTableDefine = EntityMetadataCache.loadTableDefine("bfgy_wbexportcontract", "bfgy_wbexportcontract");
//    	
//    	LinkSetElement linkSet = EntityMetadataCache.getLinkSet("bfgy_proj_wb_pmb");
//    	
//    	linkSet.getItems().forEach(m->{
//    		System.out.println(m.getLinkEntityKey());
//    	});
//    	
//    	DynamicObjectCollection cols = pro_dy.getDynamicObjectCollection("bfgy_econstractentity");
//    	
//    	DynamicObjectCollection lk = cols.get(0).getDynamicObjectCollection("bfgy_econstractentity_lk");
//    	
////    	lk.clear();
//    	
//    	 DynamicObject linkRow = new DynamicObject(lk.getDynamicObjectType());
//    	 lk.add(linkRow);
//    	 
//
//         // 在lk行中，记录源单分录表格编码、源单内码、源单分录内码
//         linkRow.set("bfgy_econstractentity_lk" + "_stableid", sourceTableDefine.getTableId());		// 源单分录表格编码：以此标识源单类型及单据体
//         linkRow.set("bfgy_econstractentity_lk" + "_sbillid", con_dy.getPkValue());		// 源单内码
//         linkRow.set("bfgy_econstractentity_lk" + "_sid", con_dy.getPkValue());			// 源单分录行内码
//    	
//    	
//    	
//         SaveServiceHelper.saveOperate(
//                 "bfgy_proj_wb_pmb", 					// 目标单主实体编码
//                 new DynamicObject[] {pro_dy}, 	// 目标单数据包
//         OperateOption.create());				// 操作参数，可通过option传入各种自定义参数
//    	System.out.println(1);
    	List<ExtendedDataEntity> select = e.getSelectedRows();

        List<Object> select_ids = select.stream().map(i -> i.getBillPkId()).collect(Collectors.toList());

        String[] select_fields = {
                "bfgy_billnofield",
                "bfgy_success",
                "bfgy_textfield",
                "bfgy_textfield1",
                "bfgy_textfield2",
                "bfgy_textfield3",
                "bfgy_textfield4",
                "bfgy_textfield5",
                "bfgy_textfield6",
                "bfgy_textfield7",
                "bfgy_textfield8",
                "bfgy_textfield9",
                "bfgy_textfield10",
                "bfgy_textfield11",
                "bfgy_textfield12",
                "bfgy_textfield13",
                "bfgy_textfield14"
        };

        DynamicObject[] cols = BusinessDataServiceHelper.load("bfgy_botppool", StringUtils.join(select_fields, ","), new QFilter[]{new QFilter("id", QCP.in, select_ids)});

        int suc_num = 0;
        int fal_num = 0;
        for(DynamicObject m : cols) {
            try {
                String application = m.getString("bfgy_textfield");//目的单应用
                BotpRelationHelper helper = new BotpRelationHelper(application);
                String type = m.getString("bfgy_textfield6");//类型
                if ("unite".equalsIgnoreCase(type)) {
                    String E_dstBillLogo = m.getString("bfgy_textfield3");
                    long E_dstBillId = m.getLong("bfgy_textfield8");
                    String E_srcBillLogo = m.getString("bfgy_textfield1");
                    List<Long> E_srcBillIds = Arrays.asList(m.getString("bfgy_textfield7").split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());

                    helper.saveApRation4Unite(E_dstBillLogo, E_dstBillId, E_srcBillLogo, E_srcBillIds);
                } else if ("entry".equalsIgnoreCase(type)) {
                    String E_dstBillLogo = m.getString("bfgy_textfield3");
                    long E_dstBillIdEx = m.getLong("bfgy_textfield10");
                    String E_dstEntryNameEx = m.getString("bfgy_textfield12");
                    long E_dstEntryIdEx = m.getLong("bfgy_textfield14");
                    String E_srcBillLogo = m.getString("bfgy_textfield1");
                    long E_srcBillIdEx = m.getLong("bfgy_textfield9");
                    String E_srcEntryNameEx = m.getString("bfgy_textfield11");
                    long E_srcEntryIdEx = m.getLong("bfgy_textfield13");

                    helper.saveAllRation4Entry(E_dstBillLogo, E_dstBillIdEx, E_dstEntryNameEx, E_dstEntryIdEx, E_srcBillLogo, E_srcBillIdEx, E_srcEntryNameEx, E_srcEntryIdEx);
                }

                m.set("bfgy_textfield5","成功");
                m.set("bfgy_success",true);
                suc_num ++;
            } catch (Exception ex) {
                //失败
                m.set("bfgy_textfield5","失败：" + ex.getMessage());
                m.set("bfgy_success",false);
                fal_num ++;
            }
        }
        SaveServiceHelper.save(cols);
        e.setCancelMessage("成功" + suc_num + "条！失败" + fal_num + "条！");
    }
}
