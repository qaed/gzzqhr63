package nc.ui.hrwa.wa_ba_unit.ace.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hrwa.IWaBaUnitMaintain;
import nc.itf.om.IDeptQueryService;
import nc.itf.om.IOrgInfoQueryService;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.ui.pe.pub.multichktree.MultiChkTree;
import nc.ui.pe.pub.multichktree.MultiChkTreeNode;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIList;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIScrollPane;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.beans.UITree;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.dialog.ButtonUtils;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.hr.frame.util.IconUtils;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.orginfo.AggHROrgVO;
import nc.vo.om.orginfo.HROrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.query.ConditionVO;
import nc.vo.pubapp.AppContext;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.wa_ba.unit.AggWaBaUnitHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

public class FromDeptGenDialog extends HrDialog implements ActionListener, KeyListener {
	private LoginContext context;
	private UISplitPane splitPane = null;
	private UIPanel contentPanel = null;
	private UIPanel leftPanel = null;
	private UIScrollPane rightPanel = null;
	private UIPanel southPanel = null;
	private UIPanel levelPanel = null;
	private UIScrollPane treePanel = null;
	private UIButton[] btnLevel = null;
	private UIButton btnLevelE = null;
	private UIList grplist = null;

	private UITree uiTree = null;

	private UIButton btn_close = null;
	private UIButton btn_ok = null;

	//	ArrayList<EvaGroupVO> exaGrps = null;

	HashMap<String, ConditionVO[]> conditionMap = null;

	HashMap<HRDeptVO, String> checkedHRDeptVOMap = new HashMap();

	public FromDeptGenDialog(Container parent, LoginContext context) {
		super(parent);
		setContext(context);
		getUITree().setModel(createTreeModel());
		setName("FromDeptGenGrpDialog");
		setTitle(ResHelper.getString("60290106", "0602901060136"));

		setSize(650, 420);
		setContentPane(getContentPane());
	}

	public UIPanel getContentPane() {
		if (this.contentPanel == null) {
			this.contentPanel = new UIPanel();
			this.contentPanel.setName("contentPanel");

			BorderLayout bl = new BorderLayout();

			this.contentPanel.setLayout(bl);

			this.contentPanel.add(getSplitPane(), "Center");
			this.contentPanel.add(getSouthPanel(), "South");
		}
		return this.contentPanel;
	}

	public UIPanel getSouthPanel() {
		if (this.southPanel == null) {
			this.southPanel = new UIPanel();
			this.southPanel.setName("southPanel");
			this.southPanel.setPreferredSize(new Dimension(50, 80));
			BorderLayout fl = new BorderLayout();
			this.southPanel.setLayout(fl);
			UILabel labelNote = new UILabel();
			labelNote.setText(ResHelper.getString("60290106", "0602901060137") + "  ");

			ImageIcon icon1 = IconUtils.getInstance().getIcon(IconUtils.ICON_TREE_CHECKED);
			UILabel label1 = new UILabel();
			label1.setIcon(icon1);
			label1.setText(ResHelper.getString("60290106", "0602901060047") + "  ");

			ImageIcon icon2 = IconUtils.getInstance().getIcon(IconUtils.ICON_TREE_PTCHECKED);
			UILabel label2 = new UILabel();
			label2.setIcon(icon2);
			label2.setText(ResHelper.getString("60290106", "0602901060048") + "  ");

			ImageIcon icon3 = IconUtils.getInstance().getIcon(IconUtils.ICON_TREE_UNCHECKED);
			UILabel label3 = new UILabel();
			label3.setIcon(icon3);
			label3.setText(ResHelper.getString("60290106", "0602901060049") + "  ");

			UIPanel btnPanel = new UIPanel();
			btnPanel.setName("btnPanel");
			FlowLayout btnLout = new FlowLayout();
			btnPanel.setLayout(btnLout);
			btnLout.setAlignment(2);
			btnPanel.add(getBtn_ok(), getBtn_ok().getName());
			btnPanel.add(getBtn_close(), getBtn_close().getName());

			UIPanel LabelPan = new UIPanel();
			FlowLayout labelLayout = new FlowLayout();
			LabelPan.setLayout(labelLayout);
			labelLayout.setAlignment(0);

			LabelPan.add(labelNote, ResHelper.getString("60290106", "0602901060138"));

			LabelPan.add(label1, ResHelper.getString("60290106", "0602901060138"));

			LabelPan.add(label2, ResHelper.getString("60290106", "0602901060139"));

			LabelPan.add(label3, ResHelper.getString("60290106", "0602901060140"));

			this.southPanel.add(LabelPan, "North");
			this.southPanel.add(btnPanel, "South");
		}
		return this.southPanel;
	}

