package kd.cus.wb.credit.operateplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.servicehelper.MetadataServiceHelper;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class WBHqCreditRegisterSubOPPlugin extends AbstractOperationServicePlugIn {
	@Override
	public void onPreparePropertys(PreparePropertysEventArgs e) {
		super.onPreparePropertys(e);
		// 提前加载表单里的字段
		List<String> fieldKeys = e.getFieldKeys();
		MainEntityType dt = MetadataServiceHelper.getDataEntityType("bfgy_credit_register_wb");
		Map<String, IDataEntityProperty> fields = dt.getAllFields();
		fields.forEach((Key, value) -> {
			fieldKeys.add(Key);
		});
	}

	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		super.onAddValidators(e);
		e.addValidator(new CreditRegisterSubmitValidator());
	}

	class CreditRegisterSubmitValidator extends AbstractValidator {

		@Override
		public void validate() {
			ExtendedDataEntity[] entities = this.getDataEntities();
			if (this.dataEntities[0] != null) {
				for (int i = 0; i < entities.length; i++) {
					DynamicObject creditRegister = entities[i].getDataEntity();
					Date issuingtime = (Date) creditRegister.get("issuingtime");//开证日期
					Date shipmentperiod = (Date) creditRegister.get("bfgy_shipmentperiod");//最晚装船日期
					Date xqtime = (Date) creditRegister.get("xqtime");//效期
					Date createtime = (Date) creditRegister.get("createtime");
					if (issuingtime!=null&&shipmentperiod!=null&&xqtime!=null) {
					if (shipmentperiod.getTime()<issuingtime.getTime()) {
						this.addErrorMessage(entities[i], "最晚装船日期不允许早于开证日期");
					}
					if (xqtime.getTime()<shipmentperiod.getTime()) {
						this.addErrorMessage(entities[i], "效期不允许早于最晚装船日期");
					}
					if (createtime.getTime()<issuingtime.getTime()) {
						this.addErrorMessage(entities[i], "开证日期不能晚于登记日期");
					}
					}
				}
			}
		}
	}
}
