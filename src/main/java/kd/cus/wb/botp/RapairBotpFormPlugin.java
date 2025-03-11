package kd.cus.wb.botp;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.EntityType;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.botp.runtime.TableDefine;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.entity.datamodel.events.PropertyChangedArgs;
import kd.bos.entity.property.EntryProp;
import kd.bos.exception.KDException;
import kd.bos.form.IFormView;
import kd.bos.form.control.Button;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.field.ComboEdit;
import kd.bos.form.field.ComboItem;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.MetadataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.botp.BFTrackerServiceHelper;
import kd.bos.servicehelper.botp.ConvertServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.fi.arapcommon.helper.BOTPHelper;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RapairBotpFormPlugin extends AbstractFormPlugin {
    private static String SRC_COMBO = "bfgy_src_combo";
    private static String DST_COMBO = "bfgy_dst_combo";
    private static String SRC_ENTRY_COMBO = "bfgy_src_entry_combo";
    private static String DST_ENTRY_COMBO = "bfgy_dst_entry_combo";
    private static String CONF_SRC_RELATED = "bfgy_conf_src_related";
    private static String CONF_DST_RELATED = "bfgy_conf_dst_related";

    private static String CONF_SRC_LOGO = "bfgy_conf_src_logo";
    private static String CONF_DST_LOGO = "bfgy_conf_dst_logo";

    //单据体标识配置

    private static String SRC_BILL_LOGO = "bfgy_src_logo";
    private static String DST_BILL_LOGO = "bfgy_dst_logo";
    private static String SRC_BILL_IDS = "bfgy_src_id";
    private static String DST_BILL_ID = "bfgy_dst_id";

    private static String DST_BILL_ID_EX = "bfgy_dst_id_ex";
    private static String SRC_BILL_ID_EX = "bfgy_src_id_ex";
    private static String DST_ENTRY_NAME_EX = "bfgy_dst_entry_name_ex";
    private static String SRC_ENTRY_NAME_EX = "bfgy_src_entry_name_ex";
    private static String DST_ENTRY_ID_EX = "bfgy_dst_entry_id_ex";
    private static String SRC_ENTRY_ID_EX = "bfgy_src_entry_id_ex";
//    private static BotpRelationHelper helper = null;

    private class UniteEntity {
        private String typed;
        private String repairResult;
        private String srcBillLogo;
        private String dstBillLogo;
        private List<Long> srcBillIds;
        private Long dstBillId;

        private Long dstBillIdEx;
        private Long srcBillIdEx;
        private String dstEntryNameEx;
        private String srcEntryNameEx;
        private Long dstEntryIdEx;
        private Long srcEntryIdEx;


        public String getRepairResult() {
            return repairResult;
        }
        public void setRepairResult(String repairResult) {
            this.repairResult = repairResult;
        }
        public String getTyped() {
            return typed;
        }
        public void setTyped(String typed) {
            this.typed = typed;
        }
        public Long getDstBillIdEx() {
            return dstBillIdEx;
        }
        public void setDstBillIdEx(Long dstBillIdEx) {
            this.dstBillIdEx = dstBillIdEx;
        }
        public Long getSrcBillIdEx() {
            return srcBillIdEx;
        }
        public void setSrcBillIdEx(Long srcBillIdEx) {
            this.srcBillIdEx = srcBillIdEx;
        }
        public String getDstEntryNameEx() {
            return dstEntryNameEx;
        }
        public void setDstEntryNameEx(String dstEntryNameEx) {
            this.dstEntryNameEx = dstEntryNameEx;
        }
        public String getSrcEntryNameEx() {
            return srcEntryNameEx;
        }
        public void setSrcEntryNameEx(String srcEntryNameEx) {
            this.srcEntryNameEx = srcEntryNameEx;
        }
        public Long getDstEntryIdEx() {
            return dstEntryIdEx;
        }
        public void setDstEntryIdEx(Long dstEntryIdEx) {
            this.dstEntryIdEx = dstEntryIdEx;
        }
        public Long getSrcEntryIdEx() {
            return srcEntryIdEx;
        }
        public void setSrcEntryIdEx(Long srcEntryIdEx) {
            this.srcEntryIdEx = srcEntryIdEx;
        }
        public String getSrcBillLogo() {
            return srcBillLogo;
        }
        public void setSrcBillLogo(String srcBillLogo) {
            this.srcBillLogo = srcBillLogo;
        }
        public String getDstBillLogo() {
            return dstBillLogo;
        }
        public void setDstBillLogo(String dstBillLogo) {
            this.dstBillLogo = dstBillLogo;
        }
        public List<Long> getSrcBillIds() {
            return srcBillIds;
        }
        public void setSrcBillIds(List<Long> srcBillIds) {
            this.srcBillIds = srcBillIds;
        }
        public Long getDstBillId() {
            return dstBillId;
        }
        public void setDstBillId(Long dstBillId) {
            this.dstBillId = dstBillId;
        }
        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return 1;
        }
        @Override
        public boolean equals(Object obj) {
            // TODO Auto-generated method stub
            RapairBotpFormPlugin.UniteEntity uniteEntity = (RapairBotpFormPlugin.UniteEntity)obj;
            if(this != null) {
                System.out.println(this.getDstBillLogo() + "+++++" + uniteEntity.getSrcBillLogo());
                System.out.println(this.getDstBillLogo() + "+++++" + uniteEntity.getDstBillLogo());
                System.out.println(this.getDstBillId() + "+++++" + uniteEntity.getDstBillId());
                System.out.println(this.getSrcBillIds() + "+++++" + uniteEntity.getSrcBillIds());

                Boolean srcBillLogoBoolean = this.getDstBillLogo().equals(uniteEntity.getSrcBillLogo());
                Boolean dstBillLogoBoolean = this.getDstBillLogo().equals(uniteEntity.getDstBillLogo());
                Boolean dstBillIdBoolean = this.getDstBillId().equals(uniteEntity.getDstBillId());
                Boolean srcBillIdsBoolean = this.getSrcBillIds().equals(uniteEntity.getSrcBillIds());
                return uniteEntity.getSrcBillLogo() == null || uniteEntity.getDstBillLogo() == null
                        || uniteEntity.getDstBillId() == null || uniteEntity.getSrcBillIds() == null ? true
                        : srcBillLogoBoolean && dstBillLogoBoolean && dstBillIdBoolean && srcBillIdsBoolean;
            }else {
                return false;
            }
        }
    }
    /**
     * 监听
     */
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button searchBillBtn = this.getControl("bfgy_search_bill_btn1");
        Toolbar getDataBtn = this.getControl("tbmain");
