package nc.ui.hrwa.wa_ba_item.ace.view;

import nc.vo.wa.wa_ba.item.WaBaLoginContext;

/**
 * 此处插入类型说明。 创建日期：(2005-1-25 19:00:58)
 * 
 * @author：Administrator
 */
public interface IWaRefPanel {
	/**
	 * 传递参数给面板 创建日期：(2005-1-25 19:01:46)
	 * 
	 * @param hashtable java.util.Hashtable
	 */
	public void setContext(WaBaLoginContext context);

	public WaBaLoginContext getContext();

}
