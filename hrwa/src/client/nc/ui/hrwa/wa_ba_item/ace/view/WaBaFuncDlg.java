package nc.ui.hrwa.wa_ba_item.ace.view;

/**
 * 薪资、福利的业务函数设置类
 */

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.itemsource.view.HrHiFuncDlg;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.vo.hr.datasource.HrDatasourceType;
import nc.vo.hr.func.FunctionVO;
import nc.vo.wa.formula.WaFormulaXmlHelper;
import nc.vo.wa.func.WaDatasourceManager;
import nc.vo.wa.wa_ba.item.WaBaLoginContext;

public class WaBaFuncDlg extends HrHiFuncDlg  {
	
	public WaBaFuncDlg() {
		super();
	}
	
	public WaBaFuncDlg(java.awt.Container parent) {
		super(parent);
		
	}


/**
	 * 
	 */
	private static final long serialVersionUID = 3957384298587099128L;

@Override
protected FunctionVO[] getFunctionVOByGroup(String productCode) {
	// TODO Auto-generated method stub
	return WaFormulaXmlHelper.getFunctionVOByGroup(productCode);
}

/**
 * 返回 UIPanel1 特性值。
 * @return nc.ui.pub.beans.UIPanel
 */
//返回参数设置panel，兼顾薪资、福利应用
@Override
protected nc.ui.pub.beans.UIPanel getPanel() {
	try {
		//福利的panel很特殊，不在初始化时用它就会出错
		String l_parapanel = "nc.ui.hr.itemsource.view.NullParaPanel";
		//工龄函数取人员信息集设置，不需要设置信息
		//原则是refpane不空，且刚才的panel未被切换时直接返回，否则构建
		int index = getCmbFunc().getSelectedIndex();
		if (index > 0) {
			l_parapanel = funcdata[index - 1].getParapanel();//getVfuncclass();
		}

		if (refpanel == null || !parapanel.equals(l_parapanel)) {
			//刚才空，或不是这个panel，则需要重新构建
			//若以前有，则先移除
			if (refpanel != null) {
				getUIPanel2().removeAll();
			}

			//构建新的
			Class c = Class.forName(l_parapanel);
			Object newInst = c.newInstance();
			refpanel = (nc.ui.pub.beans.UIPanel) newInst;
			refpanel.setBounds(20, 30, 240, 200);
			getUIPanel2().add(refpanel);
			getUIPanel2().repaint();
			if(refpanel!=null){
				((IParaPanel)refpanel).setDatatype(getFormula().getReturnType());
				((IParaPanel)refpanel).setCurrentItemKey(getFormula().getItemKey());
			}
			if (refpanel instanceof IWaRefPanel)
			{
				((IWaRefPanel) refpanel).setContext((WaBaLoginContext)getModel().getContext());
			}
		}
		parapanel = l_parapanel;

	}
	catch (Exception e) {
		reportException(e);
		showErrMsg(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0177")/*@res "获得参数界面出错! "*/);
	}
	return refpanel;
}

@Override
public String getProductcode() {
	//通过ifromflag，得到对应产品编码
	try {
		HrDatasourceType dstype=  WaDatasourceManager.getDSType(getFormula().getIfromflag());
		String code = "";
		if(dstype.getName().equals("薪资")){
			code = dstype.getProductcode();
		}
		return code;	
	} catch (Exception e) {
		Logger.error(e.getMessage(),e);
	}
	
	return null;
}

@Override
public void initData() throws Exception {
	// TODO 自动生成的方法存根
	super.initData();
	
	for(int i=0;i<funcdata.length;i++){
		if(funcdata[i].getDefaultName().equals("定调资档案")){
			funcdata[i].setParapanel("nc.ui.hrwa.wa_ba_item.ace.view.WaBaParaAdjPanel");
		}
	}
	
}

}