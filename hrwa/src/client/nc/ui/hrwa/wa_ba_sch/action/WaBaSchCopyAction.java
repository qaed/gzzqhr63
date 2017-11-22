package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import nc.ui.pubapp.uif2app.actions.CopyAction;
import nc.ui.pubapp.uif2app.components.grand.CardGrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandModel;
import nc.ui.pubapp.uif2app.model.IAppModelEx;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

import org.apache.commons.lang.SerializationUtils;

@SuppressWarnings("restriction")
public class WaBaSchCopyAction extends CopyAction {

	private static final long serialVersionUID = 1L;

	private MainGrandModel mainModel;
	private CardGrandPanelComposite mainEditor;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object object = getMainModel().getSelectedData();
		if (object instanceof AbstractBill) {

			// AbstractBill aggvo = (AbstractBill) ((AbstractBill) object).clone();
			AbstractBill aggvo = (AbstractBill) SerializationUtils.clone((Serializable) object);
			if (getCopyActionProcessor() != null) {
				getCopyActionProcessor().processVOAfterCopy(aggvo, this.getModel().getContext());
			}
			// ��ת�������̬ʱ���ᴥ����֯�ı��¼������ڸ������ֵʱ�ֻᴥ��һ����֯�ı��¼�
			// ����֯�ı��¼������ֻ����һЩ�����������Ǹ��Ƶ��������ʵ��֯��û�иı�ģ�������
			// �����˺ܶ���������������������һ��״̬������������������ԭ��������̬���ڸ�������ʱ
			// ��������֯�ı��¼�������Ҫ��ʹ�õ�model������ʵ��nc.ui.pubapp.uif2app.model.IAppModelEx��
			// ���û��ʵ������ӿ���ô��������������ʱ�����ܴ�����Ϊû�а취��չUI״̬
			if (getModel() instanceof IAppModelEx) {
				getMainModel().setUIstate(UIState.ADD);
				//				((IAppModelEx) getModel()).setAppUiState(AppUiState.COPY_ADD);
			} else {
				getModel().setUiState(UIState.ADD);
			}
			// ��ShowUpableBillForm.setValue��ʱ���Ѿ����ù�����֯
			// if (null != this.orgPanel) {
			// this.orgPanel.setPkOrg(BDObjectUtils.getPkOrg(aggvo));
			// }
			// this.model.setUiState(UIState.ADD);
			getMainModel().getSelectedData();
			WaBaSchHVO hvo = ((AggWaBaSchHVO) getMainEditor().getValue()).getParentVO();
			WaBaSchHVO currenthvo = (WaBaSchHVO)aggvo.getParentVO();
			currenthvo.setSch_code(hvo.getSch_code());
			currenthvo.setCyear(hvo.getCyear());
			currenthvo.setCperiod(hvo.getCperiod());
			getMainEditor().setValue(aggvo);
		} else {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0126")/*@res "���ݴ���"*/);
		}
	}

	/**
	 * @return mainModel
	 */
	public MainGrandModel getMainModel() {
		return mainModel;
	}

	/**
	 * @param mainModel Ҫ���õ� mainModel
	 */
	public void setMainModel(MainGrandModel mainModel) {
		this.mainModel = mainModel;
	}

	/**
	 * @return mainEditor
	 */
	public CardGrandPanelComposite getMainEditor() {
		return mainEditor;
	}

	/**
	 * @param mainEditor Ҫ���õ� mainEditor
	 */
	public void setMainEditor(CardGrandPanelComposite mainEditor) {
		this.mainEditor = mainEditor;
	}

}
