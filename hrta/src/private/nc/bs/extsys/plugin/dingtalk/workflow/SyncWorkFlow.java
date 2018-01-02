package nc.bs.extsys.plugin.dingtalk.workflow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.extsys.plugin.dingtalk.Env;
import nc.bs.extsys.plugin.dingtalk.auth.AuthHelper;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pa.PreAlertObject;
import nc.bs.pub.pa.PreAlertReturnType;
import nc.bs.pub.taskcenter.BgWorkingContext;
import nc.bs.pub.taskcenter.IBackgroundWorkPlugin;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.ta.IAwayRegisterInfoDisplayer;
import nc.itf.ta.IAwayRegisterManageMaintain;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.away.AwayRegVO;

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
		returnmsg.append("===============��ʼͬ������������===============\n");
		Properties pro = new Properties();
		InputStream in;
		String longleave = "";
		String shortleave = "";
		try {
			in = SyncWorkFlow.class.getClassLoader().getResourceAsStream("dingtalk.properties");
			pro.load(in);
			longleave = pro.getProperty("longleave");
			shortleave = pro.getProperty("shortleave");
			in.close();
		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}
		//��̨��������б�
		Map<String, Object> map = arg0.getKeyMap();
		Integer dayBefore = 1;
		String dayBeforestr = (String) map.get("dayBefore");
		if (dayBeforestr != null && !"".equals(dayBeforestr)) {
			dayBefore = Integer.parseInt(dayBeforestr);
		}
		if (dayBeforestr == null) {
			returnmsg.append("δ�����Զ���dayBefore(ͬ������ʱ�䷶Χ)��Ĭ�ϡ�ʱ�䷶Χ��Ϊ " + 1 + "\n");
		} else {
			returnmsg.append("�����Զ���dayBefore(ͬ������ʱ�䷶Χ)Ϊ: " + dayBefore + "\n");
		}
		returnmsg.append("���ε��� " + new UFLiteralDate().getDateBefore(dayBefore).toStdString() + " 23:30:00" + "--���� ����������ͨ��������\n");
		for (String processCode : Env.BUSINESS_TRIP_PROCESS_CODE) {
			doSyncBusinessTrip(processCode, null, dayBefore, longleave);
		}
		for (String processCode : Env.STEP_OUT_PROCESS_CODE) {
			doSyncStepOut(processCode, null, dayBefore, shortleave);
		}
		//����ʱ��Ϊ0�ĵ���
		IPsndocQueryService psnQueryService = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		Iterator<AwayRegVO> iterator = insertvos.iterator();
		while (iterator.hasNext()) {
			AwayRegVO vo = iterator.next();
			if (vo.getAwayhour().compareTo(UFDouble.ZERO_DBL) == 0) {
				PsndocVO[] psndocVO = psnQueryService.queryPsndocByPks(new String[] { vo.getPk_psndoc() });
				if (psndocVO.length > 0) {
					returnmsg.append(psndocVO[0].getName() + "�ĵ��ݣ�");
				}
				returnmsg.append(vo.getAwaybegintime().toStdString() + " -- " + vo.getAwayendtime().toStdString());
				returnmsg.append("__��Чʱ��Ϊ0���޷����롣\n");
				iterator.remove();
			}
		}
		int sum = 0;
		//���뵽���ݱ���
		try {
			for (AwayRegVO awayRegVO : insertvos) {
				maintain.insertData(awayRegVO);
				sum++;
			}
		} catch (Exception e) {
			Logger.error("������������ݳ���" + e);
		}
		returnmsg.append("===============����������ͬ�����===============\n");
		returnmsg.append("���ι��������ݣ�" + sum + "��\n");
		alert.setReturnObj(returnmsg.toString());
		return alert;
	}

	/**
	 * ͬ������--��Ӧ��س���
	 * 
	 * @param cursor
	 * @throws BusinessException
	 */
	private void doSyncBusinessTrip(String businessTripProcessCode, Long cursor, Integer dayBefore, String pk_timeitem)
			throws BusinessException {
		DingTalkClient client = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
		SmartworkBpmsProcessinstanceListRequest req = new SmartworkBpmsProcessinstanceListRequest();
		req.setProcessCode(businessTripProcessCode);
		Calendar calendar = Calendar.getInstance();
		UFDateTime minEndTime = new UFDateTime(new UFLiteralDate().getDateBefore(dayBefore).toStdString() + " 23:30:00");
		//		calendar.add(Calendar.MINUTE, -5);
		//		req.setEndTime(calendar.getTimeInMillis());
		calendar.add(Calendar.DATE, -80);
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
				//ֻ����ǰһ��23:00�����������
				if (new UFDateTime(dingtalkvo.getFinishTime()).before(minEndTime)) {
					continue;
				}
				//				querymaintain.queryByCond(paramLoginContext, paramFromWhereSQL, paramObject)
				sql.delete(0, sql.length());

				sql.append("select tbm_psndoc.pk_psndoc,tbm_psndoc.pk_org,tbm_psndoc.pk_group,tbm_psndoc.pk_psnorg,tbm_psndoc.pk_psnjob,tbm_timeitemcopy.pk_timeitem,tbm_timeitemcopy.pk_timeitemcopy from tbm_psndoc left join tbm_timeitemcopy on tbm_timeitemcopy.pk_org=tbm_psndoc.pk_org where tbm_psndoc.timecardid='" + dingtalkvo.getOriginatorUserid() + "' and tbm_timeitemcopy.pk_timeitem='" + pk_timeitem + "'");
				Map<String, String> psndetail = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (psndetail == null) {
					continue;
				}
				List<FormComponentValueVo> details = dingtalkvo.getFormComponentValues();
				String reason = null;//��������
				String pictures = null;//ͼƬ
				for (FormComponentValueVo componentValueVo : details) {//�ȴ�����ɺ�ͼƬ
					String name = componentValueVo.getName();
					if (name.contains("��������")) {
						reason = componentValueVo.getValue();
					} else if (name.contains("ͼƬ")) {
						pictures = componentValueVo.getValue();
					}
				}
				for (FormComponentValueVo componentValueVo : details) {
					String name = componentValueVo.getName();
					if (name.contains("�г���ϸ")) {
						JSONArray jsonobject = JSON.parseArray((componentValueVo.getValue()));
						for (int k = 0; k < jsonobject.size(); k++) {//ÿһ��������ϸ������һ������
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
							JSONArray innerDetail = jsonobject.getJSONArray(k);//innerDetail��һ��2�����ֱ�Ϊ��1������ص�������ϸ����extendValue
							for (int l = 0; l < innerDetail.size(); l++) {
								JSONObject value = innerDetail.getJSONObject(l);
								if ("����ص�".equals(value.get("key"))) {
									vo.setAwayaddress(value.get("value").toString());
								} else if (StringUtils.isNotBlank((String)value.get("extendValue"))) {//�����ϸ����extendValue
									JSONArray detailList = JSON.parseObject(value.get("extendValue").toString()).getJSONArray("detailList");
									/* 
									 * detailList�������������ֳɶ�����¼��ȡ��һ��Ŀ�ʼʱ�䣬�����һ���Ľ���ʱ�䣩
									 *	index	value
									 *	0		��ʼʱ����Ϣ
									 *	size-1	����ʱ����Ϣ
									 */
									Date begindate = detailList.getJSONObject(0).getJSONObject("approveInfo").getDate("fromTime");
									vo.setAwaybegindate(new UFLiteralDate(begindate));
									vo.setAwaybegintime(new UFDateTime(begindate));
									Date enddate =
											detailList.getJSONObject(detailList.size() - 1).getJSONObject("approveInfo").getDate("toTime");
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
				}
				//				������������
				//1������е�����:�ֱ�Ϊ�г���ϸ���������ɡ�ͼƬ
				/* 
				 *	index	value
				 *	0		�г���ϸ
				 *	1		��������
				 *	2		ͼƬ
				 */
				//�������������г���ϸ(����������ϸ--�����������ص㡢��ϸ����)

			}
			if (rsp.getResult().getResult().getNextCursor() != null) {//������һҳ
				doSyncBusinessTrip(businessTripProcessCode, rsp.getResult().getResult().getNextCursor(), dayBefore, pk_timeitem);
			}

		} catch (Exception e) {
			Logger.error(e);
			throw new BusinessException(e);
		}

	}

	/**
	 * ͬ�����--��Ӧ���س���
	 * 
	 * @param cursor
	 * @throws BusinessException
	 */
	private void doSyncStepOut(String stepOutProcessCode, Long cursor, Integer dayBefore, String pk_timeitem) throws BusinessException {

		DingTalkClient client = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
		SmartworkBpmsProcessinstanceListRequest req = new SmartworkBpmsProcessinstanceListRequest();
		req.setProcessCode(stepOutProcessCode);
		Calendar calendar = Calendar.getInstance();
		UFDateTime minEndTime = new UFDateTime(new UFLiteralDate().getDateBefore(dayBefore).toStdString() + " 23:30:00");
		//		calendar.add(Calendar.MINUTE, -5);
		//		req.setEndTime(calendar.getTimeInMillis());
		calendar.add(Calendar.DATE, -80);
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
				//ֻ����ǰһ��23:00�����������
				if (new UFDateTime(dingtalkvo.getFinishTime()).before(minEndTime)) {
					continue;
				}
				sql.delete(0, sql.length());
				sql.append("select tbm_psndoc.pk_psndoc,tbm_psndoc.pk_org,tbm_psndoc.pk_group,tbm_psndoc.pk_psnorg,tbm_psndoc.pk_psnjob,tbm_timeitemcopy.pk_timeitem,tbm_timeitemcopy.pk_timeitemcopy from tbm_psndoc left join tbm_timeitemcopy on tbm_timeitemcopy.pk_org=tbm_psndoc.pk_org where tbm_psndoc.timecardid='" + dingtalkvo.getOriginatorUserid() + "' and tbm_timeitemcopy.pk_timeitem='" + pk_timeitem + "'");
				Map<String, String> psndetail = (Map<String, String>) getDao().executeQuery(sql.toString(), new MapProcessor());
				if (psndetail == null) {
					continue;
				}
				List<FormComponentValueVo> details = dingtalkvo.getFormComponentValues();//�������ı�����
				AwayRegVO vo = new AwayRegVO();
				for (FormComponentValueVo componentValueVo : details) {//�����������ı�����
					String name = componentValueVo.getName();
					if (name.contains("\"��ʼʱ��\",\"����ʱ��\"")) {
						JSONArray time = JSON.parseArray((componentValueVo.getValue()));//�����������Ŀ�ʼʱ��,����ʱ��
						vo.setAwaybegindate(new UFLiteralDate(time.getString(0)));//��ʼ����
						vo.setAwaybegintime(new UFDateTime(time.getString(0) + ":00"));//��ʼʱ��
						vo.setAwayenddate(new UFLiteralDate(time.getString(1)));//��������
						vo.setAwayendtime(new UFDateTime(time.getString(1) + ":00"));//����ʱ��
					} else if (name.contains("�������")) {
						String reason = componentValueVo.getValue();
						vo.setAwayremark(reason);//ԭ��
					} else if (name.contains("ͼƬ")) {

					}
				}
				vo.setBillsource(2);//������Դ��2=�ǼǱ�
				vo.setIsawayoff(UFBoolean.FALSE);//�Ƿ������� 
				vo.setPk_awaytype(psndetail.get("pk_timeitem"));//����
				vo.setPk_awaytypecopy(psndetail.get("pk_timeitemcopy"));//��������copy
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
				doSyncStepOut(stepOutProcessCode, rsp.getResult().getResult().getNextCursor(), dayBefore, pk_timeitem);
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
