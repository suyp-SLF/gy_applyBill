package kd.cus.wb.credit.listplugin;

import kd.bos.bill.BillShowParameter;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.AppMetadataCache;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.*;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.BillList;
import kd.bos.list.ListShowParameter;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.permission.model.PermIsoDimType;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.permission.PermissionServiceHelper;

import java.math.BigDecimal;
import java.util.EventObject;

public class WBCreditRegisterListPlugin extends AbstractListPlugin {

	private boolean flag = true;

	@Override
	public void setFilter(SetFilterEvent e) {
		super.setFilter(e);
		// 只显示最新版本保函登记
//		if (flag) {
//
//		}
        Object param = this.getView().getFormShowParameter().getCustomParam("type");
        if (param == null || !"history".equals(param)) {
            e.getQFilters().add(new QFilter("bfgy_isnew", QCP.equals, true));
        }

	}

	// 权限确认(按个人确权，默认属于总组织)
	private Integer checkFunctionPermissionByDep(String formId, Long deptId, String permissionItemId) {
		long userId = Long.parseLong(RequestContext.get().getUserId());
		String appId = AppMetadataCache.getAppInfo("bfgy_xyz_wb").getId();
		Integer result = PermissionServiceHelper.checkPermission(Long.valueOf(userId), PermIsoDimType.DIM_ORG, 0L,
				appId, "bfgy_credit_register_wb", permissionItemId);
		return result;
	}

