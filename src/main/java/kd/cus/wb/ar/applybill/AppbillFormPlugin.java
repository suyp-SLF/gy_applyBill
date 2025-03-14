package kd.cus.wb.ar.applybill;

import java.math.BigDecimal;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.AfterAddRowEventArgs;
import kd.bos.entity.datamodel.events.ChangeData;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.BeforeItemClickEvent;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.field.TextEdit;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.org.OrgServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.fi.cas.helper.OrgHelper;
import kd.taxc.tcvat.formplugin.account.AbstractAccountingPlugin;

/**
 * 开票申请单|万宝表单插件，删行之后重新计算，（单据类型进行默认填入）
 * @author suyp
 *
 */
public class AppbillFormPlugin extends AbstractFormPlugin {

	private static final String ORG_LOGO = "bos_org";
	private static final String BOS_BILLTYPE = "bos_billtype";
	private static final String ENTRY_NAME = "bfgy_entryentity";
	
	private String taxNow = null;
	
	private static final Map valueMap = new HashMap() {{
		put("sale", "V13");
		put("A","V13");
		put("B","V13");
		put("C","V9");
		put("D","V9");
		put("E","V9");
		put("F","V9");
		put("G","V6");
		put("H","V6");
		put("I","V6");
		put("J","V6");
		put("K","V6");
		put("L","V5");
	}};

//	@Override
//	public void propertyChanged(PropertyChangedArgs e) {
//		// TODO Auto-generated method stub
//		super.propertyChanged(e);
//		
//		Object a = this.getModel().getValue("bfgy_billtype");
//		
//		DynamicObject org = (DynamicObject) this.getModel().getValue("bfgy_departmentname");
//		if (null != org) {
//			org = getOrgInfo(org.get("id"));
//		}
//		
//		if (null != org) {
//			String bfgy_taxno = org.getString("ftaxregnum");
//			String bfgy_phone = org.getString("phone");
//			String bfgy_bankaccount = org.getString("bankaccount") + "(" + org.getString("depositbank")
//					+ ")";
//			String bfgy_address = org.getString("contactaddress");
//			IDataModel b = this.getModel();
//			this.getModel().setValue("bfgy_taxno", bfgy_taxno);
//			this.getModel().setValue("bfgy_phone", bfgy_phone);
//			this.getModel().setValue("bfgy_bankaccount", bfgy_bankaccount);
//			this.getModel().setValue("bfgy_address", bfgy_address);
//		}
//	}
//
//	//
//	@Override
//	public void afterBindData(EventObject e) {
//		// TODO Auto-generated method stub
//		super.afterBindData(e);
//		DynamicObject org = (DynamicObject) this.getModel().getValue("bfgy_departmentname");
//		if (null != org) {
//			org = getOrgInfo(org.getString("id"));
//		}
//		
//		if (null != org) {
//			String bfgy_taxno = org.getString("ftaxregnum");
//			String bfgy_phone = org.getString("phone");
//			String bfgy_bankaccount = org.getString("bankaccount") + "(" + org.getString("depositbank")
//					+ ")";
//			String bfgy_address = org.getString("contactaddress");
//			IDataModel a = this.getModel();
//			this.getModel().setValue("bfgy_taxno", bfgy_taxno);
//			this.getModel().setValue("bfgy_phone", bfgy_phone);
//			this.getModel().setValue("bfgy_bankaccount", bfgy_bankaccount);
//			this.getModel().setValue("bfgy_address", bfgy_address);
//		}
//	}


	@Override
	public void afterBindData(EventObject e) {
		super.afterBindData(e);
	}

