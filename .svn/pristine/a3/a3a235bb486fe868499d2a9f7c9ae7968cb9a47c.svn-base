package kd.cus.wb.botp;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.control.Toolbar;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.QueryServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 修复botp表单插件
 * @author suyp
 *
 */
public class BotpRepairPlus extends AbstractFormPlugin {
    private static String SRC_COMBO = "src_combo";
    private static String DST_COMBO = "dst_combo";
    private static String SRC_ENTRY_COMBO = "src_entry_combo";
    private static String DST_ENTRY_COMBO = "dst_entry_combo";
    private static String CONF_SRC_RELATED = "conf_src_related";
    private static String CONF_DST_RELATED = "conf_dst_related";

    private static String CONF_SRC_LOGO = "conf_src_logo";
    private static String CONF_DST_LOGO = "conf_dst_logo";

    //单据体标识配置

    private static String SRC_BILL_LOGO = "src_logo";
    private static String DST_BILL_LOGO = "dst_logo";
    private static String SRC_BILL_IDS = "src_id";
    private static String DST_BILL_ID = "dst_id";

    private static String DST_BILL_ID_EX = "dst_id_ex";
    private static String SRC_BILL_ID_EX = "src_id_ex";
    private static String DST_ENTRY_NAME_EX = "dst_entry_name_ex";
    private static String SRC_ENTRY_NAME_EX = "src_entry_name_ex";
    private static String DST_ENTRY_ID_EX = "dst_entry_id_ex";
    private static String SRC_ENTRY_ID_EX = "src_entry_id_ex";

    public void registerListener(EventObject e) {
        Toolbar repairDataBtnBar = (Toolbar)this.getControl("tbmain");
        repairDataBtnBar.addItemClickListener(this);
    }

    public void itemClick(ItemClickEvent evt) {

        this.getView().download("http://localhost:8080/ierp/tempfile/download.do?configKey=redis.serversForCache&id=tempfile-f507f13b-18ee-4ad5-9209-f0bff9ff604f" + "&t=a");

        super.itemClick(evt);
        String key = evt.getItemKey();
        if ("bfgy_pool".equals(key)) {
            getRepairDateplus();
        }
    }

