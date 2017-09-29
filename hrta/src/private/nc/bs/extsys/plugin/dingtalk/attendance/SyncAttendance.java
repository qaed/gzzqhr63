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
		alert.setMsgTitle("同步钉钉考勤数据\n");
		StringBuilder returnmsg = new StringBuilder();
		returnmsg.append("===============开始同步钉钉考勤数据===============\n");
		StringBuilder sql = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		Map<String, Object> map = arg0.getKeyMap();
		String checkDateFrom = (String) map.get("checkDateFrom");
		String checkDateTo = (String) map.get("checkDateTo");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//		SimpleDateFormat sdfwithtime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if (checkDateFrom == null || "".equals(checkDateFrom.trim())) {
			returnmsg.append("未发现自定义checkDateFrom(开始时期)，默认「开始时期」为 " + sdf.format(calendar.getTime()) + "\n");
			checkDateFrom = sdf.format(calendar.getTime()) + " 00:00:00";
		} else {
			returnmsg.append("发现自定义checkDateFrom(开始时期):" + checkDateFrom + "\n");
			checkDateFrom += " 00:00:00";
		}
		if (checkDateTo == null || "".equals(checkDateTo.trim())) {
			returnmsg.append("未发现自定义checkDateTo(结束时期)，默认「结束时期」为 " + sdf.format(calendar.getTime()) + "\n");
			checkDateTo = sdf.format(calendar.getTime()) + " 23:59:59";
		} else {
			returnmsg.append("发现自定义checkDateTo(结束时期):" + checkDateTo + "\n");
			checkDateTo += " 23:59:59";
		}
		returnmsg.append("本次导入考勤及签到数据日期为：").append(checkDateFrom).append(" --- ").append(checkDateTo).append("\n");

		try {

			//-------------------------同步人员考勤打卡数据
			// 考勤档案下的人员
			sql.append("select timecardid from tbm_psndoc where nvl(timecardid,0)<>0 and nvl(dr,0)=0");
			List<String> userIds = (List<String>) getDao().executeQuery(sql.toString(), new ColumnListProcessor());
			List<ImportDataVO> importdatas = new ArrayList<ImportDataVO>();

			// 每次最多获取50个人的考勤数据
			returnmsg.append("本次需要导入考勤人员(考勤档案人员)数为：" + userIds.size() + "\n");
			for (int i = 0; i < userIds.size(); i = i + 50) {
				List<String> subUserIds = userIds.subList(i, i + 50 > userIds.size() ? userIds.size() : i + 50);
				JSONArray attendanceRecord =
						AttendanceHelper.listRecord(AuthHelper.getAccessToken(), subUserIds, checkDateFrom, checkDateTo);
				// 构建准备插入数据库的VO
				for (int j = 0; j < attendanceRecord.size(); j++) {
					Map recordMap = (Map) attendanceRecord.get(j);
					Date workDate = new Date((Long) recordMap.get("userCheckTime"));
					int datastatus = "OnDuty".equals(recordMap.get("checkType")) ? 0 : 1; //进出标识（签到、签退标志）   0=进，1=出，2=非门禁数据，
					sql.delete(0, sql.length());
					sql.append("select * from tbm_psndoc where timecardid='" + (String) recordMap.get("userId") + "'");
					Map tbmPsndoc = (Map) getDao().executeQuery(sql.toString(), new MapProcessor());
					ImportDataVO importDataVO = new ImportDataVO();
					importDataVO.setCalendardate(new UFLiteralDate(workDate));//刷卡日期
					importDataVO.setCalendartime(new UFDateTime(workDate));//刷卡时间 
					importDataVO.setCreator((String) tbmPsndoc.get("pk_psndoc"));
					importDataVO.setCreationtime(new UFDateTime(workDate));//创建时间 
					importDataVO.setDatastatus(datastatus);
					importDataVO.setDatatype(2);//刷卡类型 
					importDataVO.setPk_group((String) tbmPsndoc.get("pk_group"));
					importDataVO.setPk_org((String) tbmPsndoc.get("pk_org"));
					importDataVO.setPk_psndoc((String) tbmPsndoc.get("pk_psndoc"));
					importDataVO.setTimecardid((String) recordMap.get("userId"));//考勤卡号 
					importdatas.add(importDataVO);
				}
			}
			returnmsg.append("共需要导入考勤数据数为：" + importdatas.size() + "条\n");
			if (importdatas.size() > 0) {
				//IImportDataManageMaintain matain = NCLocator.getInstance().lookup(IImportDataManageMaintain.class);
				//由于Maintain东西太杂了，直接使用DAO进行操作
				ImportDataDAO dmo = new ImportDataDAO();
				//先判断是否是重复导入，如果为重复导入，则删除掉以前的数据
				dmo.deleteArrayForImport(importdatas.get(0).getPk_org(), importdatas.toArray(new ImportDataVO[0]), ICalendar.BASE_TIMEZONE);
				//执行导入操作(19位时间在数据库中都以标准时区进行保存)
				String[] pk_importdatas = dmo.execInsertData(importdatas.toArray(new ImportDataVO[0]), ICalendar.BASE_TIMEZONE);
				// 导入后事件
				EventDispatcher.fireEvent(new BusinessEvent("556ce8f3-ab1e-4820-8e1e-b34e500cfd22", "1002", importdatas));
				//如果有匹配的工作日历记录，则要将此人此天的工作日历的dataimportstatus字段设置为Y
				dmo.updatePsncalendar(importdatas.get(0).getPk_org(), pk_importdatas);
				//业务日志
				TaBusilogUtil.writeImportDataBusiLog(importdatas.toArray(new ImportDataVO[0]));
			}
			//-------------------------同步人员签卡数据
			returnmsg.append("===============开始同步钉钉签到数据===============\n");
			//删除重复签到数据
			deleteCheckindata(checkDateFrom, checkDateTo);
			//插入签到数据
			int size = insertCheckindate(checkDateFrom, checkDateTo);
			returnmsg.append("本期间导入签到数据数为：" + size + "条\n");
			//更新签到数据中人员pk值
			updatePsnAndJob(checkDateFrom, checkDateTo);
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}
		returnmsg.append("===============导入考勤及签到数据「完成」===============\n\n");
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
	 * @param dao 要设置的 dao
	 */
	public void setDao(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 删除签到数据
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
	 * 导入签到数据（只同步部门下的人，没有部门的不同步）
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
		List<Department> departments = DepartmentHelper.listDepartments(AuthHelper.getAccessToken(), "1");//钉钉会获取该父部门下所有的子部门（含下下级部门）
		StringBuilder sql = new StringBuilder();
		SQLParameter parameter = new SQLParameter();
		int size = 0;
		for (int i = 0; i < departments.size(); i++) {
			if (departments.get(i).getParentid() != 1L) {
				//只要一级部门，不要下级部门，否则数据会重复
				continue;
			}
			//获取的数据包含下级部门的人签到数据
			JSONArray checkinData =
					AttendanceHelper.listCheckinRecord(AuthHelper.getAccessToken(), departments.get(i).getId().toString(), new UFDateTime(checkDateFrom).getMillis(), new UFDateTime(checkDateTo).getMillis(), null, null, null);
			for (int j = 0; j < checkinData.size(); j++) {
				size += checkinData.size();
				sql.delete(0, sql.length());
				parameter.clearParams();
				sql.append("insert into tbm_checkindata (name,userid,latitude,longitude,checkintime,place,detailPlace,remark,imageList) VALUES ");
				sql.append("(?,?,?,?,?,?,?,?,?)");
				JSONObject data = (JSONObject) checkinData.get(j);
				parameter.addParam(data.get("name"));//	成员名称
				parameter.addParam(data.get("userId"));//员工唯一标识ID
				parameter.addParam((BigDecimal) data.get("latitude"));//	纬度
				parameter.addParam((BigDecimal) data.get("longitude"));//经度
				parameter.addParam(new UFDateTime((Long) data.get("timestamp")).toStdString());//签到时间
				parameter.addParam(data.get("place"));//签到地址
				parameter.addParam(data.get("detailPlace"));//	签到详细地址
				parameter.addParam(data.get("remark"));//	签到备注
				parameter.addParam(data.get("imageList").toString());//	签到照片url列表
				//				sql.deleteCharAt(sql.length() - 1);//删除最后一个“逗号”
				getDao().executeUpdate(sql.toString(), parameter);
			}
		}
		return size;

	}

	/**
	 * 更新签到数据的人员主键 tbm_checkindata表的pk_psndoc,pk_psnjob
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
