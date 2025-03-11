package kd.cus.wb.sys.meetingsummary;

import kd.bos.data.ParameterHelper;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.metadata.clr.DataEntityPropertyCollection;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.entity.operate.Submit;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.param.ParameterReader;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.parameter.SystemParamServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;

import java.util.Date;
import java.util.EventObject;


/**
 * 会议纪要
 * 自动添加必填附件，并校验提交时有没有附件
 */
public class MeetingsummaryFormPlugin extends AbstractFormPlugin {


    /**
     * 附件添加
     * @param e
     */
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterBindData(e);
        String field = (String) SystemParamServiceHelper.loadBillParameterObjectFromCache(this.getModel().getDataEntityType().getName()).get("bfgy_textfield");
//        this.getModel().setItemValueByNumber("attachmenttype",field);
        DynamicObject item_col = new DynamicObject(this.getModel().getDataEntity().getDynamicObjectCollection("attachmententity").getDynamicObjectType());
        QFilter[] qFilters = {new QFilter("number", QCP.equals,field)};
        DynamicObject dy_col_def = BusinessDataServiceHelper.loadSingleFromCache("bfgy_attachmenttype", qFilters);

        DynamicObject user = BusinessDataServiceHelper.loadSingleFromCache(UserServiceHelper.getCurrentUserId(), "bos_user");

        item_col.set("attachmenttype",dy_col_def);
        item_col.set("attachmentuser",user);
        item_col.set("attachmentdate",new Date());

        this.getModel().getDataEntity().getDynamicObjectCollection("attachmententity").clear();
        this.getModel().getDataEntity().getDynamicObjectCollection("attachmententity").add(item_col);
    }

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        if (args.getSource() instanceof Submit && "sunmit".equalsIgnoreCase(((Submit)args.getSource()).getOperateKey())){
            ListSelectedRowCollection select = args.getListSelectedData();
        }
    }
}
