package kd.cus.wb.fi.revcm;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.entity.BillEntityType;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.ExtendedDataEntitySet;
import kd.bos.entity.botp.plugin.AbstractConvertPlugIn;
import kd.bos.entity.botp.plugin.args.AfterBuildDrawFilterEventArgs;
import kd.bos.entity.botp.plugin.args.AfterConvertEventArgs;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.property.EntryProp;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.fi.arapcommon.helper.BaseDataHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CkfpToSrqrConvertPlugin extends AbstractConvertPlugIn {

    @Override
    public void afterBuildDrawFilter(AfterBuildDrawFilterEventArgs e) {
        long userId = UserServiceHelper.getCurrentUserId();
        QFilter[] qFilters = new QFilter[]{new QFilter("bfgy_teammemberentity.bfgy_mname.id",QCP.equals,userId)};
        DynamicObjectCollection dycols = QueryServiceHelper.query("bfgy_proj_wb_pmb", "bfgy_projno,bfgy_teammemberentity.bfgy_mname", qFilters);
        List<String> numbers = dycols.stream().map(i -> i.getString("bfgy_projno")).collect(Collectors.toList());
        QFilter filter = e.getPlugFilter();
        if (null != filter) {
            filter.and(new QFilter("bfgy_projectno", QCP.in, numbers));
            e.setPlugFilter(filter);
        }else {
            QFilter qFilter_ckfp = new QFilter("bfgy_projectno", QCP.in, numbers);
            e.setPlugFilter(qFilter_ckfp);
        }
    }
    
    @Override
    public void afterConvert(AfterConvertEventArgs e) {
        super.afterConvert(e);
        ExtendedDataEntitySet entitySet = e.getTargetExtDataEntitySet();
        ExtendedDataEntity[] entities = entitySet.FindByEntityKey("ar_revcfmbill");
        for (int i = 0; i < entities.length; i++) {
            ExtendedDataEntity entity = entities[i];
            DynamicObject dataEntity = entity.getDataEntity();
            DynamicObjectCollection bfgy_entryentity = dataEntity.getDynamicObjectCollection("bfgy_entryentity");
            Object bfgy_projectno = dataEntity.get("bfgy_projectnostr");
            QFilter[] qfilters = new QFilter[]{new QFilter("bfgy_projno", QCP.equals, bfgy_projectno)};
            DynamicObject[] dynamicObjects = BusinessDataServiceHelper.load("bfgy_proj_wb_pmb", "bfgy_customers,bfgy_prate", qfilters);
            if (dynamicObjects !=null && dynamicObjects.length>0) {
                DynamicObject bfgy_customers = dynamicObjects[0].getDynamicObject("bfgy_customers");
                Object bfgy_prate = dynamicObjects[0].get("bfgy_prate");
                dataEntity.set("asstact",bfgy_customers);
                dataEntity.set("bfgy_promtax",bfgy_prate);
            }
            //合同号转合同基础资料
            Object bfgy_excontractno = dataEntity.get("bfgy_excontractnostr");
            QFilter[] htQfilters = new QFilter[]{new QFilter("number", QCP.equals, bfgy_excontractno)};
            DynamicObject[] htDynamicObjects = BusinessDataServiceHelper.load("bfgy_saleorderbd", "id,number,name", htQfilters);
            if (htDynamicObjects !=null && htDynamicObjects.length>0) {
                dataEntity.set("bfgy_excontractno",htDynamicObjects[0]);
            }else{
                dataEntity.set("bfgy_excontractno",null);
            }
            //项目号转项目基础资料
            QFilter[] xmQfilters = new QFilter[]{new QFilter("number", QCP.equals, bfgy_projectno)};
            DynamicObject[] xmDynamicObjects = BusinessDataServiceHelper.load("bd_project", "id,number,name", xmQfilters);
            if (xmDynamicObjects !=null && xmDynamicObjects.length>0) {
                dataEntity.set("bfgy_projectno",xmDynamicObjects[0]);
            }else{
                dataEntity.set("bfgy_projectno",null);
            }
			
			// 获取毛利率
            QFilter rateFilter1= new QFilter("bfgy_ratepronumber", QCP.equals, bfgy_projectno);
            QFilter rateFilter2 = new QFilter("bfgy_lastversion", QCP.equals, true);
            DynamicObject[] rateObjects = BusinessDataServiceHelper.load("bfgy_proj_wb_rate", "bfgy_preentryentity.bfgy_prermbamount", rateFilter1.and(rateFilter2).toArray());
            if (rateObjects != null && rateObjects.length > 0) {
                DynamicObject rateObject = rateObjects[0];
                if (rateObject.getDynamicObjectCollection("bfgy_preentryentity").size() == 4 && rateObject.getDynamicObjectCollection("bfgy_preentryentity").get(3).get("bfgy_prermbamount") != null) {
                    BigDecimal rate = rateObject.getDynamicObjectCollection("bfgy_preentryentity").get(3).getBigDecimal("bfgy_prermbamount");
                    dataEntity.set("bfgy_promtax", rate.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                }
            }

//            dataEntity.set("bfgy_amountfield3",dataEntity.get("bfgy_amountfield7"));//可确认应收,初始值=本单【本次确认收入金额】
            dataEntity.set("exratedate",dataEntity.get("bizdate"));//汇率日期默认为本单【确认日期】
            String bfgy_domestic = dataEntity.getString("bfgy_domestic");
            String bfgy_notreturn = dataEntity.getString("bfgy_notreturn");
            if ("1".equalsIgnoreCase(bfgy_domestic)) {
                Object bfgy_outrecnum = dataEntity.getString("bfgy_outrecnum");//出口发票单据编号
                QFilter[] nmqfilters = new QFilter[]{new QFilter("bfgy_invoiceno", QCP.equals, bfgy_outrecnum)};
                String selectorProperties = "bfgy_domestictrade.bfgy_innername,bfgy_domestictrade.bfgy_innertaxrate,bfgy_domestictrade.bfgy_innertax";
                DynamicObject[] nmdynamicObjects = BusinessDataServiceHelper.load("bfgy_wb_exportinvoice", selectorProperties, nmqfilters);
                if (nmdynamicObjects != null && nmdynamicObjects.length>0) {
                    DynamicObjectCollection bfgy_domestictrade = nmdynamicObjects[0].getDynamicObjectCollection("bfgy_domestictrade");
                    if (bfgy_domestictrade!=null && bfgy_domestictrade.size()>0) {
                        EntryProp prop1 = (EntryProp) EntityMetadataCache.getDataEntityType("ar_revcfmbill").findProperty("bfgy_entryentity");
                        DynamicObjectType dt1 = prop1.getDynamicCollectionItemPropertyType();
                        List list = new ArrayList();
                        BigDecimal amounttotal = new BigDecimal(0);
                        if (bfgy_domestictrade != null && bfgy_domestictrade.size()>0) {
                            for (int j = 0; j < bfgy_domestictrade.size(); j++) {
                                DynamicObject dynamicObject = bfgy_domestictrade.get(j);
                                DynamicObject entry_newrow1 = new DynamicObject(dt1);
                                entry_newrow1.set("bfgy_textfield1",dynamicObject.get("bfgy_innername"));
                                entry_newrow1.set("bfgy_decimalfield",dynamicObject.get("bfgy_innertaxrate"));
                                entry_newrow1.set("bfgy_amountfield6",dynamicObject.get("bfgy_innertax"));
                                list.add(entry_newrow1);
                                amounttotal = amounttotal.add(dynamicObject.getBigDecimal("bfgy_innertax"));
                            }
                        }
                        dataEntity.set("bfgy_amountfield8",amounttotal);
                        bfgy_entryentity.addAll(list);
                    }
                }
            } else if ("1".equalsIgnoreCase(bfgy_notreturn)) {
                Object bfgy_outrecnum = dataEntity.getString("bfgy_outrecnum");//出口发票单据编号
                QFilter[] btqfilters = new QFilter[]{new QFilter("bfgy_invoiceno", QCP.equals, bfgy_outrecnum)};
                String selectorProperties = "bfgy_return.bfgy_returnname,bfgy_return.bfgy_taxrate,bfgy_return.bfgy_returntax";
                DynamicObject[] btdynamicObjects = BusinessDataServiceHelper.load("bfgy_wb_exportinvoice", selectorProperties, btqfilters);
                if (btdynamicObjects != null && btdynamicObjects.length>0) {
                    DynamicObjectCollection bfgy_return = btdynamicObjects[0].getDynamicObjectCollection("bfgy_return");
                    if (bfgy_return != null && bfgy_return.size()>0) {
                        EntryProp prop1 = (EntryProp) EntityMetadataCache.getDataEntityType("ar_revcfmbill").findProperty("bfgy_entryentity");
                        DynamicObjectType dt1 = prop1.getDynamicCollectionItemPropertyType();
                        List list = new ArrayList();
                        BigDecimal amounttotal = new BigDecimal(0);
                        for (int j= 0; j < bfgy_return.size(); j++) {
                            DynamicObject dynamicObject = bfgy_return.get(j);
                            DynamicObject entry_newrow1 = new DynamicObject(dt1);
                            entry_newrow1.set("bfgy_textfield1",dynamicObject.get("bfgy_returnname"));
                            entry_newrow1.set("bfgy_decimalfield",dynamicObject.get("bfgy_taxrate"));
                            entry_newrow1.set("bfgy_amountfield6",dynamicObject.get("bfgy_returntax"));
                            list.add(entry_newrow1);
                            amounttotal = amounttotal.add(dynamicObject.getBigDecimal("bfgy_returntax"));
                        }
                        dataEntity.set("bfgy_amountfield8",amounttotal);
                        bfgy_entryentity.addAll(list);
                    }
                }
            }
            DynamicObject srcCurrency = (DynamicObject)dataEntity.get("currency");
            DynamicObject destCurrency = (DynamicObject)dataEntity.get("basecurrency");
            DynamicObject exratetable = (DynamicObject)dataEntity.get("exratetable");
            Date exrateDate = (Date)dataEntity.get("exratedate");
            if (destCurrency != null && srcCurrency != null && exratetable != null) {
                long srcCurrencyId = srcCurrency.getLong("id");
                long destCurrencyId = destCurrency.getLong("id");
                long exratetableId = exratetable.getLong("id");
                BigDecimal exchangeRate = BaseDataHelper.getExchangeRate(exratetableId, srcCurrencyId, destCurrencyId, exrateDate);
                dataEntity.set("exchangerate", exchangeRate);
            }
//            dataEntity.set("bfgy_amountfield7", null);
//            dataEntity.set("bfgy_amountfield1", null);
            dataEntity.set("bfgy_amountfield2", null);
            dataEntity.set("bfgy_amountfield4", null);
        }
    }
}
