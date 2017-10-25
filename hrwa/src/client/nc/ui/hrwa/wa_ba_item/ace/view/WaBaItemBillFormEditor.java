package nc.ui.hrwa.wa_ba_item.ace.view;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.md.model.impl.MDEnum;
import nc.ui.hr.itemsource.view.AbstractBillItemEditor;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pubapp.uif2app.model.HierachicalDataAppModel;
import nc.ui.uif2.AppEvent;
import nc.ui.wa.item.view.custom.CheckedTextField;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.wa_ba.item.ItemsVO;



@SuppressWarnings({ "restriction", "serial" })
public class WaBaItemBillFormEditor extends HrBillFormEditor {

	public static final int Float_width = 12;
	public static final int Float_Digit = 2;
	public static final int String_width = 20;
	public static final int String_Digit = 0;
	public static final int Date_width = 10;
	public static final int Date_Digit = 0;

	public static final int String_MAX_width = 128;


	private AbstractBillItemEditor[] customEditors;

	protected static final String DATA_SOURCE_PANEL = "datatype";

	protected WaItemDataSourcePanel itemDataSourcePanel;


	/**
	 * 自定义初始化方法
	 * @see nc.ui.hr.frame.view.HrBillFormEditor#initUI()
	 */
	@Override
	public void initUI() {
		super.initUI();
		//确定及集团是否可用

		itemDataSourcePanel = createItemDataSourcePanel();
		customEditors = createCustomEditors();

		
		for(AbstractBillItemEditor editor : customEditors){
			initCustomEditor(editor);
		}

		billCardPanel.setBillData(billCardPanel.getBillData());

		
		resetPrecision();
	}


	public void resetPrecision(){

		Integer iflddecimal = null;
		
		if(iflddecimal==null){
			iflddecimal = 2;
		}
		Integer ifldwidth = null;
		
		if(ifldwidth==null){
			ifldwidth = 12;
		}
		int length = ifldwidth + 1 + iflddecimal;
		getItemDataSourcePanel().getFixValueEditor().setMaxLength(length);
		getItemDataSourcePanel().getFixValueEditor().setNumPoint(iflddecimal);
		
		for (int i = 0; i < customEditors.length; i++) {
			if(customEditors[i] instanceof CheckedTextField){
				((CheckedTextField)customEditors[i]).setMaxLength(length);
				((CheckedTextField)customEditors[i]).setNumPoint(iflddecimal);
			}
		}
		
	}

	public WaItemDataSourcePanel getItemDataSourcePanel() {
		return itemDataSourcePanel;
	}
	protected WaItemDataSourcePanel createItemDataSourcePanel(){
		return  new WaItemDataSourcePanel(DATA_SOURCE_PANEL,getModel(),this);
	}
	protected AbstractBillItemEditor[] getCustomEditors() {
		return customEditors;
	}
	
	
	protected AbstractBillItemEditor[] createCustomEditors() {
		AbstractBillItemEditor[] editors = new AbstractBillItemEditor[]{
				getItemDataSourcePanel(),
		};
		return editors;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
	}

	protected  BillItem getHeadItem(String itemkey){
		return getBillCardPanel().getHeadItem(itemkey);
	}
	

	/**
	 * 初始化单个自定义控件
	 * @param editor
	 */
	private void initCustomEditor(AbstractBillItemEditor editor){
		BillItem item = billCardPanel.getHeadItem(editor.getKeyOfBillItem());
		getHeadItem("datatype").getValueObject();
		editor.setItem(item);
		editor.initialize();
		//初始化为不可用
		editor.setContentEnabled(false);
		item.setItemEditor(editor);
	}

	/**
	 * 事件监听处理
	 * @see nc.ui.hr.frame.view.HrBillFormEditor#handleEvent(nc.ui.uif2.AppEvent)
	 */
	@Override
	public void handleEvent(AppEvent event) {		
		
		super.handleEvent(event);
		if(event.getType().equals("Selection_Changed")){			
			billCardPanel.hideHeadItem(new String[]{"datatype"});
			billCardPanel.hideHeadItem(new String[]{"value"});
			billCardPanel.hideHeadItem(new String[]{"vformula","vformulastr"});
			billCardPanel.hideHeadItem(new String[]{"vdef1"});
			billCardPanel.hideHeadItem(new String[]{"vdef2"});
			
			ItemsVO itemvo =(ItemsVO) ( (HierachicalDataAppModel) event.getSource()).getSelectedData();
			if(itemvo==null){
				return;
			}
			Integer type =itemvo.getDatatype();
			
			showitem(type);
	
			
		}
		
		

	}
	
	
	/**
	 * 显示字段
	 */
	public void showitem(Integer datatype){
		switch(datatype){
		case 2:{
//			getHeadItem("value").setName("手工输入");
//			billCardPanel.showHeadItem(new String[]{"value"});
			break;
		}
		case 3:{
			getHeadItem("value").setName("固定值");
			getHeadItem("value").setEdit(false);
			billCardPanel.showHeadItem(new String[]{"value"});
			break;
		}
			
		default:{
			billCardPanel.showHeadItem(new String[]{"vformulastr"});
			break;
		}
			
		}
	}
	

