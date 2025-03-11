package kd.cus.wb.wsdl.client;

import java.rmi.RemoteException;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.context.ServiceContext;

import kd.cus.wb.wsdl.client.stub.DocServiceStub;
import kd.cus.wb.wsdl.client.stub.DocServiceStub.Login;
import kd.cus.wb.wsdl.client.stub.DocServiceStub.LoginResponse;

public class demo {
	
	public static void main(String[] args) throws ClassNotFoundException, RemoteException {
		
		
		
		 String wsdl = "http://166.111.116.3/services/DocService";
//	        String targetNamespace = "http://briup";
//	        WsClient client = new WsClient(wsdl);
//	        
//	        String opName = "login";
//	        Object[] opArgs = new Object[]{"xuyang","*#987Bg@1020!", 0, "0.0.0.0"};
//	        Class<?>[] opReturnType = new Class[]{String[].class};
//	        Object[] response = client.invokeOp(targetNamespace, opName, opArgs, opReturnType);
		 
		 DocServiceStub docServiceStub = new DocServiceStub(wsdl);
		 Login login = new Login();
		 login.setIn0("xuyang");
		 login.setIn1("*#987Bg@1020!");
		 login.setIn2(0);
		 login.setIn3("0.0.0.0");
		 LoginResponse result = docServiceStub.login(login);
		 
		 
		 ServiceContext serviceConxt = docServiceStub._getServiceClient().getServiceContext();
		 OperationContext OperationContext  = new OperationContext();
		 
//		 OperationContext.setComplete(true);
		 
		 boolean cacheLastOperationContext = true;
		 serviceConxt.setCachingOperationContext(cacheLastOperationContext);
//		 serviceConxt.setLastOperationContext(OperationContext);
		 OperationContext operationContext = serviceConxt.getLastOperationContext();
		 
		 MessageContext outMessageContext = operationContext.getMessageContext("Out");
		 SOAPEnvelope envelopeout = outMessageContext.getEnvelope();
	     System.out.println(envelopeout.getText());
	}
	
	
}
