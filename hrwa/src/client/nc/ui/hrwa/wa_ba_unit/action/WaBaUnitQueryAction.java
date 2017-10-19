package nc.ui.hrwa.wa_ba_unit.action;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import nc.ui.hrwa.wa_ba_unit.ace.maintain.AceWaBaUnitDataManager;
import nc.ui.hrwa.wa_ba_unit.model.WaBaItemTreeModel;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.query2.DefaultQueryConditionDLG;
import nc.ui.pubapp.uif2app.query2.ICustomQueryPanelValidator;
import nc.ui.pubapp.uif2app.query2.IQueryConditionDLGInitializer;
import nc.ui.pubapp.uif2app.query2.IQueryPanel;
import nc.ui.pubapp.uif2app.query2.QueryConditionDLGDelegator;
import nc.ui.pubapp.uif2app.query2.action.QuerySchemeAction;
import nc.ui.querytemplate.IQueryConditionDLG;
import nc.ui.querytemplate.QueryAreaCreator;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.QueryTempletLoader;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.ToftPanelAdaptor;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.progress.TPAProgressUtil;
import nc.ui.uif2.editor.QueryTemplateContainer;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ModelMethod;
import nc.uif2.annoations.ModelType;
import nc.vo.ecpubapp.pattern.exception.ExceptionUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.res.Variable;
import nc.vo.querytemplate.TemplateInfo;
import nc.vo.querytemplate.queryscheme.QuerySchemeVO;
import nc.vo.querytemplate.queryscheme.SimpleQuerySchemeVO;

@SuppressWarnings("restriction")
public class WaBaUnitQueryAction extends NCAction {

	private static final long serialVersionUID = 7438130048789697075L;

	public WaBaUnitQueryAction() {
		setBtnName("查询");
		setCode("query");
		this.queryExecutor = new WaBaUnitQueryExecutor(this);
	}

	private QueryConditionDLGDelegator queryAreaDelegator;

	protected QueryConditionDLGDelegator qryCondDLGDelegator;

	private AceWaBaUnitDataManager dataManager;

	private String funNode;

	private Integer maxQueryCount;

	private String nodeKey;

	private IQueryConditionDLGInitializer qryCondDLGInitializer;

	private WaBaUnitQueryExecutor queryExecutor;

	// 查询方案Action列表
	private List<QuerySchemeAction> schemeActions = null;

	private IAutoShowUpComponent showUpComponent;

	private QueryTemplateContainer templateContainer = null;

	private String progressName;

	protected IQueryConditionDLG iQueryDlg = null;

	private boolean queryareaInit = false;

	private boolean hasQueryArea = true;

	/**
	 * 默认显示查询进度条
	 */
	private boolean showProgress = true;

