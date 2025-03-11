package kd.cus.wb.botp;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.dataentity.metadata.IDataEntityProperty;
import kd.bos.dataentity.metadata.dynamicobject.DynamicMetadata;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.metadata.dynamicobject.DynamicProperty;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.EntityType;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.property.BasedataProp;
import kd.bos.entity.property.UserProp;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.field.ComboEdit;
import kd.bos.form.field.ComboItem;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.MetadataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.isc.iscb.util.script.parser.Static;

public class SuperBotpFormPlugin extends AbstractFormPlugin {

	public void registerListener(EventObject e) {
		Toolbar repairDataBtnBar = (Toolbar) this.getControl("tbmain");
		repairDataBtnBar.addItemClickListener(this);
	}

	@Override
	public void itemClick(ItemClickEvent evt) {
		// TODO Auto-generated method stub
		super.itemClick(evt);
		String name = evt.getItemKey();
		if("bfgy_pool".equals(name)) {
			getlinks();
		}
	}

	@Override
	public void afterBindData(EventObject e) {
		// TODO Auto-generated method stub
		super.afterBindData(e);
		Object source = e.getSource();
		changeRelation("source");
		changeRelation("target");
		
		changeEntryRelation("source");
		changeEntryRelation("target");
		
		changeEntryFieldRelation("source");
		changeEntryFieldRelation("target");
	}

	@Override
	public void propertyChanged(PropertyChangedArgs e) {
		// TODO Auto-generated method stub
		super.propertyChanged(e);
		IDataEntityProperty property = e.getProperty();
		String name = property.getName();
		if ("bfgy_sourcetype".equalsIgnoreCase(name)) {
			clearCombo("bfgy_sourceentry");
			clearCombo("bfgy_sourcefield");
			clearCombo("bfgy_sourcebasefield");
			changeRelation("source");
		} else if ("bfgy_targettype".equalsIgnoreCase(name)) {
			clearCombo("bfgy_targetentry");
			clearCombo("bfgy_targetfield");
			clearCombo("bfgy_targetbasefield");
			changeRelation("target");
		} else if ("bfgy_sourcefield".equalsIgnoreCase(name)) {
			changeEntryRelation("source");
		} else if ("bfgy_targetfield".equalsIgnoreCase(name)) {
			changeEntryRelation("target");
		} else if ("bfgy_sourceentry".equalsIgnoreCase(name)) {
			changeEntryFieldRelation("source");
		} else if ("bfgy_targetentry".equalsIgnoreCase(name)) {
			changeEntryFieldRelation("target");
		}
	}

	private void changeEntryFieldRelation(String from) {
		// TODO Auto-generated method stub
		String sourceentry = (String) this.getModel().getValue("bfgy_sourceentry");
		String targetentry = (String) this.getModel().getValue("bfgy_targetentry");

		DynamicObject workflow = (DynamicObject) this.getModel().getValue("bfgy_botp_workflow");

		if (null == workflow) {
			this.getView().showErrorNotification("请选择业务流！");
			return;
		}

		String sourceentitynumbe = workflow.getString("sourceentitynumber.number");
		String targetentitynumber = workflow.getString("targetentitynumber.number");

		if (StringUtils.isNotBlank(sourceentry) && "source".equalsIgnoreCase(from)) {
			MainEntityType confType = MetadataServiceHelper.getDataEntityType(sourceentitynumbe);
			DynamicObject sourceDy = new DynamicObject(confType);

			ComboEdit sourcefield = this.getControl("bfgy_sourcefield");
			sourcefield.setComboItems(getEntryFieldsName(sourceDy.getDynamicObjectCollection(sourceentry)));
		} else if (StringUtils.isNotBlank(targetentry) && "target".equalsIgnoreCase(from)) {
			MainEntityType confType = MetadataServiceHelper.getDataEntityType(targetentitynumber);
			DynamicObject targetDy = new DynamicObject(confType);

			ComboEdit targetfield = this.getControl("bfgy_targetfield");
			targetfield.setComboItems(getEntryFieldsName(targetDy.getDynamicObjectCollection(targetentry)));
		}
	}

