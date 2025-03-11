package kd.cus.wb.report;

import kd.bos.algo.*;
import kd.bos.algo.dataset.builder.StoreDataSetBuilder;
import kd.bos.algo.env.Environment;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.db.DB;
import kd.bos.db.DBRoute;
import kd.bos.entity.report.*;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.epm.eb.common.cache.impl.Dataset;
import kd.scmc.im.formplugin.warn.datasource.ReorderPointInventoryWarnDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 项目状态
 */
public class ProjectStatusListDataPlugin  extends AbstractReportListDataPlugin {

    private String[] FIELD = {
            "id bfgy_billid",
            "bfgy_conperiod day",
            "bfgy_projname bfgy_pjname",
            "bfgy_projno bfgy_pjnum",
            "bfgy_auditdate bfgy_startdate",
            "org bfgy_org",
            "bfgy_projcountry bfgy_country",
            "bfgy_customers bfgy_customer",
            "bfgy_teammemberentity.bfgy_mname bfgy_pjmanager",
            "bfgy_currency bfgy_currency",
            "bfgy_version bfgy_baseversion",
            "bfgy_auditdate bfgy_basedate",
            "bfgy_dollaramount bfgy_sumamount",
            "bfgy_usaamount bfgy_basesumout",
            "bfgy_amountfield_all bfgy_actualsumout",
            "bfgy_amountfield_all bfgy_monthactualout",
            "bfgy_amountfield_year bfgy_yearactualout",
//
            "bfgy_ljsk bfgy_actualsumin",
            "bfgy_ljkp-bfgy_ljsk bfgy_accrecbal",
            "matoftaxamount_month bfgy_monthactualbuy",
            "matoftaxamount_all bfgy_actualsumbuy",
            "matoftaxamount_year bfgy_yearactualbuy",
            "bfgy_projstatus bfgy_pjstatus",
//            "year+month bfgy_month"
    };

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {
        FilterInfo qfilter = reportQueryParam.getFilter();
        String pj = qfilter.getString("bfgy_pj");
        DynamicObject org = qfilter.getDynamicObject("bfgy_orgfield");
        DynamicObject country = qfilter.getDynamicObject("bfgy_countrytext");
        DynamicObject customer = qfilter.getDynamicObject("bfgy_customerfield");
        Date start = null;
        Date end = qfilter.getDate("bfgy_startdaterange");

        QFilter[] qFilters = new QFilter[9];
        QFilter[] acttimeQFilters = new QFilter[5];
        QFilter[] rectimeQFilters = new QFilter[5];
        
        QFilter[] cktimeQFilters = new QFilter[3];
        QFilter[] shtimeQFilters = new QFilter[3];
        
        if (StringUtils.isNotBlank(pj)){
            QFilter qfilter1 = new QFilter("bfgy_projname", QCP.like, "%"+pj+"%");
            QFilter qFilter2 = new QFilter("bfgy_projno",QCP.like,"%"+pj+"%");
            qFilters[0] =qfilter1.or(qFilter2);
        }
        if (org != null){
            qFilters[1] = new QFilter("org.id", QCP.equals,org.getPkValue());
        }
        if (country != null) {
            qFilters[2] = new QFilter("bfgy_projcountry.id",QCP.equals,country.getPkValue());
        }
        if(customer != null){
            qFilters[3] = new QFilter("bfgy_customers.id",QCP.equals,customer.getPkValue());
        }
        if (start != null){
        	SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        	
        	String startstr = monthsdf.format(start);
        	
        	start = monthsdf.parse(startstr);
        	
        	cktimeQFilters[0] = new QFilter("bfgy_bfgy_exproject.bfgy_bfgy_antidateex", QCP.large_equals,start);
        	shtimeQFilters[0] = new QFilter("bfgy_bfgy_inproject.bfgy_bfgy_antishdatein", QCP.large_equals,start);
        	acttimeQFilters[0] = new QFilter("auditdate", QCP.large_equals,start);
        	rectimeQFilters[0] = new QFilter("auditdate", QCP.large_equals,start);
//            qFilters[4] = new QFilter("bfgy_auditdate", QCP.large_equals,start);
        }
        if (end != null){
        	SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        	
        	String endstr = monthsdf.format(end);
        	
        	end = monthsdf.parse(endstr);
        	
        	cktimeQFilters[1] = new QFilter("bfgy_bfgy_exproject.bfgy_bfgy_antidateex", QCP.less_equals,end);
        	shtimeQFilters[1] = new QFilter("bfgy_bfgy_inproject.bfgy_bfgy_antishdatein", QCP.less_equals,end);
        	acttimeQFilters[1] = new QFilter("auditdate", QCP.less_equals,end);
        	rectimeQFilters[1] = new QFilter("auditdate", QCP.less_equals,end);
//            qFilters[5] = new QFilter("bfgy_auditdate", QCP.less_equals,end);
        }
//        qFilters[6] = new QFilter("bfgy_teammemberentity.bfgy_mrole.number", QCP.in,new String[]{"projectmanager","001001"});
        qFilters[7] = new QFilter("billstatus", QCP.equals,"C");
//        qFilters[8] = new QFilter("bfgy_teammemberentity.bfgy_mrole.number",QCP.equals,"001001");

        QFilter[] timeQfilters = new QFilter[]{new QFilter("auditdate", QCP.large_equals,start), new QFilter("auditdate", QCP.less_equals,end)};
        QFilter[] monthtimeQfilters = {};
        QFilter[] yeartimeQfilters = {};
        if (null != start) {
            SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
            Date monthstart = monthsdf.parse(monthsdf.format(start));
            monthtimeQfilters = new QFilter[]{new QFilter("auditdate", QCP.large_equals, monthstart), new QFilter("auditdate", QCP.less_equals, end)};

            SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy-01-01 00:00:00");
            Date yearstart = yearsdf.parse(yearsdf.format(start));
            yeartimeQfilters = new QFilter[]{new QFilter("auditdate", QCP.large_equals,yearstart), new QFilter("auditdate", QCP.less_equals,end)};
        }

        /*项目表单*/
        String[] pj_selects = {
                "id",
                "bfgy_conperiod",
                "bfgy_projstatus",//项目状态
                "bfgy_projname",//项目名称
                "bfgy_projno",//项目号
                "bfgy_auditdate",//审核日期
                "bfgy_creprojdate",//项目起始日期
                "org",//执行部门
                "bfgy_projcountry",//项目国家/*国家地区*/
                "bfgy_customers",//客户
                "bfgy_teammemberentity.bfgy_mname",//项目团队成员.姓名
                "bfgy_teammemberentity.bfgy_mjointimes",//项目团队成员.加入时间
                "bfgy_teammemberentity.bfgy_mrole.number",
                "bfgy_currency",//币别
                "bfgy_version",//版本号
                "bfgy_dollaramount",
        };
        DynamicObjectCollection pj_dataSet = QueryServiceHelper.query(this.getClass().getName(), "bfgy_proj_wb_pmb", StringUtils.join(pj_selects, ","), qFilters, "bfgy_auditdate desc,bfgy_teammemberentity.bfgy_mjointimes desc");

        //出口合同
//        cktimeQFilters[2] =  new QFilter("bfgy_exproject.bfgy_antiamountex",QCP.is_notnull,null);
//        shtimeQFilters[2] =  new QFilter("bfgy_inproject.bfgy_antishamountin",QCP.is_notnull,null);
        DynamicObjectCollection conck_dataSet = QueryServiceHelper.query(this.getClass().getName(),"bfgy_wbexportcontract","bfgy_projno,billno,bfgy_currency.number,bfgy_rate,bfgy_rate_usd,bfgy_bfgy_exproject.bfgy_bfgy_antiamountex",cktimeQFilters,"bfgy_edition desc");
        Set<String> conck_set = new HashSet<>();
        Map<String, List<DynamicObject>> ckcon_group = conck_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_projno")));
        
        DynamicObjectCollection consh_dataSet = QueryServiceHelper.query(this.getClass().getName(),"bfgy_wbexportcontract","bfgy_projno,billno,bfgy_currency.number,bfgy_rate,bfgy_rate_usd,bfgy_bfgy_inproject.bfgy_bfgy_antishamountin",shtimeQFilters,"bfgy_edition desc");
        Set<String> consh_set = new HashSet<>();
        Map<String, List<DynamicObject>> shcon_group = consh_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_projno")));

        //实际发货单
        
        acttimeQFilters[2] = new QFilter("billstatus",QCP.equals,"C");
        rectimeQFilters[2] = new QFilter("billstatus",QCP.equals,"C");
        rectimeQFilters[3] = new QFilter("bfgy_projno.number",QCP.is_notnull,null);
        rectimeQFilters[4] = new QFilter("bfgy_projno",QCP.is_notnull,null);
        
        DynamicObjectCollection act_dataSet = QueryServiceHelper.query("bfgy_wb_actualinvoice","billno,bfgy_itemnumber,auditdate,bfgy_amountfield",acttimeQFilters);
        Map<String, List<DynamicObject>> act_all_group = act_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_itemnumber")));

        DynamicObjectCollection rec_dataSet = QueryServiceHelper.query("bfgy_wb_receiptnotice","billno,bfgy_projno.number,auditdate,bfgy_asdollertotal",rectimeQFilters);
        Map<String, List<DynamicObject>> rec_all_group = rec_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_projno.number")));
        
        List<Object[]> result_dataSetList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        
        pj_dataSet.stream().filter(i->isdis(set,i.getString("bfgy_projno"))).forEach(m->{
        	
        	BigDecimal cksum = BigDecimal.ZERO;
        	BigDecimal actsum = BigDecimal.ZERO;
        	BigDecimal shsum = BigDecimal.ZERO;
        	BigDecimal recsum = BigDecimal.ZERO;
        	
        	String pojnum = m.getString("bfgy_projno");
        	
        	List<DynamicObject> cklist = ckcon_group.get(pojnum);
        	List<DynamicObject> actlist = act_all_group.get(pojnum);
        	List<DynamicObject> shlist = shcon_group.get(pojnum);
        	List<DynamicObject> reclist = rec_all_group.get(pojnum);
        	
        	if(cklist != null) {
        		for(DynamicObject ckdy:cklist) {
        			if(!"USD".equalsIgnoreCase(ckdy.getString("bfgy_currency.number"))) {
        				BigDecimal cksumitem = ckdy.getBigDecimal("bfgy_bfgy_exproject.bfgy_bfgy_antiamountex").setScale(2,2).multiply(ckdy.getBigDecimal("bfgy_rate_usd").setScale(2,2));
        				cksumitem = cksumitem.setScale(2,2).multiply(ckdy.getBigDecimal("bfgy_rate").setScale(2,2));
        				cksum = cksum.add(cksumitem);
        			}else {
        				BigDecimal cksumitem = ckdy.getBigDecimal("bfgy_bfgy_exproject.bfgy_bfgy_antiamountex").setScale(2,2);
        				cksum = cksum.add(cksumitem).setScale(2,2);
        			}
        			
        		}
        	}
        	if(actlist != null) {
        		actsum = actlist.stream().map(i->i.getBigDecimal("bfgy_amountfield").setScale(2,2)).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2);
        	}
        	if(shlist != null) {
        		for(DynamicObject shdy:shlist) {
        			if(!"USD".equalsIgnoreCase(shdy.getString("bfgy_currency.number"))) {
        				BigDecimal shsumitem = shdy.getBigDecimal("bfgy_bfgy_inproject.bfgy_bfgy_antishamountin").setScale(2,2).multiply(shdy.getBigDecimal("bfgy_rate_usd").setScale(2,2));
        				shsumitem = shsumitem.setScale(2,2).multiply(shdy.getBigDecimal("bfgy_rate").setScale(2,2));
        				shsum = shsum.add(shsumitem).setScale(2,2);
        			}else {
        				BigDecimal shsumitem = shdy.getBigDecimal("bfgy_bfgy_inproject.bfgy_bfgy_antishamountin").setScale(2,2);
        				shsum = shsum.add(shsumitem).setScale(2,2);
        			}
        		}
        	}
        	if(reclist != null) {
        		recsum = reclist.stream().map(i->i.getBigDecimal("bfgy_asdollertotal").setScale(2,2)).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2);
        	}
        	
        	
            result_dataSetList.add(new Object[]{
                    m.getString("id"),
                    m.getInt("bfgy_conperiod"),
                    m.getString("bfgy_projname"),
                    m.getString("bfgy_projno"),
                    m.getDate("bfgy_auditdate"),
                    m.getDate("bfgy_creprojdate"),
                    m.getString("bfgy_projcountry"),
                    m.getString("bfgy_customers"),
                    m.getString("org"),
                    m.getString("bfgy_teammemberentity.bfgy_mname"),
                    m.getString("bfgy_teammemberentity.bfgy_mrole.number"),
                    m.getDate("bfgy_auditdate"),
                    m.getString("bfgy_currency"),
                    m.getBigDecimal("bfgy_dollaramount"),
                    cksum,
                    actsum,
                    shsum,
                    recsum,
                    m.getString("bfgy_projstatus"),
            });
        });

        String[] cols = new String[]{
                "bfgy_billid",
                "bfgy_day",
                "bfgy_pjname",
                "bfgy_pjnum",
                "bfgy_startdate",
                "bfgy_startdate1",
                "bfgy_country",
                "bfgy_customer",
                "bfgy_org",
                "bfgy_pjmanager",
                "bfgy_baseversion",
                "bfgy_basedate",
                "bfgy_currency",
                "bfgy_sumamount",
                "bfgy_basesumout",//累计基准出口
                "bfgy_actualsumout",//实际累计出口
                "bfgy_baseact",//累计基准出口
                "bfgy_actualact",//实际累计出口
                "bfgy_pjstatus",//项目状态
        };


        DataType[] datatypes = new DataType[]{
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.DateType,//5
                DataType.DateType,//5
                DataType.StringType,//4
                DataType.StringType,//6
                DataType.StringType,//7
                DataType.StringType,//8
                DataType.StringType,//9
                DataType.DateType,//10
                DataType.StringType,//11
                DataType.StringType,//12

                DataType.StringType,//13
                DataType.StringType,//11
                DataType.StringType,//13
                DataType.StringType,//11
                DataType.StringType,//12
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);

        return result_dataSet;
