package nc.ui.wa.payfile.action;

import java.awt.Event;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.pub.WADelegator;
import nc.ui.wa.ref.WaIncludeClassRefModel;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

import org.apache.commons.lang.StringUtils;

/**
 * ����
 *
 * @author: liangxr
 * @date: 2009-12-1 ����09:50:38
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class CopyPayfileAction extends PayfileBaseAction {

	private static final long serialVersionUID = -8652090376173676145L;

	public CopyPayfileAction() {
		super();
		setBtnName(ResHelper.getString("common", "UC001-0000043")/*@res "����"*/);
		setCode("copyPayfile");
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("common", "UC001-0000043")/*@res "����"*/+ "(Ctrl+Alt+C)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('C', Event.CTRL_MASK + Event.ALT_MASK));

	}

	/**
	 * @author liangxr on 2009-12-1
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		WaLoginContext context = (WaLoginContext) getModel().getContext();

		// ����н�������նԻ���
		UIRefPane refPane = new UIRefPane(this.getEntranceUI());
		WaIncludeClassRefModel refModel = new WaIncludeClassRefModel();
		refModel.setPk_org(context.getPk_org());
		//refModel.setJoinWhere("pk_childclass <> '"+context.getPk_wa_class()+"'");

		StringBuffer whereSql = new StringBuffer();
		whereSql.append(WaClassVO.PK_WA_CLASS);
		whereSql.append(" <> '");
		whereSql.append(context.getPk_wa_class());
		whereSql.append("' and isnull(pk_childclass ,'~')<>'" + context.getPk_wa_class() + "'");
		whereSql.append(" and ");
		whereSql.append(" (batch<100 or  isnull(showbatch ,'~')='~' or showbatch= '' or showbatch is null ) ");
		refModel.setWherePart(whereSql.toString(), true);
		refPane.setRefModel(refModel);

		// �˴�Ӧʹ��refPane.getRef().showModal(),���ڿɼ���������ʱ������m_refManage��
		if (refPane.m_refManage.showModal() == UIDialog.ID_OK) {
			String copy_pk_waclass = refPane.getRefPK();
			if (StringUtils.isEmpty(copy_pk_waclass)) {
				throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0347")/*@res "û�п��Ը��Ƶ�н�ʷ���"*/);
			}
			if (refPane.getRefValue("cyear") == null || refPane.getRefValue("cperiod") == null) {
				throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0247")/*@res "��ѡ����û�������ڼ䣬���ܸ��ƣ�"*/);
			}
			String copy_wa_year = refPane.getRefValue("cyear").toString();
			String copy_wa_period = refPane.getRefValue("cperiod").toString();

			String deptpower = context.getWaLoginVO().getDeptpower();
			String psnclpower = context.getWaLoginVO().getPsnclpower();

			// ����Դн�����
			WaLoginVO fromWaClass = new WaLoginVO();
			fromWaClass.setPk_wa_class(copy_pk_waclass);
			fromWaClass.setCyear(copy_wa_year);
			fromWaClass.setCperiod(copy_wa_period);
			fromWaClass.setPk_org(context.getPk_org());
			fromWaClass.setDeptpower(deptpower);
			fromWaClass.setPsnclpower(psnclpower);

			// ����Ŀ��н�����
			WaLoginVO toWaClass = new WaLoginVO();
			toWaClass = context.getWaLoginVO();
			toWaClass.setPk_wa_class(context.getPk_wa_class());
			toWaClass.setDeptpower(deptpower);
			toWaClass.setPsnclpower(psnclpower);

			boolean reWrite = false;
			if (WADelegator.getPayfileQuery().hasRepeatPsn(fromWaClass, toWaClass)) {
				int id =
						showYesNoCancelMessage(ResHelper.getString("60130payfile", "060130payfile0248")/*@res "���Ƶ���Ա�ڵ���н�ʵ������Ѿ����ڣ��Ƿ񸲸ǣ�"*/);
				if (id == UIDialog.ID_CANCEL) {
					return;
				}
				if (id == UIDialog.ID_YES) {
					reWrite = true;
				}
			}
			// ����
			WADelegator.getPayfile().copyWaPsn(fromWaClass, context.getWaLoginVO(), reWrite);
			// ˢ��ҳ��
			((PayfileModelDataManager) getDataManager()).refresh();
			putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("60130payfile", "060130payfile0353")/*@res "���Ʋ����ɹ���"*/);
		}
	}

	private int showYesNoCancelMessage(String msg) {
		return MessageDialog.showYesNoCancelDlg(this.getEntranceUI(), null, msg);
	}

	@Override
	protected boolean isActionEnable() {
		if (getWaLoginVO().getBatch() != null && getWaLoginVO().getBatch() > 100) {
			return false;
		}
		return super.isActionEnable();
	}

}