	/**
	 * 财金部信用证业务员可对“已审核”+“生效”状态的【信用证登记单】执行“信用证关闭”操作，
	 * 点击后信用证进入编辑状态，“信用证状态”字段变成“关闭”，“信用证关闭原因”字段显示 （或变成可编辑状态），此时单据只能进行“提交”或“关闭”操作
	 */
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		String key = evt.getItemKey();
		BillList billList = this.getControl("billlistap");
		ListSelectedRowCollection selectedRows = billList.getSelectedRows();
		// 财金部信用证业务员可对“已审核”+“生效”状态的【信用证登记单】执行“信用证关闭”操作
		if ("tblxyzclose".equals(key)) {
			if (selectedRows != null && selectedRows.size() > 0) {
				if (selectedRows.size() > 1) {
					this.getView().showTipNotification("只能选择一行！");
				} else {
					// 新建页面
					BillShowParameter billshowParameter = new BillShowParameter();
					// 要显示的单据的标识
					billshowParameter.setFormId("bfgy_credit_register_wb");
					String listSelectedRow = selectedRows.get(0).toString();
					billshowParameter.setPkId(listSelectedRow);
					billshowParameter.getOpenStyle().setShowType(ShowType.Modal);
					StyleCss css = new StyleCss();
					css.setWidth("880px");
					css.setHeight("600px");
					billshowParameter.getOpenStyle().setInlineStyleCss(css);
					billshowParameter.setCustomParam("isclose", "yes");
					billshowParameter.setCaption("信用证关闭");
					billshowParameter.setCloseCallBack(new CloseCallBack(this, "isclose"));
					this.getView().showForm(billshowParameter);
				}
			}
		}
		if ("unclosecredit".equals(key)) {
			if (selectedRows != null && selectedRows.size() > 0) {
				if (selectedRows.size() > 1) {
					this.getView().showTipNotification("只能选择一行！");
				} else {
					// 反关闭
					ConfirmCallBackListener confirmCallBacks = new ConfirmCallBackListener("unclosecreditaction");
					this.getView().showConfirm("请确认是否反关闭信用证至生效状态！", MessageBoxOptions.YesNo, confirmCallBacks);
				}
			}
		}
		if ("bfgy_consult".equals(key)) {
			if (selectedRows != null && selectedRows.size() > 0) {
				if (selectedRows.size() > 1) {
					this.getView().showTipNotification("只能选择一行！");
				} else {
					ListSelectedRow listSelectedRow = selectedRows.get(0);
					ListShowParameter listShowParameter= (ListShowParameter) this.getView().getFormShowParameter();
					String formId = listShowParameter.getBillFormId();
					DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingleFromCache(listSelectedRow.getPrimaryKeyValue(), formId);
					if (dynamicObject != null) {
						String creditnumber = dynamicObject.getString("creditnumber");
						DynamicObject creditcard = dynamicObject.getDynamicObject("creditcard");
						BigDecimal creditamount = dynamicObject.getBigDecimal("creditamount");
						BigDecimal balance = dynamicObject.getBigDecimal("balance");

						//创建弹出的单据对象并设置相关属性
						BillShowParameter billShowParameter = new BillShowParameter();
						//打开的单据页面标识
						billShowParameter.setFormId("bfgy_credit_consult_wb");
						billShowParameter.setCustomParam("xyzdjId", listSelectedRow.getPrimaryKeyValue());
						billShowParameter.setCustomParam("bfgy_xyz_number", creditnumber);
						billShowParameter.setCustomParam("bfgy_xyz_currency", creditcard.getPkValue());
						billShowParameter.setCustomParam("bfgy_xyz_amount", creditamount);
						billShowParameter.setCustomParam("bfgy_balance", balance);
						//打开的单据标题
						billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
						this.getView().showForm(billShowParameter);
					}
				}
			}

		}
	}

	@Override
	public void listRowClick(ListRowClickEvent evt) {
		// 只选择一个
		ListSelectedRow currentListSelectedRow = evt.getCurrentListSelectedRow();
		ListSelectedRowCollection listSelectedRowCollection = evt.getListSelectedRowCollection();
		if (null != listSelectedRowCollection && listSelectedRowCollection.size() != 0) {
			String billNo = currentListSelectedRow.getBillNo();
			QFilter qFilter = new QFilter("billno", QCP.equals, billNo);
			QFilter[] filters = new QFilter[] { qFilter };
			DynamicObject[] type = BusinessDataServiceHelper.load("bfgy_credit_register_wb", "billstatus,creditstatus",
					filters);
			String billstatus = type[0].get("billstatus").toString().trim();
			String creditstatus = type[0].get("creditstatus").toString().trim();
//			Boolean isnew = type[0].getBoolean("bfgy_isnew");

			// 关闭状态下   隐藏信用证关闭按钮
			if ("B".equals(creditstatus)) {
//				this.getView().setVisible(false, "tblxyzclose");
				this.getView().setEnable(false, "tblxyzclose");
				this.getView().setEnable(true, "unclosecredit");
				this.getView().setVisible(false, "genggai");
			} else {
				this.getView().setEnable(true, "tblxyzclose");
				this.getView().setEnable(false, "unclosecredit");
				this.getView().setVisible(true, "genggai");
			}
		}

	}

	@Override
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		super.beforeDoOperation(args);
		FormOperate operate = (FormOperate) args.getSource();
		String operateKey = operate.getOperateKey();
		if ("history".equals(operateKey)) {

			flag = false;
			this.getView().setVisible(false, "tblcopy");
			// 选中某一条【信用证登记单】后，点击“查看历史版本”按钮，查
			// 看所有变更与更改时存留的旧版本的【信用证登记单】列表页（如最新版本为V3，点击本按钮查看V1和V2版本），
			// 只需把单头字段全部展示在列表页，不需要点击开发查看详情功能
			ListShowParameter ListShowParameter = (ListShowParameter) this.getView().getFormShowParameter();
			ListSelectedRowCollection listSelectedData = operate.getListSelectedData();
			Object keyValue = listSelectedData.get(0).getPrimaryKeyValue();
			DynamicObject information = BusinessDataServiceHelper.loadSingle(keyValue, "bfgy_credit_register_wb");
			// 获取单据的唯一标识
			String sourcedocnumber = (String) information.get("sourcedocnumber");
			QFilter qFilter = new QFilter("sourcedocnumber", QCP.equals, sourcedocnumber);
			ListShowParameter.getListFilterParameter().setFilter(qFilter);
			this.getView().invokeOperation("refresh");

		}
		// 上查
