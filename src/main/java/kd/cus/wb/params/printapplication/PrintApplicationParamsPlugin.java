package kd.cus.wb.params.printapplication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;

import com.alibaba.fastjson.JSONObject;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.TempFileCache;
import kd.bos.cache.tempfile.RedisTempFileCache;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.entity.datamodel.ListSelectedRowCollection;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.form.CloseCallBack;
import kd.bos.form.ConfirmCallBackListener;
import kd.bos.form.MessageBoxOptions;
import kd.bos.form.MessageBoxResult;
import kd.bos.form.ShowFormHelper;
import kd.bos.form.control.AttachmentPanel;
import kd.bos.form.control.Button;
import kd.bos.form.control.events.UploadEvent;
import kd.bos.form.control.events.UploadListener;
import kd.bos.form.events.ClosedCallBackEvent;
import kd.bos.form.events.MessageBoxClosedEvent;
import kd.bos.form.field.TextEdit;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListShowParameter;
import kd.bos.log.api.ILogService;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.DoCreateWorkflowRequest;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.DoCreateWorkflowRequestResponse;
import kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WorkflowRequestInfo;

public class PrintApplicationParamsPlugin extends AbstractFormPlugin implements UploadListener  {
	
	private static final String TXT_JSON_CODE = "bfgy_largetextfield";
	private static final String BTN_OUTPUT_CODE = "bfgy_output";
	private static final String BTN_INPUT_CODE = "bfgy_input";
	private static final String FORM_CODE = "bfgy_printapplication";
	private static String FILE_URL = "";
	
	@Override
	public void registerListener(EventObject e) {
		// TODO Auto-generated method stub
		super.registerListener(e);
		 Button output_btn = this.getControl(BTN_OUTPUT_CODE);
		 Button input_btn = this.getControl(BTN_INPUT_CODE);
		 Button outputtxt_btn = this.getControl("bfgy_outputtxt");
		 Button test_btn = this.getControl("bfgy_testbtn");
		 TextEdit choose_text = this.getControl("bfgy_oachoose");
		 AttachmentPanel attach = this.getControl("bfgy_attachmentpanelap");
		 output_btn.addClickListener(this);
		 input_btn.addClickListener(this);
		 outputtxt_btn.addClickListener(this);
		 test_btn.addClickListener(this);
		 choose_text.addClickListener(this);
		 attach.addUploadListener(this);
	}
	
	/**
	 * 文件上传时触发，可以在此事件接收已上传到文件服务器的文件相对URL
	 */
	@Override
	public void afterUpload(UploadEvent evt) {
		AttachmentPanel panel = (AttachmentPanel) evt.getSource();
;		List<Map<String, Object>> atta = panel.getAttachmentData();
		List<String> fileUrls = new ArrayList<>();
		
		for(Object url : evt.getUrls()){
			fileUrls.add((String) ((Map<String,Object>)url).get("url"));
		}
		
		if (fileUrls.size() == 1) {
			FILE_URL = fileUrls.get(0);
			ConfirmCallBackListener confirmCallBackListener = new ConfirmCallBackListener("iscover", this);
			this.getView().showConfirm("已经上传文件，是否覆盖当前配置文本！", MessageBoxOptions.YesNo, confirmCallBackListener);
		}else {
			this.getView().showMessage("当前文档上传错误，请清楚所有文档并重新上传！");
		}
	}
	
	@Override
    public void confirmCallBack(MessageBoxClosedEvent messageBoxClosedEvent) {
		super.confirmCallBack(messageBoxClosedEvent);
		if ("iscover".equalsIgnoreCase(messageBoxClosedEvent.getCallBackId())) {
			if (MessageBoxResult.Yes.equals(messageBoxClosedEvent.getResult())) {
				Object pp = this.getModel().getDataEntity();
				
				// 如果点击确认按钮，则把当前页面相关值清空
				String file_info = "";
					try {
						file_info = getFileStr(FILE_URL);
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
						this.getView().showMessage("文件读取失败！");
					}
				this.getModel().setValue("bfgy_largetextfield", file_info);
			} else if (MessageBoxResult.No.equals(messageBoxClosedEvent.getResult())) {
				
			}
		}
	}
	
