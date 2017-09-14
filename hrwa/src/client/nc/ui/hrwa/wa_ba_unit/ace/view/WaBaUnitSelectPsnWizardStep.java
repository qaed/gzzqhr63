package nc.ui.hrwa.wa_ba_unit.ace.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillModelCellEditableController;
import nc.vo.bm.data.BmDataVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class WaBaUnitSelectPsnWizardStep extends WizardStep implements IWizardStepListener {
	int sum = 0;

	LoginContext context = null;

	BmDataVO[] vos = null;
	//
	Map<String, BmDataVO> vosMap = new HashMap();
	//
	Map<String, BmDataVO> selectVosMap = new HashMap();
	//
	//	BmClassVO[] classVOs = null;
	//
	//	public BmClassVO[] getClassVOs() {
	//		return this.classVOs;
	//	}
	//
	//	public void setClassVOs(BmClassVO[] classVOs) {
	//		this.classVOs = classVOs;
	//	}

	private SelecttableBillPanel psnBillScrollPane = null;

	public WaBaUnitSelectPsnWizardStep(LoginContext context1) {
		setTitle(ResHelper.getString("60150bmfile", "060150bmfile0035"));

		setDescription(ResHelper.getString("60150bmfile", "060150bmfile0033"));

		this.context = context1;
		//		this.classVOs = classVOs1;
		setComp(getPsnBillScrollPane());
		initTableModel();
		addListener(this);
	}

	public void stepActived(WizardStepEvent event) throws WizardStepException {
		String whereSql = "";
		//		String powerSql = BmPowerSqlHelper.getRefPowerSql(getLoginContext().getPk_group(), "6007psnjob", "bmdefault", "hi_psnjob");
		//		if (getModel().getAttr("whereSql") != null) {
		whereSql = getModel().getAttr("whereSql").toString();

		//			if (!StringUtils.isBlank(powerSql)) {
		//				whereSql = whereSql + " and " + powerSql;
		//			}
		//		} else {
		//			whereSql = powerSql;
		//		}
		try {
			//			IPsndocQueryService psndocquery = NCLocator.getInstance().lookup(IPsndocQueryService.class);
			//			this.vos = psndocquery.queryPsndocVOsByCondition(whereSql);
			//			this.vos = queryPsnForAdd(whereSql);
			//			this.vos = BMDelegator.getBmfileQueryService().queryPsnForAdd(getLoginContext(), whereSql);
			//
			IWaBaUnitMaintain maintain = NCLocator.getInstance().lookup(IWaBaUnitMaintain.class);
			this.vos = maintain.queryPsnForAdd(whereSql);
			for (BmDataVO vo : this.vos) {
				this.vosMap.put(vo.getPk_psndoc(), vo);
			}
			getPsnBillScrollPane().getTableModel().setBodyDataVO(this.vos);
			getPsnBillScrollPane().getTableModel().setCellEditableController(new BillModelCellEditableController() {
				public boolean isCellEditable(boolean blIsEditable, int iRowIndex, String strItemKey) {
					return WaBaUnitSelectPsnWizardStep.this.isBmClassCellEditable(blIsEditable, iRowIndex, strItemKey);
				}
			});

			if ((!ArrayUtils.isEmpty(this.vos)) && (this.selectVosMap != null) && (this.selectVosMap.size() > 0)) {
				for (int i = 0; i < this.vos.length; i++) {
					String pk_psndoc = this.vos[i].getPk_psndoc();
					if (this.selectVosMap.get(pk_psndoc) != null) {
						HashMap<String, Object> map = ((BmDataVO) this.selectVosMap.get(pk_psndoc)).appValueHashMap;
						String[] keys = (String[]) map.keySet().toArray(new String[0]);
						for (int j = 0; j < keys.length; j++) {
							if ((null != map.get(keys[j])) && (null != this.vos[i].appValueHashMap.get(keys[j])) && (!map.get(keys[j]).equals(this.vos[i].appValueHashMap.get(keys[j])))) {
								map.put(keys[j], this.vos[i].appValueHashMap.get(keys[j]));
							}
						}
						this.vos[i] = ((BmDataVO) this.selectVosMap.get(pk_psndoc));
					}
				}
			}

			getPsnBillScrollPane().getTableModel().setBodyDataVO(this.vos);
		} catch (BusinessException e) {
			throw new WizardStepException(e);
		}

		if ((this.selectVosMap != null) && (this.selectVosMap.size() > 0)) {
			return;
		}
		getPsnBillScrollPane().setSelectedRowCode("selectflag");
		getPsnBillScrollPane().selectAllRows();
	}

	public void stepDisactived(WizardStepEvent event) throws WizardStepException {
		getModel().putAttr("selectedPsn", getSelectedPsndocVOs());
	}

	/**
	 * 获取选择的psnVO
	 * 
	 * @return
	 */
	public BmDataVO[] getSelectedPsndocVOs() {
		List<BmDataVO> selectVoList = new ArrayList();
		int rowCount = getPsnBillScrollPane().getTable().getRowCount();
		if (rowCount <= 0) {
			return null;
		}
		Object obj = null;
		for (int i = 0; i < rowCount; i++) {
			if (getPsnBillScrollPane().isSelected(i)) {
				boolean needadd = false;
				BmDataVO vo = (BmDataVO) getPsnBillScrollPane().getTableModel().getBodyValueRowVO(i, BmDataVO.class.getName());

				//				for (int j = 0; j < this.classVOs.length; j++) {
				//					obj = ((BmDataVO) this.vosMap.get(vo.getPk_psndoc())).getAttributeValue(this.classVOs[j].getPk_bm_class());
				//					if ("Y".equals(obj)) {
				//						vo.setAttributeValue(this.classVOs[j].getPk_bm_class(), "N");
				//					} else if ("Y".equals(vo.getAttributeValue(this.classVOs[j].getPk_bm_class()))) {
				//						needadd = true;
				//					}
				//				}
				//				if (needadd)
				selectVoList.add(vo);
			}
		}
		for (int i = 0; i < selectVoList.size(); i++) {
			this.selectVosMap.put(((BmDataVO) selectVoList.get(i)).getPk_psndoc(), selectVoList.get(i));
		}
		return (BmDataVO[]) selectVoList.toArray(new BmDataVO[selectVoList.size()]);
	}

	public void validate() throws WizardStepValidateException {
		super.validate();
		Object[] selectVOs = getPsnBillScrollPane().getSelectedBodyVOs(BmDataVO.class);
		if (ArrayUtils.isEmpty(selectVOs)) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60150bmfile", "060150bmfile0014"), ResHelper.getString("60150bmfile", "060150bmfile0015"));

			throw e;
		}
		int count = 0;

		count = getSelectedPsndocVOs().length;
		if (count == 0) {
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60150bmfile", "060150bmfile0014"), ResHelper.getString("60150bmfile", "060150bmfile0060"));

			throw e;
		}

		BmDataVO[] selectedVOs = getSelectedPsndocVOs();
		StringBuffer sbd = new StringBuffer();
		Map<String, String> map = new HashMap();
		if (!ArrayUtils.isEmpty(selectedVOs)) {
			for (int i = 0; i < selectedVOs.length; i++) {
				if (map.containsKey(selectedVOs[i].getPk_psndoc())) {
					sbd.append("\n" + selectedVOs[i].getPsncode() + " " + selectedVOs[i].getPsnname());
				}
				map.put(selectedVOs[i].getPk_psndoc(), selectedVOs[i].getPk_psndoc());
			}
			if (StringUtils.isNotBlank(sbd.toString())) {
				throw new RuntimeException(ResHelper.getString("60150bmfile", "060150bmfile0070") + sbd.toString());
			}
		}
	}

	private void initTableModel() {
		try {
			String[] saBodyColName =
					{ ResHelper.getString("common", "UC000-0004044"), ResHelper.getString("60150bmfile", "060150bmfile0017"), ResHelper.getString("common", "UC000-0001403"), ResHelper.getString("60150bmfile", "160150bmfile0002"), ResHelper.getString("common", "UC000-0004064"), ResHelper.getString("common", "UC000-0001653"), ResHelper.getString("common", "UC000-0000140"), "pk_psndoc", "pk_psnjob", "pk_psnorg", "assgid", "workorg", "workorgvid", "workdept", "workdeptvid" };

			String[] saBodyColKeyName =
					{ "selectflag", "clerkcode", "psnName", "orgname", "deptname", "postname", "psnclname", "pk_psndoc", "pk_psnjob", "pk_psnorg", "assgid", "workorg", "workorgvid", "workdept", "workdeptvid" };

			List<BillItem> bodyList = new ArrayList();
			BillItem biaBody = null;

			for (int i = 0; i < saBodyColName.length; i++) {
				biaBody = new BillItem();
				biaBody.setName(saBodyColName[i]);
				biaBody.setKey(saBodyColKeyName[i]);
				biaBody.setWidth(100);
				biaBody.setNull(false);
				biaBody.setEnabled(false);
				biaBody.setEdit(false);
				biaBody.setDataType(0);
				if ("selectflag".equals(saBodyColKeyName[i])) {
					biaBody.setWidth(70);
					biaBody.setEdit(true);
					biaBody.setEnabled(true);
					biaBody.setDataType(4);
				}
				if (i > 6) {
					biaBody.setShow(false);
				}
				bodyList.add(biaBody);
			}

			//			for (int i = 0; i < this.classVOs.length; i++) {
			//				biaBody = new BillItem();
			//				biaBody.setName(this.classVOs[i].getMultilangName());
			//				biaBody.setKey(this.classVOs[i].getPk_bm_class());
			//				biaBody.setWidth(60);
			//				biaBody.setDataType(4);
			//				biaBody.setEdit(true);
			//				bodyList.add(biaBody);
			//			}
			BillModel billModel = new BillModel();
			billModel.setBodyItems((BillItem[]) bodyList.toArray(new BillItem[0]));
			getPsnBillScrollPane().setTableModel(billModel);
			getPsnBillScrollPane().setSelectRowCode("selectflag");
			getPsnBillScrollPane().setRowNOShow(true);

			Component[] comps = getPsnBillScrollPane().getTable().getHeaderPopupMenu().getComponents();
			for (int i = 0; (null != comps) && (i < comps.length); i++) {
				if ((comps[i] instanceof JMenuItem)) {
					JMenuItem jpm = (JMenuItem) comps[i];
					if ((null != jpm.getLabel()) && (jpm.getLabel().equals(NCLangRes.getInstance().getStrByID("_Bill", "UPP_Bill-000009")))) {

						getPsnBillScrollPane().getTable().getHeaderPopupMenu().remove(comps[i]);
					}
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	boolean isBmClassCellEditable(boolean blIsEditable, int iRowIndex, String strItemKey) {
		if ((strItemKey.equals("clerkcode")) || (strItemKey.equals("psnName")) || (strItemKey.equals("orgname")) || (strItemKey.equals("deptname")) || (strItemKey.equals("postname")) || (strItemKey.equals("psnclname"))) {

			return false;
		}

		String pk_psndoc = getPsnBillScrollPane().getTableModel().getValueObjectAt(iRowIndex, "pk_psndoc").toString();

		Object obj = ((BmDataVO) this.vosMap.get(pk_psndoc)).getAttributeValue(strItemKey);
		if ((obj != null) && (strItemKey.length() == 20)) {
			return !UFBoolean.valueOf(obj.toString()).booleanValue();
		}
		return true;
	}

	public SelecttableBillPanel getPsnBillScrollPane() {
		if (this.psnBillScrollPane == null) {
			this.psnBillScrollPane = new SelecttableBillPanel();
			this.psnBillScrollPane.setName("psnBillScrollPane");
		}
		return this.psnBillScrollPane;
	}

	private LoginContext getLoginContext() {
		return this.context;
	}

}
