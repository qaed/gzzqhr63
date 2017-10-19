package nc.ui.hrwa.wa_ba_unit.action;

import javax.swing.SwingWorker;

import nc.bs.logging.Logger;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.pubapp.uif2app.query2.action.AfterQuery;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInterceptor;
import nc.vo.pubapp.query2.sql.process.QueryConstants;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class WaBaUnitQueryExecutor implements IQueryExecutor {
	private WaBaUnitQueryAction queryAction = null;

	private AfterQuery afterQuery = null;

	private IProgressMonitor progressMonitor = null;

	public WaBaUnitQueryExecutor(WaBaUnitQueryAction action) {
		this.queryAction = action;
	}

	@Override
	public void doQuery(final IQueryScheme queryScheme) {
		if (queryScheme == null) {
			return;
		}

		if (!queryAction.isShowProgress()) {
			this.queryData(queryScheme);
			// this.showQueryInfo();
			if (queryAction.getShowUpComponent() != null) {
				queryAction.getShowUpComponent().showMeUp();
			}
		} else {
			// ����̫Ƶ��ֱ�ӷ���
			if (progressMonitor != null && !progressMonitor.isDone()) {
				return;
			}

			// ��ý�����������
			progressMonitor =
					queryAction.isTPAMonitor() ? queryAction.getTpaProgressUtil().getTPAProgressMonitor() : NCProgresses.createDialogProgressMonitor(null);
			String name = null;

			if (StringUtils.isBlank(queryAction.getProgressName())) {
				name = NCLangRes.getInstance().getStrByID("_template", "UPP_NewQryTemplate-0141")/*��ѯ�У����Ժ�...*/;
			} else {
				name = queryAction.getProgressName();
			}

			// ��ʼ����,��������δ֪
			progressMonitor.beginTask(name, IProgressMonitor.UNKNOWN_TOTAL_TASK);
			progressMonitor.setProcessInfo(name);

			//�����߳��첽��ѯʱ�����ܻ���ִ��interceptor������������߳�ǰ����interceptor���ݺ����queryAction��interceptor
			final ActionInterceptor backupinterceptor = queryAction.getInterceptor() != null ? queryAction.getInterceptor() : null;
			if (backupinterceptor != null)
				queryAction.setInterceptor(null);

			SwingWorker<Object[], Object> sw = new SwingWorker<Object[], Object>() {
				private Exception failed = null;

				protected Object[] doInBackground() throws Exception {
					try {
						//��ʼ��ѯʱ�������ݵ���������ע���ȥ
						if (backupinterceptor != null)
							queryAction.setInterceptor(backupinterceptor);

						queryData(queryScheme);
					} catch (RuntimeException e) {
						failed = e;
					}
					return null;
				}

				@Override
				protected void done() {
					if (progressMonitor != null) {
						progressMonitor.done();
						progressMonitor = null;
					}
					if (failed != null) {
						ShowStatusBarMsgUtil.showErrorMsgWithClear(NCLangRes.getInstance().getStrByID("uif2", "QueryAction-0000")/*��ѯʧ��*/, failed.getMessage(), queryAction.getModel().getContext());
						Logger.error(failed.getMessage(), failed);
						return;
					}

					if (queryAction.getShowUpComponent() != null) {
						queryAction.getShowUpComponent().showMeUp();
					}
				}
			};

			sw.execute();
		}

	}

	public void queryData(IQueryScheme queryScheme) {
		queryAction.getQryDLGDelegator().fillQuerySheme(queryScheme);
		queryScheme.put(QueryConstants.MAX_QUERY_COUNT_CONSTANT, queryAction.getMaxQueryCount());
		queryScheme.put(QueryConstants.KEY_FUNC_NODE, queryAction.getFunNode());
		queryAction.executeQuery(queryScheme);
		queryAction.showQueryInfo();
	}

	public AfterQuery getAfterQuery() {
		return afterQuery;
	}

	public void setAfterQuery(AfterQuery afterQuery) {
		this.afterQuery = afterQuery;
	}
}
