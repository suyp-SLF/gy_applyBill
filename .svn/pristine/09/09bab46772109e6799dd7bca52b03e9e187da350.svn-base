package kd.cus.wb.report;

import kd.bos.algo.*;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.report.AbstractReportColumn;
import kd.bos.entity.report.AbstractReportListDataPlugin;
import kd.bos.entity.report.FilterInfo;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在执行项目
 *
 *项目表单 -> 出口合同 -> 实际发货单
 *       |          |-> 收款单
 *       |-> 合同关闭
 *
 *
 */

public class ExecutingproListDataPlugin extends AbstractReportListDataPlugin {

    private static final String dsbxd = "er_dailyreimbursebill";//对私报销单
    private static final String dgbxd = "er_publicreimbursebill";//对公报销单
    private static final String clbxd = "er_tripreimbursebill";//差旅报销单

    private String[] params = {
            "id",
            "bfgy_projname",
            "bfgy_projno",
            "auditdate",
            "org",
            "bfgy_projcountry",
            "bfgy_customers",
            "bfgy_prodtype",
            "bfgy_mimtcoopmodes",
            "bfgy_dollaramount"
    };

    private String[] FIELD = {
            "id ",
            "bfgy_projname bfgy_pjname",
            "bfgy_projno bfgy_pjno",
            "auditdate bfgy_startdate",
            "org bfgy_executeorg",
            "bfgy_projcountry bfgy_country",
            "bfgy_customers bfgy_pjcustom",
            "bfgy_prodtype bfgy_pjtype",
            "bfgy_mimtcoopmodes bfgy_teamworkmodel",
            "bfgy_dollaramount bfgy_sumamount",
            "bfgy_amountfield bfgy_actualamountout ",
            "actrecamt bfgy_actualamountin",
          //  "bfgy_actualamountcost",
            "pc_auditdate bfgy_enddate"
    };

