package nc.ui.hrwa.wa_ba_item.ace.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.wa.IItemQueryService;
import nc.md.model.impl.MDEnum;
import nc.ui.hr.formula.HRFormulaRefPane;
import nc.ui.hr.itemsource.Filter;
import nc.ui.hr.itemsource.FilterableComboBoxModel;
import nc.ui.hr.itemsource.view.AbstractBillItemEditor;
import nc.ui.hr.itemsource.view.BillItemWraper;
import nc.ui.hr.itemsource.view.DataSourceItem;
import nc.ui.hr.itemsource.view.ExceptDateType;
import nc.ui.hr.itemsource.view.FloatOnly;
import nc.ui.hr.itemsource.view.FormulaDataManager;
import nc.ui.hr.itemsource.view.FormulaWraper;
import nc.ui.hr.itemsource.view.NullDataManager;
import nc.ui.hr.itemsource.view.UsableController;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.textfield.UITextType;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.wa.classitem.view.OtherSourceDataWraper;
import nc.ui.wa.ref.WaWageformRefModel;
import nc.vo.hr.func.HrFormula;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.formulaset.ItemVO;
import nc.vo.wa.item.FromEnumVO;
import nc.vo.wa.wa_ba.item.ItemsVO;
import nc.vo.wa.wa_ba.item.WaBaLoginContext;
/**
 * 
 * @author: wh 
 * @date: 2009-11-24 ����01:00:06
 * @since: eHR V6.0
 * @�߲���: 
 * @�߲�����: 
 * @�޸���: 
 * @�޸�����: 
 */
@SuppressWarnings({ "serial", "deprecation" })
public class WaItemDataSourcePanel extends AbstractBillItemEditor{


	
	private UIRefPane wageForm;
	private UITextField fixValue;
	private UIRefPane grade;
	private UIComboBox dataSourceType;
	

	
	private AbstractAppModel model;
	private TypeEnumVO currentDataType = TypeEnumVO.FLOATTYPE;
	private UIPanel cardPanel;
	protected CardLayout cardLayout = new CardLayout();
	private final int defualtIndex = 2;
	
	private List<DataSourceItem> typeItems;

	private FilterableComboBoxModel<DataSourceItem> comboModel;
	 HRFormulaRefPane referPanlFormula;
	 
	 protected HrBillFormEditor parentEditor = null;
	
	public HrBillFormEditor getParentEditor() {
		return parentEditor;
	}

	public WaItemDataSourcePanel(String keyOfBillItem,AbstractAppModel model,HrBillFormEditor parentEditor) {
		super(keyOfBillItem);
		this.model = model;
		this.parentEditor  = parentEditor;
		 
		typeItems = createDataSourceItems();
		FlowLayout f = new FlowLayout(FlowLayout.LEFT);
		f.setVgap(-1);
		setLayout(f);
		setPreferredSize(new Dimension(getWidth(),30));
		setSize(new Dimension(getWidth(),30));
	}
	
	@Override
	public void initialize() {
		
		setLayout(new BorderLayout());
		add(getDataSourceType() , BorderLayout.WEST);
		getDataSourceType().setAdjustHight(true);
		add(getCardPanel() , BorderLayout.CENTER);
		getDataSourceType().setSelectedItem(typeItems.get(defualtIndex));
	}
	
	protected UIPanel getCardPanel(){
		if(cardPanel == null){
			cardPanel = new UIPanel();
			cardPanel.setLayout(cardLayout);
			for(DataSourceItem item : typeItems){
				cardPanel.add(item.getWraper(), item.getName());
			}
		}
		return cardPanel;
	}
	
	
	protected ItemVO[] getFormulaInitItemVOs(){
		IItemQueryService wiqs =NCLocator.getInstance().lookup(IItemQueryService.class);
		try {
			return wiqs.getFormulaInitVO(model.getContext());
		} catch (BusinessException e) {
			throw new IllegalStateException(e);
		}
	}

	public UIRefPane getWageForm() {
		if (wageForm == null) {
			wageForm = new UIRefPane();
			wageForm.setPreferredSize(new Dimension(200, 22));
			wageForm.setRefModel(new WaWageformRefModel());
		}
		return wageForm;
	}

