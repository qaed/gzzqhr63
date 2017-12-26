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
 * ���ﶤ������ǩ������ͬ����NC
 * 
 * @author tsheay
 */
public class DingTalkAttendanceAndWorkflowPlugin implements IBackgroundWorkPlugin {

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		int attendanceErrorTimes = 0;
		int workflowErrorTimes = 0;
		int attendanceErrorMaxTimes = 20;
		int workflowErrorMaxTimes = 20;

		PreAlertObject alert = new PreAlertObject();
		alert.setMsgTitle("ͬ����������ִ�����");
		StringBuilder returnmsg = new StringBuilder();
		BaseDAO dao = new BaseDAO();
		//		SyncDept syncdept = new SyncDept(dao);
		SyncAttendance syncAttendance = new SyncAttendance(dao);
		SyncWorkFlow syncWorkFlow = new SyncWorkFlow(dao);
		PreAlertObject attendanceAlert = null;
		PreAlertObject workFlowAlert = null;
		//ͬ�����ڡ�ǩ��
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
				returnmsg.append("====���ڡ�ǩ��ͬ��ʧ�ܣ��������ӳ�ʱ�����Ժ�������\n");
			}
		}
		//ͬ��������
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
				returnmsg.append("====���������ͬ��ʧ�ܣ��������ӳ�ʱ�����Ժ�������\n");
			}
		}
		if (attendanceAlert != null && attendanceAlert.getReturnObj() != null) {
			returnmsg.append((String) attendanceAlert.getReturnObj());
			returnmsg.append("����ͬ�����ڴ���:" + (attendanceErrorTimes + 1) + "\n\n");
		} else {
			returnmsg.append("ͬ������ʧ�ܣ�\n\n");
		}
		if (workFlowAlert != null && workFlowAlert.getReturnObj() != null) {
			returnmsg.append((String) workFlowAlert.getReturnObj());
			returnmsg.append("����ͬ��������������:" + (attendanceErrorTimes + 1) + "\n\n");
		} else {
			returnmsg.append("ͬ����������ʧ�ܣ�\n\n");
		}
		returnmsg.append("===============ͬ���������===============\n");
		alert.setReturnObj(returnmsg.toString());
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		return alert;
	}
}