    private Map<String, BigDecimal> SJFSCK_AMOUNT = new HashMap<>();

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {
        FilterInfo qfilter = reportQueryParam.getFilter();
        String type = qfilter.getString("bfgy_type");
        Date year = qfilter.getDate("bfgy_dateyear");

        Date yearmonth = qfilter.getDate("bfgy_dateyearmonth");

        QFilter[] qFilters = new QFilter[4];

        QFilter[] qFilters1 = new QFilter[5];
        if ("0".equalsIgnoreCase(type) && null != year){
            SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy-01-01 00:00:00");
            year = yearsdf.parse(yearsdf.format(year));
            Calendar calyear = Calendar.getInstance();
            calyear.setTime(year);//设置起时间
            calyear.add(Calendar.YEAR,1);
            Date yearplus = calyear.getTime();
            qFilters[0] = new QFilter("auditdate", QCP.large_equals,year);
            qFilters[1] = new QFilter("auditdate", QCP.less_than,yearplus);

            qFilters1[2] = new QFilter("bankdocumentdate",QCP.large_equals,year);
            qFilters1[3] = new QFilter("bankdocumentdate",QCP.less_than,yearplus);
        }else if("1".equalsIgnoreCase(type) && null != yearmonth) {
            SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
            yearmonth = monthsdf.parse(monthsdf.format(yearmonth));
            Calendar calyearmonth = Calendar.getInstance();
            calyearmonth.setTime(yearmonth);//设置起时间
            calyearmonth.add(Calendar.MONTH,1);
            Date yearmonthplus = calyearmonth.getTime();
            qFilters[0] = new QFilter("auditdate", QCP.large_equals,yearmonth);
            qFilters[1] = new QFilter("auditdate", QCP.less_than,yearmonthplus);

            qFilters1[2] = new QFilter("bankdocumentdate",QCP.large_equals,yearmonth);
            qFilters1[3] = new QFilter("bankdocumentdate",QCP.less_than,yearmonthplus);
        }
        qFilters[2] = new QFilter("billstatus", QCP.equals,"C");
        //项目表单
        String[] pj_selects = {
                "id",
                "bfgy_projname",//项目名称
                "bfgy_projno",//项目号
                "auditdate",//审核日期
                "org",//创建部门
                "bfgy_projcountry",/*国家地区*/
                "bfgy_customers",//项目客户
                "bfgy_prodtype",//产品类型
                "bfgy_mimtcoopmodes",/*军工军技合作模式*/
                "bfgy_dollaramount",//总金额
                "bfgy_datefield",//ye务关闭
                "bfgy_datefield1",//财务关闭
        };

        String[] sjfhd_selects = {
                "bfgy_itemnumber",
                "bfgy_amountfield"
        };

        String[] not_selects = {
                "bfgy_projnumber",
                "bfgy_asdollertotal"
        };

        String[] rec_selects = {
                "bfgy_basedataxm_wb",
                "localamt"};

        DynamicObjectCollection sjfhd_dataSet = QueryServiceHelper.query(this.getClass().getName(),"bfgy_wb_actualinvoice",StringUtils.join(sjfhd_selects, ","),new QFilter[]{new QFilter("billstatus",QCP.equals,"C")},null);
        Map<String, List<DynamicObject>> sum_group = sjfhd_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_itemnumber")));

        DynamicObjectCollection shtzd_dataSet = QueryServiceHelper.query(this.getClass().getName(), "bfgy_wb_receiptnotice", StringUtils.join(not_selects, ","), new QFilter[]{new QFilter("bfgy_combofield", QCP.in, "A")}, null);
        Map<String, List<DynamicObject>> shtzd_sum_group = shtzd_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_projnumber")));

        qFilters1[0] = new QFilter("billstatus",QCP.equals,"D");
        qFilters1[1] = new QFilter("billtype.number",QCP.in,new String[]{"WBcas_paybill_pur_BT_S_01","WBcas_paybill_other_BT_S_02"});

        DynamicObjectCollection rec_dataSet = QueryServiceHelper.query(this.getClass().getName(), "cas_paybill", StringUtils.join(rec_selects, ","), qFilters1, null);
        Map<String, List<DynamicObject>> rec_sum_group = rec_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_basedataxm_wb")));

        //报销单
        DynamicObject[] clbxd_dataSet = BusinessDataServiceHelper.load(clbxd, "bfgy_wb_rbprojrep,cgjtentity_cw.bfgy_reimbursement", new QFilter[]{new QFilter("bfgy_wb_projecttype", QCP.equals, "bfgy_proj_wb_pmb"), new QFilter("bfgy_wb_rbprojrep", QCP.is_notnull, null)});
        Map<String, List<DynamicObject>> clbxd_dataSet_group = Arrays.stream(clbxd_dataSet).collect(Collectors.groupingBy(i -> i.getString("bfgy_wb_rbprojrep")));

        DynamicObjectCollection dsbxd_dataSet = QueryServiceHelper.query(dsbxd,"bfgy_wb_rbprojrep,expenseentryentity.expenseamount",new QFilter[]{new QFilter("bfgy_wb_projecttype",QCP.equals,"bfgy_proj_wb_pmb"), new QFilter("bfgy_wb_rbprojrep", QCP.is_notnull, null)});
        Map<String, List<DynamicObject>> dsbxd_dataSet_group = dsbxd_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_wb_rbprojrep")));

        DynamicObjectCollection dgbxd_dataSet = QueryServiceHelper.query(dgbxd,"bfgy_wb_rbprojrep,expenseentryentity.expenseamount",new QFilter[]{new QFilter("bfgy_wb_projecttype",QCP.equals,"bfgy_proj_wb_pmb"), new QFilter("bfgy_wb_rbprojrep", QCP.is_notnull, null)});
        Map<String, List<DynamicObject>> dgbxd_dataSet_group = dgbxd_dataSet.stream().collect(Collectors.groupingBy(i -> i.getString("bfgy_wb_rbprojrep")));

        DynamicObjectCollection pj_dataSet = QueryServiceHelper.query(this.getClass().getName(), "bfgy_proj_wb_pmb", StringUtils.join(pj_selects, ","), qFilters, "bfgy_version desc");

        List<Object[]> result_dataSetList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        pj_dataSet.stream().filter(i->isdis(set,i.getString("bfgy_projno"))).forEach(m->{
//            BigDecimal clbxd_dataSet_value = clbxd_dataSet_group.get(m.getString("bfgy_projno")) != null ?clbxd_dataSet_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("tripentry")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):BigDecimal.ZERO;
            BigDecimal clbxd_dataSet_value = BigDecimal.ZERO;
            if (clbxd_dataSet_group.get(m.getString("bfgy_projno")) != null){
                for(DynamicObject item:clbxd_dataSet_group.get(m.getString("bfgy_projno"))){
                    DynamicObjectCollection entry = item.getDynamicObjectCollection("tripentry");
//                    clbxd_dataSet_value = clbxd_dataSet_value.add(entry.stream().map(i->i.getBigDecimal("bfgy_reimbursement")).reduce(BigDecimal.ZERO,BigDecimal::add));
                    for(DynamicObject ent:entry){
                        DynamicObjectCollection the_one = ent.getDynamicObjectCollection("cgjtentity_cw");
                        for (DynamicObject one:the_one){
                            clbxd_dataSet_value = clbxd_dataSet_value.add(one.getBigDecimal("bfgy_reimbursement"));
                        }
                    }
                }
            }
            BigDecimal dsbxd_dataSet_value = dsbxd_dataSet_group.get(m.getString("bfgy_projno")) != null ?dsbxd_dataSet_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("expenseentryentity.expenseamount")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):BigDecimal.ZERO;
            BigDecimal dgbxd_dataSet_value = dgbxd_dataSet_group.get(m.getString("bfgy_projno")) != null ?dgbxd_dataSet_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("expenseentryentity.expenseamount")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):BigDecimal.ZERO;
            BigDecimal rec_dataSet_value = rec_sum_group.get(m.getString("bfgy_projno")) != null ?rec_sum_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("localamt")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):BigDecimal.ZERO;

