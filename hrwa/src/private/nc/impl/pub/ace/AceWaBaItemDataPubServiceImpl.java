package nc.impl.pub.ace;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.DAOException;
import nc.bs.framework.codesync.client.NCClassLoader;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.ResHelper;
import nc.impl.hr.formula.parser.IFormulaParser;
import nc.impl.hrwa.func.WaBaCommonProcess;
import nc.impl.hrwa.func.WaBaWaAdjustDocProcess;
import nc.jdbc.framework.SQLParameter;
import nc.vo.hr.func.FunctionVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.wa.func.SqlFragment;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.wa_ba.func.FunctionParse;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

import org.apache.commons.lang.StringUtils;

/**
 * @author tsheay
 * @see nc.impl.wa.paydata.DataCaculateService
 */
public class AceWaBaItemDataPubServiceImpl extends AppendBaseDAO {

	protected ItemsVO[] itemVOs = null;// ���������Ŀ
	private Set<String> pk_wa_itemSet = null;// ��Ҫ����ֵ����Ŀ
	ISuperVO[] schbvos = null;// ��Ҫ��������Ա���
	String year = null;
	String period = null;

	/**
	 * ����ʱ����ʼ����Ҫ�ı�����������Ŀ�����壨�����
	 * 
	 * @param bvos
	 * @throws BusinessException
	 */
	public AceWaBaItemDataPubServiceImpl(ISuperVO[] bvos, String year, String period) throws BusinessException {
		this.schbvos = bvos;
		this.year = year;
		this.period = period;
		// ��ʼ����Ŀ
		initItems();
		// �õ���Ҫ��õ���ֵ����Ŀset
		setPk_wa_itemSet();

	}

	/**
	 * ���ⲿ����
	 * 
	 * @throws BusinessException
	 */
	public void doCaculate() throws BusinessException {

		doCaculate(getItemVOs());

	}

	private void doCaculate(ItemsVO[] classItemVOs) throws BusinessException {

		if (classItemVOs == null) {
			return;
		}
		// �ֱ����ÿ����Ŀ�����������������
		for (ItemsVO waClassItemVO : classItemVOs) {
			doCaculateSingle(waClassItemVO);
		}
		// �����ӱ�����ڽ���
		StringBuilder sql = new StringBuilder();
		String[] yearAndperiod = getPreviousPeriod();
		sql.append(" update wa_ba_sch_unit now set now.class1=isnull(( ");
		sql.append(" select pre.class4 from wa_ba_sch_unit pre ");
		sql.append(" left join wa_ba_sch_h h on pre.pk_ba_sch_h=h.pk_ba_sch_h ");
		sql.append(" where pre.ba_unit_code=now.ba_unit_code and nvl(pre.dr,0)=0 and h.cperiod='");
		sql.append(yearAndperiod[1]);
		sql.append("' and h.cyear='");
		sql.append(yearAndperiod[0]);
		sql.append("'),0) where now.pk_ba_sch_h='");
		sql.append(((WaBaSchBVO) schbvos[0]).getPk_ba_sch_h());
		sql.append("'");
		getBaseDao().executeUpdate(sql.toString());
		// �����ӱ��¼ƻ������ܶ�
		sql.delete(0, sql.length());
		sql.append(" update wa_ba_sch_unit unit set class2=( ");
		sql.append(" select sum(psns.f_10) ");
		sql.append(" from wa_ba_sch_psns psns where unit.pk_ba_sch_unit = psns.pk_ba_sch_unit ) ");
		sql.append(" where unit.pk_ba_sch_h='");
		sql.append(((WaBaSchBVO) schbvos[0]).getPk_ba_sch_h());
		sql.append("'");
		getBaseDao().executeUpdate(sql.toString());
		//�����ӱ��ۼƿɷ����ܶ�
		sql.delete(0, sql.length());
		sql.append(" update wa_ba_sch_unit unit set plan_totalmoney= ");
		sql.append(" class1+class2 ");
		sql.append(" where unit.pk_ba_sch_h='");
		sql.append(((WaBaSchBVO) schbvos[0]).getPk_ba_sch_h());
		sql.append("'");
		getBaseDao().executeUpdate(sql.toString());
	}

