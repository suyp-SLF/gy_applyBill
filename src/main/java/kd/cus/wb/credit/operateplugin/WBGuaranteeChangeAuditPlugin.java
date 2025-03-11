package kd.cus.wb.credit.operateplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

/**
 * 保函变更申请【审核】，修改保函登记状态为变更
 */
public class WBGuaranteeChangeAuditPlugin extends AbstractOperationServicePlugIn {

    @Override
    public void onPreparePropertys(PreparePropertysEventArgs e) {
        e.getFieldKeys().add("guaranteenumber");
    }

    @Override
    public void beginOperationTransaction(BeginOperationTransactionArgs e) {
        DynamicObject[] dataEntities = e.getDataEntities();
        for (DynamicObject dynamicObject : dataEntities) {
            QFilter numberFilter = new QFilter("bhnumber", QCP.equals, dynamicObject.get("guaranteenumber"));
            QFilter isnewFilter = new QFilter("bfgy_isnew", QCP.equals, true);
            DynamicObject[] dynamicObjects = BusinessDataServiceHelper.load("bfgy_bhdjd_wb", "id, bhstatus", numberFilter.and(isnewFilter).toArray());
            if (dynamicObjects != null && dynamicObjects.length > 0) {
                DynamicObject bhdyn = dynamicObjects[0];
                bhdyn.set("bhstatus", "CHANGE");
                SaveServiceHelper.save(new DynamicObject[]{bhdyn});
            }
        }
    }
}
