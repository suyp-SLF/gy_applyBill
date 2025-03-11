package kd.cus.wb.credit.formplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.EventObject;
import java.util.Map;

/**
 * 意见征询插件
 *
 * @author SHX
 * @date 2021-03-07.
 */
public class WBCreditConsultPlugin extends AbstractBillPlugIn {
    @Override
    public void afterCreateNewData(EventObject e) {
        Map<String, Object> map = this.getView().getFormShowParameter().getCustomParams();
        // 查询登记单信息
        if (map.get("xyzdjId") != null) {
            this.getModel().setValue("bfgy_xyz_number", map.get("bfgy_xyz_number"));
            this.getModel().setValue("bfgy_xyz_currency", map.get("bfgy_xyz_currency"));
            this.getModel().setValue("bfgy_xyz_amount", map.get("bfgy_xyz_amount"));
            this.getModel().setValue("bfgy_balance", map.get("bfgy_balance"));
            DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingleFromCache(map.get("xyzdjId"), "bfgy_credit_register_wb");
            DynamicObjectCollection dynamicObjects = dynamicObject.getDynamicObjectCollection("bfgy_entryentity");
            for (DynamicObject dyn : dynamicObjects) {
                if (dyn.get("bfgy_project_no") ==null  || "".equals(dyn.get("bfgy_project_no"))) {
                    this.getView().setVisible(true, "bfgy_promanager");
                } else{
                    this.getView().setVisible(false, "bfgy_promanager");
                }
            }
        }
    }
}