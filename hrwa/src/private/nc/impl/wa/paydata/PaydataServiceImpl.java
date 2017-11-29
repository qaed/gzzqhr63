package nc.impl.wa.paydata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.SimpleDocLocker;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.payfile.PayfileServiceImpl;
import nc.impl.wa.repay.RepayDAO;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.wa.IAmoSchemeQuery;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.itf.hr.wa.IUnitWaClassQuery;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.PaydataDspUtil;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.pubitf.rbac.IDataPermissionPubService;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.hrp.budgetmgt.BudgetWarnMessageVo;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.ClassitemDisplayVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.paydata.WaClassItemShowInfVO;
import nc.vo.wa.paydata.WaPaydataDspVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wa.repay.ReDataVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class PaydataServiceImpl implements IPaydataManageService, IPaydataQueryService {
	private PaydataDAO queryService;

	private PaydataDAO getService() throws DAOException {
		if (this.queryService == null) {
			this.queryService = new PaydataDAO();
		}
		return this.queryService;
	}

	public void update(Object vo, WaLoginVO waLoginVO) throws BusinessException {
		BDPKLockUtil.lockString(new String[] { waLoginVO.getPk_wa_class() });

		getService().update(vo, waLoginVO);
	}

	public void onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll) throws BusinessException {
		getService().checkWaClassStateChange(waLoginVO, whereCondition);

		boolean isAllChecked = getService().onCheck(waLoginVO, whereCondition, isRangeAll);

		if ((WaLoginVOHelper.isSubClass(waLoginVO)) && (isAllChecked))
			collectWaTimesData(waLoginVO);
	}

	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll) throws BusinessException {
		getService().checkWaClassStateChange(waLoginVO, whereCondition);

		getService().onUnCheck(waLoginVO, whereCondition, isRangeAll);

		if (WaLoginVOHelper.isSubClass(waLoginVO))
			collectWaTimesData(waLoginVO);
	}

	public WaClassItemVO[] getUserClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		return getService().getUserClassItemVOs(loginVO);
	}

	public WaClassItemVO[] getUserShowClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		return getService().getUserShowClassItemVOs(loginVO);
	}

	public WaClassItemVO[] getRepayUserShowClassItemVOs(WaLoginContext loginContext) throws BusinessException {
		return getService().getRepayUserShowClassItemVOs(loginContext);
	}

	public WaClassItemShowInfVO getWaClassItemShowInfVO(WaLoginContext loginContext) throws BusinessException {
		WaClassItemVO[] vos1 = getUserShowClassItemVOs(loginContext);

		List list = queryPaydataDisplayInfo(loginContext);
		WaClassItemShowInfVO info = new WaClassItemShowInfVO();
		info.setWaClassItemVO(vos1);
		info.setWaPaydataDspVO((WaPaydataDspVO[]) list.toArray(new WaPaydataDspVO[list.size()]));

		return info;
	}

	public int updateClassItemVOsDisplayFlg(WaClassItemVO[] classItemVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(classItemVOs)) {
			return 0;
		}

		String PKclass = classItemVOs[0].getPk_wa_class();
		String userpk = PubEnv.getPk_user();
		String waYear = classItemVOs[0].getCyear();
		String waPeriod = classItemVOs[0].getCperiod();
		List lisDisplay = new ArrayList();
		for (WaClassItemVO classitem : classItemVOs) {
			ClassitemDisplayVO displayVO = new ClassitemDisplayVO();
			displayVO.setPk_wa_classitem(classitem.getPk_wa_classitem());
			displayVO.setPk_wa_class(classitem.getPk_wa_class());
			displayVO.setPk_user(PubEnv.getPk_user());
			displayVO.setCyear(classitem.getCyear());
			displayVO.setCperiod(classitem.getCperiod());
			displayVO.setBshow(classitem.getShowflag());
			displayVO.setDisplayseq(classitem.getIdisplayseq());
			displayVO.setStatus(2);
			lisDisplay.add(displayVO);
		}
		getService().getBaseDao().deleteByClause(ClassitemDisplayVO.class, new StringBuilder().append("pk_wa_class = '").append(PKclass).append("' and pk_user = '").append(userpk).append("' and cyear = '").append(waYear).append("' and cperiod = '").append(waPeriod).append("'").toString());
		getService().getBaseDao().insertVOList(lisDisplay);
		return 0;
	}

	public void onPay(WaLoginContext loginContext) throws BusinessException {
		BDPKLockUtil.lockString(new String[] { loginContext.getPk_wa_class() });

		getService().checkWaClassStateChange(loginContext.getWaLoginVO(), null);

		getService().onPay(loginContext);
	}

	public void onUnPay(WaLoginVO waLoginVO) throws BusinessException {
		BDPKLockUtil.lockString(new String[] { waLoginVO.getPk_wa_class() });

		getService().checkWaClassStateChange(waLoginVO, null);

		if ((waLoginVO.getPk_prnt_class() != null) && (waLoginVO.getPk_prnt_class() != waLoginVO.getPk_wa_class())) {
			WaLoginVO subLoginVO = (WaLoginVO) waLoginVO.clone();
			subLoginVO.setPk_wa_class(waLoginVO.getPk_prnt_class());
			checkIsApportion(subLoginVO);
		}

		getService().onUnPay(waLoginVO);
	}

	private void checkIsApportion(WaLoginVO waLoginVO) throws BusinessException {
		if (((IAmoSchemeQuery) NCLocator.getInstance().lookup(IAmoSchemeQuery.class)).isApportion(waLoginVO)) {
			if (WaLoginVOHelper.isSubClass(waLoginVO)) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0516"));
			}
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0458"));
		}
	}

	public void onReplace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula, SuperVO[] superVOs)
			throws BusinessException {
		BDPKLockUtil.lockString(new String[] { waLoginVO.getPk_wa_class() });

		String operateConditon =
				((IDataPermissionPubService) NCLocator.getInstance().lookup(IDataPermissionPubService.class)).getDataPermissionSQLWherePartByMetaDataOperation(PubEnv.getPk_user(), "wa_data", "Replace", PubEnv.getPk_group());

		if (StringUtils.isBlank(whereCondition))
			whereCondition = operateConditon;
		else {
			whereCondition =
					new StringBuilder().append(whereCondition).append(WherePartUtil.formatAddtionalWhere(operateConditon)).toString();
		}

		String userConditon = WaPowerSqlHelper.getWaPowerSql(PubEnv.getPk_group(), "6007psnjob", "wadefault", "wa_data");

		if (StringUtils.isBlank(whereCondition))
			whereCondition = userConditon;
		else {
			whereCondition = new StringBuilder().append(whereCondition).append(WherePartUtil.formatAddtionalWhere(userConditon)).toString();
		}

		if (null != superVOs) {
			BDVersionValidationUtil.validateSuperVO(superVOs);
		}

		getService().onReplace(waLoginVO, whereCondition, replaceItem, formula);
	}

	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException {
		getService().onSaveDataSVOs(waLoginVO, dataSVOs);
	}

	public AggPayDataVO queryAggPayDataVOByCondition(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		WaBusilogUtil.writePaydataQueryBusiLog(loginContext);
		if (StringUtils.isBlank(orderCondtion)) {
			SortVO[] sortVOs = null;
			SortconVO[] sortconVOs = null;
			String strCondition =
					new StringBuilder().append(" func_code='").append(loginContext.getNodeCode()).append("'").append(" and group_code= 'TableCode' and ((pk_corp='").append(PubEnv.getPk_group()).append("' and pk_user='").append(PubEnv.getPk_user()).append("') or pk_corp ='@@@@') order by pk_corp").toString();

			sortVOs =
					(SortVO[]) (SortVO[]) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, SortVO.class, strCondition);
			Vector vectSortField = new Vector();
			if ((sortVOs != null) && (sortVOs.length > 0)) {
				strCondition =
						new StringBuilder().append("pk_hr_sort='").append(sortVOs[0].getPrimaryKey()).append("' order by field_seq ").toString();
				sortconVOs =
						(SortconVO[]) (SortconVO[]) ((IPersistenceRetrieve) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)).retrieveByClause(null, SortconVO.class, strCondition);

				for (int i = 0; (sortconVOs != null) && (i < sortconVOs.length); i++) {
					Pair field = new Pair(sortconVOs[i].getField_name(), sortconVOs[i].getField_code());
					Attribute attribute = new Attribute(field, sortconVOs[i].getAscend_flag().booleanValue());
					vectSortField.addElement(attribute);
				}
				orderCondtion = getOrderby(vectSortField);
			}
			if (StringUtils.isBlank(orderCondtion)) {
				orderCondtion = " org_dept_v.code , hi_psnjob.clerkcode ";
			}
		}
		return getService().queryAggPayDataVOByCondition(loginContext, condition, orderCondtion);
	}

	public static String getOrderby(Vector<Attribute> vectSortField) {
		if ((vectSortField == null) || (vectSortField.size() == 0)) {
			return "";
		}
		String strOrderBy = "";
		for (Attribute attr : vectSortField) {
			String strFullCode = (String) attr.getAttribute().getValue();
			strOrderBy =
					new StringBuilder().append(strOrderBy).append(",").append(strFullCode).append(attr.isAscend() ? "" : " desc").toString();
		}

		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}

	public DataVO getDataVOByPk(String pk_wa_data) throws BusinessException {
		DataVO[] dataVOs =
				(DataVO[]) getService().retrieveAppendableVOsByClause(DataVO.class, new StringBuilder().append("pk_wa_data = '").append(pk_wa_data).append("'").toString());

		if (dataVOs != null) {
			return dataVOs[0];
		}
		return null;
	}

	public boolean isAnyTimesPayed(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		return getService().isAnyTimesPayed(pk_wa_class, cyear, cperiod);
	}

	public DataVO[] queryDataVOByPks(String[] pk_wa_data) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_wa_data)) {
			return new DataVO[0];
		}

		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			String conditon = inSQLCreator.getInSQL(pk_wa_data);
			DataVO[] dataVOArrays = getService().queryByPKSCondition(conditon, "");

			List dataVOList = new ArrayList();
			Map dataVOMap = new HashMap();
			for (DataVO dataVO : dataVOArrays) {
				dataVOMap.put(dataVO.getPk_wa_data(), dataVO);
			}
			for (String str_pk_wa_data : pk_wa_data) {
				dataVOList.add(dataVOMap.get(str_pk_wa_data));
			}

			return (DataVO[]) dataVOList.toArray(new DataVO[0]);
		} finally {
			inSQLCreator.clear();
		}
	}

	public DataSVO[] getDataSVOs(WaLoginContext loginContext) throws BusinessException {
		return getService().getDataSVOs(loginContext);
	}

	public DataVO[] queryDataVOsByCond(WaLoginContext loginContext, String condition, String orderCondtion) throws BusinessException {
		return getService().queryByCondition(loginContext, condition, orderCondtion);
	}

	public DataVO[] getContractDataVOs(WaLoginContext loginVO, String whereCondition, String orderCondition) throws BusinessException {
		return getService().getContractDataVOs(loginVO, whereCondition, orderCondition);
	}

	public void onCaculate(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition, SuperVO[] superVOs)
			throws BusinessException {
		if (!StringUtils.isBlank(loginContext.getPk_prnt_class())) {
			BDPKLockUtil.lockString(new String[] { loginContext.getPk_prnt_class() });
		}

		new SimpleDocLocker().lock("update", new Object[] { loginContext.getWaLoginVO() });

		BDVersionValidationUtil.validateSuperVO(new SuperVO[] { loginContext.getWaLoginVO() });

		if (null != superVOs) {
			BDVersionValidationUtil.validateSuperVO(superVOs);
		}

		checkReData(loginContext.getWaLoginVO());

		DataCaculateService caculateService = new DataCaculateService(loginContext, caculateTypeVO, condition);
		caculateService.doCaculate();
	}

	private void checkReData(WaLoginVO waLoginVO) throws BusinessException {
		boolean b = haveMakeRedata(waLoginVO, null);
		if (!b)
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0459"));
	}

	private boolean haveMakeRedata(WaLoginVO waLoginVO, String power) throws BusinessException {
		boolean b = false;
		RepayDAO dao = new RepayDAO();
		ReDataVO[] dataVo = dao.queryAllAt(waLoginVO, null, null, power);

		if (ArrayUtils.isEmpty(dataVo)) {
			b = true;
		} else {
			dataVo = dao.queryAllAt(waLoginVO, "-1", "-1", power);

			if (!ArrayUtils.isEmpty(dataVo)) {
				dataVo = dao.queryAllAt(waLoginVO, "-1", "-1", "  wa_redata.reflag='N' ");

				if (ArrayUtils.isEmpty(dataVo)) {
					b = true;
				}
			}
		}
		return b;
	}

	public void reTotal(WaLoginVO waLoginVO) throws BusinessException {
		WaState state = waLoginVO.getState();

		if ((state == WaState.CLASS_WITHOUT_RECACULATED) || (state == WaState.CLASS_RECACULATED_WITHOUT_CHECK)) {
			IUnitWaClassQuery unitClassQuery = (IUnitWaClassQuery) NCLocator.getInstance().lookup(IUnitWaClassQuery.class);
			if (!unitClassQuery.isUnitAllCheckOut(waLoginVO)) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0460"));
			}

			PaydataDAO dao = new PaydataDAO();
			//20171129 tsy 汇总所有的项目
			//			WaItemVO[] itemVOs = dao.getUnitDigitItem(waLoginVO);
			WaItemVO[] itemVOs = dao.getUnitAllItem(waLoginVO);
			//20171129 end 
			sumWaData(waLoginVO, itemVOs);

			dao.updateStateforTotal(waLoginVO);
		} else {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0461"));
		}
	}

	public void collectWaTimesData(WaLoginVO waLoginVO) throws BusinessException {
		PaydataCollectDAO dao = new PaydataCollectDAO();
		String pk_waclass = waLoginVO.getPk_prnt_class();
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();

		WaInludeclassVO[] childClasses =
				((IWaClass) NCLocator.getInstance().lookup(IWaClass.class)).queryAllCheckedChildClasses(pk_waclass, cyear, cperiod);

		dao.collectWaTimesDigitData(waLoginVO, childClasses);

		dao.collectWaTimesNODigitData(waLoginVO, childClasses);

		dao.collectTaxBase(waLoginVO, childClasses);

		dao.collectTaxedAndTaxedBase(waLoginVO, childClasses);

		dao.collectStopflag(waLoginVO);

		WaLoginContext context = waLoginVO.toWaLoginContext();

		new ParentClassPsntaxService().doPsnTax(context, childClasses);
	}

	public void sumWaData(WaLoginVO waLoginVO, WaItemVO[] itemVOs) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();

		dao.deleteUnitClassPsn(waLoginVO);

		PayfileVO[] addPsn = getUnitPsnVOs(waLoginVO);

		if (!ArrayUtils.isEmpty(addPsn)) {
			PayfileServiceImpl payfileImpl = new PayfileServiceImpl();
			payfileImpl.addPsnVOs(addPsn);
		}

		dao.updateData(waLoginVO, itemVOs);

		dao.deleteUnitRelation(waLoginVO);
	}

	private PayfileVO[] getUnitPsnVOs(WaLoginVO waLoginVO) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		PayfileVO[] datavos = dao.getUnitPsnVOs(waLoginVO);
		if (datavos == null) {
			return null;
		}

		HashMap psnVector = new HashMap();
		for (PayfileVO vo : datavos) {
			if ((!psnVector.containsKey(vo.getPk_psndoc())) || (!vo.getPartflag().booleanValue())) {
				PayfileVO psnVO = new PayfileVO();
				psnVO.setPk_psnjob(vo.getPk_psnjob());
				psnVO.setPk_psndoc(vo.getPk_psndoc());
				psnVO.setPk_psnorg(vo.getPk_psnorg());
				psnVO.setTaxtype(vo.getTaxtype());
				psnVO.setTaxtableid(vo.getTaxtableid());
				psnVO.setIsderate(vo.getIsderate());
				psnVO.setDerateptg(vo.getDerateptg());
				psnVO.setIsndebuct(vo.getIsndebuct());

				psnVO.setPk_org(vo.getPk_org());

				psnVO.setWorkorg(vo.getWorkorg());
				psnVO.setWorkorgvid(vo.getWorkorgvid());
				psnVO.setWorkdept(vo.getWorkdept());
				psnVO.setWorkdeptvid(vo.getWorkdeptvid());

				psnVO.setPk_financeorg(vo.getPk_financeorg());
				psnVO.setFiporgvid(vo.getFiporgvid());
				psnVO.setPk_financedept(vo.getPk_financedept());
				psnVO.setFipdeptvid(vo.getFipdeptvid());

				psnVO.setPk_liabilityorg(vo.getPk_liabilityorg());
				psnVO.setPk_liabilitydept(vo.getPk_liabilitydept());
				psnVO.setLibdeptvid(vo.getLibdeptvid());

				psnVO.setPartflag(vo.getPartflag());
				psnVO.setStopflag(vo.getStopflag());

				psnVO.setPk_bankaccbas1(vo.getPk_bankaccbas1());
				psnVO.setPk_bankaccbas2(vo.getPk_bankaccbas2());
				psnVO.setPk_bankaccbas3(vo.getPk_bankaccbas3());

				psnVO.setPk_wa_class(waLoginVO.getPk_wa_class());
				psnVO.setCyear(waLoginVO.getPeriodVO().getCyear());
				psnVO.setCperiod(waLoginVO.getPeriodVO().getCperiod());
				psnVO.setCyearperiod(new StringBuilder().append(waLoginVO.getCyear()).append(waLoginVO.getCperiod()).toString());

				psnVO.setPk_group(waLoginVO.getPk_group());
				psnVO.setPk_org(waLoginVO.getPk_org());

				psnVector.put(psnVO.getPk_psndoc(), psnVO);
			}
		}
		return psnVector.size() == 0 ? null : (PayfileVO[]) psnVector.values().toArray(new PayfileVO[psnVector.size()]);
	}

	public boolean isPayrollSubmit(WaLoginVO waLoginVO) throws BusinessException {
		return new PaydataDAO().isPayrollSubmit(waLoginVO);
	}

	public boolean isPayrollFree(WaLoginVO waLoginVO) throws BusinessException {
		return new PaydataDAO().isPayrollFree(waLoginVO);
	}

	public AggPayDataVO queryAggPayDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		return getService().queryAggPayDataVOForroll(loginContext);
	}

	public Map<String, AggPayDataVO> queryItemAndSumDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		Map aggvomap = new HashMap();
		aggvomap.put("itemdata", getService().queryAggPayDataVOForroll(loginContext));

		aggvomap.put("sumdata", getService().querySumDataVOForroll(loginContext));

		aggvomap.put("sumdataall", getService().querySumDataVOForrollAll(loginContext));

		return aggvomap;
	}

	public void updatePaydataFlag(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		dao.updatePaydataFlag(pk_wa_class, cyear, cperiod);
	}

	public void clearClassItemData(WaClassItemVO vo) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		dao.clearClassItemData(vo);
	}

	public BigDecimal getOrgTmSelected(String cacuItem, String whereStr) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getOrgTmSelected(cacuItem, whereStr);
	}

	public BigDecimal getOrgTm(String cacuItem, String pk_org, String accYear, String accPeriod, String pk_wa_class, int sumType)
			throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getOrgTm(cacuItem, pk_org, accYear, accPeriod, pk_wa_class, sumType);
	}

	public Map<String, BigDecimal> getDeptTm(String cacuItem, String pk_org, String accYear, String accPeriod, String pk_wa_class, int sumType)
			throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getDeptTm(cacuItem, pk_org, accYear, accPeriod, pk_wa_class, sumType);
	}

	public BudgetWarnMessageVo budgetAlarm4Pay(WaLoginContext context, String whereStr) throws BusinessException {
		PaydataBudgetAlarmTool tool = new PaydataBudgetAlarmTool();
		return tool.budgetAlarm4Pay(context, whereStr);
	}

	public Map<String, BigDecimal> getDeptTmSelected(String cacuItem, String whereStr) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getDeptTmSelected(cacuItem, whereStr);
	}

	public WaClassItemVO[] getApprovalClassItemVOs(WaLoginContext loginContext) throws BusinessException {
		PaydataDAO dao = new PaydataDAO();
		return dao.getApprovedClassItemVOs(loginContext);
	}

	public List<WaPaydataDspVO> queryPaydataDisplayInfo(WaLoginContext context) throws BusinessException {
		IClassItemQueryService citemService = (IClassItemQueryService) NCLocator.getInstance().lookup(IClassItemQueryService.class);

		WaItemVO[] itemArray = citemService.queryAllClassItemInfos(context);

		List dspList = queryPaydataPersonalDsp(context);
		if ((dspList == null) || (dspList.isEmpty())) {
			dspList = queryPaydataCommonDsp(context);
		}

		if ((dspList == null) || (dspList.isEmpty())) {
			dspList = PaydataDspUtil.queryDefaultDsp();
			if (!ArrayUtils.isEmpty(itemArray)) {
				WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
				Map itemMap = CommonUtils.toMap("pk_wa_item", itemArray);
				List itemList = new ArrayList();
				int i = 0;
				for (int j = ArrayUtils.getLength(classItemVOs); i < j; i++) {
					WaItemVO itemVO = (WaItemVO) itemMap.get(classItemVOs[i].getPk_wa_item());
					if (itemVO == null)
						continue;
					itemList.add(itemVO);
				}

				List waItemsDspList =
						PaydataDspUtil.convertWaItemVO(CollectionUtils.isEmpty(itemList) ? null : (WaItemVO[]) itemList.toArray(new WaItemVO[0]));
				dspList.addAll(waItemsDspList);
			}

		} else {
			PaydataDspUtil.setPaydataDisplayName(dspList, context);

			PaydataDspUtil.addNewlyDsiplayItem(dspList, itemArray);
		}
		return dspList;
	}

	private List<WaPaydataDspVO> queryPaydataCommonDsp(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='").append("0").append(new StringBuilder().append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '").append(context.getPk_wa_class()).append("' and cyear = '").append(context.getCyear()).append("' and cperiod = '").append(context.getCperiod()).append("' and itemkey = wa_paydatadsp.item_key) ").append("or wa_paydatadsp.item_key in ('").append("psncode").append("','").append("clerkcode").append("','").append("plsname").append("','").append("orgname").append("','").append("deptname").append("','").append("postname").append("','").append("taxtype").append("','").append("caculateflag").append("','").append("checkflag").append("','").append("cyear").append("','").append("cperiod").append("','").append("psnname").append("') ) ").toString()).append("and (item_key IN(SELECT wa_classitem.ITEMKEY ").append("   FROM wa_itempower inner join wa_classitem ON wa_classitem.pk_wa_item=wa_itempower.pk_wa_item and wa_classitem.PK_WA_CLASS=wa_itempower.PK_WA_CLASS ").append(new StringBuilder().append("  WHERE wa_itempower.pk_wa_class = '").append(context.getPk_prnt_class()).append("'").toString()).append(new StringBuilder().append("    AND wa_itempower.pk_group ='").append(context.getPk_group()).append("'").toString()).append(new StringBuilder().append("    AND wa_itempower.pk_org = '").append(context.getPk_org()).append("'").toString()).append("    AND ( wa_itempower.pk_subject IN(SELECT pk_role ").append("\t\t\t\t       FROM sm_user_role ").append(new StringBuilder().append("\t\t\t\t      WHERE cuserid = '").append(PubEnv.getPk_user()).append("'").toString()).append(new StringBuilder().append("                   ) or wa_itempower.pk_subject = '").append(PubEnv.getPk_user()).append("')) ").toString()).append(" or wa_paydatadsp.item_key in ('psncode','clerkcode','plsname','orgname','deptname','postname','taxtype','caculateflag','checkflag','cyear','cperiod','psnname') ) ");

		return (List) this.queryService.getBaseDao().retrieveByClause(WaPaydataDspVO.class, condtion.toString(), "displayseq");
	}

	private List<WaPaydataDspVO> queryPaydataPersonalDsp(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_user = '").append(context.getPk_loginUser()).append("' and pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='").append("1").append(new StringBuilder().append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '").append(context.getPk_wa_class()).append("' and cyear = '").append(context.getCyear()).append("' and cperiod = '").append(context.getCperiod()).append("' and itemkey = wa_paydatadsp.item_key) or wa_paydatadsp.item_key in ('").append("psncode").append("','").append("clerkcode").append("','").append("plsname").append("','").append("orgname").append("','").append("deptname").append("','").append("postname").append("','").append("taxtype").append("','").append("caculateflag").append("','").append("checkflag").append("','").append("cyear").append("','").append("cperiod").append("','").append("psnname").append("') ) ").toString());

		return (List) getService().getBaseDao().retrieveByClause(WaPaydataDspVO.class, condtion.toString(), "displayseq");
	}

	private List<WaPaydataDspVO> queryPaydataCommonDsp4payleave(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='").append("2").append(new StringBuilder().append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '").append(context.getPk_wa_class()).append("' and cyear = '").append(context.getCyear()).append("' and cperiod = '").append(context.getCperiod()).append("' and itemkey = wa_paydatadsp.item_key) or wa_paydatadsp.item_key in ('").append("clerkcode").append("','").append("plsname").append("','").append("orgname").append("','").append("deptname").append("','").append("postname").append("','").append("taxtype").append("','").append("caculateflag").append("','").append("checkflag").append("','").append("cyear").append("','").append("cperiod").append("','").append("psnname").append("','").append("payflag").append("','").append("leavedate").append("','").append("cpaydate").append("','").append("vpaycomment").append("') ) ").toString()).append("and (item_key IN(SELECT wa_classitem.ITEMKEY ").append("   FROM wa_itempower inner join wa_classitem ON wa_classitem.pk_wa_item=wa_itempower.pk_wa_item and wa_classitem.PK_WA_CLASS=wa_itempower.PK_WA_CLASS ").append(new StringBuilder().append("  WHERE wa_itempower.pk_wa_class = '").append(context.getPk_prnt_class()).append("'").toString()).append(new StringBuilder().append("    AND wa_itempower.pk_group ='").append(context.getPk_group()).append("'").toString()).append(new StringBuilder().append("    AND wa_itempower.pk_org = '").append(context.getPk_org()).append("'").toString()).append("    AND ( wa_itempower.pk_subject IN(SELECT pk_role ").append("\t\t\t\t       FROM sm_user_role ").append(new StringBuilder().append("\t\t\t\t      WHERE cuserid = '").append(PubEnv.getPk_user()).append("'").toString()).append(new StringBuilder().append("                   ) or wa_itempower.pk_subject = '").append(PubEnv.getPk_user()).append("')) ").toString()).append(" or wa_paydatadsp.item_key in ('psncode','clerkcode','plsname','orgname','deptname','postname','taxtype','caculateflag','checkflag','cyear','cperiod','psnname') ) ");

		return (List) this.queryService.getBaseDao().retrieveByClause(WaPaydataDspVO.class, condtion.toString(), "displayseq");
	}

	private List<WaPaydataDspVO> queryPaydataPersonalDsp4payleave(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_user = '").append(context.getPk_loginUser()).append("' and pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='").append("3").append(new StringBuilder().append("'  and ( exists ( select 1 from wa_classitem where pk_wa_class = '").append(context.getPk_wa_class()).append("' and cyear = '").append(context.getCyear()).append("' and cperiod = '").append(context.getCperiod()).append("' and itemkey = wa_paydatadsp.item_key) or wa_paydatadsp.item_key in ('").append("clerkcode").append("','").append("plsname").append("','").append("orgname").append("','").append("deptname").append("','").append("postname").append("','").append("taxtype").append("','").append("caculateflag").append("','").append("checkflag").append("','").append("cyear").append("','").append("cperiod").append("','").append("psnname").append("','").append("payflag").append("','").append("leavedate").append("','").append("cpaydate").append("','").append("vpaycomment").append("') ) ").toString());

		return (List) getService().getBaseDao().retrieveByClause(WaPaydataDspVO.class, condtion.toString(), "displayseq");
	}

	public void deleteDisplayInfo(WaLoginContext context, String type) throws BusinessException {
		StringBuffer whereStr = new StringBuffer();
		whereStr.append(" pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='").append(type).append("'");
		if ("1".equals(type)) {
			whereStr.append(" and pk_user='").append(context.getPk_loginUser()).append("'");
		}
		getService().getBaseDao().deleteByClause(WaPaydataDspVO.class, whereStr.toString());
	}

	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws BusinessException {
		if (!StringUtils.isBlank(loginContext.getPk_prnt_class())) {
			BDPKLockUtil.lockString(new String[] { loginContext.getPk_prnt_class() });
		}

		new SimpleDocLocker().lock("update", new Object[] { loginContext.getWaLoginVO() });

		BDVersionValidationUtil.validateSuperVO(new SuperVO[] { loginContext.getWaLoginVO() });
		getService().reCaculate(loginContext, whereCondition);
	}

	public PayfileVO[] getInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll) throws BusinessException {
		return getService().getInPayLeavePsn(waLoginVO, whereCondition, isRangeAll);
	}

	public List<WaPaydataDspVO> queryPaydataDisplayInfo4Payleave(WaLoginContext context) throws BusinessException {
		IClassItemQueryService citemService = (IClassItemQueryService) NCLocator.getInstance().lookup(IClassItemQueryService.class);

		WaItemVO[] itemArray = citemService.queryAllClassItemInfos(context);

		List dspList = queryPaydataPersonalDsp4payleave(context);
		if ((dspList == null) || (dspList.isEmpty())) {
			dspList = queryPaydataCommonDsp4payleave(context);
		}
		if ((dspList == null) || (dspList.isEmpty())) {
			dspList = PaydataDspUtil.queryDefaultDsp4PayLeave();
			if (!ArrayUtils.isEmpty(itemArray)) {
				WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
				Map itemMap = CommonUtils.toMap("pk_wa_item", itemArray);

				List itemList = new ArrayList();
				int i = 0;
				for (int j = ArrayUtils.getLength(classItemVOs); i < j; i++) {
					WaItemVO itemVO = (WaItemVO) itemMap.get(classItemVOs[i].getPk_wa_item());
					if (itemVO == null)
						continue;
					itemList.add(itemVO);
				}

				List waItemsDspList =
						PaydataDspUtil.convertWaItemVO(CollectionUtils.isEmpty(itemList) ? null : (WaItemVO[]) itemList.toArray(new WaItemVO[0]));

				dspList.addAll(waItemsDspList);
			}
		} else {
			PaydataDspUtil.setPaydataDisplayName4PayLeave(dspList, context);

			PaydataDspUtil.addNewlyDsiplayItem(dspList, itemArray);
		}
		return dspList;
	}

	public WaClassItemShowInfVO getWaClassItemShowInfVO4PayLeave(WaLoginContext loginContext) throws BusinessException {
		WaClassItemVO[] vos1 = getUserShowClassItemVOs(loginContext);

		List list = queryPaydataDisplayInfo4Payleave(loginContext);
		WaClassItemShowInfVO info = new WaClassItemShowInfVO();
		info.setWaClassItemVO(vos1);
		info.setWaPaydataDspVO((WaPaydataDspVO[]) list.toArray(new WaPaydataDspVO[list.size()]));

		return info;
	}

	public void updateCalFlag4OnTime(PsndocWaVO[] psndocWaVOs) throws BusinessException {
		ArrayList pk_psndocList = new ArrayList();
		String pk_wa_class = null;
		String cyear = null;
		String cperiod = null;
		if (!ArrayUtils.isEmpty(psndocWaVOs)) {
			for (int i = 0; i < psndocWaVOs.length; i++) {
				pk_psndocList.add(psndocWaVOs[i].getPk_psndoc());
			}
			pk_wa_class = psndocWaVOs[0].getPk_wa_class();
			cyear = psndocWaVOs[0].getCyear();
			cperiod = psndocWaVOs[0].getCperiod();
		}
		if ((!pk_psndocList.isEmpty()) && (cyear != null) && (cperiod != null))
			getService().updateCalFlag4OnTime(pk_wa_class, cyear, cperiod, (String[]) pk_psndocList.toArray(new String[0]));
	}
}