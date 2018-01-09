package nc.ui.ta.pub.selpsn;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.border.UITitledBorder;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.ui.ta.pub.QueryEditorListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.util.SqlWhereUtil;

/**
 * ѡ����Ա�����Ĺ���panel
 * @author zengcheng
 *
 */
public class ConditionSelPsnPanel extends UIPanel implements AppEventListener {

	private static final long serialVersionUID = 1L;

	private AbstractUIAppModel model;

	public UIPanel uiAddPanel = null;

	private QueryConditionEditor queryEditor = null;

	private String curPk_org;//��¼��ǰ��֯�������ַ�����������AppEventConst.MODEL_INITIALIZED�¼�����ʱ���ж��Ƿ�����֯�ı��ˣ���Ϊ�������е�AppEventConst.MODEL_INITIALIZED�¼�������֯�ı�

	private String orgPermissionSQL;//��֯Ȩ�޵�SQL
	private String deptPermissionSQL;//����Ȩ�޵�SQL
	private String psnclPermissionSQL;//��Ա���Ȩ�޵�SQL

	public ConditionSelPsnPanel() {
	}

	public ConditionSelPsnPanel(LayoutManager p0) {
		super(p0);
	}

	public ConditionSelPsnPanel(boolean p0) {
		super(p0);
	}

	public ConditionSelPsnPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	/**
	 * ���췽�������ô˷������ɴ����ʹ������������init����
	 * �˷����������ǣ�
	 * ����ѯ����ص������ϣ������ò�ѯ�����һЩĬ��������������֯
	 */
	public void init() {
		setLayout(new BorderLayout());
		initPermissionSQL();
		add(getQueryEditor(), BorderLayout.CENTER);
		if (getAddPanel() != null)
			add(getAddPanel(), BorderLayout.SOUTH);
	}

	public FromWhereSQL getQuerySQL() {
		//		return queryEditor.getQueryScheme().getTableJoinFromWhereSQL();
		//tsy ���ڵ�������������Ӳ���Ȩ��
		FromWhereSQL fromWhereSQL = queryEditor.getQueryScheme().getTableJoinFromWhereSQL();
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
		fromWhereSQL = new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());

		return new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
	}

	/**
	 * ��ȡ��ѯ�����༭��
	 *
	 */
	public QueryConditionEditor getQueryEditor() {
		if (queryEditor == null) {
			TemplateInfo tempInfo = new TemplateInfo();
			tempInfo.setFunNode("60170psndocqry");
			tempInfo.setNodekey("6017psndocqry");
			tempInfo.setCurrentCorpPk(getModel().getContext().getPk_org());
			tempInfo.setPk_Org(getModel().getContext().getPk_org());
			tempInfo.setUserid(getModel().getContext().getPk_loginUser());
			queryEditor = new QueryConditionEditor(tempInfo);
			queryEditor.setVisibleQSTreePanel(false);//����ʾ��ѯ����
			QueryEditorListener listener = new QueryEditorListener();
			listener.setModel(getModel());
			listener.setQueryConditionEditor(queryEditor);
			queryEditor.showPanel();
			queryEditor.setBorder(new UITitledBorder(ResHelper.getString("6017basedoc", "06017basedoc1476")
			/*@res "��Ա��Χ"*/));
		}
		return queryEditor;
	}

	/**
	 * �������������༭��
	 *
	 */
	public void resetQueryEditor() {
		remove(queryEditor);
		queryEditor = null;
		add(getQueryEditor(), BorderLayout.CENTER);
	}

	public UIPanel getAddPanel() {
		return uiAddPanel;
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	/*
	 * �˷���������֯�仯�¼��������ò�ѯ����ϲ��յķ�Χ (non-Javadoc)
	 *
	 * @see nc.ui.uif2.AppEventListener#handleEvent(nc.ui.uif2.AppEvent)
	 */
	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MODEL_INITIALIZED.equals(event.getType())) {
			String pk_org = getModel().getContext().getPk_org();
			if (pk_org != null && curPk_org != null && pk_org.equals(curPk_org))
				return;
			curPk_org = pk_org;
			resetQueryEditor();
		}
	}

	/**
	 * ��ʼ��Ȩ��SQL
	 * ��ΪȨ��ֻ���û��йأ����ȡһ�μ���
	 */
	protected void initPermissionSQL() {
		//		String[] permissionSQLs = DataPermissionUtils.getDataPermissionSQLWherePart(new String[]{"","",""}, new String[]{IRefConst.DATAPOWEROPERATION_CODE,IRefConst.DATAPOWEROPERATION_CODE,IRefConst.DATAPOWEROPERATION_CODE});
		//		if(!org.apache.commons.lang.ArrayUtils.isEmpty(permissionSQLs)){
		//			orgPermissionSQL = permissionSQLs[0];
		//			deptPermissionSQL = permissionSQLs[1];
		//			psnclPermissionSQL = permissionSQLs[2];
		//		}
	}

	protected String getOrgPermissionSQL() {
		return orgPermissionSQL;
	}

	protected String getDeptPermissionSQL() {
		return deptPermissionSQL;
	}

	protected String getPsnclPermissionSQL() {
		return psnclPermissionSQL;
	}

}