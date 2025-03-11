package kd.cus.wb.credit.billplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.*;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.control.events.RowClickEventListener;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.field.TextEdit;
import kd.bos.form.operate.FormOperate;
import kd.bos.id.ID;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

//写在下游单据
public class WBCreditRegisterBillPlugin extends AbstractBillPlugIn implements RowClickEventListener {

	// 信用证登记单
	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		super.propertyChanged(e);
		String key = e.getProperty().getName();
		boolean same = this.same();
		// 修改合同金额和信用证金额，如果合同金额小于信用证金额，那么就把信用证金额置空
		if (("creditcard".equals(key) && same) || ("creditamount".equals(key) && same)) {
			BigDecimal contractamount = (BigDecimal) this.getModel().getValue("contractamount");
			BigDecimal creditamount = (BigDecimal) this.getModel().getValue("creditamount");
			if (contractamount.compareTo(creditamount) == -1) {
				this.getModel().beginInit();
				this.getModel().setValue("creditamount", null);
				this.getModel().endInit();
			}
		}
	}

	/**
	 * 判断信用证币别和合同币别是否相同
	 * @return
	 */
	private boolean same() {
		DynamicObject creditcard = (DynamicObject) this.getModel().getValue("creditcard");
		DynamicObject contractcurrency = (DynamicObject) this.getModel().getValue("contractcurrency");
		if (null != creditcard && null != contractcurrency) {
			Long creditcardL = (Long) creditcard.getPkValue();
			Long contractcurrencyL = (Long) contractcurrency.getPkValue();
			if (creditcardL.equals(contractcurrencyL)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void afterCreateNewData(EventObject e) {
		super.afterCreateNewData(e);
		// 当新增是不到(自定义的)提交
		this.getView().setVisible(false, "closereason", "bar_closesubmit");
		// 自定义ID
		Long sourcedocnumber = ID.genLongId();
		// 来源单号
		this.getModel().setValue("sourcedocnumber", sourcedocnumber);
	}

	@Override
	public void beforeBindData(EventObject e) {
		super.beforeBindData(e);
		boolean status = OperationStatus.ADDNEW.equals(getView().getFormShowParameter().getStatus());
		if (!status) {
			// 当从查看历史版本进入时
			String creditnumber = (String) this.getModel().getValue("creditnumber");
			String bbh = (String) this.getModel().getValue("bbh");
			QFilter filter = new QFilter("creditnumber", QCP.equals, creditnumber);
			DynamicObject[] bbhcon = BusinessDataServiceHelper.load("bfgy_credit_register_wb", "bbh",
					new QFilter[] { filter });
			ArrayList<String> List = new ArrayList<>();
			if (null != bbhcon && bbhcon.length > 0) {
				for (int i = 0; i < bbhcon.length; i++) {
					List.add(bbhcon[i].getString("bbh"));
				}
				String max = Collections.max(List);
				if (!bbh.equals(max)) {
					this.getView().setVisible(false, "bar_new", "bar_save", "bar_submit", "bar_audit", "bar_unaudit",
							"viewflowchart", "tblsqchange", "jdyf", "bar_ebs", "bar_closesubmit");
				}
			}
		}
	}

	@Override
	public void afterBindData(EventObject e) {
		// 当新增是不到(自定义的)提交
		this.getView().setVisible(false, "closereason", "bar_closesubmit");
		this.visible();

		boolean isnew = (boolean) this.getModel().getValue("bfgy_isnew");
		if (isnew) {
			this.getView().setVisible(true, "bfgy_history");
		} else {
			this.getView().setVisible(false, "bfgy_history");
		}
	}

	@Override
	public void afterCopyData(EventObject e) {
		// 版本号+1 所以要把V去掉
		String bbh = (String) this.getModel().getValue("bbh");
		String replace = bbh.replace("V", "");
		int i = Integer.valueOf(replace.trim());
		String V = "V";
		this.getModel().setValue("bbh", V + (i + 1));
		this.getModel().setValue("intbbh", i + 1);
		// 显示改正时间
		this.getModel().setValue("isshowdate", true);
		super.afterCopyData(e);
		int value = (int) this.getModel().getValue("amendfrequency");
		this.getModel().setValue("amendfrequency", value + 1);
		this.Copyvisible();
	}

	// 当复制时隐藏字段
	private void Copyvisible() {
		this.getView().setVisible(false, "bar_new", "bar_del", "tblsqchange", "jdyf", "bar_closesubmit", "payment");
	}

	@Override
	public void afterLoadData(EventObject e) {
		super.afterLoadData(e);

		String creditstatus = (String) this.getModel().getValue("creditstatus");
		if ("A".equals(creditstatus)) {
			this.getView().setVisible(false, "closereason");
		} else if ("B".equals(creditstatus)) {
			this.getView().setVisible(true, "closereason");
			this.getView().setEnable(false, "baritemap1");
//			this.getView().setVisible(false, "baritemap1");
		}

		// 查询意见征询记录
		QFilter opFilter1 = new QFilter("bfgy_xyz_number", QCP.equals, this.getModel().getValue("creditnumber"));
		QFilter opFilter2 = new QFilter("billstatus", QCP.equals, "C");
		Map<Object, DynamicObject> map = BusinessDataServiceHelper.loadFromCache("bfgy_credit_consult_wb",
				"bfgy_consult_content, bfgy_reply_content, creator, createtime, auditor, auditdate, bfgy_attachment",
				opFilter1.and(opFilter2).toArray());
		DynamicObjectType type = EntityMetadataCache.getDataEntityType("bd_attachment");
		for (Object key : map.keySet()) {
			DynamicObject dyn = map.get(key);
			int index = this.getModel().createNewEntryRow("bfgy_consult_entryentity");
			this.getModel().setValue("bfgy_consult_content", dyn.get("bfgy_consult_content"), index);
			this.getModel().setValue("bfgy_reply_content", dyn.get("bfgy_reply_content"), index);
			this.getModel().setValue("bfgy_consult_person", dyn.get("creator"), index);
			this.getModel().setValue("bfgy_consult_date", dyn.get("createtime"), index);
			this.getModel().setValue("bfgy_reply_person", dyn.get("auditor"), index);
			this.getModel().setValue("bfgy_reply_date", dyn.get("auditdate"), index);
			DynamicObjectCollection attachmentCollection = dyn.getDynamicObjectCollection("bfgy_attachment");
			DynamicObjectCollection dynamicObjectCollection = (DynamicObjectCollection) this.getModel().getValue("bfgy_attachment", index);
			StringBuilder filename = new StringBuilder();
			for (DynamicObject attachment : attachmentCollection) {
				DynamicObject temp = BusinessDataServiceHelper.loadSingleFromCache(attachment.getDynamicObject("fbasedataId").get("id"), type);
				filename.append(filename.length() > 0 ? ", " + temp.getString("name") : temp.getString("name"));
				dynamicObjectCollection.addNew().set("fbasedataId", temp);
			}
			if (filename.length() > 0) {
				this.getModel().setValue("bfgy_filename", filename.toString(), index);
			}
		}
	}

	public void visible() {
		FormShowParameter parameter = this.getView().getFormShowParameter();
		String yzy = parameter.getCustomParam("isclose");
		if (null != yzy) {
			if ("yes".equals(yzy)) {
				this.getView().setVisible(false, "unsubmit", "pagepanel", "fieldsetpanelap", "fs_baseinfo", "advconap",
						"attachementadv", "attachmentpanel", "flexpanelap", "bar_new", "bar_del", "bar_modify",
						"bar_save", "bar_audit", "tblsqchange", "jdyf", "creditnumber", "country", "creditcard",
						"creditamount", "kaizhengbank", "issuingtime", "noticenumber", "negotiatebank", "paymenttime",
						"tzbank", "shipmentperiod", "validityperiod", "noticetime", "transportform", "amendfrequency",
						"gzdate", "xqtime", "isshowdate", "remark", "bbh", "intbbh", "attachmentuploadbtn",
						"attachementadv1", "advconap1", "bar_ebs", "payment", "bar_refresh", "bar_unaudit", "bfgy_consult",
						"viewflowchart", "kaizhengbankebs", "tzbankebs", "negotiatebankebs", "bfgy_payment_new", "bfgy_consult_advconap,bfgy_addpro");
//				this.getView().setVisible(true, "closereason", "bar_closesubmit");
				this.getView().setVisible(true, "closereason");
				this.getView().setEnable(true, "closereason");
			} else {
				this.getView().setVisible(false, "bar_closesubmit");
			}
		}
		String genggai = parameter.getCustomParam("isgenggai");
		if ("yes".equals(genggai)) {
			this.getView().setVisible(false, "bar_new", "bar_del", "tblsqchange", "jdyf", "bar_closesubmit");
		}
	}

	// 已经下推的单据不允许下推
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		// 点击按钮
		FormOperate operate = (FormOperate) args.getSource();
		String operateKey = operate.getOperateKey();
		super.beforeDoOperation(args);
		if ("save".equals(operateKey) || "submitandnew".equals(operateKey) || "submit".equals(operateKey)) {
			// 当点击保存 提交的时候
			this.getModel().setValue("ispush", true);
			String value = (String) this.getModel().getValue("billnubmer");
			if (StringUtils.isBlank(value)) {
				this.getModel().setValue("billnubmer", this.getModel().getValue("billno"));
			}
		}
		if ("delete".equals(operateKey)) {
			// 当点击删除的时候
			this.getModel().setValue("ispush", false);
			this.getModel().setDataChanged(false);
		}

		// 当反审核操作时做标志字段判断
		if ("pushbgsqd".equals(operateKey)) {
			Boolean flag = (Boolean) this.getModel().getValue("isdown");
			// 标志值为true时不允许反审核
			if (flag) {
				args.setCancel(true);
				this.getView().showErrorNotification("已存在相关联的信用证登记单");
			}
		}

		// 信用证关闭对字段进行控制
		if ("closesubmit".equals(operateKey)) {
			BillShowParameter parameter = (BillShowParameter) this.getView().getFormShowParameter();
			String formName = parameter.getCaption();
			if ("信用证关闭".equals(formName)) {
				this.getModel().setValue("creditstatus", "B");
				this.getView().setEnable(false, "closereason");
				this.getView().invokeOperation("save");
				this.getView().showSuccessNotification("信用证关闭成功");
				this.getModel().setDataChanged(false);
			} else if ("信用证交单仪付".equals(formName)) {
				this.getView().setEnable(false, "payment");
				this.getModel().setDataChanged(false);
			}
		}
		if ("unclosesubmit".equals(operateKey)) {
			FormShowParameter parameter = this.getView().getFormShowParameter();
			String formName = parameter.getCaption();
			if ("信用证关闭".equals(formName)) {
				this.getModel().setValue("creditstatus", "A");
				this.getView().setEnable(true, "closereason");
			} else if ("信用证交单仪付".equals(formName)) {
				this.getView().setEnable(true, "payment");
			}
		}
		// 退出操作
		if ("close".equals(operateKey)) {
			BillShowParameter parameter = (BillShowParameter) this.getView().getFormShowParameter();
			String formName = parameter.getFormName();
			if ("信用证关闭".equals(formName)) {
				this.getView().getParentView().invokeOperation("refresh");
			}
		}
	}

	@Override
	public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
		super.confirmCallBack(messageBoxClosedEvent);
		String callBackId = messageBoxClosedEvent.getCallBackId();
		if (messageBoxClosedEvent.getResult() == MessageBoxResult.Yes) {
			if ("submit".equals(callBackId)) {
				this.getView().invokeOperation("submit");
			}
			if ("close".equals(callBackId)) {
				this.getModel().setValue("creditstatus", "B");
				this.getView().showSuccessNotification("信用证关闭成功");
				this.getView().invokeOperation("save");
			}
			if ("unclosecredit".equals(callBackId)) {
				this.getModel().setValue("creditstatus", "A");
				this.getView().showSuccessNotification("信用证反关闭成功");
				this.getView().invokeOperation("save");
			}
		}

	}

	@Override
	public void registerListener(EventObject e) {
		TextEdit textEdit = this.getView().getControl("syfp");
		textEdit.addClickListener(this);
		EntryGrid entryGrid = this.getView().getControl("bfgy_entryentity");
		entryGrid.addRowClickListener(this);

		this.addItemClickListeners("tbmain");
		this.addItemClickListeners("bfgy_advcontoolbarap1");
	}

	@Override
	public void beforeItemClick(BeforeItemClickEvent evt) {
		String itemKey = evt.getItemKey();
		if ("bar_submit".equals(itemKey)) {
			String bbh = (String) this.getModel().getValue("bbh");
			if ("V1".equals(bbh)) {
				ConfirmCallBackListener confirmCallBacks = new ConfirmCallBackListener("submit", this);
				String creditnumber = (String) this.getModel().getValue("creditnumber");
				this.getView().showConfirm("请确认信用证号:" + creditnumber + "是否输入正确", MessageBoxOptions.YesNo,
						ConfirmTypes.Default, confirmCallBacks);
				evt.setCancel(true);
			}
		}
		if ("baritemap1".equals(itemKey)) {
			// 关闭
			ConfirmCallBackListener confirmCallBacks = new ConfirmCallBackListener("close", this);
			this.getView().showConfirm("请确认是否关闭信用证", MessageBoxOptions.YesNo, ConfirmTypes.Default, confirmCallBacks);
			evt.setCancel(true);
		}
		if ("unclosecredit".equals(itemKey)) {
			// 反关闭
			ConfirmCallBackListener confirmCallBacks = new ConfirmCallBackListener("unclosecredit", this);
			this.getView().showConfirm("请确认是否反关闭信用证至生效状态！", MessageBoxOptions.YesNo, ConfirmTypes.Default,
					confirmCallBacks);
			evt.setCancel(true);
		}
	}

	@Override
	public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
		super.afterDoOperation(afterDoOperationEventArgs);

		String billnubmer = (String) this.getModel().getValue("billnubmer");
		QFilter qFilter = new QFilter("billnubmer", QCP.equals, billnubmer);
		ArrayList<QFilter> filters = new ArrayList<>();
		filters.add(qFilter);
		Integer maxbbh = 0;

		String operateKey = afterDoOperationEventArgs.getOperateKey();
		if ("save".equals(operateKey) || "submit".equals(operateKey) || "delete".equals(operateKey)) {
			// 当点击保存 提交的时候
			DynamicObject[] objs = BusinessDataServiceHelper.load("bfgy_credit_register_wb", "billno,intbbh,bfgy_isnew",
					filters.toArray(new QFilter[0]));
			for (DynamicObject obj : objs) {
				Integer bbh = obj.getInt("intbbh");
				if (bbh.compareTo(maxbbh) > 0)
					maxbbh = bbh;
			}
			for (DynamicObject obj : objs) {
				Integer bbh = obj.getInt("intbbh");
				if (bbh.compareTo(maxbbh) == 0) {
					obj.set("bfgy_isnew", true);
				} else {
					obj.set("bfgy_isnew", false);
				}
			}
			SaveServiceHelper.save(objs);
		}
	}

	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		if ("bfgy_billconsult".equals(evt.getItemKey())) {
			//创建弹出的单据对象并设置相关属性
			BillShowParameter billShowParameter = new BillShowParameter();
			//打开的单据页面标识
			DynamicObject currency = (DynamicObject) this.getModel().getValue("creditcard");
			if (currency != null) {
				billShowParameter.setCustomParam("bfgy_xyz_currency", currency.getPkValue());
			}
			billShowParameter.setFormId("bfgy_credit_consult_wb");
			billShowParameter.setCustomParam("xyzdjId", this.getModel().getDataEntity(true).getPkValue());
			billShowParameter.setCustomParam("bfgy_xyz_number", this.getModel().getValue("creditnumber"));
			billShowParameter.setCustomParam("bfgy_xyz_amount", this.getModel().getValue("creditamount"));
			billShowParameter.setCustomParam("bfgy_balance", this.getModel().getValue("balance"));
			//打开的单据标题
			billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
			this.getView().showForm(billShowParameter);
		}
		if ("bfgy_addpro".equals(evt.getItemKey())) {
			// 补录项目号和项目名称
			DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingleFromCache(this.getModel().getDataEntity(true).getPkValue(), this.getView().getEntityId());
			DynamicObjectCollection contractCollection = dynamicObject.getDynamicObjectCollection("bfgy_entryentity");
			List<String> contractIds = new ArrayList<>();
			for (DynamicObject contract : contractCollection) {
				if (contract.get("bfgy_contract_text") != null) {
					contractIds.add(contract.get("bfgy_contract_text").toString());
				}
			}
			if (contractIds.size() > 0) {
				QFilter filter = new QFilter("billno", QCP.in, contractIds);
				DynamicObjectCollection dynamicObjectCollection = QueryServiceHelper.query("bfgy_wbexportcontract",
						"billno, bfgy_projno, bfgy_projname", filter.toArray());
				DynamicObject contract;
				for (DynamicObject dyn : dynamicObjectCollection) {
					for (int i = 0 ; i < contractCollection.size(); i++) {
						contract = contractCollection.get(i);
						if (StringUtils.isNotEmpty(dyn.getString("billno")) && (contract.get("bfgy_contract_text").equals(dyn.get("billno")))) {
							contract.set("bfgy_project_no", dyn.get("bfgy_projno"));
							contract.set("bfgy_project", dyn.get("bfgy_projname"));
							this.getModel().setValue("bfgy_project_no", dyn.get("bfgy_projno"), i);
							this.getModel().setValue("bfgy_project", dyn.get("bfgy_projname"), i);
						}
					}
				}
				SaveServiceHelper.save(new DynamicObject[]{dynamicObject});
				this.getView().showSuccessNotification("补录成功");
			}
		}
		if ("bfgy_history".equals(evt.getItemKey())) {
			ListShowParameter listShowParameter = new ListShowParameter();
			listShowParameter.setFormId("bos_list");
			listShowParameter.setBillFormId(this.getView().getEntityId());
			listShowParameter.setCaption("历史版本");

			QFilter filter1 = new QFilter("sourcedocnumber", QCP.equals, this.getModel().getValue("sourcedocnumber"));
			List<QFilter> filterList = new ArrayList<>();
			filterList.add(filter1);
			ListFilterParameter listFilterParameter = new ListFilterParameter(filterList, "intbbh DESC");
			listShowParameter.setListFilterParameter(listFilterParameter);
			listShowParameter.setCustomParam("type", "history");
			listShowParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
			this.getView().showForm(listShowParameter);
		}
	}
}