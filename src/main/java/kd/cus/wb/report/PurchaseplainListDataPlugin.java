package kd.cus.wb.report;

import kd.bos.algo.*;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicCollectionProperty;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.report.AbstractReportListDataPlugin;
import kd.bos.entity.report.FilterInfo;
import kd.bos.entity.report.ReportQueryParam;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.QueryServiceHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PurchaseplainListDataPlugin extends AbstractReportListDataPlugin {


    private String[] FIELD = {
            "id bfgy_idf",
            "suppliers bfgy_suppliers",
            "billno bfgy_contractno",
            "bfgy_pcname bfgy_contractname",
            "bfgy_slecurrency bfgy_contractcurrency",
            "bfgy_sleprojno bfgy_pjno",
            "bfgy_orgfield bfgy_exeorg",
            "bfgy_amountnature",
            "bfgy_payon",
            "bfgy_plainlinenum",
            "bfgy_plainlinetype",
            "bfgy_oplogo",
            "bfgy_missionname",
            "bfgy_missionno",
            "bfgy_esamount",
            "bfgy_esdate",
            "bfgy_esday",
            "bfgy_abstract",
            "bfgy_acdate",
            "bfgy_textfield",
            "bfgy_pjstartdate",
            "bfgy_pjenddate",
            "bfgy_ljsqamount",
            "bfgy_appdate",
            "bfgy_ljamount",
            "bfgy_syamount",
//            "bfgy_acamount",
//            "bfgy_reamount"
    };

    @Override
    public DataSet query(ReportQueryParam reportQueryParam, Object o) throws Throwable {

        FilterInfo qfilter = reportQueryParam.getFilter();
        String pjsearch = qfilter.getString("bfgy_pjsearch");//项目
        String pursearch = qfilter.getString("bfgy_pursearch");//采购合同
        String plainsearch = qfilter.getString("bfgy_plaintextsearch");//计划行类型
        DynamicObject supplier = qfilter.getDynamicObject("bfgy_supplierfield");//供应商
        QFilter qfilter_pj = null;
        QFilter qfilter_purcon = null;
        QFilter qfilter_sup = null;
        if (StringUtils.isNotBlank(pjsearch)) {
            qfilter_pj = new QFilter("bfgy_sleprojno", QCP.like, "%" + pjsearch + "%");
        }
        if (StringUtils.isNotBlank(pursearch)) {
            qfilter_purcon = new QFilter("billno", QCP.like, "%" + pursearch + "%");
            qfilter_purcon = qfilter_purcon.or(new QFilter("bfgy_pcname",QCP.like,"%" + pursearch + "%"));
        }
        if (StringUtils.isNotBlank(supplier)) {
            qfilter_sup  = new QFilter("bfgy_supsleconentity.bfgy_pcsuppliers.id", QCP.equals, supplier.getPkValue());
        }
        QFilter[] qfilter_all = {qfilter_pj, qfilter_purcon, qfilter_sup};

        String[] jsselects = {
                "id",
                "billno",//采购合同编号
                "bfgy_pcname",//采购合同名称
                "bfgy_slecurrency",//币别
                "bfgy_sleprojno",//项目号
                "bfgy_orgfield",//执行部门
                "bfgy_supsleconentity.bfgy_pcsuppliers suppliers",
                "bfgy_recplanentity.seq bfgy_plainlinenum",
                "bfgy_recplanentity.bfgy_recdeleted bfgy_oplogo",//操作标记
//                "bfgy_recplanentity.bfgy_recid",//接收计划id
                "bfgy_recplanentity.bfgy_rptasknamerec bfgy_missionname",//任务名称
                "bfgy_recplanentity.bfgy_rptasknorec bfgy_missionno",//任务编号
                "bfgy_recplanentity.bfgy_planrecamount bfgy_esamount",//预计接收金额
                "bfgy_recplanentity.bfgy_planrecdate bfgy_esdate",//预计接收日期
                "bfgy_recplanentity.bfgy_taskplandate bfgy_esday",//预计接收日期比任务日期延迟天数
                "bfgy_recplanentity.bfgy_rppsummaryrec bfgy_abstract",//摘要（名称）
                "bfgy_recplanentity.bfgy_actrecdate bfgy_acdate",//实际接收日期
                "bfgy_recplanentity.bfgy_connor bfgy_textfield",//合同编号
                "bfgy_recplanentity.bfgy_plstdater bfgy_pjstartdate",//项目任务开始时间
                "bfgy_recplanentity.bfgy_rectaskenddate bfgy_pjenddate",//项目任务完成时间

//                "bfgy_recplanentity.bfgy_actrecamount bfgy_acamount",//实际接收金额
//                "bfgy_recplanentity.bfgy_remainrecamt bfgy_reamount",//剩余可接收金额
        };

        String[] fkselects = {
                "id",
                "billno",//采购合同编号
                "bfgy_pcname",//采购合同名称
                "bfgy_slecurrency",//币别
                "bfgy_sleprojno",//项目号
                "bfgy_orgfield",//执行部门
                "bfgy_supsleconentity.bfgy_pcsuppliers suppliers",
                "bfgy_recpayplanentity.seq bfgy_plainlinenum",
                "bfgy_recpayplanentity.bfgy_paydeleted bfgy_oplogo",//操作标记
                "bfgy_recpayplanentity.bfgy_rptaskname bfgy_missionname",//任务名称
                "bfgy_recpayplanentity.bfgy_rptaskno bfgy_missionno",//任务编号
                "bfgy_recpayplanentity.bfgy_planpayamount bfgy_esamount",//预计接收金额
                "bfgy_recpayplanentity.bfgy_planpaydate bfgy_esdate",//预计付款日期
                "bfgy_recpayplanentity.bfgy_taskpaydate bfgy_esday",//预计接收日期比任务日期延迟天数
                "bfgy_recpayplanentity.bfgy_rppsummary bfgy_abstract",//摘要（名称）
                "bfgy_recpayplanentity.bfgy_actpaydate bfgy_acdate",//实际付款日期
                "bfgy_recpayplanentity.bfgy_conno bfgy_textfield",//合同编号
                "bfgy_recpayplanentity.bfgy_plstdate bfgy_pjstartdate",//项目任务开始时间
                "bfgy_recpayplanentity.bfgy_paytaskenddate bfgy_pjenddate",//项目任务完成时间
                "bfgy_recpayplanentity.bfgy_paynature bfgy_amountnature",//款项性质
                "bfgy_recpayplanentity.bfgy_payratio bfgy_payon",//付款比例

                "bfgy_recpayplanentity.bfgy_acapplyamount bfgy_ljsqamount",//累计申请金额
                "bfgy_recpayplanentity.bfgy_rpapplydate bfgy_appdate",//申请日期
                "bfgy_recpayplanentity.bfgy_acpayamount bfgy_ljamount",//累计付款金额
                "bfgy_recpayplanentity.bfgy_amountfield bfgy_syamount",//剩余付款金额
        };


        DynamicObjectCollection jsproconstracts = QueryServiceHelper.query(this.getClass().getName(), "bfgy_pm_wb_proconstract", StringUtils.join(jsselects, ","), qfilter_all, null);
        DynamicObjectCollection fkproconstracts = QueryServiceHelper.query(this.getClass().getName(), "bfgy_pm_wb_proconstract", StringUtils.join(fkselects, ","), qfilter_all, null);

        List<Object[]> result_dataSetList = new ArrayList<>();

        if ("js".equalsIgnoreCase(plainsearch)) {
            fkproconstracts.clear();
        } else if ("fk".equalsIgnoreCase(plainsearch)) {
            jsproconstracts.clear();
        }

        for(DynamicObject jsproconstract:jsproconstracts){
            result_dataSetList.add(new Object[]{
                    jsproconstract.getString("id"),
                    jsproconstract.getString("billno"),
                    jsproconstract.getString("bfgy_pcname"),
                    jsproconstract.getString("bfgy_slecurrency"),
                    jsproconstract.getString("bfgy_sleprojno"),
                    jsproconstract.getString("bfgy_orgfield"),
                    jsproconstract.getString("suppliers"),
                    jsproconstract.getString("bfgy_plainlinenum"),
                    "接收",
                    jsproconstract.getString("bfgy_oplogo"),
                    jsproconstract.getString("bfgy_missionname"),
                    jsproconstract.getString("bfgy_missionno"),
                    jsproconstract.getString("bfgy_esamount"),
                    jsproconstract.getDate("bfgy_esdate"),
                    jsproconstract.getString("bfgy_esday"),
                    jsproconstract.getString("bfgy_abstract"),
                    jsproconstract.getDate("bfgy_acdate"),
                    jsproconstract.getString("bfgy_textfield"),
                    jsproconstract.getDate("bfgy_pjstartdate"),
                    jsproconstract.getDate("bfgy_pjenddate"),
                    "",
                    "",
                    "",
                    null,
                    "",
                    "",
            });
        }

        for(DynamicObject fkproconstract:fkproconstracts){
            result_dataSetList.add(new Object[]{
                    fkproconstract.getString("id"),
                    fkproconstract.getString("billno"),
                    fkproconstract.getString("bfgy_pcname"),
                    fkproconstract.getString("bfgy_slecurrency"),
                    fkproconstract.getString("bfgy_sleprojno"),
                    fkproconstract.getString("bfgy_orgfield"),
                    fkproconstract.getString("suppliers"),
                    fkproconstract.getString("bfgy_plainlinenum"),
                    "付款",
                    fkproconstract.getString("bfgy_oplogo"),
                    fkproconstract.getString("bfgy_missionname"),
                    fkproconstract.getString("bfgy_missionno"),
                    fkproconstract.getString("bfgy_esamount"),
                    fkproconstract.getString("bfgy_esdate"),
                    fkproconstract.getString("bfgy_esday"),
                    fkproconstract.getString("bfgy_abstract"),
                    fkproconstract.getDate("bfgy_acdate"),
                    fkproconstract.getString("bfgy_textfield"),
                    fkproconstract.getDate("bfgy_pjstartdate"),
                    fkproconstract.getDate("bfgy_pjenddate"),
                    fkproconstract.getString("bfgy_amountnature"),
                    fkproconstract.getString("bfgy_payon"),
                    fkproconstract.getString("bfgy_ljsqamount"),
                    fkproconstract.getDate("bfgy_appdate"),
                    fkproconstract.getString("bfgy_ljamount"),
                    fkproconstract.getString("bfgy_syamount"),
            });
        }

        String[] cols = {
                "id",//1
                "billno",
                "bfgy_pcname",
                "bfgy_slecurrency",
                "bfgy_sleprojno",
                "bfgy_orgfield",
                "suppliers",
                "bfgy_plainlinenum",
                "bfgy_plainlinetype",
                "bfgy_oplogo",
                "bfgy_missionname",
                "bfgy_missionno",
                "bfgy_esamount",
                "bfgy_esdate",
                "bfgy_esday",
                "bfgy_abstract",
                "bfgy_acdate",
                "bfgy_textfield",
                "bfgy_pjstartdate",
                "bfgy_pjenddate",
                "bfgy_amountnature",
                "bfgy_payon",
                "bfgy_ljsqamount",
                "bfgy_appdate",
                "bfgy_ljamount",
                "bfgy_syamount",
        };

        DataType[] datatypes = {
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.StringType,//1
                DataType.DateType,//2
                DataType.StringType,//3
                DataType.StringType,//4
                DataType.DateType,//1
                DataType.StringType,//2
                DataType.DateType,//3
                DataType.DateType,//4
                DataType.StringType,//1
                DataType.StringType,//2
                DataType.StringType,//3
                DataType.DateType,//4
                DataType.StringType,//4
                DataType.StringType,//4
        };


        RowMeta rowMeta = new RowMeta(cols, datatypes);
        Algo algo = Algo.create(this.getClass().getName());
        DataSet result_dataSet = algo.createDataSet(result_dataSetList, rowMeta);

//        jsproconstract = jsproconstract.addField("'接收'", "bfgy_plainlinetype");
//        jsproconstract = jsproconstract.addNullField("bfgy_syamount");
//        jsproconstract = jsproconstract.addNullField("bfgy_ljamount");
//        jsproconstract = jsproconstract.addNullField("bfgy_appdate");
//        jsproconstract = jsproconstract.addNullField("bfgy_ljsqamount");
//        jsproconstract = jsproconstract.addNullField("bfgy_payon");
//        jsproconstract = jsproconstract.addNullField("bfgy_amountnature");

//        fkproconstract = fkproconstract.addField("'付款'", "bfgy_plainlinetype");
////        fkproconstract = fkproconstract.addNullField("bfgy_reamount");
////        fkproconstract = fkproconstract.addField("null","bfgy_acamount");
//        DataSet result = null;

//        return result.select(FIELD);
        return result_dataSet.select(FIELD);
    }
}
