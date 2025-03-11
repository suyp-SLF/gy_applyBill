package kd.cus.wb.credit.formplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;

/**
 * 保函变更插件
 * @author SHX
 * @date 2021-02-01.
 */
public class WBGuaranteeChangeBillPlugin extends AbstractBillPlugIn {

	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		super.beforeDoOperation(args);
		// 点击按钮
		FormOperate operate = (FormOperate) args.getSource();
		String operateKey = operate.getOperateKey();
		super.beforeDoOperation(args);
		if ("save".equals(operateKey) || "submitandnew".equals(operateKey) || "submit".equals(operateKey)) {
			// 当点击保存 提交的时候
			this.getModel().setValue("ispush", true);
		}
		if ("delete".equals(operateKey)) {
			// 当点击删除的时候
			this.getModel().setValue("ispush", false);
			this.getModel().setDataChanged(false);
		}
		
	}

	@Override
	public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
		super.afterDoOperation(afterDoOperationEventArgs);
		if ("submit".equals(afterDoOperationEventArgs.getOperateKey())) {
			this.getModel().setDataChanged(false);
		}
	}
}
