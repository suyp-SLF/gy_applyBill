package kd.cus.wb.report;

import kd.bos.algo.*;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.report.AbstractReportListDataPlugin;
import kd.bos.entity.report.FilterInfo;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class ContractStatusListDataPlugin extends AbstractReportListDataPlugin {
    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {

        String[] FIELD = {
                "conid bfgy_contractid",//出口合同ID
                "bfgy_edition bfgy_contractversion",//合同版本
                "con_auditdate bfgy_versiondate",//版本日期
                "bfgy_exportname bfgy_contractname",//出口合同名称
                "billno bfgy_contractno",//出口合同号
                "bfgy_srccontract bfgy_secondname",//已方名义
                "bfgy_country bfgy_contractcountry",//合同国别
                "bfgy_customer bfgy_contractcustomer",//合同客户
                "bfgy_currency_con bfgy_contractcurrency",//合同币别
                "bfgy_conamount bfgy_wbexeconamount",//万宝执行合同金额
                "bfgy_actualamount bfgy_monthoutamount",//本月出口金额
                "bfgy_remitmoney bfgy_yearoutamount",//
                "bfgy_ljck bfgy_sumoutamount",
                "bfgy_nooutamount",
                "bfgy_ljsk bfgy_suminamount",
                "bfgy_shouldbalance",
                "bhamount bfgy_noundolgnum",
                "countid bfgy_noundolgamount",
                "id bfgy_pjid",
                "bfgy_projname bfgy_pjname",
                "bfgy_projno bfgy_pjno",
                "bfgy_projcountry bfgy_pjcountry",
                "bfgy_customers bfgy_pjcustomer",
                "bfgy_prodtype bfgy_producttype",
                "org bfgy_exeorg",
                "bfgy_projstatus bfgy_pjstatus"
        };
        FilterInfo qfilter = reportQueryParam.getFilter();
        String pj = qfilter.getString("bfgy_pj");
        DynamicObject org = qfilter.getDynamicObject("bfgy_orgfield");
        DynamicObject country = qfilter.getDynamicObject("bfgy_countrytext");
        DynamicObject customer = qfilter.getDynamicObject("bfgy_customerfield");

        SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        Date now = monthsdf.parse(monthsdf.format(new Date()));
        Calendar calyearmonth = Calendar.getInstance();
        calyearmonth.setTime(now);//设置起时间
        calyearmonth.add(Calendar.MONTH, 1);
        Date nowmonthplus = calyearmonth.getTime();

        Calendar calyearmonth_1 = Calendar.getInstance();
        calyearmonth_1.setTime(now);//设置起时间
        calyearmonth_1.add(Calendar.YEAR, 1);
        Date nowyearplus = calyearmonth_1.getTime();

        QFilter[] qFilters = new QFilter[4];
        if (StringUtils.isNotBlank(pj)) {
            QFilter qfilter1 = new QFilter("bfgy_projname", QCP.like, "%" + pj + "%");
            QFilter qFilter2 = new QFilter("bfgy_projno", QCP.like, "%" + pj + "%");
            qFilters[0] = qfilter1.or(qFilter2);
        }
        if (org != null) {
            qFilters[1] = new QFilter("org.id", QCP.equals, org.getPkValue());
        }
        if (country != null) {
            qFilters[2] = new QFilter("bfgy_projcountry.id", QCP.equals, country.getPkValue());
        }
        if (customer != null) {
            qFilters[3] = new QFilter("bfgy_customers.id", QCP.equals, customer.getPkValue());
        }

        /*项目表单*/
        String[] pj_selects = {
                "id",
                "bfgy_prodtype",
                "bfgy_projstatus",
                "bfgy_projname",
                "bfgy_projno",
                "auditdate",
                "org",
                "bfgy_projcountry",/*国家地区*/
                "bfgy_customers",
                "bfgy_currency"
        };
        DataSet pj_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_proj_wb_pmb", StringUtils.join(pj_selects, ","), qFilters, "auditdate desc");
//        pj_dataSet = pj_dataSet.copy().executeSql("select id,bfgy_projstatus,bfgy_projname,bfgy_projmanager group by bfgy_projmanager");
//        DataSet pj_dataSet = DB.queryDataSet(this.getClass().getName(), new DBRoute("secd"), sql);
//        pj_dataSet.

        RowMeta rowMeta = pj_dataSet.getRowMeta();
        Algo algo = Algo.create(this.getClass().getName());
        DataSetBuilder dataSetBuilder = algo.createDataSetBuilder(rowMeta);
        pj_dataSet = pj_dataSet.copy().groupBy(pj_selects).finish();
        Set<Long> temp = new HashSet<Long>();
        int size = temp.size();
        for (Row row : pj_dataSet) {
            size = temp.size();
            temp.add(row.getLong("id"));
            if (temp.size() > size) {
                size = temp.size();
                dataSetBuilder.append(row);
            }
        }
        pj_dataSet = dataSetBuilder.build();
        /*出口合同*/

        String[] con_selects = {
                "id conid",
                "bfgy_edition",
                "bfgy_exportname",
                "bfgy_customer",
                "bfgy_srccontract",
                "bfgy_country",
                "billno",
                "bfgy_totalamount",
                "bfgy_projno",
                "bfgy_currency bfgy_currency_con",
                "bfgy_conamount",
                "auditdate con_auditdate",
                "bfgy_ljck",
                "bfgy_ljsk"
        };
        QFilter[] qFilters1 = {new QFilter("id", QCP.is_notnull, null), new QFilter("billno", QCP.is_notnull, null),new QFilter("billstatus",QCP.equals,"C")};
        DataSet con_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_wbexportcontract", StringUtils.join(con_selects, ","), qFilters1, null);
        DataSet pj_con_dataSet = pj_dataSet.leftJoin(con_dataSet).on("bfgy_projno", "bfgy_projno").select(pj_selects, new String[]{"conid", "billno", "bfgy_exportname", "bfgy_customer", "bfgy_country", "bfgy_srccontract", "bfgy_edition", "con_auditdate", "bfgy_currency_con", "bfgy_conamount", "bfgy_ljck", "bfgy_ljsk"}).finish();

        pj_con_dataSet = pj_con_dataSet.filter("conid is not null");
        /*实际发货单*/
        String[] act_selects = {
                "bfgy_contractno",
                "bfgy_amountfield",
                "bfgy_actualamount",
                "bfgy_rowtotalamount"
        };
        DataSet act_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_wb_actualinvoice", kd.bos.dataentity.utils.StringUtils.join(act_selects, ","), new QFilter[]{new QFilter("billstatus",QCP.equals,"C"),new QFilter("auditdate",QCP.large_equals,now),new QFilter("auditdate",QCP.less_than,nowmonthplus)}, null);
        DataSet act_dataSet_y = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_wb_actualinvoice", kd.bos.dataentity.utils.StringUtils.join(act_selects, ","), new QFilter[]{new QFilter("billstatus",QCP.equals,"C"),new QFilter("auditdate",QCP.large_equals,now),new QFilter("auditdate",QCP.less_than,nowyearplus)}, null);
        act_dataSet = act_dataSet.copy().groupBy(new String[]{"bfgy_contractno"}).sum("bfgy_amountfield").sum("bfgy_actualamount").sum("bfgy_rowtotalamount").finish();
        act_dataSet_y = act_dataSet_y.copy().groupBy(new String[]{"bfgy_contractno"}).sum("bfgy_rowtotalamount").finish().select("bfgy_contractno","bfgy_rowtotalamount bfgy_rowtotalamount_1");
        DataSet pj_con_act_dataSet = pj_con_dataSet.copy().leftJoin(act_dataSet).on("billno", "bfgy_contractno").select(ArrayUtils.addAll(pj_selects, new String[]{"conid", "billno", "bfgy_exportname", "bfgy_customer", "bfgy_country", "bfgy_srccontract", "bfgy_edition", "con_auditdate", "bfgy_currency_con", "bfgy_conamount", "bfgy_actualamount","bfgy_rowtotalamount", "bfgy_ljck", "bfgy_ljsk"})).finish();
        pj_con_act_dataSet = pj_con_act_dataSet.copy().leftJoin(act_dataSet_y).on("billno", "bfgy_contractno").select(ArrayUtils.addAll(pj_selects, new String[]{"conid", "billno", "bfgy_exportname", "bfgy_customer", "bfgy_country", "bfgy_srccontract", "bfgy_edition", "con_auditdate", "bfgy_currency_con", "bfgy_conamount", "bfgy_actualamount","bfgy_rowtotalamount","bfgy_rowtotalamount_1", "bfgy_ljck", "bfgy_ljsk"})).finish();
        //
        DataSet exp_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_wb_exportinvoice", "bfgy_excontractno,bfgy_totalamount", null, null);
        exp_dataSet = exp_dataSet.copy().groupBy(new String[]{"bfgy_excontractno"}).sum("bfgy_totalamount").finish();

        DataSet result = pj_con_act_dataSet.copy().leftJoin(exp_dataSet).on("billno", "bfgy_excontractno").select(ArrayUtils.addAll(pj_selects, new String[]{"conid", "billno", "bfgy_exportname", "bfgy_customer", "bfgy_country", "bfgy_srccontract", "bfgy_edition", "con_auditdate", "bfgy_currency_con", "bfgy_conamount", "bfgy_actualamount","bfgy_rowtotalamount","bfgy_rowtotalamount_1", "bfgy_totalamount", "bfgy_ljck", "bfgy_ljsk"})).finish();
        //收汇通知单
        String[] rec_selects = {
                "bfgy_excontractno",
                "bfgy_remitmoney",
                "auditdate",
                "bfgy_cybzje",
        };
        DataSet rec_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_wb_receiptnotice", StringUtils.join(rec_selects, ","), new QFilter[]{new QFilter("billstatus",QCP.equals,"C"),new QFilter("bfgy_combofield",QCP.equals,"A")}, null);
        rec_dataSet = rec_dataSet.copy().groupBy(new String[]{"bfgy_excontractno"}).sum("bfgy_remitmoney").sum("bfgy_cybzje").finish();
        result = result.copy().leftJoin(rec_dataSet).on("billno", "bfgy_excontractno").select(ArrayUtils.addAll(pj_selects, new String[]{"conid", "billno", "bfgy_exportname", "bfgy_customer", "bfgy_country", "bfgy_srccontract", "bfgy_edition", "con_auditdate", "bfgy_currency_con", "bfgy_conamount", "bfgy_actualamount","bfgy_rowtotalamount","bfgy_rowtotalamount_1", "bfgy_totalamount", "bfgy_ljck", "bfgy_ljsk", "bfgy_remitmoney","bfgy_cybzje"})).finish();

        //保函单
        String[] bh_select = {
                "id countid",
                "htno",
                "bhamount",
        };
        DataSet bh_dataSet = QueryServiceHelper.queryDataSet(this.getClass().getName(), "bfgy_bhdjd_wb", StringUtils.join(bh_select, ","), new QFilter[]{new QFilter("bhstatus", QCP.not_equals, "CX")}, null);
        bh_dataSet = bh_dataSet.copy().groupBy(new String[]{"htno"}).sum("bhamount").count("countid").finish();

        result = result.copy().leftJoin(bh_dataSet).on("billno", "htno").select(ArrayUtils.addAll(pj_selects, new String[]{"conid", "billno", "bfgy_exportname", "bfgy_customer", "bfgy_country", "bfgy_srccontract", "bfgy_edition", "con_auditdate", "bfgy_currency_con", "bfgy_conamount", "bfgy_actualamount","bfgy_rowtotalamount","bfgy_rowtotalamount_1", "bfgy_totalamount", "bfgy_ljck", "bfgy_ljsk", "bfgy_remitmoney","bfgy_cybzje", "bhamount", "countid"})).finish();

        List<Object[]> result_dataSetList = new ArrayList<>();
        result.forEach(m -> {
            BigDecimal bfgy_totalamount = m.getBigDecimal("bfgy_totalamount") == null?BigDecimal.ZERO:m.getBigDecimal("bfgy_totalamount");
            BigDecimal bfgy_conamount = m.getBigDecimal("bfgy_conamount") == null?BigDecimal.ZERO:m.getBigDecimal("bfgy_conamount");
            BigDecimal bfgy_actualamount = m.getBigDecimal("bfgy_actualamount") == null?BigDecimal.ZERO:m.getBigDecimal("bfgy_actualamount");
            BigDecimal bfgy_rowtotalamount = m.getBigDecimal("bfgy_rowtotalamount") == null?BigDecimal.ZERO:m.getBigDecimal("bfgy_rowtotalamount");//
            BigDecimal bfgy_rowtotalamount_1 = m.getBigDecimal("bfgy_rowtotalamount_1") == null?BigDecimal.ZERO:m.getBigDecimal("bfgy_rowtotalamount_1");//
            BigDecimal bfgy_ljck = m.getBigDecimal("bfgy_ljck") == null?BigDecimal.ZERO:m.getBigDecimal("bfgy_ljck");
            BigDecimal bfgy_nooutamount = bfgy_conamount.subtract(bfgy_ljck);
            BigDecimal bfgy_ljsk = m.getBigDecimal("bfgy_cybzje") == null?BigDecimal.ZERO:m.getBigDecimal("bfgy_cybzje");
            BigDecimal bfgy_shouldbalance = bfgy_conamount.subtract(bfgy_ljsk);
            result_dataSetList.add(new Object[]{
                    bfgy_totalamount.setScale(2),
                    m.getString("conid"),
                    m.getString("bfgy_edition"),
                    m.getString("con_auditdate"),
                    m.getString("bfgy_exportname"),
                    m.getString("billno"),
                    m.getString("bfgy_srccontract"),
                    m.getString("bfgy_country"),
                    m.getString("bfgy_customer"),
                    m.getString("bfgy_currency_con"),
                    bfgy_conamount.setScale(2),
                    bfgy_rowtotalamount.setScale(2),
                    bfgy_rowtotalamount_1.setScale(2),
                    bfgy_ljck.setScale(2),
                    bfgy_nooutamount.setScale(2),
                    bfgy_ljsk.setScale(2),
                    bfgy_shouldbalance.setScale(2),
                    m.getString("bhamount"),
                    m.getString("countid"),
                    m.getString("id"),
                    m.getString("bfgy_projname"),
                    m.getString("bfgy_projno"),
                    m.getString("bfgy_projcountry"),
                    m.getString("bfgy_customers"),
                    m.getString("bfgy_prodtype"),
                    m.getString("org"),
                    m.getString("bfgy_projstatus")
            });
        });
        String[] cols = {
                "bfgy_totalamount",
                "conid",
                "bfgy_edition",
                "con_auditdate",
                "bfgy_exportname",
                "billno",
                "bfgy_srccontract",
                "bfgy_country",
                "bfgy_customer",
                "bfgy_currency_con",
                "bfgy_conamount",
                "bfgy_actualamount",
                "bfgy_remitmoney",
                "bfgy_ljck",
                "bfgy_nooutamount",
                "bfgy_ljsk",
                "bfgy_shouldbalance",
                "bhamount",
                "countid",
                "id",
                "bfgy_projname",
                "bfgy_projno",
                "bfgy_projcountry",
                "bfgy_customers",
                "bfgy_prodtype",
                "org",
                "bfgy_projstatus"
        };

        DataType[] datatypes = {
                DataType.StringType,//1
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.DateType,//3
                DataType.StringType,//4
                DataType.StringType,//5
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//5
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//5
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//5
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//1
                DataType.StringType,//2
        };

        RowMeta rowMeta_s = new RowMeta(cols, datatypes);
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta_s);

        return result_dataSet.select(FIELD);
    }
}