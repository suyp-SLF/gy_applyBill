package kd.cus.wb.ar.finarbill;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;

public class FinarbillFormPlugin extends AbstractFormPlugin {

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Toolbar repairDataBtnBar = this.getControl("tbmain");
        repairDataBtnBar.addItemClickListener(this);
    }

    @Override
    public void beforeItemClick(BeforeItemClickEvent evt) {

        DynamicObject this_dy = this.getModel().getDataEntity();
        if(null != this_dy.getString("billstatus") && "C".equalsIgnoreCase(this_dy.getString("billstatus")) && "bfgy_unauditanddelete".equalsIgnoreCase(evt.getItemKey())){
//            this.getView().close();
            this.getView().invokeOperation("unaudit");
        }
    }

    @Override
    public void itemClick(ItemClickEvent evt) {
        super.itemClick(evt);
        Date auditdate = (Date) this.getModel().getValue("auditdate");
        if (null != auditdate) {
            Calendar calyear = Calendar.getInstance();
            calyear.setTime(auditdate);//设置起时间
            calyear.add(Calendar.MONTH, 6);
            Date duedate = calyear.getTime();
            this.getModel().setValue("duedate", duedate);
            calyear.add(Calendar.DATE,15);
            Date duedate1 = calyear.getTime();
            this.getModel().setValue("bfgy_datefield", duedate1);
            DynamicObject dy = this.getModel().getDataEntity(true);
            dy.set("duedate",duedate);
            dy.set("bfgy_datefield",duedate1);
            SaveServiceHelper.save(new DynamicObject[]{dy});
        }
    }
}
