package kd.cus.wb.cas.offerfile;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 *报盘文件| 导出日期存在的不允许反审核
 */
public class OfferfileOppoPlugin extends AbstractOperationServicePlugIn {
    @Override
    public void beforeExecuteOperationTransaction(BeforeOperationArgs e) {
            if ("unaudit".equalsIgnoreCase(e.getOperationKey())) {
                List<ExtendedDataEntity> selects = e.getSelectedRows();
                String msg = "存在已导出单据:";
                Boolean iserr = false;
                for (ExtendedDataEntity select : selects) {
                    String dtName = this.billEntityType.getName();
                    String billno = select.getBillNo();
                    QFilter[] qFilters = {new QFilter("billno", QCP.equals, billno)};
                    DynamicObject dy = BusinessDataServiceHelper.loadSingle(dtName, "billno,bfgy_datefield,billstatus", qFilters);
                    if (null != dy && null != dy.getDate("bfgy_datefield")) {
                        e.setCancel(true);
                        msg += dy.getString("billno") + ",";
                        iserr = true;
                    }
                }
                if (iserr) {
                    e.setCancelMessage(msg + "不允许反审核！");
                }
            } else if ("save".equalsIgnoreCase(e.getOperationKey()) || "submit".equalsIgnoreCase(e.getOperationKey())) {
                DynamicObject dy = e.getDataEntities()[0];
                DynamicObjectCollection dy_zl_cols = dy.getDynamicObjectCollection("bfgy_bfgy_agencyentry");
                for (DynamicObject dy_zl_col : dy_zl_cols) {
                    String zl_billno = dy_zl_col.getString("bfgy_bfgy_zldno");//
                    DynamicObject account = dy.getDynamicObject("bfgy_basedatafield");
                    if (StringUtils.isNotBlank(zl_billno)) {
                        QFilter[] qFilters = {new QFilter("billno", QCP.equals, zl_billno)};
                        DynamicObject this_dy = BusinessDataServiceHelper.loadSingleFromCache("ap_payapply", qFilters);
                        this_dy.set("bfgy_basedatafield1",account);//
                        this_dy.set("bfgy_exportstate_wb", "A");
                        SaveServiceHelper.save(new DynamicObject[]{this_dy});
                    }
                }//

                DynamicObjectCollection dy_df_cols = dy.getDynamicObjectCollection("bfgy_agencyentry");
                for (DynamicObject dy_df_col : dy_df_cols) {
                    String df_billno = dy_df_col.getString("bfgy_agencyno");//
                    if (StringUtils.isNotBlank(df_billno)) {
                        QFilter[] qFilters = {new QFilter("billno", QCP.equals, df_billno)};
                        DynamicObject this_dy = BusinessDataServiceHelper.loadSingleFromCache("cas_agentpaybill", qFilters);
                        this_dy.set("bfgy_exportstatus", true);
                        this_dy.set("bfgy_offerfile_number", dy.getString("billno"));

                        SaveServiceHelper.save(new DynamicObject[]{this_dy});
                    }
                }//cas_agentpaybill
            } else if ("delete".equalsIgnoreCase(e.getOperationKey())){
                DynamicObject[] dys = e.getDataEntities();
                for (DynamicObject dy_item : dys) {
                    String dtName = this.billEntityType.getName();
                    String billno = dy_item.getString("billno");
                    QFilter[] qFilters = {new QFilter("billno", QCP.equals, billno)};
                    DynamicObject dy = BusinessDataServiceHelper.loadSingleFromCache(dtName, "billno,bfgy_datefield,billstatus,bfgy_bfgy_agencyentry.bfgy_bfgy_zldno,bfgy_agencyentry.bfgy_agencyno", qFilters);
                    DynamicObjectCollection dy_zl_cols = dy.getDynamicObjectCollection("bfgy_bfgy_agencyentry");
                    for (DynamicObject dy_zl_col : dy_zl_cols) {
                        String zl_billno = dy_zl_col.getString("bfgy_bfgy_zldno");//
                        if (StringUtils.isNotBlank(zl_billno)) {
                            QFilter[] zl_qFilters = {new QFilter("billno", QCP.equals, zl_billno)};
                            DynamicObject this_dy = BusinessDataServiceHelper.loadSingleFromCache("ap_payapply", zl_qFilters);
                            this_dy.set("bfgy_exportstate_wb", "B");
                            SaveServiceHelper.save(new DynamicObject[]{this_dy});
                        }
                    }

                    DynamicObjectCollection dy_df_cols = dy.getDynamicObjectCollection("bfgy_agencyentry");
                    for (DynamicObject dy_df_col : dy_df_cols) {
                        String df_billno = dy_df_col.getString("bfgy_agencyno");//
                        if (StringUtils.isNotBlank(df_billno)) {
                            QFilter[] df_qFilters = {new QFilter("billno", QCP.equals, df_billno)};
                            DynamicObject this_dy = BusinessDataServiceHelper.loadSingleFromCache("cas_agentpaybill", df_qFilters);
                            this_dy.set("bfgy_exportstatus", false);
                            this_dy.set("bfgy_offerfile_number", "");
                            SaveServiceHelper.save(new DynamicObject[]{this_dy});
                        }
                    }
                }
            }
    }
}
