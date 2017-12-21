package nc.vo.hr.pf;

import nc.bs.dao.BaseDAO;
import nc.bs.pub.pf.CheckStatusCallbackContext;
import nc.bs.pub.pf.ICheckStatusCallback;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;

/**
 * �����±�����������״̬��д�������̽�����ֹ����ʱʹ��
 * @see nc.vo.hr.pf.HRPfCheckStatusCallBack
 * @author tsy
 */
public class MonthstatPfCheckStatusCallBack implements ICheckStatusCallback {

	private BaseDAO baseDAO = null;

	public void callCheckStatus(CheckStatusCallbackContext cscc) throws BusinessException {
		if (cscc.isTerminate()) {
			NCObject ncObj = NCObject.newInstance(cscc.getBillVo());
			IFlowBizItf itf = ncObj.getBizInterface(IFlowBizItf.class);
			String[] fields = new String[4];
			// ������
			itf.setApprover(cscc.getApproveId());
			fields[0] = itf.getColumnName(IFlowBizItf.ATTRIBUTE_APPROVER);
			// ����ʱ��
			itf.setApproveDate(cscc.getApproveDate() == null ? null : new UFDateTime(cscc.getApproveDate()));
			fields[1] = itf.getColumnName(IFlowBizItf.ATTRIBUTE_APPROVEDATE);
			// ����״̬
			itf.setApproveStatus(cscc.getCheckStatus());
			fields[2] = itf.getColumnName(IFlowBizItf.ATTRIBUTE_APPROVESTATUS);
			// ��������
			itf.setApproveNote(cscc.getCheckNote());
			fields[3] = itf.getColumnName(IFlowBizItf.ATTRIBUTE_APPROVENOTE);

			// �����޸ĺ�����
			SuperVO vo = (SuperVO) ((AggregatedValueObject) cscc.getBillVo()).getParentVO();
			getBaseDAO().updateVO(vo, fields);
			vo = (SuperVO) getBaseDAO().retrieveByPK(vo.getClass(), vo.getPrimaryKey());
			((AggregatedValueObject) cscc.getBillVo()).setParentVO(vo);
			//�����ĵ���ͬ����Ϊ����̬
			getBaseDAO().executeUpdate("update tbm_monthstat set " + itf.getColumnName(IFlowBizItf.ATTRIBUTE_APPROVESTATUS) + "=-1 where srcid='" + vo.getPrimaryKey() + "'");
		}
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}
}
