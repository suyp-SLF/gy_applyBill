package kd.cus.wb.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kd.fi.cas.helper.OrgHelper;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.form.IFormView;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.mvc.list.ListView;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.bos.servicehelper.org.OrgViewType;

public class ProjectOrgScopeListPlugin extends AbstractListPlugin {
	@Override
	public void setFilter(SetFilterEvent e) {
		super.setFilter(e);	
		//查询当前单据标识	
		//ListView view = (ListView)this.getView();
		//String formId = view.getBillFormId();
		
		/*IFormView parentView = this.getView().getViewNoPlugin(this.getView().getFormShowParameter().getParentPageId());
		
		String formId =parentView.getEntityId();	
		
		if("bfgy_projectorgscopeset".equalsIgnoreCase(formId))//设置界面，退出，不受控制
		   return;*/
		
		long org = RequestContext.get().getOrgId();
		Map<String, Object> companyfromOrg = OrgUnitServiceHelper.getCompanyfromOrg(org);
		if (!companyfromOrg.isEmpty()) {
			Long companyId = (Long) companyfromOrg.get("id");
			String companyNumber = (String)companyfromOrg.get("number");
			QFilter filter = new QFilter("createorg.id", QCP.equals, companyId);
			 List<Long> supOrg = OrgUnitServiceHelper.getSuperiorOrgs(OrgViewType.OrgUnit,
	                    Long.parseLong(companyId.toString()));
	            if (supOrg != null && supOrg.size() > 0 && supOrg.get(0) != 0) {
	            	filter.or( new QFilter("createorg.id", QCP.equals, supOrg.get(0)));
	            }
		
		/* List<String> unincludeList = new ArrayList<String>();
		 List<String> includeList = new ArrayList<String>();;
		 
		Map<String, Object> companyfromOrg = OrgUnitServiceHelper.getCompanyfromOrg(org);
		if (!companyfromOrg.isEmpty()) {
			Long companyId = (Long) companyfromOrg.get("id");
			String companyNumber = (String)companyfromOrg.get("number");
			DynamicObject[] permissionlist = BusinessDataServiceHelper.load("bfgy_projectorgscopeset",
					"bfgy_entry_include,bfgy_entry_include.bfgy_include_project,bfgy_entry_include.bfgy_enable_include,bfgy_entry_uninclude,bfgy_entry_uninclude.bfgy_uninclude_project,bfgy_entry_uninclude.bfgy_enable_uninclude",
					new QFilter[] { new QFilter("bfgy_org.number", QCP.equals, companyNumber)});	
			
			if (null != permissionlist) {
				for(DynamicObject permissions:permissionlist) {
					DynamicObjectCollection entryuninclude = permissions.getDynamicObjectCollection("bfgy_entry_uninclude");
					for (DynamicObject entryrow : entryuninclude) {
						DynamicObject  include_project = (DynamicObject) entryrow.getDynamicObject("bfgy_uninclude_project");
						 if(entryrow.getBoolean("bfgy_enable_uninclude"))
						 {
							 String includeID  = include_project.getPkValue().toString();
							 includeList.add(includeID);
						 }
					}
				
				
				DynamicObjectCollection entryinclude = permissions.getDynamicObjectCollection("bfgy_entry_include");
				for (DynamicObject entryrow : entryinclude) {
					DynamicObject  uninclude_project = (DynamicObject) entryrow.getDynamicObject("bfgy_include_project");
					 if(entryrow.getBoolean("bfgy_enable_include"))
					 {
						 String unincludeID  = uninclude_project.getPkValue().toString();
						 unincludeList.add(unincludeID);
					 }
				}
				
			}
			}
			 QFilter filter;
		    QFilter createOrgfilter = new QFilter("createorg.id", QCP.equals, companyId);
		    
		    if (unincludeList.size() >0)
		    {   
		    	QFilter uninfilter= new QFilter("id", QCP.not_in, unincludeList);
		    	createOrgfilter = new QFilter("1", "=", "12").or(createOrgfilter, uninfilter);
		    
		    }
		    
		    if(includeList.size()>0)
		    {
		    	QFilter infilter= new QFilter("id", QCP.in, includeList);
		    	 filter = new QFilter("1", "=", "12").or(createOrgfilter, infilter);
		    }
		    else
		    	filter =createOrgfilter;		   
		   
	    	 */
		    e.getQFilters().add(filter);
		    System.out.println("all QFilterS:" +e.getQFilters().toString());
		}
	}

}
