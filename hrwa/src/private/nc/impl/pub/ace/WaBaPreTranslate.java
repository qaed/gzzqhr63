package nc.impl.pub.ace;

import nc.impl.hr.formula.parser.IFormulaParser;
import nc.vo.pub.BusinessException;

public class WaBaPreTranslate implements IFormulaParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2297942556734015047L;

	/**
	 * ͨ�õĹ�ʽ����
	 */
	public String parse(String pk_org, String formula, Object... params) throws BusinessException {
		// year,month,day,hour��second
		String resultFormualr = "";
		resultFormualr = formula.replace("\"", "'");
		if (resultFormualr.contains("dateadd") || resultFormualr.contains("datediff")) {
			resultFormualr = resultFormualr.replaceAll("'year'", "year");
			resultFormualr = resultFormualr.replaceAll("'month'", "month");
			resultFormualr = resultFormualr.replaceAll("'day'", "day");
			resultFormualr = resultFormualr.replaceAll("'hour'", "hour");
			resultFormualr = resultFormualr.replaceAll("'second'", "second");
		}
		return resultFormualr;

	}

}
