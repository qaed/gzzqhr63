package nc.bs.hrss.ta.monthreport.cmd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import nc.bs.hrss.pub.cmd.PFApproveInfoCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.trm.train.DemandUtil;
import nc.hr.utils.ResHelper;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.md.innerservice.MDQueryService;
import nc.md.model.IBusinessEntity;
import nc.md.model.MetaDataException;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.MdDataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.log.LfwLogger;
import nc.uap.wfm.ncworkflow.cmd.NCPfUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.trm.demandinv.DemandinvVOConst;

import org.apache.commons.lang.StringUtils;

import uap.web.bd.pub.AppUtil;

public class MonthstatPFApproveInfoCmd extends PFApproveInfoCmd {
	public MonthstatPFApproveInfoCmd(String dsID, Class<? extends AggregatedValueObject> aggVOClass) {
		super(dsID, aggVOClass);
	}

	protected void run() {
		Dataset ds = getDataset();
		if (ds == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0167"), ResHelper.getString("c_pub-res", "0c_pub-res0010") + getDsID() + "!");
		}

		Row selRow = ds.getAllRow()[0];
		if (selRow == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0174"), ResHelper.getString("c_pub-res", "0c_pub-res0011"));
		}

		AggregatedValueObject aggVO = NCPfUtil.getWfmAggVO();
		IFlowBizItf flowItf = getIFlowBizItf(ds, aggVO);
		if (flowItf == null) {
			CommonUtil.showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0174"), ResHelper.getString("c_pub-res", "0c_pub-res0012"));
		}

		String billtype = StringUtils.isBlank(flowItf.getTranstype()) ? flowItf.getBilltype() : flowItf.getTranstype();
		if (!StringUtils.isEmpty(billtype)) {
			try {
				billtype = URLDecoder.decode(billtype, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LfwLogger.error(e.getMessage(), e);
			}
		}
		AppUtil.addAppAttr("$$$$$$$$FLOWTYPEPK", billtype.trim());
		AppUtil.addAppAttr("billId", flowItf.getBillId());
		CommonUtil.showWindowDialog("pfinfo", NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pub-res", "0c_pub-res0014"), "850", "700", null, "TYPE_DIALOG");
	}

	protected AggregatedValueObject getBillVOByPk(String primaryKey) {
		Dataset ds = getDataset();
		Row selRow = ds.getSelectedRow();
		primaryKey = selRow.getString(ds.nameToIndex("pk_demandinv"));
		AggregatedValueObject aggVO = null;

		Integer invLevel = (Integer) AppUtil.getAppAttr("invLevel");
		String pk = null;
		if (invLevel == DemandinvVOConst.OBJTYPE_PSN) {
			pk = SessionUtil.getPk_psndoc();
		} else {
			pk = (String) AppUtil.getAppAttr("pk_dept");
		}
		aggVO = DemandUtil.queryDemandApplyByInvId(invLevel, primaryKey, pk);
		return aggVO;
	}

	private IFlowBizItf getIFlowBizItf(Dataset ds, Object containmentObject) {
		if (StringUtils.isEmpty(((MdDataset) ds).getObjMeta())) {
			return null;
		}
		IBusinessEntity businessEntity = null;
		try {
			businessEntity = MDQueryService.lookupMDQueryService().getBusinessEntityByFullName(((MdDataset) ds).getObjMeta());
		} catch (MetaDataException e) {
			new HrssException(e).deal();
		}

		if (!businessEntity.isImplementBizInterface(IFlowBizItf.class.getName())) {
			return null;
		}
		IFlowBizItf flowItf = (IFlowBizItf) NCObject.newInstance(containmentObject).getBizInterface(IFlowBizItf.class);
		return flowItf;
	}
}