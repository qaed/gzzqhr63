package nc.bs.pub.action;

import java.util.Hashtable;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.ta.monthstat.AggMonthStatVO;
import nc.vo.uap.pf.PFBusinessException;

public class N_6407_APPROVE extends AbstractCompiler2 {
	private Hashtable m_methodReturnHas = new Hashtable();
	private Hashtable m_keyHas = null;

	public N_6407_APPROVE() {
	}

	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			this.m_tmpVo = vo;

			Object retObj = null;
			procFlowBacth(vo);

			AggregatedValueObject[] aggvos = vo.m_preValueVos;
			if ((aggvos != null) && (aggvos.length != 0)) {
				AggMonthStatVO[] tempaggvo = new AggMonthStatVO[aggvos.length];
				for (int i = 0; i < aggvos.length; i++) {
					tempaggvo[i] = ((AggMonthStatVO) aggvos[i]);
				}
				vo.m_preValueVos = tempaggvo;
			} else {
				vo.m_preValueVos = new AggMonthStatVO[0];
			}
			return runClass("nc.impl.ta.monthstat.MonthStatMaintainImpl", "doApprove", "nc.vo.ta.monthstat.AggMonthStatVO[]:6407", vo, this.m_keyHas);
			//	      return runClass("nc.impl.ta.leave.LeaveAplApvManageMaintainImpl", "doApprove", "nc.vo.ta.leave.AggLeaveVO[]:6404", vo, this.m_keyHas);

		} catch (Exception ex) {
			if ((ex instanceof BusinessException)) {
				throw ((BusinessException) ex);
			}
			throw new PFBusinessException(ex.getMessage(), ex);
		}
	}

	public String getCodeRemark() {
		return "\t//####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####\n\tObject retObj = null; \n\tprocFlowBacth( vo );  \n retObj =runClassCom@ \"nc.impl.ta.leave.LeaveManageMaintainImpl\", \"doApprove\", \"nc.vo.ta.leave.AggLeaveVO[]:6404\"@; \n\treturn retObj;\n";
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