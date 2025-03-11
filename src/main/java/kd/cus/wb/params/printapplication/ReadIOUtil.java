package kd.cus.wb.params.printapplication;

import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import cfca.sadk.org.bouncycastle.jcajce.provider.symmetric.ARC4.Base;
import kd.drp.mem.yzj.config.SSLClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
 
/**
 * 读取文件流，字符串和文件流互转
 * @author luohui
 * @create 2019/8/6
 * @since 1.0.0
 */
public class ReadIOUtil {
	/**
	 * 把文件流转成字符串
	 * @param is
	 * @return
	 */
	public static String readIoToString(InputStream is) {
		String result = null;
		byte[] data = null;
		try {
			data = new byte[is.available()];
			is.read(data);
			Base64.Encoder encoder = Base64.getEncoder();
			result =  encoder.encodeToString(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(data != null) {
				data = null;
			}
		}
		return result;
	}
 
	/**
	 * 把IO字符串输出到文件
	 * @param ioString
	 * @param filePath
	 */
	public static void readIoStringToFile(String ioString, String filePath) {
		FileOutputStream fos = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
 
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] bytes = decoder.decode(ioString);
			
			fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void getFile(String url, String destFileName) {
	    String result = null;
	    HttpClient httpClient = null;
	    try {
	        httpClient = new SSLClient();
	        HttpGet httpGet = new HttpGet(url);
	        httpGet.setHeader("111", "111");
	        httpGet.setHeader("111", "111");
	        HttpResponse response = httpClient.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream in = entity.getContent();
	        File file = new File(destFileName);
	        try {
	            FileOutputStream fout = new FileOutputStream(file);
	            int l = -1;
	            byte[] tmp = new byte[1024];
	            while ((l = in.read(tmp)) != -1) {
	                fout.write(tmp, 0, l);
	                // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
	            }
	            fout.flush();
	            fout.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            // 关闭低层流。
	            in.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
