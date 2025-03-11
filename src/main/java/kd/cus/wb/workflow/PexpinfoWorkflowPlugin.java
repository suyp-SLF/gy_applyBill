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
 * 项目专家信息库|根据项目号找项目经理
 */

public class PexpinfoWorkflowPlugin extends WorkflowPlugin {

    private static final String PRO_BILL_LOGO = "bfgy_proj_wb_pmb";

    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String key = execution.getBusinessKey();//单据id
        String logo = execution.getEntityNumber();
        DynamicObject this_dy = BusinessDataServiceHelper.loadSingle(key, logo);
        if(null != this_dy){
            String billno = this_dy.getString("bfgy_sleprojno");
            QFilter[] qFilters = {new QFilter("bfgy_projno", QCP.equals,billno)};
            DynamicObject pro_dy = BusinessDataServiceHelper.loadSingle(PRO_BILL_LOGO, "bfgy_projno,bfgy_projmanager", qFilters);
            if (null != pro_dy){
                DynamicObject money_person = pro_dy.getDynamicObject("bfgy_projmanager");
                if (null != money_person){
                    return Arrays.asList((Long)money_person.getPkValue());
                }
            }
        }
        return null;

    }
}
