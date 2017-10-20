package nc.vo.wa.wa_ba.unit;

import nc.vo.uif2.LoginContext;

public class WaUnitLoginContext extends LoginContext {
	private static final long serialVersionUID = -5037789552037653794L;
	private AggWaBaUnitHVO selectedVO = null;//当前选中的aggvo

	public AggWaBaUnitHVO getSelectedVO() {
		return selectedVO;
	}

	public void setSelectedVO(AggWaBaUnitHVO selectedVO) {
		this.selectedVO = selectedVO;
	}

}
