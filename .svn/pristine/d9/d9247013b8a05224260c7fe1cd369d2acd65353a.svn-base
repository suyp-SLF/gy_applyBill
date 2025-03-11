package kd.cus.wb.cas.offerfile;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.utils.OrmUtils;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.ExtendedDataEntitySet;
import kd.bos.entity.botp.plugin.AbstractConvertPlugIn;
import kd.bos.entity.botp.plugin.args.*;
import kd.bos.entity.botp.runtime.AbstractConvertServiceArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.botp.BFTrackerServiceHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 报盘文件|万宝转换插件，
 *
 * @author suyp
 */
public class OfferfileConvertPlugin extends AbstractConvertPlugIn {

	private List<String> billno = new ArrayList<>();

	@Override
	public void beforeGetSourceData(BeforeGetSourceDataEventArgs e) {
		System.out.println(1);
	}

	@Override
	public void afterBuildDrawFilter(AfterBuildDrawFilterEventArgs e) {
		DynamicObject dst = e.getTargetDataEntity();
		boolean iscurrency = dst.getBoolean("bfgy_iscurrency");
		if (iscurrency) {
			if (null != dst.getDynamicObject("bfgy_basedatafield")
					&& dst.getDynamicObject("bfgy_basedatafield").getDynamicObjectCollection("currency").size() > 0) {
				DynamicObject dy = dst.getDynamicObject("bfgy_basedatafield").getDynamicObjectCollection("currency").get(0);
				QFilter qfilter = e.getPlugFilter();
				String currency = dy.getDynamicObject("fbasedataid").getString("number");
				if (null != qfilter) {
					qfilter.and(new QFilter("paycurrency.number", QCP.equals, currency)
							.and(new QFilter("currency.number", QCP.equals, currency)));
	//                QFilter qFilter_zld_3 = new QFilter("bfgy_exportstate_wb",QCP.equals,false);//是否导出
	//                QFilter qFilter_zld_4 = new QFilter("settleorg.number",QCP.equals,"8018");//结算组织
	//                QFilter qFilter_zld_5 = new QFilter("billstatus",QCP.equals,"C");//单据状态
	//
	//                QFilter qFilter_zld_1 = new QFilter("bfgy_businesstype_wb.number",QCP.not_equals,"WBZC07");//业务类型：现场费
	//                QFilter qFilter_zld_2 = new QFilter("bfgy_fundingtype.number",QCP.not_equals,"B");//拨款类型
	//                QFilter qFilter_zld_6 = new QFilter("billtype",QCP.equals,"WBap_payapply_oth_BT_S_04");//单据类型：
					e.setPlugFilter(qfilter);
				} else {
					e.setPlugFilter(new QFilter("paycurrency.number", QCP.equals, currency)
							.and(new QFilter("currency.number", QCP.equals, currency)));
				}
	
			}
		}
	}

	@Override
	public void afterGetSourceData(AfterGetSourceDataEventArgs e) {
		billno = e.getSourceRows().stream().map(i -> i.getString("billno")).collect(Collectors.toList());
		System.out.println(3);
	}

