package kd.cus.wb.usethe;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.exception.KDBizException;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.servicehelper.parameter.SystemParamServiceHelper;
//import kd.cus.wb.FWworkflow.WorkflowBaseInfo;
//import kd.cus.wb.FWworkflow.WorkflowDetailTableInfo;
//import kd.cus.wb.FWworkflow.WorkflowMainTableInfo;
//import kd.cus.wb.FWworkflow.WorkflowRequestInfo;
//import kd.cus.wb.FWworkflow.WorkflowRequestTableField;
//import kd.cus.wb.FWworkflow.WorkflowRequestTableRecord;
//import kd.cus.wb.FWworkflow.WorkflowServiceHttpBindingStub;
import kd.epm.eb.common.model.DynamicInfoCollection;

//import org.apache.axis.Constants;
//import org.apache.axis.MessageContext;
//import org.apache.axis.client.Call;
//import org.apache.axis.client.Service;
//import org.apache.axis.encoding.XMLType;
//import org.apache.axis.encoding.ser.BeanDeserializerFactory;
//import org.apache.axis.encoding.ser.BeanSerializerFactory;
//import org.apache.axis.message.SOAPHeaderElement;
//import org.apache.axis.transport.http.HTTPConstants;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONObject;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAarchiveoppoPlugin extends AbstractBillPlugIn {
    private static final Log logger = LogFactory.getLog(OAarchiveoppoPlugin.class);
    /*获得的session*/
    private static String SESSION = "";

    //参数
    private static String URL = "";
    private static String ACCOUNT = "";
    private static String PASSWORD = "";
    
    
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
    
    private static final String HEAD_CODE = "bfgy_fields";
    private static final String ENTRYS_CODE = "bfgy_entrys";
    private static final String SUBENTRY_CODE = "bfgy_subentry";
    private static DynamicObjectCollection HEAD_VALUE = null;
    private static DynamicObjectCollection ENTRYS_VALUE = null;
    
    //配置参数
    private static Map<String, Object> fields = new HashMap<>();

    static {
        fields = SystemParamServiceHelper.loadBillParameterObjectFromCache("bfgy_printapplication");
        System.out.println(1);

        URL = (String) fields.get("bfgy_wb_url");//地址
        ACCOUNT = (String) fields.get("bfgy_wb_account");//账号
        PASSWORD = (String) fields.get("bfgy_wb_password");//密码
        
        HEAD_VALUE = (DynamicObjectCollection) fields.get(HEAD_CODE);
        ENTRYS_VALUE = (DynamicObjectCollection) fields.get(ENTRYS_CODE);
    }

    /**
     * 点击事件
     */
    public void itemClick(ItemClickEvent evt) {
        super.itemClick(evt);
        if ("Archive_wb".equalsIgnoreCase(evt.getOperationKey())) {
        	buildXml();
        } else if("".equalsIgnoreCase(evt.getOperationKey())) {
        	
        }
    }
    
    
    /**
     * 获得信息
     */
    private void buildXml() {
//    	JSONObject Head_value_JSON = JSONObject.parseObject(HEAD_VALUE);
    	if(HEAD_VALUE.size() > 0) {
    		HEAD_VALUE.forEach(m->{
        		String xmlfield = m.getString("bfgy_xmlfield");//xml(外层)
        		String type = m.getString("bfgy_type");//映射方式
        		String billfield = m.getString("bfgy_billfield");//表单字段
        		
        		
        	});
    	}else {
    		this.getView().showMessage("未配置流程头部信息，请查看是否配置，路径：配置工具-参数配置-单据参数-用印申请单-范围参数配置-创建流程参数-头部信息");
    	}
    	
    	if(ENTRYS_VALUE.size() > 0) {
    		ENTRYS_VALUE.forEach(m->{
    			DynamicObjectCollection subentry_values = m.getDynamicObjectCollection(SUBENTRY_CODE);
    			if(subentry_values.size() > 0) {
    				subentry_values.forEach(n->{
    					String entryxmlfield = n.getString("bfgy_entryxmlfield");
    					String entrytype = n.getString("bfgy_entrytype");
    					String entrylogofield = n.getString("bfgy_entrylogofield");
    				});
    			}
    		});
    	}
    	
    	System.out.println(1);
    }


    /**
     * 登录更新
     *
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws ServiceException
     */
