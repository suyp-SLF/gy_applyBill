package kd.cus.wb.credit.billplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.cus.common.exchangerate.JMBaseDataHelper;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 信用证登记单同过组织，币别，汇率日期取汇率
 *
 * @Author chenkang
 * @DATE 2020/9/29 18:42
 * @Version 1.0
 */
public class WBCreditRegitRateFormPlugin extends AbstractFormPlugin {

    @Override
    public  void   propertyChanged(PropertyChangedArgs e) {
        super.propertyChanged(e);
        // 点击汇率表或者汇率日期时，触发汇率的自动计算
        String propertyName = e.getProperty().getName();
        if ("bfgy_ratedate".equals(propertyName)||"bfgy_ratetable".equals(propertyName)){
            //汇率日期
            Date rateDate = (Date) this.getModel().getValue("bfgy_ratedate");
            //组织
            DynamicObject org = (DynamicObject) this.getModel().getValue("org");
            //合同币别
            DynamicObject currecy = (DynamicObject) this.getModel().getValue("contractcurrency");
            if (org != null && currecy != null) {
                long orgValue = (long) org.getPkValue();
                long currencyValue = (long) currecy.getPkValue();
                //对汇率赋值
                BigDecimal exchangeRate = JMBaseDataHelper.getExcRate(orgValue, currencyValue, rateDate);
                this.getModel().setValue("bfgy_rate", exchangeRate);
            }
        }
        if ("bfgy_companyrate".equals(propertyName)){
            boolean companyrate = (boolean) this.getModel().getValue("bfgy_companyrate");
            if (!companyrate){
                this.getModel().setValue("bfgy_rate","");
                this.getModel().setValue("bfgy_ratedate","");
                this.getModel().setValue("bfgy_ratetable","");
            }
        }
    }
}
