package kd.cus.wb.credit.botpplugin;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.metadata.dynamicobject.DynamicObjectType;
import kd.bos.db.DB;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.botp.plugin.AbstractConvertPlugIn;
import kd.bos.entity.botp.plugin.args.AfterFieldMappingEventArgs;
import kd.bos.fileservice.FileItem;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.org.utils.DynamicObjectUtils;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.util.FileNameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static kd.bos.servicehelper.AttachmentServiceHelper.SPECIAL_SYMBOLS;

public class GuaranteeApplyBotpPlugin extends AbstractConvertPlugIn {

    @Override
    public void afterFieldMapping(AfterFieldMappingEventArgs e) {
        // 取目标单，单据头数据包 （可能会生成多张单，是个数组）
        String targetEntityNumber = this.getTgtMainType().getName();
        ExtendedDataEntity[] billDataEntitys = e.getTargetExtDataEntitySet().FindByEntityKey(targetEntityNumber);
        //附件对象
        DynamicObjectType type = EntityMetadataCache.getDataEntityType("bd_attachment");

        DynamicObject attachtype = new DynamicObject();
        QFilter filter = new QFilter("number", QCP.equals, "8018-KLSQ");
        DynamicObject[] attachmenttypes = BusinessDataServiceHelper.load("bfgy_attachmenttype", "id", filter.toArray());
        if (attachmenttypes != null && attachmenttypes.length > 0) {
            attachtype = attachmenttypes[0];
        }

        // 逐单处理
        for (ExtendedDataEntity billDataEntity : billDataEntitys) {
            // 目标单
            DynamicObject dataEntity = billDataEntity.getDataEntity();
            DynamicObjectCollection attachmententity = dataEntity.getDynamicObjectCollection("attachmententity");

            // 取当前目标单，对应的源单行
            List<DynamicObject> srcRows = (List<DynamicObject>)billDataEntity.getValue("ConvertSource");
            // 取源单第一行上的字段值，忽略其他行
            DynamicObject srcRow = srcRows.get(0);
            Long id = (Long) srcRow.get("id");
            DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingleFromCache(id, "bfgy_guarantee_apply_wb");
            DynamicObjectCollection dynamicObjectCollection = dynamicObject.getDynamicObjectCollection("attachmententity");
            for (DynamicObject entryDyn : dynamicObjectCollection) {
                DynamicObject dyn = attachmententity.addNew();
                DynamicObjectCollection attachments = entryDyn.getDynamicObjectCollection("attachment");
                for(int h=0;h<attachments.size();h++) {
                    DynamicObject attachment = BusinessDataServiceHelper.loadSingle(attachments.get(h).getDynamicObject("fbasedataId").get("id"), type);
                    String oldUrl = attachment.getString("url");
                    //附件名称
                    String fileName = attachment.getString("name");
                    //创建一个新的附件对象
                    DynamicObject attachmentCopy = new DynamicObject(type);
                    //将数据复制一份到新对象
                    DynamicObjectUtils.copy(attachment, attachmentCopy);
                    String randomUUID = UUID.randomUUID().toString();
                    attachmentCopy.set("id",  DB.genGlobalLongId());
                    attachmentCopy.set("number", randomUUID);
                    attachmentCopy.set("uid", DB.genGlobalLongId());
                    //在数据库保存新附件对象
                    SaveServiceHelper.save(new DynamicObject[] {attachmentCopy});

                    FileService service = FileServiceFactory.getAttachmentFileService();
                    InputStream inputStream = service.getInputStream(oldUrl);
                    String tenantId = RequestContext.get().getTenantId();
                    String accountId = RequestContext.get().getAccountId();
                    String path = FileNameUtils.getAttachmentFileName(tenantId, accountId, attachmentCopy.getPkValue(), replaceSpeSymbol(fileName));
                    FileItem fileItem = new FileItem(fileName, path, inputStream);
                    String newUrl = service.upload(fileItem);
                    attachmentCopy.set("url", newUrl);
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    DynamicObjectCollection att = (DynamicObjectCollection) dyn.get("attachment");
                    att.addNew().set("fbasedataId", attachmentCopy);
                }

                dyn.set("attachmenttype", attachtype);
                dyn.set("attachmentnum", entryDyn.get("attachmentnum"));
                dyn.set("attachementsecurity", entryDyn.get("attachementsecurity"));
                dyn.set("attachementtime", entryDyn.get("attachementtime"));
                dyn.set("attachmentremark", entryDyn.get("attachmentremark"));
                dyn.set("attachmentuser", entryDyn.get("attachmentuser"));
                dyn.set("attachmentdate", entryDyn.get("attachmentdate"));
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
