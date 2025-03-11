package kd.cus.wb.ap.busbill;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.cus.exchangerate.helpword.JMBaseDataHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EventObject;

/**
 * 暂估应付单|表单插件，修改公司汇率
 * @author suyp
 */

public class BusbillFormPlugin extends AbstractFormPlugin {

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Toolbar repairDataBtnBar = this.getControl("tbmain");
        repairDataBtnBar.addItemClickListener(this);
    }

//    @Override
//    public void propertyChanged(PropertyChangedArgs e) {
//        IDataEntityProperty property = e.getProperty();
//        ChangeData[] change = e.getChangeSet();
//
//        if("currency".equalsIgnoreCase(property.getName())){
//            BigDecimal rate = getRate();
////            this.getModel().setValue("exchangerate", rate);
//        }
//    }

//    @Override
//    public void afterCreateNewData(EventObject e) {
//        BigDecimal rate = getRate();
////        this.getModel().setValue("exchangerate", rate);
//
//    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        DynamicObjectCollection entry = this.getModel().getEntryEntity("entry");
        BigDecimal sumamount = BigDecimal.ZERO;
        for(DynamicObject item:entry){
            sumamount = sumamount.add(item.getBigDecimal("e_uninvoicedamt"));
        }
        this.getModel().setValue("uninvoicedamt",sumamount);
        
        
        DynamicObject dataEntity = this.getModel().getDataEntity();
        String contract = dataEntity.getString("bfgy_conno");//合同
        String project = dataEntity.getString("bfgy_pronum");//项目
        String custom = dataEntity.getString("bfgy_custom");
        if (StringUtils.isNotBlank(contract)){
            QFilter[] qFilters_con = {new QFilter("number", QCP.equals,contract)};
            DynamicObject dy_pro = BusinessDataServiceHelper.loadSingleFromCache("bfgy_saleorderbd", qFilters_con);
//            dataEntity.set("bfgy_contract_base",dy_pro);
            this.getModel().setValue("bfgy_contract_base", dy_pro);
        }

        if (StringUtils.isNotBlank(project)){
            QFilter[] qFilters_pro = {new QFilter("number",QCP.equals,project)};
            DynamicObject dy_con = BusinessDataServiceHelper.loadSingleFromCache("bd_project", qFilters_pro);
//            dataEntity.set("bfgy_project_base",dy_con);
            this.getModel().setValue("bfgy_project_base", dy_con);
        }

        if (StringUtils.isNotBlank(custom)){
            QFilter[] qFilters_pro = {new QFilter("name",QCP.equals,custom)};
            DynamicObject dy_con = BusinessDataServiceHelper.loadSingleFromCache("bd_supplier", qFilters_pro);
//            dataEntity.set("asstact",dy_con);
            this.getModel().setValue("asstact", dy_con);
        }

    }

    /**
     * 修改为公司汇率
     *
     * @return
     */

    private BigDecimal getRate() {
        DynamicObject currency = (DynamicObject) this.getModel().getValue("currency");
        if (null != currency) {
            // 组织ID
            DynamicObject org = (DynamicObject) this.getModel().getValue("org");
            Long orgId = org.getLong("id");
            // 币别ID
            Long tarCurrencyId = (Long) currency.get("id");
            // 日期
            Date date = (Date) this.getModel().getValue("exratedate");
            BigDecimal excRate = JMBaseDataHelper.getExcRate(orgId, tarCurrencyId, date);
            return excRate;
        }
        return new BigDecimal(0);
    }



    @Override
    public void beforeItemClick(BeforeItemClickEvent evt) {
        DynamicObject this_dy = this.getModel().getDataEntity();
        if(null != this_dy.getString("billstatus") && "C".equalsIgnoreCase(this_dy.getString("billstatus")) && "bfgy_unauditanddelete".equalsIgnoreCase(evt.getItemKey())){
//            this.getView().close();
            this.getView().invokeOperation("unaudit");
        }
    }
}