//		if ("trackupexc".equals(operateKey)) {
//			ListSelectedRowCollection listSelectedData = operate.getListSelectedData();
//			Object keyValue = listSelectedData.get(0).getPrimaryKeyValue();
//			DynamicObject information = BusinessDataServiceHelper.loadSingle(keyValue, "bfgy_credit_register");
//			// 获取单据的唯一标识
//			String sourceid = (String) information.get("sourcedocnumber");
//			// 新建页面
//			ListShowParameter listshowParameter = new ListShowParameter();
//			// 要显示的单据的标识
//			listshowParameter.setBillFormId("bfgy_xyzklsqd");
//			// 设置显示页面类型
//			listshowParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
//			// 设置不显示列表上的过滤模块
//			listshowParameter.setLookUp(true);
//			// 关闭列表时调用
//			listshowParameter.setCloseCallBack(new CloseCallBack(this, "pack"));
//			listshowParameter.setCustomParam("source", sourceid);
//			listshowParameter.setMultiSelect(false);
//			this.getView().showForm(listshowParameter);
//		}
//		// 下查
//		if ("trackdownexc".equals(operateKey)) {
//			ListSelectedRowCollection listSelectedData = operate.getListSelectedData();
//			Object keyValue = listSelectedData.get(0).getPrimaryKeyValue();
//			DynamicObject information = BusinessDataServiceHelper.loadSingle(keyValue, "bfgy_credit_register");
//			// 获取单据的单据编号
//			String sourcedocnumber = (String) information.get("sourcedocnumber");
//			// 根据唯一标识查询最低版本的信息
//			QFilter source = new QFilter("sourcedocnumber", QCP.equals, sourcedocnumber);
//			DynamicObject[] load = BusinessDataServiceHelper.load("bfgy_credit_register", "bbh",
//					new QFilter[] { source });
//			ArrayList arrayList = new ArrayList<>();
//			for (int i = 0; i < load.length; i++) {
//				String bbh = (String) load[i].get("bbh");
//				arrayList.add(new Integer(bbh.replaceAll("V", "")));
//			}
//			Object Min = Collections.min(arrayList);
//			String min = "V" + Min.toString();
//			QFilter qFilter2 = new QFilter("bbh", QCP.equals, min);
//			qFilter2.and(source);
//			DynamicObject[] load2 = BusinessDataServiceHelper.load("bfgy_credit_register", "billno",
//					new QFilter[] { qFilter2 });
//			// 最小的版本号
//			String billNo = (String) load2[0].get("billno");
//			// 新建页面
//			ListShowParameter listshowParameter = new ListShowParameter();
//			// 要显示的单据的标识
//			listshowParameter.setBillFormId("bfgy_xyzbgsqd");
//			// 设置显示页面类型
//			listshowParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
//			// 设置不显示列表上的过滤模块
//			listshowParameter.setLookUp(true);
//			// 关闭列表时调用
//			listshowParameter.setCloseCallBack(new CloseCallBack(this, "back"));
//			listshowParameter.setCustomParam("billNo", billNo);
//			listshowParameter.setMultiSelect(false);
//			this.getView().showForm(listshowParameter);
//		}
	}

	@Override
	public void closedCallBack(ClosedCallBackEvent e) {
		super.closedCallBack(e);
		String actionId = e.getActionId();
		// 关闭弹出页面时
		if ("isclose".equals(actionId)) {
			this.getView().updateView();
		}

		// 上查
//		if ("pack".equals(actionId)) {
//			ListSelectedRowCollection collection = (ListSelectedRowCollection) e.getReturnData();
//			if (null != collection && !collection.isEmpty()) {
//				String billNo = collection.get(0).getBillNo();
//				DynamicObjectCollection excon = QueryServiceHelper.query("bfgy_xyzklsqd", "id",
//						new QFilter[] { new QFilter("billno", QCP.equals, billNo) });
//				BillShowParameter billParameter = new BillShowParameter();
//				billParameter.setFormId("bfgy_xyzklsqd");
//				billParameter.setPkId(excon.get(0).get("id"));
//				billParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
//				this.getView().showForm(billParameter);
//			}
//		}
		// 下查
//		if ("back".equals(actionId)) {
//			ListSelectedRowCollection collection = (ListSelectedRowCollection) e.getReturnData();
//			if (null != collection && !collection.isEmpty()) {
//				String billNo = collection.get(0).getBillNo();
//				DynamicObjectCollection excon = QueryServiceHelper.query("bfgy_xyzbgsqd", "id",
//						new QFilter[] { new QFilter("billno", QCP.equals, billNo) });
//				BillShowParameter billParameter = new BillShowParameter();
//				billParameter.setFormId("bfgy_xyzbgsqd");
//				billParameter.setPkId(excon.get(0).get("id"));
//				billParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
//				this.getView().showForm(billParameter);
//			}
//		}
	}

	@Override
	public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
		super.confirmCallBack(messageBoxClosedEvent);
		// 反关闭
		if ("unclosecreditaction".equals(messageBoxClosedEvent.getCallBackId())) {
			BillList billList = this.getControl("billlistap");
			ListSelectedRowCollection selectedRows = billList.getSelectedRows();
			DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingleFromCache(selectedRows.get(0).getPrimaryKeyValue(), "bfgy_credit_register_wb");
			if (dynamicObject != null) {
				dynamicObject.set("creditstatus", "A");
				SaveServiceHelper.save(new DynamicObject[]{dynamicObject});
				this.getView().invokeOperation("refresh");
			}
		}
	}

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        Object param = this.getView().getFormShowParameter().getCustomParam("type");
        if (param != null && "history".equals(param)) {
            this.getView().setVisible(false, "tblnew","genggai", "tblsubmit", "bfgy_baritemap2", "tblxyzclose"
                    ,"bfgy_consult", "tblchange", "baritemap", "tbldel");
        }
    }


}
