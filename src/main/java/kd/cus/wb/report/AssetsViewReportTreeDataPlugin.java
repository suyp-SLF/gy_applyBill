package kd.cus.wb.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.algo.Algo;
import kd.bos.algo.DataSet;
import kd.bos.algo.DataType;
import kd.bos.algo.RowMeta;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.entity.report.*;
import kd.bos.orm.ORM;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class AssetsViewReportTreeDataPlugin extends AbstractReportListDataPlugin {
    private static final String PROJECT_BILL_LOGO = "bfgy_proj_wb_pmb";
    private static final String CONTRACT_BILL_LOGO = "bfgy_wbexportcontract";
    private static final String EXPORTINVOICE_BILL_LOGO = "bfgy_wb_exportinvoice";
    private static final String REVCFMBILL_BILL_LOGO = "ar_revcfmbill";
    private static final String FINARBILL_BILL_LOGO = "ar_finarbill";

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {
        FilterInfo qfilter = reportQueryParam.getFilter();
        DynamicObjectCollection projects = qfilter.getDynamicObjectCollection("bfgy_projectsfield");//项目

        QFilter[] qFilters = null;

        if (projects != null && projects.size() > 0){
            List<String> projects_number_list = projects.stream().map(i -> i.getString("number")).collect(Collectors.toList());
            qFilters = new QFilter[]{new QFilter("bfgy_projno", QCP.in, projects_number_list)};
        }

        //报表数据
        List<Object[]> result_dataSetList = new ArrayList<>();

        Map<String,String> currencymap = new HashMap<>();

        Set<String> dist_project = new HashSet<>();
        DynamicObjectCollection project_cols = QueryServiceHelper.query(PROJECT_BILL_LOGO, "bfgy_projno,bfgy_projname", qFilters);
        List<String> project_numbers = project_cols.stream().map(i -> i.getString("bfgy_projno")).distinct().collect(Collectors.toList());

        Set<String> dist_contract = new HashSet<>();
        DynamicObjectCollection contract_cols = QueryServiceHelper.query(CONTRACT_BILL_LOGO,"billno,bfgy_projno,bfgy_exportname",null);
        List<String> project_numbers_exist = contract_cols.stream().map(i -> i.getString("bfgy_projno")).distinct().collect(Collectors.toList());

        Set<String> dist_exportinvoice = new HashSet<>();
        DynamicObjectCollection exportinvoice_cols = QueryServiceHelper.query(EXPORTINVOICE_BILL_LOGO,"bfgy_invoiceno,bfgy_excontractno,bfgy_currency,bfgy_totalamount,bfgy_unconfirmmoney",null);
        List<String> contract_numbers_exist = exportinvoice_cols.stream().map(i->i.getString("bfgy_excontractno")).distinct().collect(Collectors.toList());

        Set<String> dist_revcfmbill = new HashSet<>();
        DynamicObjectCollection revcfmbill_cols = QueryServiceHelper.query(REVCFMBILL_BILL_LOGO,"billno,bfgy_outrecnum,confirmamt,bfgy_amountfield,bfgy_amountfield3",null);
        List<String> revcfmbill_numbers_exist = revcfmbill_cols.stream().map(i->i.getString("bfgy_outrecnum")).distinct().collect(Collectors.toList());

        Set<String> dist_finarbill = new HashSet<>();
        DynamicObjectCollection finarbill_cols = QueryServiceHelper.query(FINARBILL_BILL_LOGO,"billno,bfgy_reccomfbillno,recamount",null);
        List<String> finarbill_numbers_exist = finarbill_cols.stream().map(i->i.getString("bfgy_reccomfbillno")).distinct().collect(Collectors.toList());

        project_cols.stream().filter(i->distinct(dist_project,i,"bfgy_projno")).forEach(item->{
            String COL_1_PROJECTNO = item.getString("bfgy_projno");
            String COL_1_PROJECTNAME = item.getString("bfgy_projname");
            result_dataSetList.add(new Object[]{
                    "P_" + COL_1_PROJECTNO,
                    "0",
                    "P_" + COL_1_PROJECTNO,
                    project_numbers_exist.contains(COL_1_PROJECTNO),
                    "项目：" + COL_1_PROJECTNO,
                    COL_1_PROJECTNAME,
                    COL_1_PROJECTNO,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
            });
        });

        contract_cols.stream().filter(i->distinct(dist_contract,i,"billno")).forEach(item->{
            String COL_2_CONTRACTNO = item.getString("billno");
            String COL_2_CONTRACTPROJECTNO = item.getString("bfgy_projno");
            String COL_2_CONTRACTNAME = item.getString("bfgy_exportname");

            result_dataSetList.add(new Object[]{
                    "C_" + COL_2_CONTRACTNO,
                    "P_" + COL_2_CONTRACTPROJECTNO,
                    "C_" + COL_2_CONTRACTNO,
                    contract_numbers_exist.contains(COL_2_CONTRACTNO),
                    "出口合同：" + COL_2_CONTRACTNO,
                    null,
                    null,
                    COL_2_CONTRACTNO,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
            });
        });

        exportinvoice_cols.stream().filter(i->distinct(dist_exportinvoice,i,"bfgy_invoiceno")).forEach(item->{
            String COL_3_EXPORTINVOICENO = item.getString("bfgy_invoiceno");
            String COL_3_EXPORTINVOICECONTRACTNO = item.getString("bfgy_excontractno");
            String COL_3_EXPORTINVOICECURRENCY = item.getString("bfgy_currency");
            BigDecimal COL_3_EXPORTINVOICETOTALAMOUNT = item.getBigDecimal("bfgy_totalamount");
            BigDecimal COL_3_EXPORTINVOICETOTUNCONFIRM = item.getBigDecimal("bfgy_unconfirmmoney");

            currencymap.put("I_" + COL_3_EXPORTINVOICENO,COL_3_EXPORTINVOICECURRENCY);

            result_dataSetList.add(new Object[]{
                    "I_" + COL_3_EXPORTINVOICENO,
                    "C_" + COL_3_EXPORTINVOICECONTRACTNO,
                    "I_" + COL_3_EXPORTINVOICENO,
                    revcfmbill_numbers_exist.contains(COL_3_EXPORTINVOICENO),
                    "出口发票：" + COL_3_EXPORTINVOICENO,
                    null,
                    null,
                    null,
                    COL_3_EXPORTINVOICENO,
                    COL_3_EXPORTINVOICECURRENCY,
                    COL_3_EXPORTINVOICETOTALAMOUNT,
                    COL_3_EXPORTINVOICETOTUNCONFIRM,
                    null,
                    null,
                    null,
            });
        });

        revcfmbill_cols.stream().filter(i->distinct(dist_revcfmbill,i,"billno")).forEach(item->{
            String COL_4_REVEFMBILLNO = item.getString("billno");
            String COL_4_REVEFMBILLEXPORTINVOICENO = item.getString("bfgy_outrecnum");
            BigDecimal COL_4_REVEFMBILLAMOUNT = item.getBigDecimal("confirmamt");
            BigDecimal COL_4_REVEFMBILLCONAMOUNT = item.getBigDecimal("bfgy_amountfield");
            BigDecimal COL_4_REVEFMBILLCONAMOUNTED = item.getBigDecimal("bfgy_amountfield3");

            currencymap.put("R_" + COL_4_REVEFMBILLNO,currencymap.get("I_" + COL_4_REVEFMBILLEXPORTINVOICENO));

            result_dataSetList.add(new Object[]{
                    "R_" + COL_4_REVEFMBILLNO,
                    "I_" + COL_4_REVEFMBILLEXPORTINVOICENO,
                    "R_" + COL_4_REVEFMBILLNO,
                    finarbill_numbers_exist.contains(COL_4_REVEFMBILLNO),
                    "收入确认：" + COL_4_REVEFMBILLNO,
                    null,
                    null,
                    null,
                    null,
                    currencymap.get("I_" + COL_4_REVEFMBILLEXPORTINVOICENO),
                    null,
                    null,
                    COL_4_REVEFMBILLAMOUNT.setScale(2),
                    COL_4_REVEFMBILLAMOUNT.subtract(COL_4_REVEFMBILLCONAMOUNTED).setScale(2),
                    null,
            });
        });

        finarbill_cols.stream().filter(i->distinct(dist_finarbill,i,"billno")).forEach(item->{
            String COL_5_FINARBILLNO = item.getString("billno");
            String COL_5_FINARBILLREVEFMBILLNO = item.getString("bfgy_reccomfbillno");
            BigDecimal COL_5_FINARBILLAMOUNT = item.getBigDecimal("recamount");

            result_dataSetList.add(new Object[]{
                    "F_" + COL_5_FINARBILLNO,
                    "R_" + COL_5_FINARBILLREVEFMBILLNO,
                    "F_" + COL_5_FINARBILLNO,
                    false,
                    "应收确认：" + COL_5_FINARBILLNO,
                    null,
                    null,
                    null,
                    null,
                    currencymap.get("R_" + COL_5_FINARBILLREVEFMBILLNO),
                    null,
                    null,
                    null,
                    null,
                    COL_5_FINARBILLAMOUNT.setScale(2)
            });
        });

        String[] cols = {
                "id",
                "pid",
                "rowid",
                "isgroupnode",
                "col_1_01",
                "col_1_02",
                "col_1_03",
                "col_1_04",
                "col_1_05",
                "col_1_06",
                "col_1_07",
                "col_1_08",
                "col_1_09",
                "col_1_10",
                "col_1_11",
        };


        DataType[] datatypes = {
                DataType.StringType,//0
                DataType.StringType,//0
                DataType.StringType,//0
                DataType.BooleanType,//0
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//5
                DataType.StringType,//6
                DataType.StringType,//7
                DataType.StringType,//8
                DataType.StringType,//9
                DataType.StringType,//10
                DataType.StringType,//11
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);

        return result_dataSet;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        String json = "[" +

//                "{\"caption\":\"ID\"," +
//                "\"key\":\"id\"," +
//                "\"link\":\"false\"," +
//                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +
//
//                "{\"caption\":\"父ID\"," +
//                "\"key\":\"pid\"," +
//                "\"link\":\"false\"," +
//                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +
//
//                "{\"caption\":\"rowID\"," +
//                "\"key\":\"rowid\"," +
//                "\"link\":\"false\"," +
//                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +
//
//                "{\"caption\":\"叶子节点\"," +
//                "\"key\":\"isgroupnode\"," +
//                "\"link\":\"false\"," +
//                "\"type\":\"" + ReportColumn.TYPE_BOOLEAN + "\"}," +
                "{\"caption\":\"单据\"," +
                "\"key\":\"col_1_01\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目名称\"," +
                "\"key\":\"col_1_02\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目编码\"," +
                "\"key\":\"col_1_03\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"合同号\"," +
                "\"key\":\"col_1_04\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"发票编号\"," +
                "\"key\":\"col_1_05\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"币种\"," +
                "\"key\":\"col_1_06\"," +
                "\"link\":\"false\"," +
                "\"dyname\":\"bd_currency\"," +
                "\"type\":\"" + ReportColumn.TYPE_BASEDATA + "\"}," +

                "{\"caption\":\"发票金额\"," +
                "\"key\":\"col_1_07\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_06\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"未确认合同资产金额\"," +
                "\"key\":\"col_1_08\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_06\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"已确认合同资产金额\"," +
                "\"key\":\"col_1_09\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_06\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"未确认应收账款金额\"," +
                "\"key\":\"col_1_10\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_06\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"已确认应收账款金额\"," +
                "\"key\":\"col_1_11\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_06\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "]";
        JSONArray titleJSON = JSONArray.parseArray(json);
        columns.addAll(makeTitles(titleJSON));
        return columns;
    }

    //构造报表头
    private AbstractReportColumn makeColumn(String caption, String ReportColumnType, String fieldKey, Boolean
            isLink, String dyname, String currency) {
//        DecimalReportColumn column = new DecimalReportColumn();

        ReportColumn column = new ReportColumn();
//        column.setSummary(1);
        if (StringUtils.isNotBlank(currency)) {
            column.setCurrencyField(currency);
        }

        if (StringUtils.isNotBlank(dyname)) {
            column.setRefBasedataProp(dyname);
            column = ReportColumn.createCurrencyColumn(fieldKey);
        }

        LocaleString localeString = new LocaleString();
        localeString.setLocaleValue(caption);

        column.setCaption(localeString);
        column.setDisplayProp(fieldKey);
        column.setScale(-1);
        //
        column.setFieldKey(fieldKey);
        ColumnStyle defstyle = new ColumnStyle();
        defstyle.setFontSize(12);
        defstyle.setTextAlign("default");
        column.setStyle(defstyle);

        column.setHyperlink(isLink == null ? false : isLink);
        column.setFieldType(ReportColumnType);
        return column;
    }

    //集合报表头
    private List<AbstractReportColumn> makeTitles(JSONArray titleJSON) {
        List<AbstractReportColumn> titles = new ArrayList<>();
        for (int i = 0; i < titleJSON.size(); i++) {
            JSONObject item = titleJSON.getJSONObject(i);
            titles.add(makeColumn(item.getString("caption"),
                    item.getString("type"),
                    item.getString("key"),
                    item.getBoolean("link"),
                    item.getString("dyname"),
                    item.getString("currency")));
        }
        return titles;
    }

    private Boolean distinct(Set<String> dist, DynamicObject dy,String field){
        int size = dist.size();
        dist.add(dy.getString(field));
        if (dist.size() > size){
            return true;
        }else {
            return false;
        }
    }
}
