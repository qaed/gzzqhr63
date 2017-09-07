package nc.ui.hrwa.wa_ba_item.ace.view;

import java.awt.event.ItemListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nc.hr.utils.ResHelper;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIList;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.wa.ref.WaItemRefModelForGrade;
import nc.vo.pub.BusinessException;
import nc.vo.wa.wa_ba.item.WaBaLoginContext;

import org.apache.commons.lang.StringUtils;

public class WaBaParaAdjPanel extends UIPanel implements ItemListener, IParaPanel, IWaRefPanel{

    private static final long serialVersionUID = -5828168400573519326L;
    private nc.ui.pub.beans.UILabel ivjlblType = null;
    private UILabel ivjUILabel2 = null;
//    private nc.ui.pub.beans.UIComboBox ivjcboRef = null;
    private WaBaLoginContext context = null;
    private int datatype = 0; // ������Ҫ���ص��������ͣ���ֵ��
    private String itemKey;
    private UIRefPane itemref = null;
    public static final String sdje = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0206")/*@res "ʱ�㷢�Ž��"*/;
    public static final String yffje = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0207")/*@res "ԭ���Ž��"*/;
    public static final String xffje = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0208")/*@res "�ַ��Ž��"*/;

    private UIList uiList= null;
    /**
     * WaParaAdjPanel ������ע�⡣
     */
    public WaBaParaAdjPanel(){
        super();
        initialize();
    }

    /**
     * WaParaAdjPanel ������ע�⡣
     *
     * @param p0
     *            java.awt.LayoutManager
     */
    public WaBaParaAdjPanel(java.awt.LayoutManager p0){
        super(p0);
    }

    /**
     * WaParaAdjPanel ������ע�⡣
     *
     * @param p0
     *            java.awt.LayoutManager
     * @param p1
     *            boolean
     */
    public WaBaParaAdjPanel(java.awt.LayoutManager p0, boolean p1){
        super(p0, p1);
    }

    /**
     * WaParaAdjPanel ������ע�⡣
     *
     * @param p0
     *            boolean
     */
    public WaBaParaAdjPanel(boolean p0){
        super(p0);
    }

    /**
     * /** * ��������ĺϷ��� * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
     * ��Ҫ�μ����������� * @since �������һ���汾���˷�������ӽ���������ѡ�� *
     *
     * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
     *
     * @return java.lang.String
     */
    public void checkPara(int dataType) throws java.lang.Exception{
        Object obj =  getcboRef().getSelectedValue();
        
        //itemKey is null  ��֪��β�����
        if(StringUtils.isBlank(itemKey)){
        	itemKey = "itemkey";
        	//throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0247"))/*@res"����ѡ���Ӧ�Ĺ���н����Ŀ��*/;
        }
        
        if (obj == null) {
            throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0209")/*@res "��ѡ��ȡֵ��ʽ"*/);
        } else {
            String valueTypeName = getcboRef().getSelectedValue().toString();
            if (!valueTypeName.equals(sdje)) {
                // ԭ���Ž�� �� �ַ��Ž�� ����ѡ��ȡֵ��Ŀ
                Object itemid = getItemRef().getRefPK();
                if (itemid == null) {
                    throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0210")/*@res "��ѡ��ȡֵ��Ŀ"*/);
                }
            }

        }
    }

    /**
     * ���� cmbClass ����ֵ��
     *
     * @return nc.ui.pub.beans.UIComboBox
     */
    /* ���棺�˷������������ɡ� */
    private UIRefPane getItemRef(){
        if (itemref == null) {
            itemref = new UIRefPane();
            itemref.setName("itemref");
            itemref.setBounds(100, 100, 122, 22);
            itemref.setVisible(false);
        }
        return itemref;
    }

    /**
     * /** * ���������Ĺ��ܡ���;�������Եĸ��ģ��Լ�����ִ��ǰ������״̬�� * @param ����˵�� * @return ����ֵ * @exception
     * �쳣���� * @see ��Ҫ�μ����������� * @since �������һ���汾���˷�������ӽ���������ѡ�� *
     *
     * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
     *
     */
    public void clearDis(){

    }

    /**
     * ���� UIComboBox1 ����ֵ��
     *
     * @return nc.ui.pub.beans.UIComboBox
     */
    /* ���棺�˷������������ɡ� */
    private UIList getcboRef(){
        if (uiList == null) {
            uiList = new nc.ui.pub.beans.UIList();
            uiList.setName("cboRef");
            uiList.setBounds(100, 15, 200, 80);
            uiList.setAutoscrolls(true);
            String[] names = { sdje, yffje, xffje };
            uiList.setSelectionMode(0);
            uiList.setListData(names);
            uiList.setBorder(getBorder());
            uiList.addListSelectionListener(new ListSelectionListener(){

                @Override
                public void valueChanged(ListSelectionEvent e){
                    itemStateChanged(null);
                }});
        }
        return uiList;
    }
    /**
     * ���� lblType ����ֵ��
     *
     * @return nc.ui.pub.beans.UILabel
     */
    private nc.ui.pub.beans.UILabel getlblType(){
        if (ivjlblType == null) {
            ivjlblType = new nc.ui.pub.beans.UILabel();
            ivjlblType.setName("lblType");
            ivjlblType.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0211")/*@res "н�ʱ䶯ȡֵ��ʽ"*/);
            ivjlblType.setBounds(0, 14, 102, 22);
        }
        return ivjlblType;
    }

