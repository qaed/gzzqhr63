package nc.bs.extsys.plugin.dingtalk;

import nc.bs.dao.BaseDAO;
import nc.bs.extsys.plugin.dingtalk.department.SyncDept;
import nc.bs.extsys.plugin.dingtalk.user.SyncUser;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.vo.pub.BusinessException;

/**
 * 阿里钉钉部门和人员同步到NC
 * 
 * @author tsheay
 */
public class DingTalkDeptAndPsnPlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		int deptErrorTimes = 0;
		int userErrorTimes = 0;
		int deptErrorMaxTimes = 40;
		int userErrorMaxTimes = 20;

		PreAlertObject alert = new PreAlertObject();
		alert.setMsgTitle("同步钉钉任务执行情况");
		StringBuilder returnmsg = new StringBuilder();
		BaseDAO dao = new BaseDAO();
		SyncDept syncdept1 = new SyncDept(dao);//先同步架构
		SyncUser syncuser = new SyncUser(dao);//放置人员
		SyncDept syncdept2 = new SyncDept(dao);//可以同步部门主管了
		PreAlertObject deptAlert1 = null;
		PreAlertObject deptAlert2 = null;
		PreAlertObject userAlert = null;
		//同步部门
		try {
			deptAlert1 = syncdept1.executeTask(arg0);
		} catch (Exception e) {
			if (deptErrorTimes++ < deptErrorMaxTimes) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					deptAlert1 = syncdept1.executeTask(arg0);
				} else {
					returnmsg.append("====部门同步失败，网络连接超时，请稍后再试试\n");
				}
			} else {
				throw new BusinessException(e);
			}
		}
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
		//同步部门
		try {
			deptAlert2 = syncdept2.executeTask(arg0);
		} catch (Exception e) {
			if (deptErrorTimes++ < deptErrorMaxTimes) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					deptAlert2 = syncdept2.executeTask(arg0);
				} else {
					returnmsg.append("====部门同步失败，网络连接超时，请稍后再试试\n");
				}
			} else {
				throw new BusinessException(e);
			}
		}
		if (deptAlert1 != null && deptAlert1.getReturnObj() != null) {
			returnmsg.append((String) deptAlert1.getReturnObj());
//			returnmsg.append("第一次尝试同步部门次数:" + (deptErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("第一次同步部门失败！\n\n");
		}

		if (userAlert != null && userAlert.getReturnObj() != null) {
			returnmsg.append((String) userAlert.getReturnObj());
			returnmsg.append("尝试同步人员次数:" + (userErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("同步人员失败！\n\n");
		}
		if (deptAlert2 != null && deptAlert2.getReturnObj() != null) {
			returnmsg.append((String) deptAlert2.getReturnObj());
			returnmsg.append("尝试同步部门次数:" + (deptErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("第二次同步部门失败！\n\n");
		}
		returnmsg.append("-----------同步任务完成-------------\n");
		alert.setReturnObj(returnmsg.toString());
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		return alert;
	}
}
