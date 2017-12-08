package nc.ui.ta.monthstat.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSplitPane;
import javax.swing.table.TableCellEditor;

import nc.hr.utils.ResHelper;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.ta.daystat.model.DayStatEventConst;
import nc.ui.ta.monthstat.model.PsnMonthStatAppModel;
import nc.ui.ta.pub.standardpsntemplet.PsnTempletUtils;
import nc.ui.ta.pub.view.TaUFDoubleConvert;
import nc.ui.ta.statistic.pub.model.ViewOrderEventConst;
import nc.ui.ta.statistic.pub.view.AbstractPanelWithViewOrder;
import nc.ui.ta.vieworder.ViewOrderUtils;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.pub.AllParams;
import nc.vo.ta.pub.TALoginContext;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.ta.vieworder.ViewOrderVO;

import org.apache.commons.lang.ArrayUtils;

public class PsnMonthStatPanel extends AbstractPanelWithViewOrder<MonthStatVO> {

	/**
	 *
	 */
	private static final long serialVersionUID = 8155468077431271363L;
	private UISplitPane splitPane;
	private BillListPanel monthWorkPanel;//���ڹ�ʱpanel
	

	@Override
	protected void initColumns(ViewOrderVO[] viewOrderVOs) {
		BillTempletVO btv = new BillTempletVO();
		BillTempletHeadVO headVO = new BillTempletHeadVO();
		headVO.setBillTempletName("SYSTEM");
		headVO.setMetadataclass("hrta.tbmmonthstat");
		headVO.setModulecode("HRTA");
		btv.setParentVO(headVO);
		BillTempletBodyVO[] bodyVOs = PsnTempletUtils.getBasicBillTempletVO(IBillItem.HEAD, "hrta.tbmmonthstat").toArray(new BillTempletBodyVO[0]);
		List<BillTempletBodyVO> bodyVOList = new ArrayList<BillTempletBodyVO>(Arrays.asList(bodyVOs));
		int order = 50;
		//��Ⱥ��·��У���Ҫ����
		BillTempletBodyVO bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		bodyVOList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.STRING);
		bodyVO.setListshowflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey(MonthStatVO.TBMYEAR);
		bodyVO.setMetadataproperty("hrta.tbmmonthstat."+MonthStatVO.TBMYEAR);
		bodyVO.setMetadatapath(MonthStatVO.TBMYEAR);

		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		bodyVOList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.STRING);
		bodyVO.setListshowflag(false);
		bodyVO.setDefaultshowname(null);
		bodyVO.setItemkey(MonthStatVO.TBMMONTH);
		bodyVO.setMetadataproperty("hrta.tbmmonthstat."+MonthStatVO.TBMMONTH);
		bodyVO.setMetadatapath(MonthStatVO.TBMMONTH);

		TALoginContext context = (TALoginContext)getModel().getContext();
		boolean needApprove = context.getAllParams()!=null&&context.getAllParams().getTimeRuleVO()!=null&&context.getAllParams().getTimeRuleVO().getMreportapproveflag()!=null&&context.getAllParams().getTimeRuleVO().getMreportapproveflag().booleanValue();
		if(needApprove){//�����Ҫ��ˣ�����Ҫ�������״̬��
			//20171127 tsy ��ӹ����������ֶ� 
			//1.����״̬״̬
			bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
			bodyVOList.add(bodyVO);
			bodyVO.setDatatype(IBillItem.INTEGER);
			bodyVO.setDefaultshowname(null);
			bodyVO.setItemkey("approvestatus");
			bodyVO.setMetadataproperty("hrta.tbmmonthstat.approvestatus");
			bodyVO.setMetadatapath("approvestatus");
			//20171127 end
			bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
			bodyVOList.add(bodyVO);
			bodyVO.setDatatype(IBillItem.BOOLEAN);
			bodyVO.setDefaultshowname(null);
			bodyVO.setItemkey(MonthStatVO.ISAPPROVE);
			bodyVO.setMetadataproperty("hrta.tbmmonthstat.isapprove");
			bodyVO.setMetadatapath(MonthStatVO.ISAPPROVE);
		}
		//������Ŀ��
		BillTempletBodyVO[] itemBodyVOs = ViewOrderUtils.createTempletBodyVOsByViewOrderVOs(viewOrderVOs);
		btv.setChildrenVO((BillTempletBodyVO[])ArrayUtils.addAll(itemBodyVOs, bodyVOList.toArray(new BillTempletBodyVO[0])));
		BillListPanel billListPanel = getStatPanel();
		BillListData data = new BillListData(btv);
		billListPanel.setListData(data);
		//���ڹ�����������Ҫ��˲���Ҫcheckbox
		billListPanel.setMultiSelect(needApprove);
		
		//�������ݵ���ʾλ����ȡλ��ʽ
		AllParams allParams = ((TALoginContext)getModel().getContext()).getAllParams();
		if(null==allParams)
			return;
		TimeRuleVO timeRuleVO = allParams.getTimeRuleVO();
		TaUFDoubleConvert convert = new TaUFDoubleConvert(timeRuleVO);
		BillItem[] bodyItems = billListPanel.getBillListData().getHeadItems();
		for(BillItem billItem:bodyItems){
			if(billItem.getDataType() == IBillItem.DECIMAL){
				billItem.setDecimalDigits(timeRuleVO.getTimedecimal());
				billItem.setConverter(convert);
			}
		}
	}

	public void initUI(){
		
		setLayout(new CardLayout());
		UIPanel mainPanel = new UIPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(getStatPanel(), BorderLayout.CENTER);
		mainPanel.add(getPaginationBar(), BorderLayout.SOUTH);
		getSplitPane().setLeftComponent(mainPanel);
		getSplitPane().setRightComponent(getMonthWorkPanel());
		add(getSplitPane(),getSplitPane().getName());
		setComponentVisible(true);
		hideShowAttendInfo();
	}

	protected UISplitPane getSplitPane() {
		if(splitPane==null){
			splitPane = new UISplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setName("splitpanel");
			splitPane.setResizeWeight(0.75f);
		}
		return splitPane;
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		getMonthWorkPanel().setEnabled(true);
		//�������Ҫ��˵��������༭״̬�²���ʾ��ѡ��
		TimeRuleVO timeRuleVO = ((TALoginContext)getModel().getContext()).getAllParams().getTimeRuleVO();
		if(timeRuleVO!=null&&timeRuleVO.isMonthStatNeedApprove()){
			getStatPanel().setMultiSelect(false);
			getStatPanel().getHeadBillModel().updateValue();
		}
	}

	@Override
	protected void onNotEdit() {
		super.onNotEdit();
		getMonthWorkPanel().setEnabled(false);
		//�������Ҫ��˵��������Ǳ༭״̬����ʾ��ѡ��
		TimeRuleVO timeRuleVO = ((TALoginContext)getModel().getContext()).getAllParams().getTimeRuleVO();
		if(timeRuleVO!=null&&timeRuleVO.isMonthStatNeedApprove()){
			getStatPanel().setMultiSelect(true);
		}
	}

	protected BillListPanel getMonthWorkPanel() {
		if(monthWorkPanel==null){
			monthWorkPanel = new BillListPanel();
			processMonthWorkPanel();
		}
		return monthWorkPanel;
	}

	private void processMonthWorkPanel(){
		BillListData data = new BillListData(createMonthWorkTempletVO());
		getMonthWorkPanel().setListData(data);
		getMonthWorkPanel().getHeadTable().setSortEnabled(false);
		getMonthWorkPanel().getParentListPanel().addEditListener(new BillEditListener() {
			@Override
			public void bodyRowChange(BillEditEvent e) {

			}

			@Override
			public void afterEdit(BillEditEvent e) {
				//������޸��˳��ڱ���Ҫ���������飺
				//1.���±����������ΪMODIFICATION��2.���޸ĺ��ֵ���õ�appmodel���±�vo��modifiedmap��ȥ
				BillModel billModel = getStatPanel().getHeadBillModel();
				int headRow = getStatPanel().getHeadTable().getSelectedRow();
				billModel.setRowState(headRow, BillModel.MODIFICATION);

				int bodyRow = e.getRow();
				String key = e.getKey();
				UFDouble newVal = (UFDouble) getMonthWorkPanel().getHeadBillModel().getValueAt(bodyRow, key);
				MonthStatVO monthVO = (MonthStatVO) getChangableColumnAppModel().getSelectedData();
				monthVO.getMonthworkVOs()[bodyRow].setModifiedValue(key, newVal);
			}
		});
		getMonthWorkPanel().getParentListPanel().addEditListener2(this);
	}

	protected BillTempletVO createMonthWorkTempletVO(){
		String metadataclass="hrta.tbmmonthwork";
		BillTempletVO btv = PsnTempletUtils.createBillTempletVO(metadataclass,  "HRTA");
		int order=0;
		AllParams allParams = ((TALoginContext)getModel().getContext()).getAllParams();
		TimeRuleVO timeRuleVO = allParams==null?null:allParams.getTimeRuleVO();
		int monthWorkDecimalDigit = (timeRuleVO==null||timeRuleVO.getMreportdecimal()==null)?2: timeRuleVO.getMreportdecimal().intValue();
		List<BillTempletBodyVO> retList = new ArrayList<BillTempletBodyVO>();
		BillTempletBodyVO bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		bodyVO.setDatatype(IBillItem.UFREF);
		bodyVO.setDefaultshowname(ResHelper.getString("common","UC000-0002923")
/*@res "���"*/);
		bodyVO.setListshowflag(Boolean.TRUE);
		bodyVO.setItemkey(MonthWorkVO.PK_SHIFT);
		bodyVO.setMetadataproperty(metadataclass+"."+MonthWorkVO.PK_SHIFT);
		bodyVO.setMetadatapath(MonthWorkVO.PK_SHIFT);

		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		BillPanelUtils.setDecimalDigits(bodyVO, monthWorkDecimalDigit);
		bodyVO.setDatatype(IBillItem.DECIMAL);
		bodyVO.setDefaultshowname(null);
		bodyVO.setListshowflag(Boolean.TRUE);
		bodyVO.setItemkey(MonthWorkVO.WORKDAYS);
		bodyVO.setMetadataproperty(metadataclass+"."+MonthWorkVO.WORKDAYS);
		bodyVO.setMetadatapath(MonthWorkVO.WORKDAYS);

		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		BillPanelUtils.setDecimalDigits(bodyVO, monthWorkDecimalDigit);
		bodyVO.setEditflag(true);
		bodyVO.setDatatype(IBillItem.DECIMAL);
		bodyVO.setDefaultshowname(null);
		bodyVO.setListshowflag(Boolean.TRUE);
		bodyVO.setItemkey(MonthWorkVO.ACTUALWORKDAYS);
		bodyVO.setMetadataproperty(metadataclass+"."+MonthWorkVO.ACTUALWORKDAYS);
		bodyVO.setMetadatapath(MonthWorkVO.ACTUALWORKDAYS);

		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		BillPanelUtils.setDecimalDigits(bodyVO, monthWorkDecimalDigit);
		bodyVO.setDatatype(IBillItem.DECIMAL);
		bodyVO.setDefaultshowname(null);
		bodyVO.setListshowflag(Boolean.TRUE);
		bodyVO.setItemkey(MonthWorkVO.WORKHOURS);
		bodyVO.setMetadataproperty(metadataclass+"."+MonthWorkVO.WORKHOURS);
		bodyVO.setMetadatapath(MonthWorkVO.WORKHOURS);

		bodyVO = PsnTempletUtils.createDefaultBillTempletBodyVO(IBillItem.HEAD, order++);
		retList.add(bodyVO);
		BillPanelUtils.setDecimalDigits(bodyVO, monthWorkDecimalDigit);
		bodyVO.setEditflag(true);
		bodyVO.setDatatype(IBillItem.DECIMAL);
		bodyVO.setDefaultshowname(null);
		bodyVO.setListshowflag(Boolean.TRUE);
		bodyVO.setItemkey(MonthWorkVO.ACTUALWORKHOURS);
		bodyVO.setMetadataproperty(metadataclass+"."+MonthWorkVO.ACTUALWORKHOURS);
		bodyVO.setMetadatapath(MonthWorkVO.ACTUALWORKHOURS);

		btv.setChildrenVO(retList.toArray(new BillTempletBodyVO[0]));

		return btv;
	}

	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		//�����ڰ�����ʱ���ݷŵ�������
		if(AppEventConst.SELECTION_CHANGED.equals(event.getType())){
			MonthStatVO vo = (MonthStatVO) getChangableColumnAppModel().getSelectedData();
			if(vo==null)
				return;
			MonthWorkVO[] workVOs = vo.getMonthworkVOs();
			if(org.apache.commons.lang.ArrayUtils.isEmpty(workVOs)){
				getMonthWorkPanel().getHeadBillModel().clearBodyData();
				return;
			}
			if(getModel().getUiState()!=UIState.EDIT){
				getMonthWorkPanel().getBillListData().setHeaderValueObjectByMetaData(workVOs);
				return;
			}
			//����Ǳ༭���棬��Ƚ����⣬��Ҫ���޸Ĺ������ݷŵ�������
			MonthWorkVO[] cloneVOs  = new MonthWorkVO[workVOs.length];
			for(int i=0;i<cloneVOs.length;i++){
				cloneVOs[i]=(MonthWorkVO) workVOs[i].clone();
				cloneVOs[i].syncModified2Value();
			}
			getMonthWorkPanel().getBillListData().setHeaderValueObjectByMetaData(cloneVOs);
		}
		if(ViewOrderEventConst.EDIT_CANCELED.equals(event.getType())){//���ȡ���¼�
			if(org.apache.commons.lang.ArrayUtils.isEmpty(getStatAppModel().getData()))
				return;
			MonthStatVO vo = (MonthStatVO) getChangableColumnAppModel().getSelectedData();
			MonthWorkVO[] workVOs = vo.getMonthworkVOs();
			getMonthWorkPanel().getBillListData().setHeaderValueObjectByMetaData(workVOs);
			getMonthWorkPanel().getHeadBillModel().updateValue();
			return;
		}
		if(AppEventConst.MODEL_INITIALIZED.equals(event.getType())){
			//�ػ�ʵ�ʳ��ڱ���Ϊ��֯�ĳ������ݵ�С��λ�����ܷ����仯
			processMonthWorkPanel();
			//���ö�ѡ��
			BillModel model = getStatPanel().getHeadBillModel();
			int rowCount = model.getRowCount();
			if(((TALoginContext)getModel().getContext()).getAllParams()==null)
				return;
			TimeRuleVO timeRuleVO = ((TALoginContext)getModel().getContext()).getAllParams().getTimeRuleVO();
			if(rowCount>0&&timeRuleVO!=null&&timeRuleVO.isMonthStatNeedApprove())
				model.setRowState(0, rowCount-1, BillModel.SELECTED);
		}
		if(DayStatEventConst.ATTENDINFO_CHANGED.equals(event.getType())){
			hideShowAttendInfo();
			return;
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		//���״̬��ҳ�治�ɱ༭
		if(getModel().getUiState() == UIState.NOT_EDIT)
			return false;
		if("pk_psnjob".equals(e.getKey()))
			return false;
		
		
		TimeRuleVO timeRuleVO = ((TALoginContext)getModel().getContext()).getAllParams().getTimeRuleVO();
		//ά��Ȩ
		//if(timeRuleVO!=null){
		MonthStatVO mvo = (MonthStatVO) getChangableColumnAppModel().getSelectedData();
		boolean canEdit =
			DataPermissionFacade.isUserHasPermissionByMetaDataOperation(getModel().getContext().getPk_loginUser(),
					"60170psndoc", "MonthStatEdit", getModel().getContext().getPk_group(), mvo);
		//������֯��Ȩ�޸�
		if(!mvo.getPk_org().equals(getModel().getContext().getPk_org()))
			canEdit = false;
		if(!canEdit){
			ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("6017basedoc","06017basedoc1856")
					/*@res "����Ȩ��ѡ�е�����ִ���޸Ĳ���!"*/, getModel().getContext());
			return false;
		}
		ShowStatusBarMsgUtil.showStatusBarMsg(" ", getModel().getContext());
		//}
		//�Ѿ����ͨ���Ĳ����޸�
		if(timeRuleVO!=null&&timeRuleVO.isMonthStatNeedApprove()){
			MonthStatVO vo = (MonthStatVO) getChangableColumnAppModel().getSelectedData();
			if(vo.getIsapprove()!=null&&vo.getIsapprove().booleanValue())
				return false;
		}
		return super.beforeEdit(e);
	}

	@Override
	public Object getValue() {
		TableCellEditor editor = getMonthWorkPanel().getHeadTable().getCellEditor();
		if(editor!=null)
			editor.stopCellEditing();
		return super.getValue();
	}
	
	private void hideShowAttendInfo(){
		if(!((PsnMonthStatAppModel)getModel()).isShowAttendInfo()){
			if(getSplitPane().getRightComponent()!=null)
				getSplitPane().setRightComponent(null);
			return;
		}
		if(getSplitPane().getRightComponent()==null){
			getSplitPane().setRightComponent(getMonthWorkPanel());
		}
	}

}