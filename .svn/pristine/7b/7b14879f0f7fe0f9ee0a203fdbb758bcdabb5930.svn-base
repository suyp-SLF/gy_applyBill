package kd.cus.wb.report;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.report.ReportList;
import kd.bos.report.plugin.AbstractReportFormPlugin;

import java.util.EventObject;

public class ReceivablechangeReportFormPlugin extends AbstractReportFormPlugin implements HyperLinkClickListener {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        ReportList list = (ReportList)this.getControl("reportlistap");
        list.addHyperClickListener(this);
    }

    @Override
    public void hyperLinkClick(HyperLinkClickEvent hyperLinkClickEvent) {
       if ("col_1_11".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())){

           DynamicObject this_dy = hyperLinkClickEvent.getRowData();

       }
    }
}
