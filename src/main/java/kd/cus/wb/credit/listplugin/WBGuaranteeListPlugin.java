package kd.cus.wb.credit.listplugin;

import kd.bos.bill.BillShowParameter;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.AppMetadataCache;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.ListSelectedRow;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.form.*;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.form.operate.FormOperate;
import kd.bos.list.BillList;
import kd.bos.list.ListShowParameter;
import kd.bos.list.events.BeforeShowBillFormEvent;
import kd.bos.list.events.ListRowClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.permission.model.PermIsoDimType;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.permission.PermissionServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.util.StringUtils;

import java.util.EventObject;
import java.util.List;
import java.util.Map;

/**
 * 出口保函、分包商保函列表插件
 * @author SHX
 * @date 2021-02-01.
 */
public class WBGuaranteeListPlugin extends AbstractListPlugin {
	private boolean flag = true;

	@Override
	public void setFilter(SetFilterEvent e) {
		super.setFilter(e);
		ListShowParameter parameter = (ListShowParameter) this.getView().getFormShowParameter();
		// 单据类型
		String billType = parameter.getCustomParam("bfgy_billtype");
		if (StringUtils.isNotEmpty(billType)) {
			e.getQFilters().add(new QFilter("bfgy_billtype.number", QCP.equals, billType));
		}
		// 只显示最新版本保函登记
		if (flag) {
			e.getQFilters().add(new QFilter("bfgy_isnew", QCP.equals, '1'));
		}
	}

	// 列表显示按钮
	@Override
	public void afterBindData(EventObject e) {
		super.afterBindData(e);

		FormShowParameter parameter = this.getView().getFormShowParameter();
		String billType = parameter.getCustomParam("bfgy_billtype");
		if (StringUtils.isNotEmpty(billType)) {
			if ("bfgy_bhdjd_wb_BT_fbs".equals(billType)) {
				this.getView().setVisible(false, "tbltrackup");
			}
		}
	}

	@Override
	public void beforeShowBill(BeforeShowBillFormEvent e) {
		super.beforeShowBill(e);

		// 单据类型
		FormShowParameter parameter = this.getView().getFormShowParameter();
		String billType = parameter.getCustomParam("bfgy_billtype");
		BillShowParameter setname = e.getParameter();

		QFilter[] qfilters = new QFilter[]{new QFilter("number", QCP.equals, billType)};
		DynamicObject billtype = BusinessDataServiceHelper.loadSingle("bos_billtype", "id",qfilters); //获得点击按钮对应的单据类型
		e.getParameter().setBillTypeId(billtype.getString("id")); //通过系统方法，直接改变页面单据类型

		switch (billType) {
			// 出口保函
			case "bfgy_bhdjd_wb_BT":
				setname.setCaption("出口保函");
				break;
			// 分包商保函
			case "bfgy_bhdjd_wb_BT_fbs":
				setname.setCaption("分包商保函");
				break;
		}
	}

	// 权限确认(按个人确权，默认属于总组织)
	private Integer checkFunctionPermissionByDep(String formId, Long deptId, String permissionItemId) {
		long userId = Long.parseLong(RequestContext.get().getUserId());
//				Integer result=PermissionServiceHelper.checkPermission(Long.valueOf(userId), PermIsoDimType.DIM_ORG, RequestContext.get().getOrgId(), "bfgy_pro", "bfgy_product_fsc", permissionItemId);
		String appId = AppMetadataCache.getAppInfo("bfgy_guarantee").getId(); // SPGGALVYMG4
		Integer result = PermissionServiceHelper.checkPermission(Long.valueOf(userId), PermIsoDimType.DIM_ORG, 0L,
				appId, "bfgy_bhdjd_wb", permissionItemId);
		return result;
	}

