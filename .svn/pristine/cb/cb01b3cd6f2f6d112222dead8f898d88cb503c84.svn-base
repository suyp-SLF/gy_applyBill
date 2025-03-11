package kd.cus.wb.credit.formplugin;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.EventObject;

/**
 * 意见征询插件
 * @author SHX
 * @date 2021-02-01.
 */
public class WBGuaranteeConsultPlugin extends AbstractBillPlugIn {
	@Override
	public void afterCreateNewData(EventObject e) {
		// 查询登记单信息
		String bhdjId = (String) this.getView().getFormShowParameter().getCustomParams().get("bhdjId");
		DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(bhdjId, "bfgy_bhdjd_wb");
		if (dynamicObject != null) {
			this.getModel().setValue("bfgy_contract_no", dynamicObject.get("htno"));
			this.getModel().setValue("bfgy_contract_currency", dynamicObject.get("htcurrency"));
			this.getModel().setValue("bfgy_contract_amount", dynamicObject.get("htamount"));
			this.getModel().setValue("bfgy_project", dynamicObject.get("xmmc"));
			this.getModel().setValue("bfgy_bh_number", dynamicObject.get("bhnumber"));
			this.getModel().setValue("bfgy_bh_currency", dynamicObject.get("bhcurrency"));
			this.getModel().setValue("bfgy_bh_amount", dynamicObject.get("bhamount"));
			this.getModel().setValue("bfgy_bh_status", dynamicObject.get("bhstatus"));
		}
	}
}