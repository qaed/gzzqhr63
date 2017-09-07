package nc.ui.hrwa.wa_ba_item.ace.view;

/**
 * н�ʡ�������ҵ����������
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
 * ���� UIPanel1 ����ֵ��
 * @return nc.ui.pub.beans.UIPanel
 */
//���ز�������panel�����н�ʡ�����Ӧ��
@Override
protected nc.ui.pub.beans.UIPanel getPanel() {
	try {
		//������panel�����⣬���ڳ�ʼ��ʱ�����ͻ����
		String l_parapanel = "nc.ui.hr.itemsource.view.NullParaPanel";
		//���亯��ȡ��Ա��Ϣ�����ã�����Ҫ������Ϣ
		//ԭ����refpane���գ��Ҹղŵ�panelδ���л�ʱֱ�ӷ��أ����򹹽�
		int index = getCmbFunc().getSelectedIndex();
		if (index > 0) {
			l_parapanel = funcdata[index - 1].getParapanel();//getVfuncclass();
		}

		if (refpanel == null || !parapanel.equals(l_parapanel)) {
			//�ղſգ��������panel������Ҫ���¹���
			//����ǰ�У������Ƴ�
			if (refpanel != null) {
				getUIPanel2().removeAll();
			}

			//�����µ�
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
		showErrMsg(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0177")/*@res "��ò����������! "*/);
	}
	return refpanel;
}

@Override
public String getProductcode() {
	//ͨ��ifromflag���õ���Ӧ��Ʒ����
	try {
		HrDatasourceType dstype=  WaDatasourceManager.getDSType(getFormula().getIfromflag());
		String code = "";
		if(dstype.getName().equals("н��")){
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
	// TODO �Զ����ɵķ������
	super.initData();
	
	for(int i=0;i<funcdata.length;i++){
		if(funcdata[i].getDefaultName().equals("�����ʵ���")){
			funcdata[i].setParapanel("nc.ui.hrwa.wa_ba_item.ace.view.WaBaParaAdjPanel");
		}
	}
	
}

}