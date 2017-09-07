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
    private int datatype = 0; // 函数需要返回的数据类型，数值型
    private String itemKey;
    private UIRefPane itemref = null;
    public static final String sdje = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0206")/*@res "时点发放金额"*/;
    public static final String yffje = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0207")/*@res "原发放金额"*/;
    public static final String xffje = ResHelper.getString("6013salaryctymgt","06013salaryctymgt0208")/*@res "现发放金额"*/;

    private UIList uiList= null;
    /**
     * WaParaAdjPanel 构造子注解。
     */
    public WaBaParaAdjPanel(){
        super();
        initialize();
    }

    /**
     * WaParaAdjPanel 构造子注解。
     *
     * @param p0
     *            java.awt.LayoutManager
     */
    public WaBaParaAdjPanel(java.awt.LayoutManager p0){
        super(p0);
    }

    /**
     * WaParaAdjPanel 构造子注解。
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
     * WaParaAdjPanel 构造子注解。
     *
     * @param p0
     *            boolean
     */
    public WaBaParaAdjPanel(boolean p0){
        super(p0);
    }

    /**
     * /** * 检验参数的合法性 * @param 参数说明 * @return 返回值 * @exception 异常描述 * @see
     * 需要参见的其它内容 * @since 从类的那一个版本，此方法被添加进来。（可选） *
     *
     * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选） *-/
     *
     * @return java.lang.String
     */
    public void checkPara(int dataType) throws java.lang.Exception{
        Object obj =  getcboRef().getSelectedValue();
        
        //itemKey is null  不知如何产生的
        if(StringUtils.isBlank(itemKey)){
        	itemKey = "itemkey";
        	//throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0247"))/*@res"请先选择对应的公共薪资项目！*/;
        }
        
        if (obj == null) {
            throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0209")/*@res "请选择取值方式"*/);
        } else {
            String valueTypeName = getcboRef().getSelectedValue().toString();
            if (!valueTypeName.equals(sdje)) {
                // 原发放金额 与 现发放金额 必须选择取值项目
                Object itemid = getItemRef().getRefPK();
                if (itemid == null) {
                    throw new BusinessException(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0210")/*@res "请选择取值项目"*/);
                }
            }

        }
    }

    /**
     * 返回 cmbClass 特性值。
     *
     * @return nc.ui.pub.beans.UIComboBox
     */
    /* 警告：此方法将重新生成。 */
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
     * /** * 描述函数的功能、用途、对属性的更改，以及函数执行前后对象的状态。 * @param 参数说明 * @return 返回值 * @exception
     * 异常描述 * @see 需要参见的其它内容 * @since 从类的那一个版本，此方法被添加进来。（可选） *
     *
     * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选） *-/
     *
     */
    public void clearDis(){

    }

    /**
     * 返回 UIComboBox1 特性值。
     *
     * @return nc.ui.pub.beans.UIComboBox
     */
    /* 警告：此方法将重新生成。 */
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
     * 返回 lblType 特性值。
     *
     * @return nc.ui.pub.beans.UILabel
     */
    private nc.ui.pub.beans.UILabel getlblType(){
        if (ivjlblType == null) {
            ivjlblType = new nc.ui.pub.beans.UILabel();
            ivjlblType.setName("lblType");
            ivjlblType.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0211")/*@res "薪资变动取值方式"*/);
            ivjlblType.setBounds(0, 14, 102, 22);
        }
        return ivjlblType;
    }

    /**
     * 返回 UILabel2 特性值。
     */
    private nc.ui.pub.beans.UILabel getUILabel2(){
        if (ivjUILabel2 == null) {
            ivjUILabel2 = new nc.ui.pub.beans.UILabel();
            ivjUILabel2.setName("UILabel2");
            ivjUILabel2.setText(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0212")/*@res "取值薪资项目"*/);
            ivjUILabel2.setBounds(10, 100, 80, 22);
            ivjUILabel2.setVisible(false);
        }
        return ivjUILabel2;
    }

    /**
     * 第一个参数是指本项目， 另外一个是指取数类型
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
     * 初始化类。
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
     * /** * 更换函数时刷新显示 * @param 参数说明 * @return 返回值 * @exception 异常描述 * @see
     * 需要参见的其它内容 * @since 从类的那一个版本，此方法被添加进来。（可选） *
     *
     * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选） *-/
     *
     * @param paras
     *            nc.vo.wa.func.FunctableItemVO[]
     */
    public void updateDis(int index){

    }

    /**
     * /** * 更换函数时刷新显示 * @param 参数说明 * @return 返回值 * @exception 异常描述 * @see
     * 需要参见的其它内容 * @since 从类的那一个版本，此方法被添加进来。（可选） *
     *
     * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选） *-/
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