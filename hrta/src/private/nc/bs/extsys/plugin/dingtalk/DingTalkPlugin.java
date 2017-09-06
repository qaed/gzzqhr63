package nc.bs.extsys.plugin.dingtalk;

import nc.bs.dao.BaseDAO;
import nc.bs.extsys.plugin.dingtalk.attendance.SyncAttendance;
import nc.bs.extsys.plugin.dingtalk.user.SyncUser;
import nc.bs.extsys.plugin.dingtalk.workflow.SyncWorkFlow;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.vo.pub.BusinessException;

/**
 * 阿里钉钉同步到NC
 * 
 * @author tsheay
 */
public class DingTalkPlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		//		int deptErrorTimes = 0;
		int userErrorTimes = 0;
		int attendanceErrorTimes = 0;
		int workflowErrorTimes = 0;
		int userErrorMaxTimes = 20;
		int attendanceErrorMaxTimes = 20;
		int workflowErrorMaxTimes = 20;

		PreAlertObject alert = new PreAlertObject();
		alert.setMsgTitle("同步钉钉任务执行情况");
		StringBuilder returnmsg = new StringBuilder();
		BaseDAO dao = new BaseDAO();
		//		SyncDept syncdept = new SyncDept(dao);
		SyncUser syncuser = new SyncUser(dao);
		SyncAttendance syncAttendance = new SyncAttendance(dao);
		SyncWorkFlow syncWorkFlow = new SyncWorkFlow(dao);
		PreAlertObject deptAlert = null;
		PreAlertObject userAlert = null;
		PreAlertObject attendanceAlert = null;
		PreAlertObject workFlowAlert = null;
		//同步部门
		/*
		try {
			deptAlert = syncdept.executeTask(arg0);
		} catch (Exception e) {
			if (deptErrorTimes++ < 20) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					deptAlert = syncdept.executeTask(arg0);
				} else {
					returnmsg.append("====部门同步失败，网络连接超时，请稍后再试试\n");
				}
			} else {
				throw new BusinessException(e);
			}
		}
		*/
		//同步用户
		try {
			userAlert = syncuser.executeTask(arg0);
		} catch (Exception e) {
			if (userErrorTimes++ < userErrorMaxTimes) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					try {
						Thread.sleep(3000L);
					} catch (InterruptedException e1) {
						Logger.error(e1);
					}
					userAlert = syncuser.executeTask(arg0);
				} else {
					throw new BusinessException(e);
				}
			} else {
				returnmsg.append("====人员同步失败，网络连接超时，请稍后再试试\n");
			}
		}
		//同步考勤、签到
		try {
			attendanceAlert = syncAttendance.executeTask(arg0);
		} catch (Exception e) {
			if (attendanceErrorTimes++ < attendanceErrorMaxTimes) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					try {
						Thread.sleep(3000L);
					} catch (InterruptedException e1) {
						Logger.error(e1);
					}
					attendanceAlert = syncAttendance.executeTask(arg0);
				} else {
					throw new BusinessException(e);
				}
			} else {
				returnmsg.append("====考勤、签到同步失败，网络连接超时，请稍后再试试\n");
			}
		}
		//同步出差、外出
		try {
			workFlowAlert = syncWorkFlow.executeTask(arg0);
		} catch (Exception e) {
			if (workflowErrorTimes++ < workflowErrorMaxTimes) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					try {
						Thread.sleep(3000L);
					} catch (InterruptedException e1) {
						Logger.error(e1);
					}
					workFlowAlert = syncWorkFlow.executeTask(arg0);
				} else {
					throw new BusinessException(e);
				}
			} else {
				returnmsg.append("====外出、出差同步失败，网络连接超时，请稍后再试试\n");
			}
		}
		//		if (deptAlert != null && deptAlert.getReturnObj() != null) {
		//			returnmsg.append((String) deptAlert.getReturnObj());
		//		}
		if (userAlert != null && userAlert.getReturnObj() != null) {
			returnmsg.append((String) userAlert.getReturnObj());
			returnmsg.append("尝试同步人员次数:" + (userErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("同步人员失败！\n\n");
		}
		if (attendanceAlert != null && attendanceAlert.getReturnObj() != null) {
			returnmsg.append((String) attendanceAlert.getReturnObj());
			returnmsg.append("尝试同步考勤次数:" + (attendanceErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("同步考勤失败！\n\n");
		}
		if (workFlowAlert != null && workFlowAlert.getReturnObj() != null) {
			returnmsg.append((String) workFlowAlert.getReturnObj());
			returnmsg.append("尝试同步出差审批次数:" + (attendanceErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("同步出差审批失败！\n\n");
		}
		returnmsg.append("-----------同步任务完成-------------\n");
		alert.setReturnObj(returnmsg.toString());
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		return alert;
	}
}
