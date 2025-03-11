package kd.cus.wb.filter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.IFormView;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.org.OrgUnitServiceHelper;
import kd.bos.servicehelper.parameter.SystemParamServiceHelper;

/**
 * 数据隔离（组织）列表插件
 * @author suyp
 *
 */
public class DataScopeFilterListPlugin extends AbstractListPlugin {
	
	private static Map<String, Object> fields = new HashMap<>();
	@Override
	public void setFilter(SetFilterEvent e) {
//		fields = SystemParamServiceHelper.loadBillParameterObjectFromCache("bfgy_org_scopes");
		/*获得配置单据*/
		DynamicObject[] billconf = BusinessDataServiceHelper.load("bfgy_config_bill", "bfgy_bill,bfgy_field", null);
		/*组织权限列表*/
		DynamicObject[] entry = BusinessDataServiceHelper.load("bfgy_orgscopes", "bfgy_billname,bfgy_org,bfgy_effect,bfgy_reverse,bfgy_orange.bfgy_orgrange,bfgy_orange.bfgy_lower,bfgy_orange.bfgy_enable", null);
		/*将参数转为map，便于使用*/
		Map<String, String> billMap = Arrays.asList(billconf).stream().collect(Collectors.toMap(item -> getFormId(item), item -> item.getString("bfgy_field"),(oldVal, currVal) -> oldVal, LinkedHashMap::new));
		Map<String, DynamicObject> powerMap = Arrays.asList(entry).stream().filter(item->item.getBoolean("bfgy_effect")).collect(Collectors.toMap(item->getPowerKey(item), item->item,(oldVal, currVal) -> oldVal, LinkedHashMap::new));
		
		List<IFormView> formviews = getparent(this.getView(), new ArrayList());
		List<DynamicObject> powerdys = new ArrayList<>();
		for(IFormView formview : formviews) {
			//单据名
			String parentViewformid = formview.getFormShowParameter().getFormId().toString();//单据
			IDataModel parentModel = formview.getModel();
			//组织
			String orgfield = billMap.get(parentViewformid);
			if (null == orgfield) {
				continue;
			}
			DynamicObject company = (DynamicObject) parentModel.getValue(orgfield); //组织
			String orgid = company.getString("id");
			DynamicObject powerdy = powerMap.get(parentViewformid + "@_@" + orgid);
			if (powerdy == null) {
				this.getView().showErrorNotification("已配置单据未配置规则！");
				continue;
			}else {
				powerdys.add(powerdy);
			}
		}
		powerdys.forEach(powerdy->{
			Set<Long> orgeffectids = GetIdsScope(powerdy);
			QFilter f7Filter = null;
			if(powerdy.getBoolean("bfgy_reverse")) {
				f7Filter = new QFilter("id", QCP.not_in, orgeffectids);
			}else {
				f7Filter = new QFilter("id", QCP.in, orgeffectids);
			}
			e.getQFilters().add(f7Filter);
		});
	}
	
	/**
	 * 获得所有上级节点，直到pc_main_console
	 * @param view
	 * @param formviews
	 * @return
	 */
	private List<IFormView> getparent(IFormView view, List<IFormView> formviews) {
		 String formid = view.getFormShowParameter().getFormId().toString();
		 if(formid.equals("pc_main_console") || formid.equals("pc_devportal_main")) {
			 return formviews;
		 }else {
			 formviews.add(view);
			 return getparent(view.getParentView(),formviews);
		 }
	}
	
	/**
	 * 获得权力列表
	 * @param item
	 * @return
	 */
	private String getPowerKey(DynamicObject item) {
		DynamicObject billdy = item.getDynamicObject("bfgy_billname");
		DynamicObject orgdy = item.getDynamicObject("bfgy_org");
		return billdy.getString("number") + "@_@" + orgdy.getString("id");
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 */
	private String getFormId(DynamicObject item) {
		String numer = "";
		DynamicObject bill = item.getDynamicObject("bfgy_bill");
		if (bill != null && bill.getString("number") != null) {
			numer = bill.getString("number");
		}
		return bill.getString("number");
	}
	
	/**
	 * 获得组织范围
	 * @param item
	 * @return
	 */
	private Set<Long> GetIdsScope(DynamicObject item) {
		DynamicObjectCollection orgdys = item.getDynamicObjectCollection("bfgy_orange");
		Set<Long> ids = new HashSet<>();
		for(DynamicObject m : orgdys){
			DynamicObject org = m.getDynamicObject("bfgy_orgrange");
			Boolean contain = m.getBoolean("bfgy_lower");
			Boolean effect = m.getBoolean("bfgy_enable");
			
			if (effect) {
				if (contain) {
					List<Long> subOrgIds = OrgUnitServiceHelper.getAllSubordinateOrgs(1L, java.util.Arrays.asList(new Long[] {org.getLong("id")}), true);
					ids.addAll(subOrgIds);
				}else {
					ids.add(org.getLong("id"));
				}
			}
		}
		return ids;
	}
}
