package kd.cus.wb.workflow;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.WorkflowPlugin;

import java.util.Arrays;
import java.util.List;

public class ConsultWorkflowPlugin extends WorkflowPlugin {

    private static final String PRO_BILL_LOGO = "bfgy_proj_wb_pmb";
    private static final String BH_BILL_LOGO = "bfgy_bhdjd_wb";

    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String key = execution.getBusinessKey();//单据id
        String logo = execution.getEntityNumber();
        DynamicObject this_dy = BusinessDataServiceHelper.loadSingle(key, logo);
        if(null != this_dy){
            String billno = this_dy.getString("bfgy_bh_number");
            QFilter[] qFilters = {new QFilter("bhnumber", QCP.equals,billno)};
            DynamicObject bh_dy = BusinessDataServiceHelper.loadSingle(BH_BILL_LOGO, "bfgy_project_no", qFilters);
            if(null != bh_dy) {
                String pro_no = bh_dy.getString("bfgy_project_no");
                if (pro_no != null) {
                    QFilter[] pro_qFilters = {new QFilter("bfgy_projno", QCP.equals,pro_no)};
                    DynamicObject pro_dy = BusinessDataServiceHelper.loadSingle(PRO_BILL_LOGO, "bfgy_project_no,bfgy_projmanager", pro_qFilters);
                    if (null != pro_dy) {
                        DynamicObject money_person = pro_dy.getDynamicObject("bfgy_projmanager");
                        if (null != money_person) {
                            return Arrays.asList((Long) money_person.getPkValue());
                        }
                    }
                }
            }
        }
        return null;

    }
}