	public UIScrollPane getRightPanel() {
		if (this.rightPanel == null) {
			this.rightPanel = new UIScrollPane();
			this.rightPanel.setAutoscrolls(true);
			this.rightPanel.setVerticalScrollBarPolicy(20);
			this.rightPanel.setHorizontalScrollBarPolicy(30);
			this.rightPanel.setMinimumSize(new Dimension(3, 3));
			this.rightPanel.setViewportView(getGrplist());
			this.rightPanel.setLayout(new ScrollPaneLayout());
		}

		return this.rightPanel;
	}

	public UIPanel getLeftPanel() {
		if (this.leftPanel == null) {
			this.leftPanel = new UIPanel();
			this.leftPanel.setLayout(new BorderLayout());

			this.leftPanel.add(getTreePanel(), "Center");
			this.leftPanel.setMinimumSize(new Dimension(3, 3));
		}
		return this.leftPanel;
	}

	public UIPanel getLevelPanel() {
		if (this.levelPanel == null) {
			this.levelPanel = new UIPanel();
			this.levelPanel.setName("levelPanel");
			this.levelPanel.setPreferredSize(new Dimension(150, 22));
			this.levelPanel.setLayout(new FlowLayout(0, 3, 2));
			for (int i = 0; i < 6; i++) {
				this.levelPanel.add(getBtnLevel()[i]);
			}

			this.levelPanel.add(getBtnLevelE());
			FlowLayout fl = new FlowLayout();
			fl.setAlignment(0);
			this.levelPanel.setLayout(fl);
		}
		return this.levelPanel;
	}

	public UIButton[] getBtnLevel() {
		if (this.btnLevel == null) {
			this.btnLevel = new UIButton[6];
			String name = "btnLevel";
			String toolTipPre = ResHelper.getString("60290106", "0602901060141");

			String toolTipSuf = ResHelper.getString("60290106", "0602901060142");

			int index = 1;
			for (int i = 0; i < this.btnLevel.length; i++) {
				this.btnLevel[i] = getExpandLevelBtn(name + index, String.valueOf(index), toolTipPre + index + toolTipSuf);

				index++;
				this.btnLevel[i].addActionListener(this);
			}
		}
		return this.btnLevel;
	}

	private UIButton getExpandLevelBtn(String name, String text, String toolTip) {
		Dimension defaultDimension = new Dimension(16, 16);
		UIButton btn = getButton(name, text, toolTip, false, defaultDimension, false);

		btn.setSize(defaultDimension);

		btn.setModel(new DefaultButtonModel() {
			private static final long serialVersionUID = 1L;

			public void setSelected(boolean b) {
				super.setSelected(b);
				getGroup().setSelected(this, b);
			}
		});
		return btn;
	}

	public UIButton getBtnLevelE() {
		if (this.btnLevelE == null) {
			String name = "btnLevelE";
			String text = "E";
			String toolTip = ResHelper.getString("60290106", "0602901060143");

			this.btnLevelE = getExpandLevelBtn(name, text, toolTip);
			this.btnLevelE.addActionListener(this);
		}

		return this.btnLevelE;
	}

	private UIButton getButton(String name, String text, String toolTip, boolean opaque, Dimension dimension, boolean isMyButtonUI) {
		UIButton btn = new UIButton();
		btn.setName(name);
		btn.setText(text);
		btn.setToolTipText(toolTip);
		btn.setOpaque(opaque);
		btn.setPreferredSize(dimension);

		return btn;
	}

	public UIScrollPane getTreePanel() {
		if (this.treePanel == null) {
			this.treePanel = new UIScrollPane();
			this.treePanel.setAutoscrolls(true);
			this.treePanel.setVerticalScrollBarPolicy(20);
			this.treePanel.setHorizontalScrollBarPolicy(30);
			this.treePanel.setMinimumSize(new Dimension(3, 3));
			this.treePanel.setViewportView(getUITree());
			this.treePanel.setColumnHeaderView(getLevelPanel());
		}
		return this.treePanel;
	}

