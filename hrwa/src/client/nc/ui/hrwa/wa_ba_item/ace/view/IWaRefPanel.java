package nc.ui.hrwa.wa_ba_item.ace.view;

import nc.vo.wa.wa_ba.item.WaBaLoginContext;

/**
 * �˴���������˵���� �������ڣ�(2005-1-25 19:00:58)
 * 
 * @author��Administrator
 */
public interface IWaRefPanel {
	/**
	 * ���ݲ�������� �������ڣ�(2005-1-25 19:01:46)
	 * 
	 * @param hashtable java.util.Hashtable
	 */
	public void setContext(WaBaLoginContext context);

	public WaBaLoginContext getContext();

}
