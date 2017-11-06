package nc.impl.uap.pf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.integration.workitem.util.SyncWorkitemUtil;
import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pf.pub.PFRequestDataCacheProxy;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pf.pub.cache.WFTaskCacheKey;
import nc.bs.pub.mobile.PfEmailSendTask;
import nc.bs.pub.pf.PfMailAndSMSUtil;
import nc.bs.pub.pf.PfMessageUtil;
import nc.bs.pub.pf.busistate.AbstractBusiStateCallback;
import nc.bs.pub.pf.busistate.PFBusiStateOfMeta;
import nc.bs.pub.taskmanager.TaskManagerDMO;
import nc.bs.pub.workflownote.WorknoteManager;
import nc.bs.uap.lock.PKLock;
import nc.bs.uap.oid.OidGenerator;
import nc.bs.uap.pf.overdue.WorkflowOverdueCalculator;
import nc.bs.uap.scheduler.ITaskBody;
import nc.bs.wfengine.engine.EngineService;
import nc.bs.wfengine.engine.ext.IOrgFilter4Responsibility;
import nc.bs.wfengine.engine.ext.TaskTopicResolver;
import nc.bs.wfengine.engine.ext.org.filter.PfOrg4ResponsibilityFactory;
import nc.gl.utils.SqlBuilder;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.pf.IPFConfig;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.IWorkflowAdmin;
import nc.itf.uap.rbac.IUserManageQuery_C;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.pubitf.rbac.IUserPubService;
import nc.ui.ml.NCLangRes;
import nc.ui.pf.multilang.PfMultiLangUtil;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.integration.workitem.WorkitemVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.AbstractNCLangRes;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pf.msg.MessageMetaVO;
import nc.vo.pf.pub.util.ArrayUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.change.PublicHeadVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.msg.AbstractMsgL10NCallback;
import nc.vo.pub.msg.EmailMsg;
import nc.vo.pub.msg.FlowInstanceSettingVO;
import nc.vo.pub.msg.MessageinfoVO;
import nc.vo.pub.pf.Pfi18nTools;
import nc.vo.pub.pf.WfTaskInfo;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pub.workflownote.WorkitemappointVO;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.OrganizeUnitTypes;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.wfengine.core.activity.GenericActivityEx;
import nc.vo.wfengine.core.parser.XPDLParserException;
import nc.vo.wfengine.core.util.DurationUnit;
import nc.vo.wfengine.core.workflow.BasicWorkflowProcess;
import nc.vo.wfengine.core.workflow.MailModal;
import nc.vo.wfengine.core.workflow.MailPrintTemplet;
import nc.vo.wfengine.core.workflow.WorkflowProcess;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.engine.ProcessInstanceAVO;
import nc.vo.wfengine.pub.ProcessInsSupervisorType;
import nc.vo.wfengine.pub.WFTask;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;
import nc.vo.wfengine.pub.WfTaskType;
import nc.vo.wfengine.pub.WorkitemMsgContext;
import nc.vo.workflow.admin.FlowInstanceHistoryVO;
import nc.vo.workflow.admin.FlowInstanceOperation;
import nc.vo.workflow.admin.FlowOverdueVO;
import nc.vo.workflow.admin.FlowTimeSettingVO;
import nc.vo.workflow.admin.WorkflowManageContext;

public class WorkflowAdminImpl implements IWorkflowAdmin {
	public WorkflowAdminImpl() {
	}

	public void appointWorkitem(String billId, String pkMsg, String checkman,
			String userID) throws BusinessException {
		appointWorkitem(billId, pkMsg, checkman, userID, null);
	}

	public void appointWorkitem(String billId, String pkMsg, String checkman,
			String userID, String checkNote) throws BusinessException {
		boolean isNeedUnLock = PKLock.getInstance().addDynamicLock(billId);

		if (!isNeedUnLock) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000602"));
		}

		final WorkflownoteVO noteVO = WorknoteManager.queryWorkitemByPK(pkMsg);

