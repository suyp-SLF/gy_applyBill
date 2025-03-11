package kd.cus.wb.workflow;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.WorkflowPlugin;

import java.util.Arrays;
import java.util.List;

public class SupinvoiceYFWorkflowPlugin extends WorkflowPlugin {

    private static final String PRO_BILL_LOGO = "bfgy_wb_rbprojrep";

    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String key = execution.getBusinessKey();//单据id
        String logo = execution.getEntityNumber();
        DynamicObject this_dy = BusinessDataServiceHelper.loadSingle(key, logo);
        if(null != this_dy){
            String billno = this_dy.getString("bfgy_slprojno");
            QFilter[] qFilters = {new QFilter("billno", QCP.equals,billno)};
            DynamicObject pro_dy = BusinessDataServiceHelper.loadSingle(PRO_BILL_LOGO, "billno,bfgy_financeman", qFilters);
            if (null != pro_dy){
                DynamicObject money_person = pro_dy.getDynamicObject("bfgy_financeman");
                if (null != money_person){
                    return Arrays.asList((Long)money_person.getPkValue());
                }
            }
        }
        return null;

    }
}
