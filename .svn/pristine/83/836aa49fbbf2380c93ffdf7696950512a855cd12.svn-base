package kd.cus.wb.cashier.exsettlement;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *  结算申请操作插件
 * @author suyp
 *
 */
public class ExsettlementOppoPlugin extends AbstractOperationServicePlugIn {

    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {
        List<ExtendedDataEntity> selects = e.getSelectedRows();

        List<String> billnos = new ArrayList<>();

        for (ExtendedDataEntity select : selects){
            billnos.add(select.getBillNo());
        }

        QFilter[] qFilters = {new QFilter("billno", QCP.in,billnos)};

        DynamicObject[] dys = BusinessDataServiceHelper.load("bfgy_wb_exsettlement", "id,billno,bfgy_exstatus", qFilters);

        for (DynamicObject item : dys){
            item.set("bfgy_exstatus","1");
        }


        SaveServiceHelper.save(dys);
    }
}
