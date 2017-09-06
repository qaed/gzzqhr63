package nc.bs.extsys.plugin.dingtalk.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.extsys.plugin.dingtalk.Env;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.ta.IAwayRegisterInfoDisplayer;
import nc.itf.ta.IAwayRegisterManageMaintain;
import nc.itf.ta.IAwayRegisterQueryMaintain;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.away.AwayRegVO;
import uap.xbrl.adapter.log.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.SmartworkBpmsProcessinstanceListRequest;
import com.dingtalk.api.response.SmartworkBpmsProcessinstanceListResponse;
import com.dingtalk.api.response.SmartworkBpmsProcessinstanceListResponse.FormComponentValueVo;
import com.dingtalk.api.response.SmartworkBpmsProcessinstanceListResponse.ProcessInstanceTopVo;

public class SyncWorkFlow implements IBackgroundWorkPlugin {
	private List<AwayRegVO> insertvos = new ArrayList<AwayRegVO>();
	IAwayRegisterManageMaintain maintain = NCLocator.getInstance().lookup(IAwayRegisterManageMaintain.class);
	//	IAwayRegisterQueryMaintain querymaintain = NCLocator.getInstance().lookup(IAwayRegisterQueryMaintain.class);
	IAwayRegisterInfoDisplayer appAutoDisplayer = NCLocator.getInstance().lookup(IAwayRegisterInfoDisplayer.class);

	private BaseDAO dao = null;

	public SyncWorkFlow() {
	}

	public SyncWorkFlow(BaseDAO dao) {
		this.dao = dao;
	}

	@Override
	public PreAlertObject executeTask(BgWorkingContext arg0) throws BusinessException {
		PreAlertObject alert = new PreAlertObject();
		alert.setReturnType(PreAlertReturnType.RETURNMESSAGE);
		alert.setMsgTitle("出差、外出同步任务执行情况");
		StringBuilder returnmsg = new StringBuilder();
		returnmsg.append("--------开始同步出差、外出数据----------------\n");
		doSyncBusinessTrip(null);
		doSyncStepOut(null);
		maintain.insertData(insertvos.toArray(new AwayRegVO[0]), true);
		returnmsg.append("--------------出差、外出数据同步完成--------------\n");
		alert.setReturnObj(returnmsg.toString());
		return alert;
	}