	public UITree getUITree() {
		if (this.uiTree == null) {
			this.uiTree = new MultiChkTree() {
				protected void mouseLeftClickedSelf() {
					FromDeptGenDialog.this.buildCheckedHRDeptVOMap();
				}

			};
			this.uiTree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent event) {
					UITree source = (UITree) event.getSource();
					TreePath selectedPath = source.getSelectionModel().getSelectionPath();
					if (selectedPath == null) {
					}
				}
			});
		}

		return this.uiTree;
	}

	private void buildCheckedHRDeptVOMap() {
		this.checkedHRDeptVOMap = new HashMap();
		List<String> groupListDisplayNames = new ArrayList();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getUITree().getModel().getRoot();

		Enumeration<?> e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			MultiChkTreeNode treeNode = (MultiChkTreeNode) e.nextElement();

			if ((!treeNode.equals((DefaultMutableTreeNode) getUITree().getModel().getRoot())) &&

			(treeNode.getCheckValue() != MultiChkTreeNode.UNCHECKED)) {

				HRDeptVO deptVO = (HRDeptVO) treeNode.getUserObject();
				String strCondition = dealDeptCondition(treeNode);
				if (!this.checkedHRDeptVOMap.containsKey(deptVO)) {
					this.checkedHRDeptVOMap.put(deptVO, strCondition);
				}

				if (treeNode.getCheckValue() == MultiChkTreeNode.ABSCHECKED) {
					groupListDisplayNames.add(treeNode.getNodeName() + ResHelper.getString("60290106", "0602901060144"));
				} else {
					groupListDisplayNames.add(treeNode.getNodeName());
				}
			}
		}
		getGrplist().setListData(groupListDisplayNames.toArray(new String[0]));
		setTitle(ResHelper.getString("60290106", "0602901060145") + groupListDisplayNames.size() + ResHelper.getString("60290106", "0602901060146"));
	}

	public void closeOK() {
		if (this.checkedHRDeptVOMap.size() == 0) {
			MessageDialog.showErrorDlg(this, null, "û�й�ѡ�κβ��ţ�����ѡ��һ�����ż�¼���ɽ�����䵥Ԫ!");

			return;
		}

		IWaBaUnitMaintain maintain = NCLocator.getInstance().lookup(IWaBaUnitMaintain.class);
		try {
			maintain.creatByDept(this.context, this.checkedHRDeptVOMap);
		} catch (BusinessException e) {
			MessageDialog.showWarningDlg(this, null, "���������ɽ�����䵥Ԫ����:\r\n" + e.getMessage());
			Logger.error(e.getMessage());
			return;
		}
		//		IEvaGroupMgtService mgtService = (IEvaGroupMgtService) NCLocator.getInstance().lookup(IEvaGroupMgtService.class);
		//		try {
		//			mgtService.createEvaGroupVOByDept(this.context, this.checkedHRDeptVOMap);
		//		} catch (BusinessException e) {
		//			MessageDialog.showWarningDlg(this, null, ResHelper.getString("60290106", "0602901060148") + e.getMessage());
		//
		//			Logger.error(e.getMessage());
		//			return;
		//		}
		super.closeOK();
	}

	private String dealDeptCondition(MultiChkTreeNode treeNode) {
		HRDeptVO deptVO = (HRDeptVO) treeNode.getUserObject();
		StringBuilder strCondition = new StringBuilder();
		if (treeNode.getCheckValue() == MultiChkTreeNode.ABSCHECKED) {

			strCondition.append(" (");
			strCondition.append(" hi_psnjob.pk_dept in (select pk_dept from org_dept where org_dept.innercode like '").append(deptVO.getInnercode()).append("%') ");
			Enumeration<?> e = treeNode.breadthFirstEnumeration();
			while (e.hasMoreElements()) {
				MultiChkTreeNode subtreeNode = (MultiChkTreeNode) e.nextElement();
				HRDeptVO subDeptVO = (HRDeptVO) subtreeNode.getUserObject();
				if ((!subtreeNode.equals(treeNode)) &&

				(subtreeNode.getCheckValue() != MultiChkTreeNode.UNCHECKED)) {

					if (subtreeNode.getCheckValue() == MultiChkTreeNode.ABSCHECKED) {
						strCondition.append(" and hi_psnjob.pk_dept not in (select pk_dept from org_dept where org_dept.innercode like '").append(subDeptVO.getInnercode()).append("%') ");
					} else
						strCondition.append(" and hi_psnjob.pk_dept <> '").append(subDeptVO.getPk_dept()).append("' ");
				}
			}
			strCondition.append(") ");
		} else {
			strCondition.append(" hi_psnjob.pk_dept = '").append(deptVO.getPk_dept()).append("' ");
		}
		return strCondition.toString();
	}

	protected DefaultTreeModel createTreeModel() {
		HRDeptVO[] data = null;
		List<HRDeptVO> deptVOList = new ArrayList();

		try {
			String adminOrgInSql = AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getContext().getPk_org());

			String condition = " pk_org in(" + adminOrgInSql + ") and " + "hrcanceled" + "='N' and " + "enablestate" + " = 2 ";
			AggHRDeptVO[] aggVOs = getDeptQryService().queryByCondition(getContext(), condition);
			if ((aggVOs != null) && (aggVOs.length > 0)) {
				for (AggHRDeptVO deptVO : aggVOs) {
					deptVOList.add((HRDeptVO) deptVO.getParentVO());
				}
			}

			data = (HRDeptVO[]) deptVOList.toArray(new HRDeptVO[0]);
		} catch (BusinessException e) {
			MessageDialog.showErrorDlg(this, ResHelper.getString("60290106", "0602901060149"), ResHelper.getString("60290106", "0602901060150"));
		}

		MultiChkTreeNode chkNode = new MultiChkTreeNode();
		chkNode.setNodeCode("");
		chkNode.setNodeName(ResHelper.getString("common", "UC000-0004064"));

		HashSet<String> set = new HashSet();
		if (data != null) {
			for (HRDeptVO d : data) {
				set.add(d.getPk_org());
			}
		}
		List<HROrgVO> list = getHrOrg(set);
		return createTreeModel(convertDeptToNode(data, list), chkNode);
	}

	public static DefaultTreeModel createTreeModel(MultiChkTreeNode[] nodes, MultiChkTreeNode root) {
		if ((nodes == null) || (nodes.length == 0)) {
			return new DefaultTreeModel(root);
		}

		HashMap<Object, Object> treeNodeMap = new HashMap();
		for (int i = 0; i < nodes.length; i++) {
			treeNodeMap.put(nodes[i].getNodeId(), nodes[i]);
		}

		for (int i = 0; i < nodes.length; i++) {
			Object parentId = nodes[i].getParentId();
			MultiChkTreeNode parentNode = parentId == null ? root : (MultiChkTreeNode) treeNodeMap.get(parentId);

			if (parentNode != null) {
				parentNode.add(nodes[i]);
			} else {
				root.add(nodes[i]);
			}
		}

		return new DefaultTreeModel(root);
	}

	public MultiChkTreeNode[] convertDeptToNode(HRDeptVO[] data, List<HROrgVO> list) {
		ArrayList<MultiChkTreeNode> nodes = new ArrayList();

		for (HROrgVO hrOrgVO : list) {
			for (int i = 0; i < data.length; i++) {
				if (data[i].getPk_org().equals(hrOrgVO.getPk_org())) {

					MultiChkTreeNode deptNode = new MultiChkTreeNode();
					deptNode.setParentId(data[i].getPk_fatherorg());
					deptNode.setNodeId(data[i].getPk_dept());
					deptNode.setNodeCode(data[i].getCode());
					deptNode.setNodeName(data[i].getName() + "--" + hrOrgVO.getName());
					deptNode.setUserObject(data[i]);
					nodes.add(deptNode);
				}
			}
		}

		return (MultiChkTreeNode[]) nodes.toArray(new MultiChkTreeNode[0]);
	}

	protected UISplitPane getSplitPane() {
		if (this.splitPane == null) {
			this.splitPane = new UISplitPane(1, getLeftPanel(), getRightPanel());

			this.splitPane.setDividerLocation(240);

			this.splitPane.setDividerSize(5);
		}

		return this.splitPane;
	}

	public UIButton getBtn_close() {
		if (this.btn_close == null) {
			this.btn_close =
					ButtonUtils.createButton(ResHelper.getString("common", "UC001-0000008"), 'C', ResHelper.getString("common", "UC001-0000008"));

			this.btn_close.addActionListener(this);
		}

		return this.btn_close;
	}

	public UIButton getBtn_ok() {
		if (this.btn_ok == null) {
			this.btn_ok =
					ButtonUtils.createButton(ResHelper.getString("common", "UC001-0000044"), 'Y', ResHelper.getString("common", "UC001-0000044"));

			this.btn_ok.addActionListener(this);
		}

		return this.btn_ok;
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		int modifiers = e.getModifiers();
		if ((modifiers & 0x8) != 0) {
			switch (keyCode) {
				case 89:
					closeOK();
					break;
				case 67:
					closeCancel();
			}

		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getBtn_ok())) {
			closeOK();
		} else {
			if (e.getSource().equals(getBtn_close())) {
				closeCancel();
				return;
			}
			collapseTreePath();
			expandTreePath(e);
		}
	}

	public void collapseTreePath() {
		int rowCount = getUITree().getRowCount();
		for (int i = rowCount - 1; i > 0; i--) {
			getUITree().collapseRow(i);
		}
	}

	public void expandTreePath(ActionEvent e) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getUITree().getModel().getRoot();
		makeVisible(new DefaultMutableTreeNode[] { root });
		String text = ((UIButton) e.getSource()).getText();
		if (text.equalsIgnoreCase("E")) {
			makeVisible(getAllTreeNode());
		} else {
			DefaultMutableTreeNode[] nodes = getLevelTreeNodes(new DefaultMutableTreeNode[] { root }, 0, Integer.parseInt(text));

			makeVisible(nodes);
		}
	}

	private DefaultMutableTreeNode[] getLevelTreeNodes(DefaultMutableTreeNode[] selectedTreeNodes, int beginLevel, int endLevel) {
		if (selectedTreeNodes == null) {
			return null;
		}

		List<DefaultMutableTreeNode> list = new ArrayList();
		for (int i = 0; i < selectedTreeNodes.length; i++) {
			Enumeration enumeration = selectedTreeNodes[i].breadthFirstEnumeration();

			while (enumeration.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

				if (!node.isRoot()) {

					if ((node.getLevel() >= beginLevel) && (node.getLevel() <= endLevel)) {
						if (!list.contains(node))
							list.add(node);
					}
				}
			}
		}
		return (DefaultMutableTreeNode[]) list.toArray((DefaultMutableTreeNode[]) Array.newInstance(DefaultMutableTreeNode.class, 0));
	}

	private DefaultMutableTreeNode[] getAllTreeNode() {
		List<DefaultMutableTreeNode> list = new ArrayList();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getUITree().getModel().getRoot();
		Enumeration enumeration = root.breadthFirstEnumeration();
		while (enumeration.hasMoreElements()) {
			list.add((DefaultMutableTreeNode) enumeration.nextElement());
		}

		return (DefaultMutableTreeNode[]) list.toArray((DefaultMutableTreeNode[]) Array.newInstance(DefaultMutableTreeNode.class, 0));
	}

	private void makeVisible(DefaultMutableTreeNode[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			getUITree().makeVisible(new TreePath(nodes[i].getPath()));
		}
	}

	public List<HROrgVO> getHrOrg(HashSet<String> set) {
		List<HROrgVO> list = new ArrayList();
		IOrgInfoQueryService orgInfoQueryService = (IOrgInfoQueryService) NCLocator.getInstance().lookup(IOrgInfoQueryService.class);
		String[] pk_orgs = new String[set.size()];
		int index = 0;
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			pk_orgs[index] = ((String) iterator.next());
			index++;
		}

		try {
			AggHROrgVO[] aggOrgVOs = orgInfoQueryService.queryByPks(pk_orgs);
			for (int i = 0; (aggOrgVOs != null) && (i < aggOrgVOs.length); i++) {
				HROrgVO hrOrgVO = (HROrgVO) aggOrgVOs[i].getParentVO();
				list.add(hrOrgVO);
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}

		return list;
	}

	public UIList getGrplist() {
		if (this.grplist == null) {
			this.grplist = new UIList();
		}
		return this.grplist;
	}

	public HashMap<String, ConditionVO[]> getConditionMap() {
		return this.conditionMap;
	}

	public void setConditionMap(HashMap<String, ConditionVO[]> conditionMap) {
		this.conditionMap = conditionMap;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}

	public LoginContext getContext() {
		return this.context;
	}

	public static IDeptQueryService getDeptQryService() {
		return (IDeptQueryService) NCLocator.getInstance().lookup(IDeptQueryService.class);
	}

	protected JComponent createCenterPanel() {
		return null;
	}

	public void initUI() {
	}

	public void transferFocusToFirstEditor() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FromDeptGenDialog.this.requestFocusInWindow();

				FromDeptGenDialog.this.transferFocusToFirstEditor(FromDeptGenDialog.this.getLeftPanel());
			}
		});
	}

}