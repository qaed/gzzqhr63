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
			// ####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####
			Object retObj = null;
			// ####重要说明：生成的业务组件方法尽量不要进行修改####
			for (AggregatedValueObject agg : getVos()) {
				setVo(agg);
				procUnApproveFlow(vo);
			}
			// ###返回值:true-审批流程由完成态返回到运行态;false-其他情况
			// 方法说明:UnApprvBill
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
		return "\t//####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####\n\tObject retObj  =null;\n\t//####重要说明：生成的业务组件方法尽量不要进行修改####\n\tprocUnApproveFlow@@;\n\t//方法说明:UnApprvBill\n\tretObj =runClassCom@ \"nc.impl.ta.leave.LeaveOffManageMaintainImpl\", \"doUnApprove\", \"nc.vo.ta.leaveoff.AggLeaveoffVO[]:6406\"@;\n\t//##################################################\n\treturn retObj;\n";
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
