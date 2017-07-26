package nc.impl.hrwa.func;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import uap.lfw.bd.reference.IReferenceQry.simpleRefVO;

import nc.hr.utils.SQLHelper;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

/**
 * 定调资
 * 
 * @see nc.impl.wa.func.WaAdjustDoc
 * @author tsheay
 */
public class WaBaWaAdjustDocProcess extends WaBaCommonProcess {

	public String parse(String pk_org, String functionName, Object... params) throws BusinessException {
		/*
		 * params:[f_18, #2#f_17#]
		 * arguments:[2,f_17]
		 */
		String[] arguments = getArguments(params);
		// 取数类型
		String type = arguments[0];
		int intType = new Integer(type).intValue();
		// 查询item
		String itemKey = arguments[1];

		StringBuffer sqlB = null;
		if (1 == intType) {// 原发放额
			// 暂时只保留"现发放金额"
			sqlB = new StringBuffer();

		} else if (2 == intType) {// 现发放金额
			sqlB = new StringBuffer();

			sqlB.append("select hi_psndoc_wadoc.nmoney ");
			sqlB.append("from hi_psndoc_wadoc ");
			sqlB.append("where hi_psndoc_wadoc.pk_psndoc = wa_ba_sch_psns.pk_psndoc ");
			sqlB.append("	and hi_psndoc_wadoc.pk_wa_item = (select pk_wa_item from wa_item where itemkey = '" + itemKey + "') ");
			sqlB.append("	and hi_psndoc_wadoc.lastflag = 'Y' ");// 最新标识
			sqlB.append("   and hi_psndoc_wadoc.waflag = 'Y' ");// 发放标识
			sqlB.append("	and hi_psndoc_wadoc.dr = 0");
		}
		return sqlB.toString();
	}

	/**
	 * @param params
	 * @return 如：[2,f_17]
	 * @throws BusinessException
	 */
	private String[] getArguments(Object[] params) throws BusinessException {
		String[] parameter = null;
		if (params == null && params.length != 2) {
			throw new BusinessException("公式有误，请检查");
		}
		/*
		 * params[0]:f_18
		 * params[1]:#2#f_17#
		 * parameter:[2,f_17]
		 */
		parameter = StringUtils.split((String) params[1], "#");
		return parameter;

	}

}
