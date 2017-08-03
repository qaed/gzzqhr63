package nc.ui.hrwa.wa_ba_sch.ace.handler;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.Logger;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.components.grand.util.MainGrandUtil;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.pubapp.uif2app.view.BillForm;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

/**
 * ���ݱ����ֶα༭���¼�
 * 
 * @since 6.0
 * @version 2011-7-12 ����08:17:33
 * @author duy
 */
public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {
	IWaBaUnitMaintain waBaUnitMaintain = NCLocator.getInstance().lookup(IWaBaUnitMaintain.class);
	private BillForm billForm;

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		// ����������룬���影��Ԫ�༭���Զ������������
		if ("ba_unit_code".equals(e.getKey())) {
			UIRefPane refpane = (UIRefPane) e.getBillCardPanel().getBodyItem("ba_unit_code").getComponent();
			// ���ջ�ȡ�Ľ���Ԫpkֵ
			String[] refValues = refpane.getRefModel().getPkValues();
			// ��ǰ����
			WaBaSchBVO[] baSchBVOs = (WaBaSchBVO[]) e.getBillCardPanel().getBillModel().getBodyValueVOs(WaBaSchBVO.class.getName());
			// �ѱ����ڱ����������еĽ���Ԫpk(ȥ������)
			List<String> sourceValues = new ArrayList<String>();
			if (baSchBVOs != null && baSchBVOs.length > 0) {
				for (WaBaSchBVO bvo : baSchBVOs) {
					if (bvo.getBa_unit_code() != null) {
						sourceValues.add(bvo.getBa_unit_code());
					}
				}
			}
			// �ظ��Ľ���Ԫpkֵ
			String[] duplicatePks = checkDuplicatePk(sourceValues.toArray(new String[0]), refValues);
			if (duplicatePks.length > 0) {
				// �����ظ�����Ԫ��������е�Ԫ����,��������ʾ��
				List<String> names = new ArrayList<String>();
				// SQL���
				StringBuilder sb = new StringBuilder("pk_wa_ba_unit in (");
				for (String pk : duplicatePks) {
					sb.append("'" + pk + "',");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(")");
				try {
					WaBaUnitHVO[] waBaUnitHVOs = (WaBaUnitHVO[]) HYPubBO_Client.queryByCondition(WaBaUnitHVO.class, sb.toString());
					for (WaBaUnitHVO waBaUnitHVO : waBaUnitHVOs) {
						names.add(waBaUnitHVO.getName());
					}
				} catch (UifException e1) {
					Logger.error("��ѯ�ظ���Ԫʧ�ܣ�pkֵΪ" + duplicatePks);
					e1.printStackTrace();
				}
				MessageDialog.showWarningDlg(e.getBillCardPanel(), "��ʾ", "���µ�Ԫ�����ظ���" + names + "\nϵͳ���Զ������ظ����ݣ�����������ݡ�");
			}
			// û���ظ�
			for (int i = 0; i < refValues.length; i++) {

				if (sourceValues.contains(refValues[i])) {
					e.getBillCardPanel().addLine();
					continue;
				}
				try {
					Object[] aggvos = waBaUnitMaintain.query("pk_wa_ba_unit ='" + refValues[i] + "'");
					if (aggvos != null && aggvos.length > 0) {
						AggWaBaUnitHVO aggvo = (AggWaBaUnitHVO) aggvos[0];
						e.getBillCardPanel().setBodyValueAt(refValues[i], e.getRow() + i, "ba_unit_code");// ���������룩
						// ������
						BillScrollPane TVObsp = billForm.getBillCardPanel().getBodyPanel("pk_s");
						//
						WaBaUnitBVO[] bvos = (WaBaUnitBVO[]) aggvo.getChildren(WaBaUnitBVO.class);
						for (int index = 0; index < bvos.length; index++) {
							WaBaUnitBVO waBaUnitbvo = bvos[index];
							TVObsp.addLine();
							billForm.getBillCardPanel().setBodyValueAt(waBaUnitbvo.getPk_psndoc(), index, "pk_psndoc", "pk_s");// ��Ա
						}
						e.getBillCardPanel().addLine();
					}
				} catch (BusinessException e1) {
					Logger.error("��ѯ������䵥Ԫ��Ϣʧ�ܣ�pk_psndoc='" + refValues[i] + "'", e1);
					e1.printStackTrace();
				}

			}
		}
	}

	/**
	 * <p>
	 * ����Ƿ�����ظ���pkֵ
	 * </p>
	 * 
	 * @author tsy 20170704
	 * @param source
	 * @param newPk
	 * @return �ظ���pkֵ
	 */
	public String[] checkDuplicatePk(String[] source, String[] newPk) {
		List<String> DuplicatePks = new ArrayList<String>();
		for (String s1 : source) {
			for (String s2 : newPk) {
				if (s1 != null && s1.equals(s2)) {
					DuplicatePks.add(s1);
				}
			}
		}
		return DuplicatePks.toArray(new String[0]);
	}

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}
}
