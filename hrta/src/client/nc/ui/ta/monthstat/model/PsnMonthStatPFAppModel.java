package nc.ui.ta.monthstat.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.IMonthStatManageMaintain;
import nc.ui.hr.pf.model.PFAppModel;
import nc.ui.uif2.AppEvent;
import nc.vo.pub.BusinessException;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.period.PeriodVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 新增的人员模型，用于审批
 * 
 * @author tsheay
 */
@SuppressWarnings("restriction")
public class PsnMonthStatPFAppModel extends PFAppModel {
	PeriodVO periodVO;
	private boolean showAttendInfo;

	public PsnMonthStatPFAppModel() {
	}

	public boolean isShowAttendInfo() {
		return this.showAttendInfo;
	}

	public void setShowAttendInfo(boolean showAttendInfo) {
		this.showAttendInfo = showAttendInfo;
		fireEvent(new AppEvent("ATTENDINFO_CHANGED", this, null));
	}

	protected int getFun_type() {
		return 1;
	}

	protected int getRpt_type() {
		return 0;
	}

	public PeriodVO getPeriodVO() {
		return this.periodVO;
	}

	public void setPeriodVO(PeriodVO periodVO) {
		this.periodVO = periodVO;
	}

	public MonthStatVO[] save(MonthStatVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		IMonthStatManageMaintain maintain = (IMonthStatManageMaintain) NCLocator.getInstance().lookup(IMonthStatManageMaintain.class);
		vos = maintain.save(getContext().getPk_org(), vos);
		directlyUpdate(vos);
		return vos;
	}
}