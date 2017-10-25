package nc.vo.wa.wa_ba.item;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import nc.vo.hr.caculate.WaBmLoginContext;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaState;

/**
 * @author: zhangg
 * @date: 2009-11-23 ����11:17:12
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
@SuppressWarnings("serial")
public class WaBaLoginContext extends WaBmLoginContext {
	private WaBaLoginVO waLoginVO;// н�������Ϣ�� �����ڼ䣬 ״̬

	private Integer selectCol;// ����ѡ�е���

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
	 * ����������Ƿ��п�ֵ
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
	 * �õ��û���ѡ���н���꣨��һ�������µ�ҵ���꣩
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
	 * �õ��û���ѡ���н���ڼ䣨��һ�������µ�ҵ���ڼ䣩
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
	 * ������֯��н�ʷ�����н���ڼ� ����ȷ��������״̬
	 * 
	 * @throws BusinessException
	 */
	public void refreshWaState() throws BusinessException {
		//		WaBaLoginVO waLoginVO = NCLocator.getInstance().lookup(IWaPub.class).getWaclassVOWithState(getWaLoginVO());
		//	  this.setWaLoginVO(waLoginVO);

	}

	/**
	 * ��ǰѡ�е�н�ʷ����Ƿ��η�н�ĸ�����
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
