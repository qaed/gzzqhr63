package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.ui.hrwa.wa_ba_unit.ace.view.WaBaUnitSearchPsnWizardStep;
import nc.ui.hrwa.wa_ba_unit.ace.view.WaBaUnitSelectPsnWizardStep;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.vo.bm.data.BmDataVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitBVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

@SuppressWarnings("restriction")
public class WaBaUnitBatchAddAction extends WaBaUnitWizardAction {
	private static final long serialVersionUID = 1L;

	//	BmClassVO[] classVOs = null;
	//
	//	public BmClassVO[] getClassVOs() {
	//		return this.classVOs;
	//	}
	//
	//	public void setClassVOs(BmClassVO[] classVOs) {
	//		this.classVOs = classVOs;
	//	}

	public WaBaUnitBatchAddAction() {
		setCode("BatchAdd");
		setBtnName(ResHelper.getString("60150bmfile", "060150bmfile0000"));

		//		putValue("AcceleratorKey", KeyStroke.getKeyStroke(78, 10));

		putValue("ShortDescription", ResHelper.getString("60150bmfile", "060150bmfile0054"));
	}

	public void doAction(ActionEvent e) throws Exception {
		//		this.classVOs = BMDelegator.getBmfileQueryService().queryBmClass(getBmLoginContext(), false, true);
		//		if ((this.classVOs == null) || (this.classVOs.length == 0)) {
		//			throw new BusinessException(ResHelper.getString("60150bmfile", "060150bmfile0038"));
		//		}

		WizardDialog wizardDialog = new WizardDialog(getModel().getContext().getEntranceUI(), getWizardModel(), getSteps(), null);

		wizardDialog.setWizardDialogListener(this);
		wizardDialog.setResizable(true);
		wizardDialog.setSize(new Dimension(800, 560));
		if (1 != wizardDialog.showModal()) {
			setCancelMsg();
			return;
		}
	}

	protected List<WizardStep> getSteps() {
		List<WizardStep> list = new ArrayList<WizardStep>();
		list.add(new WaBaUnitSearchPsnWizardStep(getBtnName(), getModel()));
		list.add(new WaBaUnitSelectPsnWizardStep(getLoginContext()));
		//		list.add(new SetClassInfoWizardStep(getBmLoginContext(), this.classVOs));
		return list;
	}

	public void doProcess(BmDataVO[] psndocvos) throws BusinessException {
		if (psndocvos == null || psndocvos.length == 0) {
			return;
		}
		//组装AggVO
		AggWaBaUnitHVO aggvo = new AggWaBaUnitHVO();
		//组装HVO
		WaBaUnitHVO hvo = new WaBaUnitHVO();
		hvo.setCreationtime(new UFDateTime());
		hvo.setCreator(getLoginContext().getPk_loginUser());
		hvo.setPk_group(getLoginContext().getPk_group());
		hvo.setPk_org(getLoginContext().getPk_org());
		hvo.setBa_unit_type("批量新增");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String name = sdf.format(new Date());
		hvo.setName(name);
		hvo.setCode(name);
		aggvo.setParentVO(hvo);
		//组装BVO
		List<WaBaUnitBVO> bodyvos = new ArrayList<WaBaUnitBVO>();
		for (BmDataVO psndocVO : psndocvos) {
			WaBaUnitBVO bvo = new WaBaUnitBVO();
			bvo.setPk_psndoc(psndocVO.getPk_psndoc());
			//添加pk_psnjob工作主键
			bvo.setPk_psnjob(psndocVO.getPk_psnjob());
			bvo.setDr(0);
			bodyvos.add(bvo);
		}
		aggvo.setChildrenVO(bodyvos.toArray(new WaBaUnitBVO[0]));
		//进行保存
		IWaBaUnitMaintain maintain = NCLocator.getInstance().lookup(IWaBaUnitMaintain.class);
		maintain.insert(aggvo);
	}
}
