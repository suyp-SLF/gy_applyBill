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

/**
 * 项目状态
 */
public class ProjectStatusListDataPluginOLD  extends AbstractReportListDataPlugin {

    private String[] FIELD = {
            "id bfgy_billid",
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
        Date start = qfilter.getDate("bfgy_startdaterange");
        Date end = qfilter.getDate("bfgy_enddaterange");

        QFilter[] qFilters = new QFilter[9];
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
            qFilters[4] = new QFilter("bfgy_auditdate", QCP.large_equals,start);
        }
        if (end != null){
            qFilters[5] = new QFilter("bfgy_auditdate", QCP.less_equals,end);
        }
        qFilters[6] = new QFilter("bfgy_teammemberentity.bfgy_mrole.number", QCP.in,new String[]{"projectmanager","001001"});
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
                "bfgy_projstatus",//项目状态
                "bfgy_projname",//项目名称
                "bfgy_projno",//项目号
                "bfgy_auditdate",//审核日期
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
        DynamicObjectCollection con_dataSet = QueryServiceHelper.query(this.getClass().getName(),"bfgy_wbexportcontract","bfgy_projno,billno,bfgy_usaamount",null,"bfgy_edition desc");
        Set<String> con_set = new HashSet<>();
        Map<String, List<DynamicObject>> con_group = con_dataSet.stream().filter(i -> isdis(con_set, i.getString("bfgy_projno"))).collect(Collectors.groupingBy(i -> i.getString("bfgy_projno")));
        //实际发货单
        DynamicObjectCollection act_dataSet = QueryServiceHelper.query(this.getClass().getName(),"bfgy_wb_actualinvoice","bfgy_itemnumber,auditdate,bfgy_amountfield",new QFilter[]{new QFilter("billstatus",QCP.equals,"C")},null);
        Map<String, List<DynamicObject>> act_all_group = act_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_itemnumber")));
        Map<String, List<DynamicObject>> act_month_group = act_dataSet.stream().filter(i->thismonth(i.getDate("auditdate"))).collect(Collectors.groupingBy(i -> i.getString("bfgy_itemnumber")));
        Map<String, List<DynamicObject>> act_year_group = act_dataSet.stream().filter(i->thisyear(i.getDate("auditdate"))).collect(Collectors.groupingBy(i -> i.getString("bfgy_itemnumber")));

        List<Object[]> result_dataSetList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        pj_dataSet.stream().filter(i->isdis(set,i.getString("bfgy_projno"))).forEach(m->{
            result_dataSetList.add(new Object[]{
                    m.getString("id"),
                    m.getString("bfgy_projname"),
                    m.getString("bfgy_projno"),
                    m.getDate("bfgy_auditdate"),
                    m.getString("bfgy_projcountry"),
                    m.getString("bfgy_customers"),
                    m.getString("org"),
                    m.getString("bfgy_teammemberentity.bfgy_mname"),
                    m.getString("bfgy_teammemberentity.bfgy_mrole.number"),
                    m.getDate("bfgy_auditdate"),
                    m.getString("bfgy_currency"),
                    m.getBigDecimal("bfgy_dollaramount"),
                    con_group.get(m.getString("bfgy_projno")) != null ?con_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("bfgy_usaamount")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):0,
                    act_all_group.get(m.getString("bfgy_projno")) != null ?act_all_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("bfgy_amountfield")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):0,
                    act_month_group.get(m.getString("bfgy_projno")) != null ?act_month_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("bfgy_amountfield")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):0,
                    act_year_group.get(m.getString("bfgy_projno")) != null ?act_year_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("bfgy_amountfield")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
            });
        });

        String[] cols = new String[]{
                "bfgy_billid",
                "bfgy_pjname",
                "bfgy_pjnum",
                "bfgy_startdate",
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
                "bfgy_monthactualout",//本月实际出口
                "bfgy_yearactualout",//今年实际出口
                "bfgy_actualsumin",//实际累计收款
                "bfgy_accrecbal",//应收款余额
                "bfgy_actualsumbuy",//实际累计采购接收
                "bfgy_monthactualbuy",//本月实际采购接收
                "bfgy_yearactualbuy",//今年实际采购接收
                "bfgy_actualsumcost",//实际累计总成本
                "bfgy_basesumrate",//基准累计完成度
                "bfgy_actualsumrate",//实际累计完成度
                "bfgy_basesumbuy",//基准总采购额
                "bfgy_sumalreaday",//累计已签采购合同额
                "bfgy_baseallcost",//基准总成本
                "bfgy_basepjprofits",//基准项目利润
                "bfgy_baseenddate",//基准完工日期
                "bfgy_actualenddate",//实际完工日期
                "bfgy_pjstatus",//项目状态
        };


        DataType[] datatypes = new DataType[]{
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
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
                DataType.StringType,//14
                DataType.StringType,//15
                DataType.StringType,//16
                DataType.StringType,//17
                DataType.StringType,//18
                DataType.StringType,//19
                DataType.StringType,//20
                DataType.StringType,//21
                DataType.StringType,//22
                DataType.StringType,//24
                DataType.StringType,//25
                DataType.StringType,//26
                DataType.StringType,//27
                DataType.StringType,//28
                DataType.StringType,//29
                DataType.StringType,//30
                DataType.StringType,//31
                DataType.StringType,//32
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