	private String getFileStr(String url) throws MalformedURLException, IOException {
//		InputStream intstream = new URL(url).openStream();
//		byte[] bytes = new byte[0];
//		bytes = new byte[intstream.available()];
//		intstream.read(bytes);
//		String str = new String(bytes);
//		return str;
		/*redis获取*/
//		TempFileCache tempFileCache = CacheFactory.getCommonCacheFactory().getTempFileCache();
//		InputStream inputStream = tempFileCache.getInputStream(url);
		
		 FileService fs=FileServiceFactory.getAttachmentFileService();
         InputStream inputStream = fs.getInputStream(url);
		
//		RedisTempFileCache redisTempFileCache = new RedisTempFileCache();
//		InputStream inputStream = redisTempFileCache.getInputStream(url);
		
//		FileService fs=FileServiceFactory.getAttachmentFileService();
//		RequestContext requestContext = RequestContext.get();
//		InputStream inputStream = fs.getInputStream(url);
		
//		InputStream inputStream = HttpUtils.getInputStream(url);
		byte[] bytes = new byte[0];
		bytes = new byte[inputStream.available()];
		inputStream.read(bytes);
		String str = new String(bytes);
		return str;
	}
	
	@Override
	public void click(EventObject evt) {
		// TODO Auto-generated method stub
		super.click(evt);
		if(evt.getSource() instanceof Button) {
			Button thisBtn = (Button)evt.getSource();
			if("bfgy_output".equalsIgnoreCase(thisBtn.getKey())) {
				String jsonStr = getConfigJson();
				this.getModel().setValue(TXT_JSON_CODE, jsonStr);
			}else if("bfgy_input".equalsIgnoreCase(thisBtn.getKey())) {
				String txtStr = (String) this.getModel().getValue(TXT_JSON_CODE);
				Config config = JSONObject.parseObject(txtStr, Config.class);
				updateConfig(config);
			}else if("bfgy_outputtxt".equalsIgnoreCase(thisBtn.getKey())) {
				String jsonStr = getConfigJson();
				downloadTxt(jsonStr);
			}else if("bfgy_testbtn".equalsIgnoreCase(thisBtn.getKey())) {
				try {
					test();
				} catch (RemoteException | MalformedURLException e) {
					//问题
					// TODO Auto-generated catch block
					//e.printStackTrace();
					this.getView().showMessage(e.getMessage());
				}
			}
		}else if(evt.getSource() instanceof TextEdit) {
			TextEdit thisText = (TextEdit)evt.getSource();
			if("bfgy_oachoose".equalsIgnoreCase(thisText.getKey())) {
				openoalistForm();
			}
		}
	}
	
	private void downloadTxt(String str) {
		InputStream inputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		TempFileCache tempFileCache = CacheFactory.getCommonCacheFactory().getTempFileCache();
		String url = tempFileCache.saveAsUrl("OA用印配置文本.txt", inputStream, 5000);
		this.getView().openUrl(url);
	}
	
