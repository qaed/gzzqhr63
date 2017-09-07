package nc.vo.wa.wa_ba.item;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hr.wa.IWaClass;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WACLASSTYPE;

public class WaBaLoginVOHelper {

	public static String getParentClassPK(WaBaLoginVO vo){
		return vo.getPk_prnt_class();
	}


	public static String getChildClassPK(WaBaLoginVO vo){
		return vo.getPk_wa_class();
	}

	public static WaClassVO getParentClass(WaBaLoginVO vo){
		String cyear = vo.getCyear();
		String cperiod = vo.getCperiod();
		if(vo.getPeriodVO()!=null){
			cyear = vo.getPeriodVO().getCyear();
			cperiod = vo.getPeriodVO().getCperiod();
		}
		try {
			WaClassVO parentClass = NCLocator.getInstance().lookup(IWaClass.class).queryParentClass(vo.getPk_wa_class(),cyear, cperiod);
			return  parentClass==null?vo:parentClass;
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static WaInludeclassVO[] getChildren(WaBaLoginVO vo){
		String cyear = vo.getCyear();
		String cperiod = vo.getCperiod();
		if(vo.getPeriodVO()!=null){
			cyear = vo.getPeriodVO().getCyear();
			cperiod = vo.getPeriodVO().getCperiod();
		}
		try {
			return  NCLocator.getInstance().lookup(IWaClass.class).queryIncludeClasses(vo.getPk_wa_class(),cyear, cperiod);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}
	public static WaClassVO getFirstChild(WaBaLoginVO vo){
		String cyear = vo.getCyear();
		String cperiod = vo.getCperiod();
		if(vo.getPeriodVO()!=null){
			cyear = vo.getPeriodVO().getCyear();
			cperiod = vo.getPeriodVO().getCperiod();
		}
		try {
			return  NCLocator.getInstance().lookup(IWaClass.class).queryChildClass(vo.getPk_wa_class(),cyear, cperiod,1);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}
	public static WaInludeclassVO[] getChildren(WaClassVO vo){
		String cyear = vo.getCyear();
		String cperiod = vo.getCperiod();

		try {
			return  NCLocator.getInstance().lookup(IWaClass.class).queryIncludeClasses(vo.getPk_wa_class(),cyear, cperiod);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return null;
		}
	}
	/**
	 * 返回当前薪资方案类型
	 * @return
	 */
	public static WACLASSTYPE getClassType(WaClassVO vo) {
		if (vo == null || vo.getCyear() == null || vo.getCperiod() == null) {
			return null;
		}else if(vo.getPk_group()!=null&&vo.getPk_group().equals(vo.getPk_org())){
			return WACLASSTYPE.GROUPCLASS;
		}else if(vo.getCollectflag()!=null&&vo.getCollectflag().booleanValue()){
			return WACLASSTYPE.COLLECTCLASS;
		}else if(!vo.getShowflag().booleanValue()){
			return WACLASSTYPE.CHILDCLASS;
		}

		if (vo instanceof WaBaLoginVO
				&& ((WaBaLoginVO) vo).getPeriodVO() != null
				&& (!((WaBaLoginVO) vo).getPeriodVO().getCyear()
						.equals(vo.getCyear()) || !((WaBaLoginVO) vo)
						.getPeriodVO().getCperiod().equals(vo.getCperiod()))) {
			/*		WaInludeclassVO[] inludeclassvos = null;
			inludeclassvos = getChildren((WaBaLoginVO)vo);
			if(ArrayUtils.isEmpty(inludeclassvos)){
				return WACLASSTYPE.NORMALCLASS;
			}
			for(WaInludeclassVO inludeclassvo:inludeclassvos){
				if(inludeclassvo.getBatch()>100){
					return WACLASSTYPE.LEAVECLASS;
				}
			}
			return WACLASSTYPE.PARENTCLASS;*/
			PeriodStateVO periodStateVO = ((WaBaLoginVO) vo).getPeriodVO();
			return WACLASSTYPE.valueOf(periodStateVO.getClasstype().intValue());
		}
		if (!vo.getMutipleflag().booleanValue()) {
			return WACLASSTYPE.NORMALCLASS;
		} else if (vo.getLeaveflag().booleanValue()) {
			return WACLASSTYPE.LEAVECLASS;
		}
		return WACLASSTYPE.PARENTCLASS;
	}

	/**
	 * 返回当前方案是否是汇总方案
	 * @return
	 */
	public static boolean isMultiClass(WaClassVO vo){
		return WACLASSTYPE.PARENTCLASS.equals(getClassType(vo))||WACLASSTYPE.LEAVECLASS.equals(getClassType(vo));
	}

	/**
	 * 返回当前方案是否是多次发薪或离职结薪的子方案
	 * @return
	 */
	public static boolean isSubClass(WaClassVO vo){
		return     WACLASSTYPE.CHILDCLASS.equals(getClassType(vo));
	}

	/**
	 * 返回当前方案是否是多次发薪的父方案
	 * @return
	 */
	public static boolean isParentClass(WaClassVO vo){
		return  WACLASSTYPE.PARENTCLASS.equals(getClassType(vo));
	}

	/**
	 * 返回当前方案是否离职结薪方案
	 * @return
	 */
	public static boolean isLeaveClass(WaClassVO vo){
		return  WACLASSTYPE.LEAVECLASS.equals(getClassType(vo));
	}
	/**
	 * 返回当前方案是否集团方案
	 * @return
	 */
	public static boolean isGroupClass(WaClassVO vo){
		return  WACLASSTYPE.GROUPCLASS.equals(getClassType(vo));
	}
	/**
	 * 返回当前方案是否正常结薪方案
	 * @return
	 */
	public static boolean isNormalClass(WaClassVO vo){
		return  WACLASSTYPE.NORMALCLASS.equals(getClassType(vo));
	}
	/**
	 * 返回当前方案是否汇总方案
	 * @return
	 */
	public static boolean isCollectClass(WaClassVO vo){
		return  WACLASSTYPE.COLLECTCLASS.equals(getClassType(vo));
	}
}