//    private void updateSession() throws ParserConfigurationException, IOException, SAXException, ServiceException {
//
//        Service serv = new Service();
//        Call call = (Call) serv.createCall();
//        call.setTargetEndpointAddress(URL);
//        call.setOperationName(new QName("http://localhost/services/DocService", "login", "doc"));
//        call.addParameter(new QName("http://localhost/services/DocService", "in0", "doc"), Constants.XSD_STRING, ParameterMode.IN);
//        call.addParameter(new QName("http://localhost/services/DocService", "in1", "doc"), Constants.XSD_STRING, ParameterMode.IN);
//        call.addParameter(new QName("http://localhost/services/DocService", "in2", "doc"), Constants.XSD_INT, ParameterMode.IN);
//        call.addParameter(new QName("http://localhost/services/DocService", "in3", "doc"), Constants.XSD_STRING, ParameterMode.IN);
//
//        call.setEncodingStyle("UTF-8");
//        call.setReturnType(XMLType.XSD_STRING);
//    }

    /**
     * 创建流程
     * @throws MalformedURLException
     */
//    private String creatFlow() throws ServiceException, RemoteException, MalformedURLException {
//    	  List<Object[]> lists = new ArrayList<>();
//          lists.add(new Object[]{"dyr","",true,true});//打印人
//          lists.add(new Object[]{"qtsx","test",true,true});//其他事项
//          lists.add(new Object[]{"nrzy","摘要",true,true});//内容摘要
//          lists.add(new Object[]{"bz1","beizhu",true,true});//备注
//          lists.add(new Object[]{"PDF","",true,true});//转PDF
//          lists.add(new Object[]{"hqbm","",true,true});//会签部门
//          lists.add(new Object[]{"bh","001",true,true});//编号
//          lists.add(new Object[]{"szgs","001",true,true});//所在公司
//          lists.add(new Object[]{"sqbm","",true,true});//申请部门
//          lists.add(new Object[]{"sqr","",true,true});//申请人
//          lists.add(new Object[]{"sqrq","",true,true});//申请日期
//          lists.add(new Object[]{"dh","15433333333",true,true});//电话
//          lists.add(new Object[]{"sxfl","",true,true});//事项分类
//          lists.add(new Object[]{"jh","",true,true});//急缓
//          lists.add(new Object[]{"bz","",true,true});//备注
//          lists.add(new Object[]{"jbbmd","",true,true});//经办部门领导
//          lists.add(new Object[]{"bmhqr","001",true,true});//部门核签人
//          lists.add(new Object[]{"yz","1",true,true});//印章
//          lists.add(new Object[]{"yzlb","1",true,true});//印章类别
//          lists.add(new Object[]{"bmld1","",true,true});//部门领导
//          lists.add(new Object[]{"gsld1","",true,true});//公司领导
//          lists.add(new Object[]{"hqbmsp","",true,true});//会签部门审批
//          lists.add(new Object[]{"gsld","",true,true});//fff公司领导
//          lists.add(new Object[]{"hqbmld","",true,true});//fff会签部门领导
//          lists.add(new Object[]{"yyjmc","测试用印00184",true,true});//fff用印件名称
//
//          /**
//           *创建流程，支持多明细，并且带附件字段--目前只支持一个附件，并且是（http格式的）
//           * @throws Exception
//           */
//          //主字段
//          WorkflowRequestTableField[] wrti = new WorkflowRequestTableField[lists.size()]; //字段信息
//
//          for (int i = 0; i < lists.size(); i++) {
//              wrti[i] = new WorkflowRequestTableField();
//              wrti[i].setFieldName((String)lists.get(i)[0]);
//              wrti[i].setFieldValue((String)lists.get(i)[1]);
//              wrti[i].setView((Boolean) lists.get(i)[2]);
//              wrti[i].setEdit((Boolean) lists.get(i)[3]);
//          }
//
//          wrti[3] = new WorkflowRequestTableField();
//          wrti[3].setFieldName("fj2");//附件
//          wrti[3].setFieldType("http:baidu_sylogo1.gif");//http:开头代表该字段为附件字段
//          wrti[3].setFieldValue("http://www.baidu.com/img/baidu_sylogo1.gif");//附件地址
//          wrti[3].setView(true);
//          wrti[3].setEdit(true);
//
//          WorkflowRequestTableRecord[] wrtri = new WorkflowRequestTableRecord[1];//主字段只有一行数据
//          wrtri[0] = new WorkflowRequestTableRecord();
//          wrtri[0].setWorkflowRequestTableFields(wrti);
//
//          WorkflowMainTableInfo wmi = new WorkflowMainTableInfo();
//          wmi.setRequestRecords(wrtri);
//
//
//          //明细字段
//          WorkflowDetailTableInfo wdti[] = new WorkflowDetailTableInfo[2];//两个明细表0明细表1,1明细表2
//
//          //明细表1 start
//          wrtri = new WorkflowRequestTableRecord[2];//数据 行数，假设添加2行明细数据
//          //第一行
//          wrti = new WorkflowRequestTableField[3]; //每行3个字段
//          wrti[0] = new WorkflowRequestTableField();
//          wrti[0].setFieldName("sl");//数量
//          wrti[0].setFieldValue("11");
//          wrti[0].setView(true);
//          wrti[0].setEdit(true);
//
//          wrti[1] = new WorkflowRequestTableField();
//          wrti[1].setFieldName("dj");//单价
//          wrti[1].setFieldValue("2");
//          wrti[1].setView(true);
//          wrti[1].setEdit(true);
//
//          wrti[2] = new WorkflowRequestTableField();
//          wrti[2].setFieldName("xj");//小记
//          wrti[2].setFieldValue("22");
//          wrti[2].setView(true);
//          wrti[2].setEdit(true);
//
//          wrtri[0] = new WorkflowRequestTableRecord();
//          wrtri[0].setWorkflowRequestTableFields(wrti);
//
//          //第二行
//          wrti = new WorkflowRequestTableField[3]; //每行3个字段
//          wrti[0] = new WorkflowRequestTableField();
//          wrti[0].setFieldName("sl");//数量
//          wrti[0].setFieldValue("110");
//          wrti[0].setView(true);
//          wrti[0].setEdit(true);
//
//          wrti[1] = new WorkflowRequestTableField();
//          wrti[1].setFieldName("dj");//单价
//          wrti[1].setFieldValue("2");
//          wrti[1].setView(true);
//          wrti[1].setEdit(true);
//
//          wrti[2] = new WorkflowRequestTableField();
//          wrti[2].setFieldName("xj");//小记
//          wrti[2].setFieldValue("220");
//          wrti[2].setView(true);
//          wrti[2].setEdit(true);
//
//          wrtri[1] = new WorkflowRequestTableRecord();
//          wrtri[1].setWorkflowRequestTableFields(wrti);
//
//          wdti[0] = new WorkflowDetailTableInfo();
//          wdti[0].setWorkflowRequestTableRecords(wrtri);//加入明细表1的数据
//          //明细表1 end
//
//          //明细表2 start
//          wrtri = new WorkflowRequestTableRecord[1];//数据行数，假设添加1行明细数据
//
//          //第一行
//          wrti = new WorkflowRequestTableField[3]; //每行3个字段
//          wrti[0] = new WorkflowRequestTableField();
//          wrti[0].setFieldName("cl3");//
//          wrti[0].setFieldValue("11");
//          wrti[0].setView(true);
//          wrti[0].setEdit(true);
//
//          wrti[1] = new WorkflowRequestTableField();
//          wrti[1].setFieldName("cl1111");//
//          wrti[1].setFieldValue("2");
//          wrti[1].setView(true);
//          wrti[1].setEdit(true);
//
//          wrtri[0] = new WorkflowRequestTableRecord();
//          wrtri[0].setWorkflowRequestTableFields(wrti);
//
//          wdti[1] = new WorkflowDetailTableInfo();
//          wdti[1].setWorkflowRequestTableRecords(wrtri);//加入明细表2的数据
//          //明细表2 end
//
//          WorkflowBaseInfo wbi = new WorkflowBaseInfo();
//          wbi.setWorkflowId("5");//workflowid 5 代表内部留言
//
//          WorkflowRequestInfo wri = new WorkflowRequestInfo();//流程基本信息
//          wri.setCreatorId("84");//创建人id
//          wri.setRequestLevel("2");//0 正常，1重要，2紧急
//          wri.setRequestName("留言测试接口");//流程标题
//          wri.setWorkflowMainTableInfo(wmi);//添加主字段数据
//          wri.setWorkflowBaseInfo(wbi);
//          wri.setWorkflowDetailTableInfos(wdti);
//
//          //执行创建流程接口
//          String wsdl = "http://166.111.116.3/services/WorkflowService";
//          String requestStr = "";
//          Service service = new Service();
//          WorkflowServiceHttpBindingStub stub = new WorkflowServiceHttpBindingStub(new URL(wsdl), service);
//          requestStr = stub.doCreateWorkflowRequest(wri, 84);
//          System.out.println(requestStr);
//        return requestStr;
//    }
}
