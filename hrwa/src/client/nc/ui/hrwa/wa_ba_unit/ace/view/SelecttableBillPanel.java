package nc.ui.hrwa.wa_ba_unit.ace.view;

import java.awt.Color;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nc.hr.utils.ResHelper;
import nc.ui.hr.frame.util.table.MultiSelector;
import nc.ui.pub.beans.UIMenuItem;
import nc.ui.pub.beans.UIPopupMenu;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.table.BillTableBooleanCellRenderer;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFBoolean;

@SuppressWarnings("restriction")
public class SelecttableBillPanel extends BillScrollPane {
	private static final long serialVersionUID = 8520970036957875238L;
	private String canNotEditTipMessage;
	private UIPopupMenu pmHead;
	private ActionListener pmListener = null;
	final String SEL_ALL = "SEL_ALL";
	final String SEL_NOT_ALL = "SEL_NOT_ALL";
	final String SEL_RESVERSE = "SEL_RESVERSE";
	private String selectRowCode = null;

	private String selectedRowCode = null;

	public String getSelectedRowCode() {
		return this.selectedRowCode;
	}

	public void setSelectedRowCode(String selectedRowCode) {
		this.selectedRowCode = selectedRowCode;
	}

	public SelecttableBillPanel() {
	}

	private class CheckRenderer extends JCheckBox implements TableCellRenderer {
		private static final long serialVersionUID = -3267440643446066613L;
		String refEditCode = null;
		String selectCode = null;

		public CheckRenderer(String selectCode, String editableCode) {
			this.selectCode = selectCode;
			this.refEditCode = editableCode;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			BillModel billModel = (BillModel) table.getModel();

			setHorizontalAlignment(0);
			Object editableObject = billModel.getValueAt(row, this.refEditCode);
			boolean editableValue = true;
			if ((editableObject instanceof Boolean)) {
				editableValue = ((Boolean) editableObject).booleanValue();
			} else if ((editableObject instanceof UFBoolean)) {
				editableValue = ((UFBoolean) editableObject).booleanValue();
			}

			if (editableValue) {
				billModel.getRowAttribute(row).addCellEdit(this.selectCode, true);
				super.setForeground(table.getForeground());
				super.setBackground(table.getBackground());
				setEnabled(true);
				setToolTipText(null);
			} else {
				billModel.getRowAttribute(row).addCellEdit(this.selectCode, false);
				setBackground(new Color(222, 219, 222));
				setEnabled(false);
				setToolTipText(SelecttableBillPanel.this.getTipMessage());
			}

			if ((value instanceof Boolean)) {
				setSelected(value == null ? false : ((Boolean) value).booleanValue());
			} else if ((value instanceof UFBoolean)) {
				setSelected(value == null ? false : ((UFBoolean) value).booleanValue());
			} else {
				setSelected(false);
			}
			return this;
		}
	}

