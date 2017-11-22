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
			// 在转变成新增态时，会触发组织改变事件，而在给界面放值时又会触发一次组织改变事件
			// 在组织改变事件里面又会产生一些连接数。但是复制的情况下其实组织是没有改变的，这样就
			// 产生了很多多余的连接数，所以新增一个状态（复制新增）来区分原生的新增态，在复制新增时
			// 不触发组织改变事件，但是要求使用的model必须是实现nc.ui.pubapp.uif2app.model.IAppModelEx的
			// 如果没有实现这个接口那么连接数的问题暂时还不能处理，因为没有办法扩展UI状态
			if (getModel() instanceof IAppModelEx) {
				getMainModel().setUIstate(UIState.ADD);
				//				((IAppModelEx) getModel()).setAppUiState(AppUiState.COPY_ADD);
			} else {
				getModel().setUiState(UIState.ADD);
			}
			// 在ShowUpableBillForm.setValue的时候已经设置过主组织
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
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0126")/*@res "数据错误"*/);
		}
	}

	/**
	 * @return mainModel
	 */
	public MainGrandModel getMainModel() {
		return mainModel;
	}

	/**
	 * @param mainModel 要设置的 mainModel
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
	 * @param mainEditor 要设置的 mainEditor
	 */
	public void setMainEditor(CardGrandPanelComposite mainEditor) {
		this.mainEditor = mainEditor;
	}

}