	private void changeEntryRelation(String from) {
		// TODO Auto-generated method stub
		String sourcefield = (String) this.getModel().getValue("bfgy_sourcefield");
		String sourcetype = (String) this.getModel().getValue("bfgy_sourcetype");
		String targetfield = (String) this.getModel().getValue("bfgy_targetfield");
		String targettype = (String) this.getModel().getValue("bfgy_targettype");

		DynamicObject workflow = (DynamicObject) this.getModel().getValue("bfgy_botp_workflow");

		if (null == workflow) {
			this.getView().showErrorNotification("请选择业务流！");
			return;
		}

		String sourceentitynumber = workflow.getString("sourceentitynumber.number");
		String targetentitynumber = workflow.getString("targetentitynumber.number");

		if (StringUtils.isNotBlank(sourcefield) && StringUtils.isNotBlank(sourcetype) && "source".equalsIgnoreCase(from)) {
			MainEntityType confType = MetadataServiceHelper.getDataEntityType(sourceentitynumber);
			if(confType.getProperty(sourcefield) instanceof BasedataProp) {
				BasedataProp baseprop = (BasedataProp) confType.getProperty(sourcefield);
				if (("A".equalsIgnoreCase(sourcetype) || "C".equalsIgnoreCase(sourcetype)) && null != baseprop) {
					this.getView().showErrorNotification(sourcefield + "是基础资料！,但是您未选择基础资料类型");
					return;
				} else if (("B".equalsIgnoreCase(sourcetype) || "D".equalsIgnoreCase(sourcetype)) && null == baseprop) {
					this.getView().showErrorNotification(sourcefield + "不是基础资料！,但是您选择了基础资料类型");
					return;
				}
	
				String basename = baseprop.getBaseEntityId();
	
				ComboEdit sourcebasefield = this.getControl("bfgy_sourcebasefield");
				sourcebasefield.setComboItems(getUniteFields(basename));
			}
		} else if (StringUtils.isNotBlank(targetfield) && StringUtils.isNotBlank(targettype) && "target".equalsIgnoreCase(from)) {
			MainEntityType confType = MetadataServiceHelper.getDataEntityType(targetentitynumber);
			if(confType.getProperty(targetfield) instanceof BasedataProp) {
				BasedataProp baseprop = (BasedataProp) confType.getProperty(targetfield);
				if (("A".equalsIgnoreCase(targettype) || "C".equalsIgnoreCase(targettype)) && null != baseprop) {
					this.getView().showErrorNotification(targetfield + "是基础资料！,但是您未选择基础资料类型");
					return;
				} else if (("B".equalsIgnoreCase(targettype) || "D".equalsIgnoreCase(targettype)) && null == baseprop) {
					this.getView().showErrorNotification(targetfield + "不是基础资料！,但是您选择了基础资料类型");
					return;
				}
	
				String basename = baseprop.getBaseEntityId();
	
				ComboEdit targetbasefield = this.getControl("bfgy_targetbasefield");
				targetbasefield.setComboItems(getUniteFields(basename));
			}
		}
	}

	private void changeRelation(String from) {
		String sourcetype = (String) this.getModel().getValue("bfgy_sourcetype");
		String targettype = (String) this.getModel().getValue("bfgy_targettype");

		DynamicObject workflow = (DynamicObject) this.getModel().getValue("bfgy_botp_workflow");

		if (null == workflow) {
			this.getView().showErrorNotification("请选择业务流！");
			return;
		}

		String sourceentitynumber = workflow.getString("sourceentitynumber.number");
		String targetentitynumber = workflow.getString("targetentitynumber.number");

		if (StringUtils.isNotBlank(sourcetype) && "source".equalsIgnoreCase(from)) {
			if (StringUtils.isBlank(sourcetype)) {
				this.getView().showErrorNotification("源单关联位置不能为空");
			} else {
				if ("A".equalsIgnoreCase(sourcetype) || "B".equalsIgnoreCase(sourcetype)) {
					// 更新字段
					ComboEdit sourcefield = this.getControl("bfgy_sourcefield");
					sourcefield.setComboItems(getUniteFields(sourceentitynumber));
				} else if ("C".equalsIgnoreCase(sourcetype) || "D".equalsIgnoreCase(sourcetype)) {
					// 更新单据体
					ComboEdit sourceentry = this.getControl("bfgy_sourceentry");
					sourceentry.setComboItems(getEntitiesNames(sourceentitynumber));
				}
			}
		} else if (StringUtils.isNotBlank(targettype) && "target".equalsIgnoreCase(from)) {
			if (StringUtils.isBlank(targettype)) {
				this.getView().showErrorNotification("目标单关联位置不能为空");
			} else {
				if ("A".equalsIgnoreCase(targettype) || "B".equalsIgnoreCase(targettype)) {
					// 更新字段
					ComboEdit targetfield = this.getControl("bfgy_targetfield");
					targetfield.setComboItems(getUniteFields(targetentitynumber));
				} else if ("C".equalsIgnoreCase(targettype) || "D".equalsIgnoreCase(targettype)) {
					// 更新单据体
					ComboEdit targetentry = this.getControl("bfgy_targetentry");
					targetentry.setComboItems(getEntitiesNames(targetentitynumber));
				}
			}
		}
	}

