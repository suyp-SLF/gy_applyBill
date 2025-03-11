package kd.cus.wb.filter;

import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mvc.list.ListView;
import kd.bos.form.IFormView;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.orm.query.QFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.db.DBRoute;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.orm.query.QCP;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.permission.PermissionServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;


public class PersonObjectListPlugin extends AbstractListPlugin {
	 private static Log logger = LogFactory.getLog(PersonObjectListPlugin.class);
	@Override
	public void setFilter(SetFilterEvent e) {
		super.setFilter(e);
		UserServiceHelper helper = new UserServiceHelper();
		long userId = helper.getCurrentUserId();
		Set roles= PermissionServiceHelper.getRolesByUser(userId);
		// ��ҳ����ͼ
		IFormView parentView = this.getView().getViewNoPlugin(this.getView().getFormShowParameter().getParentPageId());
		//��ѯ��ǰ���ݱ�ʶ	
		String formId =parentView.getEntityId();	
			
		
		logger.info("��Ա��֯���ݸ��룬����"+formId);
		System.out.println("formid:" + formId);	
		DynamicObject  company = null;
		// �Ƿѱ�����
		boolean isNotfbflag = true;
		if (parentView != null) {
//			String SpageID = parentView.getPageId();
//			if (SpageID.equalsIgnoreCase("c60d8ee1c3d9444f9b08c1d94b126fe5"))
//				return;
			// �ѱ���ص���ʱ
			String parentViewformid = parentView.getFormShowParameter().getFormId().toString();
			if ("er_publicreimbursebill".equals(parentViewformid)// �Թ�������
					|| "er_dailyapplybill".equals(parentViewformid)// �������뵥
					|| "er_dailyreimbursebill".equals(parentViewformid)// ���ñ�����
					|| "er_tripreqbill".equals(parentViewformid)// �������뵥
					|| "er_dailyloanbill".equals(parentViewformid)// ��
					|| "er_repaymentbill".equals(parentViewformid)// ���
					|| "er_tripreimbursebill".equals(parentViewformid)// ���ñ�����
					) { 
				IDataModel parentModel = parentView.getModel();
				company = (DynamicObject) parentModel.getValue("company"); 
				isNotfbflag = false;
			}
			// ��ҳ��ĸ�ҳ����ͼ
		
			// �������񵥾�
			if (isNotfbflag) {
				IDataModel parentModel = parentView.getModel();
				try {
					  company = (DynamicObject) parentModel.getValue("org"); 
				} catch (Exception e1) {
					parentView = this.getView().getParentView().getParentView();
					if (parentView != null) {
						parentModel = parentView.getModel();
						// org = (DynamicObject) parentModel.getValue("org");
						try {
							company = (DynamicObject) parentModel.getValue("org");
						} catch (Exception e11) {
							return;
						}
					} else {
				    	return;
					}
				}
			}
		}		
		// �����ǰ���ݹ�˾���벻Ϊ�գ���������д���
		if (company != null) {	
			
			String org_number = ((DynamicObject) company).getString("number");
			// ������Ч��֯ȥ��ѯ�ұ���֯����
			QFilter filter = new QFilter("bfgy_active_org.number", QCP.equals, org_number);
			filter = filter.and(new QFilter("bfgy_basedata_object.number", QCP.equals, formId));
			//filter = filter.and(new QFilter("enable", QCP.equals, "1"));
	
			QFilter[] filters = { filter };			
			
			DynamicObject bd_Persions = BusinessDataServiceHelper.loadSingle("bfgy_persion_scopes",
					"bfgy_show_all,bfgy_entryentity.bfgy_orgfield,bfgy_entryentity.bfgy_item_enable",
					filters);			
			List<String> list = new ArrayList<String>();
			if (bd_Persions != null) {
				Boolean show_all = bd_Persions.getBoolean("bfgy_show_all");
				if(show_all)
					return;
				DynamicObjectCollection entry_settlementtype = bd_Persions.getDynamicObjectCollection("bfgy_entryentity");
				for (int i = 0; i < entry_settlementtype.size(); i++) {
					// ��������˱ұ�����й���������ʾ
					Boolean enable =  entry_settlementtype.get(i).getBoolean("bfgy_item_enable");
					if (enable) {
						DynamicObject loadSingle = (DynamicObject) entry_settlementtype.get(i).get("bfgy_orgfield");
						list.add(loadSingle.getString("number"));
					}
				}
	
				QFilter f7Filter = new QFilter("entryentity.dpt.number", QCP.in, list);
				e.getQFilters().add(f7Filter);
				
			}
		}
	}
}

