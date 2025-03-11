package kd.cus.wb.botp;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RepairBotpOppoPlugin extends AbstractOperationServicePlugIn {

    @Override
    public void beforeExecuteOperationTransaction(BeforeOperationArgs e) {
        List<String> ids = Arrays.stream(e.getDataEntities()).map(DynamicObject::getPkValue).map(i -> i.toString()).collect(Collectors.toList());
        QFilter[] qFilters = {new QFilter("id", QCP.in,ids)};
        DynamicObject[] select = BusinessDataServiceHelper.load("bfgy_botprepairdata", "bfgy_type,bfgy_src_logo,bfgy_dst_logo,bfgy_src_id_ux,bfgy_dst_id_ux", qFilters);
        for (int i = 0; i < select.length; i++) {
            
        }
    }
}
