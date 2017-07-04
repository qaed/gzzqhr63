package nc.ui.hrwa.wa_ba_unit.ace.handler;

import java.util.ArrayList;
import java.util.List;

import nc.bs.hrss.pub.Logger;
import nc.bs.logging.Log;
import nc.ui.hi.ref.PsndocRefTreeModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.event.card.CardBodyAfterEditEvent;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;
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
		WaBaUnitBVO[] waBaUnitBVOs = (WaBaUnitBVO[]) e.getBillCardPanel().getBillModel().getBodyValueVOs(WaBaUnitBVO.class.getName());
		// 保存表体中所有行的pk(去除空行)
		List<String> sourceValues = new ArrayList<String>();
		for (WaBaUnitBVO bvo : waBaUnitBVOs) {
			if (bvo.getPk_psndoc() != null) {
				sourceValues.add(bvo.getPk_psndoc());
			}
		}
		// 重复人员的pk值
		String[] duplicatePks = checkDuplicatePk(sourceValues.toArray(new String[0]), refValues);
		if (duplicatePks.length > 0) {
			// 存在重复人员，查出所有人员姓名,并弹出提示框
			List<String> names = new ArrayList<String>();
			// sql语句
			StringBuilder sb = new StringBuilder("pk_psndoc in (");
			for (String pk : duplicatePks) {
				sb.append("'" + pk + "',");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			try {
				PsndocVO[] psnvos = (PsndocVO[]) HYPubBO_Client.queryByCondition(PsndocVO.class, sb.toString());
				for (PsndocVO psndocvo : psnvos) {
					names.add(psndocvo.getName());
				}
			} catch (UifException e1) {
				Logger.error("查询重复人员失败，pk值为" + duplicatePks);
				e1.printStackTrace();
			}
			MessageDialog.showWarningDlg(e.getBillCardPanel(), "提示", "以下人员存在重复：" + names+"\n系统将自动过滤重复数据，请检查带出数据。");
		}
		// 没有重复人员
		for (int i = 0; i < refValues.length; i++) {
			if (sourceValues.contains(refValues[i])) {
				continue;
			}
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

	/**
	 * <p>
	 * 检查是否存在重复的pk值
	 * </p>
	 * 
	 * @author tsy 20170704
	 * @param source
	 * @param newPk
	 * @return 重复的pk值
	 */
	public String[] checkDuplicatePk(String[] source, String[] newPk) {
		List<String> DuplicatePks = new ArrayList<String>();
		for (String s1 : source) {
			for (String s2 : newPk) {
				if (s1 != null && s1.equals(s2)) {
					DuplicatePks.add(s1);
				}
			}
		}
		return DuplicatePks.toArray(new String[0]);
	}
}
