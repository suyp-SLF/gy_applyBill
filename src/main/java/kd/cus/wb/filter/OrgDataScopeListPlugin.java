package kd.cus.wb.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.IFormView;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.bos.servicehelper.permission.PermissionServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;

public class OrgDataScopeListPlugin extends AbstractListPlugin {
	
	 private static Log logger = LogFactory.getLog(OrgDataScopeListPlugin.class);
	//查询当前登录用户的业务角色
		public List<Long> queryUserAssignbizrole(long userID)
		{			
			List<Long> UserAssignbizroles =new ArrayList();
			List<DynamicObject> project=QueryServiceHelper.query("perm_userbizrole", "bizrole,starttime,endtime",
					new QFilter("user.id",QCP.equals, userID).toArray());
			for(DynamicObject proj:project) {
				Long bizRole = proj.getLong("bizrole");	
				  UserAssignbizroles.add(bizRole);
			}			
			return UserAssignbizroles;
		}
		@Override
		public void setFilter(SetFilterEvent e) {
			super.setFilter(e);		
			// 父页面视图
			IFormView parentView = this.getView().getViewNoPlugin(this.getView().getFormShowParameter().getParentPageId());
			//查询当前单据标识	
			String formId =parentView.getEntityId();
			
			logger.info("组织数据隔离，表单："+formId);
			System.out.println("formid:" + formId);	
			
			QFilter filter = new QFilter("bfgy_bill.number", QCP.equals, formId);
			
			QFilter[] filters = { filter };			
			String orgFieldName = "org";
			DynamicObject bd_orgfield = BusinessDataServiceHelper.loadSingle("bfgy_config_bill",
					"bfgy_bill,bfgy_field",
					filters);
			UserServiceHelper helper = new UserServiceHelper();
			long currentUserId = helper.getCurrentUserId();
			/*if(null == bd_orgfield)
				return;*/
			//例外人员（查看全部）
			/*long uerid;
			DynamicObjectCollection entry = bd_orgfield.getDynamicObjectCollection("bfgy_entryentity_excp");
			for (DynamicObject entryrow : entry) {
				
				DynamicObject exp_per = (DynamicObject) entryrow.get("bfgy_exp_per");			
				if (null != exp_per)
				{
				  uerid=(long) exp_per.getPkValue();
				  if(currentUserId == uerid)
					 return;
				}
			}
			//例外通用角色（查看全部）
			Set roles= PermissionServiceHelper.getRolesByUser(currentUserId);
			DynamicObjectCollection entryroles = bd_orgfield.getDynamicObjectCollection("bfgy_entryentity");
			for (DynamicObject entryrow : entryroles) {
				
				DynamicObject exp_per = (DynamicObject) entryrow.get("bfgy_exception_role");			
				if (null != exp_per)
				{
					String permissionRoleId=(String) exp_per.getPkValue();
					  if(roles.contains(permissionRoleId)) {				    	
					      return;
					    }
				}
			}
			
			//例外业务角色（查看全部）
			List<Long>  Userbizroles = this.queryUserAssignbizrole(currentUserId);
			DynamicObjectCollection entryBizroles = bd_orgfield.getDynamicObjectCollection("bfgy_entryentity_exp_biz");
			for (DynamicObject entryrow : entryBizroles) {
				
				DynamicObject exp_per = (DynamicObject) entryrow.get("bfgy_exception_bizrole");			
				if (null != exp_per)
				{
					long permissionRoleId=(long) exp_per.getPkValue();
					  if(Userbizroles.contains(permissionRoleId)) {				    	
					      return;
					    }
				}
			}*/
			
			if (bd_orgfield != null) {
				orgFieldName = bd_orgfield.getString("bfgy_field");
			}			
			
			DynamicObject  company = null;
			// 非费报单据
			boolean isNotfbflag = true;
			if (parentView != null) {
				// 费报相关单据时
				String parentViewformid = parentView.getFormShowParameter().getFormId().toString();
				if ("er_publicreimbursebill".equals(parentViewformid)// 对公报销单
						|| "er_dailyapplybill".equals(parentViewformid)// 费用申请单
						|| "er_dailyreimbursebill".equals(parentViewformid)// 费用报销单
						|| "er_tripreqbill".equals(parentViewformid)// 出差申请单
						|| "er_dailyloanbill".equals(parentViewformid)// 借款单
						|| "er_repaymentbill".equals(parentViewformid)// 还款单
						|| "er_tripreimbursebill".equals(parentViewformid)// 差旅报销单
						) { 
					IDataModel parentModel = parentView.getModel();
					company = (DynamicObject) parentModel.getValue("company"); 
					isNotfbflag = false;
				}
			
			
				
				// 其他财务单据
				if (isNotfbflag) {
					IDataModel parentModel = parentView.getModel();
					try {
						  company = (DynamicObject) parentModel.getValue(orgFieldName); 
					} catch (Exception e1) {
						parentView = this.getView().getParentView().getParentView();
						if (parentView != null) {
							parentModel = parentView.getModel();						
							try {
								company = (DynamicObject) parentModel.getValue(orgFieldName);
							} catch (Exception e11) {
								return;
							}
						} else {
					    	return;
						}
					}
				}
			}		
			// 如果当前单据公司编码不为空，则继续进行处理
			if (company != null) {	
				
				String org_number = ((DynamicObject) company).getString("number");
				// 根据生效组织去查询币别组织设置
				QFilter filterPer = new QFilter("bfgy_org.number", QCP.equals, org_number);
				filter = filterPer.and(new QFilter("bfgy_billname.number", QCP.equals, formId));			
		
				QFilter[] filtersPer = { filterPer };			
				
				DynamicObject bd_Persions = BusinessDataServiceHelper.loadSingle("bfgy_orgscopes",
						"bfgy_billname,bfgy_org,bfgy_effect,bfgy_reverse,bfgy_orange.bfgy_orgrange,bfgy_orange.bfgy_lower,bfgy_orange.bfgy_enable",
						filtersPer);
				Set<Long> list = new HashSet<>();
				
				if (bd_Persions != null) {
					
					DynamicObjectCollection entry_settlementtype = bd_Persions.getDynamicObjectCollection("bfgy_orange");
					for (int i = 0; i < entry_settlementtype.size(); i++) {
						// 如果启用了币别，则进行过滤设置显示
						Boolean enable =  entry_settlementtype.get(i).getBoolean("bfgy_enable");
						if (enable) {
							DynamicObject loadSingle = (DynamicObject) entry_settlementtype.get(i).get("bfgy_orgrange");
							Boolean bfgyLower =  entry_settlementtype.get(i).getBoolean("bfgy_lower");
							Long orgid =loadSingle.getLong("ID");
							if(bfgyLower)
							{
								List<Long> subOrgIds = OrgUnitServiceHelper.getAllSubordinateOrgs(1L, java.util.Arrays.asList(new Long[] {orgid}), true);
								list.addAll(subOrgIds);
							}else {
								list.add(orgid);
							}							
						}
					}
		
					QFilter f7Filter = new QFilter("id", QCP.in, list);
					e.getQFilters().add(f7Filter);
				}
			}
		}
	}