		if ((noteVO == null)
				|| (noteVO.getApprovestatus().intValue() != WfTaskOrInstanceStatus.Started
						.getIntValue())) {

			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "wfAdminImpl-0000"));
		}

		try {
			String[] users = filtrateUsers(noteVO.getPk_wf_task(),
					new String[] { userID });

			if ((users == null) || (users.length == 0)) {
				throw new PFBusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0001"));
			}

		} catch (DbException e) {
			handleException(e, null);
		}

		WorkflownoteVO newNoteVO = (WorkflownoteVO) noteVO.clone();
		newNoteVO.setPrimaryKey(null);

		IUserManageQuery_C userManageQuery_C = (IUserManageQuery_C) NCLocator
				.getInstance().lookup(IUserManageQuery_C.class);

		UserVO senderMan = userManageQuery_C.getUser(newNoteVO.getSenderman());
		UserVO originCheckman = userManageQuery_C.getUser(checkman);
		final UserVO appointedCheckman = userManageQuery_C.getUser(userID);

		noteVO.setApprovestatus(Integer
				.valueOf(WfTaskOrInstanceStatus.Inefficient.getIntValue()));

		noteVO.setApproveresult("T");
		noteVO.setChecknote(checkNote);
		noteVO.setDealdate(new UFDateTime(InvocationInfoProxy.getInstance()
				.getBizDateTime()));

		// 20161228 hezy 转办，设置portal端转办发起人流程状态为【废弃 X】 begin
		noteVO.setIscheck("X");
		// end

		PfMultiLangUtil.doInDefaultLangBs(new Runnable() {

			public void run() {

				noteVO.setMessagenote("{0pfworkflow630098}"
						+ PfMultiLangUtil.getSuperVONameOfCurrentLang(
								appointedCheckman, "user_name") + "]"
						+ noteVO.getMessagenote());

			}

		});
		new BaseDAO().updateVO(noteVO);
		PfMessageUtil.setHandled(noteVO);
		// YiXin add begin delWorkflow
		try {
			Logger.error("YiXin记录日志：开始删除待办：" + noteVO.getPk_checkflow());
			WorkitemVO wt = SyncWorkitemUtil.queryWokritemByPk(noteVO
					.getPk_checkflow());
			Logger.error("YiXin记录日志：除待办：" + wt);
			SyncWorkitemUtil.deleteExternalWorkitem(wt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// YiXin add end delWorkflow

		String originActionType = newNoteVO.getActiontype();

		newNoteVO.setCheckman(checkman);

		Integer status = newNoteVO.getApprovestatus();
		newNoteVO.setApprovestatus(Integer
				.valueOf(WfTaskOrInstanceStatus.Inefficient.getIntValue()));

		newNoteVO.setActiontype("BIZ");

		String msgFormat = newNoteVO.getMessagenote();

		String message = originCheckman.getUser_name() + "{appoint}"
				+ newNoteVO.billno + "{appointto}"
				+ appointedCheckman.getUser_name();

		newNoteVO.setMessagenote(message);
		newNoteVO.setSenddate(new UFDateTime(InvocationInfoProxy.getInstance()
				.getBizDateTime()));
		try {
			WorknoteManager.insertWorknote(newNoteVO);
		} catch (Exception e) {
			handleException(e, null);
		}

		newNoteVO.setCheckman(userID);

		newNoteVO.setSenderman(checkman);
		newNoteVO.setApprovestatus(status);

		NCMessage ncmsg = constructNCMsgOfAppointedNote(newNoteVO,
				senderMan.getCuserid(), userID);

		if (ncmsg != null) {
			MessageVO messageVO = ncmsg.getMessage();
			newNoteVO.setMessagenote(messageVO.getSubject());
			newNoteVO.setNcMsg(ncmsg);
		} else {
			String[] subTopics = msgFormat.split(",");
			String newMsgFormat = Pfi18nTools.getUserName(checkman) + " "
					+ "{appoint}" + "," + subTopics[1] + "," + subTopics[2];

			newNoteVO.setMessagenote(newMsgFormat);
		}

		newNoteVO.setActiontype(originActionType
				+ (originActionType.endsWith("_D") ? "" : "_D"));

		newNoteVO.setSenddate(new UFDateTime(InvocationInfoProxy.getInstance()
				.getBizDateTime()));

		String newPK = null;
		try {
			newPK = WorknoteManager.insertWorknote(newNoteVO);
		} catch (Exception e) {
			handleException(e, null);
		}

		// YiXin add begin sendWorkflow发送新待办
		try {
			Logger.error("YiXin记录日志：开始发送新待办：newPK＝" + newPK);
			WorkitemVO workitem = SyncWorkitemUtil.queryWokritemByPk(newPK);
			Logger.error("YiXin记录日志：开始发送新待办：workitem＝" + workitem);
			SyncWorkitemUtil.beginExternalWorkitem(null,workitem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// YiXin add begin sendWorkflow

		WorkitemappointVO waVO = new WorkitemappointVO();
		waVO.setAppointee(userID);
		waVO.setAppointer(checkman);
		waVO.setPk_workitem(newPK);
		waVO.setOld_pk_workitem(pkMsg);
		new BaseDAO().insertVO(waVO);
	}

	private NCMessage constructNCMsgOfAppointedNote(WorkflownoteVO noteVO,
			String senderId, String appointedCheckerID) {
		String tempcode = "";
		try {
			WFTask task = new TaskManagerDMO().getTaskByPK(noteVO
					.getPk_wf_task());

			task.setBillType(noteVO.getPk_billtype());

			String wfProcessDefPK = task.getWfProcessDefPK();
			WorkflowProcess wf = PfDataCache.getWorkflowProcess(wfProcessDefPK);

			tempcode = TaskTopicResolver.getMsgTempCode(wf, task);

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return null;
		}

		if (!StringUtil.isEmptyWithTrim(tempcode)) {
			try {
				WorkitemMsgContext context = new WorkitemMsgContext();

				context.setActionType("{appoint}");

				context.setBillid(noteVO.getBillid());
				context.setBillno(noteVO.getBillno());
				context.setBillType(noteVO.getPk_billtype());

				Object busiObj = ((IPFConfig) NCLocator.getInstance().lookup(
						IPFConfig.class)).queryBillDataVO(
						noteVO.getPk_billtype(), noteVO.getBillid());

				context.setBusiObj(busiObj);
				context.setCheckman(appointedCheckerID);
				context.setCheckNote(noteVO.getChecknote());

				context.setMsgtempcode(tempcode);
				context.setSender(senderId);
				context.setResult(getCheckResultOfNoteSenderman(
						noteVO.getBillVersionPK(), noteVO.getSenderman()));

				return TaskTopicResolver.constructNCMsg(context);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				return null;
			}
		}

		return null;
	}

	private String getCheckResultOfNoteSenderman(String billid, String senderman)
			throws BusinessException {
		String sql = "select approveresult from pub_workflownote where checkman=? and billversionpk=? and approvestatus=? order by senddate desc";

		SQLParameter param = new SQLParameter();
		param.addParam(senderman);
		param.addParam(billid);
		param.addParam(1);

		Object approveResult = new BaseDAO().executeQuery(sql, param,
				new ColumnProcessor("approveresult"));

		if (approveResult == null) {
			return "{commitBill}";
		}
		String result = String.valueOf(approveResult);

		if (result.equals("Y"))
			return "{checkPass}";
		if (result.equals("N"))
			return "{checkNoPass}";
		if (result.equals("R")) {
			return "{rejectBill}";
		}

		return "{commitBill}";
	}

	public void addApprover(final WorkflownoteVO worknoteVO)
			throws BusinessException {
		try {
			String[] selectUsers = (String[]) worknoteVO.getExtApprovers()
					.toArray(new String[0]);

			String[] userIds = filtrateUsers(worknoteVO.getPk_wf_task(),
					selectUsers);

			if ((userIds == null) || (userIds.length == 0)
					|| (selectUsers.length != userIds.length)) {
				throw new BusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0002"));
			}

			WFTask task = null;
			TaskManagerDMO dmo = new TaskManagerDMO();
			if ((worknoteVO.getTaskInfo() == null)
					|| (worknoteVO.getTaskInfo().getTask() == null)) {

				task = dmo.getTaskByPK(worknoteVO.getPk_wf_task());
			} else {
				task = worknoteVO.getTaskInfo().getTask();
			}

			String modifyTime = new UFDateTime(System.currentTimeMillis())
					.toString();

			int processMode = task.getParticipantProcessMode();

			if (task.getParticipantProcessMode() == 0) {
				processMode = 1;
			} else if (task.getParticipantProcessMode() == -1) {
				processMode = 2;
			}

			String updateSql = "update  pub_wf_task  set   modifyTime = '"
					+ modifyTime + "' , processMode = " + processMode
					+ " where  pk_wf_task = '" + worknoteVO.getPk_wf_task()
					+ "'";

			new BaseDAO().executeUpdate(updateSql);

			List<WorkflownoteVO> noteVOList = new ArrayList();
			for (int i = 0; i < userIds.length; i++) {
				WorkflownoteVO noteVO = (WorkflownoteVO) worknoteVO.clone();
				String[] checkManInfo = TaskTopicResolver
						.queryDynamicAgentOfCheckman(userIds[i],
								worknoteVO.getPk_billtype());

				noteVO.setActiontype(noteVO.getActiontype() + "_A");

				noteVO.setCheckman(checkManInfo[0]);
				noteVO.setPk_checkflow(null);
				noteVO.setObserver(worknoteVO.getCheckman());

				// 20170904 hezy 加签-发送日期改为加签当天，而不是原流程发送日期
				noteVO.setSenddate(new UFDateTime());

				String originTopic = noteVO.getMessagenote();
				String userID = noteVO.getSenderman();
				final String newTopic = uapdateMsgTopic(task, userID,
						originTopic);
				noteVO.setMessagenote(new AbstractMsgL10NCallback() {
					public String getMessage() throws BusinessException {
						StringBuffer sb = new StringBuffer();
						sb.append("[");
						sb.append(NCLangResOnserver.getInstance()
								.getStrByID(
										"pfworkflow63",
										"WorkflowAdminImpl-0000",
										null,
										new String[] { Pfi18nTools
												.getUserName(worknoteVO
														.getCheckman()) }));

						sb.append("]");
						sb.append(newTopic);

						return newTopic;
					}
				}.getLocalizedMsg(noteVO.getCheckman()));

				noteVOList.add(noteVO);
			}

			PFRequestDataCacheProxy.put(new WFTaskCacheKey(task.getTaskPK()),
					task);

			WorknoteManager.insertWorknoteList(noteVOList);
			// YiXin add begin sendWorkflow协办发送新待办
			try {
				for (int i = 0; i < noteVOList.size(); i++) {
					Logger.error("YiXin记录日志：开始发送待办："
							+ noteVOList.get(i).getPk_checkflow());
					WorkitemVO workitem = SyncWorkitemUtil
							.queryWokritemByPk(noteVOList.get(i)
									.getPk_checkflow());
					Logger.error("YiXin记录日志：" + workitem);
					SyncWorkitemUtil.beginExternalWorkitem(null,workitem);
					Logger.error("YiXin记录日志：发送待办OK");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// YiXin add begin sendWorkflow
			// 20170904 hezy 财务升级加签改造-根据流程参与模式，如果为抢占模式2，则删除该流程下其他审批人的待办 begin
			if (task.getParticipantProcessMode() == 2) {
				Logger.init("addSignUpgrate");
				Logger.debug("加签改造升级记录日志：查找同一流程下其他审批人的待办事项"
						+ worknoteVO.getPk_checkflow() + "&"
						+ worknoteVO.getBillno());
				WorkitemVO[] workitems = SyncWorkitemUtil
						.queryAddSignWokritemByNote(worknoteVO);
				if (null != workitems && workitems.length > 0) {
					List<String> pkList = new ArrayList<String>();
					List<String> checkManList = new ArrayList<String>();
					Logger.debug("加签改造升级记录日志：删除同一流程下其他审批人的OA待办事项"
							+ worknoteVO.getPk_checkflow() + "&"
							+ worknoteVO.getBillno());
					for (WorkitemVO wt : workitems) {
						pkList.add(wt.getWorkitemId());
						checkManList.add(wt.getRecipientId());
						SyncWorkitemUtil.deleteExternalWorkitem(wt);
					}
					String updateIsCheckSql = "UPDATE PUB_WORKFLOWNOTE SET ISCHECK='X',APPROVESTATUS=4,CHECKNOTE='他人加签后抢占流程作废',DEALDATE='"
							+ new UFDateTime().toString()
							+ "' "
							+ " WHERE PK_CHECKFLOW "
							+ SqlBuilder.getInSQL(pkList
									.toArray(new String[pkList.size()]));
					Logger.debug("加签改造升级记录日志：设置同一流程下其他审批人的NC待办事项状态为废弃"
							+ worknoteVO.getPk_checkflow() + "&"
							+ worknoteVO.getBillno() + "&" + updateIsCheckSql);
					new BaseDAO().executeUpdate(updateIsCheckSql);

					Logger.debug("加签改造升级记录日志：删除工作任务表"
							+ worknoteVO.getPk_checkflow() + "&"
							+ worknoteVO.getBillno());
					String updateMsgSql = "UPDATE SM_MSG_CONTENT M SET M.DR=0,M.ISHANDLED='Y',M.ISREAD='Y'\n"
							+
							/*
							 * hezy 20170920
							 * oracle-00957重复列，原因是basedao执行update时会默认更新ts字段
							 * ，所以去掉sql中的ts更新部分
							 */
							/*
							 * "UPDATE SM_MSG_CONTENT M SET M.DR=0,M.ISHANDLED='Y',M.ISREAD='Y',M.TS=TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS')\n"
							 * +
							 */
							" WHERE M.CONTENTTYPE='Z' AND M.DESTINATION='inbox' AND NVL(M.ISHANDLED,'N')='N'\n"
							+
							// 20170921 hezy
							// 清除NC工作任务条件设置错误问题，误清除了加签人的工作任务，应该清除抢占模式下非加签人的工作任务
							// " AND M.RECEIVER='"+ worknoteVO.getCheckman()
							// +"' AND M.PK_DETAIL='"+
							// worknoteVO.getPk_checkflow() +"' ";
							" AND M.RECEIVER "
							+ SqlBuilder.getInSQL(checkManList
									.toArray(new String[checkManList.size()]))
							+ " AND M.PK_DETAIL "
							+ SqlBuilder.getInSQL(pkList
									.toArray(new String[pkList.size()]));

					new BaseDAO().executeUpdate(updateMsgSql);
				}
			}
			// end
		} catch (Exception e) {
			handleException(e, e.getMessage());
		}
	}

	private String uapdateMsgTopic(WFTask task, String senderMan,
			String originalMsgTopic) {
		String tempcode = null;
		String newTopic = null;
		try {
			String wfProcessDefPK = task.getWfProcessDefPK();
			WorkflowProcess wf = PfDataCache.getWorkflowProcess(wfProcessDefPK);
			tempcode = TaskTopicResolver.getMsgTempCode(wf, task);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		if (tempcode == null) {
			String[] subTopics = originalMsgTopic.split(",");

			newTopic = Pfi18nTools.getUserName(senderMan) + " "
					+ "{addApprover}" + "," + subTopics[1] + "," + subTopics[2];
		} else {
			newTopic = Pfi18nTools.getUserName(senderMan) + " "
					+ "{addApprover}" + "," + "{billno}: " + task.getBillNO()
					+ ",{please}{checkBill}";
		}

		return newTopic;
	}

	private String[] filtrateUsers(String taskPk, String[] users)
			throws DbException, BusinessException {
		List<String> resultUsers = new ArrayList();
		if ((users == null) || (users.length == 0)) {
			return null;
		}

		TaskManagerDMO dmo = new TaskManagerDMO();
		WorkflownoteVO[] worknotes = dmo.queryStartedWorkitemsOfTask(taskPk);

		if ((worknotes == null) || (worknotes.length == 0)) {
			return users;
		}
		List<String> checkMans = new ArrayList();
		for (WorkflownoteVO worknote : worknotes) {
			checkMans.add(worknote.getCheckman());
		}
		for (String approverUser : users) {
			if (!checkMans.contains(approverUser)) {
				resultUsers.add(approverUser);
			}
		}
		return (String[]) resultUsers.toArray(new String[0]);
	}

	public void terminateWorkflow(String billid, String pkBilltype,
			String billNo, int iWorkflowtype) throws BusinessException {
		PKLock.getInstance().addDynamicLock(billid);

		try {
			if (iWorkflowtype == WorkflowTypeEnum.Approveflow.getIntValue()) {
				terminateApproveflow(billid, pkBilltype, billNo, null, null,
						false);
			} else if (iWorkflowtype == WorkflowTypeEnum.Workflow.getIntValue()) {
				terminateWorkflow(billid, pkBilltype, billNo, null, null);
			} else {
				throw new PFBusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0004"));
			}
		} catch (Exception e) {
			handleException(e, null);
		}
	}

	private void terminateApproveflow(String billid, String pkBilltype,
			String billNo, String reason, AggregatedValueObject billVO,
			boolean autoApproveAfterCommit) throws Exception {
		EngineService es = new EngineService();
		int status = es.queryApproveflowStatus(billid, pkBilltype);
		if (((status == 1) || (status == 0)) && (!autoApproveAfterCommit)) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "wfAdminImpl-0005"));
		}

		AbstractBusiStateCallback absc = new PFBusiStateOfMeta();

		if (billVO == null) {
			IPFConfig pfcfg = (IPFConfig) NCLocator.getInstance().lookup(
					IPFConfig.class.getName());

			billVO = pfcfg.queryBillDataVO(pkBilltype, billid);
		}

		PfParameterVO paraVo = construtParamVO(billid, pkBilltype, billNo,
				WorkflowTypeEnum.Approveflow.getIntValue());

		paraVo.m_workFlow = new WorkflownoteVO();
		paraVo.m_workFlow.setChecknote(WfTaskOrInstanceStatus.Terminated
				.toString());

		paraVo.m_workFlow.setActiontype("TERMINATE");
		paraVo.m_preValueVo = billVO;
		WorknoteManager manager = new WorknoteManager();
		String processId = manager.getProcessId(paraVo,
				WorkflowTypeEnum.Approveflow.getIntValue());

		if (billVO != null) {
			absc.execUnApproveState(paraVo, null, -1);

			es.rollbackWorkflow(billid, pkBilltype, billVO,
					WorkflowTypeEnum.Approveflow.getIntValue());
		}

		List<Object[]> statusList = es.queryProcessStatus(billid, pkBilltype,
				WorkflowTypeEnum.Approveflow.getIntValue());

		List<FlowInstanceHistoryVO> historyList = new ArrayList();

		for (Object[] statusRow : statusList) {
			String pk_wf_instance = String.valueOf(statusRow[0]);
			Integer procStatus = Integer.valueOf(Integer.parseInt(String
					.valueOf(statusRow[1])));

			FlowInstanceHistoryVO history = new FlowInstanceHistoryVO();
			history.setPk_wf_instance(pk_wf_instance);
			history.setPreviousStatus(procStatus);
			history.setOperation(Integer
					.valueOf(FlowInstanceOperation.TERMINATE.getIntValue()));
			history.setReason(reason);
			history.setOperator(InvocationInfoProxy.getInstance().getUserId());
			history.setBilltype(pkBilltype);
			history.setBillid(billid);
			history.setBillno(billNo);

			history.setOperationDate(new UFDateTime());

			historyList.add(history);
		}

		manager.sendMsgWhenWFstateChanged(paraVo, processId,
				WfTaskOrInstanceStatus.Terminated.getIntValue(),
				WorkflowTypeEnum.Approveflow.getIntValue());

		es.deleteWorkflow(billid, pkBilltype, false,
				WorkflowTypeEnum.Approveflow.getIntValue());
	}

	private void terminateWorkflow(String billid, String pkBilltype,
			String billNo, String reason, AggregatedValueObject billVO)
			throws Exception {
		EngineService es = new EngineService();
		int status = es.queryWorkflowStatus(billid, pkBilltype);
		if (status == 1) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "wfAdminImpl-0005"));
		}

		AbstractBusiStateCallback absc = new PFBusiStateOfMeta();

		if (billVO == null) {
			IPFConfig pfcfg = (IPFConfig) NCLocator.getInstance().lookup(
					IPFConfig.class.getName());

			billVO = pfcfg.queryBillDataVO(pkBilltype, billid);
		}

		PfParameterVO paraVo = construtParamVO(billid, pkBilltype, billNo,
				WorkflowTypeEnum.Workflow.getIntValue());

		paraVo.m_workFlow = new WorkflownoteVO();
		paraVo.m_workFlow.setChecknote(WfTaskOrInstanceStatus.Terminated
				.toString());

		paraVo.m_preValueVo = billVO;
		absc.execUnApproveState(paraVo, null, -1);
		WorknoteManager manager = new WorknoteManager();
		String processId = manager.getProcessId(paraVo,
				WorkflowTypeEnum.Workflow.getIntValue());

		List<Object[]> statusList = es.queryProcessStatus(billid, pkBilltype,
				WorkflowTypeEnum.Workflow.getIntValue());

		List<FlowInstanceHistoryVO> historyList = new ArrayList();

		for (Object[] statusRow : statusList) {
			String pk_wf_instance = String.valueOf(statusRow[0]);
			Integer procStatus = Integer.valueOf(Integer.parseInt(String
					.valueOf(statusRow[1])));

			FlowInstanceHistoryVO history = new FlowInstanceHistoryVO();
			history.setPk_wf_instance(pk_wf_instance);
			history.setPreviousStatus(procStatus);
			history.setOperation(Integer
					.valueOf(FlowInstanceOperation.TERMINATE.getIntValue()));
			history.setReason(reason);
			history.setOperator(InvocationInfoProxy.getInstance().getUserId());
			history.setBilltype(pkBilltype);
			history.setBillid(billid);
			history.setBillno(billNo);

			history.setOperationDate(new UFDateTime());

			historyList.add(history);
		}

		new BaseDAO().insertVOList(historyList);

		es.rollbackWorkflow(billid, pkBilltype, billVO,
				WorkflowTypeEnum.Workflow.getIntValue());

		es.deleteWorkflow(billid, pkBilltype, false,
				WorkflowTypeEnum.Workflow.getIntValue());

		manager.sendMsgWhenWFstateChanged(paraVo, processId,
				WfTaskOrInstanceStatus.Terminated.getIntValue(),
				WorkflowTypeEnum.Workflow.getIntValue());
	}

	public void resumeWorkflow(String billid, String pkBilltype, String billNo,
			int iWorkflowtype) throws BusinessException {
		try {
			if (iWorkflowtype == WorkflowTypeEnum.Approveflow.getIntValue()) {
				resumeApproveflow(billid, pkBilltype, billNo, null);
			} else if (iWorkflowtype == WorkflowTypeEnum.Workflow.getIntValue()) {
				resumeWorkflow(billid, pkBilltype, billNo, null);
			} else {
				throw new PFBusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0004"));
			}
		} catch (Exception e) {
			handleException(e, null);
		}
	}

	private void resumeWorkflow(String billid, String pkBilltype,
			String billNo, String reason) throws Exception {
		EngineService queryDMO = new EngineService();
		int status = queryDMO.queryApproveflowStatus(billid, pkBilltype);
		if ((status == 1) || (status == 0)) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "wfAdminImpl-0006"));
		}

		List<Object[]> statusList = queryDMO.queryProcessStatus(billid,
				pkBilltype, WorkflowTypeEnum.Workflow.getIntValue());

		List<FlowInstanceHistoryVO> historyList = new ArrayList();

		for (Object[] statusRow : statusList) {
			String pk_wf_instance = String.valueOf(statusRow[0]);
			Integer procStatus = Integer.valueOf(Integer.parseInt(String
					.valueOf(statusRow[1])));

			FlowInstanceHistoryVO history = new FlowInstanceHistoryVO();
			history.setPk_wf_instance(pk_wf_instance);
			history.setPreviousStatus(procStatus);
			history.setOperation(Integer.valueOf(FlowInstanceOperation.RESUME
					.getIntValue()));
			history.setReason(reason);
			history.setOperator(InvocationInfoProxy.getInstance().getUserId());
			history.setBilltype(pkBilltype);
			history.setBillid(billid);
			history.setBillno(billNo);

			history.setOperationDate(new UFDateTime());

			historyList.add(history);
		}

		queryDMO.updateProcessStatus(billid, pkBilltype,
				WorkflowTypeEnum.Workflow.getIntValue(),
				WfTaskOrInstanceStatus.Started.getIntValue());

		PfParameterVO paramVo = construtParamVO(billid, pkBilltype, billNo,
				WorkflowTypeEnum.Workflow.getIntValue());

		WorknoteManager manager = new WorknoteManager();
		String processId = manager.getProcessId(paramVo,
				WorkflowTypeEnum.Workflow.getIntValue());

		manager.sendMsgWhenWFstateChanged(paramVo, processId, 10,
				WorkflowTypeEnum.Workflow.getIntValue());
	}

	public void suspendWorkflow(String billid, String pkBilltype,
			String billNo, int iWorkflowtype) throws BusinessException {
		try {
			if (iWorkflowtype == WorkflowTypeEnum.Approveflow.getIntValue()) {
				suspendApproveflow(billid, pkBilltype, billNo, null);
			} else if (iWorkflowtype == WorkflowTypeEnum.Workflow.getIntValue()) {
				suspendWorkflow(billid, pkBilltype, billNo, null);
			} else {
				throw new PFBusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0004"));
			}
		} catch (Exception e) {
			handleException(e, null);
		}
	}

	private void handleException(Exception e, String message)
			throws BusinessException {
		if (e == null) {
			return;
		}

		Logger.error(e.getMessage(), e);

		if (((e instanceof BusinessException))
				&& (StringUtil.isEmptyWithTrim(message))) {
			throw ((BusinessException) e);
		}
		if (StringUtil.isEmptyWithTrim(message)) {
			throw new PFBusinessException(e);
		}
		throw new PFBusinessException(message, e);
	}

	private void resumeApproveflow(String billid, String pkBilltype,
			String billNo, String reason) throws Exception {
		EngineService queryDMO = new EngineService();
		int status = queryDMO.queryApproveflowStatus(billid, pkBilltype);
		if ((status == 1) || (status == 0)) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "wfAdminImpl-0006"));
		}

		List<Object[]> statusList = queryDMO.queryProcessStatus(billid,
				pkBilltype, WorkflowTypeEnum.Approveflow.getIntValue());

		List<FlowInstanceHistoryVO> historyList = new ArrayList();

		for (Object[] statusRow : statusList) {
			String pk_wf_instance = String.valueOf(statusRow[0]);
			Integer procStatus = Integer.valueOf(Integer.parseInt(String
					.valueOf(statusRow[1])));

			FlowInstanceHistoryVO history = new FlowInstanceHistoryVO();
			history.setPk_wf_instance(pk_wf_instance);
			history.setPreviousStatus(procStatus);
			history.setOperation(Integer.valueOf(FlowInstanceOperation.RESUME
					.getIntValue()));
			history.setReason(reason);
			history.setOperator(InvocationInfoProxy.getInstance().getUserId());
			history.setBilltype(pkBilltype);
			history.setBillid(billid);
			history.setBillno(billNo);

			history.setOperationDate(new UFDateTime());

			historyList.add(history);
		}

		new BaseDAO().insertVOList(historyList);

		queryDMO.updateProcessStatus(billid, pkBilltype,
				WorkflowTypeEnum.Approveflow.getIntValue(),
				WfTaskOrInstanceStatus.Started.getIntValue());

		PfParameterVO paramVo = construtParamVO(billid, pkBilltype, billNo,
				WorkflowTypeEnum.Approveflow.getIntValue());

		WorknoteManager manager = new WorknoteManager();
		String processId = manager.getProcessId(paramVo,
				WorkflowTypeEnum.Approveflow.getIntValue());

		manager.sendMsgWhenWFstateChanged(paramVo, processId, 10,
				WorkflowTypeEnum.Approveflow.getIntValue());
	}

	private void suspendWorkflow(String billid, String pkBilltype,
			String billNo, String reason) throws Exception {
		EngineService queryDMO = new EngineService();
		int status = queryDMO.queryApproveflowStatus(billid, pkBilltype);
		if ((status == 1) || (status == 0)) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "wfAdminImpl-0007"));
		}

		List<Object[]> statusList = queryDMO.queryProcessStatus(billid,
				pkBilltype, WorkflowTypeEnum.Workflow.getIntValue());

		List<FlowInstanceHistoryVO> historyList = new ArrayList();

		for (Object[] statusRow : statusList) {
			String pk_wf_instance = String.valueOf(statusRow[0]);
			Integer procStatus = Integer.valueOf(Integer.parseInt(String
					.valueOf(statusRow[1])));

			FlowInstanceHistoryVO history = new FlowInstanceHistoryVO();
			history.setPk_wf_instance(pk_wf_instance);
			history.setPreviousStatus(procStatus);
			history.setOperation(Integer.valueOf(FlowInstanceOperation.SUSPEND
					.getIntValue()));
			history.setReason(reason);
			history.setOperator(InvocationInfoProxy.getInstance().getUserId());
			history.setBilltype(pkBilltype);
			history.setBillid(billid);
			history.setBillno(billNo);

			history.setOperationDate(new UFDateTime());

			historyList.add(history);
		}

		new BaseDAO().insertVOList(historyList);

		queryDMO.updateProcessStatus(billid, pkBilltype,
				WorkflowTypeEnum.Workflow.getIntValue(),
				WfTaskOrInstanceStatus.Suspended.getIntValue());

		PfParameterVO paramVo = construtParamVO(billid, pkBilltype, billNo,
				WorkflowTypeEnum.Workflow.getIntValue());

		WorknoteManager manager = new WorknoteManager();
		String processId = manager.getProcessId(paramVo,
				WorkflowTypeEnum.Workflow.getIntValue());

		manager.sendMsgWhenWFstateChanged(paramVo, processId,
				WfTaskOrInstanceStatus.Suspended.getIntValue(),
				WorkflowTypeEnum.Workflow.getIntValue());
	}

	private void suspendApproveflow(String billid, String pkBilltype,
			String billNo, String reason) throws Exception {
		EngineService queryDMO = new EngineService();
		int status = queryDMO.queryApproveflowStatus(billid, pkBilltype);
		if ((status == 1) || (status == 0)) {
			throw new PFBusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "wfAdminImpl-0007"));
		}

		List<Object[]> statusList = queryDMO.queryProcessStatus(billid,
				pkBilltype, WorkflowTypeEnum.Approveflow.getIntValue());

		List<FlowInstanceHistoryVO> historyList = new ArrayList();

		for (Object[] statusRow : statusList) {
			String pk_wf_instance = String.valueOf(statusRow[0]);
			Integer procStatus = Integer.valueOf(Integer.parseInt(String
					.valueOf(statusRow[1])));

			FlowInstanceHistoryVO history = new FlowInstanceHistoryVO();
			history.setPk_wf_instance(pk_wf_instance);
			history.setPreviousStatus(procStatus);
			history.setOperation(Integer.valueOf(FlowInstanceOperation.SUSPEND
					.getIntValue()));
			history.setReason(reason);
			history.setOperator(InvocationInfoProxy.getInstance().getUserId());
			history.setBilltype(pkBilltype);
			history.setBillid(billid);
			history.setBillno(billNo);

			history.setOperationDate(new UFDateTime());

			historyList.add(history);
		}

		new BaseDAO().insertVOList(historyList);

		queryDMO.updateProcessStatus(billid, pkBilltype,
				WorkflowTypeEnum.Approveflow.getIntValue(),
				WfTaskOrInstanceStatus.Suspended.getIntValue());

		PfParameterVO paramVo = construtParamVO(billid, pkBilltype, billNo,
				WorkflowTypeEnum.Approveflow.getIntValue());

		WorknoteManager manager = new WorknoteManager();
		String processId = manager.getProcessId(paramVo,
				WorkflowTypeEnum.Approveflow.getIntValue());

		manager.sendMsgWhenWFstateChanged(paramVo, processId,
				WfTaskOrInstanceStatus.Suspended.getIntValue(),
				WorkflowTypeEnum.Approveflow.getIntValue());
	}

	public void mailUrgency(WorkflownoteVO workitemnote)
			throws BusinessException {
		EngineService wfQry = new EngineService();
		LinkedHashMap<String, BasicWorkflowProcess> lhm = new LinkedHashMap();

		TaskManagerDMO dmo = new TaskManagerDMO();
		WFTask task = null;
		try {
			task = dmo.getTaskByPK(workitemnote.getPk_wf_task());
		} catch (DbException e) {
			handleException(e, null);
		}

		if (task == null) {
			return;
		}
		String strProcInstPK = task.getWfProcessInstancePK();

		BasicWorkflowProcess bwp = null;
		if (!lhm.containsKey(strProcInstPK)) {
			try {
				bwp = wfQry.findParsedMainWfProcessByInstancePK(strProcInstPK);
				lhm.put(strProcInstPK, bwp);
			} catch (Exception e) {
				handleException(e, null);
			}
		}
		if (bwp == null) {
			return;
		}
		String strCheckman = workitemnote.getCheckman();

		String pk_checkflow = workitemnote.getPk_checkflow();
		FlowOverdueVO overdue = (FlowOverdueVO) getWorknoteOverdueBatch(
				new String[] { pk_checkflow }).get(pk_checkflow);

		String strMessagenote = null;
		if (overdue.isOverdue()) {
			strMessagenote = NCLangResOnserver.getInstance().getStrByID(
					"pfworkflow",
					"wfAdminImpl-0008",
					null,
					new String[] {
							String.valueOf(overdue.getOverdueDays())
									+ DurationUnit.DAY.toString(),
							workitemnote.getMessagenote() });

		} else {

			strMessagenote = workitemnote.getMessagenote();
		}

		try {
			IUserPubService userService = (IUserPubService) NCLocator
					.getInstance().lookup(IUserPubService.class);

			IUAPQueryBS uapQry = (IUAPQueryBS) NCLocator.getInstance().lookup(
					IUAPQueryBS.class);

			UserVO[] users = userService
					.getUsersByPKs(new String[] { strCheckman });

			if (users == null) {
				throw new BusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0012"));
			}

			String pk_psn_doc = users[0].getPk_base_doc();
			if (StringUtil.isEmpty(pk_psn_doc)) {
				throw new BusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0010"));
			}

			PsndocVO psndoc = (PsndocVO) uapQry.retrieveByPK(PsndocVO.class,
					userService.queryPsndocByUserid(strCheckman));

			String email = psndoc == null ? null : psndoc.getEmail();
			if (StringUtil.isEmptyWithTrim(email)) {
				throw new BusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0011"));
			}

		} catch (Exception e) {
			handleException(e, null);
		}

		MessageMetaVO meta = PfMessageUtil.createMessageMeta(workitemnote,
				task, null);

		meta.setReceiver(strCheckman);
		meta.setSenddate(new UFDateTime());
		meta.setMessage_type("email-notice");
		meta.setTitle(strMessagenote);

		Map<String, MessageMetaVO> userMetaMap = new HashMap();
		userMetaMap.put(strCheckman, meta);

		EmailMsg em = new EmailMsg();

		em.setMailModal(MailModal.MAIL_INFO);
		em.setUserIds(new String[] { strCheckman });
		em.setBillId(task.getBillID());
		em.setBillNo(task.getBillNO());
		em.setBillType(task.getBillType());
		em.setPrintTempletId(bwp.getMailPrintTemplet().getTempletid());
		em.setTopic(strMessagenote);
		em.setSenderman(task.getSenderman());
		em.setTasktype(task.getTaskType());
		em.setLangCode(InvocationInfoProxy.getInstance().getLangCode());
		em.setDatasource(InvocationInfoProxy.getInstance().getUserDataSource());
		em.setInvocationInfo(getInvocationInfo());
		em.setUserMetaMap(userMetaMap);

		new PfEmailSendTask(em).getTaskBody().execute();
	}

	public UFBoolean hasRunningProcess(String billId, String billType,
			String flowType) {
		String sql = "select pk_wf_instance from pub_wf_instance where billversionpk=? and billtype=? and procstatus="
				+ WfTaskOrInstanceStatus.Started.getIntValue()
				+ " and workflow_type=?";

		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(billId);
			para.addParam(billType);
			para.addParam(Integer.parseInt(flowType));

			Object obj = jdbc.executeQuery(sql, para, new ColumnProcessor(1));
			return obj == null ? UFBoolean.FALSE : UFBoolean.TRUE;
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
		} finally {
			if (persist != null)
				persist.release();
		}
		return UFBoolean.FALSE;
	}

	@Deprecated
	public void saveFlowInstanceSetting(String pk_wf_instance,
			FlowInstanceSettingVO[] settings) throws BusinessException {
		String sql = " delete from pub_wf_ist where pk_wf_instance = ? ";
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(pk_wf_instance);
			jdbc.executeUpdate(sql, para);

			String insertSql = "insert into pub_wf_ist(pk_wf_ist,pk_wf_instance,activitydefid,timelimit,timeremind) values (?,?,?,?,?)";
			for (FlowInstanceSettingVO vo : settings) {
				SQLParameter para1 = new SQLParameter();
				para1.addParam(OidGenerator.getInstance().nextOid());
				para1.addParam(pk_wf_instance);
				para1.addParam(vo.getId());
				para1.addParam(vo.getTimeLimit());
				para1.addParam(vo.getTimeRemind());
				jdbc.addBatch(insertSql, para1);
			}

			jdbc.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
		} finally {
			if (persist != null) {
				persist.release();
			}
		}
	}

	@Deprecated
	public FlowInstanceSettingVO[] getFlowInstanceSetting(String pk_wf_instance)
			throws BusinessException {
		String sql = " select activitydefid , timelimit, timeremind  from pub_wf_ist where pk_wf_instance = ? ";

		ArrayList<FlowInstanceSettingVO> ret = null;
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			SQLParameter para = new SQLParameter();
			para.addParam(pk_wf_instance);
			ret = (ArrayList) jdbc.executeQuery(sql, para, new BaseProcessor() {

				public Object processResultSet(ResultSet rs)
						throws SQLException {

					ArrayList<FlowInstanceSettingVO> l = new ArrayList();
					while (rs.next()) {
						FlowInstanceSettingVO vo = new FlowInstanceSettingVO();
						vo.setId(rs.getString(1));
						vo.setTimeLimit(rs.getInt(2));
						vo.setTimeRemind(rs.getInt(3));
						l.add(vo);
					}
					return l;
				}
			});
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
		} finally {
			if (persist != null)
				persist.release();
		}
		return ret == null ? null : (FlowInstanceSettingVO[]) ret
				.toArray(new FlowInstanceSettingVO[0]);
	}

	public void cpySendByMailAndMsg(WorkflownoteVO worknoteVO,
			String[] titleAndnote) throws BusinessException {
		doCpySendByMsg(worknoteVO, titleAndnote);
		doCpySendByMail(worknoteVO, titleAndnote);
	}

	private void doCpySendByMsg(WorkflownoteVO worknoteVO, String[] titleAndnote)
			throws BusinessException {
		List<String> target = worknoteVO.getMsgExtCpySenders();

		if (ArrayUtil.isNull(target)) {
			return;
		}
		String[] msgExtCpySenders = (String[]) target.toArray(new String[0]);

		List<MessageinfoVO> msgInfoVOs = new ArrayList();
		String[] checkerNames = getUserNameByPK(msgExtCpySenders);

		String[] senderNames = getUserNameByPK(new String[] { worknoteVO
				.getCheckman() });

		if ((checkerNames == null) || (checkerNames.length == 0))
			return;
		int start = 0;
		for (int end = msgExtCpySenders.length; start < end; start++) {
			MessageinfoVO msgVO = new MessageinfoVO();
			msgVO = new MessageinfoVO();
			msgVO.setBillid(worknoteVO.getBillid());

			msgVO.setSenderman(worknoteVO.getCheckman());

			msgVO.setBillno(worknoteVO.getBillno());
			msgVO.setCheckman(msgExtCpySenders[start]);
			msgVO.setCheckmanName(checkerNames[start]);
			msgVO.setContent(titleAndnote[1]);
			msgVO.setPk_billtype(worknoteVO.getPk_billtype());

			msgVO.setTitle(senderNames[0] + "{UPPpfworkflow-000154}" + " "
					+ titleAndnote[0] + " " + "{UPPpfworkflow-000194}"
					+ worknoteVO.getBillno());

			msgVO.setSenddate(new UFDateTime());
			msgVO.setDealdate(worknoteVO.getDealdate());
			msgVO.setDr(worknoteVO.getDr());
			msgVO.setType(Integer.valueOf(6));
			msgVO.setPk_corp(worknoteVO.getPk_group());
			msgInfoVOs.add(msgVO);
		}
		PfMessageUtil.insertBizMessages((MessageinfoVO[]) msgInfoVOs
				.toArray(new MessageinfoVO[msgInfoVOs.size()]));
	}

	private void doCpySendByMail(WorkflownoteVO worknoteVO,
			String[] titleAndnote) throws BusinessException {
		List<String> target = worknoteVO.getMailExtCpySenders();

		if (ArrayUtil.isNull(target)) {
			return;
		}

		String[] mailExtCpySenders = (String[]) target.toArray(new String[0]);

		EngineService wfQry = new EngineService();
		WFTask currentTask = worknoteVO.getTaskInfo().getTask();
		BasicWorkflowProcess bwp = wfQry
				.findParsedMainWfProcessByInstancePK(currentTask
						.getWfProcessInstancePK());

		String ptId = bwp.getMailPrintTemplet().getTempletid();

		MessageMetaVO meta = PfMessageUtil.createMessageMeta(worknoteVO,
				currentTask, null);

		meta.setMessage_type("email-notice");

		Map<String, MessageMetaVO> userMetaMap = new HashMap();

		for (String receiver : mailExtCpySenders) {
			MessageMetaVO cloned = (MessageMetaVO) meta.clone();
			cloned.setReceiver(receiver);

			userMetaMap.put(receiver, meta);
		}

		EmailMsg em = new EmailMsg();
		em.setMailModal(MailModal.MAIL_INFO);
		em.setUserIds(mailExtCpySenders);
		em.setBillId(worknoteVO.getBillid());
		em.setBillNo(worknoteVO.getBillno());
		em.setBillType(worknoteVO.getPk_billtype());
		em.setPrintTempletId(ptId);
		em.setTopic(titleAndnote[0]);
		em.setSenderman(worknoteVO.getSenderman());

		em.setTasktype(WfTaskType.Makebill.getIntValue());
		em.setUserMetaMap(userMetaMap);
		em.setLangCode(InvocationInfoProxy.getInstance().getLangCode());
		em.setDatasource(InvocationInfoProxy.getInstance().getUserDataSource());
		em.setInvocationInfo(getInvocationInfo());
		PfMailAndSMSUtil.sendEMS(em);
	}

	private InvocationInfo getInvocationInfo() {
		InvocationInfo info = new InvocationInfo();

		info.setBizDateTime(InvocationInfoProxy.getInstance().getBizDateTime());
		info.setGroupId(InvocationInfoProxy.getInstance().getGroupId());
		info.setGroupNumber(InvocationInfoProxy.getInstance().getGroupNumber());
		info.setLangCode(InvocationInfoProxy.getInstance().getLangCode());
		info.setUserDataSource(InvocationInfoProxy.getInstance()
				.getUserDataSource());

		info.setUserId(InvocationInfoProxy.getInstance().getUserId());

		return info;
	}

	private String[] getUserNameByPK(String[] pks) {
		StringBuffer clause = new StringBuffer("cuserid in ( ");
		List<String> userNames = new ArrayList();
		for (String pk : pks)
			clause.append("'" + pk + "', ");
		String where = clause.substring(0, clause.lastIndexOf(",")) + ")";
		try {
			BaseDAO dao = new BaseDAO();
			Collection<UserVO> users = dao
					.retrieveByClause(UserVO.class, where);

			for (UserVO user : users)
				userNames.add(user.getUser_name());
			return (String[]) userNames.toArray(new String[userNames.size()]);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	public boolean isAlreadyTracked(String pk_wf_instance, String supervisor)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();

		String sql = "pk_wf_instance='" + pk_wf_instance + "' and supervisor='"
				+ supervisor + "' and type="
				+ ProcessInsSupervisorType.TRACKER.getIntValue();

		Collection<ProcessInstanceAVO> obj = dao.retrieveByClause(
				ProcessInstanceAVO.class, sql);

		return (obj != null) && (obj.size() != 0);
	}

	private PfParameterVO construtParamVO(String billid, String pkBilltype,
			String billNo, int workflow_type) {
		PfParameterVO paramVO = new PfParameterVO();
		paramVO.m_billVersionPK = billid;
		paramVO.m_billType = pkBilltype;
		paramVO.m_billNo = billNo;
		paramVO.m_operator = InvocationInfoProxy.getInstance().getUserId();
		paramVO.m_pkGroup = InvocationInfoProxy.getInstance().getGroupId();
		EngineService es = new EngineService();
		try {
			String strMakerId = es.queryBillmakerOfInstance(billid, pkBilltype,
					workflow_type);

			paramVO.m_makeBillOperator = strMakerId;
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return paramVO;
	}

	public void trackWFinstance(WorkflownoteVO worknoteVO, String supervisor,
			boolean isTrack) throws BusinessException {
		String pk_wf_instance = worknoteVO.getTaskInfo().getTask()
				.getWfProcessInstancePK();

		BaseDAO dao = new BaseDAO();
		if (!isTrack) {
			Logger.debug("WorkflowAdminImpl.trackWFInstance: delete supervisor="
					+ supervisor + ", pk_wf_instance=" + pk_wf_instance);

			SQLParameter param = new SQLParameter();
			param.addParam(pk_wf_instance);
			param.addParam(supervisor);

			dao.deleteByClause(ProcessInstanceAVO.class,
					"pk_wf_instance=? and supervisor=?", param);
		} else {
			ProcessInstanceAVO pvo = new ProcessInstanceAVO();
			pvo.setPk_wf_instance(pk_wf_instance);
			pvo.setSupervisor(supervisor);
			pvo.setType(Integer.valueOf(ProcessInsSupervisorType.TRACKER
					.getIntValue()));
			dao.insertVO(pvo);
		}
	}

	public void terminateWorkflow(PfParameterVO paraVo, int wftype)
			throws PFBusinessException {
		PKLock.getInstance().addDynamicLock(paraVo.m_billVersionPK);

		try {
			if (wftype == WorkflowTypeEnum.Approveflow.getIntValue()) {
				terminateApproveflow(paraVo.m_billVersionPK, paraVo.m_billType,
						paraVo.m_billNo, null, paraVo.m_preValueVo,
						paraVo.m_autoApproveAfterCommit);

			} else if (wftype == WorkflowTypeEnum.Workflow.getIntValue()) {
				terminateWorkflow(paraVo.m_billVersionPK, paraVo.m_billType,
						paraVo.m_billNo, null, paraVo.m_preValueVo);
			} else {
				throw new PFBusinessException(NCLangResOnserver.getInstance()
						.getStrByID("pfworkflow", "wfAdminImpl-0004"));
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new PFBusinessException(e);
		}
	}

	public void suspendWorkflow(WorkflowManageContext context)
			throws BusinessException {
		String billId = context.getBillId();
		Integer approvestatus = context.getApproveStatus();
		String billtype = context.getBillType();
		Integer workflow_type = context.getFlowType();
		String billNo = context.getBillNo();
		String reason = context.getManageReason();

		int iBillStatus = ((IPFWorkflowQry) NCLocator.getInstance().lookup(
				IPFWorkflowQry.class)).queryFlowStatus(billtype, billId,
				workflow_type.intValue());

		if ((approvestatus.intValue() == WfTaskOrInstanceStatus.Started
				.getIntValue()) && (iBillStatus != 5)) {
			try {

				if (workflow_type.intValue() == WorkflowTypeEnum.Approveflow
						.getIntValue()) {
					suspendApproveflow(billId, billtype, billNo, reason);
				} else if (workflow_type.intValue() == WorkflowTypeEnum.Workflow
						.getIntValue()) {
					suspendWorkflow(billId, billtype, billNo, reason);
				} else {
					throw new Exception(NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("pfworkflow61_0", "0pfworkflow61-0079"));
				}
			} catch (Exception e) {
				handleException(e, null);
			}
		} else {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow", "UPPpfworkflow-000822"));
		}
	}

	public void resumeWorkflow(WorkflowManageContext context)
			throws BusinessException {
		String billId = context.getBillId();
		Integer approvestatus = context.getApproveStatus();
		String billtype = context.getBillType();
		Integer workflow_type = context.getFlowType();
		String billNo = context.getBillNo();

		String reason = context.getManageReason();

		if (approvestatus.intValue() == WfTaskOrInstanceStatus.Suspended
				.getIntValue()) {
			try {
				if (workflow_type.intValue() == WorkflowTypeEnum.Approveflow
						.getIntValue()) {
					resumeApproveflow(billId, billtype, billNo, reason);
				} else if (workflow_type.intValue() == WorkflowTypeEnum.Workflow
						.getIntValue()) {
					resumeWorkflow(billId, billtype, billNo, reason);
				} else {
					throw new PFBusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("pfworkflow61_0",
									"0pfworkflow61-0080"));
				}
			} catch (Exception e) {
				handleException(e, null);
			}
		} else {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow", "UPPpfworkflow-000817"));
		}
	}

	public void terminateWorkflow(WorkflowManageContext context)
			throws BusinessException {
		boolean isSucess = false;
		String billId = context.getBillId();
		Integer approvestatus = context.getApproveStatus();
		String billtype = context.getBillType();
		Integer workflow_type = context.getFlowType();
		String billNo = context.getBillNo();
		String reason = context.getManageReason();

		if (approvestatus.intValue() == WfTaskOrInstanceStatus.Started
				.getIntValue()) {
			try {
				if (workflow_type.intValue() == WorkflowTypeEnum.Approveflow
						.getIntValue()) {
					terminateApproveflow(billId, billtype, billNo, reason,
							null, false);
				} else if (workflow_type.intValue() == WorkflowTypeEnum.Workflow
						.getIntValue()) {
					terminateWorkflow(billId, billtype, billNo, reason, null);
				} else {
					throw new PFBusinessException(NCLangRes4VoTransl
							.getNCLangRes().getStrByID("pfworkflow61_0",
									"0pfworkflow61-0080"));
				}
			} catch (Exception e) {
				handleException(e, null);
			}

		} else {
			throw new BusinessException(NCLangResOnserver.getInstance()
					.getStrByID("pfworkflow", "UPPpfworkflow-000535"));
		}
	}

	public void updateFlowTimeSetting(String mainPk_wf_instance,
			FlowTimeSettingVO[] settings) throws BusinessException {
		BaseDAO dao = new BaseDAO();

		dao.deleteByClause(FlowTimeSettingVO.class, "mainPk_wf_instance='"
				+ mainPk_wf_instance + "'");

		if ((settings != null) && (settings.length > 0)) {
			dao.insertVOArray(settings);
		}
	}

	public FlowTimeSettingVO[] getFlowTimeSetting(String mainPk_wf_instance)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<FlowTimeSettingVO> col = dao.retrieveByClause(
				FlowTimeSettingVO.class, "mainPk_wf_instance='"
						+ mainPk_wf_instance + "' order by type desc");

		return (FlowTimeSettingVO[]) col.toArray(new FlowTimeSettingVO[0]);
	}

	public Map<String, FlowOverdueVO> getWorknoteOverdueBatch(
			String[] pk_checkflows) throws BusinessException {
		WorkflowOverdueCalculator calculator = new WorkflowOverdueCalculator();
		Map<String, FlowOverdueVO> map = new HashMap();

		for (String pk : pk_checkflows) {
			FlowOverdueVO overdue = calculator.getWorknoteOverdue(pk);
			map.put(pk, overdue);
		}

		return map;
	}

	public Map<String, FlowOverdueVO> getFlowInstanceOverdue(
			String[] pk_wf_instances) throws BusinessException {
		WorkflowOverdueCalculator calculator = new WorkflowOverdueCalculator();
		Map<String, FlowOverdueVO> map = new HashMap();

		for (String pk : pk_wf_instances) {
			FlowOverdueVO overdue = calculator.getFlowInstanceOverdue(pk);
			map.put(pk, overdue);
		}

		return map;
	}

	public ArrayList<String> findFilterOrgs4Responsibility(WFTask task)
			throws BusinessException {
		if (task.getParticipantType().equals(
				OrganizeUnitTypes.RESPONSIBILITY.toString())) {
			try {
				BasicWorkflowProcess processdef = PfDataCache
						.getWorkflowProcess(task.getWfProcessDefPK());

				GenericActivityEx activity = (GenericActivityEx) processdef
						.findActivityByID(task.getActivityID());

				Object participantFilterMode = activity
						.getParticipantFilterMode(task.getBillType())
						.getValue();
				IOrgFilter4Responsibility orgFilter = PfOrg4ResponsibilityFactory
						.getInstance().getFilterByCode(
								participantFilterMode.toString(),
								task.getBillType());

				return orgFilter.execute(task);
			} catch (XPDLParserException e) {
				Logger.error(e.getMessage());
				throw new BusinessException(e.getCause());
			}
		}

		return null;
	}
}