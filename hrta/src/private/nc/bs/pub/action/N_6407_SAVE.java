package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.bs.trade.business.HYPubBO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;
/**
 * TaWorkFlowManager
 * @author tsheay
 *
 */
public class N_6407_SAVE extends AbstractCompiler2 {
	private Hashtable m_methodReturnHas = new Hashtable();
	private Hashtable m_keyHas = null;

	public N_6407_SAVE() {
	}

	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			this.m_tmpVo = vo;
			Object retObj = null;
			return runClass("nc.impl.ta.monthstat.MonthStatMaintainImpl", "doCommit", "nc.vo.ta.monthstat.AggMonthStatVO[]:6407", vo, null);

		} catch (Exception ex) {
			if ((ex instanceof BusinessException)) {
				throw ((BusinessException) ex);
			}
			throw new PFBusinessException(ex.getMessage(), ex);
		}
	}

	public String getCodeRemark() {
		return "\t//####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####\n\tObject retObj = null; \n\tretObj =runClassCom@ \"nc.impl.ta.leave.LeaveOffManageMaintainImpl\", \"doCommit\", \"nc.vo.ta.leaveoff.AggLeaveoffVO[]:6406\"@; \n\treturn retObj;\n";
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