//        /*项目表单*/
//        String[] pj_selects = {
//                "id",
//                "bfgy_projstatus",//项目状态
//                "bfgy_projname",//项目名称
//                "bfgy_projno",//项目号
//                "bfgy_auditdate",//审核日期
//                "org",//执行部门
//                "bfgy_projcountry",//项目国家/*国家地区*/
//                "bfgy_customers",//客户
//                "bfgy_teammemberentity.bfgy_mname",//项目团队成员.姓名
//                "bfgy_teammemberentity.bfgy_mjointimes",//项目团队成员.加入时间
//                "bfgy_currency",//币别
//                "bfgy_version",//版本号
//                "bfgy_dollaramount",
//        };
//        DataSet pj_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_proj_wb_pmb", StringUtils.join(pj_selects, ","), qFilters, "bfgy_auditdate desc,bfgy_teammemberentity.bfgy_mjointimes desc");
//
//        RowMeta rowMeta = pj_dataSet.getRowMeta();
//        Algo algo = Algo.create(this.getClass().getName());
//        DataSetBuilder dataSetBuilder = algo.createDataSetBuilder(rowMeta);
//        pj_dataSet = pj_dataSet.copy().groupBy(pj_selects).finish();
//        Set<Long> temp = new HashSet<Long>();
//        int size = temp.size();
//        for(Row row:pj_dataSet){
//            size = temp.size();
//            temp.add(row.getLong("id"));
//            if(temp.size() > size){
//                size = temp.size();
//                dataSetBuilder.append(row);
//            }
//        }
//        pj_dataSet = dataSetBuilder.build();
//        /*出口合同*/
//
//        /**/
//
//        String[] con_selects = {
//                "billno",
//                "bfgy_usaamount",
//                "bfgy_totalamount",
//                "bfgy_projno",
//                "bfgy_ljsk",/*累计收款*/
//                "bfgy_ljkp",/*累计将开票*/
//                "auditdate con_auditdate"
//        };
//        DataSet con_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_wbexportcontract", StringUtils.join(con_selects, ","), timeQfilters, null);
//        DataSet pj_con_dataSet = pj_dataSet.copy().leftJoin(con_dataSet).on("bfgy_projno","bfgy_projno").select(pj_selects,new String[]{"billno","bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","con_auditdate","bfgy_usaamount"}).finish();
//
//        DataSet pj_con_sum_dataSet = pj_con_dataSet.copy().groupBy(new String[]{"id"}).sum("bfgy_totalamount").sum("bfgy_ljsk").sum("bfgy_ljkp").sum("bfgy_usaamount").finish();
//
//        pj_dataSet = pj_dataSet.copy().leftJoin(pj_con_sum_dataSet).on("id","id").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount"}).finish();
//
//        /*实际发货单*/
//        String[] act_selects = {
//                "bfgy_contractno",
//                "bfgy_actualamount",
//                "bfgy_amountfield",//折美元金额/*今年实际出口*/
//                "auditdate act_auditdate"
//        };
//        DataSet act_hole_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"bfgy_wb_actualinvoice",StringUtils.join(act_selects,","),null,null);
//        DataSet act_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"bfgy_wb_actualinvoice",StringUtils.join(act_selects,","),timeQfilters,null);
//
//        DataSet act_month_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"bfgy_wb_actualinvoice",StringUtils.join(act_selects,","),monthtimeQfilters,null);
//        DataSet act_year_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"bfgy_wb_actualinvoice",StringUtils.join(act_selects,","),yeartimeQfilters,null);
//        DataSet act_month_sum_dataSet = pj_con_dataSet.copy().leftJoin(act_month_dataSet).on("billno","bfgy_contractno").select(new String[]{"id","bfgy_amountfield"}).finish().groupBy(new String[]{"id"}).sum("bfgy_amountfield").finish();
//        DataSet act_year_sum_dataSet = pj_con_dataSet.copy().leftJoin(act_year_dataSet).on("billno", "bfgy_contractno").select(new String[]{"id", "bfgy_amountfield"}).finish().groupBy(new String[]{"id"}).sum("bfgy_amountfield").finish();
//        DataSet act_all_sum_dataSet = pj_con_dataSet.copy().leftJoin(act_dataSet).on("billno", "bfgy_contractno").select(new String[]{"id", "bfgy_amountfield"}).finish().groupBy(new String[]{"id"}).sum("bfgy_amountfield").finish();
//        DataSet act_hole_sum_dataSet = pj_con_dataSet.copy().leftJoin(act_dataSet).on("billno", "bfgy_contractno").select(new String[]{"id", "bfgy_amountfield"}).finish().groupBy(new String[]{"id"}).sum("bfgy_amountfield").finish();
//
//        pj_dataSet = pj_dataSet.copy().leftJoin(act_month_sum_dataSet).on("id","id").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount","bfgy_amountfield bfgy_amountfield_month"}).finish();
//        pj_dataSet = pj_dataSet.copy().leftJoin(act_year_sum_dataSet).on("id","id").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount","bfgy_amountfield_month","bfgy_amountfield bfgy_amountfield_year"}).finish();
//        pj_dataSet = pj_dataSet.copy().leftJoin(act_all_sum_dataSet).on("id","id").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount","bfgy_amountfield_month","bfgy_amountfield_year","bfgy_amountfield bfgy_amountfield_all"}).finish();
//        pj_dataSet = pj_dataSet.copy().leftJoin(act_hole_sum_dataSet).on("id","id").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount","bfgy_amountfield_month","bfgy_amountfield_year","bfgy_amountfield_all","bfgy_amountfield bfgy_amountfield_hole"}).finish();
//
////        DataSet pj_con_act_dataSet = pj_con_dataSet.copy().leftJoin(act_dataSet).on("billno","bfgy_contractno").select(ArrayUtils.addAll(pj_selects,new String[]{"billno","bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","con_auditdate","bfgy_amountfield_month","bfgy_amountfield_year","bfgy_amountfield bfgy_amountfield_all"})).finish();
//        /*接收单*/
//        String[] rec_selects = {
//                "bfgy_pronum",
//                "matoftaxamount",//接收单金额 含税
//                "auditdate rec_auditdate",
//                "bfgy_curr"
//        };
//        DataSet rec_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"ecma_materialinbill",StringUtils.join(rec_selects,","),timeQfilters,null);
//        DataSet rec_month_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"ecma_materialinbill",StringUtils.join(rec_selects,","),monthtimeQfilters,null);
//        DataSet rec_year_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"ecma_materialinbill",StringUtils.join(rec_selects,","),yeartimeQfilters,null);
//
//        DataSet rec_all_sum_dataSet = pj_dataSet.copy().leftJoin(rec_dataSet).on("bfgy_projno", "bfgy_pronum").select(new String[]{"bfgy_projno", "matoftaxamount"}).finish().groupBy(new String[]{"bfgy_projno"}).sum("matoftaxamount").finish();
//        DataSet rec_month_sum_dataSet = pj_dataSet.copy().leftJoin(rec_month_dataSet).on("bfgy_projno", "bfgy_pronum").select(new String[]{"bfgy_projno", "matoftaxamount"}).finish().groupBy(new String[]{"bfgy_projno"}).sum("matoftaxamount").finish();
//        DataSet rec_year_sum_dataSet = pj_dataSet.copy().leftJoin(rec_year_dataSet).on("bfgy_projno", "bfgy_pronum").select(new String[]{"bfgy_projno", "matoftaxamount"}).finish().groupBy(new String[]{"bfgy_projno"}).sum("matoftaxamount").finish();
//
//        pj_dataSet = pj_dataSet.copy().leftJoin(rec_all_sum_dataSet).on("bfgy_projno","bfgy_projno").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount","bfgy_amountfield_month","bfgy_amountfield_year","bfgy_amountfield_all","bfgy_amountfield_hole","matoftaxamount matoftaxamount_all"}).finish();
//        pj_dataSet = pj_dataSet.copy().leftJoin(rec_month_sum_dataSet).on("bfgy_projno","bfgy_projno").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount","bfgy_amountfield_month","bfgy_amountfield_year","bfgy_amountfield_all","bfgy_amountfield_hole","matoftaxamount_all","matoftaxamount matoftaxamount_month"}).finish();
//        pj_dataSet = pj_dataSet.copy().leftJoin(rec_year_sum_dataSet).on("bfgy_projno","bfgy_projno").select(pj_selects,new String[]{"bfgy_totalamount","bfgy_ljsk","bfgy_ljkp","bfgy_usaamount","bfgy_amountfield_month","bfgy_amountfield_year","bfgy_amountfield_all","bfgy_amountfield_hole","matoftaxamount_all","matoftaxamount_month","matoftaxamount matoftaxamount_year"}).finish();
//
//
////        DataSet sumrec_dataSet = pj_con_act_rec_dataSet.copy().groupBy(ArrayUtils.addAll(pj_selects,new String[]{"substr(rec_auditdate,0,4) as year","substr(rec_auditdate,5,2) as month"})).sum("matoftaxamount").finish().select(ArrayUtils.addAll(pj_selects,new String[]{"year","month","matoftaxamount matoftaxamount_month","matoftaxamount"}));
////        DataSet sumact_dataSet = pj_con_act_rec_dataSet.copy().groupBy(ArrayUtils.addAll(new String[]{"id"},new String[]{"substr(act_auditdate,0,4) as actyear","substr(act_auditdate,5,2) as actmonth"})).sum("bfgy_actualamount").sum("bfgy_amountfield").finish();
////        DataSet sumcon_dataSet = pj_con_act_rec_dataSet.copy().groupBy(ArrayUtils.addAll(new String[]{"id"},new String[]{"substr(con_auditdate,0,4) as conyear","substr(con_auditdate,5,2) as conmonth"})).sum("bfgy_totalamount").sum("bfgy_ljsk").sum("bfgy_ljkp").finish().select("conyear year,bfgy_ljkp,bfgy_ljsk,conmonth,conyear,id,bfgy_totalamount bfgy_totalamount_month,bfgy_totalamount");
//
////        DataSet sumrec_sumact_dataSet = sumrec_dataSet.leftJoin(sumact_dataSet).on("id", "id").on("year","actyear").on("month","actmonth").select(ArrayUtils.addAll(pj_selects,new String[]{"year","month","matoftaxamount","matoftaxamount_month","bfgy_actualamount","bfgy_amountfield"})).finish();
////        DataSet sumrec_sumact_sumcon_dataSet = sumrec_sumact_dataSet.leftJoin(sumcon_dataSet).on("id", "id").on("year","conyear").on("month","conmonth").select(ArrayUtils.addAll(pj_selects,new String[]{"year","month","matoftaxamount","bfgy_ljsk","bfgy_ljkp","bfgy_actualamount","bfgy_amountfield","matoftaxamount_month","bfgy_totalamount_month"})).finish();
////        //接收单本年、累计计算
////        DataSet sumrec_sumact_dataSet_year = sumrec_dataSet.copy().groupBy(new String[]{"id","year"}).sum("matoftaxamount").finish();
////        DataSet sumrec_sumact_dataSet_all = sumrec_dataSet.copy().groupBy(new String[]{"id"}).sum("matoftaxamount").finish();
////
////        DataSet result = sumrec_sumact_sumcon_dataSet.copy().leftJoin(sumrec_sumact_dataSet_year).on("id", "id").on("year","year").select(ArrayUtils.addAll(pj_selects,new String[]{"year","month","matoftaxamount matoftaxamount_year","bfgy_ljsk","bfgy_ljkp","bfgy_actualamount","bfgy_amountfield","matoftaxamount_month","bfgy_totalamount_month"})).finish();
////        result = result.copy().leftJoin(sumrec_sumact_dataSet_all).on("id", "id").select(ArrayUtils.addAll(pj_selects,new String[]{"year","month","matoftaxamount_month","bfgy_ljsk","bfgy_ljkp","bfgy_actualamount","bfgy_amountfield","matoftaxamount matoftaxamount_all","matoftaxamount_year","bfgy_totalamount_month"})).finish();
////        //合同本年、累计计算
////        DataSet sumcon_sumact_dataSet_year = sumcon_dataSet.copy().groupBy(new String[]{"id","year"}).sum("bfgy_totalamount").finish();
////        DataSet sumcon_sumact_dataSet_all = sumcon_dataSet.copy().groupBy(new String[]{"id"}).sum("bfgy_totalamount").finish();
////        DataSet sum_ljsk_all = sumcon_dataSet.copy().groupBy(new String[]{"id"}).sum("bfgy_ljsk").finish();
//
////        result = result.copy().leftJoin(sumcon_sumact_dataSet_year).on("id","id").on("year","year").select(ArrayUtils.addAll(pj_selects,new String[]{"year","month","matoftaxamount_year","bfgy_ljsk","bfgy_ljkp","bfgy_actualamount","bfgy_amountfield","matoftaxamount_all","matoftaxamount_month","bfgy_totalamount_month","bfgy_totalamount bfgy_totalamount_year"})).finish();
////        result = result.copy().leftJoin(sumcon_sumact_dataSet_all).on("id", "id").select(ArrayUtils.addAll(pj_selects,new String[]{"year","month","matoftaxamount_month","bfgy_ljsk","bfgy_ljkp","bfgy_actualamount","bfgy_amountfield","matoftaxamount_all","matoftaxamount_year","bfgy_totalamount_month","bfgy_totalamount_year","bfgy_totalamount bfgy_totalamount_all"})).finish();
//
//        return pj_dataSet.copy().select(FIELD);
////        return null;
    }
    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
