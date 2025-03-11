package kd.cus.wb.report;

import java.util.EventObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.record.meta.FieldTypeInfo;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.ShowType;
import kd.bos.form.events.HyperLinkClickEvent;
import kd.bos.form.events.HyperLinkClickListener;
import kd.bos.list.LinkQueryPkId;
import kd.bos.list.LinkQueryPkIdCollection;
import kd.bos.list.ListShowParameter;
import kd.bos.report.ReportList;
import kd.bos.report.events.TreeReportListEvent;
import kd.bos.report.plugin.AbstractReportFormPlugin;

/**
 * 项目合同资产及应收债权确认一览表
 */
public class AssetsViewReportFormPluginNEW extends AbstractReportFormPlugin implements HyperLinkClickListener {
	
//    @Override
//    public void setTreeReportList(TreeReportListEvent event) {
//        super.setTreeReportList(event);
//        event.setTreeReportList(true);
//    }
    
    @Override
    public void registerListener(EventObject e) {
    	 super.registerListener(e);
	        ReportList list = (ReportList) this.getControl("reportlistap");
	        list.addHyperClickListener(this);
    }
    
    
    @Override
    public void hyperLinkClick(HyperLinkClickEvent hyperLinkClickEvent) {
    	DynamicObject this_dy = hyperLinkClickEvent.getRowData();
    	String fieldtype = hyperLinkClickEvent.getFieldName();
    	if("col_1_08".equalsIgnoreCase(fieldtype)) {
    		String numner = this_dy.getString("col_1_08_D");
    		String[] ids = StringUtils.split(",");
    		
    		if (ids.length > 0) {
                ListShowParameter parameter = new ListShowParameter();
                parameter.setFormId("bos_list");
                parameter.setBillFormId("ar_revcfmbill");
                parameter.setCaption("相应的" + "收入确认" + "列表");
                parameter.getOpenStyle().setTargetKey("tabap");
                parameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
                LinkQueryPkIdCollection col = parameter.getLinkQueryPkIdCollection();
                for(String id : ids) {
                    col.add(new LinkQueryPkId(id));
                }
                this.getView().showForm(parameter);
            } else {
                this.getView().showMessage("未找到对应的单据！");
            }
    		System.out.println(1);
    	}else if("col_1_10".equalsIgnoreCase(fieldtype)) {
    		String numner = this_dy.getString("col_1_10_D");
    		String[] ids = StringUtils.split(",");
    		if (ids.length > 0) {
                ListShowParameter parameter = new ListShowParameter();
                parameter.setFormId("bos_list");
                parameter.setBillFormId("ar_finarbill");
                parameter.setCaption("相应的" + "应收确认" + "列表");
                parameter.getOpenStyle().setTargetKey("tabap");
                parameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
                LinkQueryPkIdCollection col = parameter.getLinkQueryPkIdCollection();
                for(String id : ids) {
                    col.add(new LinkQueryPkId(id));
                }
                this.getView().showForm(parameter);
            } else {
                this.getView().showMessage("未找到对应的单据！");
            }
    		System.out.println(1);
    	}
    }
}
