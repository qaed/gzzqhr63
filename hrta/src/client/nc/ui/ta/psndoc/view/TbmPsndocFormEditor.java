package nc.ui.ta.psndoc.view;

import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.SwingUtilities;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.org.IPrimaryOrgQry;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.hr.uif2.view.PrimaryOrgPanel;
import nc.ui.org.ref.OrgVOsDefaultRefModel;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillData;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.ta.psndoc.model.TbmPsndocAppModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.cache.CacheManager;
import nc.vo.cache.ICache;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.pub.PubPermissionUtils;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class TbmPsndocFormEditor extends HrBillFormEditor implements BillCardBeforeEditListener {
	private static final long serialVersionUID = 1L;
	private OrgVOsDefaultRefModel refModel;

	@Override
	public void afterEdit(BillEditEvent e) {
		// 
		if (TBMPsndocCommonValue.ITEMCODE_ISINORGDATA.equals(e.getKey()) || TBMPsndocVO.PK_PSNJOB.equals(e.getKey())) {
			if (TBMPsndocVO.PK_PSNJOB.equals(e.getKey())) {
				// 
				getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNDOC).setValue(((UIRefPane) e.getSource()).getRefValue("bd_psndoc.pk_psndoc"));
				getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNORG).setValue(((UIRefPane) e.getSource()).getRefValue("hi_psnjob.pk_psnorg"));
			}
			UICheckBox checkBox = (UICheckBox) getBillCardPanel().getHeadItem(TBMPsndocCommonValue.ITEMCODE_ISINORGDATA).getComponent();
			// ȡ��ǰ����
			UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
			UFLiteralDate busLitDate = UFLiteralDate.getDate(busDate.toString().substring(0, 10));
			// ����ְ����ָ����ʼ����
			String pk_psnjob = (String) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).getValueObject();
			//ѡ��ְ���ڣ���ʼ���ڲ��ܱ༭
			getBillCardPanel().getHeadItem(TBMPsndocVO.BEGINDATE).setEnabled(!checkBox.isSelected());
			getBillCardPanel().getHeadItem(TBMPsndocVO.BEGINDATE).setValue(checkBox.isSelected() ? getIndutyDate(pk_psnjob, busLitDate) : busLitDate);
		}
		super.afterEdit(e);
	}

	/** 
	 * ȡ�õ�ְ����
	 * @param pk_psnjob
	 * @param busLitDate����ǰҵ��ʱ��
	 * @return
	 */
	private UFLiteralDate getIndutyDate(String pk_psnjob, UFLiteralDate busLitDate) {
		//���û��ѡ����Ա���򷵻ص�ǰҵ������
		if (StringUtils.isBlank(pk_psnjob))
			return busLitDate;
		UFLiteralDate ufdate = null;
		try {
			//��ѯ��Ա��ְ����
			//��ǰ�Ľӿ��ǲ�ѯ������¼��pk_psnorg�Ƿ���������ϵ������ֹ�,���ֲ�����ʱ������Ҫ��
			//��Ϊ��Ա����ʱ�� �ڵ����¼������һ��������������ϵ�����ǲ����¼��
			//ufdate = NCLocator.getInstance().lookup(IPsndocQryService.class).queryIndutyDate(pk_psnjob);
			ufdate = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class).queryBeginDateFromPsnjob(pk_psnjob);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		//���û�е�ְ���ڣ����ÿ����ڼ�Ŀ�ʼ���ڣ����û�п����ڼ���߿����ڼ��Ӧ�Ŀ�ʼ���ڣ���ȡ��ǰҵ��ʱ��
		if (ufdate == null) {
			PeriodVO vo = null;
			try {
				vo = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryByDate(getModel().getContext().getPk_org(), busLitDate);
			} catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
			}
			return (vo == null || vo.getBegindate() == null) ? busLitDate : vo.getBegindate();
		}
		//����е�ְ���ڣ���ȡ��ְ����
		return UFLiteralDate.getDate(ufdate.toDate());
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			resetBillData();
		}
		if (TBMPsndocCommonValue.BATCH_ADD.equalsIgnoreCase(event.getType())) {
			onBatchAdd();
			showMeUp();
		}
		super.handleEvent(event);
	}

	private void onBatchAdd() {
		setEditable(true);
		billCardPanel.addNew();
		setDefaultValue();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				billCardPanel.requestFocusInWindow();
				billCardPanel.transferFocusToFirstEditItem();
			}
		});
	}

	/**
	 * ��Ƭ�У������������ʱ9999-12-9������ʾ��������
	 */
	@Override
	public void setValue(Object object) {
		if (object == null)
			super.setValue(object);
		TBMPsndocVO vo = (TBMPsndocVO) object;
		if (vo == null)
			return;
		if (UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA).equals(vo.getEnddate())) {
			vo.setEnddate(null);
		}
		super.setValue(object);
	}

	@Override
	public TbmPsndocAppModel getModel() {
		return (TbmPsndocAppModel) super.getModel();
	}

	public Object getValue() {
		Object value = super.getValue();
		if (value == null)
			return null;
		// ��������Ϊ��ʱ������Ĭ��ֵ
		TBMPsndocVO vo = (TBMPsndocVO) value;
		//Ŀǰ������ֽ������ڲ�Ϊ�յ�����£���Ϊ�������ڲ��ɱ༭,����κ������vo.getEnddate() == null
		if (vo.getEnddate() == null) {
			vo.setEnddate(UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA));
		}
		return vo;
	}

	@Override
	public void initUI() {
		super.initUI();
		resetBillData();
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		resetRefInf();
	}

	/**
	 * �������ò�����Ϣ
	 * ͨ����˵��ô������֮����վͻ��������ұ��ͣ�������ƽ̨�ĵ���ģ����������е����⣬
	 * �ڲ���ѡ���ƶԻ����еĲ���������ֻ�ṩĬ�����ͺ��������ͣ�
	 * ��ʵ�������ǵ�RefModel���õ���GRIDTREE�ͣ�
	 * ���ص���ģ������ὫTBMPsndocRefModel�Ĳ�����������ΪĬ���ͣ��Ӷ�ʹ�ò��ձ���б��Ͷ����������ұ��ͣ�
	 * Ŀǰ���ǲ��õ���ʱ�������������Ӧ��CardForm����������һ�¸ò��յĲ������ͣ�		
	 * psnRef.setRefType(IRefConst.GRIDTREE)
	 */
	protected void resetRefInf() {
		UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).getComponent();
		//psnRef.getRefModel().addWherePart(PubPermissionUtils.getPsnjobPermission());
		psnRef.setRefType(IRefConst.GRIDTREE);
		UIRefPane adminorg = (UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_ADMINORG).getComponent();
		adminorg.setRefModel(getRefModel());
	}

	public TbmPsndocFormEditor() {
		super();
	}

	/*
	 * �˽ڵ������������޸���Ҫ���б�����Ͻ��У������ڿ�Ƭ�����Ͻ��С�
	 * v63--- ueҪ��Ƭ���޸�
	 * 
	 */
	@Override
	protected void onEdit() {
		super.onEdit();
		//���б���һ�£���Ա�Ϳ�ʼ���ڶ������޸�
		getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).setEnabled(false);
		getBillCardPanel().getHeadItem(TBMPsndocVO.BEGINDATE).setEnabled(false);
	}

	/**
	 * �ػ����ݽ���
	 * 
	 * @param billdata
	 */
	private void resetBillData() {
		BillData billdata = getBillCardPanel().getBillData();
		// ������ڹ���Ϊ���Ž��������ؿ��ڵص���
		billdata.getHeadItem(TBMPsndocVO.PK_PLACE).setShow(getModel().isShowWorkPlace());
		getBillCardPanel().setBillData(billdata);
		getBillCardPanel().updateUI();
	}

	@Override
	protected void setDefaultValue() {
		TBMPsndocVO vo = new TBMPsndocVO();
		UFDate busDate = WorkbenchEnvironment.getInstance().getBusiDate();
		vo.setBegindate(UFLiteralDate.getDate(busDate.toString().substring(0, 10)));
		vo.setTbm_prop(TbmPropEnum.MACHINE_CHECK.toIntValue());
		vo.setPk_group(getModel().getContext().getPk_group());
		vo.setPk_org(getModel().getContext().getPk_org());
		vo.setPk_adminorg(getModel().getContext().getPk_org());
		getBillCardPanel().getBillData().setHeaderValueVO(vo);
	}

	/**
	 * 
	 */
	public void setModel(TbmPsndocAppModel model) {
		super.setModel(model);
	}

	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		super.onAdd();
		//��������������ʱ���ܱ༭
		getBillCardPanel().getHeadItem(TBMPsndocVO.ENDDATE).setEnabled(false);
	}

	@Override
	public boolean beforeEdit(BillItemEvent e) {
		// Ա���ŵĲ��շ�Χ
		if (TBMPsndocVO.PK_PSNJOB.equals(e.getItem().getKey())) {
			UIRefPane psnRef = (UIRefPane) getBillCardPanel().getHeadItem(TBMPsndocVO.PK_PSNJOB).getComponent();
			if (psnRef != null && psnRef.getRefModel() != null) {
				// ���ò����������Ա�Լ���ְ��Ա
				StringBuilder sqlwhere = new StringBuilder();
				sqlwhere.append(" and hi_psnorg.psntype = 0 and hi_psnjob.endflag<>'Y' and " + " hi_psnjob.pk_org in (" + AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getModel().getContext().getPk_org()) + " ) and "// ���˵�ί�е�
						+ PsndocVO.getDefaultTableName() + "." + PsndocVO.PK_PSNDOC + " not in (select tbm_psndoc.pk_psndoc from tbm_psndoc " + " where tbm_psndoc.enddate='" + TBMPsndocCommonValue.END_DATA + "' and  tbm_psndoc.pk_org='" + getModel().getContext().getPk_org() + "') " + PubPermissionUtils.getPsnjobPermission());
				//tsy ���ڵ�����������Ȩ��
				String deptSql =
						HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, "hi_psnjob");
				if (StringUtils.isNotBlank(deptSql)) {
					sqlwhere.append(" and " + deptSql);
				}
				psnRef.getRefModel().addWherePart(sqlwhere.toString());
			}
		}
		//��������������ʱ���ܱ༭ onAdd�ǿ���
		//		if(TBMPsndocVO.ENDDATE.equals(e.getItem().getKey())) 
		//			return false;
		return true;
	}

	/***************************************************************************
	 * �õ���Ȩ�޵���֯<br>
	 * Created on 2011-5-11 16:52:15<br>
	 * @return OrgVO[]
	 * @author Rocex Wang
	 ***************************************************************************/
	protected OrgVO[] getPermOrgVOs() {
		String strCacheKey = PubEnv.getPk_group() + "." + PubEnv.getPk_user() + "." + getModel().getContext().getNodeCode();

		ICache cache = CacheManager.getInstance().getCache(PrimaryOrgPanel.class.getName());

		OrgVO[] orgVOs = (OrgVO[]) cache.get(strCacheKey);

		if (orgVOs == null || orgVOs.length == 0) {
			try {
				orgVOs =
						NCLocator.getInstance().lookup(IPrimaryOrgQry.class).queryPrimaryOrgVOs(IPrimaryOrgQry.CONTROLTYPE_HRADMINORG, getModel().getContext().getFuncInfo().getFuncPermissionPkorgs());

				cache.put(strCacheKey, orgVOs);
			} catch (BusinessException e) {
				Logger.error(e);
			}
		}

		return orgVOs;
	}

	/**
	 * ������֯���գ�Ϊ�˺�����֯���ձ���һ��
	 * @return
	 */
	public OrgVOsDefaultRefModel getRefModel() {
		if (refModel != null) {
			return refModel;
		}

		OrgVO funcPermOrgVOs[] = getPermOrgVOs();

		refModel = new OrgVOsDefaultRefModel(funcPermOrgVOs) {
			@SuppressWarnings("unchecked")
			@Override
			public Vector reloadData() {
				ICache cache = CacheManager.getInstance().getCache(PrimaryOrgPanel.class.getName());

				cache.flush();

				try {
					Field field = OrgVOsDefaultRefModel.class.getDeclaredField("vos");
					field.setAccessible(true);

					field.set(this, getPermOrgVOs());
				} catch (Exception ex) {
					Logger.error(ex.getMessage(), ex);
				}

				return super.reloadData();
			}
		};

		refModel.setUseDataPower(true);
		refModel.setCacheEnabled(false);
		refModel.setRefNodeName(ResHelper.getString("6001uif2", "06001uif20053")
		/* @res "������Դ��֯" */);

		return refModel;
	}

}