//        return super.getColumns(columns);
//        ReportColumn endRpt = new ReportColumn();
//        endRpt.set
//        endRpt.setCaption("测试");
//        endRpt.setWidth(width);
//        endRpt.setFieldKey("endrpt");
//        endRpt.setFieldType(ReportColumn.TYPE_DECIMAL);
//        endRpt.setScale(rptScale);
//        reportQueryParam.add(endRpt);
        return columns;
    }

    private boolean isdis(Set<String> set, String no){
        int size = set.size();
        set.add(no);
        if (set.size() > size){
            return true;
        }
        return false;
    }

    private boolean thismonth(Date date) {
        if (date != null) {
            SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
            Date now = null;
            try {
                now = monthsdf.parse(monthsdf.format(new Date()));
            } catch (ParseException e) {
//            e.printStackTrace();
                return false;
            }

            Calendar calyearmonth = Calendar.getInstance();
            calyearmonth.setTime(now);//设置起时间
            calyearmonth.add(Calendar.MONTH, 1);
            Date nowmonthplus = calyearmonth.getTime();

            if (date.compareTo(now) >= 0 && date.compareTo(nowmonthplus) < 0) {
                return true;
            }
        }
        return false;
    }

    private boolean thisyear(Date date){
        if (date != null) {
            SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-01-01 00:00:00");
            Date now = null;
            try {
                now = monthsdf.parse(monthsdf.format(new Date()));
            } catch (ParseException e) {
//            e.printStackTrace();
                return false;
            }

            Calendar calyearmonth = Calendar.getInstance();
            calyearmonth.setTime(now);//设置起时间
            calyearmonth.add(Calendar.YEAR, 1);
            Date nowmonthplus = calyearmonth.getTime();

            if (date.compareTo(now) >= 0 && date.compareTo(nowmonthplus) < 0) {
                return true;
            }
        }
        return false;
    }
}