            result_dataSetList.add(new Object[]{
                    m.getString("id"),
                    "6",
                    m.getString("bfgy_projname"),
                    m.getString("bfgy_projno"),
                    m.getDate("auditdate"),
                    m.getString("org"),
                    m.getString("bfgy_projcountry"),
                    m.getString("bfgy_customers"),
                    m.getString("bfgy_prodtype"),
                    m.getString("bfgy_mimtcoopmodes"),
                    m.getBigDecimal("bfgy_dollaramount").setScale(2,2),
                    sum_group.get(m.getString("bfgy_projno")) != null ?sum_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("bfgy_amountfield")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):0,
                    shtzd_sum_group.get(m.getString("bfgy_projno")) != null ?shtzd_sum_group.get(m.getString("bfgy_projno")).stream().map(i->i.getBigDecimal("bfgy_asdollertotal")).reduce(BigDecimal.ZERO,BigDecimal::add).setScale(2,2):0,
                    clbxd_dataSet_value.add(dsbxd_dataSet_value).add(dgbxd_dataSet_value).add(rec_dataSet_value),
                    m.getDate("bfgy_datefield"),
                    m.getDate("bfgy_datefield1"),
            });
        });

        String[] cols = new String[]{
                "bfgy_billid",
                "bfgy_currencyfield",
                "bfgy_pjname",
                "bfgy_pjno",
                "bfgy_startdate",
                "bfgy_executeorg",
                "bfgy_country",
                "bfgy_pjcustom",
                "bfgy_pjtype",
                "bfgy_teamworkmodel",
                "bfgy_sumamount",
                "bfgy_actualamountout",
                "bfgy_actualamountin",
                "bfgy_actualamountcost",
                "bfgy_datefield",
                "bfgy_datefield1",
        };


        DataType[] datatypes = new DataType[]{
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.DateType,//5
                DataType.StringType,//6
                DataType.StringType,//7
                DataType.StringType,//8
                DataType.StringType,//9
                DataType.StringType,//10
                DataType.StringType,//11
                DataType.StringType,//12
                DataType.StringType,//13
                DataType.StringType,//14
                DataType.DateType,//15
                DataType.DateType,//16
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);

        return result_dataSet;

