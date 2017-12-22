package nc.bs.hrss.pub.tool;

import java.io.Serializable;
import java.util.List;

import nc.bs.hrss.pub.PageModel;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hrss.pub.admin.IConfigurationService;
import nc.itf.hrss.pub.admin.IHROrgProvider;
import nc.uap.lfw.core.AppSession;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.WebSession;
import nc.uap.lfw.core.base.ExtAttribute;
import nc.uap.lfw.core.cache.LfwCacheManager;
import nc.uap.lfw.login.vo.LfwSessionBean;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.StringUtils;

public class SessionUtil {
	public SessionUtil() {
	}

	public static final boolean isTrmer() {
		return getSessionBean().isTrmer();
	}

	public static void setIsTrmer(boolean bool) {
		getSessionBean().setTrmer(bool);
	}

	public static final SessionBean getSessionBean() {
		SessionBean bean = (SessionBean) LfwCacheManager.getSessionCache().get("HRSS.SessionBean");

		boolean designMode = LfwRuntimeEnvironment.getMode().equals("design");
		if ((null == bean) && (designMode))
			return new SessionBean();
		return bean;
	}

	public static final SessionBean getRMWebSessionBean() {
		SessionBean bean = (SessionBean) LfwCacheManager.getSessionCache().get("RMWeb.SessionBean");

		boolean designMode = LfwRuntimeEnvironment.getMode().equals("design");
		if ((null == bean) && (designMode))
			return new SessionBean();
		return bean;
	}

	public static final SessionBean getRMWebUnLoginSessionBean() {
		SessionBean bean = (SessionBean) LfwCacheManager.getSessionCache().get("RMWeb.UNLOGIN.SessionBean");

		if (null == bean) {
			bean = new SessionBean();
			setRMWebUnLoginSessionBean(bean);
		}

		return bean;
	}

	public static final void setSessionBean(SessionBean bean) {
		if (null == bean) {
			LfwCacheManager.getSessionCache().remove("HRSS.SessionBean");
			return;
		}
		LfwCacheManager.getSessionCache().put("HRSS.SessionBean", bean);
	}

	public static final void setRMWebSessionBean(SessionBean bean) {
		if (null == bean) {
			LfwCacheManager.getSessionCache().remove("RMWeb.SessionBean");
			return;
		}
		LfwCacheManager.getSessionCache().put("RMWeb.SessionBean", bean);
	}

	public static final void setRMWebUnLoginSessionBean(SessionBean bean) {
		if (null == bean) {
			LfwCacheManager.getSessionCache().remove("RMWeb.UNLOGIN.SessionBean");
			return;
		}
		LfwCacheManager.getSessionCache().put("RMWeb.UNLOGIN.SessionBean", bean);
	}

	public static final WebSession getWebSession() {
		return LfwRuntimeEnvironment.getWebContext().getWebSession();
	}

	public static final WebSession getParentWebSession() {
		if (!StringUtils.isEmpty(LfwRuntimeEnvironment.getWebContext().getParentPageUniqueId()))
			return LfwRuntimeEnvironment.getWebContext().getParentSession();
		return null;
	}

	public static final AppSession getAppSession() {
		return LfwRuntimeEnvironment.getWebContext().getAppSession();
	}

	public static final String getAppID() {
		return (String) getAppSession().getAttribute("appId");
	}

	public static final Serializable getAttribute(String key) {
		ExtAttribute attr = getSessionBean().getExtendAttribute(key);
		if (null == attr)
			return null;
		return attr.getValue();
	}

	public static final void setAttribute(String key, Serializable value) {
		ExtAttribute attr = getSessionBean().getExtendAttribute(key);
		if (null == attr) {
			attr = new ExtAttribute();
			attr.setKey(key);
			getSessionBean().addAttribute(attr);
		}
		attr.setValue(value);
	}

	public static final String getDataSourceName() {
		return LfwRuntimeEnvironment.getDatasource();
	}

