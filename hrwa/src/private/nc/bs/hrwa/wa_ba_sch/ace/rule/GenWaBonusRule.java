package nc.bs.hrwa.wa_ba_sch.ace.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.hr.frame.PersistenceDbException;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.wa.wa_ba.bonus.WaBaBonusHVO;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;

public class GenWaBonusRule implements IRule<AggWaBaSchHVO> {
	IMDPersistenceQueryService query = NCLocator.getInstance().lookup(IMDPersistenceQueryService.class);

	@Override
	public void process(AggWaBaSchHVO[] aggvo) {
		Logger.init(GenWaBonusRule.class);
		Logger.debug("生成汇总单据nc.bs.hrwa.wa_ba_sch.ace.rule.GenWaBonusRule");
		if (aggvo == null || aggvo.length == 0) {
			return;
		}
		genWaBaBonusByBaSch(aggvo[0]);
//		ExceptionUtils.wrappBusinessException("测试");
	}

	public void genWaBaBonusByBaSch(AggWaBaSchHVO aggvo) {
		WaBaSchHVO baSchVO = (WaBaSchHVO) aggvo.getParentVO();
		WaBaSchBVO[] units = (WaBaSchBVO[]) aggvo.getChildrenVO();
		if (units == null || units.length == 0) {
			return;
		}
		String sch_code = baSchVO.getSch_code();
		String cyear = baSchVO.getCyear();
		String cperiod = baSchVO.getCperiod();
		String pk_org = baSchVO.getPk_org();
		String pk_group = baSchVO.getPk_group();
		String billType = "BAAL";
		String vmemo = baSchVO.getMemo();
		List<WaBaBonusHVO> allocBillVOList = new ArrayList<WaBaBonusHVO>();
		UFDate billMakeDate = new UFDate(new Date());
		for (int i = 0, len = units.length; i < len; i++) {
			WaBaSchBVO unit = units[i];
			String ba_mng_psnpk = unit.getBa_mng_psnpk();
			String pk_wa_ba_unit = unit.getPk_wa_ba_unit();
			String ba_unit_name = unit.getBa_unit_name();
			// 分配模式为奖金分配时，单据名称固定为：部门奖金分配
			String bill_name = "";
			if ("1".equals(baSchVO.getAllocmodel())) {
				bill_name = "部门奖金分配";
			} else {
				bill_name = cyear + "年" + cperiod + "月" + ba_unit_name + "绩效工资分配";
			}
			WaBaBonusHVO billVO = new WaBaBonusHVO();
			billVO.setPk_ba_sch_h(baSchVO.getPk_ba_sch_h());
			// billVO.setPk_wa_ba_unit(pk_wa_ba_unit);
			billVO.setBill_code(sch_code + "_" + i);
			billVO.setBill_name(bill_name);
			billVO.setVmemo(vmemo);
			// billVO.setBill_type(billType);
			billVO.setPk_org(pk_org);
			billVO.setPk_group(pk_group);
			billVO.setYear(cyear);
			billVO.setPeriod(cperiod);
			billVO.setPk_psndoc(ba_mng_psnpk);
			billVO.setApprovestatus(IBillStatus.FREE);
			// 自动填写制单日期
			billVO.setBilldate(billMakeDate);
			// 不自动填写制单人
			billVO.setStatus(VOStatus.NEW);
			billVO.setDr(0);
			allocBillVOList.add(billVO);
		}
		if (allocBillVOList.size() > 0) {
			try {
				// 根据部门负责人奖金分配单生成汇总单
				// AceWaBonusInsertBP action = new AceWaBonusInsertBP();
				// action.insert(bills)
				// iBonus.saveBonusVO(allocBillVOList.get(0));
				// for(BaAllocbillVO billVO : allocBillVOList){
				// iBonus.saveBonusVO(billVO);
				// }
				// end
				PersistenceDAO pDAO = new PersistenceDAO();
				pDAO.insert(allocBillVOList);
				// String sql =
				// "update wa_ba_sch_psns " + " set pk_ba_allocbill=" + " (" +
				// "  select pk_ba_allocbill from wa_ba_allocbill " +
				// "  where wa_ba_allocbill.pk_ba_sch_h=wa_ba_sch_psns.pk_ba_sch_h"
				// +
				// "  and wa_ba_allocbill.pk_wa_ba_unit=wa_ba_sch_psns.pk_wa_ba_unit"
				// +
				// "  and wa_ba_allocbill.pk_ba_sch_h=?" +
				// " ) where wa_ba_sch_psns.pk_ba_sch_h=?";
				// SQLParameter sqlParam = new SQLParameter();
				// sqlParam.addParam(baSchVO.getPk_ba_sch_h());
				// sqlParam.addParam(baSchVO.getPk_ba_sch_h());
				// pDAO.executeSQL(sql, sqlParam);
				// 后续提供发送站内短通知的功能
				// 发送预警通知
//				BaseDAO dao = new BaseDAO();
//				List<WorkflownoteVO> lsWorkflowVO = new ArrayList<WorkflownoteVO>();
//				for (WaBaBonusHVO billVO : allocBillVOList) {
//					// 设置分配金额默认值
//					String upSql =
//							"update wa_ba_sch_psns set revise_totalmoney = comput_totalmoney,value1 = comput_totalmoney where pk_ba_allocbill = '" + billVO.getPrimaryKey() + "'";
//					pDAO.executeSQLs(new String[] { upSql });
//					// 生成审批流消息 -- ：不需要生成通知消息了
//					String userSql =
//							"select u.* from sm_user u left join sm_userandclerk c on u.cuserid = c.userid " + " left join bd_psndoc p on p.pk_psnbasdoc = c.pk_psndoc " + " where p.pk_psndoc = '" + billVO.getPk_psndoc() + "' " + " and p.psnclscope = 0 " + " and isnull(u.locked_tag,'N') = 'N'";
//					List<UserVO> lsUser = (List<UserVO>) dao.executeQuery(userSql, new BeanListProcessor(UserVO.class));
//					if (lsUser != null && lsUser.size() > 0) {
//						for (UserVO user : lsUser) {
//							WorkflownoteVO workflowVO = new WorkflownoteVO();
//							workflowVO.setActiontype(WorkflownoteVO.WORKITEM_TYPE_BIZ);
//							workflowVO.setApprovestatus(0);
//							// workflowVO.setBillid(baSchVO.getPrimaryKey());
//							workflowVO.setBillno(billVO.getBill_code());
//							workflowVO.setCheckman(user.getPrimaryKey());
//							workflowVO.setIscheck("Y");
//							String username =
//									(String) dao.executeQuery("select user_name from sm_user where cuserid = '" + baSchVO.getBill_submitter() + "'", new ColumnProcessor());
//							workflowVO.setMessagenote(username + "已审核绩效工资分配方案");
//							workflowVO.setPk_billtype("XX");
//							workflowVO.setPk_businesstype(baSchVO.getPk_busitype());
//							String corpid =
//									(String) dao.executeQuery("select pk_corp from bd_psndoc where pk_psndoc = '" + billVO.getBa_mng_psnpk() + "'", new ColumnProcessor());
//							workflowVO.setPk_corp(corpid);
//							workflowVO.setReceivedeleteflag(UFBoolean.FALSE);
//							workflowVO.setSenddate(new UFDateTime(new Date()));
//							workflowVO.setSenderman(baSchVO.getBill_submitter());
//							// workflowVO.setWorkflow_type("1");
//							lsWorkflowVO.add(workflowVO);
//						}
//					}
//				}
//				if (lsWorkflowVO != null && lsWorkflowVO.size() > 0) {
//					dao.insertVOArray(lsWorkflowVO.toArray(new WorkflownoteVO[lsWorkflowVO.size()]));
//				}
				// end
			} catch (PersistenceDbException e) {
				ExceptionUtils.wrappBusinessException("生成奖金分配单据时发生异常!", e);
			}
		}
	}
}
