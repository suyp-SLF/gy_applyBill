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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AgeListDataPlugin extends AbstractReportListDataPlugin {
    private final static String receivable_table = "ap_finapbill";//财务应付单

    private List<String> fieldsMap_selects;
    public String timeranges;
    private String timeunit;
    private boolean haveother;
    private boolean havenull;
    private boolean haveno;
    private boolean clearempty;

    private String col_json = "";

    private final Map<String, String> ap_finapbill_fieldsMap = new HashMap<String, String>() {{
        /*财务应付单*/
        put("id", "id");
        put("col_1_01", "currency");
        put("col_1_03", "department.number");
        put("col_1_04", "asstacttype");
        put("col_1_05", "asstact.id");
        put("col_1_06", "bfgy_basedataxm.number");
        put("col_1_07", "unsettleamount");
        put("col_1_08", "bfgy_contracttexttext_wb");
        put("col_1_09", "bizdate");
        put("col_1_10", "department.name");
    }};

    private final Map<String, String> ar_finarbill_fieldsMap = new HashMap<String, String>() {{
        /*财务应收单*/
        put("id", "id");
        put("col_1_01", "currency");
        put("col_1_03", "department.number");
        put("col_1_04", "asstacttype");
        put("col_1_05", "asstact.id");
        put("col_1_06", "bfgy_wb_basedatafield.number");
        put("col_1_07", "unsettleamount");
        put("col_1_08", "bfgy_textcontract");
        put("col_1_09", "invoicedate");
        put("col_1_10", "department.name");
    }};

    private final Map<String, String> ar_revcfmbill_fieldsMap = new HashMap<String, String>() {{
        /*收入确认单*/
        put("id", "id");
        put("col_1_01", "currency");
        put("col_1_03", "bfgy_orgfield.number");
        put("col_1_04", "asstacttype");
        put("col_1_05", "asstact.id");
        put("col_1_06", "bfgy_projectno.number");
        put("col_1_07", "bfgy_amountfield4");
        put("col_1_08", "bfgy_excontractno");
        put("col_1_09", "bizdate");
        put("col_1_10", "bfgy_orgfield.name");
    }};

    private final Map<String, String> cas_paybill_fieldsMap = new HashMap<String, String>() {{
        /*财务付款单*/
        put("id", "id");
        put("col_1_01", "currency");
        put("col_1_03", "applyorg.number");
        put("col_1_04", "itempayeetype");
        put("col_1_05", "itempayee.id");
        put("col_1_06", "bfgy_basedataxmjc_wb.number");
        put("col_1_07", "entry.e_unsettledamt");
        put("col_1_08", "bfgy_basedcontractdata");
        put("col_1_09", "bizdate");
        put("col_1_10", "openorg.name");
    }};

    private final Map<String, String> cas_recbill_fieldsMap = new HashMap<String, String>() {{
        /*收款单*/
        put("id", "id");
        put("col_1_01", "currency");
        put("col_1_03", "bfgy_org.number");
        put("col_1_04", "payertype");
        put("col_1_05", "bfgy_rncustomer.id");
        put("col_1_06", "bfgy_projbd.number");
        put("col_1_07", "entry.e_unsettledamt");
        put("col_1_08", "contractno");
        put("col_1_09", "bizdate");
        put("col_1_10", "bfgy_org.name");
    }};

//    private final String[] receivable_select_ap_finapbill = {//财务应付单
//            "id",
//            "paymentcurrency col_1_01",//
//            "department.name col_1_03",
//            "asstacttype col_1_04",
//            "asstact.name col_1_05",
//            "bfgy_basedataxm.name col_1_06",
//            "unsettleamount col_1_07",
//            "bfgy_contracttexttext_wb col_1_08",
//            "auditdate col_1_09"
//    };

//    private final String[] receivable_select_ar_finarbill = {//应收单
//            "id",
//            "currency col_1_01",
//            "department.name col_1_03",
//            "asstacttype col_1_04",
//            "asstact.name col_1_05",
//            "bfgy_wb_basedatafield.name col_1_06",
//            "unsettleamount col_1_07",
//            "bfgy_textcontract col_1_08",
//            "auditdate col_1_09"
//    };

//    private final String[] receivable_select_ar_revcfmbill = {//收入确认单
//
//    };

//    private final String[] receivable_select_cas_paybill = {//财务付款单
//            "id",
//            "currency col_1_01",//
//            "bfgy_orgfxdep.name col_1_03",
//            "'bd_customer' col_1_04",
//            "providersupplier.name col_1_05",
//            "bfgy_basedataxm_wb col_1_06",
//            "entry.e_unsettledamt col_1_07",
//            "bfgy_basedcontractdata col_1_08",
//            "auditdate col_1_09"
//    };

//    private final String[] receivable_select_cas_recbill = {//收款处理
//            "id",
//            "currency col_1_01",
//            "bfgy_org.name col_1_03",
//            "payertype col_1_04",
//            "bfgy_rncustomer.name col_1_05",
//            "bfgy_projbd.name col_1_06",
//            "entry.e_unsettledamt col_1_07",
//            "contractno col_1_08",
//            "auditdate col_1_09"
//    };

    private final Map<String, Map<String, String>> selectMap = new HashMap<String, Map<String, String>>() {{
        put("ap_finapbill", ap_finapbill_fieldsMap);
        put("ar_finarbill", ar_finarbill_fieldsMap);
        put("ar_revcfmbill", ar_revcfmbill_fieldsMap);
        put("cas_paybill", cas_paybill_fieldsMap);
        put("cas_recbill", cas_recbill_fieldsMap);
    }};

    public final static Map<String, String> billname_map = new HashMap<String, String>() {{
        put("ap_finapbill", "财务应付单");
        put("ar_finarbill", "财务应收单");
        put("ar_revcfmbill", "收入确认单");
        put("cas_paybill", "财务付款单");
        put("cas_recbill", "收款处理");
    }};

    public final static Map<String, String> viewMap = new HashMap<String, String>() {{
        put("ap_finapbill", "bfgy_wb_ap_finapbill_lay");
        put("ar_finarbill", "bfgy_wb_finarmenu");
        put("ar_revcfmbill", "bfgy_wb_receiconfibill");
        put("cas_paybill", "bfgy_wb_cas_paybill_lay");
        put("cas_recbill", "cas_recbill");
    }};

    private static Map<String, Map<String, List<Object>>> Allmap = new HashMap<>();
    List<List<Object>> Singlemap = new ArrayList<>();

    private static List<List<Object>> col_0 = new ArrayList<>();
    private static List<List<Object>> col_1 = new ArrayList<>();
    private static List<List<Object>> col_2 = new ArrayList<>();
    private static List<List<Object>> col_3 = new ArrayList<>();
    private static List<List<Object>> col_4 = new ArrayList<>();
    private static List<List<Object>> col_5 = new ArrayList<>();

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {

        FilterInfo qfilter = reportQueryParam.getFilter();
        /*项目选择，用来获得项目*/
        DynamicObjectCollection projects = qfilter.getDynamicObjectCollection("bfgy_projects");//项目
        DynamicObject org = qfilter.getDynamicObject("bfgy_orgfield");//部门

        String betweentype = qfilter.getString("bfgy_betweentype");//往来户类型

        DynamicObject customer = qfilter.getDynamicObject("bfgy_customer");//客户
        DynamicObject supplier = qfilter.getDynamicObject("bfgy_supplier");//供应商
        DynamicObject user = qfilter.getDynamicObject("bfgy_user");//人员
        DynamicObject company = qfilter.getDynamicObject("bfgy_company");//公司
        Date yearmonth = qfilter.getDate("bfgy_dateyearmonth");//年月
        DynamicObjectCollection billtypes = qfilter.getDynamicObjectCollection("bfgy_billtypes");//单据类型
        String mulstatus = qfilter.getString("bfgy_mulstatus");//单据状态

        String range = qfilter.getString("bfgy_range");

        String othername = qfilter.getString("bfgy_othername");

        Date startDate = qfilter.getDate("bfgy_startdate");

        haveother = qfilter.getBoolean("bfgy_haveother");//是否含有其他时间合计
        havenull = qfilter.getBoolean("bfgy_havenull");//bfgy_havenull
        haveno = qfilter.getBoolean("bfgy_haveno");//bfgy_haveno

        clearempty = qfilter.getBoolean("bfgy_clearempty");

        timeunit = qfilter.getString("bfgy_unit");//时间单位
        timeranges = qfilter.getString("bfgy_timeranges_text");//时间段范围

        fieldsMap_selects = selectMap.get(range).entrySet().stream().map(i -> i.getValue() + " " + i.getKey()).collect(Collectors.toList());

        Allmap = new HashMap<>();
        List<Object[]> result_dataSetList = new ArrayList<>();

        QFilter[] qFilters = new QFilter[10];
        if (billtypes != null) {
            List<String> billtypes_number_list = billtypes.stream().map(i -> i.getString("number")).collect(Collectors.toList());
            if("ap_finapbill".equalsIgnoreCase(range)) {
            	qFilters[0] = new QFilter("billtypeid.number", QCP.in, billtypes_number_list);//单据类型过滤
            }else if("ar_finarbill".equalsIgnoreCase(range)) {
            	qFilters[0] = new QFilter("billtype.number", QCP.in, billtypes_number_list);//单据类型过滤
            }else if("ar_revcfmbill".equalsIgnoreCase(range)) {
            	qFilters[0] = new QFilter("billtype.number", QCP.in, billtypes_number_list);//单据类型过滤
            }else if("cas_paybill".equalsIgnoreCase(range)) {
            	qFilters[0] = new QFilter("billtype.number", QCP.in, billtypes_number_list);//单据类型过滤
            }else if("cas_recbill".equalsIgnoreCase(range)) {
            	qFilters[0] = new QFilter("bfgy_billtype.number", QCP.in, billtypes_number_list);//单据类型过滤
            }
        }

        if (projects != null && projects.size() > 0) {
            List<String> projects_number_list = projects.stream().map(i -> i.getString("number")).collect(Collectors.toList());
            qFilters[1] = new QFilter(selectMap.get(range).get("col_1_06"), QCP.in, projects_number_list);
        }

        if (org != null && StringUtils.isNotBlank(org.getString("number"))) {
            qFilters[2] = new QFilter(selectMap.get(range).get("col_1_03"), QCP.equals, org.getString("number"));
        }

        if ("supplier".equalsIgnoreCase(betweentype) && supplier != null) {
            qFilters[3] = new QFilter(selectMap.get(range).get("col_1_05"), QCP.equals, supplier.getString("name"));
        } else if ("customer".equalsIgnoreCase(betweentype) && customer != null) {
            qFilters[3] = new QFilter(selectMap.get(range).get("col_1_05"), QCP.equals, customer.getString("name"));
        } else if ("user".equalsIgnoreCase(betweentype) && user != null) {
            qFilters[3] = new QFilter(selectMap.get(range).get("col_1_05"), QCP.equals, user.getString("name"));
        } else if ("company".equalsIgnoreCase(betweentype) && company != null) {
            qFilters[3] = new QFilter(selectMap.get(range).get("col_1_05"), QCP.equals, company.getString("name"));
        }

        if (yearmonth != null) {
            SimpleDateFormat monthsdf = new SimpleDateFormat("yyyy-MM-01 00:00:00");
            yearmonth = monthsdf.parse(monthsdf.format(yearmonth));
            Calendar calyearmonth = Calendar.getInstance();
            calyearmonth.setTime(yearmonth);//设置起时间
            calyearmonth.add(Calendar.MONTH, 1);
            Date yearmonthplus = calyearmonth.getTime();
            qFilters[4] = new QFilter("auditdate", QCP.large_equals, yearmonth);
            qFilters[5] = new QFilter("auditdate", QCP.less_than, yearmonthplus);
        }

        if (null != mulstatus) {
            List<String> mulstatus_number_list = new ArrayList<>();
            String[] mulstatus_value = mulstatus.split(",");
            for (String item : mulstatus_value) {
                if (StringUtils.isNotBlank(item))
                    mulstatus_number_list.add(item);
            }
            qFilters[6] = new QFilter("billstatus", QCP.in, mulstatus_number_list);
        }
        
        if("ar_revcfmbill".equalsIgnoreCase(range)) {
            qFilters[8] = new QFilter((String) selectMap.get(range).get("col_1_07"), QCP.not_equals, 0);
        }else if("cas_paybill".equalsIgnoreCase(range)) {//paymenttype
        	qFilters[7] = new QFilter("paymenttype.ispartpayment", QCP.equals, true);
        }

        DynamicObjectCollection receivable_cols = QueryServiceHelper.query(range, StringUtils.join(fieldsMap_selects, ","), qFilters);
        Map<String, String> orgnameMap = new HashMap<>();
        receivable_cols.forEach(dynamicObject -> {
            orgnameMap.put(dynamicObject.getString("col_1_03"), dynamicObject.getString("col_1_10"));
        });
        Map<String, List<DynamicObject>> groupids = receivable_cols.stream().collect(Collectors.groupingBy(i -> i.getString("col_1_01") + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_03")) ? "null" : i.getString("col_1_03")) + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_04")) ? "null" : i.getString("col_1_04")) + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_05")) ? "null" : i.getString("col_1_05")) + "@_@" +
                (StringUtils.isBlank(i.getString("col_1_06")) ? "null" : i.getString("col_1_06")) + "@_@"));

        col_0 = new ArrayList<>();
        col_1 = new ArrayList<>();
        col_2 = new ArrayList<>();
        col_3 = new ArrayList<>();
        col_4 = new ArrayList<>();
        col_5 = new ArrayList<>();
        Iterator<Map.Entry<String, List<DynamicObject>>> iter = groupids.entrySet().iterator();

        List<String> amount_fields = new ArrayList<>();
        JSONArray jsonarray = JSONArray.parseArray(timeranges);

        if (havenull)
            amount_fields.add("时间为空");
        if (haveno)
            amount_fields.add("未分组");

        if (jsonarray != null) {
            for (int i = 0; i < jsonarray.size(); i++) {
                amount_fields.add(jsonarray.getJSONObject(i).getString("groupname"));
            }
        }
        if (haveother) {
            if (StringUtils.isNotBlank(othername)) {
                amount_fields.add(othername);
            } else {
                amount_fields.add("未设置其他名称");
            }
        }


        while (iter.hasNext()) {
            Map.Entry<String, List<DynamicObject>> item = iter.next();
            String key = item.getKey();
            String[] temp = item.getKey().split("@_@");
            String currency = temp[0];
            String department = orgnameMap.get(temp[1]);
            String asstacttype = temp[2];
            String asstact = temp[3];
            String pjno = temp[4];
//            String con = temp[5];
            String country = " ";
            DynamicObject proje_dy = null;
            if(!"null".equalsIgnoreCase(pjno))
            	proje_dy = BusinessDataServiceHelper.loadSingle("bfgy_proj_wb_pmb", "bfgy_projcountry.name", new QFilter[]{new QFilter("bfgy_projno", QCP.equals, pjno)});

            if (null != proje_dy) {
                country = proje_dy.getString("bfgy_projcountry.name");
            }


            String asstacttype_value = "无";
            DynamicObject ass_dy = null;
            
            if(("bd_customer".equalsIgnoreCase(asstacttype) || 
            		"bd_supplier".equalsIgnoreCase(asstacttype) || 
            		"bos_user".equalsIgnoreCase(asstacttype)) && !"null".equalsIgnoreCase(asstact)) {
            	 ass_dy = QueryServiceHelper.queryOne(asstacttype, "name", new QFilter[]{new QFilter("id", QCP.equals, asstact)});
            	 if(ass_dy != null) {
            		 asstact = ass_dy.getString("name");
            	 }else {
            		 asstact = "未找到：" + asstact;
            	 }
            }else {
            	asstact = "无";
            }
            
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

            BigDecimal col_temp_0D = BigDecimal.ZERO;
            List<Object> col_temp_0 = new ArrayList<>();
            Singlemap = new ArrayList<>();

            Map<String, List<DynamicObject>> time_groups = item.getValue().stream().collect(Collectors.groupingBy(i -> groupTime(i.getDate("col_1_09"), startDate)));

            Map<String, Integer> field_index_map = new HashMap<>();
            for (int i = 0; i < amount_fields.size(); i++) {
                field_index_map.put(amount_fields.get(i), i);
            }

            Object[] field_index_cols = new Object[amount_fields.size()];
            Map<String, List<Object>> col_ids = new HashMap<>();
            col_ids.clear();
            time_groups.entrySet().forEach(m -> {
                List<DynamicObject> dys = m.getValue();
                BigDecimal sumamount = BigDecimal.ZERO;
                List<Object> ids = new ArrayList<>();
                for (DynamicObject dy : dys) {
                    String amount_field_name = (String) selectMap.get(range).get("col_1_07");
                    String[] amount_field_names = amount_field_name.split("\\.");
                    if (false && amount_field_names.length > 0) {
                        String entry_field = amount_field_names[0];
                        String col_field = amount_field_names[1];
                        DynamicObjectCollection entry_cols = dy.getDynamicObjectCollection(entry_field);
                        BigDecimal amount = BigDecimal.ZERO;
                        for (DynamicObject entry_col : entry_cols) {
                            amount = amount.add(entry_col.getBigDecimal(col_field));
                        }
                        sumamount = sumamount.add(amount);
                    } else {
                        BigDecimal amount = dy.getBigDecimal("col_1_07");
                        sumamount = sumamount.add(amount);
                    }
                    Object id = dy.get("id");
                    ids.add(id);
                }
                if (null != field_index_map.get(m.getKey())) {
                    field_index_cols[(Integer) field_index_map.get(m.getKey())] = sumamount;
                    col_ids.put("sel_0_" + (Integer) field_index_map.get(m.getKey()), ids);
                }
            });

            Boolean isempty = IsEmpty(field_index_cols);

            if (!clearempty || isempty) {
                result_dataSetList.add(ArrayUtils.addAll(new Object[]{key,
                        currency,
                        country,
                        department,
                        asstacttype_value,
                        asstact,
                        pjno,
                }, field_index_cols));

                Allmap.put(key, col_ids);
            }
        }

        List<String> col_names = new ArrayList<>();
        List<DataType> select_datatype = new ArrayList<>();
        for (int i = 0; i < amount_fields.size(); i++) {
            col_names.add("sel_0_" + i);
            select_datatype.add(DataType.StringType);

            col_json += "{\"caption\":\"" + amount_fields.get(i) + "\"," +
                    "\"key\":\"" + "sel_0_" + i + "\"," +
                    "\"link\":\"true\"," +
                    "\"currency\":\"col_1_01\"," +
                    "\"type\":\"" + ReportColumn.TYPE_AMOUNT + "\"},";
        }


        String[] cols = ArrayUtils.addAll(new String[]{"key",
                "col_1_01",
                "col_1_02",
                "col_1_03",
                "col_1_04",
                "col_1_05",
                "col_1_06",
        }, col_names.toArray(new String[col_names.size()]));


        DataType[] datatypes = ArrayUtils.addAll(new DataType[]{DataType.StringType,//
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//5
                DataType.StringType,//6
        }, select_datatype.toArray(new DataType[select_datatype.size()]));

        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);

        return result_dataSet;
    }

    private Boolean IsEmpty(Object[] array){
        boolean isempty = true;
        for(int i = 0; i < array.length; i++){
        	Object item = array[i];
        	if(!havenull && i == 0) {
        		continue;
        	}
        	if(!haveno && i == 1) {
        		continue;
        	}
        	
            if (item != null && BigDecimal.ZERO.compareTo((BigDecimal) item) != 0){
                return false;
            }
        }
        return isempty;
    }

    @Override
    public List<AbstractReportColumn> getColumns(List<AbstractReportColumn> columns) throws Throwable {
        String json = "[" +
                "{\"caption\":\"标记\"," +
                "\"key\":\"key\"," +
                "\"link\":\"false\"," +
                "\"isHide\":\"true\"," +
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

                col_json +

                "]";
        JSONArray titleJSON = JSONArray.parseArray(json);
        columns.addAll(makeTitles(titleJSON));
        return columns;
    }

    //构造报表头
    private AbstractReportColumn makeColumn(String caption, String ReportColumnType, String fieldKey, Boolean
            isLink, String dyname, String currency, Boolean isHide) {
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

        if (null != isHide) {
            column.setHide(isHide);
        }
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
                    item.getString("currency"),
                    item.getBoolean("isHide")));
        }
        return titles;
    }

    public static List<Object> getColIds(String row, String col) {
        List<Object> ids = new ArrayList<>();
        ids = Allmap.get(row).get(col);
        return ids;
    }

    private String groupTime(Date time, Date startDate) {

        if (startDate == null)
            startDate = new Date();

        if (time != null) {
            int unit_cal;
            JSONArray jsonarray = JSONArray.parseArray(timeranges);
            if (jsonarray != null && jsonarray.size() > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                if ("day".equalsIgnoreCase(timeunit)) {
                    unit_cal = Calendar.DATE;
                } else if ("month".equalsIgnoreCase(timeunit)) {
                    unit_cal = Calendar.MONTH;
                } else {
                    unit_cal = Calendar.YEAR;
                }
                for (int i = 0; i < jsonarray.size(); i++) {
                    Date end_time = calendar.getTime();
                    calendar.add(unit_cal, jsonarray.getJSONObject(i).getInteger("num") * -1);
                    Date start_time = calendar.getTime();
                    if (jsonarray.getJSONObject(i).getInteger("num") != null && StringUtils.isNotBlank(jsonarray.getJSONObject(i).getString("groupname"))) {
                        if (time.getTime() >= start_time.getTime() && time.getTime() <= end_time.getTime()) {
                            return jsonarray.getJSONObject(i).getString("groupname");
                        }
                        if (haveother && i == jsonarray.size() - 1 && time.getTime() < start_time.getTime()) {
                            return "其他";
                        }
                    } else {
                        return "未分组";
                    }
                }
            }
        } else {
            return "时间为空";
        }
        return "未分组";
    }
}
