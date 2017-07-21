package nc.vo.wa.wa_ba.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nc.bs.logging.Logger;
import nc.hr.utils.SQLHelper;
import nc.impl.hr.formula.parser.FormulaParseHelper;
import nc.vo.hr.formula.FuncParseSplitResult;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.pub.HRLogicParse;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.wa.func.SqlFragment;
import nc.vo.wa.func.WherePartUtil;

import org.apache.commons.lang.StringUtils;

public class FunctionParse {
	public final String blank = " ";

	private static HashMap<String, String> tableWherePare = new HashMap<String, String>() {
		{
			put("wa_cacu_data", "wa_cacu_data.pk_wa_data = wa_ba_item.pk_wa_data");
			put("bd_psndoc", "bd_psndoc.pk_psndoc = wa_ba_item.pk_psndoc");
			put("bd_psnjob", "bd_psnjob.pk_psnjob = wa_ba_item.pk_psnjob");
			put("hi_psnjob", "hi_psnjob.pk_psnjob = wa_ba_item.pk_psnjob");
		}
	};

	/**
	 * @author zhangg on 2010-5-17
	 * @return the tableWherePare
	 */
	public static HashMap<String, String> getTableWherePare() {
		return tableWherePare;
	}

	public static List<SqlFragment> parse(String formula) {
		FuncParseSplitResult result = FormulaParseHelper.findFirstFunc(formula, "iif");
		List<SqlFragment> list = new ArrayList<SqlFragment>();
		if (result == null) {
			SqlFragment fragment = new SqlFragment();
			fragment.setValue(formula);
			list.add(fragment);
			return list;
		}
		String prePart = result.getPrePart();
		String postPart = result.getPostPart();
		if (StringUtils.isNotBlank(prePart) || StringUtils.isNotBlank(postPart)) {
			Logger.error("the formula is:" + formula);
			throw new BusinessRuntimeException("iif函数只能单独使用!");
		}
		String[] args = result.getArgs();
		String cond = args[0];
		String ifValue = args[1];
		String elseValue = args[2];
		List<SqlFragment> ifList = parse(ifValue);
		List<SqlFragment> elseList = parse(elseValue);
		for (SqlFragment fragment : ifList) {
			String condition = fragment.getCondition();
			if (StringUtils.isNotEmpty(condition))
				condition = "(" + condition + ")";
			condition = SQLHelper.appendExtraCond(condition, cond);
			fragment.setCondition(HRLogicParse.parseCondition(condition));
		}
		list.addAll(elseList);
		list.addAll(ifList);
		return list;
	}

	public static String addSourceTable2Value(String valueFormula) {
		if (valueFormula == null) {
			return valueFormula;
		}
		List<String> tableList = new LinkedList<String>();
		String where = null;

		Iterator<String> iterator = getTableWherePare().keySet().iterator();
		while (iterator.hasNext()) {
			String table = iterator.next();
			if (valueFormula.indexOf(table) >= 0) {
				tableList.add(table);
			}
		}
		if (tableList.size() > 0) {
			String tables = FormatVO.formatListToString(tableList, "");
			for (String key : tableList) {
				if (where == null) {
					where = getTableWherePare().get(key);
				} else {
					where = where + " and " + tableWherePare.get(key);
				}
			}
			valueFormula = "select " + valueFormula + " from " + tables + " where " + where;
		}
		return valueFormula;
	}

	public static String addSourceTable2Conditon(String condtionFormula) {
		if (condtionFormula == null) {
			return condtionFormula;
		}
		List<String> tableList = new LinkedList<String>();
		String where = null;

		Iterator<String> iterator = getTableWherePare().keySet().iterator();
		while (iterator.hasNext()) {
			String table = iterator.next();
			if (condtionFormula.indexOf(table) >= 0) {
				tableList.add(table);
			}
		}
		if (tableList.size() > 0) {
			tableList.add("wa_ba_sch_psns");
			String tables = FormatVO.formatListToString(tableList, "");
			for (String key : tableList) {
				if (where == null) {
					where = getTableWherePare().get(key);
				} else {
					if (tableWherePare.get(key) != null) {
						where = where + " and " + tableWherePare.get(key);
					}
				}
			}

			condtionFormula =
					" and wa_ba_sch_psns.pk_wa_data in (select wa_ba_sch_psns.pk_wa_data from " + tables + " where " + where + WherePartUtil.formatAddtionalWhere(condtionFormula) + ")";
		}

		return condtionFormula;
	}

}
