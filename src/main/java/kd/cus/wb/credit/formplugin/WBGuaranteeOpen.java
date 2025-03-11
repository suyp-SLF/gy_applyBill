package kd.cus.wb.credit.formplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.resource.ResManager;
import kd.bos.dataentity.resource.SubSystemType;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.MessageBoxOptions;
import kd.bos.form.MessageBoxResult;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.field.BasedataEdit;
import kd.bos.form.field.DecimalEdit;
import kd.bos.form.field.TextEdit;
import kd.bos.form.operate.FormOperate;

import java.math.BigDecimal;
import java.util.EventObject;

/**
 * 保函申请单插件
 * @author SHX
 * @date 2021-02-01.
 */
public class WBGuaranteeOpen extends AbstractBillPlugIn {

	@Override
	public void afterCreateNewData(EventObject e) {
		super.afterCreateNewData(e);
		long orgId = RequestContext.get().getOrgId();
		this.getModel().setValue("org", orgId);
	}

	@Override
	public void afterBindData(EventObject e) {
		super.afterBindData(e);
		this.setMustInput();
		this.setBidMustInput();
	}

	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		super.propertyChanged(e);
		// 合同号
		String key = e.getProperty().getName();
		ChangeData[] changeData = e.getChangeSet();
		Object newValue = changeData[0].getNewValue();
		Object oldValue = changeData[0].getOldValue();
		if (newValue != oldValue) {
			switch (key) {
				case "contractamount":
					this.count();
					break;
				case "paymentscale":
					this.count();
					break;
				case "kjfs":
					this.setMustInput();
					break;
				case "bhlxcombo":
					this.setBidMustInput();
					this.clearInfo(oldValue, newValue);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * 投标类型，如果旧值非001，新值是001，则清空合同号、合同金额、项目代码、项目名称
	 * @param oldValue
	 * @param newValue
	 */
	private void clearInfo(Object oldValue, Object newValue) {
		DynamicObject oldObj = (DynamicObject) oldValue;
		DynamicObject newObj = (DynamicObject) newValue;
		if (oldObj != null && newObj != null && !"001".equals(oldObj.get("number")) && "001".equals(newObj.get("number"))) {
			this.getModel().setValue("contractnumber", null);
			this.getModel().setValue("contractamount", BigDecimal.ZERO);
			this.getModel().setValue("bfgy_project_no", "");
			this.getModel().setValue("projectname", "");
		}
	}

	/**
	 * 设置投标必填
	 */
	private void setBidMustInput() {
		TextEdit textEdit = this.getControl("marking");
		BasedataEdit currency = this.getControl("contractcurrency");
		DecimalEdit amount = this.getControl("contractamount");

		DynamicObject dynamicObject = (DynamicObject) this.getModel().getValue("bhlxcombo");
		if (dynamicObject != null) {
			String number = (String) dynamicObject.get("number");
			if (StringUtils.isNotEmpty(number) && "001".equals(number)) {
				textEdit.setMustInput(true);
				currency.setMustInput(true);
				amount.setMustInput(true);
			} else {
				textEdit.setMustInput(false);
				currency.setMustInput(false);
				amount.setMustInput(false);
			}
		} else {
			textEdit.setMustInput(false);
			currency.setMustInput(false);
			amount.setMustInput(false);
		}
	}

	// 设置银行必填
	private void setMustInput() {
		TextEdit textEdit = this.getControl("ddbrankname");
		DynamicObject dynamicObject = (DynamicObject) this.getModel().getValue("kjfs");
		if (dynamicObject != null) {
			String number = (String) dynamicObject.get("number");
			if (StringUtils.isNotEmpty(number)) {
				boolean b = "002".equals(number) || "003".equals(number) || "004".equals(number) || "005".equals(number);
				if (b) {
					textEdit.setMustInput(true);
				} else {
					textEdit.setMustInput(false);
				}
			}
		} else {
			textEdit.setMustInput(false);
		}
	}

	/**
	 * 计算保函金额
	 */
	private void count() {
		// 支付比例
		BigDecimal paymentscale = (BigDecimal) this.getModel().getValue("paymentscale");
		// 合同金额
		BigDecimal contractamount = (BigDecimal) this.getModel().getValue("contractamount");

		Boolean scale = scale(paymentscale);
		if (scale) {
			BigDecimal bhamount = paymentscale.multiply(contractamount).divide(new BigDecimal(100));
			this.getModel().setValue("bhamount", bhamount);
		} else {
			this.getModel().setValue("bhamount", null);
			this.getView().updateView("bhamount");
		}
	}

	/**
	 * 校验比率
	 * @param paymentscale
	 * @return
	 */
	private Boolean scale(BigDecimal paymentscale) {
		if (paymentscale.compareTo(new BigDecimal(100)) == 1 && (paymentscale.compareTo(new BigDecimal(0)) == 1)) {
			this.getModel().beginInit();
			this.getModel().setValue("paymentscale", null);
			this.getModel().endInit();
			return false;
		}
		return true;
	}

	@Override
	public void registerListener(EventObject e) {
		super.registerListener(e);
		TextEdit textEdit = this.getView().getControl("contractnumber");
		textEdit.addClickListener(this);
	}

	@Override
	public void click(EventObject evt) {
		// source点击资源
		TextEdit source = (TextEdit) evt.getSource();
		// 点击标识
		String key = source.getKey();
		if ("contractnumber".equals(key)) {
			this.getView().invokeOperation("drawcontract");
		}
	}

	// 已经下推的单据不允许下推
	@Override
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		// 点击按钮
		FormOperate operate = (FormOperate) args.getSource();
		String operateKey = operate.getOperateKey();
		super.beforeDoOperation(args);
		// 当反审核操作时做标志字段判断
		if ("push".equals(operateKey)) {
			Boolean flag = (Boolean) this.getModel().getValue("ispush");
			// 标志值为true时不允许反审核
			if (flag) {
				args.setCancel(true);
				this.getView().showErrorNotification("已存在相关联的保函登记单");
			}
		}

		if ("dosave".equals(operateKey)) {
			this.getView().showConfirm("保函起始日期是否为保函开立之日?", MessageBoxOptions.OKCancel, new ConfirmCallBackListener("dosavecallback"));
			args.setCancel(true);
		}
	}

	@Override
	public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
		super.confirmCallBack(messageBoxClosedEvent);
		String callBackId = messageBoxClosedEvent.getCallBackId();
		MessageBoxResult result = messageBoxClosedEvent.getResult();
		if ("dosavecallback".equals(callBackId)) {
			if (this.getModel().getValue("bfgy_startdate") != null) {
				if (MessageBoxResult.Yes.equals(result)) {
					this.getModel().setValue("bfgy_use_satart_date", true);
				} else {
					this.getModel().setValue("bfgy_use_satart_date", false);
				}
			}
			this.getView().invokeOperation("save");
		}
	}
}