	/**
	 * 单据填入默认单据类型
	 * @param e
	 */
	@Override
	public void afterCreateNewData(EventObject e) {
		// TODO Auto-generated method stub
//		super.afterCreateNewData(e);
//		QFilter[] qFilters = {new QFilter("number", QCP.equals, "bfgy_applybill_inh_BT")};
//		DynamicObject dy = BusinessDataServiceHelper.loadSingleFromCache(BOS_BILLTYPE, qFilters);
//		this.getModel().setValue("bfgy_billtype", dy);
		
		DynamicObject org= (DynamicObject)this.getModel().getValue("org");
		Map<String, Object> companyfromOrg = OrgUnitServiceHelper.getCompanyfromOrg(org.getPkValue());
		if (!companyfromOrg.isEmpty()) {			
			String orgNumber = (String) companyfromOrg.get("number");
	        if("8018".equalsIgnoreCase(orgNumber))
	        {
				QFilter[] qFilters1 = {new QFilter("number",QCP.equals,"8018")};
				DynamicObject org_dy = BusinessDataServiceHelper.loadSingle("bos_org", "depositbank,bankaccount", qFilters1);
		
				this.getModel().setValue("bfgy_departmentname", org_dy);
				this.getModel().setValue("bfgy_bankaccount", org_dy.getString("depositbank") + " " + org_dy.getString("bankaccount"));
	            
				taxNow = (String)valueMap.get(this.getModel().getValue("bfgy_invoicesort"));
				this.getView().getPageCache().put("taxNow", taxNow);
	        }
		}
		
	}

//	private DynamicObject getOrgInfo(Object id) {
//		if (null != id) {
//			QFilter[] qFilters = { new QFilter("id", QCP.equals, id) };
//			try {
//				DynamicObject org_dy = BusinessDataServiceHelper.loadSingle(ORG_LOGO,
//						"id,ftaxregnum,phone,depositbank,bankaccount,contactaddress", qFilters);
//				return org_dy;
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//
//			}
//		}
//		return null;
//	}

