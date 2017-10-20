package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.Container;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hrwa.wa_ba_unit.ace.maintain.AceWaBaUnitDataManager;
import nc.ui.hrwa.wa_ba_unit.ace.view.ShowChangePsn;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIDialogFactory;
import nc.ui.uif2.editor.IEditor;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.wa.payfile.view.PeriodDia;
import nc.vo.pub.lang.UFDate;
import nc.vo.wa.wa_ba.unit.WaUnitLoginContext;

/**
 * 变动人员
 * 
 * @see nc.ui.wa.payfile.action.AlterPayfileAction
 */
@SuppressWarnings("restriction")
public class WaBaUnitAlterAction extends HrAction {
	private static final long serialVersionUID = 4704524145613064081L;
	private IAppModelDataManager dataManager = null;
	private IEditor editor;

	public WaBaUnitAlterAction() {
		super();
		setCode("WaBaUnitAlter");
		setBtnName(ResHelper.getString("60130payfile", "060130payfile0242")/*@res "变动人员"*/);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130payfile", "060130payfile0242")/*@res "变动人员"*/+ "(Ctrl+M)");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		doActionForExtend(e);
	}

	public void doActionForExtend(ActionEvent e) throws Exception {
		/*
		WaLoginContext context = (WaLoginContext) getContext();
		PeriodVO periodVO = new PeriodVO();
		periodVO.setClassid(context.getPk_wa_class());
		periodVO.setCyear(context.getWaYear());
		periodVO.setCperiod(context.getWaPeriod());
		periodVO = WADelegator.getPayfileQuery().queryStartEndDate(periodVO);

		if (periodVO == null || periodVO.getCstartdate() == null || periodVO.getCenddate() == null) {
			//			throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0243"));//薪资期间错误！
			throw new BusinessException("期间错误！");
		}
		*/
		//		UFDate beginDate = new UFDate(periodVO.getCstartdate().getMillis());
		Calendar calendar = Calendar.getInstance();
		UFDate endDate = new UFDate(calendar.getTime());
		calendar.add(Calendar.MONTH, -1);
		UFDate beginDate = new UFDate(calendar.getTime());

		Container parent = this.getEntranceUI();
		PeriodDia periodDia =
				UIDialogFactory.newDialogInstance(PeriodDia.class, parent, ResHelper.getString("60130payfile", "060130payfile0244")/*@res "选择查询的期间"*/);
		periodDia.init(beginDate, endDate);
		periodDia.showModal();

		if (UIDialog.ID_OK != periodDia.getResult()) {
			return;
		}
		String[] date = periodDia.getDate();
		if (date == null) {
			return;
		}
		beginDate = new UFDate(date[0]);
		endDate = new UFDate(date[1]);

		// 显示变动人员窗口
		int isOk = new ShowChangePsn((WaUnitLoginContext) getContext()).showChangePsnDialog(beginDate, endDate);
		if (isOk == UIDialog.ID_OK) {
			// 刷新页面
			((AceWaBaUnitDataManager) getDataManager()).refresh();
		}
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