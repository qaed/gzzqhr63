package nc.ui.ta.psncalendar.view.circularlyarrange;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.pub.BUPanel;
import nc.ui.ta.pub.ICompWithValidateFunc;
import nc.ui.ta.pub.selpsn.ConditionSelPsnDateScopePanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.util.SqlWhereUtil;

import org.apache.commons.lang.StringUtils;

/**
 * ѡ����Ա��panel
 * @author zengcheng
 *
 */
public class SelPsnPanel extends UIPanel implements ICompWithValidateFunc, AppEventListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -7304690044562845055L;

	private PsnCalendarAppModel model;

	//ѡ����Ա��Χ�����ڷ�Χ��panel
	private ConditionSelPsnDateScopePanel selPanel = null;
	//ѡ���Ƿ񸲸����й���������checkbox
	private UICheckBox overrideExistCalendarCheck;
	//�Ƿ���Ҫѡ��ҵ��Ԫ����
	private boolean needBURef = true;
	private BUPanel buPanel = null;

	public SelPsnPanel() {

	}

	public void init() {
		setLayout(new BorderLayout());
		if (needBURef) {
			add(getBuPanel(), BorderLayout.NORTH);
		}
		add(getSelPanel(), BorderLayout.CENTER);
		add(getOverrideExistCalendarCheck(), BorderLayout.SOUTH);
	}

	public BUPanel getBuPanel() {
		if (buPanel == null) {
			buPanel = new BUPanel();
			buPanel.setModel(getModel());
			buPanel.init();
		}
		return buPanel;
	}

	public FromWhereSQL getQuerySQL() {
		//tsy Ա�������Ű���Ӳ���Ȩ�޿���
		FromWhereSQL fromWhereSQL = selPanel.getQuerySQL();
		if (fromWhereSQL.getFrom() == null) {//�ղ�ѯ�������Լ�����һ��fromwheresql
			fromWhereSQL = new FromWhereSQLImpl();
			((FromWhereSQLImpl) fromWhereSQL).setFrom("tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob");
			Map<String, String> aliasMap = new HashMap<String, String>();
			aliasMap.put(".", "tbm_psndoc");
			aliasMap.put("pk_psnjob", "T1");
			((FromWhereSQLImpl) fromWhereSQL).setAttrpath_alias_map(aliasMap);
		}
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String tableAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob");
		// ����Ȩ��
		String permission =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, tableAlias);
		if (StringUtils.isNotBlank(permission)) {
			sqlWhereUtil.and(permission);
		}
		Logger.error("���Ȩ�޺�sql:" + sqlWhereUtil.getSQLWhere());

		return new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
	}

	public UFLiteralDate getBeginDate() {
		return selPanel.getBeginDate();
	}

	public UFLiteralDate getEndDate() {
		return selPanel.getEndDate();
	}

	public boolean isOverride() {
		return getOverrideExistCalendarCheck().isSelected();
	}

	protected ConditionSelPsnDateScopePanel getSelPanel() {
		if (selPanel == null) {
			selPanel = new ConditionSelPsnDateScopePanel();
			selPanel.setModel(getModel());
			selPanel.init();
		}
		return selPanel;
	}

	protected UICheckBox getOverrideExistCalendarCheck() {
		if (overrideExistCalendarCheck == null) {
			overrideExistCalendarCheck = new UICheckBox(ResHelper.getString("6017psncalendar", "06017psncalendar0057")
			/*@res "�������й�������"*/);
			overrideExistCalendarCheck.setSelected(true);
		}
		return overrideExistCalendarCheck;
	}

	public PsnCalendarAppModel getModel() {
		return model;
	}

	public void setModel(PsnCalendarAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void validateData() throws ValidationException {
		if (needBURef && StringUtils.isBlank(getPK_BU()))
			throw new ValidationException(ResHelper.getString("6017psncalendar", "06017psncalendar0058")
			/*@res "ҵ��Ԫ����Ϊ�գ�"*/);
		getSelPanel().validateData();
	}

	public boolean isNeedBURef() {
		return needBURef;
	}

	public void setNeedBURef(boolean needBURef) {
		this.needBURef = needBURef;
	}

	public String getPK_BU() {
		if (needBURef)
			return getBuPanel().getPK_BU();
		return null;
	}

	@Override
	public void handleEvent(AppEvent event) {

	}
}