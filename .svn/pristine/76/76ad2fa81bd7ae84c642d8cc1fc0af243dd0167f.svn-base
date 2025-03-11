package kd.cus.wb.ar.applybill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.AddValidatorsEventArgs;
import kd.bos.entity.validate.AbstractValidator;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

/**
 * 开票申请单|操作插件，操作之后设置bfgy_isbolish=true
 * @author suyp
 *
 */
public class ChangeFieldOppoPlugin extends AbstractOperationServicePlugIn{
	@Override
	public void onAddValidators(AddValidatorsEventArgs e) {
		// TODO Auto-generated method stub
		super.onAddValidators(e);
		e.addValidator(new WriteMeetingOpinionValidator());
	}
	
	public class WriteMeetingOpinionValidator extends AbstractValidator {
		@Override
		public void validate() {
 			ExtendedDataEntity[] data = this.getDataEntities();
 			if(data.length > 0) {
 				String name = data[0].getDataEntity().getDataEntityType().getName();
 				List<DynamicObject> Ids = Arrays.asList(data).stream().map(i->i.getDataEntity()).collect(Collectors.toList());
// 				Map<Object, DynamicObject> dys = BusinessDataServiceHelper.loadFromCache(Ids.toArray(new Object[Ids.size()]), name);
// 				List<DynamicObject> result = new ArrayList<DynamicObject>();
 				Ids.stream().forEach(dy -> {
 					dy.set("bfgy_isbolish", true);
 				});
 				SaveServiceHelper.save(Ids.toArray(new DynamicObject[Ids.size()]));
 			}
		}
	}
}
