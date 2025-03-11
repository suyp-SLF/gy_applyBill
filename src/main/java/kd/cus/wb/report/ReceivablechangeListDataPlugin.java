package kd.cus.wb.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.algo.*;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.report.*;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.botp.BFTrackerServiceHelper;
import kd.drp.ocic.util.DateUtils;
import kd.scmc.plat.business.helper.pricemodel.helper.DataSetHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReceivablechangeListDataPlugin extends AbstractReportListDataPlugin {


    private final static String receivable_table = "ar_finarbill";//应收单
    private final static String exportinvoice_table = "bfgy_wb_exportinvoice";//出口发票
    private final static String revcfmbill_table = "ar_revcfmbill";//收入确认单
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {

        Date start = null;
        Date end = null;
        FilterInfo qfilter = reportQueryParam.getFilter();
        start = qfilter.getDate("date_start");
        end = qfilter.getDate("date_end");
        String[] receivable_select = {
                "id",
                "billno",
                "department.name col_1_01",
                "bfgy_wb_basedatafield.name col_1_06",
                "bfgy_wb_basedatafield.number col_1_12",
                "asstact.name col_1_03",
                "bfgy_textcontract col_1_02",
                "bizdate col_1_10",
                "currency col_1_11",
                "bfgy_reciptcontractno.name col_1_13",
                "unsettleamount",
                "auditdate",
        };
        List<Object[]> result_dataSetList = new ArrayList<>();
        QFilter[] qFilters = new QFilter[2];
        qFilters[1] = new QFilter("billtype.number", QCP.equals, "WBarfin_standard_BT_S_01");
        qFilters[0] = new QFilter("billstatus",QCP.equals,"C");
        DynamicObjectCollection receivable_dys = QueryServiceHelper.query(receivable_table, StringUtils.join(receivable_select, ","), qFilters);
        List<Long> ids = new ArrayList<>();
        for (DynamicObject receivable_dy : receivable_dys) {
            ids.add(receivable_dy.getLong("id"));
            Map<String, HashSet<Long>> revcfmbill_source = BFTrackerServiceHelper.findSourceBills(receivable_table, new Long[]{receivable_dy.getLong("id")});
            HashSet<Long> exportinvoice_ids = revcfmbill_source.get(exportinvoice_table);
            String country = "";
            String fpno = "";
            String xmjl = "";
            String cwjl = "";
            String ckht = "";
            String ckhtno = "";
            BigDecimal sum_middle_amount = BigDecimal.ZERO;
            BigDecimal sum_right_amount = BigDecimal.ZERO;
            BigDecimal sum_middle_hx_amount = BigDecimal.ZERO;
            int due = 0;
            Boolean isnewBuildAmount = false;
            Date auditdate = receivable_dy.getDate("auditdate");
            Date bizdate = receivable_dy.getDate("col_1_10");
            if (null != auditdate && null != start && start.getTime() <= auditdate.getTime() && null != end && end.getTime() > auditdate.getTime())
                isnewBuildAmount = true;
            else if (null != auditdate && null == start && null != end && end.getTime() > auditdate.getTime())
                isnewBuildAmount = true;
            else if (null != auditdate && null != start && start.getTime() <= auditdate.getTime() && null == end)
                isnewBuildAmount = true;
            else if(null != auditdate && null == start && null == end)
                isnewBuildAmount = true;
            else
                isnewBuildAmount = false;

            BigDecimal now_amount = receivable_dy.getBigDecimal("unsettleamount");
            if (null != exportinvoice_ids) {
                DynamicObjectType exportinvoice_type = BusinessDataServiceHelper.newDynamicObject(exportinvoice_table).getDynamicObjectType();
                DynamicObject[] exportinvoices = BusinessDataServiceHelper.load(exportinvoice_ids.toArray(new Long[exportinvoice_ids.size()]), exportinvoice_type);
                for (DynamicObject exportinvoice_item : exportinvoices) {
                    country = exportinvoice_item.getString("bfgy_basedatafield.name");//国家地区
                    fpno = exportinvoice_item.getString("bfgy_invoiceno");//发票号
                    xmjl = exportinvoice_item.getString("bfgy_projectmanager");//项目经理
                    cwjl = exportinvoice_item.getString("bfgy_userfield.name");//财务经理
                    ckhtno = exportinvoice_item.getString("bfgy_excontractno");//出口合同号

                    DynamicObject ckhe_dy = BusinessDataServiceHelper.loadSingle("bfgy_wbexportcontract", "bfgy_projno", new QFilter[]{new QFilter("billno", QCP.equals, ckhtno)});

                    if (null != ckhe_dy){
                        ckht =  ckhe_dy.getString("bfgy_projno");
                    }else {
                        ckht = "";
                    }

                    QFilter[] qFilters_mid = new QFilter[3];
                    qFilters_mid[0] = new QFilter("billno", QCP.equals, receivable_dy.getString("billno"));
                    if (null != start)
                        qFilters_mid[1] = new QFilter("settledate", QCP.large_equals, start);
                    if (null != end)
                        qFilters_mid[2] = new QFilter("settledate", QCP.less_than, end);

                    QFilter[] qFilters_rig = new QFilter[2];
                    qFilters_rig[0] = new QFilter("billno", QCP.equals, receivable_dy.getString("billno"));
                    if (null != end)
                        qFilters_rig[1] = new QFilter("settledate",QCP.large_equals,end);

                    DynamicObjectCollection mid_cols = QueryServiceHelper.query("ar_settlerecord", "entry.settleamt", qFilters_mid);
                    DynamicObjectCollection rig_cols = QueryServiceHelper.query("ar_settlerecord", "entry.e_bfgy_rectypefu,entry.settleamt", qFilters_rig);
                    sum_middle_amount = BigDecimal.ZERO;
                    sum_right_amount = BigDecimal.ZERO;
                    sum_middle_hx_amount = BigDecimal.ZERO;
                    for (DynamicObject dy_mid_col : mid_cols) {
                        sum_middle_amount = sum_middle_amount.add(dy_mid_col.getBigDecimal("entry.settleamt"));
                    }
                    for (DynamicObject dy_rig_col : rig_cols) {
                        sum_right_amount = sum_right_amount.add(dy_rig_col.getBigDecimal("entry.settleamt"));
                        if ("0".equalsIgnoreCase(dy_rig_col.getString("entry.e_bfgy_rectypefu"))){
                            sum_middle_hx_amount = sum_middle_hx_amount.add(dy_rig_col.getBigDecimal("entry.settleamt"));
                        }
                    }
                    due = DateUtils.getDiffDaysIgnoreTime(bizdate,new Date());
                }
            }
//            if (now_amount.compareTo(BigDecimal.ZERO) != 0) {
                result_dataSetList.add(new Object[]{receivable_dy.getString("col_1_01"),
                        receivable_dy.getString("col_1_06"),
                        country,
                        receivable_dy.getString("col_1_03"),
                        receivable_dy.getString("col_1_12"),
                        xmjl,
                        cwjl,
                        receivable_dy.getString("col_1_13"),
                        fpno,
                        StringUtils.isNotBlank(receivable_dy.getString("col_1_10")) ? sdf.format(receivable_dy.getDate("col_1_10")) : "no time",
                        receivable_dy.getString("col_1_11"),
                        isnewBuildAmount ? now_amount.add(sum_right_amount).add(sum_middle_amount) : BigDecimal.ZERO,
                        isnewBuildAmount ? now_amount.add(sum_right_amount).add(sum_middle_amount) : BigDecimal.ZERO ,
                        sum_middle_amount,
                        sum_middle_hx_amount,
                        now_amount.add(sum_right_amount),
                        due + "天"
                });
//            }
        }

        String[] cols = {"col_1_01",
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
                "col_1_12",
                "col_1_13",
                "col_1_14",
                "col_1_15",
                "col_1_16",
                "col_1_17"
        };
        DataType[] datatypes = {DataType.StringType,//1
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
                DataType.StringType,//12
                DataType.StringType,//13
                DataType.StringType,//14
                DataType.StringType,//15
                DataType.StringType,//16
                DataType.StringType//17
        };

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);

        return result_dataSet;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        String json = "[" +
                "{\"caption\":\"军项部门\"," +
                "\"key\":\"col_1_01\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目代码\"," +
                "\"key\":\"col_1_02\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"国别\"," +
                "\"key\":\"col_1_03\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +


                "{\"caption\":\"客户名称\"," +
                "\"key\":\"col_1_04\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目名称\"," +
                "\"key\":\"col_1_05\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目经理\"," +
                "\"key\":\"col_1_06\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"项目财务经理\"," +
                "\"key\":\"col_1_07\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"合同号\"," +
                "\"key\":\"col_1_08\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"发票号\"," +
                "\"key\":\"col_1_09\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +


                "{\"caption\":\"应收确认日期\"," +
                "\"key\":\"col_1_10\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +

                "{\"caption\":\"记账原币别\"," +
                "\"key\":\"col_1_11\"," +
                "\"link\":\"false\"," +
                "\"dyname\":\"bd_currency\"," +
                "\"type\":\"" + ReportColumn.TYPE_BASEDATA + "\"}," +

                "{\"caption\":\"本期期初金额\"," +
                "\"key\":\"col_1_12\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_11\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"本期增加\"," +
                "\"key\":\"col_1_13\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_11\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"本期减少\"," +
                "\"key\":\"col_1_14\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_11\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"其中：因核销负债减少\"," +
                "\"key\":\"col_1_15\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_11\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"本期期末金额（原币）\"," +
                "\"key\":\"col_1_16\"," +
                "\"link\":\"false\"," +
                "\"currency\":\"col_1_11\"," +
                "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"}," +

                "{\"caption\":\"账龄\"," +
                "\"key\":\"col_1_17\"," +
                "\"link\":\"false\"," +
                "\"type\":\"" + ReportColumn.TYPE_TEXT + "\"}," +
                "]";
        JSONArray titleJSON = JSONArray.parseArray(json);
        columns.addAll(makeTitles(titleJSON));
        return columns;
    }

    //构造报表头
    private AbstractReportColumn makeColumn(String caption, String ReportColumnType, String fieldKey, Boolean isLink, String dyname, String currency) {
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
        column.setZeroShow(true);
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
