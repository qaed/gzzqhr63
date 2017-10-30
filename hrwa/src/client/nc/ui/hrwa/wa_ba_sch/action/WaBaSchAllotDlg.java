package nc.ui.hrwa.wa_ba_sch.action;

import java.awt.BorderLayout;

import javax.swing.ListSelectionModel;

import nc.bs.framework.common.NCLocator;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pubapp.uif2app.model.BillManageModel;
import nc.ui.trade.business.HYPubBO_Client;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.SuperVO;
import nc.vo.wa.wa_ba.sch.AggWaBaSchHVO;
import nc.vo.wa.wa_ba.sch.WaBaSchBVO;
import nc.vo.wa.wa_ba.sch.WaBaSchHVO;
import nc.vo.wa.wa_ba.unit.WaBaUnitHVO;

@SuppressWarnings("restriction")
public class WaBaSchAllotDlg extends UIDialog {

	private static final long serialVersionUID = 6172313611690007636L;
	//	private static Font labelFont = new Font("Dialog", java.awt.Font.PLAIN, 12);
	private BillModel billModel = null;

	private BillManageModel manageModel = null;
	private UIPanel mainPanel = null;
	private UIPanel northPanel = null;
	private BillScrollPane centerPanel = null;

	//	private Boolean isChange = false;//是否更新

	@SuppressWarnings("deprecation")
	public WaBaSchAllotDlg(BillManageModel manageModel) throws BusinessException {
		this.manageModel = manageModel;
		init();
	}

	public void init() throws BusinessException {
		this.setContentPane(this.getMainPanel());

		this.setSize(1000, 500);
		//		this.isChange = false;

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
		AggWaBaSchHVO saggvo = ((AggWaBaSchHVO) this.manageModel.getSelectedData());
		AggWaBaSchHVO aggvo =
				(AggWaBaSchHVO) HYPubBO_Client.queryBillVOByPrimaryKey(new String[] { AggWaBaSchHVO.class.getName(), WaBaSchHVO.class.getName(), WaBaSchBVO.class.getName() }, saggvo.getParentVO().getPk_ba_sch_h());
		ISuperVO[] childvos = aggvo.getChildren(WaBaSchBVO.class);
		//		WaBaSchBVO[] bvos = (WaBaSchBVO[]) HYPubBO_Client.queryByCondition(WaBaSchBVO.class, "");
		int row = 0;
		for (ISuperVO vo : childvos) {
			WaBaSchBVO bvo = (WaBaSchBVO) vo;
			SuperVO[] supervo = HYPubBO_Client.queryByCondition(WaBaUnitHVO.class, "pk_wa_ba_unit = '" + bvo.getBa_unit_code() + "'");
			IPsndocQueryService psndocService = NCLocator.getInstance().lookup(IPsndocQueryService.class);
			WaBaUnitHVO unithvo = (WaBaUnitHVO) supervo[0];
			//很绝望，一起查就乱序了
			PsndocVO[] mngpsndoc1 = psndocService.queryPsndocByPks(new String[] { unithvo.getBa_mng_psnpk() }, new String[] { "name" });
			PsndocVO[] mngpsndoc2 = psndocService.queryPsndocByPks(new String[] { unithvo.getBa_mng_psnpk2() }, new String[] { "name" });
			PsndocVO[] mngpsndoc3 = psndocService.queryPsndocByPks(new String[] { unithvo.getBa_mng_psnpk3() }, new String[] { "name" });
			getBillModel().addLine();
			getBillModel().setValueAt(unithvo.getName(), row, 0);//奖金分配单元名称
			getBillModel().setValueAt(mngpsndoc1[0].getName(), row, 1);//分配人1
			if (mngpsndoc2 != null && mngpsndoc2.length == 1) {
				getBillModel().setValueAt(mngpsndoc2[0].getName(), row, 2);//分配人2
			}
			if (mngpsndoc3 != null && mngpsndoc3.length == 1) {
				getBillModel().setValueAt(mngpsndoc3[0].getName(), row, 3);//分配人3
			}
			if (bvo.getVdef1() != null && !"".endsWith(bvo.getVdef1())) {
				PsndocVO[] psndocs = psndocService.queryPsndocByPks(new String[] { bvo.getVdef1() }, new String[] { "name" });
				if (psndocs != null && psndocs.length == 1) {
					getBillModel().setValueAt(psndocs[0].getName(), row, 4);//当期分配人
					getBillModel().setValueAt("分配中", row, 5);//进度
				} else {
					getBillModel().setValueAt("请检查当前分配人", row, 5);//进度
				}
			} else {
				getBillModel().setValueAt("分配完成", row, 5);//进度
			}

			row++;
		}

	}

	public BillItem[] getBillItems() {
		BillItem[] billItems = null;
		//		String name = "";

		String names[] = { "奖金分配单元名称", "分配人1", "分配人2", "分配人3", "当前分配人", "进度" };
		String keys[] = { "pk_wa_ba_unit", "pk1", "pk2", "pk3", "currpk", "progress" };
		billItems = new BillItem[names.length];
		for (int i = 0; i < names.length; i++) {
			billItems[i] = new BillItem();
			billItems[i].setName(names[i].toString());
			billItems[i].setKey(keys[i]);
			if ("pk_wa_ba_unit".equals(keys[i]))
				billItems[i].setWidth(200);
			else
				billItems[i].setWidth(150);
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
