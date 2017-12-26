package nc.ui.ta.psndoc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.ta.TBMPsndocDelegator;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.comp.combinesort.Attribute;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationBar;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.uif2.model.RowOperationInfo;
import nc.ui.uif2.model.RowSelectionOperationInfo;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.ta.psndoc.AssignCardDescriptor;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.util.SqlWhereUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TbmPsndocAppModelDataManager extends DefaultAppModelDataManager implements IAppModelDataManagerEx, AppEventListener,
		IPaginationModelListener, IQueryInfo {

	// 是否显示已结束的考勤档案
	private boolean showEndDoc = false;

	// 上一次的查询条件
	protected FromWhereSQL strWherePart;
	private BillManagePaginationDelegator paginationDelegator;
	private PaginationModel paginationModel;
	private PaginationBar paginationBar;

	public boolean isShowEndDoc() {
		return showEndDoc;
	}

	public TbmPsndocAppModelDataManager() {
	}

	public TBMPsndocVO[] assignCardNo(TBMPsndocVO[] vos, AssignCardDescriptor assignCardInf) throws BusinessException {
		return TBMPsndocDelegator.getTBMPsndocManageMaintain().assignCardNo(vos, assignCardInf);
	}

	public void batchUpdate(String pk_org, TBMPsndocVO[] vos, HashMap<String, Object> batchEditValue) throws BusinessException {
		TBMPsndocDelegator.getTBMPsndocManageMaintain().batchUpdate(pk_org, vos, batchEditValue);
	}

	public GeneralVO[] getHoldIDPsnInfs(TBMPsndocVO[] psnDatas) throws BusinessException {
		Vector<String> strPkTaPsnjobs = new Vector<String>();
		for (TBMPsndocVO psnData : psnDatas) {
			if (psnData.getTimecardid() != null)
				strPkTaPsnjobs.add(psnData.getPk_tbm_psndoc());
		}
		if (strPkTaPsnjobs.size() <= 0)
			return null;
		return TBMPsndocDelegator.getTBMPsndocQueryService().queryTaPsninfoByPks(strPkTaPsnjobs.toArray(new String[0])).toArray(new GeneralVO[0]);

	}

	@Override
	public void handleEvent(AppEvent event) {
		if (TBMPsndocCommonValue.ORG_CHANGED.equals(event.getType())) {
			// 当主组织变化时,清空查询条件
			strWherePart = null;
		}
		if (ArrayUtils.contains(new String[] { AppEventConst.DATA_DELETED, AppEventConst.DATA_INSERTED, AppEventConst.SELECTED_DATE_CHANGED, AppEventConst.MULTI_SELECTION_CHANGED }, event.getType())) {
			RowOperationInfo info = (RowOperationInfo) event.getContextObject();
			Object[] objs = info.getRowDatas();
			Object[] newObjs = new Object[objs.length];
			for (int i = 0; i < objs.length; i++) {
				newObjs[i] = (TBMPsndocVO) objs[i];
			}

			RowOperationInfo newInfo = new RowOperationInfo(info.getRowIndexes(), newObjs);
			if (info instanceof RowSelectionOperationInfo) {
				newInfo = new RowSelectionOperationInfo();
				newInfo.setRowIndexes(info.getRowIndexes());
				newInfo.setRowDatas(newObjs);
				((RowSelectionOperationInfo) newInfo).setSelectionState(((RowSelectionOperationInfo) info).getSelectionState());
			}

			AppEvent newEvent = new AppEvent(event.getType(), event.getSource(), newInfo);
			getPaginationDelegator().handleEvent(newEvent);

			// 当新增/修改后需要更新map中的数据
			if (ArrayUtils.contains(new String[] { AppEventConst.DATA_INSERTED, AppEventConst.SELECTED_DATE_CHANGED }, event.getType())) {
				for (int i = 0; i < objs.length; i++) {
					TBMPsndocVO vo = (TBMPsndocVO) objs[i];
					getPaginationModel().update(vo.getPk_tbm_psndoc(), vo);
				}
			}

		} else {
			getPaginationDelegator().handleEvent(event);
		}
	}

	@Override
	public void initModel() {
		// 初始化时，显示无卡号人员
		String noCardCond =
				TBMPsndocVO.TBM_PROP + "=" + TBMPsndocVO.TBM_PROP_MACHINE + " and " + SQLHelper.getNullSql(TBMPsndocVO.TIMECARDID) + " and " + SQLHelper.getNullSql(TBMPsndocVO.SECONDCARDID);
		//tsy 添加部门权限控制
		FromWhereSQL fromWhereSQL = new FromWhereSQLImpl();
		((FromWhereSQLImpl) fromWhereSQL).setFrom("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob");
		Map<String, String> aliasMap = new HashMap<String, String>();
		aliasMap.put(".", "tbm_psndoc");
		aliasMap.put("pk_psnjob", "T1");
		((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String tableAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
		// 部门权限
		String permission =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, tableAlias);
		if (StringUtils.isNotBlank(permission)) {
			sqlWhereUtil.and(permission);
		}
		Logger.error("添加权限后sql:" + sqlWhereUtil.getSQLWhere());
		fromWhereSQL = new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());

		try {
			initModelBySqlWhere(TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(noCardCond, fromWhereSQL));
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		// 给出提示
		if (CollectionUtils.isNotEmpty(getPaginationModel().getObjectPks())) {
			String errMsg = ResHelper.getString("6017psndoc", "06017psndoc0153")/*@res "存在无卡号人员，请设置考勤卡号！"*/;
			//			ShowStatusBarMsgUtil.showErrorMsg(errMsg, errMsg, getModel().getContext());
			ShowStatusBarMsgUtil.showStatusBarMsg(errMsg, getModel().getContext());
		}
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
	}

	public void initModelBySqlWhere(FromWhereSQL sqlWhere) {
		String condition = " pk_org = '" + getContext().getPk_org() + "' ";
		if (!showEndDoc) {
			condition += " and ( enddate like '9999%') ";
		}
		strWherePart = sqlWhere;
		try {
			String[] pk_psndocs = TBMPsndocDelegator.getTBMPsndocQueryMaintain().queryPsndocPks(getContext(), strWherePart, condition);
			getPaginationModel().setObjectPks(pk_psndocs);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	protected String getOrderby(Vector<Attribute> vectSortField) {
		if (vectSortField == null || vectSortField.size() == 0) {
			return "";
		}
		String strOrderBy = "";
		for (Attribute attr : vectSortField) {
			String strFullCode = attr.getAttribute().getValue();
			String strTableName = "";
			String strCode = strFullCode;
			int iDotIndex = strFullCode.indexOf(".");
			if (iDotIndex > 0) {
				strTableName = strFullCode.substring(0, iDotIndex);
				strCode = strFullCode.substring(iDotIndex);
			}
			strFullCode = getTableAlias(strTableName) + strCode;
			strOrderBy = strOrderBy + "," + strFullCode + (attr.isAscend() ? "" : " desc");
		}
		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}

	/***************************************************************************
	 * 返回查询是表的别名<br>
	 * @param strTableName
	 * @return String
	 * @author Caiyl
	 ***************************************************************************/
	public String getTableAlias(String strTableName) {
		return ((TbmPsndocAppModel) getModel()).getUsedTablesOfQuery().containsKey(strTableName) ? ((TbmPsndocAppModel) getModel()).getUsedTablesOfQuery().get(strTableName) : strTableName;
	}

	@Override
	public void refresh() {
		//		if (strWherePart!=null && StringUtils.isNotEmpty(strWherePart.getWhere())) {
		initModelBySqlWhere(strWherePart);
		//		}
	}

	@Override
	public void setModel(AbstractAppModel model) {
		super.setModel(model);
		getModel().addAppEventListener(this);
	}

	public void setShowEndDoc(boolean showEndDoc) {
		this.showEndDoc = showEndDoc;
	}

	@Override
	public void setShowSealDataFlag(boolean showSealDataFlag) {

	}

	public ArrayList<String>[] updateTbmCard(String pk_org, GeneralVO[] vos, boolean isOverRide) throws BusinessException {
		return TBMPsndocDelegator.getTBMPsndocManageMaintain().updateTbmCard(pk_org, vos, isOverRide);
	}

	public int getQueryDataCount() {
		return getPaginationModel().getObjectPks().size();
	}

	public PaginationModel getPaginationModel() {
		return paginationModel;
	}

	public void setPaginationModel(PaginationModel paginationModel) {
		this.paginationModel = paginationModel;
		paginationModel.addPaginationModelListener(this);
		paginationModel.setPageSize(((TbmPsndocAppModel) getModel()).getPaginationSize());
		paginationModel.setMaxPageSize(TBMPsndocCommonValue.MAX_ROW_PER_PAGE);
		paginationModel.init();
	}

	public PaginationBar getPaginationBar() {
		return paginationBar;
	}

	public void setPaginationBar(PaginationBar paginationBar) {
		this.paginationBar = paginationBar;
	}

	public BillManagePaginationDelegator getPaginationDelegator() {
		if (paginationDelegator == null) {
			paginationDelegator = new BillManagePaginationDelegator((BillManageModel) getModel(), getPaginationModel());
		}
		return paginationDelegator;
	}

	public void setPaginationDelegator(BillManagePaginationDelegator paginationDelegator) {
		this.paginationDelegator = paginationDelegator;
	}

	@Override
	public void onDataReady() {
		getPaginationDelegator().onDataReady();

	}

	@Override
	public void onStructChanged() {
	}

}
