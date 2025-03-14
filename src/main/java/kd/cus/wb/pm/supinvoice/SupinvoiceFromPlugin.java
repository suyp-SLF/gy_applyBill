package kd.cus.wb.pm.supinvoice;

import kd.bos.bill.BillShowParameter;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.AppMetadataCache;
import kd.bos.entity.botp.runtime.BFRow;
import kd.bos.entity.botp.runtime.TableDefine;
import kd.bos.entity.operate.PushAndSave;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.botp.BFTrackerServiceHelper;
import kd.bos.servicehelper.botp.ConvertMetaServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.util.EventObject;
import java.util.List;
import java.util.Map;

/**
 * 供方发票
 */
public class SupinvoiceFromPlugin extends AbstractFormPlugin {

    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);

        if ((args.getSource() instanceof PushAndSave) && "pushandsave".equalsIgnoreCase(((PushAndSave) args.getSource()).getOperateKey())) {
            Long pkid = (Long) this.getModel().getDataEntity().getPkValue();
            String billName = this.getModel().getDataEntity().getDataEntityType().getName();
            String status = this.getModel().getDataEntity().getString("billstatus");
            Map<Long, List<BFRow>> dest = BFTrackerServiceHelper.findDirtTargetBills("bfgy_pm_wb_supinvoice", new Long[]{pkid});
            List<BFRow> destbills = dest.get(pkid);
            if (null != destbills) {
                for (BFRow item : destbills) {
                    if (item.getId() != null && item.getId().getMainTableId() != null) {
                        TableDefine tabledf = ConvertMetaServiceHelper.loadTableDefine(item.getId().getMainTableId());
                        if ("ap_busbill".equalsIgnoreCase(tabledf.getEntityNumber())) {
                            this.getView().showErrorNotification("单据已经下推,无法再次下推!");
                            args.setCancelMessage("单据已经下推,无法再次下推!");
                            args.setCancel(true);
                        }
                    }
                }
            }
            if (!"C".equalsIgnoreCase(status)) {
                this.getView().showErrorNotification("单据未审核!");
                args.setCancelMessage("单据未审核!");
                args.setCancel(true);
            }
        }
    }

    @Override
    public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
        super.afterDoOperation(afterDoOperationEventArgs);
        if ("pushandsave".equalsIgnoreCase(afterDoOperationEventArgs.getOperateKey())) {
            DynamicObject this_dy = this.getModel().getDataEntity(true);
            Long[] id = new Long[]{Long.parseLong(this_dy.getPkValue().toString())};
            Map<Long, List<BFRow>> dest = BFTrackerServiceHelper.findDirtTargetBills("bfgy_pm_wb_supinvoice", id);
            List<BFRow> destbills = dest.get(this_dy.getPkValue());
            if (null != destbills) {
                for (BFRow item : destbills) {
                    if (item.getId() != null && item.getId().getMainTableId() != null) {
                        TableDefine tabledf = ConvertMetaServiceHelper.loadTableDefine(item.getId().getMainTableId());
                        if ("ap_busbill".equalsIgnoreCase(tabledf.getEntityNumber())) {
                            Long billid = item.getId().getBillId();
                            String appId = AppMetadataCache.getAppInfo("bfgy_ecma_ext").getAppId();
                            BillShowParameter parameter = new BillShowParameter();
                            parameter.setFormId("ap_busbill");
                            parameter.setCaption("暂估冲回单");
                            parameter.getOpenStyle().setTargetKey("tabap");
                            parameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
                            parameter.setPkId(billid);
                            this_dy.set("bfgy_checkboxfield", true);
                            SaveServiceHelper.save(new DynamicObject[]{this_dy});
                            this.getView().showForm(parameter);
                            this.getView().invokeOperation("refresh");
                        }
                    }
                }
            }
            System.out.println(dest);
        }
    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        String status = (String)this.getModel().getValue("billstatus");
        Boolean zgyfd = (Boolean)this.getModel().getValue("bfgy_checkboxfield");
        DynamicObject number_dy = (DynamicObject) this.getModel().getValue("bfgy_coststrc");
        if ("C".equalsIgnoreCase(status)){
            this.getView().setEnable(true,"bfgy_baritemap2");
        }else {
            this.getView().setEnable(false,"bfgy_baritemap2");
        }
        if(number_dy != null && ("YF-001".equals(number_dy.getString("number")) || "C03".equals(number_dy.getString("number")))) {
        	this.getView().setEnable(false, "bfgy_baritemap2");
        	this.getView().setEnable(true, "bfgy_pushapfinap");
		} else {
			if (zgyfd) {
				this.getView().setEnable(true, "bfgy_pushapfinap");
			} else {
				this.getView().setEnable(false, "bfgy_pushapfinap");
			}
		}
    }
}
