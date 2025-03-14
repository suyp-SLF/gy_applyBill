package kd.cus.wb.newreport;

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
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 高质量发展奖报表
 * @author suyp
 *
 */
public class HighawardListDataPlugin extends AbstractReportListDataPlugin {
    private static final String PROJECT_TABLE = "bfgy_proj_wb_pmb";

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {
        FilterInfo qfilter = reportQueryParam.getFilter();
        DynamicObjectCollection projects = qfilter.getDynamicObjectCollection("bfgy_projectsfield");//项目
        Date startdate = qfilter.getDate("startdate");
        Date enddate = qfilter.getDate("enddate");

        QFilter[] qFilters = new QFilter[2];

        if (projects != null && projects.size() > 0){
            List<String> projects_number_list = projects.stream().map(i -> i.getString("number")).collect(Collectors.toList());
            qFilters[0] = new QFilter("bfgy_projno", QCP.in, projects_number_list);
        }

        String [] selects = {
                "id col_1_01",
                "bfgy_projno col_1_02",//项目号
                "bfgy_projname col_1_03"//项目名称
        };

        DynamicObjectCollection receivable_cols = QueryServiceHelper.query(PROJECT_TABLE, StringUtils.join(selects, ","), qFilters);

        List<Object[]> result_dataSetList = new ArrayList<>();

        for (DynamicObject receivable_col: receivable_cols) {
            result_dataSetList.add(new Object[]{
                    receivable_col.getString("col_1_01"),
                    receivable_col.getString("col_1_02"),
                    receivable_col.getString("col_1_03"),
                    receivable_col.getString("col_1_03") + "预期/实际利润计算表"
            });
        }

        String[] cols = {
                "col_1_01",
                "col_1_02",
                "col_1_03",
                "col_1_04",
        };

        DataType[] datatypes = {
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);
        return result_dataSet;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        String json = "[" +
                "{\"caption\":\"ID\"," +
                "\"key\":\"col_1_01\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目号\"," +
                "\"key\":\"col_1_02\"," +
                "\"link\":\"false\"," +
                "\"dyname\":\"bd_currency\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目名称\"," +
                "\"key\":\"col_1_03\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"预期/实际利润计算表\"," +
                "\"key\":\"col_1_04\"," +
                "\"link\":\"true\"," +
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
