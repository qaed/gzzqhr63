package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;

public class N_6407_UNAPPROVE extends AbstractCompiler2 {
	private Hashtable m_methodReturnHas = new Hashtable();
	private Hashtable m_keyHas = null;

	public N_6407_UNAPPROVE() {
	}

	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			super.m_tmpVo = vo;
			// ####���ű����뺬�з���ֵ,����DLG��PNL������������з���ֵ####
			Object retObj = null;
			// ####��Ҫ˵�������ɵ�ҵ���������������Ҫ�����޸�####
			for (AggregatedValueObject agg : getVos()) {
				setVo(agg);
				procUnApproveFlow(vo);
			}
			// ###����ֵ:true-�������������̬���ص�����̬;false-�������
			// ����˵��:UnApprvBill
			retObj =
					runClass("nc.impl.ta.monthstat.MonthStatMaintainImpl", "doUnApprove", "nc.vo.ta.monthstat.AggMonthStatVO[]:6407", vo, m_keyHas);
			if (retObj != null) {
				m_methodReturnHas.put("doUnApprove", retObj);
			}
			// ##################################################
			return retObj;
		} catch (Exception ex) {
			if (ex instanceof BusinessException)
				throw (BusinessException) ex;
			else
				throw new PFBusinessException(ex.getMessage(), ex);
		}
	}

	public String getCodeRemark() {
		return "\t//####���ű����뺬�з���ֵ,����DLG��PNL������������з���ֵ####\n\tObject retObj  =null;\n\t//####��Ҫ˵�������ɵ�ҵ���������������Ҫ�����޸�####\n\tprocUnApproveFlow@@;\n\t//����˵��:UnApprvBill\n\tretObj =runClassCom@ \"nc.impl.ta.leave.LeaveOffManageMaintainImpl\", \"doUnApprove\", \"nc.vo.ta.leaveoff.AggLeaveoffVO[]:6406\"@;\n\t//##################################################\n\treturn retObj;\n";
	}

	private void setParameter(String key, Object val) {
		if (this.m_keyHas == null) {
			this.m_keyHas = new Hashtable();
		}
		if (val != null) {
			this.m_keyHas.put(key, val);
		}
	}
}