//        String[] con_selects = {
//                "billno",
//                "bfgy_projno"};
//        DataSet con_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_wbexportcontract", StringUtils.join(con_selects, ","), null, null);
//        DataSet pj_con_dataSet = pj_dataSet.leftJoin(con_dataSet).on("bfgy_projno","bfgy_projno").select(pj_selects,new String[]{"billno"}).finish();
//        //出口合同
//        String[] pc_selects = {
//                "bfgy_pcprojno",
//                "auditdate pc_auditdate"};
//        /*项目关闭*/
//        DataSet pc_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"bfgy_proj_wb_projclose", StringUtils.join(pc_selects,","), null, null);
//        DataSet result = pj_con_dataSet.leftJoin(pc_dataSet).on("bfgy_projno", "bfgy_pcprojno").select(ArrayUtils.addAll(params,new String[]{"billno"})).finish();
//
//        result = result.copy().leftJoin(pc_dataSet).on("bfgy_projno","bfgy_pcprojno").select(ArrayUtils.addAll(params,new String[]{"billno","pc_auditdate"})).finish();
//        /*实际发货单*/
//        String[] act_selects = {
//                "bfgy_contractno",
//                "bfgy_amountfield"};
//        DataSet act_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"bfgy_wb_actualinvoice",StringUtils.join(act_selects,","),null,null);
//        result = result.copy().leftJoin(act_dataSet).on("billno","bfgy_contractno").select(ArrayUtils.addAll(params,new String[]{"billno","pc_auditdate","bfgy_amountfield"})).finish();
////        params.add("bfgy_amountfield");
//        /*收款单*/
//        String[] rec_selects = {
//                "bfgy_constract",
//                "actrecamt"};
//        DataSet rec_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"cas_recbill",StringUtils.join(rec_selects,","),null,null);
//        result = result.copy().leftJoin(rec_dataSet).on("billno","bfgy_constract").select(ArrayUtils.addAll(params,new String[]{"billno","pc_auditdate","bfgy_amountfield","actrecamt"})).finish();
//
//        /*收汇通知单*/
//        String[] not_selects = {
//                "bfgy_projnumber",
//                "bfgy_asdollertotal"
//        };
//        DataSet not_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(),"bfgy_wb_receiptnotice",StringUtils.join(not_selects,","),null,null);
//        not_dataSet.copy().groupBy("bfgy_projnumber");
//
//
//        DataSet sumamount_dataSet = result.copy().groupBy(ArrayUtils.addAll(params,new String[]{"pc_auditdate"})).sum("bfgy_dollaramount").sum("bfgy_amountfield").sum("actrecamt").finish();
//
////        result = result.copy().removeFields("bfgy_dollaramount","bfgy_amountfield","actrecamt");
//
////        sumamount_dataSet = sumamount_dataSet.copy().join(result, JoinType.INNER).on("id","id").select(ArrayUtils.addAll(params,new String[]{"billno","pc_auditdate","bfgy_amountfield","actrecamt"})).finish();
//
//        DataSet retData = sumamount_dataSet.copy().select(FIELD);
//
//        retData = retData.copy().addField("6","bfgy_currencyfield");

//        return retData;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        return super.getColumns(columns);
    }

    private boolean isdis(Set<String> set, String no){
        int size = set.size();
        set.add(no);
        if (set.size() > size){
            return true;
        }
        return false;
    }
}