	private class HeaderPopupMenuActionListener implements ActionListener {
		private HeaderPopupMenuActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("SEL_ALL")) {
				SelecttableBillPanel.this.selectAllRows();
			} else if (e.getActionCommand().equals("SEL_NOT_ALL")) {
				SelecttableBillPanel.this.disSelectAllRows();
			} else if (e.getActionCommand().equals("SEL_RESVERSE")) {
				SelecttableBillPanel.this.reverseSelect();
			}
		}
	}

	private class HeaderPopupMouseAdapter extends MouseAdapter {
		private HeaderPopupMouseAdapter() {
		}

		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == 3) {
				TableColumnModel colModel = SelecttableBillPanel.this.getTable().getTableHeader().getColumnModel();
				int index = colModel.getColumnIndexAtX(e.getX());
				SelecttableBillPanel.this.getTable().getCellRenderer(0, index);
				SelecttableBillPanel.this.selectedRowCode = SelecttableBillPanel.this.getBodyKeyByCol(index);
				if ((SelecttableBillPanel.this.getTable().getCellRenderer(0, index) instanceof BillTableBooleanCellRenderer)) {
					SelecttableBillPanel.this.getPmHead().show((Component) e.getSource(), e.getX(), e.getY());
				}
			}
		}
	}

	public void addBodyVos(CircularlyAccessibleValueObject[] circularlyAccessibleValueObjects) {
		if (circularlyAccessibleValueObjects != null) {
			for (CircularlyAccessibleValueObject accessibleValueObject : circularlyAccessibleValueObjects) {
				getTableModel().addLine();
				int numberOfRow = getTableModel().getRowCount();
				getTableModel().setBodyRowVO(accessibleValueObject, numberOfRow - 1);
			}
		}
	}

	public void disSelectAllRows() {
		if (this.selectedRowCode == null) {
			this.selectedRowCode = this.selectRowCode;
		}
		int rowCount = getTable().getRowCount();
		if (rowCount > 0) {
			for (int i = 0; i < rowCount; i++) {
				if (getTable().getModel().isCellEditable(i, getTableModel().getBodyColByKey(this.selectedRowCode))) {
					setBodyCellValue(new Boolean(false), i, this.selectedRowCode);
				}
			}
		}
	}

	public Object getBodyCellValue(int rowIndex, String strKey) {
		return getTableModel().getValueAt(rowIndex, strKey);
	}

	private UIPopupMenu getPmHead() {
		if (this.pmHead == null) {
			this.pmHead = new UIPopupMenu();

			UIMenuItem miSelAll = new UIMenuItem(new MultiSelector().SEL_ALL_NAME);
			miSelAll.setActionCommand("SEL_ALL");
			miSelAll.addActionListener(getPmListener());

			UIMenuItem miNotSelAll = new UIMenuItem(new MultiSelector().SEL_NOT_ALL_NAME);
			miNotSelAll.setActionCommand("SEL_NOT_ALL");
			miNotSelAll.addActionListener(getPmListener());

			UIMenuItem miReverseSel = new UIMenuItem(new MultiSelector().SEL_RESVERSE_NAME);
			miReverseSel.setActionCommand("SEL_RESVERSE");
			miReverseSel.addActionListener(getPmListener());

			this.pmHead.add(miSelAll);
			this.pmHead.add(miNotSelAll);
			this.pmHead.add(miReverseSel);
		}

		return this.pmHead;
	}

	private ActionListener getPmListener() {
		if (this.pmListener == null) {
			this.pmListener = new HeaderPopupMenuActionListener();
		}

		return this.pmListener;
	}

	public CircularlyAccessibleValueObject[] getSelectedBodyVOs(Class<? extends CircularlyAccessibleValueObject> bodyVOClass) {
		List<CircularlyAccessibleValueObject> selectVoList = new ArrayList<CircularlyAccessibleValueObject>();
		int rowCount = getTable().getRowCount();
		if (rowCount <= 0) {
			return null;
		}
		for (int i = 0; i < rowCount; i++) {
			if (isSelected(i)) {
				CircularlyAccessibleValueObject accessibleValueObject = getTableModel().getBodyValueRowVO(i, bodyVOClass.getName());
				selectVoList.add(accessibleValueObject);
			}
		}
		if ((selectVoList != null) && (selectVoList.size() > 0)) {
			CircularlyAccessibleValueObject[] bodyVOs =
					(CircularlyAccessibleValueObject[]) Array.newInstance(bodyVOClass, selectVoList.size());

			return (CircularlyAccessibleValueObject[]) selectVoList.toArray(bodyVOs);
		}

		return null;
	}

	public String getSelectRowCode() {
		return this.selectRowCode;
	}

	public String getTipMessage() {
		return this.canNotEditTipMessage == null ? ResHelper.getString("6001frame", "06001frame0119") : this.canNotEditTipMessage;
	}

	public boolean isSelected(int rowIndex) {
		boolean isSelected = false;
		Object selectObject = getTableModel().getValueAt(rowIndex, this.selectRowCode);
		if ((selectObject instanceof Boolean)) {
			Boolean select = (Boolean) selectObject;
			isSelected = select.booleanValue();

		} else if ((selectObject instanceof UFBoolean)) {
			UFBoolean select = (UFBoolean) selectObject;
			isSelected = select.booleanValue();
		}
		return isSelected;
	}

	public void reverseSelect() {
		if (this.selectedRowCode == null) {
			this.selectedRowCode = this.selectRowCode;
		}
		int rowCount = getTable().getRowCount();
		if (rowCount > 0) {
			for (int i = 0; i < rowCount; i++) {
				if (getTable().getModel().isCellEditable(i, getTableModel().getBodyColByKey(this.selectedRowCode))) {
					setBodyCellValue(isSelected(i) ? new Boolean(false) : new Boolean(true), i, this.selectedRowCode);
				}
			}
		}
	}

	public void selectAllRows() {
		if (this.selectedRowCode == null) {
			this.selectedRowCode = this.selectRowCode;
		}
		int rowCount = getTable().getRowCount();
		if (rowCount > 0) {
			for (int i = 0; i < rowCount; i++) {
				if (getTable().getModel().isCellEditable(i, getTableModel().getBodyColByKey(this.selectedRowCode))) {
					setBodyCellValue(new Boolean(true), i, this.selectedRowCode);
				}
			}
		}
	}

	public void setBodyCellValue(Object aValue, int rowIndex, String strKey) {
		getTableModel().setValueAt(aValue, rowIndex, strKey);
	}

	public void setSelectRowCode(String selectRowCode) {
		this.selectRowCode = selectRowCode;
		getTable().getTableHeader().addMouseListener(new HeaderPopupMouseAdapter());
		TableColumnModel colModel = getTable().getTableHeader().getColumnModel();

		int index = getTableModel().getBodyColByKey(selectRowCode);

		int viewIndex = getTable().convertColumnIndexToView(index);
		if (getTableModel().getBodyItems()[index].getName() == null) {

			colModel.getColumn(viewIndex).setHeaderValue(new MultiSelector().SEL_COL_NAME);
		}
	}

	public void setSelectRowCodeRefEditCode(String selectRowCode, String refEditCode) {
		setSelectRowCode(selectRowCode);
		CheckRenderer renderer = new CheckRenderer(selectRowCode, refEditCode);
		int index = getTableModel().getBodyColByKey(selectRowCode);
		int viewIndex = getTable().convertColumnIndexToView(index);
		TableColumn tableColumn = getTable().getColumnModel().getColumn(viewIndex);
		tableColumn.setCellRenderer(renderer);
	}

	public void setTipMessage(String tipmsg) {
		this.canNotEditTipMessage = tipmsg;
	}
}
