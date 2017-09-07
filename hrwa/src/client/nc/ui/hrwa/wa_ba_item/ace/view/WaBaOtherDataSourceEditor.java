package nc.ui.hrwa.wa_ba_item.ace.view;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import nc.bs.logging.Logger;
import nc.ui.hr.formula.itf.IFormulaEditor;
import nc.ui.hr.itemsource.view.AbstractBillItemEditor;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.datasource.HrDatasourceType;
import nc.vo.hr.func.HrFormula;
import nc.vo.pub.BusinessException;
import nc.vo.wa.func.WaDatasourceManager;

import org.apache.commons.lang.ArrayUtils;

/**
 *  包装器模式
 * OtherDataSourceEditor 包装 WaOtherSourceRefPane  实现共同接口 IFormulaEditor
 * @author: xuanlt 
 * @date: 2010-5-18 下午04:05:01
 * @since: eHR V6.0
 * @走查人: 
 * @走查日期: 
 * @修改人: 
 * @修改日期:
 */

public  class WaBaOtherDataSourceEditor extends UIPanel implements IFormulaEditor{
	
	private static final long serialVersionUID = 2794213744133973372L;
	private WaBaOtherSourceRefPane waOtherSourceRefPane;
	private UIComboBox systemType; 
	
	AbstractBillItemEditor parentEditor = null;
	
	private  HrDatasourceType[] dsTypes = null;



	/**
	 * @author xuanlt on 2010-5-18 
	 */
	public WaBaOtherDataSourceEditor(AbstractBillItemEditor parent) {
		this.parentEditor = parent;
		this.setSize(4096,50);
		setLayout(new FlowLayout(FlowLayout.LEFT,5,0));
		add(getFromType());
		add(getFormularEditor());
		
	}
	
	
	private WaBaOtherSourceRefPane getFormularEditor(){
		
		if(waOtherSourceRefPane == null){
			waOtherSourceRefPane = new WaBaOtherSourceRefPane(this);
			
		}
		
		return waOtherSourceRefPane;
	}
	
	
	private UIComboBox getFromType(){
		if(systemType == null){
			systemType = new UIComboBox();
			
			try {
				dsTypes = WaDatasourceManager.getAllDsType();
			} catch (BusinessException e) {
				Logger.error(e);
			}
			if(!ArrayUtils.isEmpty(dsTypes)){
				for(HrDatasourceType regVO : dsTypes){
					systemType.addItem(regVO);
				}
                
			}
			systemType.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED){
						HrDatasourceType funcregVO = (HrDatasourceType )systemType.getSelectedItem();
						HrFormula f = getFormula();
						if(null == f) {
						    f = new HrFormula();
						}
						f.setBusinessLang("");
						f.setScirptLang("");
						f.setIfromflag(funcregVO.getIfromflag());
						
						//
						//f.setReturnType(getItemDataType());
						
						getFormularEditor().setFormula(f);
					}
					
				}
				
			});
		}
		return systemType;
	}
	
	

	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		getFormularEditor().setEnabled(enabled);
		getFromType().setEnabled(enabled);
		
	}

	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#getFormula()
	 */
	@Override
	public HrFormula getFormula() {
	
		return getFormularEditor().getFormula();
	}


	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#getModel()
	 */
	@Override
	public AbstractUIAppModel getModel() {
	
		return getFormularEditor().getModel();
	}


	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#setFormula(nc.vo.hr.func.HrFormula)
	 */
	@Override
	public void setFormula(HrFormula fromular) {
		//根据ifromflag 确定 HrDatasourceType vo		
		
		try {
			HrDatasourceType[]  vos =WaDatasourceManager.getAllDsType();
			HrDatasourceType vo = vos[0];
			for (int i = 0; i < vos.length; i++) {
				if(vos[i].getIfromflag().equals(fromular.getIfromflag())){
					vo = vos[i];
				}
				
			}
	      
	        getFromType().setSelectedItem(vo);
			//设置数据来源是薪资系统还是人事系统
			getFormularEditor().setFormula(fromular);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
	}
	
	



	/** 
	 * @author xuanlt on 2010-5-18 
	 * @see nc.ui.wa.formular.IFormulaEditor#setModel(nc.ui.uif2.model.AbstractUIAppModel)
	 */
	@Override
	public void setModel(AbstractUIAppModel model) {
		getFormularEditor().setModel(model);
		
	}
	
	public Integer getItemDataType(){
		
		return ((WaItemDataSourcePanel)parentEditor).getDataType();
	}
}