	/*
	 * 保函撤销、意见征询
	 */
	@Override
	public void itemClick(ItemClickEvent evt) {
		super.itemClick(evt);
		String key = evt.getItemKey();
		// 意见征询
		if ("bfgy_consult".equals(key)) {

			BillList billList = this.getControl("billlistap");
			ListSelectedRowCollection selectedRows = billList.getSelectedRows();
			if (selectedRows != null && selectedRows.size() > 0) {
				String listSelectedRowId = selectedRows.get(0).toString();
				this.showOpinionForm(listSelectedRowId);
//				// 先判断是否已经意见征询完成
//				DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(listSelectedRowId, "bfgy_bhdjd_wb");
//				if (dynamicObject != null) {
//					String bhnumber = (String) dynamicObject.get("bhnumber");
//					QFilter qFilter1 = new QFilter("bfgy_bh_number", QCP.equals, bhnumber);
//					QFilter qFilter2 = new QFilter("billstatus", QCP.equals, "C");
//					Map<Object, DynamicObject> map = BusinessDataServiceHelper.loadFromCache("bfgy_consult_wb", qFilter1.and(qFilter2).toArray());
//					if (map.size() > 0) {
//						this.getView().showTipNotification("意见征询已完成");
//					} else {
//					}
//				} else {
//					this.showOpinionForm(listSelectedRowId);
//				}
			}
		}

		if ("bhcancel".equals(key)) {
			Long userId = UserServiceHelper.getCurrentUserId();
			List<Long> orglist = UserServiceHelper.getAllDepartmentByUserId(userId);// 当前用户所属部门组织
			FormShowParameter showParameter = this.getView().getFormShowParameter();

			// 子产品
			boolean sonproduct = false;
			for (int i = 0; i < orglist.size(); i++) {
//				Integer products = checkFunctionPermissionByDep(showParameter.getFormId(), orglist.get(i),
//						PermissionStatusext.bhcancel);
//				Integer products = 0;
//				if (products == 1) {
//					sonproduct = true;
					// 新建页面
					BillShowParameter billshowParameter = new BillShowParameter();
					// 要显示的单据的标识
					billshowParameter.setFormId("bfgy_bhdjd_wb");
					// 传递fid
					BillList billList = this.getControl("billlistap");
					ListSelectedRowCollection selectedRows = billList.getSelectedRows();
					if (selectedRows != null && selectedRows.size() > 0) {
						// 当点击信用证关闭时,把信用证状态改为关闭状态
						String listSelectedRow = selectedRows.get(0).toString();
						billshowParameter.setPkId(listSelectedRow);
						// 设置显示页面类型
						billshowParameter.getOpenStyle().setShowType(ShowType.Modal);
						// 设置列表界面的样式
						StyleCss css = new StyleCss();
						css.setWidth("880px");
						css.setHeight("600px");
						billshowParameter.getOpenStyle().setInlineStyleCss(css);
						billshowParameter.setCustomParam("isclose", "yes");
						billshowParameter.setCaption("保函撤销");
						this.getView().showForm(billshowParameter);
						break;
					}
//				}
//				if (sonproduct == false) {
//					this.getView().setEnable(false, "advconap2");
//					this.getView().showTipNotification("对不起，暂无权限操作生产单位！");
//				}
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
			DynamicObject[] type = BusinessDataServiceHelper.load("bfgy_bhdjd_wb", "billstatus,bhstatus,bfgy_isnew,bhklsqdh",
					filters);
			String billstatus = type[0].get("billstatus").toString().trim();
			String bhstatus = type[0].get("bhstatus").toString().trim();
			Boolean isnew = type[0].getBoolean("bfgy_isnew");

//			撤销功能：财金部保函业务员可对“已审核”（单据状态）+“开立”（保函状态）状态的【保函登记单】执行“保函撤销”操作
//			，点击后【保函登记单】进入编辑状态，“保函状态”字段变成“撤销”。
			if ("C".equals(billstatus) && ("KL".equals(bhstatus) || "CHANGE".equals(bhstatus)) && isnew) {
				this.getView().setVisible(true, "bhcancel", "tblchange", "bfgy_consult");
			} else {
				this.getView().setVisible(false, "bhcancel", "genggai", "tblchange", "bfgy_consult");
			}
		}
	}

	/**
	 * 变更：需要先查询变更申请单，并且需要判断变更申请单是否已经登记
	 * 刷新
	 * 查看历史：查看登记单的历史版本
	 * 上查
	 * 下查
	 */
	@Override
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		super.beforeDoOperation(args);
		FormOperate operate = (FormOperate) args.getSource();
		String operateKey = operate.getOperateKey();

		if ("copychenge".equals(operateKey)) {
			ListSelectedRowCollection listSelectedData = operate.getListSelectedData();
			String billNo = listSelectedData.get(0).getBillNo();
			IPageCache service = this.getView().getMainView().getService(IPageCache.class);
			service.put("billlistNo", billNo);
			QFilter qFilter = new QFilter("registrationnumber", QCP.equals, billNo);
			QFilter[] filters = new QFilter[]{qFilter};
			// 查询单据的相关的唯一标识
			DynamicObject[] lydh = BusinessDataServiceHelper.load("bfgy_guaranteechange_wb", "iszxchenge,billstatus",
					filters);

			if (null != lydh && lydh.length > 0) {
				// 查询保函变更申请单,上的“是否已执行变更”字段为“否”
				Boolean iszxchenge = (Boolean) lydh[0].get("iszxchenge");
				String billstatus = (String) lydh[0].get("billstatus");
				if (iszxchenge && billstatus.equals("C")) {
					args.setCancel(true);
					this.getView().showErrorNotification("该保函对应的变更申请单已完成了变更登记，不可再次登记");
				}
			} else {
				// 没找到单据信息
				this.getView().showErrorNotification("未找到对应的保函变更申请单");
				args.setCancel(true);
			}

		}
		if ("refresh".equals(operateKey)) {
			this.getView().setVisible(true, "tblcopy");
		}

		if ("history".equals(operateKey)) {
			flag = false;
			this.getView().setVisible(false, "tblcopy");
			// 选中某一条【信用证登记单】后，点击“查看历史版本”按钮，查
			// 看所有变更与更改时存留的旧版本的【信用证登记单】列表页（如最新版本为V3，点击本按钮查看V1和V2版本），
			// 只需把单头字段全部展示在列表页，不需要点击开发查看详情功能
			ListShowParameter ListShowParameter = (ListShowParameter) this.getView().getFormShowParameter();
			ListSelectedRowCollection listSelectedData = operate.getListSelectedData();
			Object keyValue = listSelectedData.get(0).getPrimaryKeyValue();
			DynamicObject information = BusinessDataServiceHelper.loadSingle(keyValue, "bfgy_bhdjd_wb");
			// 获取单据的唯一标识
			String sourceid = (String) information.get("bhklsqdh");
			QFilter qFilter = new QFilter("bhklsqdh", QCP.equals, sourceid);
			ListShowParameter.getListFilterParameter().setFilter(qFilter);
			this.getView().invokeOperation("refresh");
		}

		// 上查
		if ("trackupexc".equals(operateKey)) {
			ListSelectedRowCollection listSelectedData = operate.getListSelectedData();
			Object keyValue = listSelectedData.get(0).getPrimaryKeyValue();
			DynamicObject information = BusinessDataServiceHelper.loadSingle(keyValue, "bfgy_bhdjd_wb");
			// 获取单据的唯一标识
			String sourceid = (String) information.get("bhklsqdh");
			// 新建页面
			ListShowParameter listshowParameter = new ListShowParameter();
			// 要显示的单据的标识
			listshowParameter.setBillFormId("bfgy_guarantee_apply_wb");
			// 设置显示页面类型
			listshowParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
			// 设置不显示列表上的过滤模块
			listshowParameter.setLookUp(true);
			// 关闭列表时调用
			listshowParameter.setCloseCallBack(new CloseCallBack(this, "pack"));
			QFilter qFilter = new QFilter("billno", QCP.equals, sourceid);
			listshowParameter.getListFilterParameter().setFilter(qFilter);
			listshowParameter.setMultiSelect(false);
			this.getView().showForm(listshowParameter);
		}

		// 下查bfgy_bhdjd_wb
		if ("trackdownexc".equals(operateKey)) {
			ListSelectedRowCollection listSelectedData = operate.getListSelectedData();
			Object keyValue = listSelectedData.get(0).getPrimaryKeyValue();
			DynamicObject information = BusinessDataServiceHelper.loadSingle(keyValue, "bfgy_bhdjd_wb");
			// 获取单据的唯一标识
			String bhklsqdh = (String) information.get("bhklsqdh");
			// 新建页面
			ListShowParameter listshowParameter = new ListShowParameter();
			// 要显示的单据的标识
			listshowParameter.setBillFormId("bfgy_guaranteechange_wb");
			// 设置显示页面类型
			listshowParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
			// 设置不显示列表上的过滤模块
			listshowParameter.setLookUp(true);
			// 关闭列表时调用
			listshowParameter.setCloseCallBack(new CloseCallBack(this, "back"));
			QFilter qFilter = new QFilter("requestbillno", QCP.equals, bhklsqdh);
			listshowParameter.getListFilterParameter().setFilter(qFilter);
			listshowParameter.setMultiSelect(false);

			this.getView().showForm(listshowParameter);
		}
	}

