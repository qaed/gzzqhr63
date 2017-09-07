package nc.ui.hrwa.wa_ba_unit.model;

import javax.swing.tree.DefaultMutableTreeNode;

import nc.ui.pubapp.uif2app.model.HierachicalDataAppModel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.bd.meta.IBDObject;

public class WaBaItemTreeModel extends HierachicalDataAppModel {
	@Override
	public DefaultMutableTreeNode findParentNodeByBusinessObject(Object object) {
		DefaultMutableTreeNode parent = null;
		if ((this.getTreeCreateStrategy() != null) && (this.getTreeCreateStrategy().isCodeTree())) {
			IBDObject bo = this.getBusinessObjectAdapterFactory().createBDObject(object);
			// parent = findParentNodeByCodeRule(bo);
		} else {// id树
			IBDObject bo = this.getBusinessObjectAdapterFactory().createBDObject(object);
			parent = findNodeByBusinessObjectId(bo.getPId());
		}
		if (parent == null)
			parent = (DefaultMutableTreeNode) getTree().getRoot();
		return parent;
	}

	@Override
	public void directlyAdd(Object obj) {
		DefaultMutableTreeNode parent = findParentNodeByBusinessObject(obj);
		if (parent == null) {
			parent = (DefaultMutableTreeNode) getTree().getRoot();
		}
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(obj);
		getTree().insertNodeInto(newNode, parent, parent.getChildCount());
		// 层次模型中传递的是当前操作对象
		fireEvent(new AppEvent(AppEventConst.DATA_INSERTED, this, obj));
	}
}