	/**
	 * 删行进行计算
	 * @param e
	 */

	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		// TODO Auto-generated method stub
		super.propertyChanged(e);
		DynamicObject org= (DynamicObject)this.getModel().getValue("org");
		Map<String, Object> companyfromOrg = OrgUnitServiceHelper.getCompanyfromOrg(org.getPkValue());
		if (!companyfromOrg.isEmpty()) {			
			String orgNumber = (String) companyfromOrg.get("number");
	        if("8018".equalsIgnoreCase(orgNumber))
	        {
				ChangeData[] a = e.getChangeSet();
				IDataEntityProperty b = e.getProperty();
				if ("bfgy_leviedtotal".equals(b.getName())) {
					BigDecimal sumAmount = new BigDecimal(0);
					DynamicObjectCollection entry = this.getModel().getEntryEntity(ENTRY_NAME);
					for (DynamicObject dy : entry) {
						if (null != dy) {
							sumAmount = sumAmount.add(dy.getBigDecimal("bfgy_leviedtotal"));
						}
					}
					this.getModel().setValue("bfgy_taxmoneysum_pri", sumAmount);
				}
				if("bfgy_bcusname".equalsIgnoreCase(b.getName())){
					DynamicObject custom = (DynamicObject) this.getModel().getValue("bfgy_bcusname");
					DynamicObjectCollection bankcols = custom.getDynamicObjectCollection("entry_bank");
					if (bankcols.size() > 1 && bankcols.get(0).getDynamicObject("bank") != null){
						DynamicObject bank = bankcols.get(0).getDynamicObject("bank");
						System.out.println(1);
					}else {
						this.getView().setEnable(true,"bfgy_bbankaccount");
					}
				}
				
				if("bfgy_billtype".equalsIgnoreCase(b.getName()) && "bfgy_applybill_inh_BT".equalsIgnoreCase((String) this.getModel().getValue("bfgy_billtype.number"))) {
					 org = BusinessDataServiceHelper.loadSingle("bos_org","id,contactaddress,depositbank,bankaccount",new QFilter[] {new QFilter("number", QCP.equals, "8018")});
					this.getModel().setValue("bfgy_departmentname", org);
					
					this.getModel().setValue("bfgy_address", org.getString("contactaddress"));
					this.getModel().setValue("bfgy_bankaccount", org.getString("depositbank") + " " + org.getString("bankaccount"));
				}
				if("bfgy_rate".equals(b.getName())) {
					if(a.length > 0 && a[0].getNewValue() != null) {
						taxNow = ((DynamicObject)a[0].getNewValue()).getString("number");
						this.getView().getPageCache().put("taxNow", taxNow);
					}
					
					DynamicObjectCollection dys_cols = this.getModel().getEntryEntity("bfgy_entryentity");
					taxNow = this.getView().getPageCache().get("taxNow");
					for(int i = 0; i < dys_cols.size(); i++) {
						DynamicObject taxdy = BusinessDataServiceHelper.loadSingle("bd_taxrate", "id", new QFilter[] {new QFilter("number", QCP.equals, taxNow)});
						this.getModel().setValue("bfgy_rate",taxdy,i);
					}
				}
				if("bfgy_invoicesort".equals(b.getName()) && a.length > 0) {
					taxNow = (String)valueMap.get((String)a[0].getNewValue());
					this.getView().getPageCache().put("taxNow", taxNow);
					DynamicObjectCollection dys_cols = this.getModel().getEntryEntity("bfgy_entryentity");
					taxNow = this.getView().getPageCache().get("taxNow");
					for(int i = 0; i < dys_cols.size(); i++) {
						DynamicObject taxdy = BusinessDataServiceHelper.loadSingle("bd_taxrate", "id", new QFilter[] {new QFilter("number", QCP.equals, taxNow)});
						this.getModel().setValue("bfgy_rate",taxdy,i);
					}
					
					if("E".equals((String)this.getModel().getValue("bfgy_invoicesort")) || "L".equals((String)this.getModel().getValue("bfgy_invoicesort"))) {
						TextEdit remark = this.getView().getControl("bfgy_remark");
						remark.setMustInput(true);
					}else {
						TextEdit remark = this.getView().getControl("bfgy_remark");
						remark.setMustInput(false);
					}
				}
	        }
		}
	        
	}

	/**
	 * 监听插件
	 * @param e
	 */
	@Override
	public void registerListener(EventObject e) {
		// TODO Auto-generated method stub
		super.registerListener(e);
		Toolbar repairDataBtnBar = this.getControl("bfgy_advcontoolbarap");
		repairDataBtnBar.addItemClickListener(this);
	}

	@Override
	public void afterAddRow(AfterAddRowEventArgs e) {
		// TODO Auto-generated method stub
		super.afterAddRow(e);
		DynamicObject org= (DynamicObject)this.getModel().getValue("org");
		Map<String, Object> companyfromOrg = OrgUnitServiceHelper.getCompanyfromOrg(org.getPkValue());
		if (!companyfromOrg.isEmpty()) {			
			String orgNumber = (String) companyfromOrg.get("number");
	        if("8018".equalsIgnoreCase(orgNumber))
	        {
				DynamicObject taxdy = BusinessDataServiceHelper.loadSingle("bd_taxrate", "id", new QFilter[] {new QFilter("number", QCP.equals, taxNow)});
				DynamicObjectCollection dys_cols = this.getModel().getEntryEntity("bfgy_entryentity");
				for(int i = 0; i < dys_cols.size(); i++) {
					this.getModel().setValue("bfgy_rate",taxdy,i);
				}
	        }
		}
	}
	
	/**
	 * 点击事件
	 * @param evt
	 */
	@Override
	public void itemClick(ItemClickEvent evt) {
		// TODO Auto-generated method stub
		super.itemClick(evt);
		DynamicObject org= (DynamicObject)this.getModel().getValue("org");
		Map<String, Object> companyfromOrg = OrgUnitServiceHelper.getCompanyfromOrg(org.getPkValue());
		if (!companyfromOrg.isEmpty()) {			
			String orgNumber = (String) companyfromOrg.get("number");
	        if("8018".equalsIgnoreCase(orgNumber))
	        {
				if ("deleteentry".equals(evt.getOperationKey())) {
					BigDecimal sumAmount = new BigDecimal(0);
					DynamicObjectCollection entry = this.getModel().getEntryEntity(ENTRY_NAME);
					for (DynamicObject dy : entry) {
						if (null != dy) {
							sumAmount = sumAmount.add(dy.getBigDecimal("bfgy_leviedtotal"));
						}
					}
					this.getModel().setValue("bfgy_taxmoneysum_pri", sumAmount);
				}
				if("bfgy_advconbaritemap".equalsIgnoreCase(evt.getItemKey())) {
		//			taxNow = this.getView().getPageCache().get("taxNow");
		//			DynamicObject taxdy = BusinessDataServiceHelper.loadSingle("bd_taxrate", "id", new QFilter[] {new QFilter("number", QCP.equals, taxNow)});
		//			this.getModel().createNewEntryRow("bfgy_entryentity");
		//			this.getModel().setValue("bfgy_rate",taxdy);
					
		//			DynamicObjectCollection dys_cols = this.getModel().getEntryEntity("bfgy_entryentity");
		//			for(int i = 0; i < dys_cols.size(); i++) {
		//				this.getModel().setValue("bfgy_rate",taxdy,i);
		//			}
				}
			}
			}
		}
}
