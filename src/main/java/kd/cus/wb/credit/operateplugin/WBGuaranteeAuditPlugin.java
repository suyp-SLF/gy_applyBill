package kd.cus.wb.credit.operateplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.plugin.args.BeginOperationTransactionArgs;

/**
 * 保函登记【审核】时，如果时保函状态是 变更，修改为 开立
 */
public class WBGuaranteeAuditPlugin extends AbstractOperationServicePlugIn {

    @Override
    public void onPreparePropertys(PreparePropertysEventArgs e) {
        e.getFieldKeys().add("bhstatus");
    }

    @Override
    public void beginOperationTransaction(BeginOperationTransactionArgs e) {
        DynamicObject[] dataEntities = e.getDataEntities();
        for (DynamicObject dynamicObject : dataEntities) {
            if ("CHANGE".equals(dynamicObject.get("bhstatus"))) {
                dynamicObject.set("bhstatus", "KL");
            }
        }
    }
}
