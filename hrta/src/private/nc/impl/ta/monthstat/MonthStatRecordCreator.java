package nc.impl.ta.monthstat;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.impl.ta.monthlydata.AbstractRecordCreator;
import nc.itf.ta.ITimeRuleQueryService;
import nc.vo.iufo.approve.ApproveStateEnum;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.monthstat.MonthStatbVO;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.springframework.util.CollectionUtils;

public class MonthStatRecordCreator extends AbstractRecordCreator {

	@Override
	protected String getPkField() {
		return MonthStatVO.PK_MONTHSTAT;
	}

	@Override
	protected String getPsndocField() {
		return MonthStatVO.PK_PSNDOC;
	}

	@Override
	protected String[] getSubTableFkFields() {
		return new String[] { MonthStatbVO.PK_MONTHSTAT, MonthWorkVO.PK_MONTHSTAT };
	}

	@Override
	protected String[] getSubTableNames() {
		return new String[] { "tbm_monthstatb", "tbm_monthwork" };
	}

	@Override
	protected String getTableAlias() {
		return "monthstat";
	}

	@Override
	protected String getTableName() {
		return MonthStatVO.TABLE_NAME;
	}

	@Override
	protected Class<? extends SuperVO> getVOClass() {
		return MonthStatVO.class;
	}

	@Override
	protected void processBeforeInsert(PeriodVO periodVO, List insertList) throws BusinessException {
		if (CollectionUtils.isEmpty(insertList))
			return;
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(periodVO.getPk_org());
		//如果月报不需要审核，则isuseful默认为Y，否则为N
		UFBoolean isuseful = UFBoolean.valueOf(!timeRule.isMonthStatNeedApprove());
		String year = periodVO.getTimeyear();
		String month = periodVO.getTimemonth();
		for (Object insertVO : insertList) {
			MonthStatVO monthVO = (MonthStatVO) insertVO;
			monthVO.setIsuseful(isuseful);
			monthVO.setPk_group(periodVO.getPk_group());
			monthVO.setPk_org(periodVO.getPk_org());
			monthVO.setTbmyear(year);
			monthVO.setTbmmonth(month);
			monthVO.setApprovestatus(ApproveStateEnum.NOSTATE);//tsy添加审批状态
			monthVO.setBilltype("6407");//tsy添加单据类型，用于审批
		}
	}

	@Override
	protected String getMonthField() {
		return MonthStatVO.TBMMONTH;
	}

	@Override
	protected String getYearField() {
		return MonthStatVO.TBMYEAR;
	}

}
