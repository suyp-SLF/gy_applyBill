package kd.cus.wb.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.algo.Algo;
import kd.bos.algo.DataSet;
import kd.bos.algo.DataType;
import kd.bos.algo.RowMeta;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.entity.report.*;
import kd.bos.ext.fi.eb.operation.QueryBudgetService;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 外币余额表
 */

public class CurrencyBalanceListDataPlugin extends AbstractReportListDataPlugin {

    private static final String VOUCHER_TABLE = "gl_voucher";
    private static DynamicObjectCollection vouchers = null;

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {
        FilterInfo filter = reportQueryParam.getFilter();
        Date startdate = filter.getDate("startdate");
        Date enddate = filter.getDate("enddate");

        QFilter[] qFilters = new QFilter[4];
        if (startdate != null) {
            qFilters[0] = new QFilter("bookeddate", QCP.large_equals, startdate);
        }
        if (enddate != null) {
            qFilters[1] = new QFilter("bookeddate", QCP.less_equals, enddate);
        }

        qFilters[2] = new QFilter("ispost", QCP.equals, true);
        qFilters[3] = new QFilter("org.number", QCP.equals, "8018");


        List<Object[]> result_dataSetList = new ArrayList<>();
        if (true) {
            vouchers = QueryServiceHelper.query(VOUCHER_TABLE, "id,entries.account.number,entries.assgrp.value,entries.debitlocal,entries.debitori,entries.creditori,entries.currency.number", qFilters);
        }

        Map<String, BigDecimal> amountMap_USD = new HashMap<String, BigDecimal>() {{
            put("col_01", BigDecimal.ZERO);
            put("col_02", BigDecimal.ZERO);
            put("col_03", BigDecimal.ZERO);
            put("col_04", BigDecimal.ZERO);
            put("col_05", BigDecimal.ZERO);
            put("col_11", BigDecimal.ZERO);
            put("col_12", BigDecimal.ZERO);
            put("col_13", BigDecimal.ZERO);
        }};

        Map<String, BigDecimal> amountMap_EUR = new HashMap<String, BigDecimal>() {{
            put("col_01", BigDecimal.ZERO);
            put("col_02", BigDecimal.ZERO);
            put("col_03", BigDecimal.ZERO);
            put("col_04", BigDecimal.ZERO);
            put("col_05", BigDecimal.ZERO);
            put("col_11", BigDecimal.ZERO);
            put("col_12", BigDecimal.ZERO);
            put("col_13", BigDecimal.ZERO);
        }};

        for (DynamicObject voucher : vouchers) {
            BigDecimal amount = voucher.getBigDecimal("entries.debitori");
            BigDecimal amountd = voucher.getBigDecimal("entries.creditori");

            if (null != voucher && null != voucher.getString("entries.assgrp.value") && "USD".equalsIgnoreCase(voucher.getString("entries.currency.number"))) {
                String keynumber = voucher.getString("entries.account.number");
                if (keynumber.startsWith("1002")) {//货币资金,银行存款
                    amountMap_USD.put("col_01", amountMap_USD.get("col_01").add(amount).subtract(amountd));
                } else if (keynumber.startsWith("1131")) {//应收股利
                    amountMap_USD.put("col_02", amountMap_USD.get("col_02").add(amount).subtract(amountd));
                } else if (keynumber.startsWith("1122")) {//应收账款
                    amountMap_USD.put("col_03", amountMap_USD.get("col_03").add(amount).subtract(amountd));
                } else if (keynumber.startsWith("1221")) {//其他应收款
                    amountMap_USD.put("col_04", amountMap_USD.get("col_04").add(amount).subtract(amountd));
                } else if (keynumber.startsWith("1123")) {//预付账款
                    amountMap_USD.put("col_05", amountMap_USD.get("col_05").add(amount).subtract(amountd));
                } else if (keynumber.startsWith("2202")) {//应付账款
                    amountMap_USD.put("col_11", amountMap_USD.get("col_11").add(amount).subtract(amountd));
                } else if (keynumber.startsWith("2203")) {//预收账款
                    amountMap_USD.put("col_12", amountMap_USD.get("col_12").add(amount).subtract(amountd));
                } else if (keynumber.startsWith("2241")) {//其他应付款
                    amountMap_USD.put("col_13", amountMap_USD.get("col_13").add(amount).subtract(amountd));
                }
            } else if (null != voucher && null != voucher.getString("entries.assgrp.value") && "EUR".equalsIgnoreCase(voucher.getString("entries.currency.number"))) {
                String keynumber = voucher.getString("entries.account.number");
                if (keynumber.startsWith("1002")) {//货币资金,银行存款
                    amountMap_EUR.put("col_01", amountMap_EUR.get("col_01").add(amountd).subtract(amount));
                } else if (keynumber.startsWith("1131")) {//应收股利
                    amountMap_EUR.put("col_02", amountMap_EUR.get("col_02").add(amountd).subtract(amount));
                } else if (keynumber.startsWith("1122")) {//应收账款
                    amountMap_EUR.put("col_03", amountMap_EUR.get("col_03").add(amountd).subtract(amount));
                } else if (keynumber.startsWith("1221")) {//其他应收款
                    amountMap_EUR.put("col_04", amountMap_EUR.get("col_04").add(amountd).subtract(amount));
                } else if (keynumber.startsWith("1123")) {//预付账款
                    amountMap_EUR.put("col_05", amountMap_EUR.get("col_05").add(amountd).subtract(amount));
                } else if (keynumber.startsWith("2202")) {//应付账款
                    amountMap_EUR.put("col_11", amountMap_EUR.get("col_11").add(amountd).subtract(amount));
                } else if (keynumber.startsWith("2203")) {//预收账款
                    amountMap_EUR.put("col_12", amountMap_EUR.get("col_12").add(amountd).subtract(amount));
                } else if (keynumber.startsWith("2241")) {//其他应付款
                    amountMap_EUR.put("col_13", amountMap_EUR.get("col_13").add(amountd).subtract(amount));
                }
            }
        }

        BigDecimal sum_amount_USD = amountMap_USD.get("col_01").add(amountMap_USD.get("col_02")).add(amountMap_USD.get("col_03")).add(amountMap_USD.get("col_04")).add(amountMap_USD.get("col_05"));
        BigDecimal sum_amount_EUR = amountMap_EUR.get("col_01").add(amountMap_EUR.get("col_02")).add(amountMap_EUR.get("col_03")).add(amountMap_EUR.get("col_04")).add(amountMap_EUR.get("col_05"));

        BigDecimal sum_amount_USD_SUB = amountMap_USD.get("col_11").add(amountMap_USD.get("col_12")).add(amountMap_USD.get("col_13"));
        BigDecimal sum_amount_EUR_SUB = amountMap_EUR.get("col_11").add(amountMap_EUR.get("col_12")).add(amountMap_EUR.get("col_13"));

        BigDecimal sum_amount_USD_BAL = sum_amount_USD.subtract(sum_amount_USD_SUB);
        BigDecimal sum_amount_EUR_BAL = sum_amount_EUR.subtract(sum_amount_EUR_SUB);
        result_dataSetList.add(new Object[]{"   1.货币资金", amountMap_USD.get("col_01").setScale(2, 2), amountMap_EUR.get("col_01").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"   2.应收股利", amountMap_USD.get("col_02").setScale(2, 2), amountMap_EUR.get("col_02").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"   3.应收账款", amountMap_USD.get("col_03").setScale(2, 2), amountMap_EUR.get("col_03").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"   4.其他应收款", amountMap_USD.get("col_04").setScale(2, 2), amountMap_EUR.get("col_04").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"   5.预付账款", amountMap_USD.get("col_05").setScale(2, 2), amountMap_EUR.get("col_05").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"资产总额", sum_amount_USD.setScale(2, 2), sum_amount_EUR.setScale(2, 2)});
        result_dataSetList.add(new Object[]{"   1.应付账款", amountMap_USD.get("col_11").setScale(2, 2), amountMap_EUR.get("col_11").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"   2.预收账款", amountMap_USD.get("col_12").setScale(2, 2), amountMap_EUR.get("col_12").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"   3.其他应付款", amountMap_USD.get("col_13").setScale(2, 2), amountMap_EUR.get("col_13").setScale(2, 2)});
        result_dataSetList.add(new Object[]{"负债总额", sum_amount_USD_SUB.setScale(2, 2), sum_amount_EUR_SUB.setScale(2, 2)});
        result_dataSetList.add(new Object[]{"净美元资产", sum_amount_USD_BAL.setScale(2, 2), sum_amount_EUR_BAL.setScale(2, 2)});


        String[] cols = {
                "col_1_01",
                "col_1_02",
                "col_1_03",
        };

        DataType[] datatypes = {
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);
        return result_dataSet;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        String json = "[" +
                "{\"caption\":\"  \"," +
                "\"key\":\"col_1_01\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"美元\"," +
                "\"key\":\"col_1_02\"," +
                "\"link\":\"false\"," +
                "\"dyname\":\"bd_currency\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"欧元\"," +
                "\"key\":\"col_1_03\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

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
}
