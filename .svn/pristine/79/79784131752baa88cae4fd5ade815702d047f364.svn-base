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
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class RevcfmbillListDataPlugin extends AbstractReportListDataPlugin {
    private final static String receivable_table = "ar_revcfmbill";//收入确认单

    List<Object[][]> ids_values = new ArrayList<>();

    private static Map<String,List<List<Object>>> Allmap = new HashMap<>();
    List<List<Object>> Singlemap = new ArrayList<>();

    private static List<List<Object>> col_0 = new ArrayList<>();
    private static List<List<Object>> col_1 = new ArrayList<>();
    private static List<List<Object>> col_2 = new ArrayList<>();
    private static List<List<Object>> col_3 = new ArrayList<>();
    private static List<List<Object>> col_4 = new ArrayList<>();
    private static List<List<Object>> col_5 = new ArrayList<>();

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {
        Allmap = new HashMap<>();
        List<Object[]> result_dataSetList = new ArrayList<>();

        String[] receivable_select = {
                "id",
                "currency col_1_01",//
                "bfgy_orgfield.name col_1_03",
                "asstacttype col_1_04",
                "asstact.name col_1_05",
                "bfgy_basedataxm.name col_1_06",
                "unsettleamount",
                "bfgy_contracttexttext_wb",
                "auditdate"
        };

        QFilter[] qFilters = {new QFilter("billtypeid.number", QCP.equals,"WBApFin_pur_BT_S_01")};

        DynamicObjectCollection receivable_cols = QueryServiceHelper.query(receivable_table, StringUtils.join(receivable_select, ","), qFilters);
        Map<String, List<DynamicObject>> groupids = receivable_cols.stream().collect(Collectors.groupingBy(i -> i.getString("col_1_01") + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_03"))?"null":i.getString("col_1_03")) + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_04"))?"null":i.getString("col_1_04")) + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_05"))?"null":i.getString("col_1_05")) + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_06"))?"null":i.getString("col_1_06")) + "@_@" +
                (StringUtils.isBlank(i.getString("bfgy_contracttexttext_wb"))?"null":i.getString("bfgy_contracttexttext_wb")) + "@_@"));

        col_0 = new ArrayList<>();
        col_1 = new ArrayList<>();
        col_2 = new ArrayList<>();
        col_3 = new ArrayList<>();
        col_4 = new ArrayList<>();
        col_5 = new ArrayList<>();
        Iterator<Map.Entry<String, List<DynamicObject>>> iter = groupids.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<String, List<DynamicObject>> item = iter.next();
            String key =item.getKey();
            String[] temp = item.getKey().split("@_@");
            String currency = temp[0];
            String department = temp[1];
            String asstacttype = temp[2];
            String asstact = temp[3];
            String pjno = temp[4];
            String con = temp[5];
            String country = "";
            DynamicObject ckhe_dy = BusinessDataServiceHelper.loadSingle("bfgy_wbexportcontract", "bfgy_country.name", new QFilter[]{new QFilter("billno", QCP.large_equals, con)});

            if (null != ckhe_dy){
                country =  ckhe_dy.getString("bfgy_country.name");
            }


            String asstacttype_value = "无";
            switch (asstacttype) {
                case "bd_customer":
                    asstacttype_value = "客户";
                    break;
                case "bd_supplier":
                    asstacttype_value = "供应商";
                    break;
                case "bos_user":
                    asstacttype_value = "人员";
                    break;
            }

            BigDecimal col_0_0D = BigDecimal.ZERO;
            BigDecimal col_1_0D = BigDecimal.ZERO;
            BigDecimal col_2_0D = BigDecimal.ZERO;
            BigDecimal col_3_0D = BigDecimal.ZERO;
            BigDecimal col_4_0D = BigDecimal.ZERO;
            BigDecimal col_5_0D = BigDecimal.ZERO;


            List<Object> col_0_0 = new ArrayList<>();
            List<Object> col_1_0 = new ArrayList<>();
            List<Object> col_2_0 = new ArrayList<>();
            List<Object> col_3_0 = new ArrayList<>();
            List<Object> col_4_0 = new ArrayList<>();
            List<Object> col_5_0 = new ArrayList<>();

            Singlemap = new ArrayList<>();

            for(DynamicObject item_i : item.getValue()){
                Date auditdate = item_i.getDate("auditdate");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, 1);
                Date one_year_date = calendar.getTime();//增加一年
                calendar.add(Calendar.YEAR, 1);
                Date two_year_date = calendar.getTime();//增加两年
                calendar.add(Calendar.YEAR, 1);
                Date three_year_date = calendar.getTime();//增加三年
                calendar.add(Calendar.YEAR, 1);
                Date four_year_date = calendar.getTime();//增加四年
                calendar.add(Calendar.YEAR, 1);
                Date five_year_date = calendar.getTime();//增加五年
                if (auditdate != null) {
                    if (auditdate.getTime() < one_year_date.getTime()) {
                        col_0_0.add(item_i.get("id"));
                        col_0_0D = col_0_0D.add(item_i.getBigDecimal("unsettleamount"));
                    } else if (auditdate.getTime() < two_year_date.getTime()) {
                        col_1_0.add(item_i.get("id"));
                        col_1_0D = col_1_0D.add(item_i.getBigDecimal("unsettleamount"));
                    } else if (auditdate.getTime() < three_year_date.getTime()) {
                        col_2_0.add(item_i.get("id"));
                        col_2_0D = col_2_0D.add(item_i.getBigDecimal("unsettleamount"));
                    } else if (auditdate.getTime() < four_year_date.getTime()) {
                        col_3_0.add(item_i.get("id"));
                        col_3_0D = col_3_0D.add(item_i.getBigDecimal("unsettleamount"));
                    } else if (auditdate.getTime() < five_year_date.getTime()) {
                        col_4_0.add(item_i.get("id"));
                        col_4_0D = col_4_0D.add(item_i.getBigDecimal("unsettleamount"));
                    } else if (auditdate.getTime() >= five_year_date.getTime()) {
                        col_5_0.add(item_i.get("id"));
                        col_5_0D = col_5_0D.add(item_i.getBigDecimal("unsettleamount"));
                    }
                }
                col_0.add(col_0_0);
                col_1.add(col_1_0);
                col_2.add(col_2_0);
                col_3.add(col_3_0);
                col_4.add(col_4_0);
                col_5.add(col_5_0);

                Singlemap.add(col_0_0);
                Singlemap.add(col_1_0);
                Singlemap.add(col_2_0);
                Singlemap.add(col_3_0);
                Singlemap.add(col_4_0);
                Singlemap.add(col_5_0);

                Allmap.put(item.getKey(),Singlemap);
            }


            result_dataSetList.add(new Object[]{key,
                    currency,
                    country,
                    department,
                    asstacttype_value,
                    asstact,
                    pjno,
                    col_0_0D,
                    col_1_0D,
                    col_2_0D,
                    col_3_0D,
                    col_4_0D,
                    col_5_0D
            });
        }


        String[] cols = {"key",
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
                "col_1_12"
        };
        DataType[] datatypes = {DataType.StringType,//
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
                DataType.StringType//12
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);

        return result_dataSet;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        String json = "[" +
                "{\"caption\":\"标记\"," +
                "\"key\":\"key\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"应收账款币种\"," +
                "\"key\":\"col_1_01\"," +
                "\"link\":\"false\"," +
                "\"dyname\":\"bd_currency\"," +
                "\"type\":\"" + ReportColumn.TYPE_BASEDATA + "\"}," +

                "{\"caption\":\"国家\"," +
                "\"key\":\"col_1_02\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"部门\"," +
                "\"key\":\"col_1_03\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"集团内外\"," +
                "\"key\":\"col_1_04\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"往来户名\"," +
                "\"key\":\"col_1_05\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目编码\"," +
                "\"key\":\"col_1_06\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +


                "{\"caption\":\"1年以内应收账款\"," +
                "\"key\":\"col_1_07\"," +
                "\"link\":\"true\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"1-2年应收账款\"," +
                "\"key\":\"col_1_08\"," +
                "\"link\":\"true\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"2-3年应收账款\"," +
                "\"key\":\"col_1_09\"," +
                "\"link\":\"true\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"3-4年应收账款\"," +
                "\"key\":\"col_1_10\"," +
                "\"link\":\"true\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"4-5年应收账款\"," +
                "\"key\":\"col_1_11\"," +
                "\"link\":\"true\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"5年以上应收账款\"," +
                "\"key\":\"col_1_12\"," +
                "\"link\":\"true\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +
                "]";
        JSONArray titleJSON = JSONArray.parseArray(json);
        columns.addAll(makeTitles(titleJSON));
        return columns;
    }

    //构造报表头
    private AbstractReportColumn makeColumn(String caption, String ReportColumnType, String fieldKey, Boolean
            isLink, String dyname, String currency) {
        ReportColumn column = new ReportColumn();
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

    public static List<Object> getColIds(String row, int col){
        List<Object> ids = new ArrayList<>();
        switch (col){
            case 0:
                ids = Allmap.get(row).get(col);
                break;
            case 1:
                ids = Allmap.get(row).get(col);
                break;
            case 2:
                ids = Allmap.get(row).get(col);
                break;
            case 3:
                ids = Allmap.get(row).get(col);
                break;
            case 4:
                ids = Allmap.get(row).get(col);
                break;
            case 5:
                ids = Allmap.get(row).get(col);
                break;
        }
        return ids;
    }
}
