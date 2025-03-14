package kd.cus.wb.credit.billplugin;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.entity.datamodel.RowDataEntity;
import kd.bos.entity.datamodel.events.AfterAddRowEventArgs;
import kd.bos.entity.datamodel.events.AfterDeleteRowEventArgs;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.property.EntryProp;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.EntryData;
import kd.bos.form.events.BeforeDoOperationEventArgs;
import kd.bos.form.operate.FormOperate;
import kd.bos.form.plugin.AbstractFormPlugin;

import java.math.BigDecimal;

public class WBHqCreditRegisterExpFormPlugin extends AbstractFormPlugin {

	@Override
	public void afterAddRow(AfterAddRowEventArgs e) {
		// TODO Auto-generated method stub
		super.afterAddRow(e);
		EntryProp entryProp = e.getEntryProp();
		String name = entryProp.getName();
		if ("bfgy_subentryentity".equals(name)) {
			int index = this.getModel().getEntryCurrentRowIndex("bfgy_entryentity");
			String bfgy_contract_text = (String) this.getModel().getValue("bfgy_contract_text", index);
			RowDataEntity[] entities = e.getRowDataEntities();
			int rowIndex = entities[0].getRowIndex();
			this.getModel().setValue("bfgy_contractno", bfgy_contract_text, rowIndex);
		}

	}

	@Override
	public void afterDeleteRow(AfterDeleteRowEventArgs e) {
		// TODO Auto-generated method stub
		super.afterDeleteRow(e);
		EntryProp entryProp = e.getEntryProp();
		String name = entryProp.getName();
		if ("bfgy_subentryentity".equals(name)) {
			EntryGrid bfgy_subentryentity = this.getControl("bfgy_subentryentity");
			BigDecimal sum = bfgy_subentryentity.getSum("bfgy_jdamount");
			int index = this.getModel().getEntryCurrentRowIndex("bfgy_entryentity");
			this.getModel().setValue("bfgy_bpamount", sum, index);
		}
	}

	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		// TODO Auto-generated method stub
		super.propertyChanged(e);
		String name = e.getProperty().getName();
		if ("creditamount".equals(name)) {
			//this.getModel().setValue("balance", this.getModel().getValue("creditamount"));
			BigDecimal creditamount = (BigDecimal) this.getModel().getValue("creditamount");
			EntryGrid control = this.getView().getControl("bfgy_subentryentity");
			BigDecimal sum =BigDecimal.ZERO;			
			
			EntryData data = control.getEntryData();
			int EndIndex =data.getEndIndex();
			if(EndIndex >0)			
			  sum = control.getSum("bfgy_jdamount");
			
			BigDecimal subtract = creditamount.subtract(sum);			
			this.getModel().setValue("balance", subtract);
		}
		if ("bfgy_jdamount".equals(name)) {
			EntryGrid bfgy_subentryentity = this.getControl("bfgy_subentryentity");
			BigDecimal sum = bfgy_subentryentity.getSum("bfgy_jdamount");
			int index = this.getModel().getEntryCurrentRowIndex("bfgy_entryentity");
			this.getModel().setValue("bfgy_bpamount", sum, index);
		}
		if ("bfgy_bpamount".equals(name)) {
			EntryGrid bfgy_subentryentity = this.getControl("bfgy_entryentity");
			BigDecimal sum = bfgy_subentryentity.getSum("bfgy_bpamount");
			this.getModel().setValue("bfgy_bptotal", sum);
		}
		if ("bfgy_bptotal".equals(name)) {
			BigDecimal bfgy_bptotal = (BigDecimal) this.getModel().getValue("bfgy_bptotal");
			BigDecimal creditamount = (BigDecimal) this.getModel().getValue("creditamount");
			if (bfgy_bptotal.compareTo(creditamount) == 1) {
				this.getView().showTipNotification("交单总金额不能大于信用证金额！");
				int index = this.getModel().getEntryCurrentRowIndex("bfgy_subentryentity");
				this.getModel().setValue("bfgy_jdamount", null, index);
			} else {
				BigDecimal subtract = creditamount.subtract(bfgy_bptotal);
				this.getModel().setValue("balance", subtract);
			}
		}
	}

	@Override
	public void beforeDoOperation(BeforeDoOperationEventArgs args) {
		// TODO Auto-generated method stub
		super.beforeDoOperation(args);
		FormOperate source = (FormOperate) args.getSource();
		String operateKey = source.getOperateKey();
		if ("closesubmit".equals(operateKey)) {
			DynamicObjectCollection entryEntity = this.getModel().getEntryEntity("entryentityjfys");
			BigDecimal totalJdamount = new BigDecimal(0);
			for (DynamicObject entry : entryEntity) {
				BigDecimal jdamount = entry.getBigDecimal("jdamount");
				totalJdamount = totalJdamount.add(jdamount);
			}
			BigDecimal creditamount = (BigDecimal) this.getModel().getValue("creditamount");
			EntryGrid control = this.getView().getControl("entryentityjfys");
			BigDecimal sum = control.getSum("jdamount");
			BigDecimal subtract = creditamount.subtract(sum);
//			if (subtract.compareTo(creditamount) == 0) {
//				this.getView().showErrorNotification("ÐÅÓÃÖ¤Óà¶î²»×ã£¡");
//				args.setCancel(true);
//			} else {
				this.getModel().setValue("balance", subtract);
				this.getView().invokeOperation("save");
//			}
		}
	}
}
