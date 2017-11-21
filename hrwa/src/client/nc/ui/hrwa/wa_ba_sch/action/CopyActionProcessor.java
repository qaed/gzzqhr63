package nc.ui.hrwa.wa_ba_sch.action;

import java.util.ArrayList;
import java.util.List;

import nc.desktop.ui.ServerTimeProxy;
import nc.ui.pubapp.uif2app.actions.intf.ICopyActionProcessor;
import nc.ui.trade.business.HYPubBO_Client;
import nc.uif.pub.exception.UifException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchTVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 单据复制时表头表体处理
 * 
 * @since 6.0
 * @version 2011-7-7 下午02:31:23
 * @author duy
 */
@SuppressWarnings("restriction")
public class CopyActionProcessor implements ICopyActionProcessor<AggWaBaSchHVO> {
	@Override
	public void processVOAfterCopy(AggWaBaSchHVO billVO, LoginContext context) {
		this.processHeadVO(billVO, context);
		this.processBodyVO(billVO);
	}

	private void processBodyVO(AggWaBaSchHVO vo) {
		vo.getParent().setAttributeValue(vo.getMetaData().getParent().getPrimaryAttribute().getName(), null);
		vo.getParent().setAttributeValue("ts", null);
		for (IVOMeta meta : vo.getMetaData().getChildren()) {
			if (vo.getChildren(meta) == null)
				continue;
			for (ISuperVO childvo : vo.getChildren(meta)) {
				childvo.setAttributeValue(meta.getPrimaryAttribute().getName(), null);
				childvo.setAttributeValue("pk_group", null);
				childvo.setAttributeValue("pk_org", null);
				childvo.setAttributeValue("ts", null);
				WaBaSchBVO bvo = (WaBaSchBVO) childvo;
				try {
					//更新当前的孙表
					WaBaUnitBVO[] unitbvos =
							(WaBaUnitBVO[]) HYPubBO_Client.queryByCondition(WaBaUnitBVO.class, "pk_wa_ba_unit='" + bvo.getBa_unit_code() + "' and isnull(dr,0)=0");
					List<WaBaSchTVO> tvos = new ArrayList<WaBaSchTVO>();
					if (!ArrayUtils.isEmpty(unitbvos)) {
						for (WaBaUnitBVO waBaUnitBVO : unitbvos) {
							WaBaSchTVO tvo = new WaBaSchTVO();
							tvo.setPk_psndoc(waBaUnitBVO.getPk_psndoc());
							tvo.setPk_psnjob(waBaUnitBVO.getPk_psnjob());
							tvo.setDr(0);
							tvos.add(tvo);
						}
					}
					bvo.setPk_s(tvos.toArray(new WaBaSchTVO[0]));
				} catch (UifException e) {

				}
			}
		}
	}

	private void processHeadVO(AggWaBaSchHVO vo, LoginContext context) {
		UFDateTime datetime = ServerTimeProxy.getInstance().getServerTime();
		WaBaSchHVO hvo = vo.getParentVO();
//		try {
//			//自动生成code
//			hvo.setSch_code(((IHrBillCode) NCLocator.getInstance().lookup(IHrBillCode.class)).getBillCode("BAAL", context.getPk_group(), context.getPk_org()));
//		} catch (BusinessException e) {
//			e.printStackTrace();
//		}
		// 设置空处理
		hvo.setApprover(null);
		hvo.setApprovedate(null);
		hvo.setModifier(null);
		hvo.setModifiedtime(null);
		hvo.setCreator(null);
		hvo.setCreationtime(null);
		// hvo.setTs(null);
		// 设置默认值
		hvo.setBilldate(datetime.getDate());
		hvo.setPk_group(context.getPk_group());
		hvo.setPk_org(context.getPk_org());
		hvo.setAttributeValue("approvestatus", BillStatusEnum.FREE.value());
	}
}
