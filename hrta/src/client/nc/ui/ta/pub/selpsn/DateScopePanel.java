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
 * 选择日期范围的公共panel
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
	
	private String curPk_org;//记录当前组织主键的字符串。用于在AppEventConst.MODEL_INITIALIZED事件发生时，判断是否是组织改变了，因为不是所有的AppEventConst.MODEL_INITIALIZED事件都是组织改变
	public DateScopePanel() {
		// TODO Auto-generated constructor stub
	}

	public void init(){
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(new UILabel(PublicLangRes.BEGINDATE()));
		add(getRefBeginDate());
		add(new UILabel(PublicLangRes.ENDDATE()));
		add(getRefEndDate());
		
		
		//下面应该加载界面上的开始日期、结束日期参照，并设置默认的日期范围：
		//当前业务日期所属的考勤期间的开始日期和结束日期，如果没有所属的考勤期间，则显示为空即可
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
		//下面应该加载界面上的开始日期、结束日期参照，并设置默认的日期范围：
		//当前业务日期所属的考勤期间的开始日期和结束日期，如果没有所属的考勤期间，则显示为空即可
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
					/*@res "日期范围不允许超过{0}天!"*/, 366+""));
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		if(event.getSource()==getRefBeginDate()){//如果用户设置了开始日期参照
			UFLiteralDate endDate = getEndDate();
			if(endDate==null)
				return;
			UFLiteralDate beginDate = getBeginDate();
			if(beginDate!=null&&beginDate.after(endDate)){//如果用户把开始日期调到了结束日期之后，则结束日期自动变成开始日期
				getRefEndDate().setValueObj(beginDate);
			}
			return;
		}
		if(event.getSource()==getRefEndDate()){//如果用户设置了结束日期参照
			UFLiteralDate beginDate = getBeginDate();
			if(beginDate==null)
				return;
			UFLiteralDate endDate = getEndDate();
			if(endDate!=null&&beginDate.after(endDate)){//如果用户把结束日期调到了开始日期之前，则开始日期自动变成结束日期
				getRefBeginDate().setValueObj(endDate);
			}
			return;
		}
		
	}
}
