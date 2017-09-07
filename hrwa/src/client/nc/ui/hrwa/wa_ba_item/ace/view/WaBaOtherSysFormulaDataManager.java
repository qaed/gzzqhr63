package nc.ui.hrwa.wa_ba_item.ace.view;

import java.awt.Component;

import nc.ui.hr.itemsource.view.IEnableController;
import nc.ui.hr.itemsource.view.ITypeSourceComponentManager;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.vo.hr.func.HrFormula;
import nc.vo.hr.itemsource.ItemPropertyConst;
import nc.vo.pub.SuperVO;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.wa_ba.item.ItemsVO;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: wh 
 * @date: 2009-12-15 上午11:34:34
 * @since: eHR V6.0
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
public class WaBaOtherSysFormulaDataManager   implements ITypeSourceComponentManager,IEnableController {

	@Override
	public ItemsVO getData(Component comp, SuperVO item) {
		

		if(item == null){
			return null;
		}
		
		WaBaOtherSourceRefPane refPane = getFormulaRefPane(comp);	
		if(StringUtils.isBlank(refPane.getFormula().getScirptLang())){
			item.setAttributeValue(ItemPropertyConst.VFORMULA, null);
			  item.setAttributeValue(ItemPropertyConst.VFORMULASTR, null);
		}else{
			item.setAttributeValue(ItemPropertyConst.VFORMULA,refPane.getFormula().getScirptLang());
			item.setAttributeValue(ItemPropertyConst.VFORMULASTR,refPane.getFormula().getBusinessLang());
		}
		
		if(refPane.getFormula()!=null){
			item.setAttributeValue(ItemPropertyConst.IFROMFLAG,refPane.getFormula().getIfromflag());
		}else{
			item.setAttributeValue(ItemPropertyConst.IFROMFLAG,FromEnumVO.WAORTHER.value());
		}		

		return ((ItemsVO)item);
	}

	private WaBaOtherSourceRefPane getFormulaRefPane(Component comp) {
		UIPanel panel = (UIPanel) comp;
		WaBaOtherSourceRefPane refPane = (WaBaOtherSourceRefPane) panel.getComponent(1);
		return refPane;
	}

	private UIComboBox getSysTypeCombo(Component comp) {
		UIPanel panel = (UIPanel) comp;
		UIComboBox systemType = (UIComboBox) panel.getComponent(0);
		return systemType;
	}

	@Override
	public void setData(SuperVO item, Component comp) {
		ItemsVO classItem = (ItemsVO)item;
		//FIX ME
		
//		if(StringHelper.isNotBlank(classItem.getOthersys())){
//		UIComboBox systemType = getSysTypeCombo(comp);
//		int count = systemType.getItemCount();		
//		for(int i = 0 ; i < count; i++){
//			FuncregVO regVO = (FuncregVO) systemType.getItemAt(i);
//			if(classItem.getOthersys().equals(regVO.getVproductcode())){
//				systemType.setSelectedIndex(i);
//				break;
//			}
//		}
//		}
		if(null!=oldVformula&&oldVformula.equals(classItem.getVformula())){
			  return;
		}
		
		//设置默认
		HrFormula f = new HrFormula();
		//f.setBusinessLang(classItem.getVformulastr());
		f.setScirptLang(classItem.getVformula());
		f.setDefault(false);
		f.setItemKey(classItem.getPk_ba_item());
		f.setReturnType(classItem.getIitemtype());
		f.setIfromflag(classItem.getIfromflag());
		
		((WaBaOtherDataSourceEditor)comp).setFormula(f);//
		
		oldVformula = classItem.getVformula();
	}
	
	private String oldVformula = null;
	
	public void clearData(SuperVO item, Component comp) {
		ItemsVO classItem = (ItemsVO)item;
		//FIX ME
		
//		if(StringHelper.isNotBlank(classItem.getOthersys())){
//		UIComboBox systemType = getSysTypeCombo(comp);
//		int count = systemType.getItemCount();		
//		for(int i = 0 ; i < count; i++){
//			FuncregVO regVO = (FuncregVO) systemType.getItemAt(i);
//			if(classItem.getOthersys().equals(regVO.getVproductcode())){
//				systemType.setSelectedIndex(i);
//				break;
//			}
//		}
//		}
		
		//设置默认
		HrFormula f = new HrFormula();
		f.setBusinessLang(null);
		f.setScirptLang(null);
		f.setDefault(false);
		f.setItemKey(classItem.getPk_ba_item());
		f.setReturnType(0);
		f.setIfromflag(0);
		
		
		((WaBaOtherDataSourceEditor)comp).setFormula(f);//
	}

	/** 
	 * @author wh on 2009-12-15 
	 * @see nc.ui.hr.itemsource.view.IEnableController#setEnabled(java.awt.Component, boolean)
	 */
	@Override
	public void setEnabled(Component comp, boolean enabled) {
		comp.setEnabled(enabled);
		
		
	}

	/** 
	 * @author xuanlt on 2010-2-23 
	 * @see nc.ui.hr.itemsource.view.ITypeSourceComponentManager#setPk_org(java.lang.String, java.awt.Component)
	 */
	@Override
	public void setPk_org(String pk_org, Component comp) {
		// TODO Auto-generated method stub
		
	}
	
}
