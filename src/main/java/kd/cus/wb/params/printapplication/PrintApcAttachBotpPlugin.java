package kd.cus.wb.params.printapplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import apusic.gdf.unitidx.api.exception.BizIndexException;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.db.DB;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.botp.plugin.AbstractConvertPlugIn;
import kd.bos.entity.botp.plugin.args.AfterConvertEventArgs;
import kd.bos.entity.botp.plugin.args.AfterFieldMappingEventArgs;
import kd.bos.fileservice.FileItem;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.fileservice.impl.AttachmentFileService;
import kd.bos.org.utils.DynamicObjectUtils;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.session.EncreptSessionUtils;
import kd.bos.url.UrlService;
import kd.bos.util.FileNameUtils;
/**
 * 采购合同botp下推用印申请单
 * 获取附件地址到用印申请单附件applyurl
 * @author SYQ
 *
 */
public class PrintApcAttachBotpPlugin extends AbstractConvertPlugIn{
	
	private static final String[] SPECIAL_SYMBOLS = new String[]{"%", "=", "+", "&"};
	
	@Override
	public void afterConvert(AfterConvertEventArgs e) {
		// TODO Auto-generated method stub
		super.afterConvert(e);
		
		// 取目标单，单据头数据包 （可能会生成多张单，是个数组）
		String targetEntityNumber = this.getTgtMainType().getName();		
		ExtendedDataEntity[] billDataEntitys = e.getTargetExtDataEntitySet().FindByEntityKey(targetEntityNumber);		
		for(ExtendedDataEntity aDataEntity : billDataEntitys) {
			DynamicObject dataEntity = aDataEntity.getDataEntity();
			long id = DB.genGlobalLongId();
			dataEntity.set("id", id);
		}
	}

	@Override
	public void afterFieldMapping(AfterFieldMappingEventArgs e) {
		// TODO Auto-generated method stub
		super.afterFieldMapping(e);
		// 取目标单，单据头数据包 （可能会生成多张单，是个数组）
		String targetEntityNumber = this.getTgtMainType().getName();		
		ExtendedDataEntity[] billDataEntitys = e.getTargetExtDataEntitySet().FindByEntityKey(targetEntityNumber);
		
		try {
			attachmentTransfer(billDataEntitys);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
	}

	private void attachmentTransfer(ExtendedDataEntity[] billDataEntitys) throws UnsupportedEncodingException {
		for (int i = 0; i < billDataEntitys.length; i++) {
			DynamicObject entity=billDataEntitys[i].getDataEntity();
				
			//上游采购合同
			DynamicObject loadSingle = BusinessDataServiceHelper.loadSingle( "bfgy_pm_wb_proconstract","attachmententity.attachment,", new QFilter("billno", QCP.equals, entity.get("purchasecontract")).toArray());

			if(loadSingle != null) {
				DynamicObjectCollection attachmententity = loadSingle.getDynamicObjectCollection("attachmententity");//获取上游单据附件单据体
				DynamicObjectCollection attachmententityHis = entity.getDynamicObjectCollection("entryentity");//获取当前附件单据体
				DynamicObjectType type = EntityMetadataCache.getDataEntityType("bd_attachment");
				for(int j=0; j<attachmententity.size();j++ ) {					
					//附件
					DynamicObjectCollection bfgy_attachmentfield =  (DynamicObjectCollection) attachmententity.get(j).get("attachment"); //上游获取附件字段对象
					
					for(int h=0;h<bfgy_attachmentfield.size();h++) {
						DynamicObject attachment = BusinessDataServiceHelper.loadSingle(bfgy_attachmentfield.get(h).getDynamicObject("fbasedataId").get("id"), type);//要下推的附件对象				
						String oldUrl = attachment.getString("url");//原单上的附件存在服务器的路径
						String fileName = attachment.getString("name");//附件名称
						
						fileName = fileName.trim();
						if(StringUtils.isNotBlank(fileName)) {
							fileName.substring(0, fileName.length() - 1);
							fileName = fileName.replace(" ","-");
						}
//				         DynamicObject attachmentCopy = new DynamicObject(type);//创建一个新的附件对象
//				         DynamicObjectUtils.copy(attachment, attachmentCopy);//将数据复制一份到新对象

//				         String randomUUID = UUID.randomUUID().toString();
//				         attachmentCopy.set("id",  DB.genGlobalLongId());
//				         attachmentCopy.set("number", randomUUID);
//				         attachmentCopy.set("uid", DB.genGlobalLongId());
//				         SaveServiceHelper.save(new DynamicObject[] {attachmentCopy});//在数据库保存新附件对象

//				         AttachmentFileService service = (AttachmentFileService)FileServiceFactory.getAttachmentFileService();
//				         String preurl = System.getProperty("imageServer.external");
//				         String newUrl = preurl.concat(oldUrl);
//						 InputStream inputStream = service.getInputStream(oldUrl);
//						
//						 String tenantId = RequestContext.get().getTenantId();
//				         String accountId = RequestContext.get().getAccountId();
//				         String path = FileNameUtils.getAttachmentFileName(tenantId, accountId, attachmentCopy.getPkValue(), replaceSpeSymbol(fileName));
//				         FileItem fileItem = new FileItem(fileName, path, inputStream);
//				         String newUrl = service.upload(fileItem);
				         
						String fullUrl = oldUrl;
						if (!oldUrl.startsWith("http://")) {
							fullUrl = UrlService.getAttachmentFullUrl(URLEncoder.encode(oldUrl, "UTF-8"));
						}
	
						fullUrl = EncreptSessionUtils.encryptSession(fullUrl);
							
						DynamicObject aaa = attachmententityHis.get(j);
						aaa.set("applyurl", fullUrl);
						aaa.set("filename", fileName);
					}		
				}
			}
		}
	}
	
	private static String replaceSpeSymbol(String str) {
      for(int i = 0; i < SPECIAL_SYMBOLS.length; ++i) {
          if (str.contains(SPECIAL_SYMBOLS[i])) {
             str = str.replace(SPECIAL_SYMBOLS[i], "_");
          }      
      }
       return str;
    }
}