	public static final LoginContext getLoginContext() {
		SessionBean bean = getSessionBean();
		LoginContext context = null;
		if (bean != null) {
			context = getSessionBean().getContext();
		}
		if (context == null) {
			context = new LoginContext();
			LfwSessionBean sesionBean = LfwRuntimeEnvironment.getLfwSessionBean();
			context.setPk_loginUser(sesionBean.getPk_user());
			context.setPk_group(sesionBean.getPk_unit());
			context.setPk_org(sesionBean.getUser_org());
			context.setNodeType(NODE_TYPE.ORG_NODE);
		}
		return context;
	}

	public static final String getPk_mng_group() {
		return getSessionBean().getPk_mng_group();
	}

	public static final String getPk_mng_org() {
		return getSessionBean().getPk_mng_org();
	}

	public static final String getPk_mng_dept() {
		return getSessionBean().getPk_mng_dept();
	}

	public static final boolean isIncludeSubDept() {
		return getSessionBean().isIncludeSubDept();
	}

	public static final HRDeptVO getMngDept() {
		String pkMngDept = getPk_mng_dept();
		HRDeptVO[] depts = getHRDeptVO();
		if ((depts == null) || (depts.length == 0)) {
			return null;
		}
		for (HRDeptVO dept : depts) {
			if (dept.getPk_dept().equals(pkMngDept))
				return dept;
		}
		return null;
	}

	public static final String getMng_dept_code() {
		return getSessionBean().getMng_dept_code();
	}

	public static final String getPk_org() {
		return getSessionBean().getPk_org();
	}

	public static final HRDeptVO[] getHRDeptVO() {
		return getSessionBean().getMngDeptVOs();
	}

	public static final String getPk_user() {
		SessionBean sessionBean = getSessionBean();
		if (sessionBean == null) {
			return LfwRuntimeEnvironment.getLfwSessionBean().getPk_user();
		}
		return sessionBean.getUserVO().getCuserid();
	}

	public static final String getPk_psndoc() {
		PsndocVO psndocVO = getSessionBean().getPsndocVO();
		if (psndocVO == null) {
			return null;
		}
		return psndocVO.getPk_psndoc();
	}

	public static final String getPk_psnjob() {
		PsndocVO psndocVO = getSessionBean().getPsndocVO();
		if (psndocVO == null) {
			return null;
		}
		PsnJobVO psnjobVO = psndocVO.getPsnJobVO();
		if (psnjobVO == null) {
			return null;
		}
		return psnjobVO.getPk_psnjob();
	}

	public static final PsndocVO getPsndocVO() {
		return getSessionBean().getPsndocVO();
	}

	public static final List<PsnJobVO> getPsnjobVOs() {
		return getSessionBean().getPsnjobVOs();
	}

	public static final String getDefaultDeptFromSession() throws BusinessException {
		return getSessionBean().getPk_mng_dept();
	}

	public static final void setDefaultDeptToSession(String pk_mngDep) throws BusinessException {
		SessionBean bean = getSessionBean();
		bean.setPk_mng_dept(pk_mngDep);
	}

