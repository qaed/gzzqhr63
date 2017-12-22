package nc.ui.ta.psndoc.view;

import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.JTable;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.trn.IhrtrnQBS;
import nc.pub.tools.HiSQLHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.hr.frame.util.table.SelectableBillScrollPane;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UITabbedPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.comp.trn.PsnChangeVO;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TBMPsnChangePanel extends UIPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 3995846039012361555L;

	private UFLiteralDate beginDate = null;
	//    private UIPanel bottomPanel = null;
	private UFLiteralDate endDate = null;
	private HashMap<Integer, SelectableBillScrollPane> hashMapPane = null;
	private HashMap<Integer, String> hashMapPaneTitle = null;
	private UIPanel jSplitPane = null;
	private JTabbedPane jTabbedPane = null;
	private LoginContext context = null;
	private PsnChangeVO selectedPks = null;
	private SelectableBillScrollPane TRA_ADD = null;

	private SelectableBillScrollPane TRA_SUB = null;
	private SelectableBillScrollPane TRN_PART_ADD = null;
	private SelectableBillScrollPane TRN_PART_SUB = null;
	private SelectableBillScrollPane TRN_POST_MOD = null;

	/**
	 * @param parent
	 */
	public TBMPsnChangePanel(LoginContext context, UFLiteralDate beginDate, UFLiteralDate endDate) {
		super();
		this.context = context;
		this.beginDate = beginDate;
		this.endDate = endDate;
		initialize();
	}

	/**
	 * @param parent
	 */
	public TBMPsnChangePanel(LoginContext context) {
		super();
		this.context = context;
	}

	public LoginContext getContext() {
		return context;
	}

	/**
	 * ���������Ƿ���� Created on 2008-10-22
	 * @author zhangg
	 * @return
	 */
	public static boolean isExists(int trnType) {
		boolean isExists = true;
		if (trnType == CommonValue.TRN_ADD || trnType == CommonValue.TRN_PART_ADD || trnType == CommonValue.TRN_ALL_ADD) {
			isExists = false;
		} else if (trnType == CommonValue.TRN_SUB || trnType == CommonValue.TRN_PART_SUB || trnType == CommonValue.TRN_POST_MOD) {
			isExists = true;
		}
		return isExists;
	}

	/**
	 * ��ѯ���������� Created on 2008-5-4
	 * @author zhangg
	 * @param trnType
	 * @return
	 */
	public String getAddWhere(int trnType) {
		// �Ѿ����ڿ��ڵ�����Ա
		StringBuilder sqlB = new StringBuilder();
		sqlB.append(" and psnjob.endflag <> 'Y' ");//���˵���ְ��Ա��  modify by-zhouyuh 2012 -8-2
		sqlB.append(" and not exists ");
		sqlB.append(" (select 1 from tbm_psndoc where tbm_psndoc.pk_psndoc = psnjob.pk_psndoc and tbm_psndoc.enddate like '9999%' and pk_org = '" + getContext().getPk_org() + "') ");

		//����Ȩ�޿���
		String permission = TBMPsndocSqlPiecer.getPowerSql("6007psnjob", "PSNJOB");
		// tsy ���ڵ����䶯��Ա��Ӳ���Ȩ��
		String deptpermission = TBMPsndocSqlPiecer.getPowerSql(HICommonValue.RESOUCECODE_DEPT, "PSNJOB");

		//("6007psnjob", "hrta_default", "psnjob");
		if (StringUtils.isNotEmpty(permission))
			sqlB.append(" and ( ").append(permission).append(")");
		
		if (StringUtils.isNotEmpty(deptpermission))
			sqlB.append(" and ( ").append(deptpermission).append(")");
		return sqlB.toString();
	}

	//    private UIPanel getBottomPanel()
	//    {
	//        if (bottomPanel == null)
	//        {
	//            bottomPanel = new UIPanel();
	//            java.awt.FlowLayout ivjUIPanel1FlowLayout = new java.awt.FlowLayout(FlowLayout.CENTER, 50, 10);
	//            bottomPanel.setLayout(ivjUIPanel1FlowLayout);
	////            bottomPanel.add(getOkButton(), getOkButton().getName());
	////            bottomPanel.add(getCancelButton(), getCancelButton().getName());
	////            bottomPanel.setPreferredSize(new java.awt.Dimension(0, 50));
	//
	//
	//        }
	//        return bottomPanel;
	//    }

	public String[] getColKeyName(int trnType) {
		String[] keyname = null;
		if (trnType == CommonValue.TRN_POST_MOD) {
			keyname =
					new String[] { "isSelected", "clerkcode", "psnname", "psnclassname", "orgname", "predeptname", "prepostname", "prepostrank", "deptname", "postname", "postrank", "psnid", "trndate", "pk_psndoc", "pk_dept", "pk_post", "pk_psncl", "pk_psnjob", "pk_psnorg" };

		} else {
			keyname =
					new String[] { "isSelected", "clerkcode", "psnname", "psnclassname", "orgname", "deptname", "postname", "psnid", "trndate", "pk_psndoc", "pk_dept", "pk_post", "pk_psncl", "pk_psnjob", "pk_psnorg" };
		}
		return keyname;
	}

	/**
	 * @param trnType
	 * @return
	 */
	public String[] getColName(int trnType) {
		String[] colname = null;
		switch (trnType) {
			case CommonValue.TRN_ADD:
				colname =
						new String[] { ResHelper.getString("common", "2UC000-000553")/* "ѡ���־" */, PublicLangRes.EMPNO(), ResHelper.getString("common", "UC000-0000135")/* "����" */, ResHelper.getString("common", "UC000-0000140")/* "��Ա���" */, ResHelper.getString("common", "2UC000-000693") /* "ҵ��Ԫ" */, ResHelper.getString("common", "UC000-0004064")/* "����" */, ResHelper.getString("common", "UC000-0001653")/* "ְ��" */, ResHelper.getString("common", "UC000-0003914")/*@res "���֤��"*/, ResHelper.getString("6017psndoc", "06017psndoc0089")/*@res "�䶯ʱ��"*/, ResHelper.getString("common", "UC000-0000131")/* "��Ա����" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg" };

				break;
			case CommonValue.TRN_SUB:
				colname =
						new String[] { ResHelper.getString("common", "2UC000-000553")/* "ѡ���־" */, PublicLangRes.EMPNO(), ResHelper.getString("common", "UC000-0000135")/* "����" */, ResHelper.getString("common", "UC000-0000140")/* "��Ա���" */, ResHelper.getString("common", "2UC000-000693") /* "ҵ��Ԫ" */, ResHelper.getString("common", "UC000-0003054")/* "��ְǰ����" */, ResHelper.getString("common", "UC000-0003042")/* "��ְǰ��λ" */, ResHelper.getString("6017psndoc", "06017psndoc0142")
						/*@res "֤������"*/, ResHelper.getString("common", "UC000-0003062")/* "��ְʱ��" */, ResHelper.getString("common", "UC000-0000131")/* "��Ա����" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg" };
				break;
			case CommonValue.TRN_POST_MOD:
				colname =
						new String[] { ResHelper.getString("common", "2UC000-000553")/* "ѡ���־" */, PublicLangRes.EMPNO(), ResHelper.getString("common", "UC000-0000135")/* "����" */, ResHelper.getString("common", "UC000-0000140")/* "��Ա���" */, ResHelper.getString("common", "2UC000-000693") /* "ҵ��Ԫ" */, ResHelper.getString("6017psndoc", "06017psndoc0090")/*@res "�䶯ǰ����"*/, ResHelper.getString("6017psndoc", "06017psndoc0091")/*@res "�䶯ǰ��λ"*/, ResHelper.getString("6017psndoc", "06017psndoc0092")/*@res "�䶯ǰ��λ����"*/, ResHelper.getString("6017psndoc", "06017psndoc0093")/*@res "�䶯����"*/, ResHelper.getString("6017psndoc", "06017psndoc0094")/*@res "�䶯���λ"*/, ResHelper.getString("6017psndoc", "06017psndoc0095")/*@res "�䶯���λ����"*/, ResHelper.getString("common", "UC000-0003914")/*@res "���֤��"*/, ResHelper.getString("6017psndoc", "06017psndoc0089")/*@res "�䶯ʱ��"*/, ResHelper.getString("common", "UC000-0000131")/* "��Ա����" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg" };
				break;
			case CommonValue.TRN_PART_ADD:
				colname =
						new String[] { ResHelper.getString("common", "2UC000-000553")/* "ѡ���־" */, PublicLangRes.EMPNO(), ResHelper.getString("common", "UC000-0000135")/* "����" */, ResHelper.getString("common", "UC000-0000140")/* "��Ա���" */, ResHelper.getString("common", "2UC000-000693") /* "ҵ��Ԫ" */, ResHelper.getString("common", "UC000-0000435")/* "��ְ����" */, ResHelper.getString("common", "UC000-0000434")/* "��ְ��λ" */, ResHelper.getString("common", "UC000-0003914")/*@res "���֤��"*/, ResHelper.getString("6017psndoc", "06017psndoc0089")/*@res "�䶯ʱ��"*/, ResHelper.getString("common", "UC000-0000131")/* "��Ա����" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg" };
				break;
			case CommonValue.TRN_PART_SUB:
				colname =
						new String[] { ResHelper.getString("common", "2UC000-000553")/* "��ʶ" */, PublicLangRes.EMPNO(), ResHelper.getString("common", "UC000-0000135")/* "����" */, ResHelper.getString("common", "UC000-0000140")/* "��Ա���" */, ResHelper.getString("common", "2UC000-000693") /* "ҵ��Ԫ" */, ResHelper.getString("common", "UC000-0000435")/* "��ְ����" */, ResHelper.getString("common", "UC000-0000434")/* "��ְ��λ" */, ResHelper.getString("common", "UC000-0003914")/*@res "���֤��"*/, ResHelper.getString("6017psndoc", "06017psndoc0089")/*@res "�䶯ʱ��"*/, ResHelper.getString("common", "UC000-0000131")/* "��Ա����" */, "pk_dept", "pk_postdoc", "pk_psncl", "pk_psnjob", "pk_psnorg" };
				break;
		}

		return colname;
	}

	public int[] getColType(int trnType) {
		if (trnType == CommonValue.TRN_POST_MOD) {
			return new int[] { BillItem.BOOLEAN, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.LITERALDATE, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING };

		} else
			return new int[] { BillItem.BOOLEAN, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.LITERALDATE, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING, BillItem.STRING };
	}

	/**
	 * ��ʼ����Ҫ���ӵĿ�Ƭ Created on 2008-5-4
	 * @author zhangg
	 * @return
	 */
	public Integer[] getInitTabs() {
		return new Integer[] { CommonValue.TRN_ADD, CommonValue.TRN_PART_ADD };
	}

	/**
	 * This method initializes jSplitPane
	 * @return javax.swing.JSplitPane
	 */
	//    private JSplitPane getJSplitPane()
	//    {
	//        if (jSplitPane == null)
	//        {
	//            jSplitPane = new JSplitPane();
	//            jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	//            jSplitPane.setDividerLocation(420);
	//            jSplitPane.setBottomComponent(getBottomPanel());
	//            jSplitPane.setTopComponent(getJTabbedPane());
	//            jSplitPane.setDividerSize(1);
	//        }
	//        return jSplitPane;
	//    }

	private UIPanel getMainUIPane() {
		if (jSplitPane == null) {
			jSplitPane = new UIPanel();
			jSplitPane.setName("jSplitPane");
			jSplitPane.setLayout(new java.awt.BorderLayout());
			//            jSplitPane.add(getBottomPanel(), "South");
			jSplitPane.add(getJTabbedPane(), "Center");

		}
		return jSplitPane;
	}

	/**
	 * This method initializes jTabbedPane
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new UITabbedPane();
			// jTabbedPane.setPreferredSize(new Dimension(650, 450));
			jTabbedPane.setPreferredSize(new Dimension(700, 450));//ʹ�б��������
			jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {

					JTabbedPane tabbedPane = (JTabbedPane) e.getSource();

					SelectableBillScrollPane billScrollPane = (SelectableBillScrollPane) tabbedPane.getSelectedComponent();
					int trnType = getSelectedKey(billScrollPane);
					refreshData(trnType, billScrollPane);

					//                    // ���ڵ�ɾ���� �����ڵ�����
					//                    String text =
					//                        isExists(trnType) ? ResHelper.getString("common", "UC001-0000039")/* "ɾ��" */
					//                            : ResHelper.getString("common", "UC001-0000002")/* "����" */;
					//                    getOkButton().setText(text);
				}
			});
		}
		return jTabbedPane;
	}

	//    /**
	//     * This method initializes okButton
	//     * @return javax.swing.JButton
	//     */
	//    private JButton getOkButton()
	//    {
	//        if (okButton == null)
	//        {
	//            okButton = new JButton();
	//            okButton.setName("okButton");
	//            okButton.setText(ResHelper.getString("common", "UC001-0000044")/* "ȷ��" */);
	//            okButton.setPreferredSize(new Dimension(60,20));
	//            okButton.addActionListener(new java.awt.event.ActionListener()
	//            {
	//                public void actionPerformed(java.awt.event.ActionEvent e)
	//                {
	//
	//                    try
	//                    {
	//                        SelectableBillScrollPane billScrollPane =
	//                            (SelectableBillScrollPane) getJTabbedPane().getSelectedComponent();
	//                        int state = getSelectedKey(billScrollPane);
	//                        List<PsnTrnVO> list = getSelPsnInf(billScrollPane);
	//                        selectedPks = new PsnChangeVO();
	//                        selectedPks.setState(state);
	//                        selectedPks.setPsnVOlist(list);
	//
	//                        setResult(nc.ui.pub.beans.UIDialog.ID_OK);
	//                        dispose();
	//                    }
	//                    catch (ValidationException e1)
	//                    {
	//                        MessageDialog.showErrorDlg(context.getEntranceUI(),null,e1.getMessage());
	//
	//                    }
	//                }
	//            });
	//        }
	//        return okButton;
	//    }

	/**
	 * @return
	 */

	/**
	 * @param billScrollPane
	 * @return
	 */
	private Integer getSelectedKey(SelectableBillScrollPane billScrollPane) {
		for (Integer key : hashMapPane.keySet()) {
			if (hashMapPane.get(key).equals(billScrollPane)) {
				return key;
			}
		}
		return 0;
	}

	/**
	 * @return
	 */
	public PsnChangeVO getSelectedPks() throws ValidationException {
		SelectableBillScrollPane billScrollPane = (SelectableBillScrollPane) getJTabbedPane().getSelectedComponent();
		int state = getSelectedKey(billScrollPane);
		List<PsnTrnVO> list = getSelPsnInf(billScrollPane);
		if (CollectionUtils.isEmpty(list))
			return null;
		selectedPks = new PsnChangeVO();
		selectedPks.setState(state);
		selectedPks.setPsnVOlist(list);
		return selectedPks;
	}

	/**
	 * ���ѡ����Ա��Ϣ�� �������ڣ�(2007-4-18 19:43:08)
	 * @throws ValidationException
	 * @throws ValidationException
	 */
	private List<PsnTrnVO> getSelPsnInf(SelectableBillScrollPane billScrollPane) throws ValidationException {

		List<PsnTrnVO> list = new ArrayList<PsnTrnVO>();

		PsnTrnVO[] generalVOs = (PsnTrnVO[]) billScrollPane.getSelectedBodyVOs(PsnTrnVO.class);
		if (ArrayUtils.isEmpty(generalVOs))
			return list;
		for (PsnTrnVO generalVO : generalVOs) {

			if (personIsInList(list, generalVO)) {
				String code = generalVO.getClerkcode();
				String msg = ResHelper.getString("6017psndoc", "06017psndoc0096")
				/*@res "Ա����Ϊ{0}����Ա���ظ�"*/;
				throw new ValidationException(MessageFormat.format(msg, code));
			}
			list.add(generalVO);
		}
		return list;
	}

	/**
	 * This method initializes TRA_ADD
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRA_ADD() {
		if (TRA_ADD == null) {
			TRA_ADD = new SelectableBillScrollPane();

		}
		return TRA_ADD;
	}

	/**
	 * This method initializes TRA_SUB
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRA_SUB() {
		if (TRA_SUB == null) {
			TRA_SUB = new SelectableBillScrollPane();

		}
		return TRA_SUB;
	}

	/**
	 * This method initializes TRN_PART_ADD
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRN_PART_ADD() {
		if (TRN_PART_ADD == null) {
			TRN_PART_ADD = new SelectableBillScrollPane();

		}
		return TRN_PART_ADD;
	}

	/**
	 * This method initializes TRN_PART_SUB
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRN_PART_SUB() {
		if (TRN_PART_SUB == null) {
			TRN_PART_SUB = new SelectableBillScrollPane();

		}
		return TRN_PART_SUB;
	}

	/**
	 * This method initializes TRN_POST_MOD
	 * @return javax.swing.JScrollPane
	 */
	private SelectableBillScrollPane getTRN_POST_MOD() {
		if (TRN_POST_MOD == null) {
			TRN_POST_MOD = new SelectableBillScrollPane();

		}
		return TRN_POST_MOD;
	}

	/**
	 * ��ʼ����Ҫ������Щ��Ƭ Created on 2008-5-4
	 * @author zhangg
	 */
	private void initHashMap() {
		if (hashMapPane == null) {
			hashMapPane = new LinkedHashMap<Integer, SelectableBillScrollPane>();
			Integer[] tabs = getInitTabs();
			for (Integer tab : tabs) {
				switch (tab) {
					case CommonValue.TRN_ADD:
						hashMapPane.put(CommonValue.TRN_ADD, getTRA_ADD());
						break;
					case CommonValue.TRN_SUB:
						hashMapPane.put(CommonValue.TRN_SUB, getTRA_SUB());
						break;
					case CommonValue.TRN_PART_ADD:
						hashMapPane.put(CommonValue.TRN_PART_ADD, getTRN_PART_ADD());
						break;
					case CommonValue.TRN_PART_SUB:
						hashMapPane.put(CommonValue.TRN_PART_SUB, getTRN_PART_SUB());
						break;
					case CommonValue.TRN_POST_MOD:
						hashMapPane.put(CommonValue.TRN_POST_MOD, getTRN_POST_MOD());
						break;
					default:
						break;
				}
			}

		}
		if (hashMapPaneTitle == null) {

			hashMapPaneTitle = new LinkedHashMap<Integer, String>();
			Integer[] tabs = getInitTabs();
			for (Integer tab : tabs) {
				switch (tab) {
					case CommonValue.TRN_ADD:
						hashMapPaneTitle.put(CommonValue.TRN_ADD, ResHelper.getString("6017psndoc", "06017psndoc0098")
						/*@res "�½���Ա"*/);//
						break;
					case CommonValue.TRN_SUB:
						hashMapPaneTitle.put(CommonValue.TRN_SUB, ResHelper.getString("6017psndoc", "06017psndoc0099")
						/*@res "��ְ��Ա"*/);//
						break;
					//                    case CommonValue.TRN_PART_ADD :
					//                        hashMapPaneTitle.put(CommonValue.TRN_PART_ADD, ResHelper.getString("6017psndoc","06017psndoc0100")/*@res "��ְ��ʼ"*/);//
					//                        break;
					case CommonValue.TRN_PART_ADD:
						hashMapPaneTitle.put(CommonValue.TRN_PART_ADD, ResHelper.getString("6017psndoc", "06017psndoc0148")/*@res "��ְ��Ա"*/);//
						break;
					case CommonValue.TRN_PART_SUB:
						hashMapPaneTitle.put(CommonValue.TRN_PART_SUB, ResHelper.getString("6017psndoc", "06017psndoc0101")
						/*@res "��ְ����"*/);//
						break;
					case CommonValue.TRN_POST_MOD:
						hashMapPaneTitle.put(CommonValue.TRN_POST_MOD, ResHelper.getString("6017psndoc", "06017psndoc0102")
						/*@res "�䶯��Ա"*/);//
						break;
					default:
						break;
				}
			}
		}
	}

	/**
	 * This method initializes this
	 */
	public void initialize() {
		//  setPreferredSize(new Dimension(600, 400));
		add(getMainUIPane());
		initHashMap();
		for (Integer key : hashMapPane.keySet()) {
			setTable(key, hashMapPane.get(key));
			getJTabbedPane().addTab(hashMapPaneTitle.get(key), hashMapPane.get(key));

		}
	}

	/**
	 * ��ʼ��������ʾ�� �������ڣ�(2007-4-18 10:57:21)
	 * @return nc.ui.pub.bill.BillModel
	 */
	private void initTable(int trnType, SelectableBillScrollPane billScrollPane) {
		String[] colNames = getColName(trnType);
		String[] colKeyNames = getColKeyName(trnType);
		int[] dataTypes = getColType(trnType);
		BillItem[] abillBody = new BillItem[colNames.length];
		for (int i = 0; i < colNames.length; i++) {
			abillBody[i] = new BillItem();
			abillBody[i].setName(colNames[i]);
			abillBody[i].setKey(colKeyNames[i]);
			abillBody[i].setDataType(dataTypes[i]);
			abillBody[i].setWidth(200);
			// ������ʾ��ʽ
			if (i == 0) {
				abillBody[i].setEnabled(true);
				abillBody[i].setEdit(true);// true
				abillBody[i].setLength(50);
				((UICheckBox) abillBody[i].getComponent()).setHorizontalAlignment(UICheckBox.CENTER);
			} else if ((trnType == CommonValue.TRN_POST_MOD && i > 11) || (CommonValue.TRN_POST_MOD != trnType && i > 8)) {
				abillBody[i].setEnabled(false);
				abillBody[i].setShow(false);
			} else {
				abillBody[i].setEnabled(false);
				abillBody[i].setEdit(false);
				abillBody[i].setNull(false);
			}
		}
		BillModel billModel = new BillModel();
		billModel.setBodyItems(abillBody);
		billScrollPane.setTableModel(billModel);
		billScrollPane.setSelectRowCode("isSelected");
		billScrollPane.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

	}

	/**
	 * �ж��Ƿ��ظ� Created on 2008-5-4
	 * @author zhangg
	 * @param list
	 * @param psnVO
	 * @return
	 */
	private boolean personIsInList(List<PsnTrnVO> list, PsnTrnVO psnVO) {
		boolean isInList = false;
		for (PsnTrnVO psnVO2 : list) {
			if (psnVO2.getAttributeValue("pk_psndoc").toString().equals(psnVO.getAttributeValue("pk_psndoc").toString())) {
				return true;
			}

		}

		return isInList;
	}

	/**
	 * @param trnType
	 * @param billScrollPane
	 */
	public void refreshData(int trnType, SelectableBillScrollPane billScrollPane) {

		try {
			
			IhrtrnQBS hrtrnQBS = NCLocator.getInstance().lookup(IhrtrnQBS.class);
			PsnTrnVO[] hrMainVOs = hrtrnQBS.queryTRNPsnInf4TA(context.getPk_org(), beginDate, endDate, trnType, getAddWhere(trnType));
			//���û�����ݻ�����������10������ʾ������
			if (ArrayUtils.isEmpty(hrMainVOs) || hrMainVOs.length <= 10) {
				setPreferredSize(new Dimension(10, 10));
			}
			billScrollPane.getTableModel().setBodyDataVO(hrMainVOs);
			billScrollPane.selectAllRows();

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(context.getEntranceUI(), null, e.getMessage());
		}

	}

	/**
	 * @param trnType
	 * @param billScrollPane
	 */

	private void setTable(int trnType, SelectableBillScrollPane billScrollPane) {
		initTable(trnType, billScrollPane);

	}

	public UFLiteralDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(UFLiteralDate beginDate) {
		this.beginDate = beginDate;
	}

	public UFLiteralDate getEndDate() {
		return endDate;
	}

	public void setEndDate(UFLiteralDate endDate) {
		this.endDate = endDate;
	}

}