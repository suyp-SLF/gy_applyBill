package kd.cus.wb.credit.operateplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class CheckInfoOp extends AbstractOperationServicePlugIn {

    @Override
    public void onAddValidators(AddValidatorsEventArgs e) {
        e.addValidator(new Validator());
    }

    @Override
    public void onPreparePropertys(PreparePropertysEventArgs e) {
        List<String> list = e.getFieldKeys();
        list.add("kjfs");
        list.add("ddbrankname");

        list.add("bhlxcombo");
        list.add("contractamount");
        list.add("contractcurrency");
        list.add("marking");
    }
}

class Validator extends AbstractValidator {
    @Override
    public void validate() {
        ExtendedDataEntity value = this.dataEntities[0];
        DynamicObject dynamicObject = value.getDataEntity();

        if (dynamicObject.get("kjfs") != null) {
            DynamicObject bhlxcombo = (DynamicObject) dynamicObject.get("kjfs");
            String number = (String) bhlxcombo.get("number");
            if (StringUtils.isNotEmpty(number)) {
                boolean b = ("002".equals(number) || "003".equals(number) || "004".equals(number) || "005".equals(number))
                        && StringUtils.isEmpty(dynamicObject.getString("ddbrankname"));
                if (b) {
                    this.addErrorMessage(value, "当地银行名称为空");
                }
            }
        }

        if (dynamicObject.get("bhlxcombo") != null) {
            DynamicObject bhlxcombo = (DynamicObject) dynamicObject.get("bhlxcombo");
            String number = (String) bhlxcombo.get("number");
            if (StringUtils.isNotEmpty(number) && "001".equals(number)) {
                BigDecimal amount = dynamicObject.getBigDecimal("contractamount");
                if (amount.compareTo(BigDecimal.ZERO) < 1) {
                    this.addErrorMessage(value, "投保金额不能为空");
                }
                if (StringUtils.isEmpty(dynamicObject.getString("contractcurrency"))) {
                    this.addErrorMessage(value, "投保币别不能为空");
                }
                if (StringUtils.isEmpty(dynamicObject.getString("marking"))) {
                    this.addErrorMessage(value, "投标号不能为空");
                }
            }
        }

    }

}