	/**
	 * 获取单据的行名称
	 * 
	 * @return
	 */
	private List<ComboItem> getUniteFields(String logoName) {
		List<ComboItem> comboItems = new ArrayList<ComboItem>();

		MainEntityType confType = MetadataServiceHelper.getDataEntityType(logoName);
//		confType.getFields().entrySet().stream().map(Map.Entry::getKey).forEach(UniteField->{
//			LocaleString localeString = new LocaleString(UniteField);
//			comboItems.add(new ComboItem(localeString, UniteField));
//		});
		confType.getProperties().stream().forEach(field -> {
			LocaleString localeString = new LocaleString(field.getName() + "(" + field.getDisplayName() + ")");
			comboItems.add(new ComboItem(localeString, field.getName()));
		});
		return comboItems;
	}

	/**
	 * 获取单据的所有分录名
	 * 
	 * @param allEntities
	 * @param logoName
	 * @return
	 */
	private List<ComboItem> getEntitiesNames(String logoName) {
		List<ComboItem> comboItems = new ArrayList<ComboItem>();
		MainEntityType confType = MetadataServiceHelper.getDataEntityType(logoName);
		Map<String, EntityType> allEntities = confType.getAllEntities();
		allEntities.entrySet().stream().map(Map.Entry::getKey).filter(entityKey -> !logoName.equals(entityKey))
				.forEach(entitiesName -> {
					DynamicObject dy = new DynamicObject(confType);
					String displayName = "error=>未找到";
					try {
						displayName = dy.getDynamicObjectCollection(entitiesName).getDynamicObjectType()
								.getDisplayName().getLocaleValue();
					} catch (Exception e) {

					}
					LocaleString localeString = new LocaleString(entitiesName + "(" + displayName + ")");
					comboItems.add(new ComboItem(localeString, entitiesName));
				});
		return comboItems;
	}

	/**
	 * 获取单据体属性信息
	 * 
	 * @param dynamicObjectCollection
	 * @return
	 */
	private List<ComboItem> getEntryFieldsName(DynamicObjectCollection dynamicObjectCollection) {
		DynamicObjectType type = dynamicObjectCollection.getDynamicObjectType();
		List<ComboItem> comboItems = new ArrayList<ComboItem>();
		type.getProperties().stream().forEach(field -> {
			LocaleString localeString = new LocaleString(field.getName() + "(" + field.getDisplayName() + ")");
			comboItems.add(new ComboItem(localeString, field.getName()));
		});
		return comboItems;
	}

	private void clearCombo(String comboLogo) {
		ComboEdit comboEdit = this.getControl(comboLogo);
		comboEdit.setComboItems(null);
	}

