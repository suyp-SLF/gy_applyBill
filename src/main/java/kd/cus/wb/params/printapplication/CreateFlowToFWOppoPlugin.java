package kd.cus.wb.params.printapplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;
//import org.apache.axis.AxisFault;
//import org.apache.axis.Message;
//import org.apache.axis.MessageContext;
//import org.apache.axis.client.Service;
//import org.apache.axis.encoding.Base64;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.exception.KDBizException;
import kd.bos.exception.KDException;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.parameter.SystemParamServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.session.EncreptSessionUtils;
//import kd.cus.wb.Client.DocAttachment;
//import kd.cus.wb.Client.DocInfo;
//import kd.cus.wb.Client.DocServiceHttpBindingStub;
//import kd.cus.wb.FWsecrets.SecretsUpdateHttpBindingStub;
//import kd.cus.wb.FWworkflow.WorkflowBaseInfo;
//import kd.cus.wb.FWworkflow.WorkflowDetailTableInfo;
//import kd.cus.wb.FWworkflow.WorkflowMainTableInfo;
//import kd.cus.wb.FWworkflow.WorkflowRequestInfo;
//import kd.cus.wb.FWworkflow.WorkflowRequestTableField;
//import kd.cus.wb.FWworkflow.WorkflowRequestTableRecord;
//import kd.cus.wb.FWworkflow.WorkflowServiceHttpBindingStub;
import kd.cus.wb.ar.applybill.AppbillOppoPlugin.WriteMeetingOpinionValidator;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub.DocInfo;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub.Login;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub.LoginResponse;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.ArrayOfWorkflowDetailTableInfo;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.ArrayOfWorkflowRequestTableField;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.ArrayOfWorkflowRequestTableRecord;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.DoCreateWorkflowRequest;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.DoCreateWorkflowRequestResponse;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetUserId;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetUserIdResponse;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WorkflowBaseInfo;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WorkflowDetailTableInfo;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WorkflowMainTableInfo;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WorkflowRequestInfo;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WorkflowRequestTableField;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WorkflowRequestTableRecord;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub.ArrayOfDocAttachment;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub.CreateDoc;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub.CreateDocResponse;
import kd.cus.wb.wsdl.client.docstub.DocServiceStub.DocAttachment;
import kd.cus.wb.wsdl.client.updstub.SecretsUpdateStub;
import kd.cus.wb.wsdl.client.updstub.SecretsUpdateStub.GetDeptIdByCode;
import kd.cus.wb.wsdl.client.updstub.SecretsUpdateStub.GetDeptIdByCodeResponse;
import kd.cus.wb.wsdl.client.updstub.SecretsUpdateStub.UpdateSecrets;
import kd.cus.wb.wsdl.client.updstub.SecretsUpdateStub.UpdateSecretsResponse;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;

public class CreateFlowToFWOppoPlugin extends AbstractOperationServicePlugIn{
	 private static Log logger = LogFactory.getLog(CreateFlowToFWOppoPlugin.class);
	/*获得的session*/
    private static String SESSION = "";
    
    private String CURRENCTUSERID = "";

    //参数
    private static String URL = "";
    private static String ACCOUNT = "";
    private static String PASSWORD = "";
    public static String CREATEURL = "";
    private static String USERURL = "";
    private static String DOCURL = "";
    private static String USERFIELD = "";
    private static String USTERTYPE = "";
    private static Boolean ISBUFFER = true;
    private static String BUFFER = "";
    public static String SECRETSURL = "";
    private static String ORGURL = "";
    private static String ORGFIELD = "";
    private static Boolean ISORGBUFFER = true;
    private static String ORGBUFFER = "";
    private static String SECURITYTYPE = "";
    private static String SECURITYTIME = "";
    
    private String securityClass = null;//密级
    private String securityTime = null;//保密期限
    private String reason = null;//用印事由
    private String contract = null;//采购合同号
    private String from = null;//来源单号
    private String sealType = null;//印章类型
    private String seal = null;//印章
    private String billType = null;//单据类型
    private String departmentLeader = null;//部门领导
    private String printer = null;//部门领导
    private String phone = null;//电话
    private String note = null;//备注
    private static String workflowid = null;
    
    private static final String HEAD_CODE = "bfgy_fields";
    private static final String ENTRYS_CODE = "bfgy_entrys";
    private static final String MAP_CODE = "bfgy_mapentry";
    private static DynamicObjectCollection HEAD_VALUE = null;
    private static DynamicObjectCollection ENTRYS_VALUE = null;
    private static DynamicObjectCollection Map_VALUE = null;
    
