package kd.cus.wb.ar.applybill;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;


import cfca.sadk.org.bouncycastle.util.Arrays;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.plugin.args.BeforeOperationArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.tmc.psd.common.enums.SelectSettleTypeEnum;

/**
 *  开票申请单|万宝表单插件，判断提交时没有校验：金额信息中的【价税合计】=开票信息子表【价税合计】汇总。
 * @author suyp
 *
 */
public class AppbillOppoPlugin extends AbstractOperationServicePlugIn {
	private static final String BILL_LOGO = "bfgy_applybill";
	private static final String ENTRY_NAME = "bfgy_entryentity";

	/**
	 * 操作前进行校验
	 */
	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		super.onAddValidators(e);
		WriteMeetingOpinionValidator opinionVal = new WriteMeetingOpinionValidator();
		e.addValidator(opinionVal);
	}

	public class WriteMeetingOpinionValidator extends AbstractValidator {

		@Override
		public void validate() {
			ExtendedDataEntity[] data = this.getDataEntities();
			for (ExtendedDataEntity dy_item : data) {
				DynamicObject this_dy = dy_item.getDataEntity();
				OperationServiceHelper.executeOperate("save",BILL_LOGO,new DynamicObject[] {this_dy}, OperateOption.create());
				QFilter[] qFilters = {new QFilter("billno",QCP.equals,this_dy.getString("billno"))};
				DynamicObject dy_item_search = BusinessDataServiceHelper.loadSingle(BILL_LOGO,"billno,bfgy_leviedtotal,bfgy_taxmoneysum_pri",qFilters);
				DynamicObjectCollection cols  = dy_item_search.getDynamicObjectCollection(ENTRY_NAME);
				BigDecimal sumAmount = new BigDecimal(0);
				for (DynamicObject dys_entry : cols) {
					sumAmount = sumAmount.add(dys_entry.getBigDecimal("bfgy_leviedtotal"));
				}
				BigDecimal sumAmount_p = dy_item_search.getBigDecimal("bfgy_taxmoneysum_pri");
				if (0 != sumAmount_p.compareTo(sumAmount)) {
					this.addErrorMessage(dy_item, "金额信息中的【价税合计】不等于 开票信息子表【价税合计】汇总");
				}
			}
		}
	}
}