	private void getlinks() {
		DynamicObject workflow = (DynamicObject) this.getModel().getValue("bfgy_botp_workflow");

		if (null == workflow) {
			this.getView().showErrorNotification("请选择业务流！");
			return;
		}

		String sourceentitynumber = workflow.getString("sourceentitynumber.number");
		String targetentitynumber = workflow.getString("targetentitynumber.number");

		String sourcetype = (String) this.getModel().getValue("bfgy_sourcetype");
		String sourceentry = (String) this.getModel().getValue("bfgy_sourceentry");
		String sourcefield = (String) this.getModel().getValue("bfgy_sourcefield");
		String sourcebasefield = (String) this.getModel().getValue("bfgy_sourcebasefield");
		String sourcelocal = (String) this.getModel().getValue("bfgy_sourcelocal");

		String targettype = (String) this.getModel().getValue("bfgy_targettype");
		String targetentry = (String) this.getModel().getValue("bfgy_targetentry");
		String targetfield = (String) this.getModel().getValue("bfgy_targetfield");
		String targetbasefield = (String) this.getModel().getValue("bfgy_targetbasefield");
		String targetlocal = (String) this.getModel().getValue("bfgy_targetlocal");
		
		Set<String> target_select = new HashSet<>();
		target_select.add("billno");
		target_select.add("id");
		if ("C".equalsIgnoreCase(targettype) || "D".equalsIgnoreCase(targettype)) {
			target_select.add(targetentry+".id");
		}
		target_select.add(targetlocal);
		Set<String> source_select = new HashSet<>();
		source_select.add("billno");
		source_select.add("id");
		if ("C".equalsIgnoreCase(sourcetype) || "D".equalsIgnoreCase(sourcetype)) {
			source_select.add(sourceentry+".id");
		}
		source_select.add(sourcelocal);

		QFilter[] qFilters_target = { new QFilter(targetlocal, QCP.is_notnull, null) };
		DynamicObjectCollection target_dys = QueryServiceHelper.query(targetentitynumber, StringUtils.join(target_select, ",") , qFilters_target);
		
		QFilter[] qFilters_source = { new QFilter(sourcelocal, QCP.is_notnull, null) };
		DynamicObjectCollection source_dys = QueryServiceHelper.query(sourceentitynumber, StringUtils.join(source_select, ",") , qFilters_source);
		
		
//		Map<String,String> target_map = new HashMap<String,String>();
//		target_dys.stream().forEach(m->{
//			if(StringUtils.isNotBlank(m.getString(targetlocal)) && "A".equalsIgnoreCase(targettype) || "B".equalsIgnoreCase(targettype)) {
//				target_map.put(m.getString(targetlocal), m.getString("id")+"."+m.getString("id")+".billno:"+m.getString("billno"));
//			}else if(StringUtils.isNotBlank(m.getString(targetlocal)) && "C".equalsIgnoreCase(targettype) || "D".equalsIgnoreCase(targettype)) {
//				target_map.put(m.getString(targetlocal), m.getString("id")+"."+m.getString(targetentry+".id")+".billno:"+m.getString("billno"));
//			}
//		});
		Map<Object, List<DynamicObject>> target_group = target_dys.stream().collect(Collectors.groupingBy(i->i.getString(targetlocal)));
		
		Map<Object, List<DynamicObject>> source_group = source_dys.stream().collect(Collectors.groupingBy(i->i.getString(sourcelocal)));
		
		List<DynamicObject> list = new ArrayList<>();
		target_group.forEach((key, value)->{
			value.stream().forEach(target_dy->{
				String target_id = target_dy.getString("id");
				String target_billno = target_dy.getString("billno");
				String target_eid = "";
				if(StringUtils.isNotBlank(target_dy.getString(targetlocal)) && "A".equalsIgnoreCase(targettype) || "B".equalsIgnoreCase(targettype)) {
					target_eid = target_dy.getString("id");
				}else if(StringUtils.isNotBlank(target_dy.getString(targetlocal)) && "C".equalsIgnoreCase(targettype) || "D".equalsIgnoreCase(targettype)) {
					target_eid = target_dy.getString(targetentry+".id");
				}
				
				String target_local = "";
				if ("C".equalsIgnoreCase(targettype) || "D".equalsIgnoreCase(targettype)) {
					target_local = "B";
				}else {
					target_local = "A";
				}
				
				List<DynamicObject> source_list = source_group.get(key);
				if (null != source_list && source_list.size() > 0) {
					for(DynamicObject m : source_list){
						String source_id = m.getString("id");
						String source_eid = "";
						String source_local = "";
						String source_billno = "billno:"+m.getString("billno");
						if ("C".equalsIgnoreCase(sourcetype) || "D".equalsIgnoreCase(sourcetype)) {
							source_eid = m.getString(sourceentry+".id");
							source_local = "B";
						}else {
							source_eid = source_id;
							source_local = "A";
						}
						
						DynamicObject new_dy = BusinessDataServiceHelper.newDynamicObject("bfgy_super_pool");
						new_dy.set("name", source_billno + "->"+ key + "->" + target_billno);
						new_dy.set("bfgy_value", key);
						new_dy.set("bfgy_sourcename", sourceentitynumber);
						new_dy.set("bfgy_sourceid", source_id);
						new_dy.set("bfgy_sourceename", sourceentry);
						new_dy.set("bfgy_sourceeid", source_eid);
						new_dy.set("bfgy_sourcelocal", source_local);
						new_dy.set("bfgy_sourcebillno", source_billno);
						
						new_dy.set("bfgy_targetname", targetentitynumber);
						new_dy.set("bfgy_targetid", target_id);
						new_dy.set("bfgy_targetename", targetentry);
						new_dy.set("bfgy_targeteid", target_eid);
						new_dy.set("bfgy_targetlocal", target_local);
						new_dy.set("bfgy_targetbillno", target_billno);
						
						new_dy.set("status", "A");
						new_dy.set("enable", true);
						list.add(new_dy);
					}
				}
			});
		});
		
		this.getView().showMessage("共" + list.size() + "条！");
		SaveServiceHelper.save(list.toArray(new DynamicObject[list.size()]));
		System.out.println(1);
		
	}
}
