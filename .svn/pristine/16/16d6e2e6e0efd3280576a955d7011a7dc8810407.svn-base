package kd.cus.wb.sys.meetingsummary;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.scmc.im.business.util.DymServiceUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MeetingsummaryOppoPlugin extends AbstractOperationServicePlugIn {

    @Override
    public void beforeExecuteOperationTransaction(BeforeOperationArgs e) {
        if (e.getDataEntities().length == 1){
            SaveServiceHelper.save(e.getDataEntities());
        }
        List<ExtendedDataEntity> select = e.getSelectedRows();
        List<Object> ids = select.stream().map(m -> m.getBillPkId()).collect(Collectors.toList());
        QFilter[] qFilters = {new QFilter("id", QCP.in,ids),new QFilter("billstatus",QCP.equals,"A")};
        DynamicObject[] dys = BusinessDataServiceHelper.load("bfgy_wb_meetingsummary","status,attachmententity.attachment", qFilters);

        String error = "";
        for (DynamicObject item:dys){
            DynamicObjectCollection cols = item.getDynamicObjectCollection("attachmententity");
            if(cols.size() <=0 || null == cols.get(0) || null == cols.get(0).get("attachment")){

                error += "存在文件未上传必传附件！";
                break;
            }
        }
        if (StringUtils.isNotBlank(error)) {
            e.setCancel(true);
            e.setCancelMessage(error);
        }
    }
}