	/**
	 * 增加从自定义控件取数的逻辑
	 * @see nc.ui.uif2.editor.BillForm#getValue()
	 */
	@Override
	public Object getValue() {
		Object value = "";
		
		getHeadItemValue("datatype");
			
		ItemsVO itemVO =  (ItemsVO) super.getValue();
		if(itemDataSourcePanel.getCurrentDataSourceItem().getValue()==3){
			UITextField uf  = (UITextField) itemDataSourcePanel.getCurrentDataSourceItem().getComponent();
			value = uf.getValue();
			itemVO.setValue(new UFDouble(value==null?"0":value.toString()));
		}else if (itemDataSourcePanel.getCurrentDataSourceItem().getValue()==1){
			UIRefPane ref = (UIRefPane) itemDataSourcePanel.getCurrentDataSourceItem().getComponent();
			String refpks = ref.getRefPK();
			String name = ref.getRefName();
			itemVO.setVformula("wageFormData("+refpks+")");
			itemVO.setVformulastr(name);
		}else if(itemDataSourcePanel.getCurrentDataSourceItem().getValue()==4){
			UIRefPane ref = (UIRefPane) itemDataSourcePanel.getCurrentDataSourceItem().getComponent();
			itemVO.setVdef2(ref.getRefPK());
		}
		if(itemVO.getCreator()==null){
			itemVO.setCreationtime( new UFDateTime());
			itemVO.setCreator( WorkbenchEnvironment.getInstance().getLoginUser().getCuserid());
		}
		itemVO.setVdef20((itemDataSourcePanel.getCurrentDataSourceItem().getValue()).toString());
		
		for(AbstractBillItemEditor editor : customEditors){
			editor.collectData(itemVO);
		}
		if(getModel().getContext().getNodeType()==NODE_TYPE.GROUP_NODE){
			itemVO.setPk_org(getModel().getContext().getPk_group());
		}

		return itemVO;
	}

	/**
	 * 增加对自定义控件赋值的逻辑
	 *
	 * @see nc.ui.uif2.editor.BillForm#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object object) {
		TypeEnumVO typeEnum = getTypeEnumValue();
		updateCustomEditorState(typeEnum); //修改面板公式面板

		super.setValue(object);
//
//		// 设定预算总额项目
//
		if (object == null) {
			return;
		}
		for (AbstractBillItemEditor editor : customEditors) {
			editor.setDataToEditor((SuperVO) object);
		}

	}

	public Integer getItemDataType(){

		Integer  itemType = (Integer)getHeadItem("datatype").getValueObject();
		if(itemType == null){
			itemType = 0;
		}

		return  itemType;
	}

	/**
	 * 更新自定义控件的状态
	 * @return
	 */
	protected void updateCustomEditorState(TypeEnumVO typeEnum) {
		for(AbstractBillItemEditor editor : customEditors){
			editor.updateStateByType(typeEnum);
		}
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		billCardPanel.showHeadItem(new String[]{"datatype"});
//		getHeadItem("datatype").clearViewData();
		
		ItemsVO vo = (ItemsVO) getHeadItem("datatype").getValueObject();
		vo.setDatatype(0);

	}

	
	@Override
	protected void onAdd() {
		
		super.onAdd();
		billCardPanel.showHeadItem(new String[]{"datatype"});
		TypeEnumVO typeEnum = getTypeEnumValue();
		updateCustomEditorState(typeEnum);


	}


	public TypeEnumVO getTypeEnumValue() {
		
		BillItem item = billCardPanel.getHeadItem("vdef20");
		if (item != null) {
			TypeEnumVO typeEnum = TypeEnumVO.FLOATTYPE;
			
			Integer itype = 0;
			if(item.getValueObject()!=null)
			itype = Integer.valueOf(item.getValueObject().toString());

			if (itype != null) {
				typeEnum = MDEnum.valueOf(TypeEnumVO.class, itype);
			}

			return typeEnum;
		}else{
			return TypeEnumVO.FLOATTYPE;//默认是数值
		}
	}

	public  String  getItemKey(){
		return  (String) billCardPanel.getHeadItem("pk_ba_item").getValueObject();
	}


	public  String  getItemPK(){
		return  (String) billCardPanel.getHeadItem("pk_ba_item").getValueObject();
	}


	public  Integer  getIfldWidth(){
		return  (Integer) 50;
	}

	public  Integer  getIflddecimal(){
		return  (Integer) 50;
	}

}
