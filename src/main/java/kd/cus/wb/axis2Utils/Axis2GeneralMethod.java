package kd.cus.wb.axis2Utils;

import org.apache.axis2.AxisFault;

public class Axis2GeneralMethod {
	
	public static void main(String[] args) throws AxisFault, ClassNotFoundException {
		String wsdl = "http://10.116.20.55:6888/ormrpc/services/WSTP_BC_WebServiceFacade";
		
        String targetNamespace = "http://briup";
        WsClient client = new WsClient(wsdl);
        
        String opName = "AddNewORUpdateCustomers";
        Object[] opArgs = new Object[]{"[{\"customer_name_us\":\"test0012\",\"cosmicCode\":\"02\",\"customer_number\":\"test0012\",\"customer_name\":\"test0012\"}]"};
        Class<?>[] opReturnType = new Class[]{String[].class};
        Object[] response = client.invokeOp(targetNamespace, opName, opArgs, opReturnType);
		System.out.println(response);
	}
}