	public static final void setCurrentDept(String pk_mngDept, boolean includeSub) {
		if (!StringUtils.isEmpty(pk_mngDept)) {
			SessionBean session = getSessionBean();

			session.setPk_mng_dept(pk_mngDept);
			try {
				IConfigurationService service = (IConfigurationService) ServiceLocator.lookup(IConfigurationService.class);

				IPsndocQryService psnService = (IPsndocQryService) ServiceLocator.lookup(IPsndocQryService.class);
				String sqlPart =
						psnService.getDeptPsnCondition(pk_mngDept, getPk_psndoc(), service.canViewMaster() == null ? false : service.canViewMaster().booleanValue(), service.canViewEachOther() == null ? false : service.canViewEachOther().booleanValue());

				session.setPsnScopeSqlPart(sqlPart);
			} catch (HrssException e) {
			} catch (BusinessException e) {
			}

			HRDeptVO[] hrDeptVOs = session.getMngDeptVOs();
			if ((hrDeptVOs != null) && (hrDeptVOs.length > 0)) {
				for (int i = 0; i < hrDeptVOs.length; i++) {
					HRDeptVO vo = hrDeptVOs[i];
					if ((vo != null) && (!StringUtils.isEmpty(vo.getPk_dept())) && (vo.getPk_dept().equals(pk_mngDept))) {
						session.setPk_mng_org(vo.getPk_org());

						session.setPk_mng_group(vo.getPk_group());

						session.setIncludeSubDept(includeSub);
						break;
					}
				}
			} else {
				session.setPk_mng_org(null);
				session.setPk_mng_group(null);
			}
		}
	}

	public static final String getPk_group() {
		SessionBean sessionBean = getSessionBean();
		if (sessionBean == null) {
			return LfwRuntimeEnvironment.getLfwSessionBean().getPk_unit();
		}
		PsndocVO vo = sessionBean.getPsndocVO();
		if (null != vo)
			return getSessionBean().getPsndocVO().getPk_group();
		return null;
	}

	public static final String getAdminGroupPK() {
		return LfwRuntimeEnvironment.getLfwSessionBean().getPk_unit();
	}

	public static final Class getCurrentPageModelType() {
		WebSession ses = getCurrentHrssWebSession();
		if (null != ses)
			return (Class) ses.getAttribute("HRSS.PAGE.modelClass");
		return null;
	}

	public static final Class getParentPageModelType() {
		WebSession ps = getParentWebSession();
		if (null != ps) {
			return (Class) getParentWebSession().getAttribute("HRSS.PAGE.modelClass");
		}
		return null;
	}

	public static final boolean isMngFunc() {
		String funCode = getCurrentFunCode();
		return isMngFunc(funCode);
	}

	public static final boolean isMngFunc(String funCode) {
		if ((null != funCode) && (funCode.startsWith("E204") && (funCode != "E20400911")))
			return true;
		return false;
	}

	public static String getCurrentFunCode() {
		WebSession ses = getCurrentHrssWebSession();
		if (null != ses)
			return (String) ses.getAttribute("HRSS.PAGE.FunCode");
		return null;
	}

	private static WebSession getCurrentHrssWebSession() {
		if (PageModel.isWebSessionBelongToHrss(getWebSession()))
			return getWebSession();
		WebSession ps = getParentWebSession();
		if ((null != ps) && (PageModel.isWebSessionBelongToHrss(ps)))
			return ps;
		return null;
	}

	public static String getParentFunCode() {
		Class clazz = getParentPageModelType();
		if (null == clazz)
			return null;
		if (PageModel.class.isAssignableFrom(clazz))
			return (String) getParentWebSession().getAttribute("HRSS.PAGE.FunCode");
		return null;
	}

	public static String getHROrg() {
		return getHROrg(getCurrentFunCode(), isMngFunc());
	}

	public static String getHROrg(String funCode, boolean byDept) {
		String org = null;
		try {
			IHROrgProvider provider =
					((IConfigurationService) ServiceLocator.lookup(IConfigurationService.class)).getHROrgProvider(funCode);

			if (byDept) {
				HRDeptVO hrDeptVO = getMngDept();
				org = provider.getDeptHROrg(hrDeptVO);
			} else {
				org = provider.getPsnHROrg(getSessionBean().getPsnjobVO());

			}

		} catch (BusinessException e) {

			new HrssException(e).deal();
		} catch (HrssException e) {
			e.deal();
		}
		return org;
	}

	public static String getThemeID() {
		return LfwRuntimeEnvironment.getThemeId();
	}

	public static String getThemePath() {
		return "/lfw/frame/device_pc/themes/" + getThemeID() + "/ext/hrss/";
	}
}