package kd.cus.wb.workflow;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.WorkflowPlugin;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CreditconsultworkflowPlugin extends WorkflowPlugin {
    @Override
    public List<Long> calcUserIds(AgentExecution execution) {
        String key = execution.getBusinessKey();//单据id
        String logo = execution.getEntityNumber();
        DynamicObject this_dy = BusinessDataServiceHelper.loadSingle(key, logo);
        List<String> pjids = new ArrayList<>();
        Set<Long> perids = new HashSet<>();
        if(null != this_dy){
            String creditnumber = this_dy.getString("bfgy_xyz_number");
            QFilter[] qFilterscr = {new QFilter("creditnumber",QCP.equals,creditnumber)};
            DynamicObject credit_register_dy = BusinessDataServiceHelper.loadSingle("bfgy_credit_register_wb","creditnumber,bfgy_project_no", qFilterscr);
            DynamicObjectCollection credit_register_dy2 = QueryServiceHelper.query("bfgy_credit_register_wb","creditnumber,bfgy_entryentity.bfgy_project_no",qFilterscr);
            if (credit_register_dy2.size() > 0){
                for(DynamicObject item:credit_register_dy2){
                    if (StringUtils.isNotBlank(item.getString("bfgy_entryentity.bfgy_project_no")))
                        pjids.add(item.getString("bfgy_entryentity.bfgy_project_no"));
                }
            }
            QFilter[] qFilters = {new QFilter("bfgy_projno", QCP.in,pjids)};
            DynamicObject[] pro_dys = BusinessDataServiceHelper.load("bfgy_proj_wb_pmb", "bfgy_projno,bfgy_projmanager", qFilters);
            if (null != pro_dys){
                for(DynamicObject pro_dy:pro_dys) {
                    DynamicObject money_person = pro_dy.getDynamicObject("bfgy_projmanager");
                    if (null != money_person) {
                        perids.add((Long) money_person.getPkValue());
                    }
                }
            }
        }
        return Arrays.asList(perids.toArray(new Long[perids.size()]));

    }
}