	private String test() throws RemoteException, MalformedURLException {
		String oatext = (String) this.getModel().getValue("bfgy_oachoose");
		DynamicObject dy = BusinessDataServiceHelper.loadSingle(oatext, FORM_CODE);
		CreateFlowToFWOppoPlugin createFlowToFWOppoPlugin = new CreateFlowToFWOppoPlugin();
		WorkflowRequestInfo workflowRequestInfo = createFlowToFWOppoPlugin.getWorkFlowInfo(dy);
		
		String wsdl = createFlowToFWOppoPlugin.CREATEURL;
        String requestStr = "";
//        Service service = new Service();
//        WorkflowServiceHttpBindingStub stub = new WorkflowServiceHttpBindingStub(new URL(wsdl), service);
//        requestStr = stub.doCreateWorkflowRequest(workflowRequestInfo, 84);
        
        WorkflowServiceStub stub = new WorkflowServiceStub();
        DoCreateWorkflowRequest doCreateWorkflowRequest = new DoCreateWorkflowRequest();
        doCreateWorkflowRequest.setIn0(workflowRequestInfo);
        doCreateWorkflowRequest.setIn1(84);
        DoCreateWorkflowRequestResponse doCreateWorkflowRequestResponse = stub.doCreateWorkflowRequest(doCreateWorkflowRequest);
        String response = doCreateWorkflowRequestResponse.getOut();
        ServiceContext serviceConxt = stub._getServiceClient().getServiceContext();
        
		 boolean cacheLastOperationContext = true;
		 serviceConxt.setCachingOperationContext(cacheLastOperationContext);
//		 serviceConxt.setLastOperationContext(OperationContext);
		 OperationContext operationContext = serviceConxt.getLastOperationContext();
		 
		 MessageContext outMessageContext = operationContext.getMessageContext("Out");
		 SOAPEnvelope envelopeout = outMessageContext.getEnvelope();
		 
		 String request = envelopeout.toString();
        
//        MessageContext context = stub._getCall().getMessageContext();
//        String requestMessage = context.getRequestMessage().getSOAPPartAsString();
//        String responseMessage = context.getResponseMessage().getSOAPPartAsString();
        
        //更改密级
        
        this.getModel().setValue("bfgy_requestmsg", request);
        this.getModel().setValue("bfgy_responsemsg", response);
        
        String result = createFlowToFWOppoPlugin.updateSecurity(requestStr,createFlowToFWOppoPlugin.getSecuritytype(dy),createFlowToFWOppoPlugin.getSecuritytime(dy));
        
        this.getView().showMessage(result);
        
        return requestStr;
	}
	
	private void openoalistForm() {
		//第一个参数为列表的单据标识，第二个参数为是否支持多选
        ListShowParameter showParameter = ShowFormHelper.createShowListForm(FORM_CODE, false);
        //设置弹出页面的样式
        showParameter.setCloseCallBack(new CloseCallBack(this, "choose"));
        this.getView().showForm(showParameter);
		
//		ListShowParameter listShowParameter = new ListShowParameter();
//    	//设置FormId，列表的FormId固定为"bos_list"
//    	listShowParameter.setFormId("bos_list");
//    	//设置BillFormId，为列表所对应单据的标识
//    	listShowParameter.setBillFormId(FORM_CODE);
//    	//设置弹出页面标题
//    	listShowParameter.setCaption("选择要传输的OA用印申请单：");
//    	StyleCss styleCss = new StyleCss();
//    	styleCss.setWidth("800");
//    	styleCss.setHeight("600");
//    	//设置弹出页面的样式
//    	listShowParameter.getOpenStyle().setInlineStyleCss(styleCss);
//    	//设置为不能多选，如果为true则表示可以多选
//    	listShowParameter.setMultiSelect(false);
//    	//设置页面回调事件方法
//    	listShowParameter.setCloseCallBack(new CloseCallBack(this, "choose"));
//    	//设置弹出页面的打开方式
//    	listShowParameter.getOpenStyle().setShowType(ShowType.Modal);
//    	//设置打开界面的目标容器
//    	listShowParameter.getOpenStyle().setTargetKey("tabap");
//    	//获取最外层根页面的View对象
//    	this.getView().showForm(listShowParameter);
	}
	
	@Override
	public void closedCallBack(ClosedCallBackEvent closedCallBackEvent) {
		// TODO Auto-generated method stub
		super.closedCallBack(closedCallBackEvent);
		if ("choose".equalsIgnoreCase(closedCallBackEvent.getActionId())) {
			ListSelectedRowCollection data = (ListSelectedRowCollection)closedCallBackEvent.getReturnData();
			if (data != null) {
				Object[] ids = data.getPrimaryKeyValues();
				if (ids.length == 1) {
					this.getModel().setValue("bfgy_oachoose", ids[0]);
				}else {
					this.getView().showErrorNotification("用印申请单只允许选择1条");
				}
			}
		}
	}
	
