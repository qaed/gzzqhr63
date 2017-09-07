package nc.ui.hrwa.wa_ba_item.ace.view;

import java.util.ArrayList;
import java.util.List;

import nc.ui.hr.formula.itf.IFormulaTableCreator;
import nc.ui.hr.formula.variable.IHrVaribleItemCreator;
import nc.ui.wa.formular.ItemTableCreator;
import nc.ui.wa.formular.WaItemVaribleItemCreator;
import nc.ui.wa.item.view.HrWaDefaultVariableFactory;
import nc.vo.pub.formulaedit.FormulaItem;

/**
 * 
 * @author: xuanlt 
 * @date: 2010-4-1 下午01:17:01
 * @since: eHR V6.0
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
public class WabaItemFormularFactory extends   HrWaDefaultVariableFactory{
		
	
	private IFormulaTableCreator tableCreator;

	protected List<FormulaItem> createAllFieldItems(String tablename) {
		List<FormulaItem> fieldItems = new ArrayList<FormulaItem>();
		
		IHrVaribleItemCreator contentCreator  = null;
//    	if (HI_PSN_JOB_DES.equals(tablename)) {
//    		fieldItems = new PsnjobHrVariableCreator().createFormulaItems();
//    	}
//    	
//    	else if (BD_PSNDOC_DES.equals(tablename)) {    		
//    		  contentCreator = new PsndocHrVariableCreator();
//		}
//    	
//    	else
    		
    	if (WA_ITEM_DES.equals(tablename)) {
			fieldItems = new WaItemVaribleItemCreator()
					.createFormulaItems(getcontex());

		} else {
			fieldItems = super.createAllFieldItems(tablename);
		}

		
		return fieldItems;
	}

	
	@Override
	public IFormulaTableCreator getTableCreator() {
		
		if(tableCreator == null){
			tableCreator =  new WabaItemTableCreator();
		}
		return tableCreator;
	}
	
}
