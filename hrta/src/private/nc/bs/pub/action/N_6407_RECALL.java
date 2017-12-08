package nc.bs.pub.action;

import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * @author tsheay
 * @see N_10GY_UNSAVE
 */
public class N_6407_RECALL extends AbstractCompiler2 {
	public N_6407_RECALL() {
	}

	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			this.m_tmpVo = vo;
			Object retObj = null;
			for (AggregatedValueObject agg : getVos()) {
				setVo(agg);
				procRecallFlow(vo);
			}
			return runClass("nc.impl.ta.monthstat.MonthStatMaintainImpl", "unCommit", "nc.vo.ta.monthstat.AggMonthStatVO[]:6407", vo, null);
		} catch (Exception ex) {
			if ((ex instanceof BusinessException)) {
				throw ((BusinessException) ex);
			}
			throw new PFBusinessException(ex.getMessage(), ex);
		}
	}

	public String getCodeRemark() {
		return "\tprocRecallFlow(m_tmpVo);\n\treturn getVo();\n";
	}
}
