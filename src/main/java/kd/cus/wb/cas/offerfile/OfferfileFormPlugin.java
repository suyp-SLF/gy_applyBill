package kd.cus.wb.cas.offerfile;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.IPageCache;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.math.BigDecimal;
import java.util.EventObject;

/**
 * 报盘文件|表单插件
 * @author suyp
 */
public class OfferfileFormPlugin extends AbstractFormPlugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        if( null != this.getView().getParentView().getPageCache().get("billtype")){
            String billtype = this.getView().getParentView().getPageCache().get("billtype");
            DynamicObject havetype = this.getModel().getDataEntity().getDynamicObject("bfgy_billtype");
            if (null != billtype && null == havetype) {
                QFilter[] qFilters = {new QFilter("number", QCP.equals, billtype)};
                DynamicObject dy = BusinessDataServiceHelper.loadSingleFromCache("bos_billtype", qFilters);
                this.getModel().setValue("bfgy_billtype", dy);
            }

            //计算申请付款金额
            DynamicObjectCollection entry_cols = this.getModel().getDataEntity().getDynamicObjectCollection("bfgy_bfgy_agencyentry");
            DynamicObjectCollection entry_cols_dfd = this.getModel().getDataEntity().getDynamicObjectCollection("bfgy_agencyentry");
            DynamicObjectCollection entry_cols_dhd = this.getModel().getDataEntity().getDynamicObjectCollection("bfgy_dhdentry");
            BigDecimal cnysumamount = new BigDecimal("0");
            BigDecimal eursumamount = new BigDecimal("0");
            BigDecimal usdsumamount = new BigDecimal("0");
            for (DynamicObject entry_col : entry_cols) {
                if (null != entry_col.getDynamicObject("bfgy_currencyfield")) {
                    if ("CNY".equalsIgnoreCase(entry_col.getDynamicObject("bfgy_currencyfield").getString("number"))) {
                        cnysumamount = cnysumamount.add(entry_col.getBigDecimal("bfgy_bfgy_applyamount"));
                    } else if ("EUR".equalsIgnoreCase(entry_col.getDynamicObject("bfgy_currencyfield").getString("number"))) {
                        eursumamount = eursumamount.add(entry_col.getBigDecimal("bfgy_bfgy_applyamount"));
                    }else if("USD".equalsIgnoreCase(entry_col.getDynamicObject("bfgy_currencyfield").getString("number"))){
                        usdsumamount = usdsumamount.add(entry_col.getBigDecimal("bfgy_bfgy_applyamount"));
                    }
                }
            }
            this.getModel().setValue("bfgy_cnyamount",cnysumamount);
            this.getModel().setValue("bfgy_euramount",eursumamount);
            this.getModel().setValue("bfgy_usdamount",usdsumamount);
            update();
        }
    }

    public void propertyChanged(PropertyChangedArgs e) {
        IDataEntityProperty property = e.getProperty();
        ChangeData[] change = e.getChangeSet();
        update();
    }

    @Override
    public void registerListener(EventObject e) {
        // TODO Auto-generated method stub
        super.registerListener(e);
        Toolbar repairDataBtnBar = this.getControl("tbmain");
        repairDataBtnBar.addItemClickListener(this);
        Toolbar barDataBtnBar = this.getControl("bfgy_advcontoolbarap2");
        barDataBtnBar.addItemClickListener(this);
        Toolbar barDataBtnBar1 = this.getControl("bfgy_advcontoolbarap1");
        barDataBtnBar1.addItemClickListener(this);
        Toolbar barDataBtnBar2 = this.getControl("bfgy_advcontoolbarap");
        barDataBtnBar2.addItemClickListener(this);
    }

    @Override
    public void itemClick(ItemClickEvent evt) {
        super.itemClick(evt);
        if("bfgy_dhddel".equalsIgnoreCase(evt.getItemKey()) || "bfgy_bfgy_deleterow".equalsIgnoreCase(evt.getItemKey())){
            update();
        }
    }

    @Override
    public void beforeItemClick(BeforeItemClickEvent evt) {
        if ("bfgy_dhdselect".equalsIgnoreCase(evt.getItemKey()) || "bfgy_bfgy_selectrow".equalsIgnoreCase(evt.getItemKey()) || "bfgy_selectrow".equalsIgnoreCase(evt.getItemKey())){
            if (null == this.getModel().getValue("bfgy_basedatafield")){
                evt.setCancel(true);
                this.getView().showTipNotification("请填写银行账户再选单！");
            }
        }
    }

    private void update(){
        DynamicObjectCollection entry_cols = this.getModel().getEntryEntity("bfgy_bfgy_agencyentry");
        DynamicObjectCollection entry_cols_dfd = this.getModel().getEntryEntity("bfgy_agencyentry");
        DynamicObjectCollection entry_cols_dhd = this.getModel().getEntryEntity("bfgy_dhdentry");



        //entry_cols
        BigDecimal allsumamount = BigDecimal.ZERO;
        for (DynamicObject entry_col : entry_cols) {
            if (null != entry_col.getDynamicObject("bfgy_currencyfield")) {
                allsumamount = allsumamount.add(entry_col.getBigDecimal("bfgy_bfgy_applyamount"));
            }
        }
        for (DynamicObject entry_col : entry_cols_dfd) {
            if (null != entry_col.getBigDecimal("bfgy_agencymoney")) {
                allsumamount = allsumamount.add(entry_col.getBigDecimal("bfgy_agencymoney"));
            }
        }
        for (DynamicObject entry_col : entry_cols_dhd) {
            if (null != entry_col.getBigDecimal("bfgy_dhdamount")) {
                allsumamount = allsumamount.add(entry_col.getBigDecimal("bfgy_dhdamount"));
            }
        }
        this.getModel().setValue("bfgy_agencyamount",allsumamount);
    }
}
