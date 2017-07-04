package nc.ui.hrwa.wa_ba_unit.ace.handler;

import nc.bs.hrss.pub.Logger;
import nc.bs.logging.Log;
import nc.ui.hi.ref.PsndocRefTreeModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.bd.psn.PsndocVO;

/**
 * 单据表体字段编辑后事件
 * 
 * @since 6.0
 * @version 2011-7-12 下午08:17:33
 * @author duy
 */
public class AceBodyAfterEditHandler implements IAppEventHandler<CardBodyAfterEditEvent> {
	@Override
	public void handleAppEvent(CardBodyAfterEditEvent e) {
		HYPubBO_Client bo = new HYPubBO_Client();
		// 带出人员名字
		UIRefPane refmodel = (UIRefPane) e.getBillCardPanel().getBodyItem("pk_psndoc").getComponent();
		PsndocRefTreeModel pt = (PsndocRefTreeModel) refmodel.getRefModel();// 添加过滤
		String[] refValues = pt.getPkValues();
		for (int i = 0; i < refValues.length; i++) {
			PsnJobVO[] jobvo = null;
			PsndocVO[] psnvo = null;
			try {
				psnvo = (PsndocVO[]) HYPubBO_Client.queryByCondition(PsndocVO.class, "pk_psndoc='" + refValues[i] + "'");
				jobvo =
						(PsnJobVO[]) HYPubBO_Client.queryByCondition(PsnJobVO.class, "pk_psndoc='" + refValues[i] + "'  and lastflag ='Y' and ismainjob ='Y'and pk_psnorg = (select pk_psnorg from hi_psnorg where pk_psndoc ='" + refValues[i] + "' and lastflag ='Y' and indocflag='Y')");
			} catch (UifException e1) {
				Logger.error("查询人员工作信息失败，pk_psndoc='" + refValues[i] + "'", e1);
				e1.printStackTrace();
			}
			if (jobvo != null) {
				e.getBillCardPanel().addLine();
				e.getBillCardPanel().setBodyValueAt(refValues[i], e.getRow() + i, "pk_psndoc");
				e.getBillCardPanel().setBodyValueAt(jobvo[0].getPk_dept(), e.getRow() + i, "pk_deptdoc");
				e.getBillCardPanel().setBodyValueAt(jobvo[0].getPk_org(), e.getRow() + i, "pk_corp");
			}
			if (psnvo != null) {
				e.getBillCardPanel().setBodyValueAt(psnvo[0].getName(), e.getRow() + i, "psnname");
				e.getBillCardPanel().setBodyValueAt(psnvo[0].getSex().toString(), e.getRow() + i, "sex");
			}
		}
	}
}
