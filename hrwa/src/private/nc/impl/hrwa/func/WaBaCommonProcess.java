package nc.impl.hrwa.func;

import java.text.MessageFormat;
import java.text.ParseException;

import nc.bs.dao.BaseDAO;
import nc.hr.utils.ResHelper;
import nc.impl.hr.formula.parser.AbstractFormulaParser;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

public class WaBaCommonProcess extends AbstractFormulaParser {

	private BaseDAO baseDao = null;

	@Override
	public String parseAfterValidate(String pk_org, String formula, Object... params) throws BusinessException {
		return formula;
	}

	@Override
	public boolean validate(String formula) throws BusinessException {
		if (formula == null || "".equals(formula.trim())) {
			throw new BusinessException("公式有误，请检查");
		}


		return true;
	}

	public String[] getArguments(String formula) throws BusinessException {
		Object[] parts = null;
		String[] arguments = null;

//		MessageFormat format = new MessageFormat(functionVO.getArguments());
//
//		try {
//			parts = format.parse(formula);
//		} catch (ParseException e) {
//			throw new BusinessException(ResHelper.getString("6001formula", "06001formula0021", new String[] { formula }));
//		}

		if (parts != null) {
			arguments = new String[parts.length];

			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = parts[i].toString().trim();
			}
		}

		return arguments;
	}

	public BaseDAO getBaseDao() {
		if (this.baseDao == null) {
			this.baseDao = new BaseDAO();
		}
		return this.baseDao;
	}

}
