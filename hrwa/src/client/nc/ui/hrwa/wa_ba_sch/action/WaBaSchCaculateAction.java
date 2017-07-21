package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.event.ActionEvent;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hrwa.IWaBaSchMaintain;
import nc.ui.uif2.NCAction;
import nc.vo.pub.BusinessException;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

public class WaBaSchCaculateAction extends NCAction {
	AggWaBaSchHVO aggvo = null;
	String year;

	String period;

	Vector<WaBaSchBVO> v = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1292796453304461013L;

	public WaBaSchCaculateAction(AggWaBaSchHVO aggvo) {
		this.aggvo = aggvo;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		// NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
		IWaBaSchMaintain maintain = NCLocator.getInstance().lookup(IWaBaSchMaintain.class);
		maintain.doCaculate(new AggWaBaSchHVO[] { aggvo });

	}

}
