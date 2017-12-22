package nc.ui.ta.psndoc.view;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;

import nc.bs.logging.Logger;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.md.model.impl.BusinessEntity;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UITree;
import nc.ui.pub.beans.border.UITitledBorder;
import nc.ui.querytemplate.QueryConditionEditor;
import nc.ui.querytemplate.candidate.ICandidatePanel;
import nc.ui.querytemplate.candidate.MetaDataCandidatePanelType2;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.pub.QueryEditorListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.util.SqlWhereUtil;

@SuppressWarnings("restriction")
public class PsnConditionSelPanel extends UIPanel implements AppEventListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -6340315608535209982L;
	private AbstractUIAppModel model;

	public UIPanel uiAddPanel = null;

	private QueryConditionEditor queryEditor = null;

	private String curPk_org;//��¼��ǰ��֯�������ַ�����������AppEventConst.MODEL_INITIALIZED�¼�����ʱ���ж��Ƿ�����֯�ı��ˣ���Ϊ�������е�AppEventConst.MODEL_INITIALIZED�¼�������֯�ı�

	public PsnConditionSelPanel() {
	}

	public PsnConditionSelPanel(LayoutManager p0) {
		super(p0);
	}

	public PsnConditionSelPanel(boolean p0) {
		super(p0);
	}

	public PsnConditionSelPanel(LayoutManager p0, boolean p1) {
		super(p0, p1);
	}

	/**
	 * ���췽�������ô˷������ɴ����ʹ������������init����
	 * �˷����������ǣ�
	 * ����ѯ����ص������ϣ������ò�ѯ�����һЩĬ��������������֯
	 */
	public void init() {
		setLayout(new BorderLayout());
		add(getQueryEditor(), BorderLayout.CENTER);
		if (getAddPanel() != null)
			add(getAddPanel(), BorderLayout.SOUTH);
	}

	public FromWhereSQL getQuerySQL() {
		//		return queryEditor.getQueryScheme().getTableJoinFromWhereSQL();
		//tsy ���ڵ�������������Ӳ���Ȩ��
		FromWhereSQL fromWhereSQL = queryEditor.getQueryScheme().getTableJoinFromWhereSQL();
		SqlWhereUtil sqlWhereUtil = new SqlWhereUtil(fromWhereSQL.getWhere());
		String alias = "hi_psnjob";
		// ����Ȩ��
		String deptSql =
				HiSQLHelper.getPsnPowerSql(PubEnv.getPk_group(), HICommonValue.RESOUCECODE_DEPT, IRefConst.DATAPOWEROPERATION_CODE, alias);
		if (StringUtils.isNotBlank(deptSql)) {
			Logger.error("����Ȩ��:" + deptSql);
			sqlWhereUtil.and(deptSql);
		} else {
			Logger.error("����֯Ȩ��:" + "	HiSQLHelper.getPsnPowerSql(" + PubEnv.getPk_group() + ", " + HICommonValue.RESOUCECODE_DEPT + "," + IRefConst.DATAPOWEROPERATION_CODE + ", " + alias + ");");
		}
		Logger.error("���Ȩ�޺�sql:" + sqlWhereUtil.getSQLWhere());
		if (fromWhereSQL.getFrom() == null) {//ԭ��ѯ������ȫ�յ�
			Map<String, String> aliasMap = new HashMap<String, String>();
			aliasMap.put(".", "hi_psnjob");
			return new nc.ui.hr.pub.FromWhereSQL("hi_psnjob", sqlWhereUtil.getSQLWhere(), fromWhereSQL, aliasMap);
		}
		return new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL, sqlWhereUtil.getSQLWhere());
	}

	/**
	 * ��ȡ��ѯ�����༭��
	 *
	 */
	public QueryConditionEditor getQueryEditor() {
		if (queryEditor == null) {
			TemplateInfo tempInfo = new TemplateInfo();
			tempInfo.setFunNode("60170psndoc");
			tempInfo.setNodekey("batchadd");
			tempInfo.setCurrentCorpPk(getModel().getContext().getPk_org());
			tempInfo.setPk_Org(getModel().getContext().getPk_org());
			tempInfo.setUserid(getModel().getContext().getPk_loginUser());
			queryEditor = new QueryConditionEditor(tempInfo);
			QueryEditorListener listener = new TBMQueryEditorListener();
			listener.setModel(getModel());
			listener.setQueryConditionEditor(queryEditor);

			ICandidatePanel panel = queryEditor.getCandidatePnl();
			if (panel instanceof MetaDataCandidatePanelType2) {
				UITree tree =
						(UITree) ((UIScrollPane) ((MetaDataCandidatePanelType2) queryEditor.getCandidatePnl()).getComponent(1)).getViewport().getView();
				if (tree != null && tree.getModel() != null && tree.getModel().getRoot() != null) {
					BusinessEntity root = (BusinessEntity) ((DefaultMutableTreeNode) tree.getModel().getRoot()).getUserObject();
					if (root != null) {
						root.setDisplayName(ResHelper.getString("6017psndoc", "06017psndoc0087")
						/*@res "��������"*/);
					}
				}
			}
			queryEditor.showPanel();
			queryEditor.setBorder(new UITitledBorder(ResHelper.getString("6017psndoc", "06017psndoc0088")
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
}