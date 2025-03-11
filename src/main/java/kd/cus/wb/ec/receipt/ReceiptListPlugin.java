package kd.cus.wb.ec.receipt;

import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.botp.Push;
import kd.bos.form.operate.listop.ExportList;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.servicehelper.botp.BFTrackerServiceHelper;

import java.util.List;
import java.util.stream.Collectors;

public class ReceiptListPlugin extends AbstractListPlugin {
    @Override
    public void beforeDoOperation(BeforeDoOperationEventArgs args) {
        super.beforeDoOperation(args);
        Object source = args.getSource();
        if ((args.getSource() instanceof Push) && "push".equalsIgnoreCase(((Push) args.getSource()).getOperateKey())) {
            Push info = (Push) args.getSource();
            System.out.println("");
            ListSelectedRowCollection select = info.getListSelectedData();
//            info.listSelectedData.get(0).primaryKeyValue

            List<Object> distinctSelect = select.stream().map(i -> i.getPrimaryKeyValue()).distinct().collect(Collectors.toList());

            if (distinctSelect.size() == 1) {
                ListSelectedRow this_dy = select.get(0);
                if ("C".equalsIgnoreCase(this_dy.getBillStatus())) {
                    Long billno = (Long) this_dy.getPrimaryKeyValue();
//            String billName = dy.getDataEntityType().getName();
                    String billName = info.getEntityId();
                    if (BFTrackerServiceHelper.isPush(billName, billno)) {
                        this.getView().showErrorNotification("单据已经下推,无法再次下推!");
                        args.setCancelMessage("单据已经下推,无法再次下推!");
                        args.setCancel(true);
                    }
                } else {
                    this.getView().showErrorNotification("单据未审核!");
                    args.setCancelMessage("单据未审核!");
                    args.setCancel(true);
                }
            } else if (distinctSelect.size() > 1) {
                this.getView().showErrorNotification("仅支持一张单据的生成下推单！");
                args.setCancelMessage("仅支持一张单据的生成下推单！");
                args.setCancel(true);
            } else {
                this.getView().showErrorNotification("请选择一张下推单！");
                args.setCancelMessage("请选择一张下推单！");
                args.setCancel(true);
            }
        }
    }

}
