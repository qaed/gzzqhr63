package nc.bs.extsys.plugin.dingtalk.attendance;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.extsys.plugin.dingtalk.OApiException;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.extsys.plugin.dingtalk.department.DepartmentHelper;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.impl.ta.importdata.ImportDataDAO;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.importdata.ImportDataVO;
import nc.vo.ta.log.TaBusilogUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.open.client.api.model.corp.Department;
import com.dingtalk.open.client.common.SdkInitException;
import com.dingtalk.open.client.common.ServiceException;
import com.dingtalk.open.client.common.ServiceNotExistException;

public class SyncAttendance implements IBackgroundWorkPlugin {
	private BaseDAO dao;

	public SyncAttendance() {
	}

	public SyncAttendance(BaseDAO dao) {
		this.dao = dao;
	}

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		PreAlertObject alert = new PreAlertObject();
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		alert.setMsgTitle("ͬ��������������\n");
		StringBuilder returnmsg = new StringBuilder();
		returnmsg.append("===============��ʼͬ��������������===============\n");
		StringBuilder sql = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		Map<String, Object> map = arg0.getKeyMap();
		String checkDateFrom = (String) map.get("checkDateFrom");
		String checkDateTo = (String) map.get("checkDateTo");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//		SimpleDateFormat sdfwithtime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if (checkDateFrom == null || "".equals(checkDateFrom.trim())) {
			returnmsg.append("δ�����Զ���checkDateFrom(��ʼʱ��)��Ĭ�ϡ���ʼʱ�ڡ�Ϊ " + sdf.format(calendar.getTime()) + "\n");
			checkDateFrom = sdf.format(calendar.getTime()) + " 00:00:00";
		} else {
			returnmsg.append("�����Զ���checkDateFrom(��ʼʱ��):" + checkDateFrom + "\n");
			checkDateFrom += " 00:00:00";
		}
		if (checkDateTo == null || "".equals(checkDateTo.trim())) {
			returnmsg.append("δ�����Զ���checkDateTo(����ʱ��)��Ĭ�ϡ�����ʱ�ڡ�Ϊ " + sdf.format(calendar.getTime()) + "\n");
			checkDateTo = sdf.format(calendar.getTime()) + " 23:59:59";
		} else {
			returnmsg.append("�����Զ���checkDateTo(����ʱ��):" + checkDateTo + "\n");
			checkDateTo += " 23:59:59";
		}
		returnmsg.append("���ε��뿼�ڼ�ǩ����������Ϊ��").append(checkDateFrom).append(" --- ").append(checkDateTo).append("\n");

		try {

			//-------------------------ͬ����Ա���ڴ�����
			// ���ڵ����µ���Ա
			sql.append("select timecardid from tbm_psndoc where nvl(timecardid,0)<>0 and nvl(dr,0)=0");
			List<String> userIds = (List<String>) getDao().executeQuery(sql.toString(), new ColumnListProcessor());
			List<ImportDataVO> importdatas = new ArrayList<ImportDataVO>();

			// ÿ������ȡ50���˵Ŀ�������
			returnmsg.append("������Ҫ���뿼����Ա(���ڵ�����Ա)��Ϊ��" + userIds.size() + "\n");
			for (int i = 0; i < userIds.size(); i = i + 50) {
				List<String> subUserIds = userIds.subList(i, i + 50 > userIds.size() ? userIds.size() : i + 50);
				JSONArray attendanceRecord =
						AttendanceHelper.listRecord(AuthHelper.getAccessToken(), subUserIds, checkDateFrom, checkDateTo);
				// ����׼���������ݿ��VO
				for (int j = 0; j < attendanceRecord.size(); j++) {
					Map recordMap = (Map) attendanceRecord.get(j);
					Date workDate = new Date((Long) recordMap.get("userCheckTime"));
					int datastatus = "OnDuty".equals(recordMap.get("checkType")) ? 0 : 1; //������ʶ��ǩ����ǩ�˱�־��   0=����1=����2=���Ž����ݣ�
					sql.delete(0, sql.length());
					sql.append("select * from tbm_psndoc where timecardid='" + (String) recordMap.get("userId") + "'");
					Map tbmPsndoc = (Map) getDao().executeQuery(sql.toString(), new MapProcessor());
					ImportDataVO importDataVO = new ImportDataVO();
					importDataVO.setCalendardate(new UFLiteralDate(workDate));//ˢ������
					importDataVO.setCalendartime(new UFDateTime(workDate));//ˢ��ʱ�� 
					importDataVO.setCreator((String) tbmPsndoc.get("pk_psndoc"));
					importDataVO.setCreationtime(new UFDateTime(workDate));//����ʱ�� 
					importDataVO.setDatastatus(datastatus);
					importDataVO.setDatatype(2);//ˢ������ 
					importDataVO.setPk_group((String) tbmPsndoc.get("pk_group"));
					importDataVO.setPk_org((String) tbmPsndoc.get("pk_org"));
					importDataVO.setPk_psndoc((String) tbmPsndoc.get("pk_psndoc"));
					importDataVO.setTimecardid((String) recordMap.get("userId"));//���ڿ��� 
					importdatas.add(importDataVO);
				}
			}
			returnmsg.append("����Ҫ���뿼��������Ϊ��" + importdatas.size() + "��\n");
			if (importdatas.size() > 0) {
				//IImportDataManageMaintain matain = NCLocator.getInstance().lookup(IImportDataManageMaintain.class);
				//����Maintain����̫���ˣ�ֱ��ʹ��DAO���в���
				ImportDataDAO dmo = new ImportDataDAO();
				//���ж��Ƿ����ظ����룬���Ϊ�ظ����룬��ɾ������ǰ������
				dmo.deleteArrayForImport(importdatas.get(0).getPk_org(), importdatas.toArray(new ImportDataVO[0]), ICalendar.BASE_TIMEZONE);
				//ִ�е������(19λʱ�������ݿ��ж��Ա�׼ʱ�����б���)
				String[] pk_importdatas = dmo.execInsertData(importdatas.toArray(new ImportDataVO[0]), ICalendar.BASE_TIMEZONE);
				// ������¼�
				EventDispatcher.fireEvent(new BusinessEvent("556ce8f3-ab1e-4820-8e1e-b34e500cfd22", "1002", importdatas));
				//�����ƥ��Ĺ���������¼����Ҫ�����˴���Ĺ���������dataimportstatus�ֶ�����ΪY
				dmo.updatePsncalendar(importdatas.get(0).getPk_org(), pk_importdatas);
				//ҵ����־
				TaBusilogUtil.writeImportDataBusiLog(importdatas.toArray(new ImportDataVO[0]));
			}
			//-------------------------ͬ����Աǩ������
			returnmsg.append("===============��ʼͬ������ǩ������===============\n");
			//ɾ���ظ�ǩ������
			deleteCheckindata(checkDateFrom, checkDateTo);
			//����ǩ������
			int size = insertCheckindate(checkDateFrom, checkDateTo);
			returnmsg.append("���ڼ䵼��ǩ��������Ϊ��" + size + "��\n");
			//����ǩ����������Աpkֵ
			updatePsnAndJob(checkDateFrom, checkDateTo);
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}
		returnmsg.append("===============���뿼�ڼ�ǩ�����ݡ���ɡ�===============\n\n");
		alert.setReturnObj(returnmsg.toString());
		return alert;
	}

	public BaseDAO getDao() {
		if (this.dao == null) {
			this.dao = new BaseDAO();
		}
		return this.dao;
	}

	/**
	 * @param dao Ҫ���õ� dao
	 */
	public void setDao(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * ɾ��ǩ������
	 * 
	 * @param checkDateFrom yyyy-MM-dd hh:mm:dd
	 * @param checkDateTo yyyy-MM-dd hh:mm:dd
	 * @throws DAOException
	 */
	private void deleteCheckindata(String checkDateFrom, String checkDateTo) throws DAOException {
		StringBuilder sql = new StringBuilder();
		SQLParameter parameter = new SQLParameter();
		sql.append("delete from tbm_checkindata where checkintime between ? and ?");
		parameter.addParam(checkDateFrom);
		parameter.addParam(checkDateTo);
		getDao().executeUpdate(sql.toString(), parameter);
	}

	/**
	 * ����ǩ�����ݣ�ֻͬ�������µ��ˣ�û�в��ŵĲ�ͬ����
	 * 
	 * @param checkDateFrom yyyy-MM-dd hh:mm:dd
	 * @param checkDateTo yyyy-MM-dd hh:mm:dd
	 * @return
	 * @throws ServiceNotExistException
	 * @throws SdkInitException
	 * @throws ServiceException
	 * @throws OApiException
	 * @throws DAOException
	 */
	private Integer insertCheckindate(String checkDateFrom, String checkDateTo) throws ServiceNotExistException, SdkInitException,
			ServiceException, OApiException, DAOException {
		List<Department> departments = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");//�������ȡ�ø����������е��Ӳ��ţ������¼����ţ�
		StringBuilder sql = new StringBuilder();
		SQLParameter parameter = new SQLParameter();
		int size = 0;
		for (int i = 0; i < departments.size(); i++) {
			if (departments.get(i).getParentid() != 1L) {
				//ֻҪһ�����ţ���Ҫ�¼����ţ��������ݻ��ظ�
				continue;
			}
			//��ȡ�����ݰ����¼����ŵ���ǩ������
			JSONArray checkinData =
					AttendanceHelper.listCheckinRecord(AuthHelper.getAccessToken(), departments.get(i).getId().toString(), new UFDateTime(checkDateFrom).getMillis(), new UFDateTime(checkDateTo).getMillis(), null, null, null);
			for (int j = 0; j < checkinData.size(); j++) {
				size += checkinData.size();
				sql.delete(0, sql.length());
				parameter.clearParams();
				sql.append("insert into tbm_checkindata (name,userid,latitude,longitude,checkintime,place,detailPlace,remark,imageList) VALUES ");
				sql.append("(?,?,?,?,?,?,?,?,?)");
				JSONObject data = (JSONObject) checkinData.get(j);
				parameter.addParam(data.get("name"));//	��Ա����
				parameter.addParam(data.get("userId"));//Ա��Ψһ��ʶID
				parameter.addParam((BigDecimal) data.get("latitude"));//	γ��
				parameter.addParam((BigDecimal) data.get("longitude"));//����
				parameter.addParam(new UFDateTime((Long) data.get("timestamp")).toStdString());//ǩ��ʱ��
				parameter.addParam(data.get("place"));//ǩ����ַ
				parameter.addParam(data.get("detailPlace"));//	ǩ����ϸ��ַ
				parameter.addParam(data.get("remark"));//	ǩ����ע
				parameter.addParam(data.get("imageList").toString());//	ǩ����Ƭurl�б�
				//				sql.deleteCharAt(sql.length() - 1);//ɾ�����һ�������š�
				getDao().executeUpdate(sql.toString(), parameter);
			}
		}
		return size;

	}

	/**
	 * ����ǩ�����ݵ���Ա���� tbm_checkindata���pk_psndoc,pk_psnjob
	 * 
	 * @param checkDateFrom
	 * @param checkDateTo
	 * @throws DAOException
	 */
	private void updatePsnAndJob(String checkDateFrom, String checkDateTo) throws DAOException {
		StringBuilder sql = new StringBuilder();
		SQLParameter parameter = new SQLParameter();
		sql.append("update tbm_checkindata set (pk_psndoc,pk_psnjob)=(select pk_psndoc,pk_psnjob from tbm_psndoc where tbm_psndoc.timecardid=tbm_checkindata.userid  and tbm_psndoc.enddate > to_char(sysdate,'yyyy-mm-dd') and nvl(tbm_psndoc.dr,0)=0) where checkintime between ? and ?");
		parameter.addParam(checkDateFrom);
		parameter.addParam(checkDateTo);
		getDao().executeUpdate(sql.toString(), parameter);
	}
}