	@Override
	public void afterConvert(AfterConvertEventArgs e) {
		super.afterConvert(e);
		ExtendedDataEntitySet entitySet = e.getTargetExtDataEntitySet();
		ExtendedDataEntity[] entities = entitySet.FindByEntityKey("bfgy_bfgy_agencyentry");
		List<ExtendedDataEntity> copyDataEntitys = new ArrayList<>();
		QFilter[] qFilters = { new QFilter("billno", QCP.in, billno) };
		DynamicObjectCollection this_dys = QueryServiceHelper.query("ap_payapply",
				"applyamount,billno,entry.e_asstact,entry.e_assacct,paycurrency,paycurrency.id,billtype.number,bfgy_collectdetail_wb.bfgy_sqfkje_wb,bfgy_collectdetail_wb.bfgy_skf_wb.name,bfgy_collectdetail_wb.bfgy_skzh_wb,entry.e_bebank.province.name,entry.e_bebank.city.name,entry.e_bebank.name,entry.e_bebank.union_number,"
				+ "bfgy_collectdetail_wb.bfgy_skyh_wb.province.name,bfgy_collectdetail_wb.bfgy_skyh_wb.city.name,bfgy_collectdetail_wb.bfgy_skyh_wb.name,bfgy_collectdetail_wb.bfgy_skyh_wb.union_number",
				qFilters);
		Map<String, List<DynamicObject>> billidGroup = this_dys.stream().collect(Collectors.groupingBy(i->i.getString("billno")));
		
		int i=0;
		Iterator<Entry<String, List<DynamicObject>>> iter = billidGroup.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, List<DynamicObject>> entry = iter.next();
			String key = entry.getKey();
			List<DynamicObject> value = entry.getValue();
			
			DynamicObject dataEntity = entities[i].getDataEntity();
			
			List<DynamicObject> dys = value;
			Set<String> accts = new HashSet<String>();
			Set<String> names = new HashSet<String>();
			Set<String> billnos = new HashSet<String>();
			Set<Object> billnoset = new HashSet<Object>();
			Set<String> amount = new HashSet<String>();
			Set<String> province = new HashSet<String>();
			Set<String> city = new HashSet<String>();
			Set<String> banknames = new HashSet<String>();
			Set<String> unionnames = new HashSet<String>();
			String currency = null;
			
			Set<String> currency_map = new HashSet<String>();
			Map<Object, DynamicObject> currency_dys = null;
			for(DynamicObject m : value) {
				currency_map.add(m.getString("paycurrency.id"));
			}
			if(currency_map.size() > 0) {
				currency_dys = BusinessDataServiceHelper.loadFromCache(currency_map.toArray(), "bd_currency");
			}
			
			for (DynamicObject m : value) {
				if ("WBap_payapply_BT_S_01".equalsIgnoreCase(value.get(0).getString("billtype.number"))
						|| "WBap_payapply_BT_S_02".equalsIgnoreCase(value.get(0).getString("billtype.number"))) {
					if(m.getString("bfgy_collectdetail_wb.bfgy_skzh_wb") != null) {
						accts.add(m.getString("bfgy_collectdetail_wb.bfgy_skzh_wb"));
					}
					if(m.getString("bfgy_collectdetail_wb.bfgy_skf_wb.name") != null) {
						names.add(m.getString("bfgy_collectdetail_wb.bfgy_skf_wb.name"));
					}
					if(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.province.name") != null) {
						province.add(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.province.name"));
					}
					if(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.city.name") != null) {
						city.add(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.city.name"));
					}
					if(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.name") != null) {
						banknames.add(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.name"));
					}
					if(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.union_number") != null) {
						unionnames.add(m.getString("bfgy_collectdetail_wb.bfgy_skyh_wb.union_number"));
					}
					if(m.getString("bfgy_collectdetail_wb.bfgy_sqfkje_wb") != null) {
//						amount.add( m.getString("bfgy_collectdetail_wb.bfgy_sqfkje_wb"));
					}
				}else {
					if(m.getString("entry.e_asstact") != null) {
						accts.add(m.getString("entry.e_asstact"));
					}
					if(m.getString("entry.e_assacct") != null) {
						names.add(m.getString("entry.e_assacct"));
					}
					if(m.getString("entry.e_bebank.province.name") != null) {
						province.add(m.getString("entry.e_bebank.province.name"));
					}
					if(m.getString("entry.e_bebank.city.name") != null) {
						city.add(m.getString("entry.e_bebank.city.name"));
					}
					if(m.getString("entry.e_bebank.name") != null) {
						banknames.add(m.getString("entry.e_bebank.name"));
					}
					if(m.getString("entry.e_bebank.union_number") != null) {
						unionnames.add(m.getString("entry.e_bebank.union_number"));
					}
					if(m.getString("applyamount") != null) {
//						amount.add(m.getString("applyamount"));
					}

				}
				if (m.get("paycurrency") != null) {
					currency = m.getString("paycurrency.id");
				}
				if(m.getString("billno") != null) {
					billnos.add(m.getString("billno"));
				}
				if(m.get("paycurrency.id") != null) {
					billnoset.add(m.get("paycurrency.id"));
				}
			}
			
			dataEntity.set("bfgy_bfgy_reciptaccount", accts.stream().collect(Collectors.joining(",")));
			dataEntity.set("bfgy_bfgy_recivename", names.stream().collect(Collectors.joining(",")));
			dataEntity.set("bfgy_bfgy_zldno", billnos.stream().collect(Collectors.joining(",")));
//			dataEntity.set("bfgy_currencyfield", billnoset.));
//			dataEntity.set("bfgy_bfgy_applyamount", amount.stream().map(m->m.toString()).collect(Collectors.joining(",")));
			dataEntity.set("bfgy_zjlcprovince", province.stream().collect(Collectors.joining(",")));//
			dataEntity.set("bfgy_zjlccity", city.stream().collect(Collectors.joining(",")));
			dataEntity.set("bfgy_zjlcbank", banknames.stream().collect(Collectors.joining(",")));
			dataEntity.set("bfgy_zjlcbanknum", unionnames.stream().collect(Collectors.joining(",")));
//			dataEntity.set("bfgy_currencyfield", currency_dys.get(currency));
			i++;
		}
	}
}