	/**
	 * ���㵥��н����Ŀ
	 */
	private void doCaculateSingle(ItemsVO itemVO) throws BusinessException {
		try {
			// ������ֹ����룬 ����
			if (itemVO.getDatatype().equals(FromEnumVO.USER_INPUT.value())) {// �ֹ����롾2��
				return;
			}
			// ����ǹ̶�ֵ���������е����
			if (itemVO.getDatatype().equals(FromEnumVO.FIX_VALUE.value())) {// �̶�ֵ��3��
				updateConstantItem(itemVO);
				return;
			}
			// �������������Դ
			if (!itemVO.getDatatype().equals(FromEnumVO.FORMULA.value())) {// �ɹ�ʽ���㡾0��
				execute(itemVO);
			} else if (itemVO.getVformula() != null) {// ����ǹ�ʽ
				WaBaPreTranslate waPreTranslate = new WaBaPreTranslate();
				String formua = waPreTranslate.parse(itemVO.getPk_org(), itemVO.getVformula());
				itemVO.setVformula(formua);
				List<SqlFragment> fragmentList = FunctionParse.parse(formua);
				if (fragmentList != null && fragmentList.size() > 0) {
					for (SqlFragment sqlFragment : fragmentList) {
						execute(itemVO, sqlFragment);
					}
				}
			}
		} catch (Exception e) {
			if (e instanceof DAOException) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0444") + itemVO.getName() + ResHelper.getString("60130paydata", "060130paydata0445"));// ��ʽ���ô�������н����Ŀ

			} else if (e instanceof BusinessException) {
				throw (BusinessException) e;
			} else {
				throw new BusinessException(e);
			}
		}
	}

	private void execute(ItemsVO itemVO) throws Exception {
		String formula = itemVO.getVformula();
		formula = parse(formula);
		SqlFragment sqlFragment = new SqlFragment();
		sqlFragment.setValue(formula);
		execute(itemVO, sqlFragment);
	}

	private void execute(ItemsVO itemVO, SqlFragment sqlFragment) throws BusinessException {

		try {
			if (!StringUtils.isBlank(sqlFragment.getValue())) {
				String sql = translate2ExecutableSql(itemVO, sqlFragment);
				getBaseDao().executeUpdate(sql);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0444") + itemVO.getName() + ResHelper.getString("60130paydata", "060130paydata0445"));// н����Ŀ:
																																												// XX��ʽ���ô�������

		}
	}

	/**
	 * ���¹̶���Ŀ��������Ŀ��
	 */
	private void updateConstantItem(ItemsVO itemVO) throws DAOException {
		String where = getCacuRangeWhere();
		String sql = "update wa_ba_sch_psns set wa_ba_sch_psns." + itemVO.getCode() + " = ? " + where;
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(itemVO.getValue());
		getBaseDao().executeUpdate(sql, parameter);
	}

	public String getCacuRangeWhere() {
		return WherePartUtil.addWhereKeyWord2Condition(" where wa_ba_sch_psns.pk_ba_sch_h='" + ((WaBaSchBVO) schbvos[0]).getPk_ba_sch_h() + "'");
	}

	/**
	 * ��������
	 * 
	 * @throws Exception
	 */
	public String parse(String formula) throws Exception {
		/*
		//  ��������
		String pk_country = "0001Z010000000079UJJ";

		Map<String, FunctionVO> hashMap = HrWaXmlReader.getInstance().getFormulaParserByzonePK(pk_country);

		Iterator<String> iterator = hashMap.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			FunctionVO functionVO = hashMap.get(key);
			if (FormulaParseHelper.isExist(formula, functionVO)) {
				IFormulaParser formulaParse = getFormulaParse(functionVO);
				formula =
						formulaParse.parse(WorkbenchEnvironment.getInstance().getLoginUser().getPk_org(), formula, AppContext.getInstance(), functionVO);
			}
		}
		 */
		if (StringUtils.isNotEmpty(formula) && formula.indexOf("(") > -1) {
			String functionName = formula.substring(0, formula.indexOf("("));
			String param = formula.substring(formula.indexOf("(") + 1, formula.indexOf(")"));
			String[] params = param.split(",");
			/*
			 * ���Ϊ2����
			 * valueOfWadoc(#2#207#,#123#12#)
			 * func��valueOfWadoc
			 * params:#2#207#  #123#12
			 */
			formula = translateFormulaSub(functionName, params);
		}
		//

		return formula;
	}

	private String translateFormulaSub(String functionName, String[] args) throws Exception {
		WaBaCommonProcess process =
				(WaBaCommonProcess) Class.forName("nc.impl.hrwa.func.WaBa" + toUpperCaseFirst(functionName) + "Process").newInstance();
		return process.parse(null, functionName, (Object[]) args);

	}

	/**
	 * תΪ��ִ�е�SQL���
	 * 
	 * @param itemVO
	 * @param sqlFragment
	 * @return
	 * @throws BusinessException
	 */
	public String translate2ExecutableSql(ItemsVO itemVO, SqlFragment sqlFragment) throws BusinessException {
		String value = sqlFragment.getValue();

		// value = parse(value);
		value = FunctionParse.addSourceTable2Value(value);

		String where = getCacuRangeWhere();

		// ������ֵ�ͣ����Ĭ��ֵ0
		value = addDefaultVaule(itemVO, value);
		StringBuilder sbd = new StringBuilder();
		sbd.append("update wa_ba_sch_psns set wa_ba_sch_psns." + itemVO.getCode() + " = (" + value + ") " + where);
		return sbd.toString();

	}

	/**
	 * ���Ĭ��ֵ0
	 * 
	 * @param itemVO
	 * @param value
	 * @return
	 */
	private String addDefaultVaule(ItemsVO itemVO, String value) {
		//		if (itemVO.getDatatype().equals(TypeEnumVO.FLOATTYPE.value())) {
		return " isnull((" + value + "),0)";
		//		}

		//		return value;

	}

	public void setWaDataCacuRange4Class(String where) throws BusinessException {

		if (StringUtils.isBlank(where)) {
			// where =
			// WherePartUtil.getCommonWhereCondtion4Data(getLoginContext().getWaLoginVO());
		} else {
			// where = where + " and " +
			// WherePartUtil.getCommonWhereCondtion4Data(getLoginContext().getWaLoginVO());
		}

		// String creator =
		// AppContext.getInstance().getPkUser();
		// String pk_wa_class =
		// getLoginContext().getPk_wa_class();
		// ɾ���ø÷������㷶Χ�ڵ�����
		StringBuffer deleteWa_cacu_data = new StringBuffer();
		// deleteWa_cacu_data.append("delete from wa_cacu_data  ");
		// deleteWa_cacu_data.append(" where pk_wa_class = '"
		// + pk_wa_class +
		// "' ");

		// ����.
		StringBuffer insert2Wa_cacu_data = new StringBuffer();
		// insert2Wa_cacu_data.append("insert into wa_cacu_data ");
		// // 1
		// insert2Wa_cacu_data.append("  (pk_cacu_data,  taxtype , taxtableid , isndebuct,pk_wa_class, pk_wa_data, pk_psndoc, cacu_value, creator, currencyrate,redatamode,workorg) ");
		// // 2
		// insert2Wa_cacu_data.append("  select pk_wa_data , taxtype , taxtableid , isndebuct, pk_wa_class, pk_wa_data, pk_psndoc, 0, '"
		// + creator + "',  " +
		// getCurrenyRate() + "," +
		// getRedataMode() +
		// ",workorg "); // 3
		// insert2Wa_cacu_data.append("    from wa_data ");
		// insert2Wa_cacu_data.append("   where "
		// + where);

		executeSQLs(deleteWa_cacu_data, insert2Wa_cacu_data);
	}

	/**
	 * ��ʼ��������䷽���������Ŀ �ǹ̶���3��
	 * <p>
	 * 1��Чϵ���������ǹ�ʽ <br/>
	 * 2��׼ְλн�꣺������н�귽�������<br/>
	 * 3��׼��Ч����=��Чϵ��*��׼ְλн��
	 * <P>
	 */
	protected void initItems() throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from wa_ba_item where isnull(dr,0)=0 ");
		// �õ����Ǳ���Ҫ�������Ŀ
		itemVOs = executeQueryVOs(sqlBuffer.toString(), ItemsVO.class);

	}

	/**
	 * ��ȡ����������
	 * 
	 * @param functionVO
	 * @return
	 * @throws BusinessException
	 */
	public static IFormulaParser getFormulaParse(FunctionVO functionVO) throws BusinessException {
		IFormulaParser formulaParse = null;

		String parse = functionVO.getProcess();

		try {
			formulaParse = (IFormulaParser) Class.forName(parse).newInstance();
		} catch (InstantiationException e) {
			throw new BusinessException(ResHelper.getString("6013commonbasic", "06013commonbasic0034"));// "�������������� ʱʧ��."
		} catch (IllegalAccessException e) {
			throw new BusinessException(ResHelper.getString("6013commonbasic", "06013commonbasic0034"));
		} catch (ClassNotFoundException e) {
			throw new BusinessException(ResHelper.getString("6013commonbasic", "06013commonbasic0034"), parse);
		}

		return formulaParse;
	}

	/**
	 * ����ĸתΪ��д
	 * 
	 * @param name
	 * @return
	 */
	public static String toUpperCaseFirst(String name) {
		char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);
	}

	public ItemsVO[] getItemVOs() {
		return itemVOs;
	}

	public void setItemVOs(ItemsVO[] itemVOs) {
		this.itemVOs = itemVOs;
	}

	private Set<String> setPk_wa_itemSet() throws DAOException {
		pk_wa_itemSet = new HashSet<String>();
		return pk_wa_itemSet;
	}

	private String[] getPreviousPeriod() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(this.year));
		cal.set(Calendar.MONTH, Integer.parseInt(this.period));
		cal.add(Calendar.MONTH, -1);
		int month = cal.get(Calendar.MONTH) - 1;
		return new String[] { cal.get(Calendar.YEAR) + "", month < 10 ? "0" + month : month + "" };
	}

}