package nc.vo.wa.wa_ba.item;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.wa.IWaClass;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.StringUtils;


/**
 *
 * @author: zhangg
 * @date: 2009-11-25 下午02:45:04
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaBaLoginVO extends WaClassVO {

    private static final long serialVersionUID = 1L;

    private WaState state;// 记录当前类别的状态
    private nc.vo.pub.lang.UFBoolean isintaxgroup;//
    private PeriodStateVO periodVO;
    private PeriodStateVO parentperiodVO;

	private String deptpower, psnclpower;// 权限
    private String reyear ;
    private String reperiod;
    private String pk_prnt_class;//记录当前选中的父方案，如果为null,表示当前方案为非多次发放
    private WaState prntState; //记录父方案的状态
    private String classmode;//记录是普通方案还是合并计税方案
    private Integer payslipType;//记录薪资条类型
    
    private    ItemsVO[] classItemVOs = null;

    public ItemsVO[] getClassItemVOs() {
        return classItemVOs;
    }

    public void setClassItemVOs(ItemsVO[] classItemVOs) {
        this.classItemVOs = classItemVOs;
    }


    public WaState getState() {
        return state;
    }


    public void setStates(WaState states) {
        this.state = states;
    }
    
    @Override
    public String getMultilangName() {
    	return super.getName();
    }


    public nc.vo.pub.lang.UFBoolean getIsintaxgroup() {
        return isintaxgroup;
    }


    public void setIsintaxgroup(nc.vo.pub.lang.UFBoolean isintaxgroup) {
        this.isintaxgroup = isintaxgroup;
    }

    /**
     * 得到用户设定的薪资期间
     * @author zhangg on 2009-12-1
     * @return the periodVO
     */
    public PeriodStateVO getPeriodVO() {
        return periodVO;
    }

    public void setPeriodVO(PeriodStateVO periodVO) {
        this.periodVO = periodVO;
    }

    public String getDeptpower() throws BusinessException {
        return deptpower;
    }

    public void setDeptpower(String deptpower) {
        this.deptpower = deptpower;
    }

    public String getPsnclpower() throws BusinessException {
        return psnclpower;
    }

    public void setPsnclpower(String psnclpower) {
        this.psnclpower = psnclpower;
    }

    public void setReyear(String reyear) {
        this.reyear = reyear;
    }

    public String getReyear() {
        return reyear;
    }

    public void setReperiod(String reperiod) {
        this.reperiod = reperiod;
    }

    public String getReperiod() {
        return reperiod;
    }

    public void setPk_prnt_class(String pk_prnt_class) {
        this.pk_prnt_class = pk_prnt_class;
    }

    public String getPk_prnt_class() {
        //得到父方案PK
        if(StringUtils.isEmpty(pk_prnt_class)){
            if(!StringUtils.isEmpty(getPk_wa_class())){
                //后台数据库查询  
                String cyear = getCyear();
                if(getPeriodVO()!=null){
                    cyear = getPeriodVO().getCyear();
                }
                String cperiod = getCperiod();
                if(getPeriodVO()!=null){
                    cperiod = getPeriodVO().getCperiod();
                }
                try {
                    pk_prnt_class = NCLocator.getInstance().lookup(IWaClass.class).queryParentClasspk(getPk_wa_class(),cyear, cperiod);
                } catch (BusinessException e) {
                    Logger.error(e.getMessage(), e);
                    pk_prnt_class=null;
                }
                
            }
        }
        return pk_prnt_class;
    }

    public void setPrntState(WaState prntState) {
        this.prntState = prntState;
    }

    public WaState getPrntState() {
        return prntState;
    }
  
    public String getClassmode() {
		return classmode;
	}

	public Integer getPayslipType() {
		return payslipType;
	}

	public void setPayslipType(Integer payslipType) {
		this.payslipType = payslipType;
	}

	public void setClassmode(String classmode) {
		this.classmode = classmode;
	}
    public PeriodStateVO getParentperiodVO() {
		return parentperiodVO;
	}

	public void setParentperiodVO(PeriodStateVO parentperiodVO) {
		this.parentperiodVO = parentperiodVO;
	}

	public WaBaLoginContext  toWaLoginContext(){
		WaBaLoginContext context = new WaBaLoginContext();
        context.setPk_group(this.getPk_group());
        context.setPk_org(this.getPk_org());
        context.setWaLoginVO(this);
        
        return context;
        
    }
    
    /**
     * 返回当前方案是否是多次发薪或离职结薪的子方案
     * @return
     * @throws BusinessException 
     */
    public boolean hasLeaveClasses() throws BusinessException 
    {
//    	return NCLocator.getInstance().lookup(IPayLeaveQueryService.class).hasLeaveClasses(this);
    	return false;
    }
}