	/**
	 * 更新配置
	 * @param config
	 */
	private void updateConfig(Config config) {
		DynamicObject this_dy = this.getModel().getDataEntity(true);
		DynamicObjectCollection body_entry = this.getModel().getEntryEntity("bfgy_entrys");
		DynamicObjectCollection head_entry = this.getModel().getEntryEntity("bfgy_fields");
		DynamicObjectCollection map_entry = this.getModel().getEntryEntity("bfgy_mapentry");
		
		//登录参数
		Config1Entity loginInfo = config.getLoginInfo();
		this.getModel().setValue("bfgy_wb_url", loginInfo.getLogin());
		this.getModel().setValue("bfgy_wb_account", loginInfo.getAccount());
		this.getModel().setValue("bfgy_wb_password", loginInfo.getPassword());
		this.getModel().setValue("bfgy_create_url", loginInfo.getCreateurl());
		this.getModel().setValue("bfgy_userid_url", loginInfo.getUseridurl());
		this.getModel().setValue("bfgy_usertype", loginInfo.getUsertype());
		this.getModel().setValue("bfgy_checkbuffer", loginInfo.getIsbuffer());
		this.getModel().setValue("bfgy_buffer", loginInfo.getBuffer());
		this.getModel().setValue("bfgy_createdoc", loginInfo.getCreatedoc());
		
		Config3Entity fileInfo = config.getFileInfo();
		
		//创建流程参数
		Config2Entity createFlowInfo = config.getCreateFlow();
		/*头部信息*/
		List<HeadEntity> head = createFlowInfo.getHead();
		DynamicObjectType headType = head_entry.getDynamicObjectType();
		head_entry.clear();
		head.forEach(m->{
			DynamicObject head_dy = new DynamicObject(headType);
			head_dy.set("bfgy_xmlfield", m.getXml());
			head_dy.set("bfgy_type", m.getType());
			head_dy.set("bfgy_billfield", m.getField());
			head_dy.set("bfgy_headnote", m.getNote());
			head_entry.add(head_dy);
		});
		this.getModel().setValue("bfgy_fields", head_entry);
		/*单据体*/
		List<BodyEntryEntity> body = createFlowInfo.getBody();
		body_entry.clear();
		DynamicObjectType bodyType = body_entry.getDynamicObjectType();
		body.forEach(m->{
			DynamicObject body_dy = new DynamicObject(bodyType);
			DynamicObjectCollection lines_dys = body_dy.getDynamicObjectCollection("bfgy_subentry");
			DynamicObjectType lineType = lines_dys.getDynamicObjectType();
			body_dy.set("bfgy_entrylogo", m.getEntryname());
			body_dy.set("bfgy_entryxml", m.getXml());
			body_dy.set("bfgy_entrynote", m.getNote());
			List<BodySubentryEntity> lines = m.getLines();
			lines.forEach(n->{
				DynamicObject line_dy = new DynamicObject(lineType);
				line_dy.set("bfgy_entryxmlfield", n.getXml());
				line_dy.set("bfgy_entrytype", n.getType());
				line_dy.set("bfgy_entrylogofield", n.getField());
				line_dy.set("bfgy_subnote", n.getNote());
				lines_dys.add(line_dy);
			});
			body_dy.set("bfgy_subentry", lines_dys);
			body_entry.add(body_dy);
		});
		this.getModel().setValue("bfgy_entrys", body_entry);
		
		/*映射信息*/
		List<MapEntity> map = createFlowInfo.getMap();
		DynamicObjectType mapType = map_entry.getDynamicObjectType();
		map_entry.clear();
		map.forEach(m->{
			DynamicObject map_dy = new DynamicObject(mapType);
			map_dy.set("bfgy_fieldname", m.getField());
			map_dy.set("bfgy_srcvalue", m.getOldvalue());
			map_dy.set("bfgy_dstvalue", m.getNewvalue());
			map_dy.set("bfgy_mapnote", m.getNote());
			map_entry.add(map_dy);
		});
		this.getModel().setValue("bfgy_mapentry", map_entry);
		
		this.getView().invokeOperation("parametersave");
		this.getView().updateView();
	}
	
