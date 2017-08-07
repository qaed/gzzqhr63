package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Map;

import javax.swing.ListSelectionModel;

import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;

public class WaBaSchAllotDlg extends UIDialog {

	private static final long serialVersionUID = 6172313611690007636L;
	private static Font labelFont = new Font("Dialog", java.awt.Font.PLAIN, 12);
	private BillModel billModel = null;
	private BillManageModel manageModel = null;
	private UIPanel mainPanel = null;
	private UIPanel northPanel = null;
	private BillScrollPane centerPanel = null;

	private Boolean isChange = false;//是否更新

	public WaBaSchAllotDlg(BillManageModel manageModel) throws BusinessException {
		this.manageModel = manageModel;
		init();
	}

	public void init() throws BusinessException {
		this.setContentPane(this.getMainPanel());

		this.setSize(1000, 500);
		isChange = false;

		//初始化已设置的数据
		initBillModelData();
	}

	public UIPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new UIPanel();
			mainPanel.setLayout(new BorderLayout());
			//			mainPanel.add(getNorthPanel(), BorderLayout.NORTH);
			mainPanel.add(getBillCenterPane(), BorderLayout.CENTER);
		}
		return mainPanel;
	}

	public UIPanel getNorthPanel() {
		if (northPanel == null) {
			northPanel = new UIPanel();
			northPanel.setSize(500, 500);
		}
		return northPanel;
	}

	public BillScrollPane getBillCenterPane() {
		if (centerPanel == null) {
			centerPanel = new BillScrollPane();
			//			centerPanel.setSize(500, 500);
			centerPanel.setTableModel(getBillModel());
			centerPanel.setRowNOShow(true);
			centerPanel.getTable().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			centerPanel.getTable().setSortEnabled(false);//禁用排序
		}
		return centerPanel;
	}

	public BillModel getBillModel() {
		if (billModel == null) {
			billModel = new BillModel();
			billModel.setBodyItems(getBillItems());
		}
		return billModel;
	}

	public void initBillModelData() throws BusinessException {
		getBillModel().clearBodyData();
		//初始化现有数据
		AggWaBaSchHVO aggvo = ((AggWaBaSchHVO)this.manageModel.getSelectedData());
		ISuperVO[] childvos = aggvo.getChildren(WaBaSchBVO.class);
//		WaBaSchBVO[] bvos = (WaBaSchBVO[]) HYPubBO_Client.queryByCondition(WaBaSchBVO.class, "");
		int row = 0;
		for (ISuperVO vo : childvos) {
//			WaBaSchBVO bvo = (WaBaSchBVO)vo;
			getBillModel().addLine();
//			getBillModel().setValueAt(map.get("accode"), row, 0);
//			getBillModel().setValueAt(map.get("accname"), row, 1);
//			getBillModel().setValueAt(map.get("ts"), row, 2);
//			getBillModel().setCellEditable(row, "accode", false);
			getBillModel().setValueAt("值A_"+row, row, 0);
			getBillModel().setValueAt("值B_"+row, row, 1);
			getBillModel().setValueAt("值C_"+row, row, 2);
			getBillModel().setValueAt("值D_"+row, row, 3);
			
			row++;
		}

	}

	public BillItem[] getBillItems() {
		BillItem[] billItems = null;
		String name = "";

		String names[] = { "奖金分配单元名称", "分配单编号", "分配单名称", "进度" };
		String keys[] = { "pk_wa_ba_unit", "code", "name", "progress" };
		billItems = new BillItem[names.length];
		for (int i = 0; i < names.length; i++) {
			billItems[i] = new BillItem();
			billItems[i].setName(names[i].toString());
			billItems[i].setKey(keys[i]);
			if ("pk_wa_ba_unit".equals(keys[i]))
				billItems[i].setWidth(200);
			else
				billItems[i].setWidth(250);
			//			if ("code".equals(keys[i])) {
			//			} else {
			//				billItems[i].setEdit(false);
			//				billItems[i].setDataType(IBillItem.STRING);
			//			}
			billItems[i].setShow(true);
			billItems[i].setNull(false);
		}
		return billItems;
	}

}
