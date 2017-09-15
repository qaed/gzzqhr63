package nc.ui.hrwa.wa_ba_item.ace.view;

import java.util.ArrayList;
import java.util.List;

import nc.ui.hr.formula.HRFormulaItem;
import nc.ui.hr.formula.itf.IFormulaTableCreator;
import nc.ui.hr.formula.itf.IVariableFactory;
import nc.vo.pub.formulaedit.FormulaItem;
import nc.vo.uap.busibean.exception.BusiBeanException;

/**
 * @author: xuanlt
 * @date: 2010-4-1 上午11:39:28
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WabaItemTableCreator implements IFormulaTableCreator {

	/**
	 * @author xuanlt on 2010-4-1
	 * @see nc.ui.wa.formular.HRFormularTableCreator#getAllTables()
	 */
	@Override
	public List<FormulaItem> getAllTables() throws BusiBeanException {
		List<FormulaItem> items = new ArrayList<FormulaItem>();
		//只安装薪资不安装员工信息，需要在公式设置的项目一栏加入“人员类别”、“所在部门”
		//		        items.add( new HRFormulaItem("wa_item",IVariableFactory.WA_ITEM_DES,IVariableFactory.WA_ITEM_DES,IVariableFactory.WA_ITEM_DES));
		items.add(new HRFormulaItem("wa_ba_item", "奖金分配项目", "奖金分配项目", "奖金分配项目"));
		items.add(new HRFormulaItem("hi_psnjob", IVariableFactory.HI_PSN_JOB_DES, IVariableFactory.HI_PSN_JOB_DES, IVariableFactory.HI_PSN_JOB_DES));
		items.add(new HRFormulaItem("bd_psndoc", IVariableFactory.BD_PSNDOC_DES, IVariableFactory.BD_PSNDOC_DES, IVariableFactory.BD_PSNDOC_DES));

		return items;
	}

}