    private static Map<String,HeadEntity> head_value_map = new HashMap<String, HeadEntity>();
    private static Map<String, Map<String, BodySubentryEntity>> entry_value_map = new HashMap<String, Map<String,BodySubentryEntity>>();
    private static Map<String,Map<String,MapEntity>> map_value_map = new HashMap<String,Map<String,MapEntity>>();
  //配置参数
    private static Map<String, Object> fields = new HashMap<>();
	
    static {
        fields = SystemParamServiceHelper.loadBillParameterObjectFromCache("bfgy_printapplication");

        URL = (String) fields.get("bfgy_wb_url");//地址
        ACCOUNT = (String) fields.get("bfgy_wb_account");//账号
        PASSWORD = (String) fields.get("bfgy_wb_password");//密码
        
        CREATEURL = (String) fields.get("bfgy_create_url");//创建流程地址
        workflowid = (String) fields.get("bfgy_workflowid");//创建流程ID
        USERURL = (String) fields.get("bfgy_userid_url");//OA用户地址
        DOCURL = (String)fields.get("bfgy_createdoc");//创建文件url
        USERFIELD = (String) fields.get("bfgy_userfield");//苍穹人员对应参数
        USTERTYPE = (String) fields.get("bfgy_usertype");//OA用户类型
        ISBUFFER = (Boolean) fields.get("bfgy_checkbuffer"); //是否缓存泛微ID
        BUFFER = (String) fields.get("bfgy_buffer"); //缓存泛微ID位置
        SECRETSURL = (String) fields.get("bfgy_secret"); //密级url
        ORGURL = (String) fields.get("bfgy_org"); //部门url
        ORGFIELD = (String) fields.get("bfgy_orgfield"); //
        ISORGBUFFER = (Boolean) fields.get("bfgy_checkorgbuffer"); //
        ORGBUFFER = (String) fields.get("bfgy_orgbuffer"); //
        SECURITYTYPE = (String) fields.get("bfgy_securitytype"); //
        SECURITYTIME = (String) fields.get("bfgy_securitytime"); //
        
        HEAD_VALUE = (DynamicObjectCollection) fields.get(HEAD_CODE);
        ENTRYS_VALUE = (DynamicObjectCollection) fields.get(ENTRYS_CODE);
        Map_VALUE = (DynamicObjectCollection) fields.get(MAP_CODE);
        
        //HEAD get info
        HEAD_VALUE.stream().forEach(m->{
        	String xml = m.getString("bfgy_xmlfield");
        	String type = m.getString("bfgy_type");
        	String field = m.getString("bfgy_billfield");
        	head_value_map.put(xml, new HeadEntity(xml, type, field));
        });
        //Entry get info
        ENTRYS_VALUE.stream().forEach(m->{
        	String name = m.getString("bfgy_entrylogo");
        	DynamicObjectCollection cols = m.getDynamicObjectCollection("bfgy_subentry");
        	
        	List<BodySubentryEntity> lines = new ArrayList<BodySubentryEntity>(); 
        	Map<String, BodySubentryEntity> line_map = new HashMap<>(); 
        	cols.stream().forEach(n->{
        		String xml = n.getString("bfgy_entryxmlfield");
        		String type = n.getString("bfgy_entrytype");
        		String field = n.getString("bfgy_entrylogofield");
        		lines.add(new BodySubentryEntity(xml, type, field));
        		line_map.put(xml, new BodySubentryEntity(xml,type,field));
        	});
        	entry_value_map.put(name, line_map);
        });
        //map get info
        Map<String, List<DynamicObject>> map_value_group = Map_VALUE.stream().collect(Collectors.groupingBy(i->i.getString("bfgy_fieldname")));
        
        map_value_group.forEach((key,value)->{
        	Map<String, MapEntity> line_map = new HashMap<>(); 
        	value.forEach(m->{
        		String field = m.getString("bfgy_fieldname");
            	String oldvalue = m.getString("bfgy_srcvalue");
            	String newvalue = m.getString("bfgy_dstvalue");
            	line_map.put(oldvalue, new MapEntity(field, oldvalue, newvalue));
        	});
        	map_value_map.put(key, line_map);
        });
    }

	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		// TODO Auto-generated method stub
		super.onAddValidators(e);
		WriteMeetingOpinionValidator opinionVal = new WriteMeetingOpinionValidator();
		e.addValidator(opinionVal);
	}
	
	public class WriteMeetingOpinionValidator extends AbstractValidator {
		@Override
		public void validate() {
			ExtendedDataEntity[] data = this.getDataEntities();
			if (data.length > 0) {
				Arrays.asList(data).forEach(m->{
					DynamicObject this_dy = m.getDataEntity();
					WorkflowRequestInfo woInfo = getWorkFlowInfo(this_dy);
					Date startTime = new Date();
					String request = JSONObject.toJSONString(woInfo);
					String response = "";
					
					Boolean suc_flow = false;
					Boolean suc_upd = false;
					try {
						String result = postCreateFlow(woInfo, 84);
						if(Integer.parseInt(result) < 0) {
							this.addWarningMessage(m, "OA返回值：" + result + "请根据手册进行返回值确认！");
							response = "OA返回值：" + result + "请根据手册进行返回值确认！";
						}else {
							suc_flow = true;
							String updResult = updateSecurity(result,getSecuritytype(this_dy),getSecuritytime(this_dy));
							if(!"0".equalsIgnoreCase(updResult)) {
								this.addErrorMessage(m, "传输失败！");
								response = "传输失败！";
							}else {
								this.addMessage(m, "传输成功！流程id：" + result);
								response = "传输成功！流程id：" + result;
								suc_upd = true;
							}
						}
					} catch (MalformedURLException|RemoteException e) {
						// TODO Auto-generated catch block
						this.addErrorMessage(m, "传输出错！请检查网络问题！");
						response = "传输出错！请检查网络问题！";
					} 
					Date endTime = new Date();
					long subTime = endTime.getTime() - startTime.getTime();
					
					DynamicObject printapplication_dy = BusinessDataServiceHelper.newDynamicObject("bfgy_printapplication_l");
					printapplication_dy.set("bfgy_source", this_dy.getString("billno"));
					printapplication_dy.set("bfgy_subtime", subTime);
					printapplication_dy.set("bfgy_request_tag", request);
					printapplication_dy.set("bfgy_response_tag", response);
					printapplication_dy.set("bfgy_success", suc_flow&&suc_upd );
					
					SaveServiceHelper.save(new DynamicObject[] {printapplication_dy});
				});
			}
		}
	}
	
	public String getSecuritytype(DynamicObject this_dy) {
		return this_dy.getString(SECURITYTYPE);
	}
	
	public String getSecuritytime(DynamicObject this_dy) {
		return this_dy.getString(SECURITYTIME);
	}
	
	
	/**
	 * 创建流程接口getSecuritytime
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	private String postCreateFlow(WorkflowRequestInfo woInfo, int id) throws RemoteException, MalformedURLException {
		//执行创建流程接口
        String wsdl = CREATEURL;
        String requestStr = "";
//        Service service = new Service();
//        WorkflowServiceHttpBindingStub stub = new WorkflowServiceHttpBindingStub(new URL(wsdl), service);
//        requestStr = stub.doCreateWorkflowRequest(woInfo, id);
        //axis2
        WorkflowServiceStub stub = new WorkflowServiceStub();

		stub

        DoCreateWorkflowRequest doCreateWorkflowRequest = new DoCreateWorkflowRequest();
        doCreateWorkflowRequest.setIn0(woInfo);
        doCreateWorkflowRequest.setIn1(id);
        DoCreateWorkflowRequestResponse doCreateWorkflowRequestResponse = stub.doCreateWorkflowRequest(doCreateWorkflowRequest);
        requestStr = doCreateWorkflowRequestResponse.getOut();
        return requestStr;
	}
	
	/**
	 * 执行获得人员接口
	 * @param value
	 * @param type
	 * @return
	 * @throws MalformedURLException
	 * @throws RemoteException
	 */
	private String postGetId(String value, String type) throws MalformedURLException, RemoteException {
		//执行获得人员接口
        String wsdl = USERURL;
        String id = "";
//        Service service = new Service();
//        WorkflowServiceHttpBindingStub stub = new WorkflowServiceHttpBindingStub(new URL(wsdl), service);
//        id = stub.getUserId(type, value);
        //axis2
        WorkflowServiceStub stub = new WorkflowServiceStub(wsdl);
        
        GetUserId getUserId = new GetUserId();
        getUserId.setIn0(type);
        getUserId.setIn1(value);
        GetUserIdResponse getUserIdResponse = stub.getUserId(getUserId);
        id = getUserIdResponse.getOut();
		return id;
	}
	
	/**
	 * 
	 * @param requestId
	 * @param securitylevel
	 * @param securitylevelperiod
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	private String postUpdSecrets(String requestId, String securitylevel,String securitylevelperiod) throws RemoteException, MalformedURLException {
		//执行修改密级
		String wsdl = SECRETSURL;
        String id = "";
//        Service service = new Service();
//        SecretsUpdateHttpBindingStub stub = new SecretsUpdateHttpBindingStub(new URL(wsdl), service);
//        id = stub.updateSecrets(requestId, securitylevel, securitylevelperiod);
        SecretsUpdateStub stub = new SecretsUpdateStub(wsdl);
        UpdateSecrets updateSecrets = new UpdateSecrets();
        updateSecrets.setIn0(requestId);
        updateSecrets.setIn1(securitylevel);
        updateSecrets.setIn2(securitylevelperiod);
        UpdateSecretsResponse updateSecretsResponse = stub.updateSecrets(updateSecrets);
        id = updateSecretsResponse.getOut();
		return id;
	}

	/**
	 * 
	 * @param code
	 * @return
	 * @throws MalformedURLException
	 * @throws RemoteException
	 */
	private String postGetOrgId(String code) throws MalformedURLException, RemoteException {
		// 执行获得部门接口
		String wsdl = ORGURL;
		String id = "";
//		Service service = new Service();
//		SecretsUpdateHttpBindingStub stub = new SecretsUpdateHttpBindingStub(new URL(wsdl), service);
//		id = stub.getDeptIdByCode(code);
		//axis2
		SecretsUpdateStub stub = new SecretsUpdateStub(wsdl);
		GetDeptIdByCode getDeptIdByCode = new GetDeptIdByCode();
		getDeptIdByCode.setIn0(code);
		GetDeptIdByCodeResponse getDeptIdByCodeResponse = stub.getDeptIdByCode(getDeptIdByCode);
		id = getDeptIdByCodeResponse.getOut();
		
		JSONObject json = JSONObject.parseObject(id);
		if("0".equalsIgnoreCase(json.getString("resultCode"))){
			return json.getString("deptid");
		}else {
			return "0";
		}
	}
	
	public String updateSecurity(String requestId, String securitylevel, String securitylevelperiod) {
		try {
			securitylevel = mapValueFunction("securitytype",securitylevel);
			securitylevelperiod = mapValueFunction("securitylevelperiod",securitylevelperiod);
			
			String result = postUpdSecrets(requestId, securitylevel, securitylevelperiod);
			
			JSONObject json = JSONObject.parseObject(result);
			
			return json.getString("resultCode");
		} catch (RemoteException | MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new KDBizException("向泛微更新密级出错，请检查相应配置以及网络！"+e.getMessage());
		}
	}
	
	private String mapValueFunction(String xml, String oldvalue) {
		if (null != map_value_map.get(xml) && null != map_value_map.get(xml).get(oldvalue) && StringUtils.isNotEmpty(map_value_map.get(xml).get(oldvalue).getNewvalue())) {
			return map_value_map.get(xml).get(oldvalue).getNewvalue();
		}else {
			return oldvalue;
		}
	}
	
	/**
	 * 创建用印信息
	 * @param this_dy
	 * @return
	 */
	public WorkflowRequestInfo getWorkFlowInfo(DynamicObject this_dy) {
		ArrayOfWorkflowRequestTableField wrtis_head = new ArrayOfWorkflowRequestTableField(); //字段信息
		
		//主字段
		head_value_map.forEach((key, value) -> {
			// 映射规则
			String type = value.getType();
			String field = value.getField();
			String xml = value.getXml();

			WorkflowRequestTableField wrti_head = new WorkflowRequestTableField();
			wrti_head.setFieldName(value.getXml());

			if ("A".equalsIgnoreCase(type)) {
				wrti_head.setFieldValue(mapValueFunction(xml,field));
			} else if ("B".equalsIgnoreCase(type)) {
				wrti_head.setFieldValue(mapValueFunction(xml, (String) this_dy.get(field)));
			} else if ("C".equalsIgnoreCase(type)) {
				wrti_head.setFieldValue(getUserId(xml, this_dy.getDynamicObject(field)));
			} else if ("D".equalsIgnoreCase(type)) {
				wrti_head.setFieldValue(getOrgId(xml, this_dy.getDynamicObject(field)));
			}else if("E".equalsIgnoreCase(type)) {
				//wangqz modify 20211110 去掉附件名称最后的分号
				String strFileName =this_dy.getString("printedfilename");
				strFileName = strFileName.trim();
//				String  strPrintedFileName = strFileName.substring( 0,strFileName.length()-1);
//				logger.info("OA用印接口，strPrintedFileName："+strPrintedFileName);
				wrti_head.setFieldValue(getDocInfo(this_dy.getString(field),strFileName));
				
				//wrti_head.setFieldValue(getDocInfo(this_dy.getString(field),this_dy.getString("printedfilename")));
        	}else if("F".equalsIgnoreCase(type)) {
        		wrti_head.setFieldValue(getTime(this_dy.getDate(field)));
        	}else if("G".equalsIgnoreCase(type)) {
        		wrti_head.setFieldValue(this_dy.getBigDecimal(field).toString());
        	}
			wrti_head.setView(true);
			wrti_head.setEdit(true);
			wrtis_head.addWorkflowRequestTableField(wrti_head);
		});
		
		ArrayOfWorkflowRequestTableRecord wrtri_head = new ArrayOfWorkflowRequestTableRecord();//主字段只有一行数据
		WorkflowRequestTableRecord wRecord = new WorkflowRequestTableRecord();
		
		wRecord.setWorkflowRequestTableFields(wrtis_head);
		
		wrtri_head.addWorkflowRequestTableRecord(wRecord);
        WorkflowMainTableInfo wmi_head = new WorkflowMainTableInfo();
        wmi_head.setRequestRecords(wrtri_head);
        
        //明细字段
        
        
        ArrayOfWorkflowDetailTableInfo wdtis_entry = new ArrayOfWorkflowDetailTableInfo();
        entry_value_map.forEach((key, value)->{
        	DynamicObjectCollection this_dycols = this_dy.getDynamicObjectCollection(key);
        	
        	//单表信息
        	WorkflowDetailTableInfo wdti_entry = new WorkflowDetailTableInfo();
        	
        	ArrayOfWorkflowRequestTableRecord wrtris_entry = new ArrayOfWorkflowRequestTableRecord();
        	this_dycols.forEach(col_dy->{
        		
        		//单行信息
        		WorkflowRequestTableRecord wrtri_entry = new WorkflowRequestTableRecord();
            	ArrayOfWorkflowRequestTableField wrtis_entry = new ArrayOfWorkflowRequestTableField();
            	value.forEach((key_col, value_col)->{
            		//单个信息
            		WorkflowRequestTableField wrti_entry = new WorkflowRequestTableField();
            		wrti_entry.setFieldName(value_col.getXml());
            		
            		String type = value_col.getType();
            		String xml = value_col.getXml();
            		
            		if ("A".equalsIgnoreCase(type)) {
            			wrti_entry.setFieldValue(mapValueFunction(xml,value_col.getField()));
        			} else if ("B".equalsIgnoreCase(type)) {
        				wrti_entry.setFieldValue(mapValueFunction(xml, (String) (col_dy.get(value_col.getField()))));
        			} else if ("C".equalsIgnoreCase(type)) {
        				wrti_entry.setFieldValue(getUserId(xml, (col_dy.getDynamicObject(value_col.getField()))));
        			} else if ("D".equalsIgnoreCase(type)) {
        				wrti_entry.setFieldValue(getOrgId(xml, col_dy.getDynamicObject(value_col.getField())));
        			}else if("E".equalsIgnoreCase(type)) {
        				//wangqz modify 20211110 去掉附件名称最后的分号
        				String strFileName =col_dy.getString("printedfilename");
        				
//        				strFileName = strFileName.trim();
//        				String  strPrintedFileName = strFileName.substring( 0,strFileName.length()-1);
//        				logger.info("OA用印接口col_dy，strPrintedFileName："+strPrintedFileName);
        				wrti_entry.setFieldValue(getDocInfo(col_dy.getString(value_col.getField()),strFileName));
                	}else if("F".equalsIgnoreCase(type)) {
                		wrti_entry.setFieldValue(getTime(col_dy.getDate(value_col.getField())));
                	}else if("G".equalsIgnoreCase(type)) {
                		wrti_entry.setFieldValue(col_dy.getBigDecimal(value_col.getField()).toString());
                	}
            		
            		wrti_entry.setView(true);
            		wrti_entry.setEdit(true);
            		wrtis_entry.addWorkflowRequestTableField(wrti_entry);
            	});
            	wrtri_entry.setWorkflowRequestTableFields(wrtis_entry);
            	wrtris_entry.addWorkflowRequestTableRecord(wrtri_entry);
        	});
        	wdti_entry.setWorkflowRequestTableRecords(wrtris_entry);
        	wdtis_entry.addWorkflowDetailTableInfo(wdti_entry);
        });
        
        WorkflowBaseInfo wbi = new WorkflowBaseInfo();
        wbi.setWorkflowId(workflowid);//workflowid 5 代表内部留言
        logger.info("OA用印接口，workflowid："+workflowid);
        
        WorkflowRequestInfo wri = new WorkflowRequestInfo();//流程基本信息
        wri.setCreatorId(getCurrencyUserFWID());//创建人id
        wri.setRequestLevel("2");//0 正常，1重要，2紧急
        String strpurchasecontract = this_dy.getString("purchasecontract") +"合同OA用印";//wangqz modify 20211111 用印标题改为  合同号+合同OA用印
        wri.setRequestName(strpurchasecontract);//流程标题
      //  wri.setRequestName("OA用印");//流程标题
        wri.setWorkflowMainTableInfo(wmi_head);//添加主字段数据
        wri.setWorkflowBaseInfo(wbi);
        wri.setWorkflowDetailTableInfos(wdtis_entry);
		
		return wri;
	}
	
	private String getTime(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}
	
	private String getOrgId(String xml, DynamicObject this_dy) {
		this_dy = BusinessDataServiceHelper.loadSingle(this_dy.getPkValue(), this_dy.getDynamicObjectType().getName(), "id,name," + ORGFIELD);
		String value = this_dy.getString(ORGFIELD);
		
		value = mapValueFunction(xml, value);
		
		DynamicObject orgId_dy = BusinessDataServiceHelper.loadSingle(ORGBUFFER, "bfgy_number,bfgy_name,bfgy_oaid", new QFilter[]{new QFilter("bfgy_number", QCP.equals, value)});
		if(orgId_dy != null && orgId_dy.getString("bfgy_oaid") != null) {
			return orgId_dy.getString("bfgy_oaid");
		}else {
			String orgId;
			try {
				orgId = postGetOrgId(value);
			} catch (MalformedURLException | RemoteException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				throw new KDBizException("向泛微获取组织:"+value+" 的ID出错，请检查相应配置以及网络！"+e.getMessage());
			}
			if(Integer.parseInt(orgId) > 0 && ISORGBUFFER) {
				DynamicObject new_dy = BusinessDataServiceHelper.newDynamicObject(ORGBUFFER);
				new_dy.set("bfgy_number", value);
				new_dy.set("bfgy_name", this_dy.getString("name"));
				new_dy.set("bfgy_oaid", orgId);
				SaveServiceHelper.save(new DynamicObject[] {new_dy});
			}
			return orgId;
		}
	}
	
	/**
	 * 获得用户泛微系统ID
	 * @param this_dy
	 * @return
	 * @throws IOException
	 * @throws IOException
	 */
	private String getUserId(String xml, DynamicObject this_dy) {
		this_dy = BusinessDataServiceHelper.loadSingle(this_dy.getPkValue(), this_dy.getDynamicObjectType().getName(), "id,name," + USERFIELD);
		String value =this_dy.getString(USERFIELD);
		
		value = mapValueFunction(xml, value);
		//value ="xuyang";
		
		DynamicObject userId_dy = BusinessDataServiceHelper.loadSingle(BUFFER, "bfgy_number,bfgy_name,bfgy_oaid", new QFilter[]{new QFilter("bfgy_number", QCP.equals, value)});
		if (userId_dy != null && userId_dy.getString("bfgy_oaid") != null) {
			return userId_dy.getString("bfgy_oaid");
		} else {
			String userId;
			try {
				
				userId = postGetId(value, USTERTYPE);
			} catch (MalformedURLException | RemoteException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();				
				throw new KDBizException("向泛微获取"+value+"的人员ID出错，请检查相应配置以及网络！"+e.getMessage());
			}
			if(Integer.parseInt(userId) > 0 && ISBUFFER) {
				DynamicObject new_dy = BusinessDataServiceHelper.newDynamicObject(BUFFER);
				new_dy.set("bfgy_number", value);
				new_dy.set("bfgy_name", this_dy.getString("name"));
				new_dy.set("bfgy_oaid", userId);
				SaveServiceHelper.save(new DynamicObject[] {new_dy});
			}
			else
				throw new 	KDBizException(value+"的人员ID未查到！");
			
			return userId;
		}
	}
	
	/**
	 * 创建文档接口
	 * @param docInfo
	 * @param session
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	private int postCreateDoc(DocInfo docInfo, String session) throws RemoteException, MalformedURLException {
		//执行创建文件接口
		
        String wsdl = DOCURL;
        logger.info("OA用印接口，执行创建文件接口附件wsdl:"+wsdl);
//        Service service = new Service();
//        DocServiceHttpBindingStub stub = new DocServiceHttpBindingStub(new URL(wsdl), service);
        //axis2
        DocServiceStub stub = new DocServiceStub(wsdl);
        CreateDoc createDoc = new CreateDoc();
        createDoc.setIn0(docInfo);
        createDoc.setIn1(session);
        CreateDocResponse createDocResponse = stub.createDoc(createDoc);
        
        ServiceClient context = stub._getServiceClient();
        
        
        Integer id = createDocResponse.getOut();
//        logger.info("OA用印接口，执行创建文件接口附件 调用:");
        String strdocInfo = docInfo.toString();
        System.out.println(strdocInfo);
//        Integer id = stub.createDoc(docInfo, session);
        logger.info("OA用印接口，执行创建文件接口附件 id:"+id);
        
        OperationContext OperationContext  = new OperationContext();
		 
//		 OperationContext.setComplete(true);
		 
        ServiceContext serviceConxt = stub._getServiceClient().getServiceContext();
        
		 boolean cacheLastOperationContext = true;
		 serviceConxt.setCachingOperationContext(cacheLastOperationContext);
//		 serviceConxt.setLastOperationContext(OperationContext);
		 OperationContext operationContext = serviceConxt.getLastOperationContext();
		 
		 MessageContext outMessageContext = operationContext.getMessageContext("Out");
		 SOAPEnvelope envelopeout = outMessageContext.getEnvelope();
		 
		 String request = envelopeout.toString();
		 
//        MessageContext res = stub._getCall().getMessageContext();
        
//        String request = res.getRequestMessage().getSOAPPartAsString();
        logger.info("OA用印接口，执行创建文件接口附件 request:"+request);
//        String response = res.getResponseMessage().getSOAPPartAsString();
        logger.info("OA用印接口，执行创建文件接口附件 response:"+id);
        
		return id;
	}
	
	private String getCurrencyUserFWID() {
		if (StringUtils.isBlank(CURRENCTUSERID)) {
			DynamicObject user = UserServiceHelper.getCurrentUser(USERFIELD);
			CURRENCTUSERID = getUserId("THIS" ,user);
		}
		return CURRENCTUSERID;
	}
	
	private String getIP() {
		String IP = RequestContext.get().getLoginIP();
		 logger.info("OA用印接口，执行创建文件接口附件 postLogin IP:"+IP);
		return IP;
	}
	
	
	private String postLogin() throws RemoteException, MalformedURLException {
		//执行创建文件接口
        String wsdl = URL;
        DocServiceStub stub = new DocServiceStub(wsdl);
        logger.info("OA用印接口，执行创建文件接口附件 postLogin ACCOUNT:"+ACCOUNT);
        logger.info("OA用印接口，执行创建文件接口附件 postLogin PASSWORD:"+PASSWORD);
        Login login = new Login();
        login.setIn0(ACCOUNT);
        login.setIn1(PASSWORD);
        login.setIn2(0);
        login.setIn3(getIP());
        LoginResponse loginResponse = stub.login(login);
        String session = loginResponse.getOut();
//        String session = stub.login(ACCOUNT, PASSWORD, 0, getIP());
        logger.info("OA用印接口，执行创建文件接口附件 postLogin session:"+session);
		return session;
	}
	
	
	/**
	 * 文档信息
	 * @param url
	 * @return
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 * @throws  
	 * @throws RemoteException 
	 */
