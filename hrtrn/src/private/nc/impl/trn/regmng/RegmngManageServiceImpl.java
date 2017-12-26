package nc.impl.trn.regmng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.pub.pflock.PfBusinessLock;
import nc.bs.pub.pflock.VOConsistenceCheck;
import nc.bs.pub.pflock.VOLockData;
import nc.bs.pub.pflock.VOsConsistenceCheck;
import nc.bs.pub.pflock.VOsLockData;
import nc.bs.uif2.VersionConflictException;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.BillCodeHelper;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.hr.pf.IHrPf;
import nc.itf.hrp.psnbudget.IOrgBudgetQueryService;
import nc.itf.om.IAOSQueryService;
import nc.itf.trn.IItemSetAdapter;
import nc.itf.trn.IItemSetQueryService;
import nc.itf.trn.TrnDelegator;
import nc.itf.trn.regmng.IRegmngManageService;
import nc.itf.trn.regmng.IRegmngQueryService;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.md.model.IBean;
import nc.md.model.impl.MDEnum;
import nc.md.persist.framework.IMDPersistenceService;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.VOUtils;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.psn.PsnClVO;
import nc.vo.hi.entrymng.HiSendMsgHelper;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hrp.psnorgbudget.ValidateResultVO;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pf.change.IActionDriveChecker;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trn.pub.BeanUtil;
import nc.vo.trn.pub.TRNConst;
import nc.vo.trn.pub.TrnManageService;
import nc.vo.trn.regitem.TrnRegItemVO;
import nc.vo.trn.regmng.AggRegapplyVO;
import nc.vo.trn.regmng.RegapplyVO;
import nc.vo.uap.pf.PFBatchExceptionInfo;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class RegmngManageServiceImpl extends TrnManageService implements IRegmngManageService, IRegmngQueryService, IActionDriveChecker {
	private final String DOC_NAME = "RegmngManage";
	private SimpleDocServiceTemplate serviceTemplate;

	public RegmngManageServiceImpl() {
	}

	public PfProcessBatchRetObject batchCommitBill(AggregatedValueObject[] bills) throws BusinessException {
		PFBatchExceptionInfo exInfo = new PFBatchExceptionInfo();
		ArrayList<AggregatedValueObject> successObj = new ArrayList();
		for (int i = 0; i < bills.length; i++) {
			try {
				AggregatedValueObject aggVO =
						((IRegmngManageService) NCLocator.getInstance().lookup(IRegmngManageService.class)).commitBill_RequiresNew(bills[i]);
				successObj.add(aggVO);
			} catch (Exception e) {
				if ((e instanceof VersionConflictException)) {
					exInfo.putErrorMessage(i, bills[i], ((VersionConflictException) e).getBusiObject() == null ? "" : ((VersionConflictException) e).getBusiObject().toString());

				} else {
					exInfo.putErrorMessage(i, bills[i], StringUtils.isBlank(e.getMessage()) ? "" : e.getMessage());
				}
				Logger.error(e.getMessage(), e);
			}
		}
		return new PfProcessBatchRetObject(successObj.toArray(new AggregatedValueObject[0]), exInfo);
	}

	public AggRegapplyVO[] batchSaveBill(AggRegapplyVO aggvo, ArrayList<String> pkPsnjobs, LoginContext context, String[] billCodes)
			throws BusinessException {
		boolean isAutoGenerateBillCode = isAutoGenerateBillCode("6111", context.getPk_group(), context.getPk_org());
		ArrayList<AggRegapplyVO> al = new ArrayList();
		if (!isAutoGenerateBillCode) {

			BillCodeHelper.lockBillCodeRule("hr_auto_billcode6111", 100L);
		}
		try {
			String prefix = "ZD6111" + PubEnv.getServerDate().toStdString();

			String flowCode = SQLHelper.getFlowCode(prefix, "bill_code", RegapplyVO.class);
			for (int i = 0; i < pkPsnjobs.size(); i++) {
				AggRegapplyVO temp = clone(aggvo);
				RegapplyVO head = (RegapplyVO) temp.getParentVO();
				if ((isAutoGenerateBillCode) && (billCodes != null) && (billCodes.length > 0) && (billCodes[i] != null)) {
					head.setBill_code(billCodes[i]);

				} else {
					head.setBill_code(prefix + "_" + getFlowCode(flowCode, i));
				}
				head.setApprove_state(Integer.valueOf(-1));
				head.setBillmaker(context.getPk_loginUser());

				head.setPk_billtype("6111");
				head.setPk_org(context.getPk_org());
				head.setPk_group(context.getPk_group());

				PsnJobVO oldJobVO = (PsnJobVO) queryByPk(PsnJobVO.class, (String) pkPsnjobs.get(i), true);
				head.setPk_psnjob(oldJobVO.getPk_psnjob());
				head.setPk_psndoc(oldJobVO.getPk_psndoc());
				head.setPk_psnorg(oldJobVO.getPk_psnorg());
				head.setAssgid(oldJobVO.getAssgid());

				for (String attr : oldJobVO.getAttributeNames()) {
					head.setAttributeValue("old" + attr, oldJobVO.getAttributeValue(attr));
				}

				String[] flds = { "newpk_post", "newpk_postseries", "newpk_job", "newpk_jobgrade", "newpk_jobrank", "newseries" };

				TrnRegItemVO[] itemvos =
						(TrnRegItemVO[]) TrnDelegator.getIItemSetQueryService().queryItemSetByOrg("8d246f75-552f-40e9-8688-0685a8a99a7d", context.getPk_group(), context.getPk_org(), head.getProbation_type());

				for (int j = 0; (itemvos != null) && (j < itemvos.length); j++) {
					if ((!itemvos[j].getItemkey().startsWith("old")) && (!ArrayUtils.contains(flds, itemvos[j].getItemkey()))) {

						if ((itemvos[j] != null) && (itemvos[j].getIsdefault() != null) && (itemvos[j].getIsdefault().booleanValue()) && (head.getAttributeValue(itemvos[j].getItemkey()) == null)) {

							head.setAttributeValue(itemvos[j].getItemkey(), oldJobVO.getAttributeValue(itemvos[j].getItemkey().substring(3)));
						}
					}
				}

				String newPost = head.getNewpk_post();
				String newJob = head.getNewpk_job();

				if ((newPost == null) || (newJob == null)) {

					if ((newPost != null) && (newJob == null)) {

						TrnRegItemVO rankItem = getItemByItemkey(itemvos, "newpk_jobrank");
						if ((rankItem != null) && (rankItem.getIsdefault() != null) && (rankItem.getIsdefault().booleanValue()) && (head.getNewpk_jobrank() == null)) {

							head.setNewpk_jobrank(head.getOldpk_jobrank());
						}
					} else if ((newPost != null) || (newJob == null)) {

						if (head.getOldpk_dept().equals(head.getNewpk_dept())) {
							TrnRegItemVO postItem = getItemByItemkey(itemvos, "newpk_post");
							TrnRegItemVO jobItem = getItemByItemkey(itemvos, "newpk_job");
							if ((postItem != null) && (postItem.getIsdefault() != null) && (postItem.getIsdefault().booleanValue())) {

								head.setNewpk_post(head.getOldpk_post());
								head.setNewpk_postseries(head.getOldpk_postseries());
								head.setNewpk_job(head.getOldpk_job());
								head.setNewpk_jobrank(head.getOldpk_jobrank());
								head.setNewpk_jobgrade(head.getOldpk_jobgrade());
								head.setNewseries(head.getOldseries());
							} else if ((jobItem != null) && (jobItem.getIsdefault() != null) && (jobItem.getIsdefault().booleanValue())) {

								head.setNewpk_job(head.getOldpk_job());
								head.setNewpk_jobrank(head.getOldpk_jobrank());
								head.setNewpk_jobgrade(head.getOldpk_jobgrade());
								head.setNewseries(head.getOldseries());
							}
						}
					}
				}

				String where = " pk_psnorg = '" + oldJobVO.getPk_psnorg() + "' and endflag = 'N' ";
				TrialVO[] trialVOs = (TrialVO[]) getIPersistenceRetrieve().retrieveByClause(null, TrialVO.class, where);
				if ((trialVOs != null) && (trialVOs.length > 0)) {
					head.setBegin_date(trialVOs[0].getBegindate());
					head.setEnd_date(trialVOs[0].getEnddate());
				}

				al.add(temp);
			}

			checkBillCodeRepeat((AggregatedValueObject[]) al.toArray(new AggRegapplyVO[0]));

			return (AggRegapplyVO[]) saveBatchBill((AggregatedValueObject[]) al.toArray(new AggRegapplyVO[0]));
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (!isAutoGenerateBillCode) {

				BillCodeHelper.unlockBillCodeRule("hr_auto_billcode6111");
			}
		}
	}

	private WorkflownoteVO buildWorkflownoteVO(IFlowBizItf itf, String strApproveId, String strCheckNote, int blPassed, String billtype)
			throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();
		worknoteVO.setBillid(itf.getBillId());
		worknoteVO.setBillVersionPK(itf.getBillId());
		worknoteVO.setChecknote(strCheckNote);

		worknoteVO.setSenddate(PubEnv.getServerTime());
		worknoteVO.setDealdate(PubEnv.getServerTime());

		worknoteVO.setPk_org(itf.getPkorg());

		worknoteVO.setBillno(itf.getBillNo());

		String sendman = itf.getApprover() == null ? itf.getBillMaker() : itf.getApprover();
		worknoteVO.setSenderman(sendman);

		worknoteVO.setApproveresult(1 == blPassed ? "Y" : -1 == blPassed ? "R" : "N");
		worknoteVO.setApprovestatus(Integer.valueOf(1));
		worknoteVO.setIscheck(0 == blPassed ? "N" : 1 == blPassed ? "Y" : "X");
		worknoteVO.setActiontype("APPROVE");
		worknoteVO.setCheckman(strApproveId);

		worknoteVO.setPk_billtype(billtype);
		worknoteVO.setWorkflow_type(Integer.valueOf(WorkflowTypeEnum.Approveflow.getIntValue()));
		return worknoteVO;
	}

	private void changeBillData(IFlowBizItf itf, String strApproveId, UFDateTime strApproveDate, String strCheckNote, Integer intAppState)
			throws BusinessException {
		if (itf == null) {
			return;
		}
		itf.setApprover(strApproveId);
		itf.setApproveNote(strCheckNote);
		itf.setApproveDate(strApproveDate);
		itf.setApproveStatus(intAppState);
	}

	private <T extends AggregatedValueObject> void checkBillCodeRepeat(T... billvos) throws BusinessException {
		StringBuffer errMsg = new StringBuffer();
		ArrayList<String> repeatCodes = new ArrayList();
		IPersistenceRetrieve ser = (IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
		for (T vo : billvos) {
			IFlowBizItf itf = (IFlowBizItf) NCObject.newInstance(vo).getBizInterface(IFlowBizItf.class);
			String billCode = itf.getBillNo();
			String pk_regapply = itf.getBillId();
			String billType = itf.getBilltype();
			String whereSql =
					"bill_code = '" + billCode + "' and pk_group = '" + PubEnv.getPk_group() + "'  and " + "pk_billtype" + " = '" + billType + "'";

			if (!StringUtils.isEmpty(pk_regapply)) {
				whereSql = whereSql + " and pk_hi_regapply <> '" + pk_regapply + "'";
			}
			int count = ser.getCountByCondition(RegapplyVO.getDefaultTableName(), whereSql);
			if (count > 0) {
				errMsg.append('\n' + ResHelper.getString("6007entry", "06007entry0050") + billCode + ResHelper.getString("6007entry", "06007entry0051"));

				repeatCodes.add(billCode);
			}
		}

		if (errMsg.length() > 0) {
			BillCodeRepeatBusinessException ex =
					new BillCodeRepeatBusinessException(ResHelper.getString("6007entry", "06007entry0052") + errMsg.toString());

			ex.setRepeatCodes((String[]) repeatCodes.toArray(new String[0]));
			throw ex;
		}
	}

	private AggRegapplyVO clone(AggRegapplyVO src) {
		AggRegapplyVO trg = new AggRegapplyVO();
		RegapplyVO head = new RegapplyVO();
		trg.setParentVO(head);
		for (String attrName : src.getParentVO().getAttributeNames()) {
			trg.getParentVO().setAttributeValue(attrName, src.getParentVO().getAttributeValue(attrName));
		}
		return trg;
	}

	public AggregatedValueObject commitBill_RequiresNew(AggregatedValueObject aggvo) throws BusinessException {
		RegapplyVO billvo = (RegapplyVO) ((AggRegapplyVO) aggvo).getParentVO();

		if (billvo.getTrialresult() == null) {
			throw new BusinessException(ResHelper.getString("6009reg", "06009reg0030"));
		}

		String errMsg = getMsg(billvo);
		if (StringUtils.isNotBlank(errMsg)) {
			throw new BusinessException(ResHelper.getString("6009reg", "06009reg0031"));
		}

		if (!isHasFile(billvo)) {
			throw new BusinessException(ResHelper.getString("6009reg", "06009reg0047"));
		}

		String billmaker = (String) aggvo.getParentVO().getAttributeValue("billmaker");
		if ((billmaker != null) && ("NC_USER0000000000000".equals(billmaker))) {
			aggvo.getParentVO().setAttributeValue("billmaker", PubEnv.getPk_user());
		}
		return getIHrPf().commitBill_RequiresNew(aggvo);
	}

	private PsnJobVO createNewPsnjob(RegapplyVO bill) throws BusinessException {
		PsnJobVO[] lastvo = (PsnJobVO[]) getLastVO(PsnJobVO.class, bill.getPk_psnorg(), Integer.valueOf(1));

		PsnJobVO psnjob = new PsnJobVO();

		if (bill.getRegulardate() != null) {
			psnjob.setBegindate(bill.getRegulardate());
		} else {
			psnjob.setBegindate(PubEnv.getServerLiteralDate());
		}
		psnjob.setEnddate(null);
		psnjob.setEndflag(UFBoolean.FALSE);
		psnjob.setIsmainjob(UFBoolean.TRUE);
		psnjob.setLastflag(UFBoolean.TRUE);
		psnjob.setPk_hrgroup(bill.getPk_group());
		psnjob.setPk_group(bill.getPk_group());
		psnjob.setPk_hrorg(bill.getPk_org());
		psnjob.setPk_psndoc(bill.getPk_psndoc());
		psnjob.setPk_psnorg(bill.getPk_psnorg());
		psnjob.setPk_psnjob(null);
		psnjob.setPsntype(Integer.valueOf(0));
		psnjob.setAssgid(Integer.valueOf(1));

		psnjob.setPoststat(UFBoolean.TRUE);

		psnjob.setTrial_type(null);
		psnjob.setRecordnum(Integer.valueOf(0));
		psnjob.setTrnsevent((Integer) TrnseventEnum.REGAPPLY.value());
		psnjob.setTrnstype("1002Z710000000008GSF");
		psnjob.setTrial_flag(UFBoolean.FALSE);
		psnjob.setShoworder(Integer.valueOf(9999999));

		psnjob.setOribilltype("6111");
		psnjob.setOribillpk(bill.getPk_hi_regapply());

		psnjob.setClerkcode(lastvo[0].getClerkcode());

		for (String name : bill.getAttributeNames()) {

			if (name.startsWith("new")) {
				Object value = bill.getAttributeValue(name);
				psnjob.setAttributeValue(name.substring(3), value);
			}
		}

		UFBoolean blPoststate = UFBoolean.FALSE;

		TrnRegItemVO[] tempvos =
				(TrnRegItemVO[]) TrnDelegator.getIItemSetQueryService().queryItemSetByOrg("8d246f75-552f-40e9-8688-0685a8a99a7d", bill.getPk_group(), bill.getPk_org(), bill.getProbation_type());

		TrnRegItemVO vo = null;
		for (int i = 0; (tempvos != null) && (i < tempvos.length); i++) {
			if ("newpoststat".equals(tempvos[i].getItemkey())) {

				vo = tempvos[i];
			}
		}
		if (vo == null) {

			blPoststate = lastvo[0].getPoststat();

		} else if ((vo.getIsedit() != null) && (vo.getIsedit().booleanValue())) {

			blPoststate = bill.getNewpoststat();

		} else {
			blPoststate = lastvo[0].getPoststat();
		}

		psnjob.setPoststat(blPoststate);

		return psnjob;
	}

	public Hashtable<String, String[]> createUserValue(AggregatedValueObject[] aggvos) throws BusinessException {
		RegapplyVO[] vos = new RegapplyVO[aggvos.length];
		for (int i = 0; (aggvos != null) && (i < aggvos.length); i++) {
			vos[i] = ((RegapplyVO) aggvos[i].getParentVO());
		}
		return createValueFromBills(vos);
	}

	private Hashtable<String, String[]> createValueFromBills(RegapplyVO[] vos) throws BusinessException {
		String[] fieldCode = TRNConst.FIELDCODE_REG;
		Hashtable<String, String[]> hm = new Hashtable();
		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			RegapplyVO bill = vos[i];
			for (int j = 0; j < fieldCode.length; j++) {
				String value = "";
				if ("bill_code".equals(fieldCode[j])) {

					value = bill.getBill_code();
				} else if ("regulardate".equals(fieldCode[j])) {

					value = bill.getRegulardate() == null ? "" : bill.getRegulardate().toStdString();
				} else if ("approve_state".equals(fieldCode[j])) {

					value =
							TRNConst.getBillStateName(Integer.valueOf(bill.getApprove_state() == null ? 1 : bill.getApprove_state().intValue()));
				} else if ("probation_type".equals(fieldCode[j])) {

					value = getProbationType(Integer.valueOf(bill.getProbation_type() == null ? 1 : bill.getProbation_type().intValue()));
				} else if ("trialresult".equals(fieldCode[j])) {

					value = getTrialresult(bill.getTrialresult());
				} else if ("pk_psnjob".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PsndocVO.class, bill.getPk_psndoc());
				} else if ("oldpk_org".equals(fieldCode[j])) {

					value = VOUtils.getDocName(OrgVO.class, bill.getOldpk_org());
				} else if ("newpk_org".equals(fieldCode[j])) {

					value = VOUtils.getDocName(OrgVO.class, bill.getNewpk_org());
				} else if ("oldpk_dept".equals(fieldCode[j])) {

					value = VOUtils.getDocName(DeptVO.class, bill.getOldpk_dept());
				} else if ("newpk_dept".equals(fieldCode[j])) {

					value = VOUtils.getDocName(DeptVO.class, bill.getNewpk_dept());
				} else if ("oldpk_psncl".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PsnClVO.class, bill.getOldpk_psncl());
				} else if ("newpk_psncl".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PsnClVO.class, bill.getNewpk_psncl());
				} else if ("oldpk_post".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PostVO.class, bill.getOldpk_post());
				} else if ("newpk_post".equals(fieldCode[j])) {

					value = VOUtils.getDocName(PostVO.class, bill.getNewpk_post());
				} else if ("oldpk_job".equals(fieldCode[j])) {

					value = VOUtils.getDocName(JobVO.class, bill.getOldpk_job());
				} else if ("newpk_job".equals(fieldCode[j])) {

					value = VOUtils.getDocName(JobVO.class, bill.getNewpk_job());
				} else {
					value = "";
				}
				hm.put(fieldCode[j] + i, new String[] { value });
			}
		}
		return hm;
	}

	public <T extends AggregatedValueObject> void deleteBatchBill(T... billvos) throws BusinessException {
		PfBusinessLock pfLock = null;
		IFlowBizItf flowItf = getFlowBizItf(billvos[0].getParentVO());

		try {
			pfLock = new PfBusinessLock();
			pfLock.lock(new VOsLockData(billvos, flowItf.getBilltype()), new VOsConsistenceCheck(billvos, flowItf.getBilltype()));

			DefaultValidationService vService = new DefaultValidationService();
			createCustomValidators(vService, "delete");
			SuperVO[] headvos = (SuperVO[]) getHeadVO(billvos).toArray(new SuperVO[0]);
			vService.validate(headvos);
			getMDPersistenceService().deleteBillFromDB(billvos);
		} finally {
			if (pfLock != null) {

				pfLock.unLock();
			}
		}
	}

	public <T extends AggregatedValueObject> void deleteBill(T billvo) throws BusinessException {
		PfBusinessLock pfLock = null;
		IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());

		try {
			pfLock = new PfBusinessLock();
			pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));

			DefaultValidationService vService = new DefaultValidationService();
			createCustomValidators(vService, "delete");
			SuperVO[] headvos = (SuperVO[]) getHeadVO(billvo).toArray(new SuperVO[0]);
			vService.validate(headvos);
			getMDPersistenceService().deleteBillFromDB(billvo);
		} finally {
			if (pfLock != null) {

				pfLock.unLock();
			}
		}
	}

	private void deleteOldWorknote(AggRegapplyVO vo) throws BusinessException {
		getIHrPf().deleteWorkflowNote(vo);
	}

	public PfProcessBatchRetObject directApprove(AggregatedValueObject[] billvos, String approveNote, int approveResult)
			throws BusinessException {
		PFBatchExceptionInfo errInfo = new PFBatchExceptionInfo();
		ArrayList<AggregatedValueObject> bill = new ArrayList();

		for (int i = 0; i < billvos.length; i++) {
			try {
				AggregatedValueObject retVO =
						((IRegmngManageService) NCLocator.getInstance().lookup(IRegmngManageService.class)).singleDirectApprove_RequiresNew(billvos[i], PubEnv.getPk_user(), PubEnv.getServerTime(), approveNote, approveResult);

				bill.add(retVO);
			} catch (VersionConflictException ve) {
				errInfo.putErrorMessage(i, billvos[i], ve.getBusiObject() == null ? "" : ve.getBusiObject().toString());
				Logger.error(ve.getMessage(), ve);
			} catch (Exception e) {
				errInfo.putErrorMessage(i, billvos[i], StringUtils.isBlank(e.getMessage()) ? "" : e.getMessage());
				Logger.error(e.getMessage(), e);
			}
		}

		return new PfProcessBatchRetObject(bill.toArray(new AggregatedValueObject[0]), errInfo);
	}

	public AggRegapplyVO[] doApprove(AggregatedValueObject[] vosin) throws BusinessException {
		AggRegapplyVO[] vos = new AggRegapplyVO[vosin.length];
		for (int i = 0; i < vosin.length; i++) {
			vos[i] = ((AggRegapplyVO) vosin[i]);
		}
		validate(vos);
		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			vos[i] = ((AggRegapplyVO) updateBill(vos[i], false));
		}
		return vos;
	}

	public AggregatedValueObject[] doCallBack(AggregatedValueObject[] vos) throws BusinessException {
		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			vos[i] = updateBill(vos[i], false);
		}
		return vos;
	}

	public AggRegapplyVO[] doCommit(AggRegapplyVO[] vos) throws BusinessException {
		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			RegapplyVO billvo = (RegapplyVO) vos[i].getParentVO();
			if (!isHasFile(billvo)) {
				throw new BusinessException(ResHelper.getString("6009reg", "06009reg0047"));
			}

			billvo.setApprove_state(Integer.valueOf(3));
			vos[i].setParentVO(billvo);
			vos[i] = ((AggRegapplyVO) updateBill(vos[i], false));
		}
		String pk_org = ((RegapplyVO) vos[0].getParentVO()).getPk_org();
		Integer approvetype = Integer.valueOf(1);
		try {
			approvetype = SysInitQuery.getParaInt(pk_org, (String) IHrPf.hashBillTypePara.get("6111"));
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		if ((approvetype != null) && (approvetype.intValue() == 0)) {

			String tempCode = "600712";
			HiSendMsgHelper.sendMessage1(tempCode, vos, pk_org);
		}

		return vos;
	}

	public AggRegapplyVO[] doDelete(AggRegapplyVO[] vos) throws BusinessException {
		IBillcodeManage billcodeMng = (IBillcodeManage) NCLocator.getInstance().lookup(IBillcodeManage.class);
		for (AggRegapplyVO vo : vos) {
			String billType = (String) vo.getParentVO().getAttributeValue("pk_billtype");
			String pk_group = (String) vo.getParentVO().getAttributeValue("pk_group");
			String pk_org = (String) vo.getParentVO().getAttributeValue("pk_org");
			String bill_code = (String) vo.getParentVO().getAttributeValue("bill_code");
			if (isAutoGenerateBillCode(billType, pk_group, pk_org)) {
				billcodeMng.returnBillCodeOnDelete(billType, pk_group, pk_org, bill_code, null);
			}
			deleteOldWorknote(vo);
			deleteBill(vo);
		}
		return vos;
	}

	public void doPerfromBill_RequiresNew(AggRegapplyVO aggVO) throws BusinessException {
		if (aggVO == null) {
			return;
		}
		RegapplyVO vo = (RegapplyVO) aggVO.getParentVO();
		if (vo.getTrialresult() == null) {
			throw new BusinessException(ResHelper.getString("6009reg", "06009reg0032"));
		}
		switch (vo.getTrialresult().intValue()) {
			case 1:
				updateTrialForPASS(vo);
				break;
			case 2:
				updateTrialForDelay(vo);
				break;
			case 3:
				updateTrialForFall(vo);
				break;
		}

		aggVO.getParentVO().setAttributeValue("approve_state", Integer.valueOf(102));
		getServiceTemplate().update(aggVO, false);
	}

	public Object doPush(AggRegapplyVO vo) throws BusinessException {
		return vo;
	}

	public void doPushBill_RequiresNew(AggRegapplyVO aggVO) throws BusinessException {
		HashMap<String, String> hashPara = new HashMap();
		hashPara.put("nosendmessage", "nosendmessage");
		((IplatFormEntry) NCLocator.getInstance().lookup(IplatFormEntry.class)).processAction("PUSH", "6111", null, aggVO, null, hashPara);
	}

	public AggregatedValueObject[] doUnapprove(AggregatedValueObject[] vos) throws BusinessException {
		List<String> regPKList = new ArrayList();
		for (int i = 0; i < vos.length; i++) {
			regPKList.add(((RegapplyVO) vos[i].getParentVO()).getPk_hi_regapply());
		}
		if (!regPKList.isEmpty()) {
			InSQLCreator isc = new InSQLCreator();
			String insql = isc.getInSQL((String[]) regPKList.toArray(new String[0]));
			String strCondition = "pk_hi_regapply in (" + insql + ")";
			RegapplyVO[] regApplyVOs =
					(RegapplyVO[]) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, RegapplyVO.class, strCondition);

			for (int i = 0; i < regApplyVOs.length; i++) {
				int approvestate = regApplyVOs[i].getApprove_state().intValue();
				if (approvestate == 102) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0209"));
				}
			}
		}
		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			vos[i] = updateBill(vos[i], false);
		}
		return vos;
	}

	public HashMap<String, Object> execBills(AggRegapplyVO[] billVOs, LoginContext context, boolean isRunBackgroundTask)
			throws BusinessException {
		HashMap<String, Object> result = new HashMap();
		StringBuffer sb = new StringBuffer();

		AggregatedValueObject[] retVOs = null;
		//tsy ∑¿ø’÷∏’Î
		if (!ArrayUtils.isEmpty(billVOs))//billVOs.length > 0
		{
			retVOs = validateBudget(billVOs, context);
		}
		//tsy ∑¿ø’÷∏’Î
		for (int i = 0; !ArrayUtils.isEmpty(billVOs) && i < billVOs.length; i++) {
			if (!isExit(retVOs, billVOs[i])) {

				sb.append(ResHelper.getString("6009reg", "06009reg0033", new String[] { (String) billVOs[i].getParentVO().getAttributeValue("bill_code") }));
			}
		}

		if ((retVOs == null) || (retVOs.length == 0)) {

			String msg = sb.length() == 0 ? "" : sb.toString();
			result.put("RESULT_MSG", isRunBackgroundTask ? msg : msg.replaceAll("<br>", "\n"));
			result.put("RESULT_BILLS", null);
			return result;
		}

		ArrayList<AggRegapplyVO> passBills = new ArrayList();
		IRegmngManageService regService = (IRegmngManageService) NCLocator.getInstance().lookup(IRegmngManageService.class);
		for (int i = 0; i < retVOs.length; i++) {

			try {
				regService.doPerfromBill_RequiresNew((AggRegapplyVO) retVOs[i]);
				passBills.add((AggRegapplyVO) retVOs[i]);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
				String billcode = (String) retVOs[i].getParentVO().getAttributeValue("bill_code");
				if (StringUtils.isBlank(e.getMessage())) {
					sb.append(i + 1 + ":" + ResHelper.getString("6009reg", "06009reg0034", new String[] { billcode, e.getMessage() }));

				} else if (e.getMessage().indexOf(billcode) < 0) {

					sb.append(i + 1 + ":" + ResHelper.getString("6009reg", "06009reg0034", new String[] { billcode, e.getMessage() }));

				} else {

					sb.append(i + 1 + ":" + e.getMessage());
				}

				continue;
			}

			try {
				AggRegapplyVO agg = queryByPk(retVOs[i].getParentVO().getPrimaryKey());
				regService.doPushBill_RequiresNew(agg);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}

		HashMap<String, ArrayList<AggRegapplyVO>> hmTrans = new HashMap();
		for (AggregatedValueObject bill : retVOs) {
			String pk_org = (String) bill.getParentVO().getAttributeValue("pk_org");
			if (hmTrans.get(pk_org) == null) {
				hmTrans.put(pk_org, new ArrayList());
			}
			((ArrayList) hmTrans.get(pk_org)).add((AggRegapplyVO) bill);
		}
		for (String key : hmTrans.keySet()) {
			if ((hmTrans.get(key) != null) && (((ArrayList) hmTrans.get(key)).size() > 0)) {

				String tempCode = "600703";
				HiSendMsgHelper.sendMessage1(tempCode, (HYBillVO[]) ((ArrayList) hmTrans.get(key)).toArray(new AggRegapplyVO[0]), key);
			}
		}

		String msg = sb.length() == 0 ? "" : sb.toString();
		result.put("RESULT_MSG", isRunBackgroundTask ? msg : msg.replaceAll("<br>", "\n"));
		result.put("RESULT_BILLS", passBills);
		return result;
	}

	public String getBillIdSql(int iBillStatus, String billType) throws BusinessException {
		String strWorkFlowWhere = getIHrPf().getBillIdSql(iBillStatus, billType);
		if (StringUtils.isNotBlank(strWorkFlowWhere)) {
			strWorkFlowWhere = "pk_hi_regapply in (" + strWorkFlowWhere + ") ";
		}
		return strWorkFlowWhere;
	}

	private String getFlowCode(String code, int i) throws BusinessException {
		Integer value = Integer.valueOf(code);
		return StringUtils.leftPad(value.intValue() + i + "", 5, '0');
	}

	private List<SuperVO> getHeadVO(Object objects) {
		List<SuperVO> headls = new ArrayList();
		if (((objects instanceof AggregatedValueObject[])) && (((AggregatedValueObject[]) objects).length >= 1)) {
			AggregatedValueObject[] objs = (AggregatedValueObject[]) objects;
			for (int i = 0; i < objs.length; i++) {
				headls.add((SuperVO) objs[i].getParentVO());
			}
		} else {
			AggregatedValueObject obj = (AggregatedValueObject) objects;
			headls.add((SuperVO) obj.getParentVO());
		}
		return headls;
	}

	private IHrBillCode getHrBillCode() {
		return (IHrBillCode) NCLocator.getInstance().lookup(IHrBillCode.class);
	}

	private IHrPf getIHrPf() {
		return (IHrPf) NCLocator.getInstance().lookup(IHrPf.class);
	}

	private IPersistenceRetrieve getIPersistenceRetrieve() {
		return (IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
	}

	private IPersistenceUpdate getIPersistenceUpdate() {
		return (IPersistenceUpdate) NCLocator.getInstance().lookup(IPersistenceUpdate.class);
	}

	private TrnRegItemVO getItemByItemkey(TrnRegItemVO[] itemvos, String itemKey) {
		for (int i = 0; (itemvos != null) && (i < itemvos.length); i++) {
			if (itemKey.equals(itemvos[i].getItemkey())) {
				return itemvos[i];
			}
		}
		return null;
	}

	private <T> T[] getLastVO(Class<T> className, String pk_psnorg, Integer assgid) throws BusinessException {
		String where = " pk_psnorg = '" + pk_psnorg + "' and lastflag = 'Y' ";
		if (assgid != null) {
			where = where + " and assgid = " + assgid.intValue();
		}
		return getServiceTemplate().queryByCondition(className, where);
	}

	private String getMsg(RegapplyVO billvo) throws BusinessException {
		SuperVO[] itemvos =
				TrnDelegator.getIItemSetQueryService().queryItemSetByOrg("8d246f75-552f-40e9-8688-0685a8a99a7d", billvo.getPk_group(), billvo.getPk_org(), billvo.getProbation_type());

		IBean ibean = BeanUtil.getBeanEntity("8d246f75-552f-40e9-8688-0685a8a99a7d");
		List<IItemSetAdapter> iitemadpls = BeanUtil.getBizImpObjFromVo(ibean, IItemSetAdapter.class, itemvos);
		for (IItemSetAdapter item : iitemadpls) {
			if ((item != null) && (!item.getItemkey().startsWith("old"))) {

				if ((item.getIsnotnull().booleanValue()) && (isNull(billvo.getAttributeValue(item.getItemkey())))) {
					return '\n' + billvo.getBill_code();
				}
			}
		}
		return "";
	}

	private IPersistenceUpdate getPersistenceUpdate() {
		return (IPersistenceUpdate) NCLocator.getInstance().lookup(IPersistenceUpdate.class);
	}

	private String getProbationType(Integer state) {
		switch (state.intValue()) {
			case 1:
				return ResHelper.getString("6009reg", "06009reg0035");
			case 2:
				return ResHelper.getString("6009reg", "06009reg0036");
		}
		return ResHelper.getString("6009reg", "06009reg0035");
	}

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (this.serviceTemplate == null) {
			this.serviceTemplate = new SimpleDocServiceTemplate("RegmngManage");
		}
		return this.serviceTemplate;
	}

	private String getTrialresult(Integer state) {
		if (state == null) {
			return "";
		}
		switch (state.intValue()) {
			case 1:
				return ResHelper.getString("6009reg", "06009reg0014");
			case 2:
				return ResHelper.getString("6009reg", "06009reg0037");
			case 3:
				return ResHelper.getString("6009reg", "06009reg0038");
		}
		return "";
	}

	private <T> T[] getValidVO(Class<T> className, String pk_psnorg, Integer assgid) throws BusinessException {
		String where = " pk_psnorg = '" + pk_psnorg + "' and endflag = 'N' ";
		if (assgid != null) {
			where = where + " and assgid = " + assgid.intValue();
		}
		return getServiceTemplate().queryByCondition(className, where);
	}

	private boolean hasFile(NCFileNode node) {
		if (node.getChildCount() <= 0) {
			return false;
		}

		for (int i = 0; i < node.getChildCount(); i++) {
			if (!((NCFileNode) node.getChildAt(i)).isFolder()) {
				return true;
			}
			if (hasFile((NCFileNode) node.getChildAt(i))) {
				return true;
			}
		}
		return false;
	}

	public <T extends AggregatedValueObject> T insertBill(T billvo) throws BusinessException {
		PfBusinessLock pfLock = null;
		IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());

		RegapplyVO head = (RegapplyVO) billvo.getParentVO();

		try {
			pfLock = new PfBusinessLock();
			pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));

			checkBillCodeRepeat(new AggregatedValueObject[] { billvo });

			DefaultValidationService vService = new DefaultValidationService();
			createCustomValidators(vService, "insert");
			SuperVO[] headvos = (SuperVO[]) getHeadVO(billvo).toArray(new SuperVO[0]);
			vService.validate(headvos);

			billvo.getParentVO().setStatus(2);
			setAuditInfoAndTs((SuperVO) billvo.getParentVO(), true);
			String pk = getMDPersistenceService().saveBill(billvo);
			billvo.getParentVO().setPrimaryKey(pk);
			if (isAutoGenerateBillCode(head.getPk_billtype(), head.getPk_group(), head.getPk_org())) {
				getHrBillCode().commitPreBillCode(head.getPk_billtype(), head.getPk_group(), head.getPk_org(), head.getBill_code());
			}

		} catch (BillCodeRepeatBusinessException be) {
			String[] codes = be.getRepeatCodes();
			if ((isAutoGenerateBillCode("6111", PubEnv.getPk_group(), flowItf.getPkorg())) && (codes != null)) {
				IBillcodeManage billcode = (IBillcodeManage) NCLocator.getInstance().lookup(IBillcodeManage.class);
				for (int i = 0; i < codes.length; i++) {
					try {
						billcode.AbandonBillCode_RequiresNew("6111", PubEnv.getPk_group(), flowItf.getPkorg(), codes[i]);
					} catch (Exception e2) {
						Logger.error(e2.getMessage(), e2);
					}
				}
			}
			throw be;

		} catch (Exception e) {

			if (isAutoGenerateBillCode(head.getPk_billtype(), PubEnv.getPk_group(), flowItf.getPkorg())) {
				((IHrBillCode) NCLocator.getInstance().lookup(IHrBillCode.class)).rollbackPreBillCode(head.getPk_billtype(), PubEnv.getPk_group(), flowItf.getPkorg(), flowItf.getBillNo());
			}

			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (pfLock != null) {

				pfLock.unLock();
			}
		}
		return billvo;
	}

	private boolean isAutoGenerateBillCode(String billType, String pk_group, String pk_org) throws BusinessException {
		BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(billType, pk_group, pk_org);
		return billCodeContext != null;
	}

	public boolean isEnableDrive(String srcBilltype, AggregatedValueObject srcBillVO, String srcAction, String destBillType, String beDrivedActionName)
			throws BusinessException {
		RegapplyVO billvo = (RegapplyVO) srcBillVO.getParentVO();
		if ("6115".equals(destBillType)) {

			if ((billvo.getTrialresult() != null) && (3 == billvo.getTrialresult().intValue())) {
				return true;
			}

			return false;
		}

		if ((billvo.getTrialresult() != null) && (1 == billvo.getTrialresult().intValue())) {
			return true;
		}

		return false;
	}

	private boolean isExit(AggregatedValueObject[] retVOs, AggRegapplyVO billVO) throws BusinessException {
		for (int i = 0; (retVOs != null) && (i < retVOs.length); i++) {
			if (billVO.getParentVO().getPrimaryKey().equals(retVOs[i].getParentVO().getPrimaryKey())) {
				return true;
			}
		}
		return false;
	}

	private boolean isHasFile(RegapplyVO bill) throws BusinessException {
		if ((bill.getIsneedfile() == null) || (!bill.getIsneedfile().booleanValue())) {

			return true;
		}

		IFileSystemService service = (IFileSystemService) NCLocator.getInstance().lookup(IFileSystemService.class);
		NCFileNode node = service.getNCFileNodeTreeAndCreateAsNeed(bill.getPk_hi_regapply(), PubEnv.getPk_user());
		return hasFile(node);
	}

	private boolean isNull(Object o) {
		if ((o == null) || (o.toString() == null) || (o.toString().trim().equals(""))) {
			return true;
		}
		return false;
	}

	public HashMap<String, Object> manualExecBills(AggRegapplyVO[] bills, LoginContext context, UFLiteralDate effectDate)
			throws BusinessException {
		if (!ArrayUtils.isEmpty(bills)) {
			for (int i = 0; i < bills.length; i++) {
				bills[i].getParentVO().setAttributeValue("regulardate", effectDate);
			}
		}
		HashMap<String, Object> result = execBills(bills, context, true);

		return result;
	}

	public AggRegapplyVO[] queryByCondition(LoginContext context, String condition) throws BusinessException {
		return (AggRegapplyVO[]) queryByCondition(context, AggRegapplyVO.class, condition);
	}

	public AggRegapplyVO queryByPk(String pk) throws BusinessException {
		return (AggRegapplyVO) queryByPk(AggRegapplyVO.class, pk);
	}

	private void sendMessage(ValidateResultVO[] resultVOs) throws BusinessException {
		for (ValidateResultVO vo : resultVOs) {
			if (!StringUtils.isEmpty(vo.getHintMsg())) {

				OrgVO org = ((IAOSQueryService) NCLocator.getInstance().lookup(IAOSQueryService.class)).queryHROrgByOrgPK(vo.getPk_org());
				String hrorg = org == null ? vo.getPk_org() : org.getPk_org();
				IHRMessageSend messageSendService = (IHRMessageSend) NCLocator.getInstance().lookup(IHRMessageSend.class);

				HRBusiMessageVO messageInfoVO = new HRBusiMessageVO();

				Hashtable<String, Object> value = new Hashtable();
				value.put("reason", vo.getHintMsg());
				messageInfoVO.setBusiVarValues(value);

				messageInfoVO.setBillVO(vo);
				String tempCode = "600301";
				messageInfoVO.setMsgrescode(tempCode);
				messageInfoVO.setPkorgs(new String[] { hrorg });

				messageSendService.sendBuziMessage_RequiresNew(messageInfoVO);
			}
		}
	}

	public AggregatedValueObject singleDirectApprove_RequiresNew(AggregatedValueObject aggvo, String pk_user, UFDateTime approveTime, String approveNote, int blPassed)
			throws BusinessException {
		IFlowBizItf itf = (IFlowBizItf) NCObject.newInstance(aggvo).getBizInterface(IFlowBizItf.class);

		if ((blPassed == 1) && (3 == itf.getApproveStatus().intValue())) {
			if (((RegapplyVO) aggvo.getParentVO()).getTrialresult() == null) {
				throw new BusinessException(ResHelper.getString("6009reg", "06009reg0030"));
			}
			if (StringUtils.isNotBlank(getMsg((RegapplyVO) aggvo.getParentVO()))) {
				throw new BusinessException(ResHelper.getString("6009reg", "06009reg0031"));
			}
		}

		changeBillData(itf, pk_user, approveTime, approveNote, Integer.valueOf(blPassed));

		WorkflownoteVO worknoteVO = buildWorkflownoteVO(itf, pk_user, approveNote, blPassed, itf.getBilltype());
		getIPersistenceUpdate().insertVO(null, worknoteVO, null);
		return (AggregatedValueObject) getServiceTemplate().update(aggvo, false);
	}

	public <T extends AggregatedValueObject> T updateBill(T billvo, boolean blChangeAuditInfo) throws BusinessException {
		PfBusinessLock pfLock = null;
		IFlowBizItf flowItf = getFlowBizItf(billvo.getParentVO());

		try {
			pfLock = new PfBusinessLock();
			pfLock.lock(new VOLockData(billvo, flowItf.getBilltype()), new VOConsistenceCheck(billvo, flowItf.getBilltype()));

			checkBillCodeRepeat(new AggregatedValueObject[] { billvo });

			DefaultValidationService vService = new DefaultValidationService();
			createCustomValidators(vService, "update");
			SuperVO[] headvos = (SuperVO[]) getHeadVO(billvo).toArray(new SuperVO[0]);
			vService.validate(headvos);

			billvo.getParentVO().setStatus(1);
			setAuditInfoAndTs((SuperVO) billvo.getParentVO(), blChangeAuditInfo);
			String pk = getMDPersistenceService().saveBill(billvo);
			billvo.getParentVO().setPrimaryKey(pk);
		} catch (BillCodeRepeatBusinessException be) {
			String[] codes = be.getRepeatCodes();
			if ((isAutoGenerateBillCode("6111", PubEnv.getPk_group(), flowItf.getPkorg())) && (codes != null)) {
				for (int i = 0; i < codes.length; i++) {
					try {
						((IBillcodeManage) NCLocator.getInstance().lookup(IBillcodeManage.class)).AbandonBillCode_RequiresNew("6111", PubEnv.getPk_group(), flowItf.getPkorg(), codes[i]);

					} catch (Exception e2) {
						Logger.error(e2.getMessage(), e2);
					}
				}
			}
			throw be;

		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (pfLock != null) {

				pfLock.unLock();
			}
		}
		return billvo;
	}

	private void updateTrialForDelay(RegapplyVO vo) throws BusinessException {
		if (vo == null) {
			return;
		}

		TrialVO[] trialVOs =
				(TrialVO[]) getIPersistenceRetrieve().retrieveByClause(null, TrialVO.class, "  endflag = 'N' and pk_psnorg ='" + vo.getPk_psnorg() + "' ");

		TrialVO trialVO = trialVOs[0];

		String[] updateFields = { "trialresult", "enddate" };
		trialVO.setAttributeValue(updateFields[0], vo.getTrialresult());
		trialVO.setAttributeValue(updateFields[1], vo.getTrialdelaydate());
		getPersistenceUpdate().updateVO(null, trialVO, updateFields, null);
	}

	private void updateTrialForFall(RegapplyVO vo) throws BusinessException {
		if (vo == null) {
			return;
		}

		TrialVO[] trialVOs = (TrialVO[]) getValidVO(TrialVO.class, vo.getPk_psnorg(), Integer.valueOf(1));

		if ((trialVOs != null) && (trialVOs.length > 0)) {
			TrialVO trialVO = trialVOs[0];

			trialVO.setTrialresult(vo.getTrialresult());
			trialVO.setEnddate(vo.getEnd_date());
			trialVO.setEndflag(UFBoolean.TRUE);
			getServiceTemplate().update(trialVO, false);
		}

		PsnJobVO jobVO = (PsnJobVO) queryByPk(PsnJobVO.class, vo.getPk_psnjob());
		String[] updateFields = { "trial_flag", "trial_type" };
		jobVO.setAttributeValue(updateFields[0], Boolean.valueOf(false));
		jobVO.setAttributeValue(updateFields[1], null);
		getPersistenceUpdate().updateVO(null, jobVO, updateFields, null);
	}

	private void updateTrialForPASS(RegapplyVO vo) throws BusinessException {
		if (vo == null) {
			return;
		}

		TrialVO[] trialVOs = (TrialVO[]) getValidVO(TrialVO.class, vo.getPk_psnorg(), Integer.valueOf(1));

		if ((trialVOs != null) && (trialVOs.length > 0)) {
			TrialVO trialVO = trialVOs[0];

			if (trialVO.getEnddate() == null) {
				trialVO.setEnddate(vo.getRegulardate());
			}
			trialVO.setTrialresult(vo.getTrialresult());
			trialVO.setEnddate(vo.getEnd_date());
			trialVO.setRegulardate(vo.getRegulardate());
			trialVO.setEndflag(UFBoolean.TRUE);
			getServiceTemplate().update(trialVO, false);
		}

		PsnJobVO newVO = createNewPsnjob(vo);
		newVO =
				((IPersonRecordService) NCLocator.getInstance().lookup(IPersonRecordService.class)).addNewPsnjob(newVO, vo.getIfsynwork() == null ? false : vo.getIfsynwork().booleanValue(), vo.getPk_org());
	}

	private void validate(AggRegapplyVO[] vos) throws BusinessException {
		String errMsg = "";
		for (AggRegapplyVO vo : vos) {
			RegapplyVO billvo = (RegapplyVO) vo.getParentVO();
			if (1 == billvo.getApprove_state().intValue()) {
				if (billvo.getTrialresult() == null) {
					errMsg = errMsg + ResHelper.getString("6009reg", "06009reg0030");

				} else if (StringUtils.isNotBlank(getMsg(billvo))) {
					errMsg = errMsg + ResHelper.getString("6009reg", "06009reg0031");
				}
			}
		}

		if (StringUtils.isNotBlank(errMsg)) {
			throw new BusinessException(errMsg);
		}
	}

	public AggregatedValueObject[] validateBudget(AggregatedValueObject[] vos, LoginContext context) throws BusinessException {
		Vector<AggregatedValueObject> passVO = new Vector();

		PsnJobVO[] oldPsnJobVOs = new PsnJobVO[vos.length];
		PsnJobVO[] newPsnJobVOs = new PsnJobVO[vos.length];
		for (int i = 0; i < vos.length; i++) {
			SuperVO bill = (SuperVO) vos[i].getParentVO();
			PsnJobVO psn =
					(PsnJobVO) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByPk(null, PsnJobVO.class, (String) bill.getAttributeValue("pk_psnjob"));

			oldPsnJobVOs[i] = ((PsnJobVO) psn.clone());

			String[] codes = { "newpk_job", "newpk_job_type", "newpk_jobgrade", "newpk_jobrank", "newpk_post", "newpk_postseries" };

			for (String name : bill.getAttributeNames()) {
				if (name.startsWith("new")) {
					Object value = bill.getAttributeValue(name);
					if ((value == null) && (!ArrayUtils.contains(codes, name))) {
						value = psn.getAttributeValue(name.substring(3));
					}
					psn.setAttributeValue(name.substring(3), value);
				}
			}
			psn.setAttributeValue("pk_psnjob", "");
			newPsnJobVOs[i] = psn;
			passVO.add(vos[i]);
		}

		ValidateResultVO[] resultVOs =
				((IOrgBudgetQueryService) NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)).validateBudgetValue_RequiresNew(context, oldPsnJobVOs, newPsnJobVOs);

		if ((resultVOs == null) || (resultVOs.length == 0)) {
			return vos;
		}

		sendMessage(resultVOs);

		for (ValidateResultVO resultVO : resultVOs) {

			if (!resultVO.isValid()) {
				String pk_org = resultVO.getPk_org();

				for (AggregatedValueObject vo : vos) {
					if (pk_org.equals(vo.getParentVO().getAttributeValue("newpk_org"))) {
						passVO.removeElement(vo);
					}
				}
			}
		}
		return (AggregatedValueObject[]) passVO.toArray(new AggregatedValueObject[0]);
	}

	public String validateValidBudget(LoginContext context, AggregatedValueObject[] billvos) throws BusinessException {
		PsnJobVO[] oldPsnJobVOs = new PsnJobVO[billvos.length];
		PsnJobVO[] newPsnJobVOs = new PsnJobVO[billvos.length];

		for (int i = 0; i < billvos.length; i++) {
			SuperVO bill = (SuperVO) billvos[i].getParentVO();
			PsnJobVO psn =
					(PsnJobVO) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByPk(null, PsnJobVO.class, (String) bill.getAttributeValue("pk_psnjob"));

			oldPsnJobVOs[i] = ((PsnJobVO) psn.clone());

			String[] codes = { "newpk_job", "newpk_job_type", "newpk_jobgrade", "newpk_jobrank", "newpk_post", "newpk_postseries" };

			for (String name : bill.getAttributeNames()) {
				if (name.startsWith("new")) {
					Object value = bill.getAttributeValue(name);
					if ((value == null) && (!ArrayUtils.contains(codes, name))) {
						value = psn.getAttributeValue(name.substring(3));
					}
					psn.setAttributeValue(name.substring(3), value);
				}
			}
			newPsnJobVOs[i] = psn;
			newPsnJobVOs[i].setPk_psnjob("");
		}
		ValidateResultVO[] resultVOs = null;
		if ((oldPsnJobVOs.length != 0) && (oldPsnJobVOs.length != 0)) {

			resultVOs =
					((IOrgBudgetQueryService) NCLocator.getInstance().lookup(IOrgBudgetQueryService.class)).validateBudgetValue(context, oldPsnJobVOs, newPsnJobVOs);
		}

		String strWarningMsg = "";

		if (resultVOs != null) {
			for (ValidateResultVO resultVO : resultVOs) {

				if ((resultVO.getHintMsg() != null) && (resultVO.isValid())) {
					strWarningMsg = strWarningMsg + "\n" + resultVO.getHintMsg();
				} else if (resultVO.getHintMsg() != null) {
					throw new BusinessException(resultVO.getHintMsg());
				}
			}
		}

		return strWarningMsg;
	}
}