    /**
     * ���� UILabel2 ����ֵ��
     */
    private nc.ui.pub.beans.UILabel getUILabel2(){
        if (ivjUILabel2 == null) {
            ivjUILabel2 = new nc.ui.pub.beans.UILabel();
            ivjUILabel2.setName("UILabel2");
            ivjUILabel2.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0212")/*@res "ȡֵн����Ŀ"*/);
            ivjUILabel2.setBounds(10, 100, 80, 22);
            ivjUILabel2.setVisible(false);
        }
        return ivjUILabel2;
    }

    /**
     * ��һ��������ָ����Ŀ�� ����һ����ָȡ������
     *
     * @author zhangg on 2010-6-24
     * @see nc.ui.hr.itemsource.view.IParaPanel#getPara()
     */
    public java.lang.String[] getPara() throws java.lang.Exception{
        Object v = getcboRef().getSelectedIndex();
        if (!getcboRef().getSelectedValue().equals(sdje)) {
            String itemid = getItemRef().getRefValue("pk_wa_item").toString();
            String split = "#";
            v = split + v + split + itemid + split;
        }

       
        return new String[] { itemKey, v.toString() };
    }

//    /**
//     * @return java.lang.String
//     */
//    public java.lang.String getParaStr(){
//        String valueTypeName =  (String)getcboRef().getSelectedValue() ;
//        String split = ",";
//        Integer valueTypeIndex = getcboRef().getSelectedIndex();
//        if (valueTypeIndex == 0) {
//            return valueTypeName;
//        }
//        String itemid = getItemRef().getRefValue(WaItemVO.NAME).toString();
//        return valueTypeName + split + itemid;
//    }

    /**
     * ��ʼ���ࡣ
     */
    private void initialize(){
        setName("WaParaAdjPanel");
        setLayout(null);
        setSize(253, 96);
        add(getlblType(), getlblType().getName());
        add(getItemRef(), getItemRef().getName());
        add(getUILabel2(), getUILabel2().getName());
        add(getcboRef(), getcboRef().getName());

    }

    public void setDatatype(int newDatatype){
        datatype = newDatatype;
    }

    /**
     * @param paras
     *            nc.vo.wa.func.FunctableItemVO[]
     */
    public void updateDis(nc.vo.hr.func.FunctableItemVO[] paras){

    }

    /**
     * /** * ��������ʱˢ����ʾ * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
     * ��Ҫ�μ����������� * @since �������һ���汾���˷�������ӽ���������ѡ�� *
     *
     * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
     *
     * @param paras
     *            nc.vo.wa.func.FunctableItemVO[]
     */
    public void updateDis(int index){

    }

    /**
     * /** * ��������ʱˢ����ʾ * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
     * ��Ҫ�μ����������� * @since �������һ���汾���˷�������ӽ���������ѡ�� *
     *
     * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
     *
     * @param paras
     *            nc.vo.wa.func.FunctableItemVO[]
     */
    public void updateDis(java.lang.String funcname){

    }

    /**
     * @author zhangg on 2010-6-3
     * @see nc.ui.hr.itemsource.view.IParaPanel#setCurrentItemKey(java.lang.String)
     */
    @Override
    public void setCurrentItemKey(String itemKey){

        this.itemKey = itemKey;
    }

    @Override
    public WaBaLoginContext getContext(){
        // TODO Auto-generated method stub
        return context;
    }

    @Override
    public void setContext(WaBaLoginContext context){
        // TODO Auto-generated method stub
        this.context = context;
    }

    public void itemStateChanged(java.awt.event.ItemEvent e){
        Object value = getcboRef().getSelectedValue();
        getItemRef().setText("");
        if (value != null) {
            String strValue = value.toString();
            if (strValue.equals(sdje)) {
                getItemRef().setVisible(false);
                getUILabel2().setVisible(false);
            } else {
                getItemRef().setVisible(true);
                getUILabel2().setVisible(true);
                getItemRef().setEditable(true);
                getItemRef().setEnabled(true);
                WaItemRefModelForGrade refmodel = new WaItemRefModelForGrade();
                refmodel.setPkOrg(getContext().getPk_org());
                refmodel.setPk_org(getContext().getPk_org());
                getItemRef().setRefModel(refmodel);
                getItemRef().setButtonFireEvent(true);
            }
        }

    }
    
}