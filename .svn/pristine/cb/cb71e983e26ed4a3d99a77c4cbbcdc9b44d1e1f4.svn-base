package kd.cus.wb.report;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.ShowType;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.list.LinkQueryPkId;
import kd.bos.list.LinkQueryPkIdCollection;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.report.ReportList;
import kd.bos.report.plugin.AbstractReportFormPlugin;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * 	其他应付账款账龄分析表
 */

public class ApFinapbillOtherReportFormPlugin extends AbstractReportFormPlugin implements HyperLinkClickListener {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        ReportList list = (ReportList)this.getControl("reportlistap");
        list.addHyperClickListener(this);
    }

    @Override
    public void hyperLinkClick(HyperLinkClickEvent hyperLinkClickEvent) {
        List<Object> ids = new ArrayList<>();
        DynamicObject this_dy = hyperLinkClickEvent.getRowData();
        String key = this_dy.getString("key");

        if ("col_1_07".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())){
            ids = ApFinapbillOtherListDataPlugin.getColIds(key,0);
        }else if ("col_1_08".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())){
            ids = ApFinapbillOtherListDataPlugin.getColIds(key,1);
        }else if ("col_1_09".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())){
            ids = ApFinapbillOtherListDataPlugin.getColIds(key,2);
        }else if ("col_1_10".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())){
            ids = ApFinapbillOtherListDataPlugin.getColIds(key,3);
        }else if ("col_1_11".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())){
            ids = ApFinapbillOtherListDataPlugin.getColIds(key,4);
        }else if ("col_1_12".equalsIgnoreCase(hyperLinkClickEvent.getFieldName())){
            ids = ApFinapbillOtherListDataPlugin.getColIds(key,5);
        }

        if (ids.size() > 0) {
            ListShowParameter parameter = new ListShowParameter();
            parameter.setFormId("bos_list");
            parameter.setBillFormId("ap_finapbill");
            parameter.setCaption("相应的" +"应付单列表");
            parameter.getOpenStyle().setTargetKey("tabap");
            parameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
//            ListFilterParameter filters = parameter.getListFilterParameter();
//            filters.setFilter(new QFilter("id", QCP.in,ids));
            LinkQueryPkIdCollection col = parameter.getLinkQueryPkIdCollection();
            ids.forEach(m->{
                col.add(new LinkQueryPkId(m));
            });
            this.getView().showForm(parameter);
        }else {
            this.getView().showMessage("未找到对应的单据！");
        }
    }
}