private String getDocInfo(String url, String name) {
	
//	url = "/jmup/1141938769906108416/202107/1205039300689793024/ce0d4d0db4ab45699e9fa8bd65c629bb/分配-引出数据_会计科目导入导出标准模板_0721.xlsx";
	
//	name = name.trim();
//	if(StringUtils.isNotBlank(name)) {
//		name.substring(0, name.length() - 1);
//	}
	
	 logger.info("OA用印接口，生成附件：url:"+url);
	 logger.info("OA用印接口，生成附件：name:"+name);
		String userid = "";
		
//		FileService fs=FileServiceFactory.getAttachmentFileService();
		URL httpUrl;
		byte[] content = new byte[10240000];
		
		try {
			
			if (url.contains("kdedcba")) {
				httpUrl = new URL(url);
			} else {
				httpUrl = new URL(EncreptSessionUtils.encryptSession(url));
			}
			
//			httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)httpUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(30 * 1000);
			InputStream inStream = conn.getInputStream();
//			String streamRead = ReadIOUtil.readIoToString(input);
			
//			File file = new File("C:\\Users\\suyp\\Downloads\\"+name);
//			
//			
//	            FileOutputStream fout = new FileOutputStream(file);
//	            int l = -1;
//	            byte[] tmp = new byte[1024];
//	            while ((l = inStream.read(tmp)) != -1) {
//	                fout.write(tmp, 0, l);
//	                // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
//	            }
//	            fout.flush();
//	            fout.close();
			
//			ReadIOUtil.readIoStringToFile(streamRead, "C:\\Users\\suyp\\Downloads\\"+name);
		
			// 上传附件，创建html文档
			content = null;
			userid = postGetId(ACCOUNT, USTERTYPE);
			logger.info("OA用印接口，生成附件：userid:"+userid);
			int byteread;
			byte data[] = new byte[1024];

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((byteread = inStream.read(data)) != -1) {
				out.write(data, 0, byteread);
				out.flush();
			}
			logger.info("OA用印接口，生成附件：ByteArrayOutputStream size:"+out.size());
			if(out.toString().contains("无效的文件访问")) {
				content = null;
			}else {
				content = out.toByteArray();
			}
			logger.info("OA用印接口，生成附件：content length:"+content.length);
			inStream.close();
			out.close();
		} catch (Exception e) {
			logger.info("OA用印接口，生成附件file Exception1:"+e.getMessage());
			e.printStackTrace();
		}

		Base64.Encoder encoder = Base64.getEncoder();
		DocAttachment da = new DocAttachment();
		da.setDocid(0);
		da.setImagefileid(0);
//		da.setFilecontent(Base64.encode(content));
		da.setFilecontent(content == null?null:encoder.encodeToString(content));
		da.setFilerealpath("url");
		da.setIszip(1);
		da.setFilename(name);
		da.setIsextfile("1");
		da.setDocfiletype("3");

		DocInfo doc = new DocInfo();//创建文档
		doc.setDoccreaterid(StringUtils.isEmpty(userid)?null:Integer.parseInt(userid));//
		doc.setDoccreatertype(0);
		doc.setAccessorycount(1);
		doc.setMaincategory(0);//主目录id
		doc.setSubcategory(0);//分目录id
		doc.setSeccategory(322);//子目录id
		//doc.setSeccategory(170);//子目录id
		doc.setOwnerid(Integer.parseInt(userid));
		doc.setDocStatus(1);
		doc.setId(0);
		doc.setDocType(2);
		doc.setDocSubject("service html 文档");
		doc.setDoccontent("service html 文档 ");
		
		ArrayOfDocAttachment arrayOfDocAttachment = new ArrayOfDocAttachment();
		arrayOfDocAttachment.addDocAttachment(da);
//		doc.setAttachments(new DocAttachment[] { da });
		doc.setAttachments(arrayOfDocAttachment);
		
		Integer res = null;
		try {
			res = postCreateDoc(doc, postLogin());
		} catch (RemoteException | MalformedURLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			logger.info("OA用印接口，生成附件：Exception:"+e.getMessage());
			throw new KDBizException(e.getMessage());
		}
		logger.info("OA用印接口，生成附件 postCreateDoc：res:"+res.toString());
		return res.toString();
	}


	/*private String getDocInfo(String url, String name) {
		 logger.info("OA用印接口，生成附件：url:"+url);
		 logger.info("OA用印接口，生成附件：name:"+name);
		FileService fs=FileServiceFactory.getAttachmentFileService();
		InputStream input = fs.getInputStream(url);
		String userid = "";
		logger.info("OA用印接口，生成附件：input:"+input.toString());
		
		byte[] content = new byte[102400];
		// 上传附件，创建html文档
		content = null; 
		try {
			userid = postGetId("xuyang", USTERTYPE);
			
			int byteread;
			byte data[] = new byte[1024];

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((byteread = input.read(data)) != -1) {
				out.write(data, 0, byteread);
				 logger.info("OA用印接口，生成附件：byteread:"+byteread);
				out.flush();
			}
			content = out.toByteArray();
			input.close();
			out.close();
		} catch (Exception e) {
			 logger.info("OA用印接口，生成附件：Exception:"+e.getMessage());
			e.printStackTrace();
		}
		 logger.info("OA用印接口，生成附件：content.length:"+content.length); 
		
		DocAttachment da = new DocAttachment();
		da.setDocid(0);
		da.setImagefileid(0);
		da.setFilecontent(Base64.encode(content));
		da.setFilerealpath("url");
		da.setIszip(1);
		da.setFilename(name);
		da.setIsextfile("1");
		da.setDocfiletype("3");

		DocInfo doc = new DocInfo();//创建文档
		doc.setDoccreaterid(Integer.parseInt(userid));//
		doc.setDoccreatertype(0);
		doc.setAccessorycount(1);
		doc.setMaincategory(0);//主目录id
		doc.setSubcategory(0);//分目录id
		doc.setSeccategory(322);//子目录id
		doc.setOwnerid(Integer.parseInt(userid));
		doc.setDocStatus(1);
		doc.setId(0);
		doc.setDocType(2);
		doc.setDocSubject("service html 文档");
		doc.setDoccontent("service html 文档 content");
		doc.setAttachments(new DocAttachment[] { da });		
		
		
		Integer res = null;
		try {
			res = postCreateDoc(doc, postLogin());
		} catch (RemoteException | MalformedURLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			logger.info("OA用印接口，生成附件 postCreateDoc：Exception:"+e.getMessage());
			throw new KDBizException(e.getMessage());
		}
		//logger.info("OA用印接口，生成附件 postCreateDoc：res:"+res.toString());
		return res.toString();
	}*/
}
