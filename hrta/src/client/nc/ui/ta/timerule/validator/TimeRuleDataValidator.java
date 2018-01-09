package nc.ui.ta.timerule.validator;

import nc.bs.uif2.validation.ValidationFailure;
import nc.bs.uif2.validation.Validator;
import nc.hr.utils.ResHelper;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timerule.TimeRuleVO;

/**
 * ���ڹ���У��
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class TimeRuleDataValidator implements Validator {

	@Override
	public ValidationFailure validate(Object obj) {
		if (obj == null)
			return null;
		TimeRuleVO vo = (TimeRuleVO) obj;
		Integer autoArrangeMonth = vo.getAutoarrangemonth();
		if (autoArrangeMonth != null && autoArrangeMonth.intValue() > 12)
			return new ValidationFailure(ResHelper.getString("6017basedoc", "06017basedoc1918", 12 + "")
			/*@res "�Զ��Ű�������������{0}!"*/);
		return null;
	}

}
