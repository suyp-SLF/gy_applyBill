package kd.cus.wb.sys.pjdatabase;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.entity.operate.result.IOperateInfo;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.entity.property.LongProp;
import kd.bos.entity.property.PKFieldProp;
import kd.bos.entity.property.VarcharProp;
import kd.bos.fileservice.FileService;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.service.attachment.FilePathService;
import kd.bos.service.business.datamodel.DynamicFormModelProxy;
import kd.bos.servicehelper.AttachmentServiceHelper;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DBServiceHelper;
import kd.bos.servicehelper.MetadataServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.swc.hsas.business.file.FileDBHelper;

import java.math.BigDecimal;
import java.util.*;

/**
 * 项目资料库
 * 北方单据模板|归档操作
 * author suyp
 */
public class PjdatabaseFormPlugin extends AbstractFormPlugin {

    private static final String TOOLBAR_LOGO = "attachmenttool";

    @Override
    public void registerListener(EventObject e) {
        // TODO Auto-generated method stub
        super.registerListener(e);
        Toolbar repairDataBtnBar = this.getControl(TOOLBAR_LOGO);
        repairDataBtnBar.addItemClickListener(this);
    }


    @Override
    public void itemClick(ItemClickEvent evt) {
        // TODO Auto-generated method stub
        super.itemClick(evt);
        evt.getItemKey();
        //进行归档操作
        if ("bfgy_wb_pjbtn".equalsIgnoreCase(evt.getItemKey())) {
            //获得附件分录
            DynamicObject this_dy = this.getModel().getDataEntity(true);
            DynamicObjectType dy_type = this_dy.getDynamicObjectType();
            String billno = this_dy.getString("billno");
            String billName = dy_type.getName();

            DynamicObjectCollection fileEntry = this.getModel().getEntryEntity("attachmententity");
            RequestContext context = RequestContext.get();
            DynamicObjectType pj_type = MetadataServiceHelper.getDataEntityType("bfgy_wb_pjdatabase");
            EntryGrid entryGrid = this.getControl("attachmententity");
            int[] rows = entryGrid.getSelectRows();
            List<DynamicObject> dys = new ArrayList<>();
            Map<Object, String> res = new HashMap<>();
            List<Object> ids = new ArrayList();
            Map<Object,Integer> map = new HashMap<>();
            if (rows.length > 0) {
                for (int i : rows) {
                    PKFieldProp pkProp = (PKFieldProp) pj_type.getPrimaryKey();
                    Map<Class<?>, Object> services = new HashMap<>();
                    DynamicFormModelProxy model = new DynamicFormModelProxy("bfgy_wb_pjdatabase", UUID.randomUUID().toString(), services);
                    model.createNewData();
                    if (pkProp instanceof LongProp) {
                        model.getDataEntity().set(pkProp, DBServiceHelper.genGlobalLongId());
                    } else if (pkProp instanceof VarcharProp) {
                        model.getDataEntity().set(pkProp, DBServiceHelper.genStringId());
                    }
                    DynamicObject new_pj = model.getDataEntity(true);
                Object pkValue = model.getDataEntity().getPkValue();
//                    Object pkValue = pkProp.getValueFast(this_dy);
                    DynamicObject item = fileEntry.get(i);
                    DynamicObjectCollection file = item.getDynamicObjectCollection("attachment");
                    if(file.size() == 0){
                        this.getView().showMessage("归档失败！请选择有附件的进行归档！");
                        return;
                    }
                    List<DynamicObject> listfile = new ArrayList<>();
                    for (DynamicObject dycol : file) {
//                    DynamicObject bffile = BusinessDataServiceHelper.loadSingle(dycol.getDynamicObject("fbasedataid").getPkValue(), "bos_attachment");
                        DynamicObject dytype = new DynamicObject(new_pj.getDynamicObjectCollection("bfgy_attachmentfield").getDynamicObjectType());
                        dytype.set("fbasedataid", dycol.getDynamicObject("fbasedataid"));
                        listfile.add(dytype);
                    }
                    QFilter[] qFilters = {new QFilter("number", QCP.equals, billName)};
                    DynamicObject typename = BusinessDataServiceHelper.loadSingle("bos_objecttype", "name,number", qFilters);

                    Object type = item.get("attachmenttype");
                    String name = item.getString("attachmentnum");
                    String security = item.getString("attachementsecurity");
                    String time = item.getString("attachementtime");
                    String security_p = this_dy.getString("securitytype");
                    String time_p = this_dy.getString("securitytime");

                    //判断是否导入过
                    QFilter[] qfilter_ispj = {new QFilter("bfgy_fileid",QCP.equals,item.getString("id")),
                    new QFilter("bfgy_wb_sourcebill",QCP.equals, billno),
                    new QFilter("bfgy_wb_billname",QCP.equals,billName),
                    new QFilter("bfgy_wb_filename",QCP.equals,name)};
                    DynamicObject the_Same_dy = BusinessDataServiceHelper.loadSingleFromCache("bfgy_wb_pjdatabase", "id,number", qfilter_ispj);

                    if(null != the_Same_dy){
                        fileEntry.get(rows[0]).set("bfgy_pjfile",the_Same_dy);
                        fileEntry.get(rows[0]).set("bfgy_ispj",true);
                        SaveServiceHelper.save(new DynamicObject[]{this_dy});
                        this.getView().showMessage("归档失败！选中文件已归档，对照归档文件：" + the_Same_dy.get("number"));
//                        this.getView().updateView("attachmententity");
                        pkValue = the_Same_dy.getPkValue();
                        res.put(pkValue, "");
                        ids.add(pkValue);
                        map.put(pkValue,i);
                        continue;
//                        return;
                    }
//                    new_pj.set("id",pkValue);
                    new_pj.set("bfgy_wb_sourcebill", billno);//来源单据
                    new_pj.set("bfgy_wb_billname", billName);//单据名称
                    new_pj.set("bfgy_wb_databasetype", type);//类型
                    new_pj.set("bfgy_wb_dpt", null);//部门
                    new_pj.set("bfgy_wb_filename", name);
//                new_pj.set("bfgy_attachmentfield",file);
                    new_pj.getDynamicObjectCollection("bfgy_attachmentfield").addAll(listfile);
                    new_pj.set("bfgy_wb_dpt", this_dy.get("org"));
                    new_pj.set("createorg", this_dy.get("org"));
                    new_pj.set("useorg", this_dy.get("org"));
                    new_pj.set("name", null == typename ? billName : typename.getString("name") + ":" + billno);

                    new_pj.set("securitytype", StringUtils.isBlank(security) ? security_p : security);
                    new_pj.set("securitytime", StringUtils.isBlank(time) ? time_p : time);
                    new_pj.set("bfgy_fileid", item.getString("id"));

                    String pjno = null;
                    if ("bfgy_wb_quotation".equalsIgnoreCase(billName)){//报价单
                        pjno = this_dy.getString("bfgy_projectname");
                    }else if ("bfgy_wbexportcontract".equalsIgnoreCase(billName)){//出口合同
                        pjno = this_dy.getString("bfgy_projno");
                    }else if ("bfgy_proj_wb_pmb".equalsIgnoreCase(billName)){//项目表单
                        pjno = this_dy.getString("bfgy_projno");
                    }else if ("bfgy_wb_proj_taskviewbd".equalsIgnoreCase(billName)){//任务查看
                        pjno = this_dy.getString("bfgy_tprojno");
                    }else if ("bfgy_proj_wb_monthreport".equalsIgnoreCase(billName)){//项目月报
                        pjno = this_dy.getString("bfgy_slprojno");
                    }else if ("bfgy_pm_wb_supinvoice".equalsIgnoreCase(billName)){//供方发票
                        pjno = this_dy.getString("bfgy_slprojno");
                    }else if ("bfgy_pm_wb_proconstract".equalsIgnoreCase(billName)){//采购合同
                        pjno = this_dy.getString("bfgy_sleprojno");
                    }else if ("bfgy_wb_rbprojrep".equalsIgnoreCase(billName)){//研发项目立项报告
                        pjno = this_dy.getString("billno");
                    }else if ("bfgy_pm_wb_receiptslip".equalsIgnoreCase(billName)){//接收单
                        pjno = this_dy.getString("bfgy_pronum");
                    }else if ("bfgy_shipporders".equalsIgnoreCase(billName)){//发运通知单
                        pjno = this_dy.getString("textfield");
                    }else if ("bfgy_wb_exportinvoice".equalsIgnoreCase(billName)){//出口发票
                        pjno = this_dy.getString("bfgy_projectno");
                    }else if ("bfgy_wb_actualinvoice".equalsIgnoreCase(billName)){
                        pjno = this_dy.getString("bfgy_itemnumber");
                    }else if ("bfgy_proj_wb_projclose".equalsIgnoreCase(billName)){
                        pjno = this_dy.getString("bfgy_pcprojno");
                    }
                    new_pj.set("bfgy_pjno",pjno);
                    new_pj.set("status", "C");
                    dys.add(new_pj);

                    res.put(pkValue, "");
                    ids.add(pkValue);
                    map.put(pkValue,i);
                }

//            DynamicObject this1 = BusinessDataServiceHelper.loadSingle(Long.parseLong("1071643549268731904"), "bfgy_wb_pjdatabase");

//            SaveServiceHelper.save(dys.toArray(new DynamicObject[dys.size()]));
                OperationResult saverResult = OperationServiceHelper.executeOperate("save", "bfgy_wb_pjdatabase", dys.toArray(new DynamicObject[dys.size()]));
                List<IOperateInfo> resResultList = saverResult.getAllErrorOrValidateInfo();
                for (IOperateInfo resResult : resResultList) {
                    res.put(resResult.getPkValue(), res.get(resResult.getPkValue()) + resResult.getMessage());
                }

                StringBuffer resmsg = new StringBuffer();
                res.entrySet().stream().forEach(m -> {
                    resmsg.append(m.getValue() + "\r\n");
                });

                List<Map<String, Object>> file = AttachmentServiceHelper.getAttachments(billName, this_dy.getPkValue(), "attachment");
                if (StringUtils.isNotBlank(resmsg.toString())) {
                    this.getView().showMessage(resmsg.toString());
                } else {
                    Map<Object, DynamicObject> pj_dy = BusinessDataServiceHelper.loadFromCache(ids.toArray(), "bfgy_wb_pjdatabase");
                    pj_dy.entrySet().forEach(m->{
                        fileEntry.get(map.get(m.getKey())).set("bfgy_pjfile",m.getValue());
                        fileEntry.get(map.get(m.getKey())).set("bfgy_ispj",true);
                    });
//                    fileEntry.get(rows[0]).set("bfgy_pjfile",pj_dy);
                    SaveServiceHelper.save(new DynamicObject[]{this_dy});
                    this.getView().showMessage("归档成功！");
                    this.getView().updateView("attachmententity");
                    return;
                }
            }else {
                this.getView().showMessage("请至少选择一个文件进行归档！");
                return;
            }
        }
    }
}
