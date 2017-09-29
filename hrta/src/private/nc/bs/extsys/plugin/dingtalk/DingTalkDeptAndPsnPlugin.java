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
 * ���ﶤ�����ź���Աͬ����NC
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
		alert.setMsgTitle("ͬ����������ִ�����");
		StringBuilder returnmsg = new StringBuilder();
		BaseDAO dao = new BaseDAO();
		SyncDept syncdept1 = new SyncDept(dao);//��ͬ���ܹ�
		SyncUser syncuser = new SyncUser(dao);//������Ա
		SyncDept syncdept2 = new SyncDept(dao);//����ͬ������������
		PreAlertObject deptAlert1 = null;
		PreAlertObject deptAlert2 = null;
		PreAlertObject userAlert = null;
		//ͬ������
		try {
			deptAlert1 = syncdept1.executeTask(arg0);
		} catch (Exception e) {
			if (deptErrorTimes++ < deptErrorMaxTimes) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					deptAlert1 = syncdept1.executeTask(arg0);
				} else {
					returnmsg.append("====����ͬ��ʧ�ܣ��������ӳ�ʱ�����Ժ�������\n");
				}
			} else {
				throw new BusinessException(e);
			}
		}
		//ͬ���û�
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
				returnmsg.append("====��Աͬ��ʧ�ܣ��������ӳ�ʱ�����Ժ�������\n");
			}
		}
		//ͬ������
		try {
			deptAlert2 = syncdept2.executeTask(arg0);
		} catch (Exception e) {
			if (deptErrorTimes++ < deptErrorMaxTimes) {
				if (e.getMessage().contains("-1") || e.getMessage().contains("-2")) {
					deptAlert2 = syncdept2.executeTask(arg0);
				} else {
					returnmsg.append("====����ͬ��ʧ�ܣ��������ӳ�ʱ�����Ժ�������\n");
				}
			} else {
				throw new BusinessException(e);
			}
		}
		if (deptAlert1 != null && deptAlert1.getReturnObj() != null) {
			returnmsg.append((String) deptAlert1.getReturnObj());
//			returnmsg.append("��һ�γ���ͬ�����Ŵ���:" + (deptErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("��һ��ͬ������ʧ�ܣ�\n\n");
		}

		if (userAlert != null && userAlert.getReturnObj() != null) {
			returnmsg.append((String) userAlert.getReturnObj());
			returnmsg.append("����ͬ����Ա����:" + (userErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("ͬ����Աʧ�ܣ�\n\n");
		}
		if (deptAlert2 != null && deptAlert2.getReturnObj() != null) {
			returnmsg.append((String) deptAlert2.getReturnObj());
			returnmsg.append("����ͬ�����Ŵ���:" + (deptErrorTimes + 1) + "\n");
		} else {
			returnmsg.append("�ڶ���ͬ������ʧ�ܣ�\n\n");
		}
		returnmsg.append("-----------ͬ���������-------------\n");
		alert.setReturnObj(returnmsg.toString());
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		return alert;
	}
}
