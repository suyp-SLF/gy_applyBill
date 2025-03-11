package kd.cus.wb.report;

import kd.bos.report.events.TreeReportListEvent;
import kd.bos.report.plugin.AbstractReportFormPlugin;

/**
 * 项目合同资产及应收债权确认一览表
 */
public class AssetsViewReportFormPlugin extends AbstractReportFormPlugin {
    @Override
    public void setTreeReportList(TreeReportListEvent event) {
        super.setTreeReportList(event);
        event.setTreeReportList(true);
    }
}
