package nc.ui.om.hrdept.validator;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.IValidationService;
import nc.bs.uif2.validation.ValidationException;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.ResHelper;
import nc.itf.om.IDeptQueryService;
import nc.ui.om.pub.GenericBillVOHelper;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.trade.business.HYPubBO_Client;
import nc.ui.uif2.editor.BillForm;
import nc.uif.pub.exception.UifException;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.orginfo.HRCorpVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class DeptInsertUpdateValidator implements IValidationService {
	public DeptInsertUpdateValidator() {
	}

	BillForm billForm = null;

	public BillForm getBillForm() {
		return this.billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

	private UIRefPane getHeadRefPane(String itemName) {
		return (UIRefPane) getBillForm().getBillCardPanel().getHeadItem(itemName).getComponent();
	}

	public void validate(Object obj) throws ValidationException {
		if (obj == null) {
			return;
		}
		AggHRDeptVO aggDeptVO = (AggHRDeptVO) obj;
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();

		UFLiteralDate createDate = deptVO.getCreatedate();
		DeptHistoryVO[] dhVOs =
				(DeptHistoryVO[]) GenericBillVOHelper.getBodyValueVOs(getBillForm().getBillCardPanel(), "depthistory", DeptHistoryVO.class);

		for (DeptHistoryVO dhVO : dhVOs) {
			if ((!"1".equals(dhVO.getChangetype())) && (createDate.after(dhVO.getEffectdate()))) {
				throw newValidationException(ResHelper.getString("6005dept", "16005dept0008"));
			}
		}

		if (StringUtils.isEmpty(deptVO.getPk_fatherorg())) {
			return;
		}
		AggHRDeptVO fatherAggVO = null;
		try {
			fatherAggVO = ((IDeptQueryService) NCLocator.getInstance().lookup(IDeptQueryService.class)).queryByPk(deptVO.getPk_fatherorg());
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		if ((fatherAggVO != null) && (((HRDeptVO) fatherAggVO.getParentVO()).getHrcanceled().booleanValue())) {
			throw newValidationException(ResHelper.getString("6005dept", "06005dept0176"));
		}
		if ((deptVO.getPk_dept() != null) && (deptVO.getPk_dept().equals(deptVO.getPk_fatherorg()))) {
			throw newValidationException(ResHelper.getString("6005dept", "06005dept0177"));
		}
		try {
			if (HYPubBO_Client.queryByCondition(HRDeptVO.class, " code = '" + deptVO.getCode() + "' and pk_dept <> '" + deptVO.getPk_dept() + "'").length != 0) {
				throw newValidationException("下列字段值已存在，不允许重复，请检查：\n[编码："+deptVO.getCode()+"]");
			}
			if (HYPubBO_Client.queryByCondition(HRCorpVO.class, " code = '" + deptVO.getCode() + "'").length != 0) {
				throw newValidationException("部门编码不允许与组织编码重复，请检查：\n[编码："+deptVO.getCode()+"]");
			}
		} catch (UifException e) {
//			throw newValidationException("数据异常，请刷新后重试");
		}

	}

	private ValidationException newValidationException(String msg) {
		ValidationFailure failure = new ValidationFailure(msg);
		List<ValidationFailure> failureList = new ArrayList();
		failureList.add(failure);
		ValidationException e = new ValidationException(failureList);
		return e;
	}
}