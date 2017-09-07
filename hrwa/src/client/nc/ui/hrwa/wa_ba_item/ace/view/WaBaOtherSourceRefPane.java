package nc.ui.hrwa.wa_ba_item.ace.view;

import nc.bs.logging.Logger;
import nc.ui.hr.formula.itf.IFormulaEditor;
import nc.ui.hr.func.WaFuncDlg;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.func.HrFormula;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.wa_ba.item.WaBaLoginContext;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: xuanlt 
 * @date: 2010-2-23 下午02:01:22
 * @since: eHR V6.0
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期: 
 */
public class WaBaOtherSourceRefPane extends  UIRefPane implements IFormulaEditor{

	/**
	 * @author xuanlt on 2010-5-17 
	 */
	private static final long serialVersionUID = 8177900453561045899L;
	//公式定义规则
	protected HrFormula formula = null;
	protected AbstractUIAppModel model = null;
	
	private java.awt.Container parent = null;
	protected WaBaFuncDlg dlg = null;


	/**
	 * 根据fromflag ,得到对应的产品编码
	 * @return
	 */
//	protected String getModuleCode() {
//		//通过ifromflag，得到对应产品编码
//		
//		return FromEnumVO.valueOf(FromEnumVO.class, getFormula().getIfromflag()).convertSystemType().getVproductcode();
//	}
	/**
	 * FormulaRefPane 构造子注解.
	 */
	public WaBaOtherSourceRefPane(java.awt.Container parent) {
		super();
		this.parent = parent;
		setMaxLength(4096);
		//init();
	}


	@Override
	public void onButtonClicked() {
		try {
			//从父面板中提取formular 传入 WaFuncDlg中
			//需要上下文信息context
			
			WaBaFuncDlg  dlg = getDlg();
		
		getFormula().setReturnType(getItemDataType());
		dlg.setFormula(getFormula());
		
        dlg.setModel(getModel()); 
        
        dlg.showModal();
		if (dlg.getResult() == UIDialog.ID_OK) {
			
			//重新设置公式
			HrFormula f =dlg.generateFormula();
			setText(f.getBusinessLang());
			
		}
		dlg.destroy();
		} catch (Exception e) {
			MessageDialog.showErrorDlg(this, null, e.getMessage());
			Logger.error(e.getMessage(),e);
		}
	}

	@Override
	public void setEnabled(boolean param) {
		// TODO Auto-generated method stub
		super.setEnabled(param);
		
	}
	
	public WaBaFuncDlg getDlg() {
		if (dlg == null) {
           dlg = new WaBaFuncDlg(this);           
		}		
        return dlg;
	}

	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#getFormula()
	 */
	@Override
	public HrFormula getFormula() {
		
		return formula;
	}

	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#getModel()
	 */
	@Override
	public AbstractUIAppModel getModel() {		
		return model;
	}

	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#setFormula(nc.vo.hr.func.HrFormula)
	 */
	@Override
	public void setFormula(HrFormula fromular) {
		this.formula = fromular;
		if(StringUtils.isEmpty(fromular.getBusinessLang())&&StringUtils.isNotEmpty(fromular.getScirptLang())){
			WaBaFuncDlg  dlg = getDlg();
			getFormula().setReturnType(getItemDataType());
			dlg.setFormula(getFormula());
			dlg.setModel(getModel());
			
			this.formula.setBusinessLang(getDlg().getBusinessLang(this.formula.getScirptLang()));
		}
		setText(this.formula.getBusinessLang());
	}

	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#setModel(nc.ui.uif2.model.AbstractUIAppModel)
	 */
	@Override
	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		
	}
	
	public WaBaLoginContext getContext(){
		return (WaBaLoginContext)getModel().getContext();
	}
	
	private Integer getItemDataType(){
		return ((WaBaOtherDataSourceEditor)getParent()).getItemDataType();
		
	}
}
