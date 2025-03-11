package kd.cus.wb.usethe;

import com.alibaba.fastjson.JSONObject;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.login.utils.sms.SMSSender;
import kd.bos.util.HttpClientUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CaptchaSendPlugin implements SMSSender{
	
	
	@Override
	public boolean sendMessage(String phone, String message, String signature) {
//		String countryCode = "86";
		return sendMessage(phone, message);
	}
	
	@Override
	public boolean sendMessage(String phone, String content) {
		// TODO Auto-generated method stub
		Log logger = LogFactory.getLog(CaptchaSendPlugin.class);
		logger.info("触发发送验证码：" + "\t\r\n"
				+ " phone = " + phone + "\t\r\n"
				+ " content =  "+ content + "\t\r\n");
		try {
//			int i=SingletonClient.getClient().sendSMS(new String[] {phone}, "【北方工业】" + content ,5);
//			if (i == 0) {
//				logger.info("验证码发送成功");
//			} else {
//				logger.info("验证码发送失败" + i);
//			}
			String response = HttpClientUtils.get("http://bjmtn.b2m.cn/simpleinter/sendSMS?appId=8SDK-EMY-6699-SDZRP&timestamp=20200609161215&sign=0AA9A6172D5DEABDE29F93754263567C&mobiles=" + phone + "&content=【北方工业】" + content);

			InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
			SAXReader reader = new SAXReader();
			Document doc = reader.read(is);

			String resJSONSTR = doc.getRootElement().elementText("data");
			JSONObject resJSON = JSONObject.parseObject(resJSONSTR);
			String code = resJSON.getString("code");
			String data = resJSON.getString("data");

			if ("SUCCESS".equals(code)){
				logger.info("验证码发送成功：" + code + "\r\n" +
						"data:" + data + "\r\n");
			}else {
				logger.error("验证码发送失败：code：" + code +"\r\n" +
						"data:" + data + "\r\n");
			}
		} catch (Exception e) {
			logger.error("验证码发送失败,代码逻辑错误：" + e.getMessage(),e);
		}
		return true;
	}
}
