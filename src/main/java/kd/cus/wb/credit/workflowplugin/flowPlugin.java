package kd.cus.wb.credit.workflowplugin;

import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.api.constants.WFTaskResultEnum;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;

public class flowPlugin implements IWorkflowPlugin {

    @Override
    public void notify(AgentExecution execution) {
        //单据的BusinessKey(业务ID)
        String BusinessKey = execution.getBusinessKey();
        //单据实体编码
        String entityNumber = execution.getEntityNumber();
        // 获取单据实体
        DynamicObject dyn = BusinessDataServiceHelper.loadSingle(BusinessKey, entityNumber);
        Object opinion = execution.getCurrentTaskResult(WFTaskResultEnum.auditMessage);
        if (opinion != null) {
            // key:zh_TW,zh_CN
            JSONObject jsonObject = JSONObject.parseObject(opinion.toString());
            dyn.set("bfgy_reply_content", jsonObject.get("zh_CN"));
            SaveServiceHelper.save(new DynamicObject[] {dyn});
        }
    }

}