	/**
	 * 获得配置转成的json
	 * @return
	 */
	private String getConfigJson() {
		String url_value = (String) this.getModel().getValue("bfgy_wb_url");
		String account_value = (String) this.getModel().getValue("bfgy_wb_account");
		String password_value = (String) this.getModel().getValue("bfgy_wb_password"); 
		String createurl_value = (String) this.getModel().getValue("bfgy_create_url"); 
		String useridurl_value = (String) this.getModel().getValue("bfgy_userid_url"); 
		String usertype_value = (String) this.getModel().getValue("bfgy_usertype"); 
		Boolean isbuffer_value = (Boolean) this.getModel().getValue("bfgy_checkbuffer"); 
		String buffer_value = (String) this.getModel().getValue("bfgy_buffer"); 
		String createdoc_value = (String) this.getModel().getValue("bfgy_createdoc"); 
		DynamicObjectCollection head_entry = (DynamicObjectCollection) this.getModel().getEntryEntity("bfgy_fields");
		DynamicObjectCollection body_entry = (DynamicObjectCollection) this.getModel().getEntryEntity("bfgy_entrys");
		DynamicObjectCollection map_entry = (DynamicObjectCollection) this.getModel().getEntryEntity("bfgy_mapentry");
		
		Config config = new Config();
		//登录参数
		Config1Entity config_page1 = new Config1Entity();
		config_page1.setLogin(url_value);
		config_page1.setAccount(account_value);
		config_page1.setPassword(password_value);
		config_page1.setCreateurl(createurl_value);
		config_page1.setUseridurl(useridurl_value);
		config_page1.setUsertype(usertype_value);
		config_page1.setIsbuffer(isbuffer_value);
		config_page1.setBuffer(buffer_value);
		config_page1.setCreatedoc(createdoc_value);
		config.setLoginInfo(config_page1);
		//创建流程参数 
		Config2Entity config_page2 = new Config2Entity();
		/*头部信息*/
		List<HeadEntity> headlines = new ArrayList<HeadEntity>();
		head_entry.forEach(m->{
			HeadEntity headline = new HeadEntity();
			headline.setXml(m.getString("bfgy_xmlfield"));
			headline.setType(m.getString("bfgy_type"));
			headline.setField(m.getString("bfgy_billfield"));
			headline.setNote(m.getString("bfgy_headnote"));
			headlines.add(headline);
		});
		config_page2.setHead(headlines);
		/*单据体*/
		List<BodyEntryEntity> bodylines = new ArrayList<BodyEntryEntity>();
		body_entry.forEach(m->{
			BodyEntryEntity bodyline = new BodyEntryEntity();
			bodyline.setEntryname(m.getString("bfgy_entrylogo"));
			bodyline.setXml(m.getString("bfgy_entryxml"));
			bodyline.setNote(m.getString("bfgy_entrynote"));
			
			List<BodySubentryEntity> bodyline_lines = new ArrayList<BodySubentryEntity>();
			DynamicObjectCollection body_subentry = m.getDynamicObjectCollection("bfgy_subentry");
			body_subentry.forEach(n->{
				BodySubentryEntity bodyline_line = new BodySubentryEntity();
				bodyline_line.setXml(n.getString("bfgy_entryxmlfield"));
				bodyline_line.setType(n.getString("bfgy_entrytype"));
				bodyline_line.setField(n.getString("bfgy_entrylogofield"));
				bodyline_line.setNote(n.getString("bfgy_subnote"));
				bodyline_lines.add(bodyline_line);
			});
			bodyline.setLines(bodyline_lines);
			bodylines.add(bodyline);
		});
		config_page2.setBody(bodylines);
		
		/*数据映射配置*/
		List<MapEntity> maplines = new ArrayList<MapEntity>();
		map_entry.forEach(m->{
			MapEntity mapline = new MapEntity();
			mapline.setField(m.getString("bfgy_fieldname"));
			mapline.setOldvalue(m.getString("bfgy_srcvalue"));
			mapline.setNewvalue(m.getString("bfgy_dstvalue"));
			mapline.setNote(m.getString("bfgy_mapnote"));
			maplines.add(mapline);
		});
		config_page2.setMap(maplines);
		config.setCreateFlow(config_page2);
		//附件参数配置
		Config3Entity config_page3 = new Config3Entity();
		config.setFileInfo(config_page3);
		
		return JSONObject.toJSONString(config);
	}
	
}
