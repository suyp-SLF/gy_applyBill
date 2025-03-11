package kd.cus.wb.newreport;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.report.ReportList;
import kd.bos.report.plugin.AbstractReportFormPlugin;
import kd.cus.wb.report.ApFinapbillOtherListDataPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

/**
 *高质量发展表
 */

public class HighawardReportFormPlugin extends AbstractReportFormPlugin implements HyperLinkClickListener {
    public static Date startdate = null;
    public static Date enddate = null;

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        ReportList list = (ReportList) this.getControl("reportlistap");
        list.addHyperClickListener(this);
    }

    @Override
    public void hyperLinkClick(HyperLinkClickEvent hyperLinkClickEvent) {
        List<Object> ids = new ArrayList<>();
        DynamicObject this_dy = hyperLinkClickEvent.getRowData();

        if ("col_1_04".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())) {
            String pkid = hyperLinkClickEvent.getRowData().getString("col_1_01");
            String caption = hyperLinkClickEvent.getRowData().getString("col_1_04");

            FormShowParameter parameter = new FormShowParameter();
            parameter.setFormId("bfgy_highawardpaper");
            parameter.setCaption(caption);
            parameter.setCustomParam("cus_id_value",pkid);
            parameter.getOpenStyle().setTargetKey("tabap");
            parameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
            this.getView().showForm(parameter);
        }
    }

    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        ChangeData[] change = e.getChangeSet();
        IDataEntityProperty property = e.getProperty();
        if ("startdate".equalsIgnoreCase(property.getName())){
            startdate = (Date)change[0].getNewValue();
        }
        if ("enddate".equalsIgnoreCase(property.getName())){
            enddate = (Date)change[0].getNewValue();
        }
    }
}
