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
		alert.setMsgTitle("������ͬ������ִ�����");
		StringBuilder returnmsg = new StringBuilder();
		returnmsg.append("--------��ʼͬ������������----------------\n");
		doSyncBusinessTrip(null);
		doSyncStepOut(null);
		maintain.insertData(insertvos.toArray(new AwayRegVO[0]), true);
		returnmsg.append("--------------����������ͬ�����--------------\n");
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
		req.setCursor(cursor == null ? 0L : cursor);//���ε��ò��ô�ֵ
		SmartworkBpmsProcessinstanceListResponse rsp;
		try {
			rsp = client.execute(req, AuthHelper.getAccessToken());
			List<ProcessInstanceTopVo> list = rsp.getResult().getResult().getList();
			StringBuilder sql = new StringBuilder();
			//��������������
			for (int i = 0; list != null && i < list.size(); i++) {
				ProcessInstanceTopVo dingtalkvo = list.get(i);
				String result = dingtalkvo.getProcessInstanceResult();//�����������Ϊagree��refuse
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
				List<FormComponentValueVo> details = dingtalkvo.getFormComponentValues();//1������е�����:�ֱ�Ϊ�г���ϸ���������ɡ�ͼƬ
				/* 
				 *	index	value
				 *	0		�г���ϸ
				 *	1		��������
				 *	2		ͼƬ
				 */
				String reason = details.get(1).getValue();
				String pictures = details.get(2).getValue();
				//�������������г���ϸ(����������ϸ--�����������ص㡢��ϸ����)
				JSONArray jsonobject = JSON.parseArray((details.get(0).getValue()));
				for (int k = 0; k < jsonobject.size(); k++) {
					AwayRegVO vo = new AwayRegVO();
					vo.setBillsource(2);//������Դ��2=�ǼǱ�
					vo.setIsawayoff(UFBoolean.FALSE);//�Ƿ������� 
					vo.setPk_awaytype(psndetail.get("pk_timeitem"));//����
					vo.setPk_awaytypecopy(psndetail.get("pk_timeitemcopy"));//��������copy
					vo.setAwayremark(reason);//ԭ��
					vo.setPk_psndoc(psndetail.get("pk_psndoc"));//��Ա
					vo.setPk_psnjob(psndetail.get("pk_psnjob"));//����
					vo.setPk_psnorg(psndetail.get("pk_psnorg"));//��֯��ϵ
					vo.setCreator(psndetail.get("pk_psndoc"));//������
					vo.setPk_group(psndetail.get("pk_group"));
					vo.setPk_org(psndetail.get("pk_org"));
					vo.setCreationtime(new UFDateTime(dingtalkvo.getCreateTime()));//����ʱ��
					JSONArray innerDetail = jsonobject.getJSONArray(k);//һ��2�����ֱ�Ϊ��1������ص�������ϸ����extendValue
					for (int l = 0; l < innerDetail.size(); l++) {
						JSONObject value = innerDetail.getJSONObject(l);
						if ("����ص�".equals(value.get("key"))) {
							vo.setAwayaddress(value.get("value").toString());
						} else {//�����ϸ����extendValue
							JSONArray detailList = JSON.parseObject(value.get("extendValue").toString()).getJSONArray("detailList");
							/* 
							 * detailList
							 *	index	value
							 *	0		��ʼʱ����Ϣ
							 *	1		����ʱ����Ϣ
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
			if (rsp.getResult().getResult().getNextCursor() != null) {//������һҳ
				doSyncBusinessTrip(rsp.getResult().getResult().getNextCursor());
			}

		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}

	}

	/**
	 * ͬ�����
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
		req.setCursor(cursor == null ? 0L : cursor);//���ε��ò��ô�ֵ
		SmartworkBpmsProcessinstanceListResponse rsp;
		try {
			rsp = client.execute(req, AuthHelper.getAccessToken());
			List<ProcessInstanceTopVo> list = rsp.getResult().getResult().getList();
			StringBuilder sql = new StringBuilder();
			for (int i = 0; list != null && i < list.size(); i++) {//��������������
				ProcessInstanceTopVo dingtalkvo = list.get(i);
				String result = dingtalkvo.getProcessInstanceResult();//�����������Ϊagree��refuse
				if (!"agree".equals(result)) {
					continue;
				}
				sql.delete(0, sql.length());
				sql.append("select tbm_psndoc.pk_psndoc,tbm_psndoc.pk_org,tbm_psndoc.pk_group,tbm_psndoc.pk_psnorg,tbm_psndoc.pk_psnjob,tbm_timeitemcopy.pk_timeitem,tbm_timeitemcopy.pk_timeitemcopy from tbm_psndoc left join tbm_timeitemcopy on tbm_timeitemcopy.pk_org=tbm_psndoc.pk_org where tbm_psndoc.timecardid='" + dingtalkvo.getOriginatorUserid() + "' and tbm_timeitemcopy.pk_timeitem='1001Z71000000002Q5YD'");
				Map<String, String> psndetail = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (psndetail == null) {
					continue;
				}
				List<FormComponentValueVo> details = dingtalkvo.getFormComponentValues();//1������е�����:�ֱ�Ϊ�г���ϸ���������ɡ�ͼƬ
				/* 
				 *	index	value
				 *	0		��ʼʱ��,����ʱ��
				 *	1		�������
				 *	2		ͼƬ
				 */
				String reason = details.get(1).getValue();
				//				String pictures = details.get(2).getValue();
				AwayRegVO vo = new AwayRegVO();
				JSONArray jsonobject = JSON.parseArray((details.get(0).getValue()));//�����������Ŀ�ʼʱ��,����ʱ��
				vo.setAwaybegindate(new UFLiteralDate(jsonobject.getString(0)));//��ʼ����
				vo.setAwaybegintime(new UFDateTime(jsonobject.getString(0) + ":00"));//��ʼʱ��
				vo.setAwayenddate(new UFLiteralDate(jsonobject.getString(1)));//��������
				vo.setAwayendtime(new UFDateTime(jsonobject.getString(1) + ":00"));//����ʱ��
				vo.setBillsource(2);//������Դ��2=�ǼǱ�
				vo.setIsawayoff(UFBoolean.FALSE);//�Ƿ������� 
				vo.setPk_awaytype(psndetail.get("pk_timeitem"));//����
				vo.setPk_awaytypecopy(psndetail.get("pk_timeitemcopy"));//��������copy
				vo.setAwayremark(reason);//ԭ��
				vo.setPk_psndoc(psndetail.get("pk_psndoc"));//��Ա
				vo.setPk_psnjob(psndetail.get("pk_psnjob"));//����
				vo.setPk_psnorg(psndetail.get("pk_psnorg"));//��֯��ϵ
				vo.setCreator(psndetail.get("pk_psndoc"));//������
				vo.setPk_group(psndetail.get("pk_group"));
				vo.setPk_org(psndetail.get("pk_org"));
				vo.setCreationtime(new UFDateTime(dingtalkvo.getCreateTime()));//����ʱ��

				sql.delete(0, sql.length());
				int count =
						(Integer) getDao().executeQuery("select count(*) from tbm_awayreg where pk_psndoc='" + vo.getPk_psndoc() + "' and awaybegintime ='" + vo.getAwaybegintime().toStdString() + "'", new ColumnProcessor());
				if (count < 1) {
					vo = appAutoDisplayer.calculate(vo, TimeZone.getDefault());
					insertvos.add(vo);
				}
			}
			if (rsp.getResult().getResult().getNextCursor() != null) {//������һҳ
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
	 * @param dao Ҫ���õ� dao
	 */
	private void setDao(BaseDAO dao) {
		this.dao = dao;
	}

}