	@Override
	public void closedCallBack(ClosedCallBackEvent e) {
		super.closedCallBack(e);
		String actionId = e.getActionId();

		if ("isclose".equals(actionId)) {
			this.getView().updateView();
		}
		// 关闭弹出页面时
		// 上查
		if ("pack".equals(actionId)) {
			ListSelectedRowCollection collection = (ListSelectedRowCollection) e.getReturnData();
			if (collection != null && !collection.isEmpty()) {
				String billNo = collection.get(0).getBillNo();
				DynamicObjectCollection excon = QueryServiceHelper.query("bfgy_guarantee_apply_wb", "id",
						new QFilter[] { new QFilter("billno", QCP.equals, billNo) });
				BillShowParameter billParameter = new BillShowParameter();
				billParameter.setFormId("bfgy_guarantee_apply_wb");
				billParameter.setPkId(excon.get(0).get("id"));
				billParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
				this.getView().showForm(billParameter);
			}
		}

		// 下查
		if ("back".equals(actionId)) {
			ListSelectedRowCollection collection = (ListSelectedRowCollection) e.getReturnData();
			if (null != collection && !collection.isEmpty()) {
				String billNo = collection.get(0).getBillNo();
				DynamicObjectCollection excon = QueryServiceHelper.query("bfgy_guaranteechange_wb", "id",
						new QFilter[] { new QFilter("billno", QCP.equals, billNo) });
				BillShowParameter billParameter = new BillShowParameter();
				billParameter.setFormId("bfgy_guaranteechange_wb");
				billParameter.setPkId(excon.get(0).get("id"));
				billParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
				this.getView().showForm(billParameter);
			}
		}
	}


	/**
	 * 显示意见征询新增页面
	 */
	private void showOpinionForm(String listSelectedRowId) {
		// 新建页面
		BillShowParameter billshowParameter = new BillShowParameter();
		// 要显示的单据的标识
		billshowParameter.setFormId("bfgy_consult_wb");
		// 设置显示页面类型
		billshowParameter.getOpenStyle().setShowType(ShowType.Modal);
		billshowParameter.setCustomParam("bhdjId", listSelectedRowId);
		billshowParameter.setCaption("意见征询");
		this.getView().showForm(billshowParameter);
	}
}
