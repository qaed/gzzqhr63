package nc.ui.hrwa.wa_ba_unit.ace.view;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.ui.hr.comp.trn.PsnChangeDlg;
import nc.ui.pub.beans.UIDialog;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hr.comp.trn.PsnChangeVO;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.payfile.PsnChangeHelper;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;
import nc.vo.wa.wa_ba.unit.WaUnitLoginContext;
import nc.vo.wabm.util.HRWADateConvertor;

/**
 * ��ʾ�䶯��Ա�Ի���
 * 
 * @see nc.ui.wa.payfile.common.ShowChangePsn
 */
@SuppressWarnings("restriction")
public class ShowChangePsn {
	private IWaBaUnitMaintain unitMaintain = NCLocator.getInstance().lookup(IWaBaUnitMaintain.class);
	private WaUnitLoginContext context = null;

	public ShowChangePsn(WaUnitLoginContext context) {
		this.context = context;
	}

	/**
	 * ��ȡ�䶯��Ա��ѯSqlƬ��
	 * 
	 * @author liangxr on 2010-4-2
	 * @param context
	 * @param trnType
	 * @return
	 * @throws BusinessException
	 */
	public static String getAddWhere(WaUnitLoginContext context, int trnType) {
//		StringBuilder insql = new StringBuilder();
		String returnSQL = null;
		WaBaUnitBVO[] bvos = (WaBaUnitBVO[]) context.getSelectedVO().getChildren(WaBaUnitBVO.class);
//		for (int i = 0; i < bvos.length; i++) {
//			WaBaUnitBVO bvo = bvos[i];
//			if (i == 0) {
//				insql.append("'" + bvo.getPk_psnjob() + "'");
//			} else {
//				insql.append(",'" + bvo.getPk_psnjob() + "'");
//			}
//		}
		if (trnType == CommonValue.TRN_ADD) {//�½���Ա
//			returnSQL = " and psnjob.pk_psnjob  not in (" + insql.toString() + ")";
			returnSQL = " and not exists (select 1 from wa_ba_unit_b where wa_ba_unit_b.pk_wa_ba_unit='"+bvos[0].getPk_wa_ba_unit()+"' and wa_ba_unit_b.pk_psnjob=psnjob.pk_psnjob)";
		} else if (trnType == CommonValue.TRN_SUB || trnType == CommonValue.TRN_POST_MOD) {//��ְ��Ա || �䶯��Ա
//			returnSQL = " and psnjob.pk_psnjob  in (" + insql.toString() + ")";
			returnSQL = " and exists (select 1 from wa_ba_unit_b where wa_ba_unit_b.pk_wa_ba_unit='"+bvos[0].getPk_wa_ba_unit()+"' and wa_ba_unit_b.pk_psnjob=psnjob.pk_psnjob)";
		}

		return returnSQL;
	}

	/**
	 * ��ʾ�䶯��Ա�Ի���
	 * 
	 * @author liangxr on 2010-4-7
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	public int showChangePsnDialog(UFDate beginDate, UFDate endDate) throws BusinessException {
		UFLiteralDate beginLDate = HRWADateConvertor.toUFLiteralDate(beginDate);
		UFLiteralDate endLDate = HRWADateConvertor.toUFLiteralDate(endDate);
		PsnChangeDlg changeDia = new WaUnitPsnChangeDlg(context, beginLDate, endLDate);
		changeDia.showModal();

		if (changeDia.getResult() == UIDialog.ID_OK) {
			PsnChangeVO psnChangeVO = changeDia.getSelectedPks();
			dealWithChangePsn(psnChangeVO);
		}
		return changeDia.getResult();
	}

	public void dealWithChangePsn(PsnChangeVO psnChangeVO) throws BusinessException {
		if (psnChangeVO.getPsnVOlist() == null || psnChangeVO.getPsnVOlist().size() == 0) {
			throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0258")/*@res "û��ѡ����Ա��"*/);
		}

		int state = psnChangeVO.getState();

		if (PsnChangeHelper.isExists(state)) {
			deleteSelectePsn(psnChangeVO);
		} else {
			addSelectedPsn(psnChangeVO, state);
		}
	}

	/**
	 * ����Ա���ӵ�н�ʵ���
	 * 
	 * @author liangxr on 2010-2-25
	 * @param psnChangeVO
	 * @param state
	 * @throws BusinessException
	 */
	private void addSelectedPsn(PsnChangeVO psnChangeVO, int state) throws BusinessException {

		WaBaUnitBVO[] selectVOs = getSelectedPsns(psnChangeVO);
		for (WaBaUnitBVO waBaUnitBVO : selectVOs) {
			waBaUnitBVO.setStatus(VOStatus.NEW);

		}
		// ���ӵ���Ӧ��������
		AggWaBaUnitHVO aggvo = new AggWaBaUnitHVO();
		aggvo.setParentVO(this.context.getSelectedVO().getParentVO());
		aggvo.setChildren(WaBaUnitBVO.class, selectVOs);
		unitMaintain.update(aggvo);

	}

	/**
	 * ��н�ʵ���ɾ����¼
	 * 
	 * @author liangxr on 2010-2-25
	 * @param psnChangeVO
	 * @throws BusinessException
	 */
	private void deleteSelectePsn(PsnChangeVO psnChangeVO) throws BusinessException {
		WaBaUnitBVO[] needDeletePsnVOs = getSelectedPsns(psnChangeVO);
		if (needDeletePsnVOs != null && needDeletePsnVOs.length > 0) {
			checkDataPermission(needDeletePsnVOs);

			// ����ɾ��
			WaBaUnitBVO[] selectVOs = getSelectedPsns(psnChangeVO);
			for (WaBaUnitBVO waBaUnitBVO : selectVOs) {
				waBaUnitBVO.setStatus(VOStatus.DELETED);
			}
			// ִ�и���
			AggWaBaUnitHVO aggvo = new AggWaBaUnitHVO();
			aggvo.setParentVO(this.context.getSelectedVO().getParentVO());
			aggvo.setChildren(WaBaUnitBVO.class, selectVOs);
			unitMaintain.update(aggvo);
		}
	}

	/***************************************************************************
	 * ����Ƿ���Ȩ�޲�������<br>
	 * Created on 2011-5-5 11:02:09<br>
	 * 
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	protected void checkDataPermission(Object objData) throws BusinessException {
		//TODO ��������Ȩ�޺�Ҫ��Ȩ�޿���

		//		ValidationException ex =
		//				HrDataPermHelper.checkDataPermission(IHRWADataResCode.WADATA, IActionCode.DELETE, IActionCode.DELETE, objData, context);
		//
		//		HrDataPermHelper.dealValidationException(ex);
	}

	/**
	 * ��ȡѡ�еı䶯��Ա
	 */
	private WaBaUnitBVO[] getSelectedPsns(PsnChangeVO psnChangeVO) {
		List<WaBaUnitBVO> list = new ArrayList<WaBaUnitBVO>();
		if (psnChangeVO.getPsnVOlist() != null) {
			WaBaUnitBVO psnVO = null;
			for (PsnTrnVO trnVO : psnChangeVO.getPsnVOlist()) {
				psnVO = new WaBaUnitBVO();
				psnVO.setPk_wa_ba_unit(this.context.getSelectedVO().getParentVO().getPk_wa_ba_unit());
				psnVO.setPk_psnjob(trnVO.getPk_psnjob());
				psnVO.setPk_psndoc(trnVO.getPk_psndoc());
				list.add(psnVO);
			}

		}
		return list.toArray(new WaBaUnitBVO[0]);

	}
}