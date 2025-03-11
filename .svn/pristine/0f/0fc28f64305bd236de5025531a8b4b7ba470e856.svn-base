package kd.cus.wb.workflow;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.WorkflowPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * 对公报销单，对私报销单，差旅报销单。查找财务经理
 */

public class ExpenseWorkflowPlugin extends WorkflowPlugin {
    private static final String PRO_BILL_LOGO = "bfgy_proj_wb_pmb";

    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String key = execution.getBusinessKey();//单据id
        String logo = execution.getEntityNumber();
        DynamicObject this_dy = BusinessDataServiceHelper.loadSingle(key, logo);
        if(null != this_dy){
            String billno = this_dy.getString("bfgy_wb_rbprojrep");
            QFilter[] qFilters = {new QFilter("bfgy_projno", QCP.equals,billno)};
            DynamicObject pro_dy = BusinessDataServiceHelper.loadSingle(PRO_BILL_LOGO, "bfgy_projno,bfgy_projmanager", qFilters);
            if (null != pro_dy){
                DynamicObject money_person = pro_dy.getDynamicObject("bfgy_userfield");
                if (null != money_person){
                    return Arrays.asList((Long)money_person.getPkValue());
                }
            }
        }
        return null;

    }
}