	public UITextField getFixValueEditor() {
		if (fixValue == null) {
			fixValue = new UITextField();
			fixValue.setPreferredSize(new Dimension(150,20));
			fixValue.setTextType(UITextType.TextDbl);
			fixValue.setMaxLength(WaBaItemBillFormEditor.Float_width+1+ WaBaItemBillFormEditor.Float_Digit);
			fixValue.setNumPoint(WaBaItemBillFormEditor.Float_Digit);
		}
		return fixValue;
	}
	public UIRefPane getGrade() {
		if (grade == null) {
			grade = new UIRefPane();
			grade.setRefNodeName("н�ʱ�׼");
			grade.setRefModel(new nc.ui.wa.ref.WaGradeRefModel());
		}
		return grade;
	}
	
	protected UIComboBox getDataSourceType() {
		if (dataSourceType == null) {
			dataSourceType = new UIComboBox();
			comboModel = new FilterableComboBoxModel<DataSourceItem>(typeItems);
			comboModel.setFilter(new Filter<DataSourceItem>(){

				@Override
				public boolean accept(DataSourceItem item) {
					
					return item.isUsable();
				}
				
			});
			dataSourceType.setModel(comboModel);
			//dataSourceType.addItems(typeItems.toArray());
			dataSourceType.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange()==ItemEvent.SELECTED){
						DataSourceItem select = ((DataSourceItem)e.getItem());
						refreshDataSourceType(select);
						((WaBaItemBillFormEditor)getParentEditor()).resetPrecision();
					}
					
				}
			
			});
		}
		
		return dataSourceType;
	}
	protected  void refreshDataSourceType(DataSourceItem select) {

		
		//���¶��幫ʽ,������ʽ���õ��༭����
				ItemsVO vo = getNewItem();
				vo.setPk_ba_item(getItemPK());
				vo.setVformula("");
				vo.setVformulastr("");
				vo.setDatatype(getDataType());
				vo.setIitemtype(getDataType());
				vo.setIfromflag(getDataType());
		
		select.setData(vo);
		
		if(select.getValue()!=getDataType()){
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"value"});
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"vformula","vformulastr"});
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"vdef1"});
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"vdef2"});
			
			showitem(select.getValue());
		}else{
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"value"});
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"vformula","vformulastr"});
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"vdef1"});
			parentEditor.getBillCardPanel().hideHeadItem(new String[]{"vdef2"});
			showitem(select.getValue());
		}
		
		
        select.setComponetEnable(model.getUiState() == UIState.ADD 
                ||model.getUiState() == UIState.EDIT);
