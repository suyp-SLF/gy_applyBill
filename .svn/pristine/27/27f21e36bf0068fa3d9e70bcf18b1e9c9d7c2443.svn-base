package kd.cus.wb.earlywarn;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.earlywarn.EarlyWarnContext;
import kd.bos.entity.earlywarn.warn.plugin.IEarlyWarnCustomReceiver;

/**
 * 出国预警
 * @author suyp
 *
 */
public class AbroadIEarlyWarnCustomPlugin implements IEarlyWarnCustomReceiver {

	@Override
	public List<Long> getReceiverIds(EarlyWarnContext arg0, DynamicObject[] arg1) {
		// TODO Auto-generated method stub
		
		List<DynamicObject> signs = Arrays.asList(arg1).stream().map(i->i.getDynamicObject("bfgy_oasign")).collect(Collectors.toList());
		List<DynamicObject> names = signs.stream().map(i->i.getDynamicObject("user_name")).collect(Collectors.toList());
		List<Long> ids = names.stream().map(i->i.getLong("id")).collect(Collectors.toList());
		
		return ids;
	}

}
