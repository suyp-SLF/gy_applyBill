package kd.cus.wb.botp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.LinkSetElement;
import kd.bos.entity.LinkSetItemElement;
import kd.bos.entity.botp.runtime.TableDefine;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
//import kd.fi.arapcommon.helper.BotpRelationHelper;

public class SuperBotpOppoPlugin extends AbstractOperationServicePlugIn {

	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		// TODO Auto-generated method stub
		super.onAddValidators(e);
		WriteMeetingOpinionValidator opinionVal = new WriteMeetingOpinionValidator();
		e.addValidator(opinionVal);
	}

	public class WriteMeetingOpinionValidator extends AbstractValidator {
		@Override
		public void validate() {
			ExtendedDataEntity[] data = this.getDataEntities();
			if (data.length > 0) {
//				List<ExtendedDataEntity> select = data.getSelectedRows();
				for (ExtendedDataEntity itemvalue : data) {

					Object id = itemvalue.getBillPkId();
					List<Object> select_ids = Arrays.asList(id);

					String oper = this.getOperateKey();
					if ("addbotp".equalsIgnoreCase(oper)) {
						relation(select_ids, "add");
					} else if ("delbotp".equalsIgnoreCase(oper)) {
						relation(select_ids, "del");
					} else if ("cekbotp".equalsIgnoreCase(oper)) {
						Boolean cekbotp = relation(select_ids, "cek");
						if (cekbotp) {
							this.addMessage(itemvalue, "存在关系！");
						} else {
							this.addMessage(itemvalue, "不存在关系！");
						}
					}else if("savebotp".equalsIgnoreCase(oper)) {
						saveop(select_ids);
					}
				}
			}
		}
	}