//		if(select.getValue().equals(FromEnumVO.WA_GRADE.value())){
//			// ����н�ʱ�׼����
//			Object obj= getParentEditor().getHeadItemValue("pk_wa_item");
//			String pk_wa_item = "";
//			if(obj != null){
//				pk_wa_item = obj.toString();
//			}
//			getGrade().getRefModel().setWherePart(" pk_wa_item = '"+pk_wa_item+"'");
//		}
		cardLayout.show(getCardPanel(), select.getName());
	}
	
	
	/**
	 * ��ʾ�ֶ�
	 */
	public void showitem(Integer datatype){
		switch(datatype){
		case 2:{
//			parentEditor.getBillCardPanel().getHeadItem("value").setName("�ֹ�����");
//			parentEditor.getBillCardPanel().getHeadItem("value").setEnabled(true);
//			parentEditor.getBillCardPanel().showHeadItem(new String[]{"value"});
			break;
		}
		case 3:{
			parentEditor.getBillCardPanel().getHeadItem("value").setName("�̶�ֵ");
			parentEditor.getBillCardPanel().getHeadItem("value").setEnabled(true);
			parentEditor.getBillCardPanel().showHeadItem(new String[]{"value"});
			break;
		}
			
		default:{
			parentEditor.getBillCardPanel().showHeadItem(new String[]{"vformulastr"});
			break;
		}
			
		}
	}
	

	protected ItemsVO getNewItem(){
		
		ItemsVO vo = new ItemsVO();		
		return vo;	
	}
	
	@Override
	public Object getValue() {
		return getCurrentDataSourceItem().getData(new ItemsVO());
		
	}
	
	@Override
	public void setDataToEditor(SuperVO vo){
		if(vo == null){
			vo = new ItemsVO();
		}
		
		Integer itemtype = (Integer)vo.getAttributeValue("iitemtype");
		Integer ifromflag = (Integer)vo.getAttributeValue("ifromflag");
		//TODO ����ȡֵ�����⣬����һ���ֳ�������
		this.currentDataType = MDEnum.valueOf(TypeEnumVO.class, itemtype);
		FromEnumVO fromType = MDEnum.valueOf(FromEnumVO.class, ifromflag);
		
		
		
		//�����������ϵͳн�ʡ�����ϵͳ���¡���ѡ������ϵͳ��н�ʡ�
		if(fromType.equals(FromEnumVO.HI)|| fromType.equals(FromEnumVO.WAORTHER)
				||fromType.equals(FromEnumVO.TA) ||fromType.equals(FromEnumVO.BM)
				||fromType.equals(FromEnumVO.PE)
				
				
				){
			fromType = FromEnumVO.WAORTHER;
		}		
		
		//������Դ
		for(DataSourceItem item : typeItems){
			if(item.getValue().equals(fromType.value())){
				getDataSourceType().setSelectedItem(item);
				break;
			}
		}
		getCurrentDataSourceItem().setData(vo);
	}
	
	protected TypeEnumVO getCurrentDataType() {
		return currentDataType;
	}

	@Override
	public void collectData(SuperVO itemVO){
		if(itemVO == null){
			itemVO = new ItemsVO();
		}
		
		Integer fromType = getCurrentDataSourceItem().getValue(); 
		((ItemsVO)itemVO).setDatatype(fromType);
		getCurrentDataSourceItem().getData(itemVO);
	}

	@Override
	public void setContentEnabled(boolean enabled) {
		getDataSourceType().setEnabled(enabled);
		getCurrentDataSourceItem().setComponetEnable(enabled);
	}

	@Override
	public void setPk_org(String pk_org) {
		for(DataSourceItem item : typeItems){
			item.setPk_org(pk_org);
		}
	}
	
	public DataSourceItem getCurrentDataSourceItem(){
		return comboModel.getSelectedItem();
	}
	
	@Override
	public void updateStateByType(TypeEnumVO type){
		this.currentDataType = type;
		for(DataSourceItem item : typeItems){
			item.setUsable(type);
		}
		
		DataSourceItem current = getCurrentDataSourceItem();
		if(!current.isUsable()){
			current = typeItems.get(defualtIndex);
		}
		
		comboModel.updateFilteredItems();
		comboModel.setSelectedItem(current);
		
		//����ģ��״̬ ����������� ����Ҫ��� ���޸�����Ҫ���
//		if(needClearData()){
			current.clearData(getNewItem());
//		}

	}
	
	protected boolean needClearData(){
		return model.getUiState()==UIState.ADD;
	}
	
	public AbstractAppModel getModel() {
		return model;
	}

	
	@SuppressWarnings("restriction")
	protected List<DataSourceItem> createDataSourceItems(){
		List<DataSourceItem> items = new ArrayList<DataSourceItem>(5);
		items.add(new DataSourceItem(FromEnumVO.FORMULA.getName(),FromEnumVO.FORMULA.value(),getReferPanlFormula(),
						 getFormulaDataManager(),  new FormulaWraper(),new ExceptDateType()));
		items.add(new DataSourceItem(FromEnumVO.WA_WAGEFORM.getName(),FromEnumVO.WA_WAGEFORM.value(),getWageForm(),
				new NullDataManager(), new BillItemWraper(),FloatOnly.shareInstance));
		items.add(new DataSourceItem(FromEnumVO.USER_INPUT.getName(),FromEnumVO.USER_INPUT.value(),new UIPanel(),
						new NullDataManager(),new BillItemWraper(),new UsableController() ));
		items.add(new DataSourceItem(FromEnumVO.FIX_VALUE.getName(),FromEnumVO.FIX_VALUE.value(),new UIPanel(),
						new NullDataManager(),new BillItemWraper(),new UsableController() ));
//		items.add(new DataSourceItem(FromEnumVO.WA_GRADE.getName(),FromEnumVO.WA_GRADE.value(),getGrade(),
//				new NullDataManager(), new BillItemWraper(),FloatOnly.shareInstance));
		
		
		/**
		 * ����ϵͳ��Ĭ���ǡ�����ϵͳ��н�ʣ���
		 */
		items.add(new DataSourceItem(FromEnumVO.WAORTHER.getName(),FromEnumVO.WAORTHER.value(),createComponetView(),
				new WaBaOtherSysFormulaDataManager(), new OtherSourceDataWraper(),new UsableController()));
		
//		/**
//		 * ���Ŵ������� 
//		 * ע: �û�����ѡ���������Դ. ֻ�и�������н�ʷ�����Ŀ������Դ��"���Ŵ������� "
//		 */
//		items.add(new DataSourceItem(FromEnumVO.TIMESCOLLECT.getName(),FromEnumVO.TIMESCOLLECT.value(),new UIPanel(),
//				new NullDataManager(),new BillItemWraper(),new WaBaCollectOnly((WaBaLoginContext)this.getModel().getContext())));
//		
	    
		
		return items;
	}
	
	private Component createComponetView(){
		WaBaOtherDataSourceEditor otherDataSourceEditor     =  new WaBaOtherDataSourceEditor(this);
		otherDataSourceEditor.setModel(getModel());
		
		HrFormula f = new HrFormula();
		
		ItemsVO waClassItemVO = (ItemsVO) getModel().getSelectedData();
		
		if(waClassItemVO!=null){
			//f.setBusinessLang(waClassItemVO.getVformulastr());
			f.setItemKey(waClassItemVO.getPk_ba_item());
			f.setReturnType(waClassItemVO.getIitemtype());
			f.setScirptLang(waClassItemVO.getVformula());
			
			//waClassItemVO.getFromEnumVO() ��Ĭ��ֵ��2  �ֹ����롣��Ҫ���е�����
			f.setIfromflag(waClassItemVO.getIfromflag());
			if(waClassItemVO.getVdef20().equals(FromEnumVO.USER_INPUT)){
				f.setIfromflag(FromEnumVO.WAORTHER.value());
			}
			otherDataSourceEditor.setFormula(f);
		}
		
		return otherDataSourceEditor;
	}
	
     protected 	FormulaDataManager  getFormulaDataManager(){
	  return new FormulaDataManager();
		
	}
	
	 protected UIRefPane getReferPanlFormula() {
		  try {
		   if (referPanlFormula == null) {
		    referPanlFormula = new WaBaItemFormulaRefPane(this.model,this.getParentEditor());
		    referPanlFormula.setAutoCheck(false);
		   }
		  } catch (Exception ex) {
			  Logger.error(ex.getMessage(),ex);
		  }
		  return referPanlFormula;
		 }

	/** 
	 * @author xuanlt on 2010-3-24 
	 * @see nc.ui.pub.bill.itemeditors.IBillItemEditor#stopEditing()
	 */
	@Override
	public boolean stopEditing() {
		System.out.println("stopEditing");
		return false;
	}
	
	public Integer getDataType(){
		WaBaItemBillFormEditor formEditor = (WaBaItemBillFormEditor)	getParentEditor();
		Object type =  formEditor.getHeadItem("vdef20").getValueObject();
		Integer t = 0;
		if(type!=null){
			t =  Integer.valueOf(type.toString());
		}
		return t;
		
	}
	
	protected String getItemKey(){
		WaBaItemBillFormEditor formEditor = (WaBaItemBillFormEditor)	getParentEditor();
		return formEditor.getItemKey();
		
	}
	
	protected String getItemPK(){
		WaBaItemBillFormEditor formEditor = (WaBaItemBillFormEditor)	getParentEditor();
		return formEditor.getItemPK();
		
	}
	
	
	protected Integer getIfldWidth(){
		WaBaItemBillFormEditor formEditor = (WaBaItemBillFormEditor)	getParentEditor();
		return formEditor.getIfldWidth();
		
	}
	
	protected Integer getIflddecimal(){
		WaBaItemBillFormEditor formEditor = (WaBaItemBillFormEditor)	getParentEditor();
		return formEditor.getIflddecimal();
		
	}
	
	

}
