package nc.ui.hrwa.wa_ba_unit.ace.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.hrss.pub.Logger;
import nc.ui.hi.ref.PsndocRefTreeModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;

/**
 * ���ݱ����ֶα༭���¼�
 * 
 * @since 6.0
 * @version 2011-7-12 ����08:17:33
 * @author duy
 */
@SuppressWarnings("restriction")
public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {

	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		if ("pk_psndoc".equals(e.getKey())) {
			// ������Ա����
			UIRefPane refmodel = (UIRefPane) e.getBillCardPanel().getBodyItem("pk_psndoc").getComponent();
			PsndocRefTreeModel pt = (PsndocRefTreeModel) refmodel.getRefModel();// ��ӹ���
			String[] refValues = pt.getPkValues();
			//					;pt.getFieldIndex("hi_psnjob.pk_psnjob");pt.getHiddenFieldCode()

			@SuppressWarnings("unchecked")
			Vector<Vector<String>> selectedData = pt.getSelectedData();
			Map<String, String> psndocAndpsnjob = new HashMap<String, String>();
			Integer psndocIndex = pt.getFieldIndex("bd_psndoc.pk_psndoc");
			Integer psnjobIndex = pt.getFieldIndex("hi_psnjob.pk_psnjob");
			for (Vector<String> data : selectedData) {
				psndocAndpsnjob.put(data.get(psndocIndex), data.get(psnjobIndex));
			}
			WaBaUnitBVO[] waBaUnitBVOs = (WaBaUnitBVO[]) e.getBillCardPanel().getBillModel().getBodyValueVOs(WaBaUnitBVO.class.getName());
			// ��������������е�pk(ȥ������)
			List<String> sourceValues = new ArrayList<String>();
			for (WaBaUnitBVO bvo : waBaUnitBVOs) {
				if (bvo.getPk_psndoc() != null) {
					sourceValues.add(bvo.getPk_psndoc());
				}
			}
			// 1.��Ϊ�������У���sourceValues�а���������pkֵ��Ҫ�޳�
			// 2.���޸�ԭ���У���sourceValues�����ԭ����pkֵ�������л�ȡ��ֵҲ��ԭ����pkֵ
			sourceValues.remove(((WaBaUnitBVO) e.getBillCardPanel().getBillModel().getBodyValueRowVO(e.getRow(), WaBaUnitBVO.class.getName())).getPk_psndoc());
			// �ظ���Ա��pkֵ
			String[] duplicatePks = checkDuplicatePk(sourceValues.toArray(new String[0]), refValues);
			if (duplicatePks.length > 0) {
				// �����ظ���Ա�����������Ա����,��������ʾ��
				List<String> names = new ArrayList<String>();
				// sql���
				StringBuilder sb = new StringBuilder("pk_psndoc in (");
				for (String pk : duplicatePks) {
					sb.append("'" + pk + "',");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(")");
				try {
					PsndocVO[] psnvos = (PsndocVO[]) HYPubBO_Client.queryByCondition(PsndocVO.class, sb.toString());
					for (PsndocVO psndocvo : psnvos) {
						names.add(psndocvo.getName());
					}
				} catch (UifException e1) {
					Logger.error("��ѯ�ظ���Աʧ�ܣ�pkֵΪ" + duplicatePks);
					e1.printStackTrace();
				}
				MessageDialog.showWarningDlg(e.getBillCardPanel(), "��ʾ", "������Ա�����ظ���" + names + "\nϵͳ���Զ������ظ����ݣ���������С����С������Ƿ�ԭΪ�ظ�����");
			}
			// û���ظ���Ա
			for (int i = 0; i < refValues.length; i++) {
				if (i < refValues.length - 1)
					e.getBillCardPanel().addLine();

				if (sourceValues.contains(refValues[i])) {
					e.getBillCardPanel().setBodyValueAt(null, e.getRow() + i, "pk_psndoc");
					continue;
				}
				//			PsnJobVO[] jobvo = null;
				//			try {
				//				jobvo =
				//						(PsnJobVO[]) HYPubBO_Client.queryByCondition(PsnJobVO.class, "pk_psndoc='" + refValues[i] + "'  and lastflag ='Y' and ismainjob ='Y'and pk_psnorg = (select pk_psnorg from hi_psnorg where pk_psndoc ='" + refValues[i] + "' and lastflag ='Y' and indocflag='Y')");
				//			} catch (UifException e1) {
				//				Logger.error("��ѯ��Ա������Ϣʧ�ܣ�pk_psndoc='" + refValues[i] + "'", e1);
				//				e1.printStackTrace();
				//			}
				//			if (jobvo != null) {
				e.getBillCardPanel().setBodyValueAt(refValues[i], e.getRow() + i, "pk_psndoc");
				e.getBillCardPanel().setBodyValueAt(psndocAndpsnjob.get(refValues[i]), e.getRow() + i, "pk_psnjob");
				//			}
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
		if (source != null) {
			for (String s1 : source) {
				for (String s2 : newPk) {
					if (s1 != null && s1.equals(s2)) {
						DuplicatePks.add(s1);
					}
				}
			}

		}
		return DuplicatePks.toArray(new String[0]);
	}
}
