package nc.impl.hrwa.func;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * 薪资规则表
 * 
 * @author tsheay
 */
public class WaBaWageFormDataProcess extends WaBaCommonProcess {

	private static final long serialVersionUID = -4417686855450280940L;

	@SuppressWarnings("unchecked")
	public String parse(String pk_org, String functionName, Object... params) throws BusinessException {
		/*
		 * wageFormData(1001A11000000000A48N)
		 * functionName：wageFormData
		 * params：1001A11000000000A48N
		 */
		List<Map<String, Object>> formulaAndValue =
				(List<Map<String, Object>>) getBaseDao().executeQuery("select vformula,nvalue from wa_wageformdet where pk_wa_wageform='" + params[0] + "' and isnull(dr,0)=0 order by ilevel asc", new MapListProcessor());
		StringBuffer sqlB = new StringBuffer();

		sqlB.append("select case ");
		for (int i = 0; i < formulaAndValue.size(); i++) {
			String vfomula = (String) formulaAndValue.get(i).get("vformula");
			Double nvalue = ((BigDecimal) formulaAndValue.get(i).get("nvalue")).doubleValue();
			sqlB.append(" when( ");
			sqlB.append(parseFormula(vfomula));
			sqlB.append(" ) then ");
			sqlB.append(nvalue);
		}
		sqlB.append(" else 0 end ");
		sqlB.append(" from hi_psnjob left join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc");
		sqlB.append(" left join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg");
		sqlB.append(" where wa_ba_sch_psns.pk_psndoc=hi_psnjob.pk_psndoc ");
		sqlB.append(" and hi_psnjob.lastflag='Y' ");
		sqlB.append(" and hi_psnjob.ismainjob='Y' ");
		sqlB.append(" and hi_psnorg.lastflag='Y' ");
		/*
		sqlB.append(" else 0 end ");
		sqlB.append(" from hi_psnjob,bd_psndoc ");
		sqlB.append(" where wa_ba_sch_psns.pk_psndoc=hi_psnjob.pk_psndoc ");
		sqlB.append(" and wa_ba_sch_psns.pk_psndoc=bd_psndoc.pk_psndoc ");
		sqlB.append(" and hi_psnjob.lastflag='Y' ");
		sqlB.append(" and hi_psnjob.ismainjob='Y' ");
		 */
		/*
		 * select case 
		 * 		when(hi_psnjob.pk_org = '0001A110000000000HED') then 1.20000000
		 * 		when(hi_psnjob.pk_org = '0001A110000000000ICI') then 0.40000000
		 *  	when(hi_psnjob.pk_org = '0001A110000000000IBG') then 0.30000000
		 *  	when(hi_psnjob.clerkcode='000035' or hi_psnjob.clerkcode='003567') then 0.40000000
		 *  	else 0.00000000 end
		 *  from hi_psnjob where wa_ba_sch_psns.pk_psndoc=hi_psnjob.pk_psndoc and hi_psnjob.lastflag='Y' )
		 */
		return sqlB.toString();
		// return null;
	}

	/**
	 * @param params
	 * @return 如：[2,f_17]
	 * @throws BusinessException
	 */
	private String parseFormula(String formula) throws BusinessException {
		//		String[] parameter = null;
		/*
		 * 
		 */
		formula = StringUtils.replace(formula, "\"", "'");
		formula = StringUtils.replace(formula, "||", "or");
		formula = StringUtils.replace(formula, "&&", "and");
		// formula =
		// StringUtils.replace(formula,
		// "\"", "'");
		return formula;

	}

}