//        Toolbar repairDataBtnBar = this.getControl("bfgy_advcontoolbarap");
//        Button testDataBtn = this.getControl("bfgy_get_test_btn");


        searchBillBtn.addClickListener(this);
        getDataBtn.addItemClickListener(this);
//        repairDataBtnBar.addItemClickListener(this);
//        testDataBtn.addClickListener(this);
    }
    /**
     * 点击工具栏按钮
     */
    @Override
    public void itemClick(ItemClickEvent evt) {
        // TODO Auto-generated method stub

        super.itemClick(evt);
        String key = evt.getItemKey();
        //点击修复按钮事件
        if ("bar_submit_nono".equals(key)) {
            //修复数据
            DynamicObjectCollection result_entry = this.getModel().getEntryEntity("result_entry");
            String application = (String)this.getModel().getValue("application");
            Set<RapairBotpFormPlugin.UniteEntity> uniteEntitiesSets = new HashSet<>();
            if (StringUtils.isNotEmpty(application)) {
                try {
//                    helper = new BotpRelationHelper(application);
                } catch (Exception e) {
                    //初始化修复工具失败
                    this.getView().showErrorNotification("请检查目的单据应用是否填写正确！");
                }

                for (DynamicObject dynamicObject : result_entry) {
                    Object e_id = dynamicObject.getPkValue();
                    String E_typed = dynamicObject.getString("type");
                    String E_srcBillLogo = dynamicObject.getString(SRC_BILL_LOGO);
                    String E_dstBillLogo = dynamicObject.getString(DST_BILL_LOGO);


                    String ids = (String)dynamicObject.get(SRC_BILL_IDS);
                    List<Long> E_srcBillIds = Arrays.asList(ids.split(",")).stream().map(s->Long.parseLong(s.trim())).collect(Collectors.toList());
                    Long E_dstBillId = dynamicObject.getLong(DST_BILL_ID);
                    Long E_dstBillIdEx = dynamicObject.getLong(DST_BILL_ID_EX);
                    Long E_srcBillIdEx = dynamicObject.getLong(SRC_BILL_ID_EX);
                    String E_dstEntryNameEx = dynamicObject.getString(DST_ENTRY_NAME_EX);
                    String E_srcEntryNameEx = dynamicObject.getString(SRC_ENTRY_NAME_EX);
                    Long E_dstEntryIdEx = dynamicObject.getLong(DST_ENTRY_ID_EX);
                    Long E_srcEntryIdEx = dynamicObject.getLong(SRC_ENTRY_ID_EX);

                    String repairResult = "error";
                    if ("unite".equals(E_typed)) {
                        //修复
                        try {
//                            helper.saveApRation4Unite(E_dstBillLogo, E_dstBillId, E_srcBillLogo, E_srcBillIds);
                            dynamicObject.set("repair_result", "修复成功");
                            repairResult = "修复成功";
                        } catch (Exception e) {
                            //头关系修复失败
                            dynamicObject.set("repair_result", "修复失败");
                            repairResult = "修复失败";
                            System.out.println();
                        }
                    }else if ("entry".equals(E_typed)) {
                        //修复
                        try {
//                            helper.saveAllRation4Entry(E_dstBillLogo, E_dstBillIdEx, E_dstEntryNameEx, E_dstEntryIdEx, E_srcBillLogo, E_srcBillIdEx, E_srcEntryNameEx, E_srcEntryIdEx);
                            dynamicObject.set("repair_result", "修复成功");
                            repairResult = "修复成功";
                        } catch (Exception e) {
                            //行关系修复失败
                            dynamicObject.set("repair_result", "修复失败");
                            repairResult = "修复失败";
                            System.out.println();
                        }
                    } else {
                        //error
                    }
                    RapairBotpFormPlugin.UniteEntity uniteEntity = new RapairBotpFormPlugin.UniteEntity();
                    uniteEntity.setTyped(E_typed);
                    uniteEntity.setSrcBillLogo(E_srcBillLogo);
                    uniteEntity.setDstBillLogo(E_dstBillLogo);
                    uniteEntity.setSrcBillIds(E_srcBillIds);
                    uniteEntity.setDstBillId(E_dstBillId);

                    uniteEntity.setDstBillIdEx(E_dstBillIdEx);
                    uniteEntity.setSrcBillIdEx(E_srcBillIdEx);
                    uniteEntity.setDstEntryNameEx(E_dstEntryNameEx);
                    uniteEntity.setSrcEntryNameEx(E_srcEntryNameEx);
                    uniteEntity.setDstEntryIdEx(E_dstEntryIdEx);
                    uniteEntity.setSrcEntryIdEx(E_srcEntryIdEx);
                    uniteEntity.setRepairResult(repairResult);
                    uniteEntitiesSets.add(uniteEntity);
                }
                //更新数据
            }
        } else if("bar_submit".equals(key)){
            getRepairData();
        } else  {
            //error
            this.getView().showErrorNotification("请填写目的单据所属应用！");
        }
    }
    /**
     * 点击按钮
     */
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        IDataModel thisMode = this.getModel();
        IFormView thisView = this.getView();
        String confSrcLogo = (String)thisMode.getValue(CONF_SRC_LOGO);
        String confDstLogo = (String)thisMode.getValue(CONF_DST_LOGO);
        String confSrcRelated = (String)thisMode.getValue(CONF_SRC_RELATED);
        String confDstRelated = (String)thisMode.getValue(CONF_DST_RELATED);

        Button thisBtn = (Button)evt.getSource();
        //点击按钮事件
        if ("bfgy_search_bill_btn1".equals(thisBtn.getKey())) {
            if("unite".equals(confSrcRelated)) {
                setUniteView(confSrcLogo, SRC_COMBO);
            }else {
                setEntryView(confSrcLogo, SRC_ENTRY_COMBO);
            }
            if("unite".equals(confDstRelated)) {
                setUniteView(confDstLogo, DST_COMBO);
            }else {
                setEntryView(confDstLogo, DST_ENTRY_COMBO);
            }
        }else if("get_data_btn".equals(thisBtn.getKey())) {
            try {
                getRepairData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if("repair_data_btn".equals(thisBtn.getKey())) {

        }else if("get_test_btn".equals(thisBtn.getKey())){
            testFun();
        }else {}

    }
    /**
     * 下拉菜单更新
     */
    @Override
    public void propertyChanged(PropertyChangedArgs e) {
        // TODO Auto-generated method stub
        //super.propertyChanged(e);
        String entryComboName = e.getProperty().getName();
        String newValue = (String) e.getChangeSet()[0].getNewValue();
        if (SRC_COMBO.equals(entryComboName) || DST_COMBO.equals(entryComboName)) {

        } else if (SRC_ENTRY_COMBO.equals(entryComboName)) {
            String confSrcLogo = (String)this.getModel().getValue(CONF_SRC_LOGO);
            setEntitiesView(confSrcLogo, newValue, SRC_COMBO);
        } else if (DST_ENTRY_COMBO.equals(entryComboName)) {
            String confDstLogo = (String)this.getModel().getValue(CONF_DST_LOGO);
            setEntitiesView(confDstLogo, newValue, DST_COMBO);
        } else if (CONF_SRC_RELATED.equals(entryComboName)) {
            //
            if("unite".equals(newValue)) {
                this.getView().setEnable(false, SRC_ENTRY_COMBO);
            }else {
                this.getView().setEnable(true, SRC_ENTRY_COMBO);
            }
        } else if (CONF_DST_RELATED.equals(entryComboName)) {
            if("unite".equals(newValue)) {
                this.getView().setEnable(false, DST_ENTRY_COMBO);
            }else {
                this.getView().setEnable(true, DST_ENTRY_COMBO);
            }
        }
    }
    /**
     * 填写分录体下拉菜单
     * @param confLogo
     * @param entryName
     * @param comboLogo
     */
    public void setEntitiesView(String confLogo, String entryName, String comboLogo) {
        MainEntityType type = MetadataServiceHelper.getDataEntityType(confLogo);
        DynamicObject dy = new DynamicObject(type);
        List<ComboItem> untieList = getEntryFieldsName(dy.getDynamicObjectCollection(entryName));
        ComboEdit ComboEdit = this.getControl(comboLogo);
        ComboEdit.setComboItems(untieList);
    }

    /**
     *	填写行下拉菜单
     * @param confLogo
     * @param comboLogo
     */
    public void setUniteView(String confLogo, String comboLogo) {
        if(StringUtils.isBlank(confLogo)) {
            this.getView().showTipNotification("请填写原始单标识、目的单标识");
        }else {
            List<ComboItem> untieList = getUniteFields(confLogo);
            ComboEdit ComboEdit = this.getControl(comboLogo);
            ComboEdit.setComboItems(untieList);
        }
    }

    /**
     * 填写单据体属性下拉菜单
     * @param confLogo
     * @param comboLogo
     */
    public void setEntryView(String confLogo, String comboLogo) {
        if(StringUtils.isBlank(confLogo)) {
            this.getView().showTipNotification("请填写原始单标识、目的单标识");
        }else {
            List<ComboItem> untieList = getEntitiesNames(confLogo);
            ComboEdit ComboEdit = this.getControl(comboLogo);
            ComboEdit.setComboItems(untieList);
        }
    }
    /**
     * 清空下拉框
     * @param comboLogo
     */
    public void setComboEmpty(String comboLogo) {
        ComboEdit comboEdit = this.getControl(comboLogo);
        comboEdit.setComboItems(null);
    }

    /**
     * 获取单据的行名称
     * @return
     */
    public List<ComboItem> getUniteFields(String logoName){
        List<ComboItem> comboItems = new ArrayList<ComboItem>();

        MainEntityType confType = MetadataServiceHelper.getDataEntityType(logoName);
//		confType.getFields().entrySet().stream().map(Map.Entry::getKey).forEach(UniteField->{
//			LocaleString localeString = new LocaleString(UniteField);
//			comboItems.add(new ComboItem(localeString, UniteField));
//		});
        confType.getProperties().stream().forEach(field->{
            LocaleString localeString = new LocaleString(field.getName()+"("+field.getDisplayName()+")");
            comboItems.add(new ComboItem(localeString, field.getName()));
        });
        return comboItems;
    }
    /**
     * 获取单据的所有分录名
     * @param logoName
     * @return
     */
    public List<ComboItem> getEntitiesNames(String logoName){
        List<ComboItem> comboItems = new ArrayList<ComboItem>();
        MainEntityType confType = MetadataServiceHelper.getDataEntityType(logoName);
        Map<String, EntityType> allEntities = confType.getAllEntities();
        allEntities.entrySet().stream().map(Map.Entry::getKey).filter(entityKey->!logoName.equals(entityKey)).forEach(entitiesName->{
            DynamicObject dy = new DynamicObject(confType);
            String displayName = "error=>未找到";
            try {
                displayName = dy.getDynamicObjectCollection(entitiesName).getDynamicObjectType().getDisplayName().getLocaleValue();
            }catch(Exception e) {

            }
            LocaleString localeString = new LocaleString(entitiesName+"("+displayName+")");
            comboItems.add(new ComboItem(localeString, entitiesName));
        });
        return comboItems;
    }
    /**
     * 获取单据体属性信息
     * @param dynamicObjectCollection
     * @return
     */
    public List<ComboItem> getEntryFieldsName(DynamicObjectCollection dynamicObjectCollection){
        DynamicObjectType type = dynamicObjectCollection.getDynamicObjectType();
        List<ComboItem> comboItems = new ArrayList<ComboItem>();
        type.getProperties().stream().forEach(field->{
            LocaleString localeString = new LocaleString(field.getName()+"("+field.getDisplayName()+")");
            comboItems.add(new ComboItem(localeString, field.getName()));
        });
        return comboItems;
    }
    /**
     * 获取修复的数据
     */
    public void getRepairData(){

        IDataModel thisMode = this.getModel();
        String confSrcLogo = (String) thisMode.getValue(CONF_SRC_LOGO);
        String confDstLogo = (String) thisMode.getValue(CONF_DST_LOGO);
        String confSrcRelated = (String) thisMode.getValue(CONF_SRC_RELATED);
        String confDstRelated = (String) thisMode.getValue(CONF_DST_RELATED);

        Set<RapairBotpFormPlugin.UniteEntity> uniteEntitiesSets = new HashSet<>();

        //获取前台配置信息
        String dstEntryField = (String)this.getModel().getValue(DST_ENTRY_COMBO);
        String dstField = (String)this.getModel().getValue(DST_COMBO);
        String srcEntryField = (String)this.getModel().getValue(SRC_ENTRY_COMBO);
        String srcField = (String)this.getModel().getValue(SRC_COMBO);
        if ("unite".equals(confSrcRelated) && "unite".equals(confDstRelated)) {
            QFilter[] dstFilters = {QFilter.isNotNull(dstField)};
            DynamicObject[] dstDys = BusinessDataServiceHelper.load(confDstLogo, dstField, dstFilters);
            for (DynamicObject dstDy : dstDys) {
                String dstValue = dstDy.getString(dstField);
                if (StringUtils.isNotEmpty(dstValue)) {
                    QFilter[] srcFilters = {QFilter.isNotNull(srcField)};
                    DynamicObject[] srcDys = BusinessDataServiceHelper.load(confSrcLogo, srcField, srcFilters);
                    List<Long> srcList = new ArrayList<>();
                    RapairBotpFormPlugin.UniteEntity uniteEntity = new RapairBotpFormPlugin.UniteEntity();
                    for (DynamicObject srcDy : srcDys) {
                        srcList.add((Long)srcDy.getPkValue());
                        //System.out.println(srcDy.getString(srcField));
                    }
                    //拼装修复信息
                    if(StringUtils.isNotEmpty(confDstLogo) && StringUtils.isNotEmpty(confSrcLogo) && null != dstDy && srcList.size()>0) {
                        uniteEntity.setTyped("unite");
                        uniteEntity.setDstBillLogo(confDstLogo);
                        uniteEntity.setSrcBillLogo(confSrcLogo);
                        uniteEntity.setDstBillId((Long)dstDy.getPkValue());
                        uniteEntity.setSrcBillIds(srcList);
                        uniteEntity.setRepairResult("准备修复");
                        uniteEntitiesSets.add(uniteEntity);
                    }
                }
            }

        } else if ("unite".equals(confSrcRelated) && "entry".equals(confDstRelated)) {
            //查找目的单据的行并进行去重逻辑
            QFilter[] dstEntryFilters = {QFilter.isNotNull(dstEntryField + "." + dstField)};
            DynamicObject[] dstDys = BusinessDataServiceHelper.load(confDstLogo, dstField, dstEntryFilters);
            for (DynamicObject dstDy : dstDys) {
                DynamicObjectCollection dstEntryDyCols = dstDy.getDynamicObjectCollection(dstEntryField);
                for (DynamicObject dstEntryDyCol : dstEntryDyCols) {
                    String compareValue = dstEntryDyCol.getString(dstField);
                    if(StringUtils.isNotEmpty(compareValue)) {
                        QFilter[] srcEntryFilters = {new QFilter(srcField, QCP.equals, compareValue)};
                        DynamicObject[] srcDys = BusinessDataServiceHelper.load(confSrcLogo, "", srcEntryFilters);
                        //获取到源单据列表进行数据拼装
                        List<Long> srcBillIds = new ArrayList<>();
                        for (DynamicObject srcDy : srcDys) {
                            Long srcValue = (Long)srcDy.getPkValue();
                            srcBillIds.add(srcValue);
                        }
                        Long dstBillId = (Long) dstDy.getPkValue();
                        if(StringUtils.isNotBlank(confSrcLogo) && StringUtils.isNotBlank(confSrcLogo) &&
                                srcBillIds.size()>0 && null != dstBillId) {
                            RapairBotpFormPlugin.UniteEntity uniteEntity = new RapairBotpFormPlugin.UniteEntity();
                            uniteEntity.setTyped("unite");
                            uniteEntity.setSrcBillLogo(confSrcLogo);
                            uniteEntity.setDstBillLogo(confDstLogo);
                            uniteEntity.setSrcBillIds(srcBillIds);
                            uniteEntity.setDstBillId(dstBillId);
                            uniteEntity.setRepairResult("准备修复");
                            uniteEntitiesSets.add(uniteEntity);
                        }
                    }
                }
            }
        } else if ("entry".equals(confSrcRelated) && "unite".equals(confDstRelated)) {//行对头
            //查找目的单据的值列
            QFilter[] dstEntryFilters = {QFilter.isNotNull(srcField)};
            DynamicObject[] dstDys = BusinessDataServiceHelper.load(confDstLogo, srcField, dstEntryFilters);

            for (DynamicObject dstDy : dstDys) {
                String compareValue = dstDy.getString(srcField);
                if(StringUtils.isNotEmpty(compareValue)) {
                    QFilter[] srcEntryFilters = {new QFilter(srcEntryField+"."+srcField, QCP.equals, compareValue)};
                    DynamicObject[] srcDys = BusinessDataServiceHelper.load(confSrcLogo, "", srcEntryFilters);

                    List<Long> srcBillIds = new ArrayList<>();
                    for (DynamicObject srcDy : srcDys) {
                        Long srcValue = (Long)srcDy.getPkValue();
                        srcBillIds.add(srcValue);
                    }
                    Long dstBillId = (Long) dstDy.getPkValue();
                    if(StringUtils.isNotBlank(confSrcLogo) && StringUtils.isNotBlank(confSrcLogo) &&
                            srcBillIds.size()>0 && null != dstBillId) {
                        RapairBotpFormPlugin.UniteEntity uniteEntity = new RapairBotpFormPlugin.UniteEntity();
                        uniteEntity.setTyped("unite");
                        uniteEntity.setSrcBillLogo(confSrcLogo);
                        uniteEntity.setDstBillLogo(confDstLogo);
                        uniteEntity.setSrcBillIds(srcBillIds);
                        uniteEntity.setDstBillId(dstBillId);
                        uniteEntity.setRepairResult("准备修复");
                        uniteEntitiesSets.add(uniteEntity);
                    }
                }
            }
        } else if("entry".equals(confSrcRelated) && "entry".equals(confDstRelated)){//行对行
            QFilter[] dstEntryFilters = {QFilter.isNotNull(dstEntryField+"."+dstField)};
            DynamicObject[] dstDys = BusinessDataServiceHelper.load(confDstLogo, dstField, dstEntryFilters);
            for(DynamicObject dstDy : dstDys) {
                DynamicObjectCollection dstEntryDyCols = dstDy.getDynamicObjectCollection(dstEntryField);
                for (DynamicObject dstEntryDyCol : dstEntryDyCols) {
                    String compareValue = dstEntryDyCol.getString(dstField);
                    if (StringUtils.isNotEmpty(compareValue)) {
                        QFilter[] srcEntryFilters = {new QFilter(srcEntryField+"."+srcField, QCP.equals, compareValue)};
                        DynamicObject[] srcDys = BusinessDataServiceHelper.load(confSrcLogo, srcField, srcEntryFilters);
                        List<Long> srcBillIds = new ArrayList<>();
                        for (DynamicObject srcDy : srcDys) {
                            Long srcValue = (Long)srcDy.getPkValue();
                            srcBillIds.add(srcValue);

                            DynamicObjectCollection srcEntryDyCols = srcDy.getDynamicObjectCollection(srcEntryField);
                            for (DynamicObject srcEntryDyCol : srcEntryDyCols) {
                                if (StringUtils.isNotEmpty(confSrcLogo) && StringUtils.isNotEmpty(confDstLogo) && null != srcDy && null != dstDy
                                        && StringUtils.isNotEmpty(srcEntryField) && StringUtils.isNotEmpty(dstEntryField) && null != srcEntryDyCol
                                        && null != dstEntryDyCol) {
                                    RapairBotpFormPlugin.UniteEntity uniteEntity = new RapairBotpFormPlugin.UniteEntity();
                                    uniteEntity.setTyped("entry");
                                    uniteEntity.setSrcBillLogo(confSrcLogo);
                                    uniteEntity.setDstBillLogo(confDstLogo);
                                    uniteEntity.setSrcBillIdEx((Long)srcDy.getPkValue());
                                    uniteEntity.setDstBillIdEx((Long)dstDy.getPkValue());
                                    uniteEntity.setSrcEntryNameEx(srcEntryField);
                                    uniteEntity.setDstEntryNameEx(dstEntryField);
                                    uniteEntity.setSrcEntryIdEx((Long)srcEntryDyCol.getPkValue());
                                    uniteEntity.setDstEntryIdEx((Long)dstEntryDyCol.getPkValue());
                                    uniteEntity.setRepairResult("准备修复");
                                    uniteEntitiesSets.add(uniteEntity);
                                }
                            }
                        }
                        Long dstBillId = (Long) dstDy.getPkValue();
                        if(StringUtils.isNotBlank(confSrcLogo) && StringUtils.isNotBlank(confSrcLogo) &&
                                srcBillIds.size()>0 && null != dstBillId) {
                            RapairBotpFormPlugin.UniteEntity uniteEntity = new RapairBotpFormPlugin.UniteEntity();
                            uniteEntity.setTyped("unite");
                            uniteEntity.setSrcBillLogo(confSrcLogo);
                            uniteEntity.setDstBillLogo(confDstLogo);
                            uniteEntity.setSrcBillIds(srcBillIds);
                            uniteEntity.setDstBillId(dstBillId);
                            uniteEntity.setRepairResult("准备修复");
                            uniteEntitiesSets.add(uniteEntity);
                        }
                    }
                }
            }

        }else {
            //Error
            this.getView().showErrorNotification("请选择数据所在的位置！");
        }

        //获取到的需要进行修复的数据
        for (RapairBotpFormPlugin.UniteEntity uniteEntitiesSet : uniteEntitiesSets) {
            System.out.println(uniteEntitiesSet);
        }
        //将数据写入单据体
        saveListData(uniteEntitiesSets);

    }

    public void testFun() {
        try {
//            helper = new BotpRelationHelper("pm");
            QFilter[] qDestFilter = {new QFilter("billno", QCP.equals, "2014BJHZ016")};
            DynamicObject[] destDys = BusinessDataServiceHelper.load("pm_purorderbill", "billno", qDestFilter);
            QFilter[] qSrcFilter = {new QFilter("billno", QCP.equals, "PD3/126/2013/14执行1")};
            DynamicObject[] srcDys = BusinessDataServiceHelper.load("sm_salorder", "", qSrcFilter);
//            helper.saveApRation4Unite("pm_purorderbill", (Long)destDys[0].getPkValue(), "sm_salorder", Arrays.asList((Long) srcDys[0].getPkValue()));
            System.out.println(srcDys.length);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }


    }

    public void saveListData(Set<RapairBotpFormPlugin.UniteEntity> uniteEntitiesSets){
//        IDataModel resultEntry = this.getModel();//
//        DynamicObjectCollection result_entry = (DynamicObjectCollection) resultEntry.getValue("result_entry");
//        DynamicObjectType type = result_entry.getDynamicObjectType();
        List<DynamicObject> list = new ArrayList<>();
        for (RapairBotpFormPlugin.UniteEntity uniteEntitieSet : uniteEntitiesSets) {
            DynamicObject row = BusinessDataServiceHelper.newDynamicObject("bfgy_botprepairdata");

            row.set("bfgy_type", uniteEntitieSet.getTyped());

            row.set("bfgy_src_logo", uniteEntitieSet.getSrcBillLogo());
            row.set("bfgy_dst_logo", uniteEntitieSet.getDstBillLogo());
            row.set("bfgy_src_id_ux_tag", StringUtils.join(uniteEntitieSet.getSrcBillIds(), ","));
            row.set("bfgy_dst_id_ux", uniteEntitieSet.getDstBillId());

            row.set("bfgy_src_id_ex_tag", uniteEntitieSet.getSrcBillIdEx());
            row.set("bfgy_dst_id_ex", uniteEntitieSet.getDstBillIdEx());
            row.set("bfgy_src_entry_name_ex", uniteEntitieSet.getSrcEntryNameEx());
            row.set("bfgy_dst_entry_name_ex", uniteEntitieSet.getDstEntryNameEx());
            row.set("bfgy_src_entry_id_ex", uniteEntitieSet.getSrcEntryIdEx());
            row.set("bfgy_dst_entry_id_ex", uniteEntitieSet.getDstEntryIdEx());
            row.set("bfgy_repair_result", uniteEntitieSet.getRepairResult());

            row.set("billstatus", "A");
//            result_entry.add(row);
            list.add(row);
        }
//        EntryProp e1 = new EntryProp("result_entry", type);
//        this.getModel().deleteEntryData("result_entry");
//        this.getModel().batchInsertEntryRow(e1, 0, result_entry);
        SaveServiceHelper.save(list.toArray(new DynamicObject[list.size()]));
    }

    public static void main(String[] args) {
        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        list1.add("zhang");
        list1.add("li");
        list1.add("zhangs");
        list1.add("zhangs");

        list2.add("li");
        list2.add("zhangs");
        list2.add("zhang");
        list2.add("zhangs");

        Collections.sort(list1);
        Collections.sort(list2);

        System.out.println(list1.equals(list2));
    }

    /**
     * 修改目标单，为每行单据体，添加关联子实体数据行，记录关联的源单行内码
     *
     * @param targetTableDefine 目标单单据体表格定义：包含了目标单标识、单据体标识
     * @param targetBillObj 目标单
     * @param srcTableDefine 源单单据体表格定义：包含了源单标识、单据体标识、tableid
     * @param sourceBillObjs 源单
     * @param srcRowSeqFld 目标单单据体上，记录源单行号的字段名：需要基于此字段值定位源单行
     *
     * @remark
     * 根据目标单分录行上，记录的源单行号字段值，匹配找到源单行，把源单行内码，记录在目标单分录行上
     */
    private void createLinkEntity(
            TableDefine targetTableDefine, DynamicObject targetBillObj,
            TableDefine srcTableDefine, DynamicObject[] sourceBillObjs,
            String srcRowSeqFld) {

        // 循环分析源单行，提取源单行号、源单内码、分录行内码的关系，放在字典中备用: key = 源单行号； value = [源单内码、分录行内码]
        Map<Integer, Object[]> srcRowIds = new HashMap<>();
        for(DynamicObject srcObj : sourceBillObjs) {
            DynamicObjectCollection srcRows = srcObj.getDynamicObjectCollection(srcTableDefine.getEntityKey());
            for (DynamicObject srcRow : srcRows) {
                Integer srcRowSeq = srcRow.getInt("seq");
                Object[] srcRowId = new Object[] {srcObj.getPkValue(), srcRow.getPkValue()};
                srcRowIds.put(srcRowSeq, srcRowId);
            }
        }

        Long srcTableId = srcTableDefine.getTableId();

        // 拼接处关联子实体标识：如果是单据头下的lk子表，固定使用billhead_lk；如果是单据体下的lk子表，用单据体标识+lk
        String lkEntryKey = targetTableDefine.getEntityKey() + "_lk";

        // 循环分析下游单的单据体行，逐一建立起与源单行的关联关系
        DynamicObjectCollection targetRows = targetBillObj.getDynamicObjectCollection(targetTableDefine.getEntityKey());
        for(DynamicObject targetRow : targetRows) {
            // 获取下级_lk子实体行
            DynamicObjectCollection linkRows = targetRow.getDynamicObjectCollection(lkEntryKey);
            if (!linkRows.isEmpty()) {
                // 已经有关联源单，无需再重建
                continue;
            }

            // 寻找匹配的源单行：提取本行的匹配字段值，和源单行进行比对
            Integer srcRowSeq = targetRow.getInt(srcRowSeqFld);
            if (srcRowIds.containsKey(srcRowSeq)) {
                // 找到了匹配的行，创建一条_lk子实体上数据，记录源单内码
                DynamicObject linkRow = new DynamicObject(linkRows.getDynamicObjectType());
                linkRows.add(linkRow);

                // 在lk行中，记录源单分录表格编码、源单内码、源单分录内码
                Object[] srcRowId = srcRowIds.get(srcRowSeq);
                linkRow.set(lkEntryKey + "_stableid", srcTableId);		// 源单分录表格编码：以此标识源单类型及单据体
                linkRow.set(lkEntryKey + "_sbillid", srcRowId[0]);		// 源单内码
                linkRow.set(lkEntryKey + "_sid", srcRowId[1]);			// 源单分录行内码
            }
        }
    }
}