	private void doSyncBusinessTrip(Long cursor) throws BusinessException {
		DingTalkClient client = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
		SmartworkBpmsProcessinstanceListRequest req = new SmartworkBpmsProcessinstanceListRequest();
		req.setProcessCode(Env.BUSINESS_TRIP_PROCESS_CODE);
		Calendar calendar = Calendar.getInstance();
		//		calendar.add(Calendar.MINUTE, -5);
		//		req.setEndTime(calendar.getTimeInMillis());
		calendar.add(Calendar.DATE, -40);
		req.setStartTime(calendar.getTimeInMillis());
		req.setSize(10L);
		req.setCursor(cursor == null ? 0L : cursor);//初次调用不用传值
		SmartworkBpmsProcessinstanceListResponse rsp;
		try {
			rsp = client.execute(req, AuthHelper.getAccessToken());
			List<ProcessInstanceTopVo> list = rsp.getResult().getResult().getList();
			StringBuilder sql = new StringBuilder();
			//多条出差审批单
			for (int i = 0; list != null && i < list.size(); i++) {
				ProcessInstanceTopVo dingtalkvo = list.get(i);
				String result = dingtalkvo.getProcessInstanceResult();//审批结果，分为agree和refuse
				if (!"agree".equals(result)) {
					continue;
				}
				//				querymaintain.queryByCond(paramLoginContext, paramFromWhereSQL, paramObject)
				sql.delete(0, sql.length());
				sql.append("select tbm_psndoc.pk_psndoc,tbm_psndoc.pk_org,tbm_psndoc.pk_group,tbm_psndoc.pk_psnorg,tbm_psndoc.pk_psnjob,tbm_timeitemcopy.pk_timeitem,tbm_timeitemcopy.pk_timeitemcopy from tbm_psndoc left join tbm_timeitemcopy on tbm_timeitemcopy.pk_org=tbm_psndoc.pk_org where tbm_psndoc.timecardid='" + dingtalkvo.getOriginatorUserid() + "' and tbm_timeitemcopy.pk_timeitem='1002Z710000000021ZLZ'");
				Map<String, String> psndetail = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (psndetail == null) {
					continue;
				}
				List<FormComponentValueVo> details = dingtalkvo.getFormComponentValues();//1条出差单中的数据:分别为行程明细、出差事由、图片
				/* 
				 *	index	value
				 *	0		行程明细
				 *	1		出差事由
				 *	2		图片
				 */
				String reason = details.get(1).getValue();
				String pictures = details.get(2).getValue();
				//这条审批单的行程明细(多条出差明细--包含多个出差地点、详细内容)
				JSONArray jsonobject = JSON.parseArray((details.get(0).getValue()));
				for (int k = 0; k < jsonobject.size(); k++) {
					AwayRegVO vo = new AwayRegVO();
					vo.setBillsource(2);//单据来源，2=登记表
					vo.setIsawayoff(UFBoolean.FALSE);//是否已销差 
					vo.setPk_awaytype(psndetail.get("pk_timeitem"));//出差
					vo.setPk_awaytypecopy(psndetail.get("pk_timeitemcopy"));//出差类型copy
					vo.setAwayremark(reason);//原因
					vo.setPk_psndoc(psndetail.get("pk_psndoc"));//人员
					vo.setPk_psnjob(psndetail.get("pk_psnjob"));//工作
					vo.setPk_psnorg(psndetail.get("pk_psnorg"));//组织关系
					vo.setCreator(psndetail.get("pk_psndoc"));//创建者
					vo.setPk_group(psndetail.get("pk_group"));
					vo.setPk_org(psndetail.get("pk_org"));
					vo.setCreationtime(new UFDateTime(dingtalkvo.getCreateTime()));//创建时间
					JSONArray innerDetail = jsonobject.getJSONArray(k);//一共2条，分别为：1个出差地点和相关详细内容extendValue
					for (int l = 0; l < innerDetail.size(); l++) {
						JSONObject value = innerDetail.getJSONObject(l);
						if ("出差地点".equals(value.get("key"))) {
							vo.setAwayaddress(value.get("value").toString());
						} else {//相关详细内容extendValue
							JSONArray detailList = JSON.parseObject(value.get("extendValue").toString()).getJSONArray("detailList");
							/* 
							 * detailList
							 *	index	value
							 *	0		开始时间信息
							 *	1		结束时间信息
							 */
							Date begindate = detailList.getJSONObject(0).getJSONObject("approveInfo").getDate("fromTime");
							vo.setAwaybegindate(new UFLiteralDate(begindate));
							vo.setAwaybegintime(new UFDateTime(begindate));
							Date enddate = detailList.getJSONObject(0).getJSONObject("approveInfo").getDate("toTime");
							vo.setAwayenddate(new UFLiteralDate(enddate));
							vo.setAwayendtime(new UFDateTime(enddate));
						}
					}
					sql.delete(0, sql.length());
					int count =
							(Integer) getDao().executeQuery("select count(*) from tbm_awayreg where pk_psndoc='" + vo.getPk_psndoc() + "' and awaybegintime ='" + vo.getAwaybegintime().toStdString() + "'", new ColumnProcessor());
					if (count < 1) {
						vo = appAutoDisplayer.calculate(vo, TimeZone.getDefault());
						insertvos.add(vo);
					}
				}
			}
			if (rsp.getResult().getResult().getNextCursor() != null) {//还有下一页
				doSyncBusinessTrip(rsp.getResult().getResult().getNextCursor());
			}

		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}

	}

