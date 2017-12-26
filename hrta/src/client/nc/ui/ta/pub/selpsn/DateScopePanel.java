package nc.ui.ta.pub.selpsn;

import java.awt.FlowLayout;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.IRefConst;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.ta.period.utils.PeriodUtils;
import nc.ui.ta.pub.ICompWithValidateFunc;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.pub.ICommonConst;

import org.apache.commons.lang.StringUtils;

/**
 * ѡ�����ڷ�Χ�Ĺ���panel
 * @author zengcheng
 *
 */
public class DateScopePanel extends UIPanel implements AppEventListener, ValueChangedListener,ICompWithValidateFunc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4420257227136229432L;
	private UIRefPane refBeginDate = null;
	private UIRefPane refEndDate = null;
	private AbstractUIAppModel model;
	
	private String curPk_org;//��¼��ǰ��֯�������ַ�����������AppEventConst.MODEL_INITIALIZED�¼�����ʱ���ж��Ƿ�����֯�ı��ˣ���Ϊ�������е�AppEventConst.MODEL_INITIALIZED�¼�������֯�ı�
	public DateScopePanel() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(new UILabel(PublicLangRes.BEGINDATE()));
		add(getRefBeginDate());
		add(new UILabel(PublicLangRes.ENDDATE()));
		add(getRefEndDate());
		
		
		//����Ӧ�ü��ؽ����ϵĿ�ʼ���ڡ��������ڲ��գ�������Ĭ�ϵ����ڷ�Χ��
		//��ǰҵ�����������Ŀ����ڼ�Ŀ�ʼ���ںͽ������ڣ����û�������Ŀ����ڼ䣬����ʾΪ�ռ���
		resetDate();
	}

	
	/**
	 */
	private UIRefPane getRefBeginDate() {
		if (refBeginDate == null) {
			refBeginDate = new UIRefPane();
			refBeginDate.setName("refBeginDate");
			refBeginDate.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			refBeginDate.getUITextField().setFormatShow(true);
			refBeginDate.addValueChangedListener(this);
			refBeginDate.getUITextField().setShowMustInputHint(true);
		}
		return refBeginDate;
	}
	/**
	 */
	public UIRefPane getRefEndDate() {
		if (refEndDate == null) {
			refEndDate = new UIRefPane();
			refEndDate.setName("refEndDate");
			refEndDate.setRefNodeName(IRefConst.REFNODENAME_LITERALCALENDAR);
			refEndDate.addValueChangedListener(this);
			refEndDate.getUITextField().setShowMustInputHint(true);
		}
		return refEndDate;
	}
	
	public UFLiteralDate getBeginDate() {
		if(StringUtils.isEmpty(getRefBeginDate().getText()))
			return null;
		return new UFLiteralDate(getRefBeginDate().getValueObj().toString());
	}
	
	public void setBeginDate(UFLiteralDate beginDate) {
		if(beginDate == null) 
			return;
		getRefBeginDate().setValueObj(beginDate);
	}
	public void setEndDate(UFLiteralDate endDate) {
		if(endDate == null) 
			return;
		getRefEndDate().setValueObj(endDate);
	}
	
	public UFLiteralDate getEndDate(){
		if(StringUtils.isEmpty(getRefEndDate().getText()))
			return null;
		return new UFLiteralDate(getRefEndDate().getValueObj().toString());
	}
	
	

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	@Override
	public void handleEvent(AppEvent event) {
		if(AppEventConst.MODEL_INITIALIZED.equals(event.getType())){
			resetDate();
		}
		
	}
	
	protected void resetDate(){
		String pk_org = getModel().getContext().getPk_org();
		if(StringUtils.equals(curPk_org, pk_org))
			return;
		curPk_org = pk_org;
		//����Ӧ�ü��ؽ����ϵĿ�ʼ���ڡ��������ڲ��գ�������Ĭ�ϵ����ڷ�Χ��
		//��ǰҵ�����������Ŀ����ڼ�Ŀ�ʼ���ںͽ������ڣ����û�������Ŀ����ڼ䣬����ʾΪ�ռ���
		UFLiteralDate[] dates = PeriodUtils.getDefaultPeriodBeginEndDate(getModel().getContext());
		if(dates==null){
			getRefBeginDate().setValueObj(null);
			getRefEndDate().setValueObj(null);
			return;
		}
		UFLiteralDate beginDate = dates[0];
		UFLiteralDate endDate = dates[1];
		getRefBeginDate().setValueObj(beginDate);
		getRefEndDate().setValueObj(endDate);
	}
	@Override
	public void validateData() throws ValidationException{
		UFLiteralDate beginDate = getBeginDate();
		if(beginDate==null)
			throw new ValidationException(PublicLangRes.NOTNULL(PublicLangRes.BEGINDATE()));
		UFLiteralDate endDate = getEndDate();
		if(endDate==null)
			throw new ValidationException(PublicLangRes.NOTNULL(PublicLangRes.ENDDATE()));
		if(beginDate.after(endDate))
			throw new ValidationException(PublicLangRes.CANNOTLATER(PublicLangRes.BEGINDATE(), PublicLangRes.ENDDATE()));
		if(UFLiteralDate.getDaysBetween(beginDate, endDate)>366)
			throw new ValidationException(ResHelper.getString("6017basedoc","06017basedoc1917"
					/*@res "���ڷ�Χ��������{0}��!"*/, 366+""));
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if(event.getSource()==getRefBeginDate()){//����û������˿�ʼ���ڲ���
			UFLiteralDate endDate = getEndDate();
			if(endDate==null)
				return;
			UFLiteralDate beginDate = getBeginDate();
			if(beginDate!=null&&beginDate.after(endDate)){//����û��ѿ�ʼ���ڵ����˽�������֮������������Զ���ɿ�ʼ����
				getRefEndDate().setValueObj(beginDate);
			}
			return;
		}
		if(event.getSource()==getRefEndDate()){//����û������˽������ڲ���
			UFLiteralDate beginDate = getBeginDate();
			if(beginDate==null)
				return;
			UFLiteralDate endDate = getEndDate();
			if(endDate!=null&&beginDate.after(endDate)){//����û��ѽ������ڵ����˿�ʼ����֮ǰ����ʼ�����Զ���ɽ�������
				getRefBeginDate().setValueObj(endDate);
			}
			return;
		}
		
	}
}
