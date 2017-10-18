package com.yonyou.portal.hrss.wabasch.wfm;

import nc.bs.framework.common.NCLocator;
import nc.uap.cpb.baseservice.IUifCpbService;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.constants.AppConsts;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.context.WfmFlowInfoCtx;
import nc.uap.wfm.dftimpl.DefaultFormOper;
import nc.uap.wfm.engine.TaskProcessUI;
import nc.uap.wfm.model.Task;
import nc.uap.wfm.vo.WfmFlwTypeVO;
import nc.uap.wfm.vo.WfmFormInfoCtx;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.SuperVO;

public class WfmFlwFormOper extends DefaultFormOper {
	public WfmFormInfoCtx save(WfmFormInfoCtx frmInfoCtx, WfmFlowInfoCtx flwInfoCtx) {
		//������PK�����ڵ�ʱ�򷢻�����
		if (frmInfoCtx == null) {
			return null;
		}
		try {
			IUifCpbService cpbService = NCLocator.getInstance().lookup(IUifCpbService.class);
			cpbService.insertOrUpdateSuperVO((SuperVO) frmInfoCtx, false);
			((SuperVO) frmInfoCtx).setStatus(VOStatus.UPDATED);
			/*
			�˴��迪���������߼��� ���ӱ�״̬��ΪUPDATED�����ӱ���ʹ��forѭ���������ӱ�״̬��ΪUPDATE
			����ο���������״̬��UPDATE��д��
			*/

		} catch (BusinessException e) {
			LfwLogger.error(e.getMessage(), e);
			throw new LfwRuntimeException(e.getMessage());
		}

		return frmInfoCtx;
	}

	public WfmFormInfoCtx update(WfmFormInfoCtx frmInfoCtx, WfmFlowInfoCtx flwInfoCtx) {
		if (frmInfoCtx == null) {
			return null;
		}

		try {
			IUifCpbService cpbService = NCLocator.getInstance().lookup(IUifCpbService.class);
			cpbService.updateSuperVO((SuperVO) frmInfoCtx, false);
		} catch (BusinessException e) {
			LfwLogger.error(e.getMessage(), e);
			throw new LfwRuntimeException(e.getMessage());
		}
		return frmInfoCtx;
	}

	public Class<WfmFormInfoCtx>[] getBizMetaDataDesc(WfmFlwTypeVO flowTypeVo) {
		return new Class[] { nc.vo.wa.wa_ba.sch.WaBaSchHVO.class };
	}

	@Override
	public WfmFormInfoCtx getWfmFormInfoCtx(String pk_frmins, String pk_flwtype) {
		return null;
	}

	@Override
	public TaskProcessUI getHanlderUrlByTask(Task task) {
		if (task == null)
			return null;
		String url = "/app/hrss_wabasch/com.yonyou.portal.hrss.WabaschComps.wabasch_cardwin";
		if (url == null || url.length() == 0) {
			throw new LfwRuntimeException("�޷��ҵ���Ӧ��URL");
		}
		url =
				url + "&" + WfmConstants.WfmUrlConst_TaskPk + "=" + task.getPk_task() + "&${forminspk}=" + task.getPk_frmins() + "&" + AppConsts.OPE_SIGN + "=" + AppConsts.OPE_EDIT;
		TaskProcessUI processUI = new TaskProcessUI();
		processUI.setUrl(LfwRuntimeEnvironment.getRootPath() + "/" + url);
		String title = task.getTitile();
		if ("".equals(title) || title == null) {
			title = "hrss_wabasch";
		}
		processUI.setTitle(title);
		return processUI;
	}

}