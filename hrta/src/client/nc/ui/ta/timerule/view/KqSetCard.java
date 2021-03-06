package nc.ui.ta.timerule.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.TitledBorder;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringHelper;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.textfield.UITextType;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 考勤设置卡片的空间
 * 2010年10月20日12:41:27
 * @author caiyl
 *
 */
public class KqSetCard extends UIPanel implements ActionListener, ItemListener, MouseListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -170525437445518013L;

	private List<JComponent> kqSetPanelList;
	private List<JComponent> mustInputList;	//需要必输的组件

	/**
	 * 自动排班月数
	 */
	private UIRefPane autoarrangemonth;
	/**
	 * 刷卡数据文本文件所在路径
	 */
	private UIRefPane datafilepath;
	/**
	 * 考勤机数据支持签到、签退标识
	 */
	private UICheckBox checkinflag;
	/**
	 * 刷卡最小时间间隔（分钟）
	 */
	private UIRefPane mintimespace;
	/**
	 * 启用考勤地点异常判断
	 */
	private UICheckBox workplaceflag;
	/**
	 * 考勤月报是否需要审核
	 */
	private UICheckBox mreportapproveflag;
	/**
	 * 考勤月报出勤班数小数位数
	 */
	private UIRefPane mreportdecimal;
	/**
	 * 记录一个考勤期间签卡次数超过x次时提示
	 */
	private UIRefPane signcounts;
	/**
	 * 是否控制期间内加班时长
	 */
	//    private UICheckBox isctrlothours;
	/**
	 * 加班控制小时数
	 */
	private UIRefPane ctrlothours;
	/**
	 * 是否严格控制
	 */
	private UICheckBox isrestrictctrlot;
	/**
	 * 启用往期结余假优先
	 */
	private UICheckBox ispreholidayfirst;

	private UICheckBox dayabsentnotice;
	private UICheckBox daymidoutnotice;
	private UICheckBox dayearlynotice;
	private UICheckBox daylatenotice;

	// 考勤月报中 矿工是否发送通知
	private UICheckBox isAbsentNotice;

	// 考勤月报中 迟到是否发送通知
	private UICheckBox isLateNotice;

	// 考勤月报中 早退是否发送通知
	private UICheckBox isEarlyNotice;

	// 考勤月报中 迟到是否发送通知
	private UICheckBox isMidOutNotice;

	private UFDateTime ts;//ts

	// 假日类别
	//    private UIRefPane holidaysort;

	// 班次
	//    private UIRefPane shift;

	// 假日加班规则
	//    private UIButton otRuleInHoliday;

	// 组织节点才显示的panel
	//    private UIPanel orgShowPanel;
	//新进员工是否加入考勤档案和考勤方式
	private UIComboBox toPsndocType;

	//出差单据跨区间处理方式
	private UIComboBox awayOPPType;

	//出差单据跨区间处理方式
	private UIComboBox leaveOPPType;

	//加班单生成/校验规则
	private UIComboBox overtimeRule;

	//一个考勤期间内转调休的最大时长
	private UIRefPane toRestLongest;

	//跨年度休假是否允许保存
	private UICheckBox isCanSave;

	//转调休是否严格控制
	private UICheckBox istorestctrlot;

	private boolean editable = false;//当前页签是否为编辑态

	// Component Creation and Initialization **********************************

	public KqSetCard() {
		buildPanel();
	}

	/**
	 * Builds the flange editor panel.
	 * Columns are specified before components are added to the form,
	 * rows are added dynamically using the {@link DefaultFormBuilder}.<p>
	 *
	 * The builder combines a step that is done again and again:
	 * add a label, proceed to the next data column and add a component.
	 *
	 * @return the built panel
	 */
	public void buildPanel() {

		if (kqSetPanelList == null) {
			kqSetPanelList = new ArrayList<JComponent>();
		}
		if (mustInputList == null) {
			mustInputList = new ArrayList<JComponent>();
		}
		UIPanel mainPanel = new UIPanel();
		FormLayout mainLayout = new FormLayout("pref", "default,5px,default,5px,default");
		DefaultFormBuilder mainBuilder = new DefaultFormBuilder(mainLayout, mainPanel);
		add(mainPanel);

		FormLayout layout =
				new FormLayout("right:400px, 10px, 120px, 10px, left:60px,100px,100px,50px", "25px,25px,25px,25px,25px,25px,25px");

		// ===========工作日历及刷卡设置界面========
		{
			UIPanel daystatRulePanel = new UIPanel();
			daystatRulePanel.setBorder(new TitledBorder(null, ResHelper.getString("6017basedoc", "06017basedoc1865")
			/*@res"工作日历及刷卡设置"*/, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			DefaultFormBuilder dayBuilder = new DefaultFormBuilder(layout, daystatRulePanel);
			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1580")/*@res "刷卡数据文本文件所在路径"*/);
			dayBuilder.append(getDatafilepath(), 3);
			dayBuilder.nextLine();

			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1579")/*@res "自动排班月数"*/);
			dayBuilder.append(getAutoarrangemonth());
			dayBuilder.nextLine();

			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1581")/*@res "最小刷卡时间间隔"*/);
			dayBuilder.append(getMintimespace());
			dayBuilder.append(PublicLangRes.MINUTE());
			dayBuilder.nextLine();

			String msg = ResHelper.getString("6017basedoc", "06017basedoc1583")/*@res "一个考勤期间签卡次数超过{0}次时提示"*/;
			String part1 = StringHelper.getPartOfString(msg, 0);
			String part2 = StringHelper.getPartOfString(msg, 1);
			//一个考勤期间签卡次数超过
			UILabel part1Label = new UILabel(part1, UILabel.STYLE_TITLE_SMALL);
			dayBuilder.append(part1Label);
			dayBuilder.append(getSigncounts());
			//次时提示
			dayBuilder.append(part2);
			dayBuilder.nextLine();

			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1866")/*@res"新进员工自动加入考勤档案"*/);
			dayBuilder.append(getToPsndocType());
			dayBuilder.nextLine();

			dayBuilder.append("");
			dayBuilder.append(getCheckinflag(), 5);
			dayBuilder.nextLine();

			dayBuilder.append("");
			dayBuilder.append(getWorkplaceflag(), 4);

			mainBuilder.add(daystatRulePanel);
			mainBuilder.nextLine();
			mainBuilder.nextLine();
		}
		//==============考勤月报设置====================
		{
			UIPanel monthstatRulePanel = new UIPanel();
			monthstatRulePanel.setBorder(new TitledBorder(null, ResHelper.getString("6017basedoc", "06017basedoc1867")/*@res"考勤月报设置"*/
			, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			FormLayout monthLayout = new FormLayout("right:400px, 10px, 95px,5px,left:pref,5px,left:pref,5px,150px,5px", "25px,25px,pref");

			DefaultFormBuilder monthBuilder = new DefaultFormBuilder(monthLayout, monthstatRulePanel);

			monthBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1582")/*@res "考勤月报出勤班数小数位数"*/);
			monthBuilder.append(getMreportdecimal(), 2);
			monthBuilder.nextLine();

			monthBuilder.append("");
			monthBuilder.append(getMreportapproveflag(), 8);
			monthBuilder.nextLine();

			UILabel dayNoticeLabel = new UILabel(ResHelper.getString("6017basedoc", "06017basedoc1927"));
			monthBuilder.append(dayNoticeLabel);
			monthBuilder.append(getDaylatenotice());
			monthBuilder.append(getDayearlynotice());
			monthBuilder.append(getDayabsentnotice());
			monthBuilder.append(getDaymidoutnotice());
			monthBuilder.nextLine();

			UILabel noticeLabel = new UILabel(ResHelper.getString("6017basedoc", "06017basedoc1585")
			/*@res "计算月报时对存在下列异常的人员发送通知邮件"*/);
			monthBuilder.append(noticeLabel);
			monthBuilder.append(getIsLateNotice());
			monthBuilder.append(getIsEarlyNotice());
			monthBuilder.append(getIsAbsentNotice());
			monthBuilder.append(getIsMidOutNotice());

			mainBuilder.add(monthstatRulePanel);
			mainBuilder.nextLine();
			mainBuilder.nextLine();
		}
		//===============加班、休假、出差设置=======================
		{
			FormLayout timeitemLayout =
					new FormLayout("right:400px, 10px, 120px, 10px, left:90px,10px,165px,50px", "25px,25px,25px,25px,25px,25px,25px");

			UIPanel timeitemPanel = new UIPanel();
			timeitemPanel.setBorder(new TitledBorder(null, ResHelper.getString("6017basedoc", "06017basedoc1868")/*@res"加班、休假、出差设置"*/, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			DefaultFormBuilder timeitemBuilder = new DefaultFormBuilder(timeitemLayout, timeitemPanel);

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1869")/*@res"出差单据跨期处理方式"*/);
			timeitemBuilder.append(getAwayOPPType(), 3);
			timeitemBuilder.nextLine();

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1870")/*@res"休假单据跨期处理方式"*/);
			timeitemBuilder.append(getLeaveOPPType(), 3);
			timeitemBuilder.nextLine();

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1871")/*@res"加班单据生成/校验规则"*/);
			timeitemBuilder.append(getOverTimeRule());
			timeitemBuilder.nextLine();

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1872")/*@res"一个考勤期间内转调休最大时长"*/);
			timeitemBuilder.append(getToRestLongest());
			timeitemBuilder.append(PublicLangRes.HOUR());//小时
			timeitemBuilder.append(getIstorestctrlot());
			timeitemBuilder.nextLine();

			//    		timeitemBuilder.append(getIsctrlothours());
			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1873")/*@res"一个考勤期间内加班不得超过"*/);
			timeitemBuilder.append(getCtrlothours());
			timeitemBuilder.append(PublicLangRes.HOUR());//小时
			timeitemBuilder.append(getIsrestrictctrlot(), 2);
			timeitemBuilder.nextLine();

			//启用往期结余优先
			timeitemBuilder.append("");
			timeitemBuilder.append(getIspreholidayfirst(), 5);
			timeitemBuilder.nextLine();

			//跨年度是否允许保存
			timeitemBuilder.append("");
			timeitemBuilder.append(getIsCanSave(), 5);

			mainBuilder.add(timeitemPanel);
		}
		//        UIPanel mainPanel = new UIPanel();
		//        setLayout(new BorderLayout());
		//		add(mainPanel,BorderLayout.CENTER);
		//		UIPanel leftPanel = new UIPanel();
		//		leftPanel.setPreferredSize(new Dimension(150,100));
		//		UIPanel upPanel = new UIPanel();
		//		upPanel.setPreferredSize(new Dimension(150,20));
		//		add(leftPanel, BorderLayout.WEST);
		//		add(upPanel, BorderLayout.NORTH);
		//
		//		UIPanel centerPanel = new UIPanel();
		//		UIPanel noticePanel = new UIPanel();
		//		noticePanel.setLayout(new BorderLayout());
		//		UIPanel noticeCheckBoxPanel = new UIPanel();
		//
		//		mainPanel.setLayout(new BorderLayout());
		//		mainPanel.add(centerPanel,BorderLayout.NORTH);
		//		mainPanel.add(noticePanel,BorderLayout.CENTER);
		//
		//		//add(mainPanel, 3)
		//        FormLayout layout = new FormLayout(
		////        		  "left:200px, 20px, fill:100px, 15px, left:pref, 50px",
		//        		  "left:420px, 20px, fill:100px, 15px, left:pref, 50px",//为了调整英文界面而修改
		//        		  "");
		//        DefaultFormBuilder builder = new DefaultFormBuilder(layout,centerPanel);
		//
		//        FormLayout layoutNotice = new FormLayout(
		//        		  "left:200px, 20px, fill:100px, 15px, left:pref, 50px, left:200px, 20px",//为了英文界面修改，如果第一个200px太小会造成矿工不显示
		//        		  "");
		//        DefaultFormBuilder builderNotice = new DefaultFormBuilder(layoutNotice,noticeCheckBoxPanel);
		//
		//        if(kqSetPanelList == null) {
		//        	kqSetPanelList = new ArrayList<JComponent>();
		//        }
		//        if(mustInputList == null) {
		//			mustInputList = new ArrayList<JComponent>();
		//		}
		//        builder.append(ResHelper.getString("6017basedoc","06017basedoc1579")
		///*@res "自动排班月数"*/);
		//        builder.append(getAutoarrangemonth());
		//        builder.nextLine();
		//
		//        builder.append(ResHelper.getString("6017basedoc","06017basedoc1580")
		///*@res "刷卡数据文本文件所在路径"*/, getDatafilepath());
		//        builder.nextLine();
		//
		//        builder.append(getCheckinflag());
		//        builder.nextLine();
		//
		//        builder.append(ResHelper.getString("6017basedoc","06017basedoc1581")
		///*@res "最小刷卡时间间隔"*/);
		//        builder.append(getMintimespace());
		//        builder.append(PublicLangRes.MINUTE());
		//
		//        builder.nextLine();
		//        builder.append(getWorkplaceflag());
		//        builder.nextLine();
		//
		//        builder.append(getMreportapproveflag());
		//        builder.nextLine();
		//
		//        builder.append(ResHelper.getString("6017basedoc","06017basedoc1582")
		///*@res "考勤月报出勤班数小数位数"*/);
		//        builder.append(getMreportdecimal());
		//        builder.nextLine();
		//        String msg = ResHelper.getString("6017basedoc","06017basedoc1583")/*@res "一个考勤期间签卡次数超过{0}次时提示"*/;
		//
		//        String part1 = StringHelper.getPartOfString(msg, 0);
		//        String part2 = StringHelper.getPartOfString(msg, 1);
		//        //一个考勤期间签卡次数超过
		//        builder.append(part1);
		//        builder.append(getSigncounts());
		//        //次时提示
		//        builder.append(part2);
		//        builder.nextLine();
		//
		//        builder.append(getIsctrlothours());
		//        builder.append(getCtrlothours());
		//
		//        UIPanel panel = new UIPanel();
		//        panel.setLayout(new FlowLayout());
		//        String overtimelimitstr = getOvertimeLimitStr();
		//        UILabel lable = new UILabel(StringHelper.getPartOfString(overtimelimitstr, 1));
		//
		//        panel.add(lable);
		//        panel.add(getIsrestrictctrlot());
		//        builder.append(panel);
		//        builder.nextLine();
		//
		//        builder.append(getIspreholidayfirst());
		//        builder.nextLine();
		//
		//        UILabel noticeLable = new UILabel(ResHelper.getString("6017basedoc","06017basedoc1585")
		///*@res "计算月报时对存在下列异常的人员发送通知邮件"*/);
		//        noticePanel.add(noticeLable,BorderLayout.NORTH);
		//        noticePanel.add(noticeCheckBoxPanel,BorderLayout.CENTER);
		//        builderNotice.append(this.getIsAbsentNotice());
		//        builderNotice.append(this.getIsLateNotice());
		//        builderNotice.append(this.getIsEarlyNotice());
		//        builderNotice.append(this.getIsMidOutNotice());

		//        builder.append("假日类别");
		//        builder.append(getHolidaysort());
		//        builder.append(getOtRuleInHoliday());
		//        builder.nextLine();
		//
		//        builder.append(getOrgShowPanel(), 3);
		//        builder.nextLine();
	}

	//    public UIPanel getOrgShowPanel() {
	//    	if(orgShowPanel==null){
	//    		orgShowPanel = new UIPanel();
	//    		FormLayout layout = new FormLayout(
	//          		  "left:200px, 20px, fill:100px, 15px, left:pref, 50px",
	//          		  "");
	//          DefaultFormBuilder builder = new DefaultFormBuilder(layout,orgShowPanel);
	//
	//          builder.append("默认班次");
	//          builder.append(getShift());
	//          builder.nextLine();
	//    	}
	//		return orgShowPanel;
	//	}

	/**
	 * 强制鼠标焦点移开
	 */
	private void stopEditing() {
		if (CollectionUtils.isEmpty(kqSetPanelList))
			return;
		for (JComponent comp : kqSetPanelList) {
			if (comp instanceof UIRefPane)
				((UIRefPane) comp).stopEditing();
			//			try {

			//			} catch(ParseException e) {
			//				((UIRefPane)comp).setValue(null);
			//				(((UIRefPane)comp).getUITextField()).ShowErrToolTip(e.getMessage());
			//				Logger.error(e.getMessage(), e);
			//			}
			//
		}
	}

	/**
	 * 取页面中考勤设置信息
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public TimeRuleVO getValue(TimeRuleVO vo) throws Exception {
		stopEditing();
		// 自动排班月数默认
		int def_autoarrangemonth = 0;
		//刷卡最小时间间隔（分钟）默认
		int def_mintimespace = 0;
		//考勤月报出勤班数小数位数
		int def_mreportdecimal = 0;
		//记录一个考勤期间签卡次数超过x次时提示
		int def_signcounts = 0;

		try {

			// 自动排班月数
			def_autoarrangemonth =
					getAutoarrangemonth().getValueObj() == null ? 0 : Integer.valueOf(getAutoarrangemonth().getValueObj().toString());
			vo.setAutoarrangemonth(def_autoarrangemonth);

			// 刷卡数据文本文件所在的路径
			vo.setDatafilepath((String) getDatafilepath().getValueObj());
			// 考勤机数据支持签到.签退标识
			vo.setCheckinflag(UFBoolean.valueOf(getCheckinflag().isSelected()));
			// 刷卡最小时间间隔（分钟）默认
			if (getMintimespace().getValueObj() != null) {
				def_mintimespace = (Integer) getMintimespace().getValueObj();
			}
			vo.setMintimespace(def_mintimespace);

			// 启用考勤地点异常判断
			vo.setWorkplaceflag(UFBoolean.valueOf(getWorkplaceflag().isSelected()));

			// 考勤月报是否需审核
			vo.setMreportapproveflag(UFBoolean.valueOf(getMreportapproveflag().isSelected()));

			// 考勤月报出勤班数小数位数
			if (getMreportdecimal().getValueObj() != null) {
				def_mreportdecimal = (Integer) getMreportdecimal().getValueObj();
			}
			vo.setMreportdecimal(def_mreportdecimal);
			// 记录一个考勤期间签卡次数超过x次时提示
			if (getSigncounts().getValueObj() != null) {
				def_signcounts = (Integer) getSigncounts().getValueObj();
			}
			vo.setSigncounts(def_signcounts);

			// 是否控制期间内加班时长,如果是，则 加班控制小时数 为必输，Isrestrictctrlot：是否严格控制
			//			vo.setIsctrlothours(UFBoolean.valueOf(getIsctrlothours().isSelected()));
			//			if (getCtrlothours().getValueObj() != null) {
			//			}
			vo.setCtrlothours((UFDouble) getCtrlothours().getValueObj());
			if (vo.getCtrlothours().doubleValue() < 0) {//若加班时长小于零表示不作控制，则要保证不是严格控制
				getIsrestrictctrlot().setSelected(false);
			}
			vo.setIsrestrictctrlot(UFBoolean.valueOf(getIsrestrictctrlot().isSelected()));
			//启用往期结余假优先
			vo.setIspreholidayfirst(UFBoolean.valueOf(getIspreholidayfirst().isSelected()));

			//考勤异常 邮件发送设置
			vo.setExceptionnoticeset(this.getExceptionNoticeSet());

			vo.setTs(getTs());
			// 假日类别
			//			vo.setPk_holidaysort(getHolidaysort().getRefPK());
			//			// 默认班次
			//			vo.setPk_shift(getShift().getRefPK());
			//新进员工自动加入考勤档案
			vo.setTotbmpsntype((Integer) getToPsndocType().getSelectdItemValue());
			//出差单据跨期间处理方式
			vo.setAwayovperprtype((Integer) getAwayOPPType().getSelectdItemValue());
			//休假单据跨期间处理方式
			vo.setLevaeovperprtype((Integer) getLeaveOPPType().getSelectdItemValue());
			//加班单生成/校验规则
			vo.setOvertimecheckrule((Integer) getOverTimeRule().getSelectdItemValue());
			//一个考勤期间内转调休最大时长
			vo.setTorestlongest(getToRestLongest().getValueObj() == null ? new UFDouble(0) : (UFDouble) getToRestLongest().getValueObj());
			//若转调休时长小于零则不做控制，要保证严格控制没有选中
			if (vo.getTorestlongest().doubleValue() < 0) {
				getIstorestctrlot().setSelected(false);
			}
			//是否严格控制转调休时长
			vo.setIstorestctrlot(UFBoolean.valueOf(getIstorestctrlot().isSelected()));
			//跨年度休假是否允许保存
			vo.setIscansaveoveryear(UFBoolean.valueOf(getIsCanSave().isSelected()));

			vo.setDayabsentnotice(UFBoolean.valueOf(getDayabsentnotice().isSelected()));
			vo.setDayearlynotice(UFBoolean.valueOf(getDayearlynotice().isSelected()));
			vo.setDaylatenotice(UFBoolean.valueOf(getDaylatenotice().isSelected()));
			vo.setDaymidoutnotice(UFBoolean.valueOf(getDaymidoutnotice().isSelected()));

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

		return vo;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//是否选择一个考勤期间内加班不得超过x次，如果选中，则严格控制和小时数可编辑
		//		if(e.getSource().equals(getIsctrlothours())) {
		//			getCtrlothours().setEnabled(getIsctrlothours().isSelected());
		//			getIsrestrictctrlot().setEnabled(getIsctrlothours().isSelected());
		//		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

	}

	@Override
	public void setEnabled(boolean enabled) {
		editable = enabled;
		if (kqSetPanelList != null) {
			for (JComponent kqComp : kqSetPanelList) {
				kqComp.setEnabled(enabled);
			}
		}
		if (enabled) {
			//是否选择一个考勤期间内加班不得超过x次，如果选中，则严格控制和小时数可编辑
			//			getCtrlothours().setEnabled(getIsctrlothours().isSelected());
			//			getIsrestrictctrlot().setEnabled(getIsctrlothours().isSelected());
			//			getCtrlothours().getUITextField().setShowMustInputHint(getIsctrlothours().isSelected());
			//若转调休最大时长为负则不能选择严格控制
			//			if(getToRestLongest().getValueObj()==null||new UFDouble(getToRestLongest().getValueObj().toString()).doubleValue()<0){
			//				getIstorestctrlot().setSelected(false);
			//				getIstorestctrlot().setEnabled(false);
			//			}else{
			//				getIstorestctrlot().setEnabled(true);
			//			}
			setRestrictCtrlotEnable();
		}
		if (CollectionUtils.isEmpty(mustInputList))
			return;
		for (JComponent comp : mustInputList) {
			if (comp instanceof UIRefPane) {
				((UIRefPane) comp).getUITextField().setShowMustInputHint(enabled);
			}
		}
	}

	/**
	 * 转调休，加班时长是否严格控制 
	 */
	public void setRestrictCtrlotEnable() {
		getToRestLongest().stopEditing();
		//若转调休最大时长为负则不能选择严格控制
		if (getToRestLongest().getValueObj() == null || new UFDouble(getToRestLongest().getValueObj().toString()).doubleValue() < 0) {
			getIstorestctrlot().setSelected(false);
			getIstorestctrlot().setEnabled(false);
		} else {
			getIstorestctrlot().setEnabled(true && editable);
		}
		getCtrlothours().stopEditing();
		if (getCtrlothours().getValueObj() == null || new UFDouble(getCtrlothours().getValueObj().toString()).doubleValue() < 0) {
			getIsrestrictctrlot().setSelected(false);
			getIsrestrictctrlot().setEnabled(false);
		} else {
			getIsrestrictctrlot().setEnabled(true && editable);
		}
	}

	public void setValue(TimeRuleVO vo) {
		getAutoarrangemonth().setText(vo.getAutoarrangemonth() != null ? vo.getAutoarrangemonth().toString() : "6");
		getDatafilepath().setText(vo.getDatafilepath());
		getCheckinflag().setSelected(vo.getCheckinflag() == null ? false : vo.getCheckinflag().booleanValue());
		getMintimespace().setValueObj(vo.getMintimespace());
		getWorkplaceflag().setSelected(vo.getWorkplaceflag() == null ? false : vo.getWorkplaceflag().booleanValue());
		getMreportapproveflag().setSelected(vo.getMreportapproveflag() == null ? false : vo.getMreportapproveflag().booleanValue());
		getMreportdecimal().setValueObj(vo.getMreportdecimal());
		getSigncounts().setValueObj(vo.getSigncounts());
		//		getHolidaysort().setPK(vo.getPk_holidaysort());
		//		getShift().setPK(vo.getPk_shift());
		//		if(vo.getIsctrlothours() != null) {
		//			getIsctrlothours().setSelected(vo.getIsctrlothours().booleanValue());
		//		}
		getCtrlothours().setValueObj(vo.getCtrlothours());
		getIsrestrictctrlot().setSelected(vo.getIsrestrictctrlot() == null ? false : vo.getIsrestrictctrlot().booleanValue());
		getIspreholidayfirst().setSelected(vo.getIspreholidayfirst() == null ? false : vo.getIspreholidayfirst().booleanValue());

		this.setExceptionNoticeSet(vo);

		getToPsndocType().setSelectedItem(vo.getTotbmpsntype());
		getAwayOPPType().setSelectedItem(vo.getAwayovperprtype());
		getLeaveOPPType().setSelectedItem(vo.getLevaeovperprtype());
		getOverTimeRule().setSelectedItem(vo.getOvertimecheckrule());
		getToRestLongest().setValueObj(vo.getTorestlongest());
		getIstorestctrlot().setSelected(vo.getIstorestctrlot() == null ? false : vo.getIstorestctrlot().booleanValue());
		getIsCanSave().setSelected(vo.getIscansaveoveryear() == null ? false : vo.getIscansaveoveryear().booleanValue());
		setTs(vo.getTs());

		getDayabsentnotice().setSelected(vo.getDayabsentnotice() == null ? false : vo.getDayabsentnotice().booleanValue());
		getDayearlynotice().setSelected(vo.getDayearlynotice() == null ? false : vo.getDayearlynotice().booleanValue());
		getDaylatenotice().setSelected(vo.getDaylatenotice() == null ? false : vo.getDaylatenotice().booleanValue());
		getDaymidoutnotice().setSelected(vo.getDaymidoutnotice() == null ? false : vo.getDaymidoutnotice().booleanValue());
	}

	public void clearViewValue() {
		if (kqSetPanelList == null)
			return;

		for (JComponent comp : kqSetPanelList) {
			if (comp.getClass().getSimpleName().equals("UIRefPane")) {
				((UIRefPane) comp).setValueObj(null);
			}
			if (comp.getClass().getSimpleName().equals("UIComboBox")) {
				((UIComboBox) comp).setSelectedIndex(0);
			}
			if (comp.getClass().getSimpleName().equals("UICheckBox")) {
				((UICheckBox) comp).setSelected(false);
			}
		}
	}

	public List<JComponent> getKqSetPanelList() {
		return kqSetPanelList;
	}

	public void setKqSetPanelList(List<JComponent> kqSetPanelList) {
		this.kqSetPanelList = kqSetPanelList;
	}

	public UIRefPane getAutoarrangemonth() {
		if (autoarrangemonth == null) {
			autoarrangemonth = new UIRefPane();
			autoarrangemonth.setTextType(UITextType.TextInt);
			autoarrangemonth.setMinValue(0);
			//tsy 调整最大值及长度
//			autoarrangemonth.setMaxValue(ICommonConst.AUTOARRANGEMAX);
//			autoarrangemonth.setMaxLength(1);
			autoarrangemonth.setMaxValue(12);
			autoarrangemonth.setMaxLength(2);
			kqSetPanelList.add(autoarrangemonth);
			autoarrangemonth.setName("AutoarrangemonthField");
			autoarrangemonth.setButtonVisible(false);
			mustInputList.add(autoarrangemonth);	//必输
		}
		return autoarrangemonth;
	}

	public UIRefPane getDatafilepath() {
		if (datafilepath == null) {
			datafilepath = new UIRefPane();
			datafilepath.setName("DatafilepathField");
			datafilepath.setButtonVisible(false);
			kqSetPanelList.add(datafilepath);
		}
		return datafilepath;
	}

	public UICheckBox getCheckinflag() {
		if (checkinflag == null) {
			checkinflag = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1586")
			/*@res "考勤机数据支持签到、签退标识"*/);
			//    		checkinflag.setPreferredSize(new Dimension(200, 30));
			kqSetPanelList.add(checkinflag);
		}
		return checkinflag;
	}

	public UIRefPane getMintimespace() {
		if (mintimespace == null) {
			mintimespace = new UIRefPane();
			mintimespace.setTextType(UITextType.TextInt);
			mintimespace.setMaxLength(4);
			mintimespace.setMinValue(0);
			mintimespace.setMaxValue(9999);
			mintimespace.setName("mintimespace");
			mintimespace.setButtonVisible(false);
			kqSetPanelList.add(mintimespace);
		}
		return mintimespace;
	}

	public UICheckBox getWorkplaceflag() {
		if (workplaceflag == null) {
			workplaceflag = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1587")
			/*@res "启用考勤地点异常判断"*/);
			//    		workplaceflag.setPreferredSize(new Dimension(150, 30));
			kqSetPanelList.add(workplaceflag);
		}
		return workplaceflag;
	}

	public UICheckBox getMreportapproveflag() {
		if (mreportapproveflag == null) {
			mreportapproveflag = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1588")
			/*@res "考勤月报是否需要审核"*/);
			//    		mreportapproveflag.setPreferredSize(new Dimension(150, 30));
			kqSetPanelList.add(mreportapproveflag);
		}
		return mreportapproveflag;
	}

	public UIRefPane getMreportdecimal() {
		if (mreportdecimal == null) {
			mreportdecimal = new UIRefPane();
			mreportdecimal.setTextType(UITextType.TextInt);
			mreportdecimal.setMaxValue(4);
			mreportdecimal.setMinValue(0);
			mreportdecimal.setMaxLength(1);
			mreportdecimal.setName("Mreportdecimal");
			mreportdecimal.setButtonVisible(false);
			kqSetPanelList.add(mreportdecimal);
			mustInputList.add(mreportdecimal);	//必输
		}
		return mreportdecimal;
	}

	public UIRefPane getSigncounts() {
		if (signcounts == null) {
			signcounts = new UIRefPane();
			signcounts.setTextType(UITextType.TextInt);
			signcounts.setNumPoint(0);
			signcounts.setMaxLength(4);
			signcounts.setMinValue(0);
			signcounts.setMaxValue(9999);
			signcounts.setName("signcounts");
			signcounts.setButtonVisible(false);
			kqSetPanelList.add(signcounts);
		}
		return signcounts;
	}

	//	private String getOvertimeLimitStr(){
	//		return ResHelper.getString("6017basedoc","06017basedoc1589")
	//		/*@res "一个考勤期间内加班不得超过{0}小时,"*/;
	//	}
	//	public UICheckBox getIsctrlothours() {
	//		if(isctrlothours == null) {
	//			String msg = getOvertimeLimitStr();
	//			//一个考勤期间内加班不得超过
	//    		isctrlothours =	new UICheckBox(StringHelper.getPartOfString(msg, 0));
	////    		isctrlothours.getComp().setPreferredSize(new Dimension(180, 30));
	//    		isctrlothours.addActionListener(this);
	//    		 kqSetPanelList.add(isctrlothours);
	//    	}
	//		return isctrlothours;
	//	}

	public UIRefPane getCtrlothours() {
		if (ctrlothours == null) {
			ctrlothours = new UIRefPane();
			ctrlothours.setTextType(UITextType.TextDbl);
			ctrlothours.setNumPoint(2);
			ctrlothours.setMaxLength(6);
			//    		ctrlothours.setMinValue(-1);
			ctrlothours.setMaxValue(999.99);
			ctrlothours.setName("ctrlothours");
			ctrlothours.setButtonVisible(false);
			kqSetPanelList.add(ctrlothours);
			ctrlothours.setToolTipText(ResHelper.getString("6017basedoc", "06017basedoc1879")/*@res "负数表示不做控制"*/);
			ctrlothours.addMouseListener(this);
		}
		return ctrlothours;
	}

	public UICheckBox getIsrestrictctrlot() {
		if (isrestrictctrlot == null) {
			//    		isrestrictctrlot = new UICheckBox(ResHelper.getString("6017basedoc","06017basedoc1531")
			///*@res "严格控制"*/,UICheckBox.GRAY_WHEN_DISABLE,UICheckBox.CHECKBOX);
			isrestrictctrlot = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1531")
			/*@res "严格控制"*/);
			//    		isrestrictctrlot.getComp().setPreferredSize(new Dimension(300, 30));
			kqSetPanelList.add(isrestrictctrlot);
			isrestrictctrlot.addMouseListener(this);
		}
		return isrestrictctrlot;
	}

	public UICheckBox getIspreholidayfirst() {
		if (ispreholidayfirst == null) {
			ispreholidayfirst = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1590")
			/*@res "启用往期结余假优先"*/);
			//			ispreholidayfirst.setSelected(true);
			kqSetPanelList.add(ispreholidayfirst);
		}
		return ispreholidayfirst;
	}

	public UICheckBox getIsAbsentNotice() {
		if (isAbsentNotice == null) {
			isAbsentNotice = new UICheckBox(PublicLangRes.KUANGGONG());
			kqSetPanelList.add(isAbsentNotice);
		}
		return isAbsentNotice;
	}

	public UICheckBox getIsLateNotice() {
		if (isLateNotice == null) {
			isLateNotice = new UICheckBox(PublicLangRes.LATEIN());
			kqSetPanelList.add(isLateNotice);
		}
		return isLateNotice;
	}

	public UICheckBox getIsEarlyNotice() {
		if (isEarlyNotice == null) {
			isEarlyNotice = new UICheckBox(PublicLangRes.EARLYOUT());
			kqSetPanelList.add(isEarlyNotice);
		}
		return isEarlyNotice;
	}

	public UICheckBox getIsMidOutNotice() {
		if (isMidOutNotice == null) {
			isMidOutNotice = new UICheckBox(PublicLangRes.MIDOUT());
			kqSetPanelList.add(isMidOutNotice);
		}
		return isMidOutNotice;
	}

	/**
	 * 获取月报考勤异常发送邮件通知的配置信息
	 * @return
	 */
	public String getExceptionNoticeSet() {
		StringBuilder notice = new StringBuilder();
		if (this.getIsAbsentNotice().isSelected()) {
			notice.append(TimeRuleVO.EXCEPTION_NOTICE_ABSENT);
		}
		if (this.getIsLateNotice().isSelected()) {
			notice.append(TimeRuleVO.EXCEPTION_NOTICE_LATE);
		}
		if (this.getIsEarlyNotice().isSelected()) {
			notice.append(TimeRuleVO.EXCEPTION_NOTICE_EARLY);
		}
		if (this.getIsMidOutNotice().isSelected()) {
			notice.append(TimeRuleVO.EXCEPTION_NOTICE_MIDOUT);
		}
		return notice.toString();
	}

	public void setExceptionNoticeSet(TimeRuleVO vo) {

		this.getIsAbsentNotice().setSelected(vo.isAbsentNotice());
		this.getIsLateNotice().setSelected(vo.isLateNotice());
		this.getIsEarlyNotice().setSelected(vo.isEarlyNotice());
		this.getIsMidOutNotice().setSelected(vo.isMidOutNotice());
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UIComboBox getToPsndocType() {
		if (toPsndocType == null) {
			toPsndocType = new UIComboBox();
			toPsndocType.addItem(new DefaultConstEnum(0, ResHelper.getString("6017basedoc", "06017basedoc1874")
			/*@res "不加入考勤档案"*/));
			toPsndocType.addItem(new DefaultConstEnum(1, ResHelper.getString("6017psndoc", "06017psndoc0072")
			/*@res "手工考勤"*/));
			toPsndocType.addItem(new DefaultConstEnum(2, ResHelper.getString("6017psndoc", "06017psndoc0073")
			/*@res"机器考勤"*/));
			kqSetPanelList.add(toPsndocType);
		}
		return toPsndocType;
	}

	public DefaultConstEnum[] getOverPeriodProType() {
		return new DefaultConstEnum[] { new DefaultConstEnum(0, ResHelper.getString("6017basedoc", "06017basedoc1875")
		/*@res "分别计算在各自对应期间")*/), new DefaultConstEnum(1, ResHelper.getString("6017basedoc", "06017basedoc1876")
		/*@res "第一期间计算到第二期间"*/) };
	}

	public UIComboBox getAwayOPPType() {
		if (awayOPPType == null) {
			awayOPPType = new UIComboBox();
			awayOPPType.addItems(getOverPeriodProType());
			kqSetPanelList.add(awayOPPType);
		}
		return awayOPPType;
	}

	public UIComboBox getLeaveOPPType() {
		if (leaveOPPType == null) {
			leaveOPPType = new UIComboBox();
			leaveOPPType.addItems(getOverPeriodProType());
			kqSetPanelList.add(leaveOPPType);
		}
		return leaveOPPType;
	}

	public UIComboBox getOverTimeRule() {
		if (overtimeRule == null) {
			overtimeRule = new UIComboBox();
			overtimeRule.addItem(new DefaultConstEnum(0, ResHelper.getString("6017basedoc", "06017basedoc1877")/*@res "严格"*/));
			overtimeRule.addItem(new DefaultConstEnum(1, ResHelper.getString("6017basedoc", "06017basedoc1878")/*@res "宽松"*/));
			kqSetPanelList.add(overtimeRule);
		}
		return overtimeRule;
	}

	public UIRefPane getToRestLongest() {
		if (toRestLongest == null) {
			toRestLongest = new UIRefPane();
			toRestLongest.setTextType(UITextType.TextDbl);
			toRestLongest.setNumPoint(2);
			toRestLongest.setMaxLength(6);
			//    		toRestLongest.setMinValue(-1);
			toRestLongest.setMaxValue(999.99);
			toRestLongest.setButtonVisible(false);
			kqSetPanelList.add(toRestLongest);
			toRestLongest.setToolTipText(ResHelper.getString("6017basedoc", "06017basedoc1879")/*@res "负数表示不做控制"*/);
			toRestLongest.addMouseListener(this);
		}
		return toRestLongest;
	}

	public UICheckBox getIsCanSave() {
		if (isCanSave == null) {
			isCanSave = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1880")/*@res"跨年度休假是否允许保存"*/);
			kqSetPanelList.add(isCanSave);
		}
		return isCanSave;
	}

	/**
	 * 调休时长是否严格控制
	 * @return
	 */
	public UICheckBox getIstorestctrlot() {
		if (istorestctrlot == null) {
			istorestctrlot = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1531")
			/*@res "严格控制"*/);
			kqSetPanelList.add(istorestctrlot);
			istorestctrlot.addMouseListener(this);
		}
		return istorestctrlot;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setRestrictCtrlotEnable();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setRestrictCtrlotEnable();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public UICheckBox getDayabsentnotice() {
		if (this.dayabsentnotice == null) {
			this.dayabsentnotice = new UICheckBox(PublicLangRes.KUANGGONG());
			this.kqSetPanelList.add(this.dayabsentnotice);
		}
		return this.dayabsentnotice;
	}

	public UICheckBox getDaymidoutnotice() {
		if (this.daymidoutnotice == null) {
			this.daymidoutnotice = new UICheckBox(PublicLangRes.MIDOUT());
			this.kqSetPanelList.add(this.daymidoutnotice);
		}
		return this.daymidoutnotice;
	}

	public UICheckBox getDayearlynotice() {
		if (this.dayearlynotice == null) {
			this.dayearlynotice = new UICheckBox(PublicLangRes.EARLYOUT());
			this.kqSetPanelList.add(this.dayearlynotice);
		}
		return this.dayearlynotice;
	}

	public UICheckBox getDaylatenotice() {
		if (this.daylatenotice == null) {
			this.daylatenotice = new UICheckBox(PublicLangRes.LATEIN());
			this.kqSetPanelList.add(this.daylatenotice);
		}
		return this.daylatenotice;
	}
}