	public boolean isShowProgress() {
		return showProgress;
	}

	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
	}

	private TPAProgressUtil tpaProgressUtil;

	private boolean isTPAMonitor = true;

	/**
	 * 加载快速查询区时默认以查询方案里保存的值覆盖掉事件联动关系设置的值，若业务组不希望覆盖则将改属性设为false
	 */
	private boolean reloadQuickAreaValue = true;

	private WaBaItemTreeModel model;

	public String getProgressName() {
		return progressName;
	}

	public void setProgressName(String progressName) {
		this.progressName = progressName;
	}

	@SuppressWarnings("unused")
	private IQueryConditionDLG getQueryCoinditionDLG() {
		if (iQueryDlg == null) {
			iQueryDlg = createQueryDlg_New();
			iQueryDlg.getQryCondEditor().getQueryContext().setReloadQuickAreaValue(isReloadQuickAreaValue());
		}
		return iQueryDlg;
	}

	protected IQueryConditionDLG createQueryDlg_New() {
		TemplateInfo tempinfo = getTemplateInfo();
		if (getTemplateContainer() == null) {
			QueryAreaCreator queryAreaCreator = new QueryAreaCreator(tempinfo, null);
			queryAreaCreator.setQcdParent(this.getModel().getContext().getEntranceUI());
			return queryAreaCreator;
		} else {
			QueryAreaCreator queryAreaCreator = new QueryAreaCreator(tempinfo, getTemplateContainer().getQueryTempletLoader());
			queryAreaCreator.setQcdParent(this.getModel().getContext().getEntranceUI());
			return queryAreaCreator;
		}
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if (this.getQryDLGDelegator().showModal() == UIDialog.ID_OK) {
			this.processQuery();
		}
	}

	public void doSimpleSchemeQuery(final SimpleQuerySchemeVO vo) {

		if (!hasQueryArea) {
			doSimpleSchemeQueryInner(vo);
		} else {
			boolean hasExec = true;
			synchronized (this) {
				if (!WaBaUnitQueryAction.this.queryareaInit) {
					SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
						@Override
						protected Object doInBackground() throws Exception {
							do {
								Thread.sleep(100);
							} while (!WaBaUnitQueryAction.this.queryareaInit);
							return null;
						}

						@Override
						protected void done() {
							doSimpleSchemeQueryInner(vo);
						}
					};
					sw.execute();
				} else {
					hasExec = false;
				}
			}
			if (!hasExec) {
				doSimpleSchemeQueryInner(vo);
			}
		}
	}

	private void doSimpleSchemeQueryInner(SimpleQuerySchemeVO vo) {
		QueryConditionDLG qcd = this.getQryDLGDelegator().getQueryConditionDLG();
		if (qcd != null) {
			try {
				qcd.initUIData();
				// IQueryScheme scheme =
				// qcd.getQuerySchemeByPK(vo.getPrimaryKey());
				IQueryScheme scheme = qcd.getQuerySchemeByName(vo.toString());
				this.queryExecutor.doQuery(scheme);
			} catch (BusinessException e) {
				ExceptionUtils.wrappException(e);
			}
		}
	}

	public AceWaBaUnitDataManager getDataManager() {
		return this.dataManager;
	}

	public String getFunNode() {
		if (this.funNode == null) {
			this.funNode = this.getModel().getContext().getNodeCode();
		}
		return this.funNode;
	}

	public Integer getMaxQueryCount() {
		if (this.maxQueryCount == null) {
			this.maxQueryCount = Integer.valueOf(Variable.getMaxQueryCount());
		}
		return this.maxQueryCount;
	}

	@ModelMethod(modelType = ModelType.AbstractUIAppModel, methodType = MethodType.GETTER)
	public WaBaItemTreeModel getModel() {
		return this.model;
	}

	public String getNodeKey() {
		return this.nodeKey;
	}

	public IQueryConditionDLGInitializer getQryCondDLGInitializer() {
		return this.qryCondDLGInitializer;
	}

	public QueryConditionDLGDelegator getQryDLGDelegator() {
		if (iQueryDlg != null) {
			DefaultQueryConditionDLG qcd = (DefaultQueryConditionDLG) iQueryDlg.createQCDByIQCD(iQueryDlg);
			qcd.getQryCondEditor().getQueryContext().setReloadQuickAreaValue(isReloadQuickAreaValue());
			this.qryCondDLGDelegator = this.createQryDLGDelegator(qcd);
			return qryCondDLGDelegator;

		} else {
			if (this.qryCondDLGDelegator == null) {
				TemplateInfo tempinfo = getTemplateInfo();
				// 返回供应链的查询对话框
				this.qryCondDLGDelegator = this.createQryDLGDelegator(tempinfo);
				this.qryCondDLGDelegator.getQce().getQueryContext().setReloadQuickAreaValue(isReloadQuickAreaValue());
			}
			return this.qryCondDLGDelegator;
		}
	}

	private TemplateInfo getTemplateInfo() {
		TemplateInfo tempinfo = new TemplateInfo();
		tempinfo.setPk_Org(this.getModel().getContext().getPk_group());
		tempinfo.setFunNode(this.getFunNode());
		tempinfo.setUserid(this.getModel().getContext().getPk_loginUser());
		tempinfo.setNodekey(this.getNodeKey());
		tempinfo.setSealedDataShow(true);
		return tempinfo;
	}

	public IAutoShowUpComponent getShowUpComponent() {
		return this.showUpComponent;
	}

	public QueryTemplateContainer getTemplateContainer() {
		return this.templateContainer;
	}

	public void preFetchQueryTemplateData() {
		QueryTemplateContainer container = this.getTemplateContainer();
		if (container != null) {
			QueryTempletLoader loader = container.getQueryTempletLoader();
			if (loader != null) {
				loader.getQueryTempletData();
			}
		}
	}

	public void setDataManager(AceWaBaUnitDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public void setFunNode(String funNode) {
		this.funNode = funNode;
	}

	public void setMaxQueryCount(Integer maxQueryCount) {
		this.maxQueryCount = maxQueryCount;
	}

	@ModelMethod(modelType = ModelType.AbstractUIAppModel, methodType = MethodType.SETTER)
	public void setModel(WaBaItemTreeModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public void setQryCondDLGDelegator(QueryConditionDLGDelegator qryCondDLGDelegator) {
		this.qryCondDLGDelegator = qryCondDLGDelegator;
	}

	public void setQryCondDLGInitializer(IQueryConditionDLGInitializer qryCondDLGInitializer) {
		this.qryCondDLGInitializer = qryCondDLGInitializer;
	}

	public void setShowUpComponent(IAutoShowUpComponent showUpComponent) {
		this.showUpComponent = showUpComponent;
	}

	public void setTemplateContainer(QueryTemplateContainer templateContainer) {
		this.templateContainer = templateContainer;
	}

	protected QueryConditionDLGDelegator createQryDLGDelegator(TemplateInfo ti) {
		QueryConditionDLGDelegator dlgDelegator;
		if (this.getTemplateContainer() == null) {
			dlgDelegator = new QueryConditionDLGDelegator(this.getModel().getContext(), ti);
		} else {
			dlgDelegator =
					new QueryConditionDLGDelegator(this.getModel().getContext(), ti, this.getTemplateContainer().getQueryTempletLoader());
		}
		this.initQueryConditionDLG(dlgDelegator);
		return dlgDelegator;
	}

	protected QueryConditionDLGDelegator createQryDLGDelegator(DefaultQueryConditionDLG qcd) {
		QueryConditionDLGDelegator dlgDelegator = new QueryConditionDLGDelegator(qcd);
		// 将DefaultQueryConditionDLG特有一些设置回设回去。
		List<IQueryPanel> customQueryPanels = this.queryAreaDelegator.getCustomQueryPanels();
		((DefaultQueryConditionDLG) dlgDelegator.getQueryConditionDLG()).setCustomQueryPanels(customQueryPanels);

		List<ICustomQueryPanelValidator> customQueryValidators = this.queryAreaDelegator.getCustomQueryValidators();
		for (ICustomQueryPanelValidator validator : customQueryValidators) {
			dlgDelegator.registerCustomQueryPanelValidator(validator);
		}

		Map<String, List<String>> redundancyInfo = this.queryAreaDelegator.getRedundancyInfo();
		((DefaultQueryConditionDLG) dlgDelegator.getQueryConditionDLG()).setRedundancyInfo(redundancyInfo);
		// this.initQueryConditionDLG(dlgDelegator);
		return dlgDelegator;
	}

	protected void executeQuery(IQueryScheme queryScheme) {
		this.getDataManager().initModelByQueryScheme(queryScheme);

	}

	/**
	 * @param dlgDelegator
	 */
	protected void initQueryConditionDLG(QueryConditionDLGDelegator dlgDelegator) {
		if (this.getQryCondDLGInitializer() != null) {
			this.getQryCondDLGInitializer().initQueryConditionDLG(dlgDelegator);
		}
	}

	// String[] getFuncPermissionOrg() {
	// LoginContext context = this.getModel().getContext();
	// OrgVO[] orgVOs = context.getFuncInfo().getFuncPermissionOrgVOs();
	// if (orgVOs == null) {
	// return null;
	// }
	// String[] pk_orgs = new String[orgVOs.length];
	// for (int i = 0; i < pk_orgs.length; i++) {
	// pk_orgs[i] = orgVOs[i].getPk_org();
	// }
	// return pk_orgs;
	// }

	@Override
	protected boolean isActionEnable() {
		return this.getModel().getUiState() == UIState.INIT || this.getModel().getUiState() == UIState.NOT_EDIT;
	}

	protected void showQueryInfo() {
		int size = 0;

		ModelDataDescriptor des = getModel().getCurrentDataDescriptor();
		if (des != null) {
			size = des.getCount();
		}
		//		if (this.getModel() instanceof BillManageModel) {
		//			size = ((BillManageModel) this.getModel()).getData().size();
		//
		//		} else if (this.getModel() instanceof BatchBillTableModel) {
		//			size = ((BatchBillTableModel) this.getModel()).getRows().size();
		//		}
		if (size > 0) {
			// ShowStatusBarMsgUtil.showStatusBarMsg("查询成功，已查到" + size + "张单据。",
			// this
			// .getModel().getContext());
			ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("pubapp_0", "0pubapp-0265", null, new String[] { "" + size })/*
																																										 * @res
																																										 * "查询成功，已查到{0}张单据。"
																																										 */, this.getModel().getContext());
		} else {
			ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQueryNullInfo(), this.getModel().getContext());
		}
	}

	public void doSchemeAction(QuerySchemeVO vo) {

		QueryConditionDLGDelegator qcd = this.getQryDLGDelegator();
		final IQueryScheme scheme = qcd.getQuerySchemeByVO(vo);
		if (qcd != null) {
			this.queryExecutor.doQuery(scheme);
		}
	}

	/**
	 * 调用Processor执行查询行为
	 */
	private void processQuery() {
		IQueryScheme queryScheme = this.getQryDLGDelegator().getQueryScheme();
		this.queryExecutor.doQuery(queryScheme);
		afterProcessQuery(queryScheme);
	}

	//供业务组做查询后操作使用 20130719 add
	protected void afterProcessQuery(IQueryScheme queryScheme) {
	}

	public boolean isHasQueryArea() {
		return hasQueryArea;
	}

	public void setHasQueryArea(boolean hasQueryArea) {
		this.hasQueryArea = hasQueryArea;
	}

	public boolean isReloadQuickAreaValue() {
		return reloadQuickAreaValue;
	}

	public void setReloadQuickAreaValue(boolean reloadQuickAreaValue) {
		this.reloadQuickAreaValue = reloadQuickAreaValue;
	}

	public TPAProgressUtil getTpaProgressUtil() {
		if (tpaProgressUtil == null && isTPAMonitor) {
			tpaProgressUtil = new TPAProgressUtil();
			tpaProgressUtil.setContext(getModel().getContext());
		}

		return tpaProgressUtil;
	}

	public void setTpaProgressUtil(TPAProgressUtil tpaProgressUtil) {
		this.tpaProgressUtil = tpaProgressUtil;
	}

	public boolean isTPAMonitor() {
		return isTPAMonitor && getModel() != null && getTpaProgressUtil().getContext().getEntranceUI() instanceof ToftPanelAdaptor;
	}

	public void setTPAMonitor(boolean isTPAMonitor) {
		this.isTPAMonitor = isTPAMonitor;
	}

	public List<QuerySchemeAction> getSchemeActions() {
		return schemeActions;
	}

	public void setSchemeActions(List<QuerySchemeAction> schemeActions) {
		this.schemeActions = schemeActions;
	}

}
