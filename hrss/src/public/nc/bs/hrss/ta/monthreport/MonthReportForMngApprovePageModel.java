package nc.bs.hrss.ta.monthreport;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.cata.ICatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.cata.TestCatagoryDataProvider;
import nc.bs.hrss.pub.advpanel.mngdept.MngDeptPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.bs.hrss.pub.pf.PFUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.ta.utils.TAUtil;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.bs.hrss.ta.utils.TaAppContextUtil;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.uap.ctrl.tpl.bill.gen.BillTemplateConst;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.WebContext;
import nc.uap.lfw.core.cmd.UifDatasetLoadCmd;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.utils.WfmTaskUtil;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.timerule.TimeRuleVO;

/**
 * Ա�������±�PageModel
 * 
 * @author liuhongd
 * 
 */
public class MonthReportForMngApprovePageModel extends AdvancePageModel {

	@Override
	protected LfwWindow createPageMeta() {
		// ��������
		String billId = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(UifDatasetLoadCmd.OPEN_BILL_ID);
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_BillID, billId);
//		WebContext contex = LfwRuntimeEnvironment.getWebContext();
//		String i1 = WfmTaskUtil.getTaskPkFromSession();
//		String i2 = WfmTaskUtil.getTaskPkFromUrlParams();
//		IPFWorkflowQry workflowQry = NCLocator.getInstance().lookup(IPFWorkflowQry.class);
//		workflowQry.queryw
		// �����Ƿ�ɱ༭��ʶ
		String billEditable = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(PFUtil.FLAG_BILL_EDITABLED);
		AppUtil.addAppAttr(PFUtil.FLAG_BILL_EDITABLED, billEditable);

		// ��������
		String billType = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(HrssConsts.BILL_TYPE_CODE);
		AppUtil.addAppAttr(HrssConsts.BILL_TYPE_CODE, billType);

		// ��������
		String flowTypePk = LfwRuntimeEnvironment.getWebContext().getAppSession().getOriginalParameter(BillTemplateConst.BILLTYPE);
		if (!StringUtils.isEmpty(flowTypePk)) {
			try {
				flowTypePk = URLDecoder.decode(flowTypePk, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LfwLogger.error(e.getMessage(), e);
			}
		}
		AppUtil.addAppAttr(WfmConstants.WfmAppAttr_FolwTypePk, flowTypePk);

		LfwWindow pm = super.createPageMeta();
		return pm;
	}

	@Override
	protected String getFunCode() {
		return "E20400911";
	}

	@Override
	protected void initPageMetaStruct() {
		SessionUtil.getAppSession().setAttribute(ICatagoryDataProvider.SID_CATAGORY_PROVIDER, TestCatagoryDataProvider.class.getName());
		//
		super.initPageMetaStruct();
		// ��applicationContext��������Կ��ڵ����Ϳ��ڹ���
		TaAppContextUtil.addTaAppContext();
		Map<String, String[]> periodMap = TBMPeriodUtil.getPeriodMap(TaAppContextUtil.getHROrg());
		TaAppContextUtil.setTBMPeriodVOMap(periodMap);
		// ����С��λ��
		setPrecision();
	}

	/**
	 * ���ݿ��ڹ��������ֶα���С��λ��
	 * 
	 */
	private void setPrecision() {
		TimeRuleVO timeRuleVO = TaAppContextUtil.getTimeRuleVO();
		if (timeRuleVO == null) {
			return;
		}
		String[] fields =
				new String[] { MonthWorkVO.ACTUALWORKDAYS, MonthWorkVO.ACTUALWORKHOURS, MonthWorkVO.WORKDAYS, MonthWorkVO.WORKHOURS };
		Integer mreportdecimal = timeRuleVO.getMreportdecimal();
		LfwView view = getPageMeta().getView("MonthReportDetail");
		Dataset dsMthDetail = ViewUtil.getDataset(view, "dsMthDetail");
		TAUtil.setPrecision(dsMthDetail, mreportdecimal, fields);
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected String getRightPage() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[] { new CanvasPanel(), new MngDeptPanel(), new SimpleQueryPanel() };
	}

}
