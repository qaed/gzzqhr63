package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hrwa.wa_ba_unit.ace.maintain.AceWaBaUnitDataManager;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.IAppModelDataManager;

/**
 * 变动人员
 * 
 * @see nc.ui.wa.payfile.action.AlterPayfileAction
 */
@SuppressWarnings("restriction")
public class WaBaUnitEasyUpdateAction extends HrAction {
	private static final long serialVersionUID = 4704524145613064081L;
	private IAppModelDataManager dataManager = null;
	private IEditor editor;

	public WaBaUnitEasyUpdateAction() {
		super();
		setCode("WaBaUnitEasyUpdate");
		setBtnName("一键更新");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, "一键更新(Ctrl+U)");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		IWaBaUnitMaintain maintain = NCLocator.getInstance().lookup(IWaBaUnitMaintain.class);
		maintain.fastUpdate(getContext(), null);
		((AceWaBaUnitDataManager) getDataManager()).refresh();
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getSelectedData() == null) {
			return false;
		}
		return super.isActionEnable();
	}

	/**
	 * @return dataManager
	 */
	public IAppModelDataManager getDataManager() {
		return dataManager;
	}

	/**
	 * @param dataManager 要设置的 dataManager
	 */
	public void setDataManager(IAppModelDataManager dataManager) {
		this.dataManager = dataManager;
	}

	/**
	 * @return editor
	 */
	public IEditor getEditor() {
		return editor;
	}

	/**
	 * @param editor 要设置的 editor
	 */
	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

}