	/**
	 * 同步外出
	 * 
	 * @param cursor
	 * @throws BusinessException
	 */
	private void doSyncStepOut(Long cursor) throws BusinessException {

		DingTalkClient client = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
		SmartworkBpmsProcessinstanceListRequest req = new SmartworkBpmsProcessinstanceListRequest();
		req.setProcessCode(Env.STEP_OUT_PROCESS_CODE);
		Calendar calendar = Calendar.getInstance();
		//		calendar.add(Calendar.MINUTE, -5);
		//		req.setEndTime(calendar.getTimeInMillis());
		calendar.add(Calendar.DATE, -40);
		req.setStartTime(calendar.getTimeInMillis());
		req.setSize(10L);
		req.setCursor(cursor == null ? 0L : cursor);//初次调用不用传值
		SmartworkBpmsProcessinstanceListResponse rsp;
		try {
			rsp = client.execute(req, AuthHelper.getAccessToken());
			List<ProcessInstanceTopVo> list = rsp.getResult().getResult().getList();
			StringBuilder sql = new StringBuilder();
			for (int i = 0; list != null && i < list.size(); i++) {//多条出差审批单
				ProcessInstanceTopVo dingtalkvo = list.get(i);
				String result = dingtalkvo.getProcessInstanceResult();//审批结果，分为agree和refuse
				if (!"agree".equals(result)) {
					continue;
				}
				sql.delete(0, sql.length());
				sql.append("select tbm_psndoc.pk_psndoc,tbm_psndoc.pk_org,tbm_psndoc.pk_group,tbm_psndoc.pk_psnorg,tbm_psndoc.pk_psnjob,tbm_timeitemcopy.pk_timeitem,tbm_timeitemcopy.pk_timeitemcopy from tbm_psndoc left join tbm_timeitemcopy on tbm_timeitemcopy.pk_org=tbm_psndoc.pk_org where tbm_psndoc.timecardid='" + dingtalkvo.getOriginatorUserid() + "' and tbm_timeitemcopy.pk_timeitem='1001Z71000000002Q5YD'");
				Map<String, String> psndetail = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (psndetail == null) {
					continue;
				}
				List<FormComponentValueVo> details = dingtalkvo.getFormComponentValues();//1条出差单中的数据:分别为行程明细、出差事由、图片
				/* 
				 *	index	value
				 *	0		开始时间,结束时间
				 *	1		外出事由
				 *	2		图片
				 */
				String reason = details.get(1).getValue();
				//				String pictures = details.get(2).getValue();
				AwayRegVO vo = new AwayRegVO();
				JSONArray jsonobject = JSON.parseArray((details.get(0).getValue()));//这条审批单的开始时间,结束时间
				vo.setAwaybegindate(new UFLiteralDate(jsonobject.getString(0)));//开始日期
				vo.setAwaybegintime(new UFDateTime(jsonobject.getString(0) + ":00"));//开始时间
				vo.setAwayenddate(new UFLiteralDate(jsonobject.getString(1)));//结束日期
				vo.setAwayendtime(new UFDateTime(jsonobject.getString(1) + ":00"));//结束时间
				vo.setBillsource(2);//单据来源，2=登记表
				vo.setIsawayoff(UFBoolean.FALSE);//是否已销差 
				vo.setPk_awaytype(psndetail.get("pk_timeitem"));//出差
				vo.setPk_awaytypecopy(psndetail.get("pk_timeitemcopy"));//出差类型copy
				vo.setAwayremark(reason);//原因
				vo.setPk_psndoc(psndetail.get("pk_psndoc"));//人员
				vo.setPk_psnjob(psndetail.get("pk_psnjob"));//工作
				vo.setPk_psnorg(psndetail.get("pk_psnorg"));//组织关系
				vo.setCreator(psndetail.get("pk_psndoc"));//创建者
				vo.setPk_group(psndetail.get("pk_group"));
				vo.setPk_org(psndetail.get("pk_org"));
				vo.setCreationtime(new UFDateTime(dingtalkvo.getCreateTime()));//创建时间

				sql.delete(0, sql.length());
				int count =
						(Integer) getDao().executeQuery("select count(*) from tbm_awayreg where pk_psndoc='" + vo.getPk_psndoc() + "' and awaybegintime ='" + vo.getAwaybegintime().toStdString() + "'", new ColumnProcessor());
				if (count < 1) {
					vo = appAutoDisplayer.calculate(vo, TimeZone.getDefault());
					insertvos.add(vo);
				}
			}
			if (rsp.getResult().getResult().getNextCursor() != null) {//还有下一页
				doSyncStepOut(rsp.getResult().getResult().getNextCursor());
			}

		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}

	}

	/**
	 * @return dao
	 */
	private BaseDAO getDao() {
		if (this.dao == null) {
			this.dao = new BaseDAO();
		}
		return this.dao;
	}

	/**
	 * @param dao 要设置的 dao
	 */
	private void setDao(BaseDAO dao) {
		this.dao = dao;
	}

}
