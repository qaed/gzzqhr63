package nc.vo.wa.wa_ba.item;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import nc.vo.hr.caculate.WaBmLoginContext;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaState;

/**
 * @author: zhangg
 * @date: 2009-11-23 上午11:17:12
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class WaBaLoginContext extends WaBmLoginContext {
	private WaBaLoginVO waLoginVO;// 薪资类别信息， 包括期间， 状态

	private Integer selectCol;// 锁定选中的列

	/***************************************************************************
	 * <br>
	 * Created on 2012-7-5 13:03:49<br>
	 * 
	 * @author daicy
	 * @return the selectCol
	 ***************************************************************************/
	public Integer getSelectCol() {
		return selectCol;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2012-7-5 13:03:49<br>
	 * 
	 * @author daicy
	 * @param selectCol the selectCol to set
	 ***************************************************************************/
	public void setSelectCol(Integer selectCol) {
		this.selectCol = selectCol;
	}

	/**
	 * @author xuanlt on 2010-3-1
	 */
	public WaBaLoginContext() {
		super();
	}

	/**
	 * @author zhangg on 2009-11-25
	 * @return the waLoginVO
	 */
	public WaBaLoginVO getWaLoginVO() {
		if (waLoginVO == null) {
			waLoginVO = new WaBaLoginVO();
		}
		return waLoginVO;
	}

	/**
	 * @author zhangg on 2009-11-25
	 * @param waLoginVO the waLoginVO to set
	 */
	public void setWaLoginVO(WaBaLoginVO waLoginVO) {
		this.waLoginVO = waLoginVO;
	}

	/**
	 * 检查属性中是否有空值
	 * 
	 * @author zhangg on 2009-12-1
	 * @return
	 */
	@Override
	public boolean isContextNotNull() {
		return isNotBlank(getPk_org()) && isNotBlank(getPk_wa_class()) && isNotBlank(getWaYear()) && isNotBlank(getWaPeriod());
	}

	public String getPk_wa_class() {
		if (getWaLoginVO() == null) {
			return null;
		}
		return getWaLoginVO().getPk_wa_class();
	}

	/**
	 * 得到用户当选择的薪资年（不一定是最新的业务年）
	 * 
	 * @return
	 */
	public String getWaYear() {
		if (getWaLoginVO() == null || getWaLoginVO().getPeriodVO() == null) {
			return null;
		}
		return getWaLoginVO().getPeriodVO().getCyear();
	}

	/**
	 * 得到用户当选择的薪资期间（不一定是最新的业务期间）
	 * 
	 * @return java.lang.String
	 */
	public String getWaPeriod() {
		if (getWaLoginVO() == null || getWaLoginVO().getPeriodVO() == null) {
			return null;
		}
		return getWaLoginVO().getPeriodVO().getCperiod();
	}

	@Override
	public String getCyear() {
		if (getWaLoginVO() == null || getWaLoginVO().getPeriodVO() == null) {
			return null;
		}
		return getWaLoginVO().getPeriodVO().getCyear();
	}

	@Override
	public String getCperiod() {
		if (getWaLoginVO() == null || getWaLoginVO().getPeriodVO() == null) {
			return null;
		}
		return getWaLoginVO().getPeriodVO().getCperiod();
	}

	public String getPk_prnt_class() {
		if (getWaLoginVO() == null || getWaLoginVO().getPeriodVO() == null) {
			return null;
		}
		return getWaLoginVO().getPk_prnt_class();
	}

	public WaState getWaState() {
		if (getWaLoginVO() == null) {
			return null;
		}
		return getWaLoginVO().getState();
	}

	/**
	 * 根据组织、薪资方案、薪资期间 重新确定方案的状态
	 * 
	 * @throws BusinessException
	 */
	public void refreshWaState() throws BusinessException {
		//		WaBaLoginVO waLoginVO = NCLocator.getInstance().lookup(IWaPub.class).getWaclassVOWithState(getWaLoginVO());
		//	  this.setWaLoginVO(waLoginVO);

	}

	/**
	 * 当前选中的薪资发放是否多次发薪的父方案
	 * 
	 * @return
	 */
	public boolean isMutiParentWaclss() {
		return WaBaLoginVOHelper.isMultiClass(getWaLoginVO());

	}

	@Override
	public String getClassPK() {
		return this.getPk_wa_class();
	}

	@Override
	public String getPk_country() {
		return this.getWaLoginVO().getPk_country();
	}

}
