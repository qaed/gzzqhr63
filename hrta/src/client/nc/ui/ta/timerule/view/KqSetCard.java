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
 * �������ÿ�Ƭ�Ŀռ�
 * 2010��10��20��12:41:27
 * @author caiyl
 *
 */
public class KqSetCard extends UIPanel implements ActionListener, ItemListener, MouseListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -170525437445518013L;

	private List<JComponent> kqSetPanelList;
	private List<JComponent> mustInputList;	//��Ҫ��������

	/**
	 * �Զ��Ű�����
	 */
	private UIRefPane autoarrangemonth;
	/**
	 * ˢ�������ı��ļ�����·��
	 */
	private UIRefPane datafilepath;
	/**
	 * ���ڻ�����֧��ǩ����ǩ�˱�ʶ
	 */
	private UICheckBox checkinflag;
	/**
	 * ˢ����Сʱ���������ӣ�
	 */
	private UIRefPane mintimespace;
	/**
	 * ���ÿ��ڵص��쳣�ж�
	 */
	private UICheckBox workplaceflag;
	/**
	 * �����±��Ƿ���Ҫ���
	 */
	private UICheckBox mreportapproveflag;
	/**
	 * �����±����ڰ���С��λ��
	 */
	private UIRefPane mreportdecimal;
	/**
	 * ��¼һ�������ڼ�ǩ����������x��ʱ��ʾ
	 */
	private UIRefPane signcounts;
	/**
	 * �Ƿ�����ڼ��ڼӰ�ʱ��
	 */
	//    private UICheckBox isctrlothours;
	/**
	 * �Ӱ����Сʱ��
	 */
	private UIRefPane ctrlothours;
	/**
	 * �Ƿ��ϸ����
	 */
	private UICheckBox isrestrictctrlot;
	/**
	 * �������ڽ��������
	 */
	private UICheckBox ispreholidayfirst;

	private UICheckBox dayabsentnotice;
	private UICheckBox daymidoutnotice;
	private UICheckBox dayearlynotice;
	private UICheckBox daylatenotice;

	// �����±��� ���Ƿ���֪ͨ
	private UICheckBox isAbsentNotice;

	// �����±��� �ٵ��Ƿ���֪ͨ
	private UICheckBox isLateNotice;

	// �����±��� �����Ƿ���֪ͨ
	private UICheckBox isEarlyNotice;

	// �����±��� �ٵ��Ƿ���֪ͨ
	private UICheckBox isMidOutNotice;

	private UFDateTime ts;//ts

	// �������
	//    private UIRefPane holidaysort;

	// ���
	//    private UIRefPane shift;

	// ���ռӰ����
	//    private UIButton otRuleInHoliday;

	// ��֯�ڵ����ʾ��panel
	//    private UIPanel orgShowPanel;
	//�½�Ա���Ƿ���뿼�ڵ����Ϳ��ڷ�ʽ
	private UIComboBox toPsndocType;

	//����ݿ����䴦��ʽ
	private UIComboBox awayOPPType;

	//����ݿ����䴦��ʽ
	private UIComboBox leaveOPPType;

	//�Ӱ൥����/У�����
	private UIComboBox overtimeRule;

	//һ�������ڼ���ת���ݵ����ʱ��
	private UIRefPane toRestLongest;

	//������ݼ��Ƿ�������
	private UICheckBox isCanSave;

	//ת�����Ƿ��ϸ����
	private UICheckBox istorestctrlot;

	private boolean editable = false;//��ǰҳǩ�Ƿ�Ϊ�༭̬

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

		// ===========����������ˢ�����ý���========
		{
			UIPanel daystatRulePanel = new UIPanel();
			daystatRulePanel.setBorder(new TitledBorder(null, ResHelper.getString("6017basedoc", "06017basedoc1865")
			/*@res"����������ˢ������"*/, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			DefaultFormBuilder dayBuilder = new DefaultFormBuilder(layout, daystatRulePanel);
			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1580")/*@res "ˢ�������ı��ļ�����·��"*/);
			dayBuilder.append(getDatafilepath(), 3);
			dayBuilder.nextLine();

			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1579")/*@res "�Զ��Ű�����"*/);
			dayBuilder.append(getAutoarrangemonth());
			dayBuilder.nextLine();

			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1581")/*@res "��Сˢ��ʱ����"*/);
			dayBuilder.append(getMintimespace());
			dayBuilder.append(PublicLangRes.MINUTE());
			dayBuilder.nextLine();

			String msg = ResHelper.getString("6017basedoc", "06017basedoc1583")/*@res "һ�������ڼ�ǩ����������{0}��ʱ��ʾ"*/;
			String part1 = StringHelper.getPartOfString(msg, 0);
			String part2 = StringHelper.getPartOfString(msg, 1);
			//һ�������ڼ�ǩ����������
			UILabel part1Label = new UILabel(part1, UILabel.STYLE_TITLE_SMALL);
			dayBuilder.append(part1Label);
			dayBuilder.append(getSigncounts());
			//��ʱ��ʾ
			dayBuilder.append(part2);
			dayBuilder.nextLine();

			dayBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1866")/*@res"�½�Ա���Զ����뿼�ڵ���"*/);
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
		//==============�����±�����====================
		{
			UIPanel monthstatRulePanel = new UIPanel();
			monthstatRulePanel.setBorder(new TitledBorder(null, ResHelper.getString("6017basedoc", "06017basedoc1867")/*@res"�����±�����"*/
			, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			FormLayout monthLayout = new FormLayout("right:400px, 10px, 95px,5px,left:pref,5px,left:pref,5px,150px,5px", "25px,25px,pref");

			DefaultFormBuilder monthBuilder = new DefaultFormBuilder(monthLayout, monthstatRulePanel);

			monthBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1582")/*@res "�����±����ڰ���С��λ��"*/);
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
			/*@res "�����±�ʱ�Դ��������쳣����Ա����֪ͨ�ʼ�"*/);
			monthBuilder.append(noticeLabel);
			monthBuilder.append(getIsLateNotice());
			monthBuilder.append(getIsEarlyNotice());
			monthBuilder.append(getIsAbsentNotice());
			monthBuilder.append(getIsMidOutNotice());

			mainBuilder.add(monthstatRulePanel);
			mainBuilder.nextLine();
			mainBuilder.nextLine();
		}
		//===============�Ӱࡢ�ݼ١���������=======================
		{
			FormLayout timeitemLayout =
					new FormLayout("right:400px, 10px, 120px, 10px, left:90px,10px,165px,50px", "25px,25px,25px,25px,25px,25px,25px");

			UIPanel timeitemPanel = new UIPanel();
			timeitemPanel.setBorder(new TitledBorder(null, ResHelper.getString("6017basedoc", "06017basedoc1868")/*@res"�Ӱࡢ�ݼ١���������"*/, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, null, null));
			DefaultFormBuilder timeitemBuilder = new DefaultFormBuilder(timeitemLayout, timeitemPanel);

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1869")/*@res"����ݿ��ڴ���ʽ"*/);
			timeitemBuilder.append(getAwayOPPType(), 3);
			timeitemBuilder.nextLine();

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1870")/*@res"�ݼٵ��ݿ��ڴ���ʽ"*/);
			timeitemBuilder.append(getLeaveOPPType(), 3);
			timeitemBuilder.nextLine();

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1871")/*@res"�Ӱ൥������/У�����"*/);
			timeitemBuilder.append(getOverTimeRule());
			timeitemBuilder.nextLine();

			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1872")/*@res"һ�������ڼ���ת�������ʱ��"*/);
			timeitemBuilder.append(getToRestLongest());
			timeitemBuilder.append(PublicLangRes.HOUR());//Сʱ
			timeitemBuilder.append(getIstorestctrlot());
			timeitemBuilder.nextLine();

			//    		timeitemBuilder.append(getIsctrlothours());
			timeitemBuilder.append(ResHelper.getString("6017basedoc", "06017basedoc1873")/*@res"һ�������ڼ��ڼӰ಻�ó���"*/);
			timeitemBuilder.append(getCtrlothours());
			timeitemBuilder.append(PublicLangRes.HOUR());//Сʱ
			timeitemBuilder.append(getIsrestrictctrlot(), 2);
			timeitemBuilder.nextLine();

			//�������ڽ�������
			timeitemBuilder.append("");
			timeitemBuilder.append(getIspreholidayfirst(), 5);
			timeitemBuilder.nextLine();

			//������Ƿ�������
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
		//        		  "left:420px, 20px, fill:100px, 15px, left:pref, 50px",//Ϊ�˵���Ӣ�Ľ�����޸�
		//        		  "");
		//        DefaultFormBuilder builder = new DefaultFormBuilder(layout,centerPanel);
		//
		//        FormLayout layoutNotice = new FormLayout(
		//        		  "left:200px, 20px, fill:100px, 15px, left:pref, 50px, left:200px, 20px",//Ϊ��Ӣ�Ľ����޸ģ������һ��200px̫С����ɿ󹤲���ʾ
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
		///*@res "�Զ��Ű�����"*/);
		//        builder.append(getAutoarrangemonth());
		//        builder.nextLine();
		//
		//        builder.append(ResHelper.getString("6017basedoc","06017basedoc1580")
		///*@res "ˢ�������ı��ļ�����·��"*/, getDatafilepath());
		//        builder.nextLine();
		//
		//        builder.append(getCheckinflag());
		//        builder.nextLine();
		//
		//        builder.append(ResHelper.getString("6017basedoc","06017basedoc1581")
		///*@res "��Сˢ��ʱ����"*/);
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
		///*@res "�����±����ڰ���С��λ��"*/);
		//        builder.append(getMreportdecimal());
		//        builder.nextLine();
		//        String msg = ResHelper.getString("6017basedoc","06017basedoc1583")/*@res "һ�������ڼ�ǩ����������{0}��ʱ��ʾ"*/;
		//
		//        String part1 = StringHelper.getPartOfString(msg, 0);
		//        String part2 = StringHelper.getPartOfString(msg, 1);
		//        //һ�������ڼ�ǩ����������
		//        builder.append(part1);
		//        builder.append(getSigncounts());
		//        //��ʱ��ʾ
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
		///*@res "�����±�ʱ�Դ��������쳣����Ա����֪ͨ�ʼ�"*/);
		//        noticePanel.add(noticeLable,BorderLayout.NORTH);
		//        noticePanel.add(noticeCheckBoxPanel,BorderLayout.CENTER);
		//        builderNotice.append(this.getIsAbsentNotice());
		//        builderNotice.append(this.getIsLateNotice());
		//        builderNotice.append(this.getIsEarlyNotice());
		//        builderNotice.append(this.getIsMidOutNotice());

		//        builder.append("�������");
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
	//          builder.append("Ĭ�ϰ��");
	//          builder.append(getShift());
	//          builder.nextLine();
	//    	}
	//		return orgShowPanel;
	//	}

	/**
	 * ǿ����꽹���ƿ�
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
	 * ȡҳ���п���������Ϣ
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	public TimeRuleVO getValue(TimeRuleVO vo) throws Exception {
		stopEditing();
		// �Զ��Ű�����Ĭ��
		int def_autoarrangemonth = 0;
		//ˢ����Сʱ���������ӣ�Ĭ��
		int def_mintimespace = 0;
		//�����±����ڰ���С��λ��
		int def_mreportdecimal = 0;
		//��¼һ�������ڼ�ǩ����������x��ʱ��ʾ
		int def_signcounts = 0;

		try {

			// �Զ��Ű�����
			def_autoarrangemonth =
					getAutoarrangemonth().getValueObj() == null ? 0 : Integer.valueOf(getAutoarrangemonth().getValueObj().toString());
			vo.setAutoarrangemonth(def_autoarrangemonth);

			// ˢ�������ı��ļ����ڵ�·��
			vo.setDatafilepath((String) getDatafilepath().getValueObj());
			// ���ڻ�����֧��ǩ��.ǩ�˱�ʶ
			vo.setCheckinflag(UFBoolean.valueOf(getCheckinflag().isSelected()));
			// ˢ����Сʱ���������ӣ�Ĭ��
			if (getMintimespace().getValueObj() != null) {
				def_mintimespace = (Integer) getMintimespace().getValueObj();
			}
			vo.setMintimespace(def_mintimespace);

			// ���ÿ��ڵص��쳣�ж�
			vo.setWorkplaceflag(UFBoolean.valueOf(getWorkplaceflag().isSelected()));

			// �����±��Ƿ������
			vo.setMreportapproveflag(UFBoolean.valueOf(getMreportapproveflag().isSelected()));

			// �����±����ڰ���С��λ��
			if (getMreportdecimal().getValueObj() != null) {
				def_mreportdecimal = (Integer) getMreportdecimal().getValueObj();
			}
			vo.setMreportdecimal(def_mreportdecimal);
			// ��¼һ�������ڼ�ǩ����������x��ʱ��ʾ
			if (getSigncounts().getValueObj() != null) {
				def_signcounts = (Integer) getSigncounts().getValueObj();
			}
			vo.setSigncounts(def_signcounts);

			// �Ƿ�����ڼ��ڼӰ�ʱ��,����ǣ��� �Ӱ����Сʱ�� Ϊ���䣬Isrestrictctrlot���Ƿ��ϸ����
			//			vo.setIsctrlothours(UFBoolean.valueOf(getIsctrlothours().isSelected()));
			//			if (getCtrlothours().getValueObj() != null) {
			//			}
			vo.setCtrlothours((UFDouble) getCtrlothours().getValueObj());
			if (vo.getCtrlothours().doubleValue() < 0) {//���Ӱ�ʱ��С�����ʾ�������ƣ���Ҫ��֤�����ϸ����
				getIsrestrictctrlot().setSelected(false);
			}
			vo.setIsrestrictctrlot(UFBoolean.valueOf(getIsrestrictctrlot().isSelected()));
			//�������ڽ��������
			vo.setIspreholidayfirst(UFBoolean.valueOf(getIspreholidayfirst().isSelected()));

			//�����쳣 �ʼ���������
			vo.setExceptionnoticeset(this.getExceptionNoticeSet());

			vo.setTs(getTs());
			// �������
			//			vo.setPk_holidaysort(getHolidaysort().getRefPK());
			//			// Ĭ�ϰ��
			//			vo.setPk_shift(getShift().getRefPK());
			//�½�Ա���Զ����뿼�ڵ���
			vo.setTotbmpsntype((Integer) getToPsndocType().getSelectdItemValue());
			//����ݿ��ڼ䴦��ʽ
			vo.setAwayovperprtype((Integer) getAwayOPPType().getSelectdItemValue());
			//�ݼٵ��ݿ��ڼ䴦��ʽ
			vo.setLevaeovperprtype((Integer) getLeaveOPPType().getSelectdItemValue());
			//�Ӱ൥����/У�����
			vo.setOvertimecheckrule((Integer) getOverTimeRule().getSelectdItemValue());
			//һ�������ڼ���ת�������ʱ��
			vo.setTorestlongest(getToRestLongest().getValueObj() == null ? new UFDouble(0) : (UFDouble) getToRestLongest().getValueObj());
			//��ת����ʱ��С�����������ƣ�Ҫ��֤�ϸ����û��ѡ��
			if (vo.getTorestlongest().doubleValue() < 0) {
				getIstorestctrlot().setSelected(false);
			}
			//�Ƿ��ϸ����ת����ʱ��
			vo.setIstorestctrlot(UFBoolean.valueOf(getIstorestctrlot().isSelected()));
			//������ݼ��Ƿ�������
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
		//�Ƿ�ѡ��һ�������ڼ��ڼӰ಻�ó���x�Σ����ѡ�У����ϸ���ƺ�Сʱ���ɱ༭
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
			//�Ƿ�ѡ��һ�������ڼ��ڼӰ಻�ó���x�Σ����ѡ�У����ϸ���ƺ�Сʱ���ɱ༭
			//			getCtrlothours().setEnabled(getIsctrlothours().isSelected());
			//			getIsrestrictctrlot().setEnabled(getIsctrlothours().isSelected());
			//			getCtrlothours().getUITextField().setShowMustInputHint(getIsctrlothours().isSelected());
			//��ת�������ʱ��Ϊ������ѡ���ϸ����
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
	 * ת���ݣ��Ӱ�ʱ���Ƿ��ϸ���� 
	 */
	public void setRestrictCtrlotEnable() {
		getToRestLongest().stopEditing();
		//��ת�������ʱ��Ϊ������ѡ���ϸ����
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
			//tsy �������ֵ������
//			autoarrangemonth.setMaxValue(ICommonConst.AUTOARRANGEMAX);
//			autoarrangemonth.setMaxLength(1);
			autoarrangemonth.setMaxValue(12);
			autoarrangemonth.setMaxLength(2);
			kqSetPanelList.add(autoarrangemonth);
			autoarrangemonth.setName("AutoarrangemonthField");
			autoarrangemonth.setButtonVisible(false);
			mustInputList.add(autoarrangemonth);	//����
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
			/*@res "���ڻ�����֧��ǩ����ǩ�˱�ʶ"*/);
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
			/*@res "���ÿ��ڵص��쳣�ж�"*/);
			//    		workplaceflag.setPreferredSize(new Dimension(150, 30));
			kqSetPanelList.add(workplaceflag);
		}
		return workplaceflag;
	}

	public UICheckBox getMreportapproveflag() {
		if (mreportapproveflag == null) {
			mreportapproveflag = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1588")
			/*@res "�����±��Ƿ���Ҫ���"*/);
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
			mustInputList.add(mreportdecimal);	//����
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
	//		/*@res "һ�������ڼ��ڼӰ಻�ó���{0}Сʱ,"*/;
	//	}
	//	public UICheckBox getIsctrlothours() {
	//		if(isctrlothours == null) {
	//			String msg = getOvertimeLimitStr();
	//			//һ�������ڼ��ڼӰ಻�ó���
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
			ctrlothours.setToolTipText(ResHelper.getString("6017basedoc", "06017basedoc1879")/*@res "������ʾ��������"*/);
			ctrlothours.addMouseListener(this);
		}
		return ctrlothours;
	}

	public UICheckBox getIsrestrictctrlot() {
		if (isrestrictctrlot == null) {
			//    		isrestrictctrlot = new UICheckBox(ResHelper.getString("6017basedoc","06017basedoc1531")
			///*@res "�ϸ����"*/,UICheckBox.GRAY_WHEN_DISABLE,UICheckBox.CHECKBOX);
			isrestrictctrlot = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1531")
			/*@res "�ϸ����"*/);
			//    		isrestrictctrlot.getComp().setPreferredSize(new Dimension(300, 30));
			kqSetPanelList.add(isrestrictctrlot);
			isrestrictctrlot.addMouseListener(this);
		}
		return isrestrictctrlot;
	}

	public UICheckBox getIspreholidayfirst() {
		if (ispreholidayfirst == null) {
			ispreholidayfirst = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1590")
			/*@res "�������ڽ��������"*/);
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
	 * ��ȡ�±������쳣�����ʼ�֪ͨ��������Ϣ
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
			/*@res "�����뿼�ڵ���"*/));
			toPsndocType.addItem(new DefaultConstEnum(1, ResHelper.getString("6017psndoc", "06017psndoc0072")
			/*@res "�ֹ�����"*/));
			toPsndocType.addItem(new DefaultConstEnum(2, ResHelper.getString("6017psndoc", "06017psndoc0073")
			/*@res"��������"*/));
			kqSetPanelList.add(toPsndocType);
		}
		return toPsndocType;
	}

	public DefaultConstEnum[] getOverPeriodProType() {
		return new DefaultConstEnum[] { new DefaultConstEnum(0, ResHelper.getString("6017basedoc", "06017basedoc1875")
		/*@res "�ֱ�����ڸ��Զ�Ӧ�ڼ�")*/), new DefaultConstEnum(1, ResHelper.getString("6017basedoc", "06017basedoc1876")
		/*@res "��һ�ڼ���㵽�ڶ��ڼ�"*/) };
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
			overtimeRule.addItem(new DefaultConstEnum(0, ResHelper.getString("6017basedoc", "06017basedoc1877")/*@res "�ϸ�"*/));
			overtimeRule.addItem(new DefaultConstEnum(1, ResHelper.getString("6017basedoc", "06017basedoc1878")/*@res "����"*/));
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
			toRestLongest.setToolTipText(ResHelper.getString("6017basedoc", "06017basedoc1879")/*@res "������ʾ��������"*/);
			toRestLongest.addMouseListener(this);
		}
		return toRestLongest;
	}

	public UICheckBox getIsCanSave() {
		if (isCanSave == null) {
			isCanSave = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1880")/*@res"������ݼ��Ƿ�������"*/);
			kqSetPanelList.add(isCanSave);
		}
		return isCanSave;
	}

	/**
	 * ����ʱ���Ƿ��ϸ����
	 * @return
	 */
	public UICheckBox getIstorestctrlot() {
		if (istorestctrlot == null) {
			istorestctrlot = new UICheckBox(ResHelper.getString("6017basedoc", "06017basedoc1531")
			/*@res "�ϸ����"*/);
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