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
 * @date: 2010-2-23 ����02:01:22
 * @since: eHR V6.0
 * @�߲���: 
 * @�߲�����: 
 * @�޸���: 
 * @�޸�����: 
 */
public class WaBaOtherSourceRefPane extends  UIRefPane implements IFormulaEditor{

	/**
	 * @author xuanlt on 2010-5-17 
	 */
	private static final long serialVersionUID = 8177900453561045899L;
	//��ʽ�������
	protected HrFormula formula = null;
	protected AbstractUIAppModel model = null;
	
	private java.awt.Container parent = null;
	protected WaBaFuncDlg dlg = null;


	/**
	 * ����fromflag ,�õ���Ӧ�Ĳ�Ʒ����
	 * @return
	 */
//	protected String getModuleCode() {
//		//ͨ��ifromflag���õ���Ӧ��Ʒ����
//		
//		return FromEnumVO.valueOf(FromEnumVO.class, getFormula().getIfromflag()).convertSystemType().getVproductcode();
//	}
	/**
	 * FormulaRefPane ������ע��.
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
			//�Ӹ��������ȡformular ���� WaFuncDlg��
			//��Ҫ��������Ϣcontext
			
			WaBaFuncDlg  dlg = getDlg();
		
		getFormula().setReturnType(getItemDataType());
		dlg.setFormula(getFormula());
		
        dlg.setModel(getModel()); 
        
        dlg.showModal();
		if (dlg.getResult() == UIDialog.ID_OK) {
			
			//�������ù�ʽ
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
