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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContractCostListDataPlugin extends AbstractReportListDataPlugin {

    private static final String BASE_PROJECT_TABLE = "bd_project";
    private static final String VOUCHER_TABLE = "gl_voucher";
    private static DynamicObjectCollection vouchers = null;

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {

        FilterInfo qfilter = reportQueryParam.getFilter();
        DynamicObjectCollection projects = qfilter.getDynamicObjectCollection("bfgy_projectsfield");//项目
        Boolean empty = qfilter.getBoolean("bfgy_filterempty");//项目

        QFilter[] qFilters = null;

        if (projects != null && projects.size() > 0) {
            List<String> projects_number_list = projects.stream().map(i -> i.getString("number")).collect(Collectors.toList());
            qFilters = new QFilter[]{new QFilter("bfgy_projno", QCP.in, projects_number_list)};
        }

        DynamicObjectCollection project = QueryServiceHelper.query("bfgy_proj_wb_pmb", "id,bfgy_projno,bfgy_projname", qFilters);

        List<Object[]> result_dataSetList = new ArrayList<>();

        if (vouchers == null) {
            vouchers = QueryServiceHelper.query(VOUCHER_TABLE, "id,entries.account.number,entries.assgrp.value,entries.debitlocal,entries.debitori", new QFilter[]{new QFilter("ispost", QCP.equals, true)});
        }

        project.forEach(m -> {
            DynamicObject base_project = BusinessDataServiceHelper.loadSingle(BASE_PROJECT_TABLE, "id", new QFilter[]{new QFilter("number", QCP.equals, m.getString("bfgy_projno"))});
            if (base_project == null) {
                return;
            }
            String projectPkid = base_project.getString("id");
            Map<String, BigDecimal> amountMap = new HashMap<String, BigDecimal>() {{
                put("col_zj", BigDecimal.ZERO);
                put("col_jj", BigDecimal.ZERO);
                put("col_zg", BigDecimal.ZERO);
                put("col_jz", BigDecimal.ZERO);
            }};
            for (DynamicObject voucher : vouchers) {
//                DynamicObjectCollection entries = voucher.getDynamicObjectCollection("entries");
//                for (DynamicObject entry : entries) {
                    BigDecimal amount = voucher.getBigDecimal("entries.debitlocal");
                    if (null != voucher && null != voucher.getString("entries.assgrp.value")) {
                        String json = voucher.getString("entries.assgrp.value");
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        if (StringUtils.isNotBlank(jsonObject.getString("f000006")) && jsonObject.getString("f000006").equalsIgnoreCase(projectPkid)) {
                            String keynumber = voucher.getString("entries.account.number");
                            if (keynumber.startsWith("1472.04.01") && !"1472.04.01.09".equalsIgnoreCase(keynumber)) {
                                amountMap.put("col_zj", amountMap.get("col_zj").add(amount));
                            } else if (keynumber.startsWith("1472.04.02")) {
                                amountMap.put("col_jj", amountMap.get("col_jj").add(amount));
                            } else if ("1472.04.01.09".equalsIgnoreCase(keynumber)) {
                                amountMap.put("col_zg", amountMap.get("col_zg").add(amount));
                            } else if (keynumber.startsWith("1472.04.04")) {
                                amountMap.put("col_jz", amountMap.get("col_jz").add(amount));
                            }
                        }
                    }
//                }
            }
            BigDecimal balance = amountMap.get("col_zj").add(amountMap.get("col_jj")).add(amountMap.get("col_zg")).subtract(amountMap.get("col_jz"));
            BigDecimal nor_balance = amountMap.get("col_zj").add(amountMap.get("col_jj")).subtract(amountMap.get("col_jz"));

            DynamicObject rmb = BusinessDataServiceHelper.loadSingle("bd_currency", "id", new QFilter[]{new QFilter("number", QCP.equals, "CNY")});
            if (empty && (amountMap.get("col_zj") == null || amountMap.get("col_zj").compareTo(BigDecimal.ZERO) == 0)
                    && (amountMap.get("col_jj") == null || amountMap.get("col_jj").compareTo(BigDecimal.ZERO) == 0)
                    && (amountMap.get("col_zg") == null || amountMap.get("col_zg").compareTo(BigDecimal.ZERO) == 0)
                    && (amountMap.get("col_jz") == null || amountMap.get("col_jz").compareTo(BigDecimal.ZERO) == 0)) {
                return;
            } else {
                result_dataSetList.add(new Object[]{

                        m.getString("bfgy_projname"),
                        amountMap.get("col_zj"),
                        amountMap.get("col_jj"),
                        amountMap.get("col_zg"),
                        amountMap.get("col_jz"),
                        balance,
                        nor_balance,
                        rmb.get("id"),
                });
            }
        });

        String[] cols = {
                "col_1_01",
                "col_1_02",
                "col_1_03",
                "col_1_04",
                "col_1_05",
                "col_1_06",
                "col_1_07",
                "col_1_08",
        };

        DataType[] datatypes = {
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//5
                DataType.StringType,//6
                DataType.StringType,//7
                DataType.StringType,//8
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);
        return result_dataSet;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        String json = "[" +
                "{\"caption\":\"项目\"," +
                "\"key\":\"col_1_01\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"合同履约成本-直接\"," +
                "\"key\":\"col_1_02\"," +
                "\"link\":\"false\"," +
                "\"dyname\":\"bd_currency\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"合同履约成本-间接\"," +
                "\"key\":\"col_1_03\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"合同履约成本-暂估\"," +
                "\"key\":\"col_1_04\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"合同履约成本-结转\"," +
                "\"key\":\"col_1_05\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"存货余额\"," +
                "\"key\":\"col_1_06\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"账面实际存货余额\"," +
                "\"key\":\"col_1_07\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"币别\"," +
                "\"key\":\"col_1_08\"," +
                "\"link\":\"false\"," +
                "\"dyname\":\"bd_currency\"," +
                "\"type\":\"" + ReportColumn.TYPE_BASEDATA + "\"}," +
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