//	@Override
//	public void beforeExecuteOperationTransaction(BeforeOperationArgs e) {
//		
//	}
	
	private Boolean saveop(List<Object> select_ids) {
		String[] select_fields = {"bfgy_targetid","bfgy_targetname"};
		DynamicObject[] cols = BusinessDataServiceHelper.load("bfgy_super_pool", StringUtils.join(select_fields, ","),
				new QFilter[] { new QFilter("id", QCP.in, select_ids) });
		for(DynamicObject col:cols) {
			String targetid = col.getString("bfgy_targetid");
			String targetname = col.getString("bfgy_targetname");
			DynamicObject target = BusinessDataServiceHelper.loadSingle(targetid,targetname);
//			OperationResult result = OperateServiceHelper.execOperate("save", targetname, new DynamicObject[] {target}, OperateOption.create());
			
			SaveServiceHelper.saveOperate(targetname, // 目标单主实体编码
			new DynamicObject[] { target }, // 目标单数据包
			OperateOption.create());
		}
			
		return null;
	}

	private Boolean relation(List<Object> select_ids, String option) {

		String[] select_fields = { "bfgy_sourcename", "bfgy_sourceid", "bfgy_sourceename", "bfgy_sourceeid",
				"bfgy_sourcelocal", "bfgy_sourcebillno", "bfgy_targetname", "bfgy_targetid", "bfgy_targetename",
				"bfgy_targeteid", "bfgy_targetlocal", "bfgy_targetbillno", "bfgy_success","bfgy_link" };

		DynamicObject[] cols = BusinessDataServiceHelper.load("bfgy_super_pool", StringUtils.join(select_fields, ","),
				new QFilter[] { new QFilter("id", QCP.in, select_ids) });
		for (DynamicObject col : cols) {
			String sourceid = col.getString("bfgy_sourceid");
			String sourceeid = col.getString("bfgy_sourceeid");
			String sourcename = col.getString("bfgy_sourcename");
			String sourcelocal = col.getString("bfgy_sourcelocal");
			String sourceename = col.getString("bfgy_sourceename");

			String targetid = col.getString("bfgy_targetid");
			String targeteid = col.getString("bfgy_targeteid");
			String targetname = col.getString("bfgy_targetname");
			String targetlocal = col.getString("bfgy_targetlocal");
			String targetename = col.getString("bfgy_targetename");

			DynamicObject target_dy = BusinessDataServiceHelper.loadSingle(targetid, targetname);
			DynamicObject source_dy = BusinessDataServiceHelper.loadSingle(sourceid, sourcename);			
			
			TableDefine sourceTableDefine = null;
			if ("A".equalsIgnoreCase(sourcelocal)) {
				sourceTableDefine = EntityMetadataCache.loadTableDefine(sourcename, sourcename);
			} else if ("B".equalsIgnoreCase(sourcelocal)) {
				sourceTableDefine = EntityMetadataCache.loadTableDefine(sourcename, sourceename);
			}

			DynamicObjectCollection target_cols = null;
			String lk = "";
			if ("A".equalsIgnoreCase(targetlocal)) {
				lk = "billhead_lk";
				if (target_dy.getString("id").equals(targetid)) {
					DynamicObjectCollection lk_col = target_dy.getDynamicObjectCollection("billhead_lk");
					List<DynamicObject> lk_dys = new ArrayList<DynamicObject>();
					
					for (DynamicObject lk_dy : lk_col) {
						if (lk_dy.getLong(lk + "_stableid") == sourceTableDefine.getTableId()
								&& lk_dy.getString(lk + "_sbillid").equalsIgnoreCase(sourceid)
								&& lk_dy.getString(lk + "_sid").equalsIgnoreCase(sourceeid)) {
							lk_dys.add(lk_dy);
						}
					}

					lk_col.removeAll(lk_dys);

					if ("cek".equalsIgnoreCase(option) && lk_dys.size() > 0) {
						col.set("bfgy_link", true);
						SaveServiceHelper.save(new DynamicObject[] { col });
						return true;
					} else if ("cek".equalsIgnoreCase(option) && lk_dys.size() == 0) {
						col.set("bfgy_link", false);
						SaveServiceHelper.save(new DynamicObject[] { col });
						return false;
					}

					if ("add".equalsIgnoreCase(option)) {
						DynamicObject linkRow = new DynamicObject(lk_col.getDynamicObjectType());
						lk_col.add(linkRow);

						linkRow.set(lk + "_stableid", sourceTableDefine.getTableId()); // 源单分录表格编码：以此标识源单类型及单据体
						linkRow.set(lk + "_sbillid", sourceid); // 源单内码
						linkRow.set(lk + "_sid", sourceeid); // 源单分录行内码
						
						col.set("bfgy_link", true);
					} else if ("del".equalsIgnoreCase(option)) {
						col.set("bfgy_link", false);
					}
				}
			} else if ("B".equalsIgnoreCase(targetlocal)) {
				LinkSetElement targetlinkSet = EntityMetadataCache.getLinkSet(targetname);
				for (LinkSetItemElement link : targetlinkSet.getItems()) {
					if (targetename.equalsIgnoreCase(link.getParentEntityKey())) {
						lk = link.getLinkEntityKey();
					}
				}

				target_cols = target_dy.getDynamicObjectCollection(targetename);
				for (DynamicObject target_col : target_cols) {
					if (target_col.getString("id").equals(targeteid)) {
						DynamicObjectCollection lk_col = target_col.getDynamicObjectCollection(lk);
	
						List<DynamicObject> lk_dys = new ArrayList<DynamicObject>();
						for (DynamicObject lk_dy : lk_col) {
							if (lk_dy.getLong(lk + "_stableid") == sourceTableDefine.getTableId()
									&& lk_dy.getString(lk + "_sbillid").equalsIgnoreCase(sourceid)
									&& lk_dy.getString(lk + "_sid").equalsIgnoreCase(sourceeid)) {
								lk_dys.add(lk_dy);
							}
						}
	
						lk_col.removeAll(lk_dys);
	
						if ("cek".equalsIgnoreCase(option) && lk_dys.size() > 0) {
							col.set("bfgy_link", true);
							SaveServiceHelper.save(new DynamicObject[] { col });
							return true;
						} else if ("cek".equalsIgnoreCase(option) && lk_dys.size() == 0) {
							col.set("bfgy_link", false);
							SaveServiceHelper.save(new DynamicObject[] { col });
							return false;
						}
	
						if ("add".equalsIgnoreCase(option)) {
							DynamicObject linkRow = new DynamicObject(lk_col.getDynamicObjectType());
							lk_col.add(linkRow);
	
							linkRow.set(lk + "_stableid", sourceTableDefine.getTableId()); // 源单分录表格编码：以此标识源单类型及单据体
							linkRow.set(lk + "_sbillid", sourceid); // 源单内码
							linkRow.set(lk + "_sid", sourceeid); // 源单分录行内码
							
							col.set("bfgy_link", true);
						} else if ("del".equalsIgnoreCase(option)) {
							col.set("bfgy_link", false);
						}
					}
				}
			}

//			SaveServiceHelper.saveOperate(targetname, // 目标单主实体编码
//					new DynamicObject[] { target_dy }, // 目标单数据包
//					OperateOption.create());
			
			SaveServiceHelper.save(new DynamicObject[] { target_dy }, // 目标单数据包
					OperateOption.create());
			col.set("bfgy_success", true);
			
			SaveServiceHelper.save(new DynamicObject[] { col });
		}
		return true;
	}
}