    private void getRepairDateplus(){
        Map<String, Set<String>> map_entry = new HashMap<>();
        Map<String,Set<String>> map_unite = new HashMap<>();

        IDataModel thisMode = this.getModel();

        String app = (String) thisMode.getValue("application");//目的单应用

        String confSrcLogo = (String) thisMode.getValue(CONF_SRC_LOGO);
        String confDstLogo = (String) thisMode.getValue(CONF_DST_LOGO);
        String confSrcRelated = (String) thisMode.getValue(CONF_SRC_RELATED);
        String confDstRelated = (String) thisMode.getValue(CONF_DST_RELATED);

        //获取前台配置信息
        String dstEntryField = (String)this.getModel().getValue(DST_ENTRY_COMBO);
        String dstField = (String)this.getModel().getValue(DST_COMBO);
        String srcEntryField = (String)this.getModel().getValue(SRC_ENTRY_COMBO);
        String srcField = (String)this.getModel().getValue(SRC_COMBO);

        Set<String> src_set = new HashSet<>();
        
        Map<String, List<DynamicObject>> dts_map_PO = new HashMap<>();
        HashMap<String, List<String>> src_set_map = new HashMap<>();

        if ("entry".equalsIgnoreCase(confDstRelated)) {
            QFilter[] dstFilters = {QFilter.isNotNull(dstEntryField + "." + dstField)};
            DynamicObjectCollection dst = QueryServiceHelper.query(confDstLogo, "id," + dstEntryField + ".id," + dstEntryField + "." + dstField, dstFilters);

            src_set = dst.stream().map(i -> i.getString(dstEntryField + "." + dstField)).collect(Collectors.toSet());
            
            dts_map_PO = dst.stream().collect(Collectors.groupingBy(i -> i.getString(dstEntryField + "." + dstField)));

            dst.stream().forEach(m->{
            	if(src_set_map.get(m.getString(dstEntryField + "." + dstField))!= null && src_set_map.get(m.getString(dstEntryField + "." + dstField)).size() > 0) {
            		List<String> list = src_set_map.get(m.getString(dstEntryField + "." + dstField));
            		list.add(m.getString("id") + "." + m.getString(dstEntryField + ".id"));
            		src_set_map.put(m.getString(dstEntryField + "." + dstField), list);
            	}else {
            		List<String> list = new ArrayList<>();
            		list.add(m.getString("id") + "." + m.getString(dstEntryField + ".id"));
            		src_set_map.put(m.getString(dstEntryField + "." + dstField), list);
            	}
            });



        }else if ("unite".equalsIgnoreCase(confDstRelated)){
            QFilter[] dstFilters = {QFilter.isNotNull(dstField)};
            DynamicObjectCollection dst = QueryServiceHelper.query(confDstLogo, "id," + dstField, dstFilters);

            src_set = dst.stream().map(i -> i.getString(dstField)).collect(Collectors.toSet());
            
            dts_map_PO = dst.stream().collect(Collectors.groupingBy(i -> i.getString(dstField)));
            
            dst.stream().forEach(m->{
            	if(src_set_map.get(m.getString(dstField))!= null && src_set_map.get(m.getString(dstField)).size() > 0) {
            		List<String> list = src_set_map.get(m.getString(dstField));
            		list.add(m.getString("id") + "." + "0");
            		src_set_map.put(m.getString(dstField), list);
            	}else {
            		List<String> list = new ArrayList<>();
            		list.add(m.getString("id") + "." + "0");
            		src_set_map.put(m.getString(dstField), list);
            	}
            });
        }

        if ("entry".equalsIgnoreCase(confSrcRelated)){
            QFilter[] srcFilters = {new QFilter(srcEntryField + "." + srcField, QCP.in, src_set)};
            DynamicObjectCollection src_cols = QueryServiceHelper.query(confSrcLogo, "id," + dstEntryField + ".id," + srcEntryField + "." + srcField, srcFilters);

            Map<String, List<DynamicObject>> src_group = src_cols.stream().collect(Collectors.groupingBy(i -> i.getString("id")));
            src_group.entrySet().forEach(m->{
                String key = m.getKey();
//                String id_v = key.split("\\.")[0];
//                String id_e = key.split("\\.")[1];
                List<DynamicObject> value = m.getValue();

                Map<String, List<DynamicObject>> src_entry_group = value.stream().collect(Collectors.groupingBy(i -> i.getString(srcEntryField + "." + srcField)));
                src_entry_group.entrySet().forEach(n->{
                    String key_entry = n.getKey();//关联参数
                    List<DynamicObject> value_entry = n.getValue();
                    if (src_set_map.get(key_entry) != null) {
                    	
                    	src_set_map.get(key_entry).forEach(entry->{
                    		
                    		 String link_str_p = (String) entry.split("\\.")[0];
                             String link_str_e = (String) entry.split("\\.")[1];

                             if ("0".equalsIgnoreCase(link_str_e)) {
                                 Set<String> set = new HashSet<>();
                                 set.add(key);
                                 map_unite.put(link_str_p, set);
                             } else {
                                 Set<String> entry_ids = value_entry.stream().map(i -> i.getString(srcEntryField + ".id")).collect(Collectors.toSet());
                                 map_entry.put(link_str_p + "." + key + "." + link_str_e, entry_ids);

                                 Set<String> set = new HashSet<>();
                                 set.add(key);
                                 map_unite.put(link_str_p, set);
                             }
                    	});
                    }
                });
            });

        }else if ("unite".equalsIgnoreCase(confSrcRelated)){
            QFilter[] srcFilters = {new QFilter(srcField,QCP.in, src_set)};
            DynamicObjectCollection src_cols = QueryServiceHelper.query(confSrcLogo, "id," + srcField, srcFilters);

            Map<String, List<DynamicObject>> src_group = src_cols.stream().collect(Collectors.groupingBy(i -> i.getString(srcField)));

			src_group.entrySet().forEach(m -> {
				String key = m.getKey();

				List<DynamicObject> value = m.getValue();
				if (src_set_map.get(key) != null) {

					src_set_map.get(key).forEach(entry -> {
						String link_str_p = (String) entry.split("\\.")[0];
						String link_str_e = (String) entry.split("\\.")[1];
						Set<String> src_ids = value.stream().map(i -> i.getString("id")).collect(Collectors.toSet());

						map_unite.put(link_str_p, src_ids);
					});

				}

			});
        }
        List<DynamicObject> dys = new ArrayList<>();

        map_unite.entrySet().forEach(m->{
            String key = m.getKey();

            Set<String> _this_value = m.getValue();
            int currentPage = 1;
            int pageSize = 10;

            while (true) {

                List<String> thisPagevalue = _this_value.stream().skip((currentPage - 1) * pageSize).limit(pageSize).
                        collect(Collectors.toList());

                currentPage ++;

                if (thisPagevalue.size() < 1){
                    break;
                }

                String value = StringUtils.join(thisPagevalue, ",");
                DynamicObject newDy = BusinessDataServiceHelper.newDynamicObject("bfgy_botppool");
                newDy.set("bfgy_textfield", app);//目的单应用
                newDy.set("bfgy_textfield1", confSrcLogo);//原始单配置标识
                newDy.set("bfgy_textfield2", confSrcRelated);//原始单关联位置
                newDy.set("bfgy_textfield3", confDstLogo);//目的单配置标识
                newDy.set("bfgy_textfield4", confDstRelated);//目的单关联位置
                newDy.set("bfgy_textfield5", "准备修复");//修复结果
                newDy.set("bfgy_textfield6", "unite");//类型
                newDy.set("bfgy_textfield7", value);//原始单id（头）
                newDy.set("bfgy_textfield8", key);//目的单id（头）

                newDy.set("bfgy_textfield9", "");//原始单id（行）
                newDy.set("bfgy_textfield10", "");//目的单id（行）
                newDy.set("bfgy_textfield11", "");//原始单分录名（行）
                newDy.set("bfgy_textfield12", "");//目的单分录名（行）
                newDy.set("bfgy_textfield13", "");//原始单分录id（行）
                newDy.set("bfgy_textfield14", "");//目的单分录id（行）\

                dys.add(newDy);
            }
        });

        map_entry.entrySet().forEach(m->{
            String key = m.getKey();
            String id_v = key.split("\\.")[0];
            String id_e = key.split("\\.")[1];
            String id_i = key.split("\\.")[2];
            Set<String> values_entrys = m.getValue();
            for (String values_entry:values_entrys) {

                DynamicObject newDy = BusinessDataServiceHelper.newDynamicObject("bfgy_botppool");
                newDy.set("bfgy_textfield", app);//目的单应用
                newDy.set("bfgy_textfield1", confSrcLogo);//原始单配置标识
                newDy.set("bfgy_textfield2", confSrcRelated);//原始单关联位置
                newDy.set("bfgy_textfield3", confDstLogo);//目的单配置标识
                newDy.set("bfgy_textfield4", confDstRelated);//目的单关联位置
                newDy.set("bfgy_textfield5", "准备修复");//修复结果
                newDy.set("bfgy_textfield6", "entry");//类型
                newDy.set("bfgy_textfield7", "");//原始单id（头）
                newDy.set("bfgy_textfield8", "");//目的单id（头）

                newDy.set("bfgy_textfield9", id_e);//原始单id（行）
                newDy.set("bfgy_textfield10", id_v);//目的单id（行）
                newDy.set("bfgy_textfield11", srcEntryField);//原始单分录名（行）
                newDy.set("bfgy_textfield12", dstEntryField);//目的单分录名（行）
                newDy.set("bfgy_textfield13", values_entry);//原始单分录id（行）
                newDy.set("bfgy_textfield14", id_i);//目的单分录id（行）

                dys.add(newDy);
            }
        });

        SaveServiceHelper.save(dys.toArray(new DynamicObject[dys.size()]));

        this.getView().showMessage("放入线程池成功！" + dys.size() + "条");
    }

    private Integer countNum(int index){
        return index/10;
    }

    public static void main(String[] args) {
        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
        cache.addList("demo",new String[]{"123"});//将自定义参数加入缓存
        cache.get("demo");//获取自定义缓存
    }
}
