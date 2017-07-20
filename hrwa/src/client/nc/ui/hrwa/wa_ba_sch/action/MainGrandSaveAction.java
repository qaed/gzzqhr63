package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.IActionCode;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.components.grand.CardGrandPanelComposite;
import nc.ui.pubapp.uif2app.components.grand.model.MainGrandModel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

public class MainGrandSaveAction extends NCAction {
	/**
	 * SaveAction DifferentVOSaveAction
	 */
	private static final long serialVersionUID = 6917165758995672772L;
	private MainGrandModel model;
	private CardGrandPanelComposite editor;
	private IDataOperationService service;
	private ShowUpableBillForm sunbillfrom;

	public IDataOperationService getService() {
		return this.service;
	}

	public void setService(IDataOperationService service) {
		this.service = service;
	}

	IValidationService validationService;

	public MainGrandSaveAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.SAVE);
	}

	// 注意将孙面板XXX属性设置
	@Override
	public void doAction(ActionEvent e) throws Exception {
		
//		editor.getModel().getBufferCardAddMap()
		Object value = editor.getValue();
//		model.getBufferCardAddMap();
		if (model.getMainModel().getUiState() == UIState.ADD) {
			doAddSave(value);
		} else if (model.getMainModel().getUiState() == UIState.EDIT) {
			doEditSave(value);
		}
		// TODO 执行计算
		WaBaSchCaculateAction action = new WaBaSchCaculateAction(((AggWaBaSchHVO)value));
		action.doAction(e);
		//end
		showSuccessInfo();
	}

	protected void doEditSave(Object value) throws Exception {
		IBill[] vos = this.getService().update(new AggWaBaSchHVO[] { (AggWaBaSchHVO) value });
		this.getModel().directlyUpdate(vos[0]);
		this.getModel().setUIstate(UIState.NOT_EDIT);
	}

	protected void doAddSave(Object value) throws Exception {
		IBill[] vos = this.getService().insert(new AggWaBaSchHVO[] { (AggWaBaSchHVO) value });
		this.getModel().directlyAdd(vos[0]);
		this.getModel().setUIstate(UIState.NOT_EDIT);
	}

	protected void showSuccessInfo() {
		ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getSaveSuccessInfo(), model.getMainModel().getContext());
		// // 将消息栏字段隐藏标志位复位
		// if (getExceptionHandler() instanceof DefaultExceptionHanler) {
		// ((DefaultExceptionHanler)
		// getExceptionHandler()).setAutoClearError(true);
		// }
	}

	/**
	 * 此方法在调用模型的add或update调用。用来对从编辑器中取出的value对象进行校验。
	 * 
	 * @param value
	 */
	protected void validate(Object value) {
		if (validationService != null) {
			try {
				validationService.validate(value);
			} catch (ValidationException e) {
				throw new BusinessExceptionAdapter(e);
			}
		}
	}

	public MainGrandModel getModel() {
		return model;
	}

	public void setModel(MainGrandModel model) {
		this.model = model;
	}

	public CardGrandPanelComposite getEditor() {
		return editor;
	}

	public void setEditor(CardGrandPanelComposite editor) {
		this.editor = editor;
	}

	public IValidationService getValidationService() {
		return validationService;
	}

	public void setValidationService(IValidationService validationService) {
		this.validationService = validationService;
	}

	public ShowUpableBillForm getSunbillfrom() {
		return sunbillfrom;
	}

	public void setSunbillfrom(ShowUpableBillForm sunbillfrom) {
		this.sunbillfrom = sunbillfrom;
	}

}
