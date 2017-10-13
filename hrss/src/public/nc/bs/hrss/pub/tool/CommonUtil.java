package nc.bs.hrss.pub.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;

import nc.bs.hrss.pub.DialogSize;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.Logger;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.exception.HrssException;
import nc.hr.utils.ResHelper;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.mail.MailSender;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.IColumn;
import nc.md.model.MetaDataException;
import nc.md.model.impl.BusinessEntity;
import nc.md.model.type.impl.CollectionType;
import nc.md.model.type.impl.RefType;
import nc.message.config.MailConfigAccessor;
import nc.newinstall.util.StringUtil;
import nc.uap.cpb.org.exception.CpbBusinessException;
import nc.uap.cpb.org.itf.ICpAppsNodeQry;
import nc.uap.cpb.org.vos.CpAppsNodeVO;
import nc.uap.ctrl.tpl.qry.SimpleQueryWidgetProvider;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.AppInteractionUtil.TbsDialogResult;
import nc.uap.lfw.core.ContextResourceUtil;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.cache.ILfwCache;
import nc.uap.lfw.core.cache.LfwCacheManager;
import nc.uap.lfw.core.comp.WebComponent;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.OpenProperties;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.exception.LfwRuntimeException;
import nc.uap.lfw.core.exception.LfwValidateException;
import nc.uap.lfw.core.model.plug.TranslatedRow;
import nc.uap.lfw.core.model.plug.TranslatedRows;
import nc.uap.lfw.core.page.LfwView;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.ml.LanguageVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.ml.UserLangCodeFindHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import uap.web.bd.cache.BDRbacCacheHelper;
import uap.web.bd.cache.SimpleCpNodeVO;
import uap.web.bd.pub.AppUtil;

public class CommonUtil {

	public static final String CACHE_ID_HRSSNODEMAP = "hrss_cache_hrssnodemap";
	// fileMap为点击附件连接时,能够在页面直接打开的文件类型的集合,07版office有问题,故注销掉
	private static HashMap<String, String> fileMap = new HashMap<String, String>();

	static {
		fileMap.put("doc", "application/msword");
		fileMap.put("docx", "application/msword");
		fileMap.put("jpg", "image/jpeg");
		fileMap.put("jpeg", "image/jpeg");
		fileMap.put("pdf", "application/pdf");
		fileMap.put("xls", "application/vnd.ms-excel");
		fileMap.put("png", "image/png");
		fileMap.put("bmp", "image/bmp");
		fileMap.put("gif", "image/gif");
		fileMap.put("png", "image/png");
		fileMap.put("html", "text/html");
		fileMap.put("htm", "text/html");
		fileMap.put("xml", "text/xml");
		// fileMap.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	/**
	 * 获取HRSS专用缓存
	 * 
	 * @return
	 * @author haoy 2012-4-26
	 */
	public static ILfwCache getCache() {
		return LfwCacheManager.getStrongCache(HrssConsts.CACHE_ID, null);
	}

	/**
	 * 获取HRSS缓存的项目内容
	 * 
	 * @param key
	 * @return
	 * @author haoy 2012-4-26
	 */
	public static Object getCacheValue(String key) {
		return getCache().get(key);
	}

	/**
	 * 设置HRSS缓存中某项目的内容
	 * 
	 * @param key
	 * @param value
	 * @author haoy 2012-4-26
	 */
	public static void setCacheValue(String key, Object value) {
		getCache().put(key, value);
	}

	/**
	 * 从pfconfig.xml中获得审批配置信息放入HRSS专用缓存中存放
	 * 
	 * @param session
	 * @throws IOException
	 */
	public static HashMap<String, CpAppsNodeVO> getCacheHRSSNodeMap() {
		@SuppressWarnings("unchecked")
		HashMap<String, CpAppsNodeVO> hrssNodeMap = (HashMap<String, CpAppsNodeVO>) CommonUtil.getCacheValue(CACHE_ID_HRSSNODEMAP);

		if (hrssNodeMap == null) {
			hrssNodeMap = new HashMap<String, CpAppsNodeVO>();
			CpAppsNodeVO[] hrssNodes = null;
			try {

//				hrssNodes = ServiceLocator.lookup(ICpAppsNodeQry.class).getAppsNodeVos("id like 'E202%' or id like 'E204%'");
				hrssNodes = ServiceLocator.lookup(ICpAppsNodeQry.class).getAppsNodeVos("id like 'E202%' or id like 'E204%' or id like 'E6013%'");
			} catch (CpbBusinessException e) {
				new HrssException(e).log();
			} catch (HrssException e) {
				e.log();
			}
			if (ArrayUtils.isEmpty(hrssNodes)) {
				CommonUtil.setCacheValue(CACHE_ID_HRSSNODEMAP, null);
				return null;
			}
			for (CpAppsNodeVO nodeVO : hrssNodes) {
				hrssNodeMap.put(nodeVO.getId(), nodeVO);
			}
			CommonUtil.setCacheValue(CACHE_ID_HRSSNODEMAP, hrssNodeMap);
		}
		return hrssNodeMap;
	}

	/**
	 * 根据功能节点编码,判断当前登录用户是否有功能权限
	 * 
	 * @param rescode
	 * @return
	 */
	public static boolean checkLoginUserFuncPermission(String rescode) {
		boolean permission = false;

		Map<String, SimpleCpNodeVO> map = BDRbacCacheHelper.getRBACExtend().getAllFuncResByUserid(SessionUtil.getPk_user());

		if (map != null) {
			Iterator<Entry<String, SimpleCpNodeVO>> it = map.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, SimpleCpNodeVO> entry = it.next();
				SimpleCpNodeVO node = entry.getValue();
				if (node != null && (node.getPk_parent() == null || "~".equals(node.getPk_parent())) && rescode.equals(node.getId())) {

					permission = true;
					break;
				}
			}
		}
		return permission;
	}

	/**
	 * 显示消息提示框
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showMessageDialog(String message) {
		showMessageDialog(ResHelper.getString("c_pub-res", "0c_pub-res0166")/*
																			* @ res "提示信息"
																			*/, message);
	}

	public static void showMessageDialog(String title, String message) {
		AppInteractionUtil.showMessageDialog(message, title, ResHelper.getString("c_pub-res", "0c_pub-res0165")/*
																												* @ res "关闭"
																												*/, false);
	}

	/**
	 * 显示可自动消失的消息提示框
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showShortMessage(String message) {
		AppInteractionUtil.showShortMessage(message);
	}

	/**
	 * 显示错误对话框
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showErrorDialog(String message) {
		showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0167")/* @res "错误"*/, message);
	}

	/**
	 * 显示错误对话框
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showErrorDialog(String title, String message) {
		AppInteractionUtil.showErrorDialog(message, title, ResHelper.getString("c_pub-res", "0c_pub-res0165")/*
																												* @ res "关闭"
																												*/);
	}

	/**
	 * 将错误信息显示到指定的Form/Grid控件
	 * 
	 * @param message
	 * @author qiaoxp 2012-7-13
	 */
	public static void showCompErrorDialog(WebComponent wc, String message) {
		LfwValidateException exception = new LfwValidateException(message);
		if (wc != null) {
			exception.setViewId(wc.getView().getId());
			exception.addComponentId(wc.getId());
		}
		throw exception;
	}

	/**
	 * 显示窗体对话框
	 * 
	 * @param winID
	 * @param title
	 * @param size
	 * @param paramMap
	 * @param type
	 * @author haoy 2012-3-13
	 */
	public static void showWindowDialog(String winID, String title, DialogSize size, Map<String, String> paramMap, String type) {
		String width = (size.getWidth() < 0) ? "100%" : Integer.toString(size.getWidth());

		String height = (size.getHeight() < 0) ? "100%" : Integer.toString(size.getHeight());

		showWindowDialog(winID, title, width, height, paramMap, type);
	}

	/**
	 * 显示窗体对话框，可以设置关闭时是否提示确定关闭和按钮区边线
	 * 
	 * @param winID
	 * @param title
	 * @param size
	 * @param paramMap
	 * @param type
	 * @param isPopclose
	 * @param buttonZone
	 */
	public static void showWindowDialog(String winID, String title, DialogSize size, Map<String, String> paramMap, String type, boolean isPopclose, boolean buttonZone) {
		String width = (size.getWidth() < 0) ? "100%" : Integer.toString(size.getWidth());

		String height = (size.getHeight() < 0) ? "100%" : Integer.toString(size.getHeight());

		OpenProperties prop = new OpenProperties(winID, title, width, height, paramMap, type, isPopclose, buttonZone);

		AppLifeCycleContext.current().getViewContext().navgateTo(prop);
	}

	public static void showWindowDialog(String winID, String title, int width, int height, Map<String, String> paramMap, String type) {
		OpenProperties prop = new OpenProperties(winID, title, Integer.toString(width), Integer.toString(height), paramMap, type);

		AppLifeCycleContext.current().getViewContext().navgateTo(prop);
	}

	/**
	 * 显示窗体对话框,带按钮区边线
	 * 
	 * @param winID
	 * @param title
	 * @param width
	 * @param height
	 * @param paramMap
	 * @param type
	 */
	public static void showWindowDialog(String winID, String title, String width, String height, Map<String, String> paramMap, String type) {
		OpenProperties prop = new OpenProperties(winID, title, width, height, paramMap, type);

		AppLifeCycleContext.current().getViewContext().navgateTo(prop);
	}

	/**
	 * 显示窗体对话框,可不显示按钮区边线，可不提示关闭消息
	 * 
	 * @param winID
	 * @param title
	 * @param width
	 * @param height
	 * @param paramMap
	 * @param type
	 * @param isPopclose 是否显示按钮区边线
	 * @param buttonZone 是否提示确认关闭消息
	 */
	public static void showWindowDialog(String winID, String title, String width, String height, Map<String, String> paramMap, String type, boolean isPopclose, boolean buttonZone) {
		OpenProperties prop = new OpenProperties(winID, title, width, height, paramMap, type, isPopclose, buttonZone);

		AppLifeCycleContext.current().getViewContext().navgateTo(prop);
	}

	/**
	 * 显示view对话框
	 * 
	 * @param viewId
	 * @param title
	 * @param size
	 * @author haoy 2012-3-13
	 */
	public static void showViewDialog(String viewId, String title, DialogSize size) {
		showViewDialog(viewId, title, size, false);
	}

	/**
	 * 显示view对话框
	 * 
	 * @param viewId
	 * @param title
	 * @param size
	 * @param showCloseHint 是否在关闭时显示提醒对话框
	 * @author haoy 2012-3-13
	 */
	public static void showViewDialog(String viewId, String title, DialogSize size, boolean showCloseHint) {

		OpenProperties prop =
				new OpenProperties(viewId, title, Integer.toString(size.getWidth()), Integer.toString(size.getHeight()), showCloseHint, false);

		AppLifeCycleContext.current().getWindowContext().popView(prop);
	}

	/**
	 * 显示view对话框\随意组合自己对话框的高度宽度
	 * 
	 * @param viewId
	 * @param title
	 * @param size
	 * @author haoy 2012-3-13
	 */
	public static void showViewDialog(String viewId, String title, int width, int height) {
		OpenProperties prop = new OpenProperties(viewId, title, Integer.toString(width), Integer.toString(height));

		AppLifeCycleContext.current().getWindowContext().popView(prop);
	}

	/**
	 * 显示view对话框，可控制是否显示按钮区域线及关闭时是否提示确定关闭
	 * 
	 * @param viewId
	 * @param title
	 * @param width
	 * @param height
	 */
	public static void showViewDialogNoLine(String viewId, String title, String width, String height, boolean isPopClose, boolean buttonZone) {
		OpenProperties prop = new OpenProperties(viewId, title, width, height, isPopClose, buttonZone);

		AppLifeCycleContext.current().getWindowContext().popView(prop);
	}

	/**
	 * 弹出确认提示框
	 * 
	 * @param title -标题
	 * @param message -提示信息
	 */
	public static boolean showConfirmDialog(String title, String message) {
		return AppInteractionUtil.showConfirmDialog(title, message);
	}

	public static boolean showConfirmDialog(String message) {
		return AppInteractionUtil.showConfirmDialog(ResHelper.getString("c_pub-res", "0c_pub-res0146")/* @res "询问"*/, message);
	}

	/**
	 * 显示 是、否、取消 三按钮对话框
	 * 
	 * @param title
	 * @param message
	 * @return
	 */

	public static TbsDialogResult showChoiceDialog(String title, String message) {
		return AppInteractionUtil.show3ButtonsDialog(title, message, null);
	}

	public static TbsDialogResult showChoiceDialog(String message) {
		return AppInteractionUtil.show3ButtonsDialog(ResHelper.getString("c_pub-res", "0c_pub-res0146")/* @res "询问"*/, message, null);
	}

	/**
	 * 关闭view对话框
	 * 
	 * @param viewId
	 * @author haoy 2012-3-13
	 */
	public static void closeViewDialog(String viewId) {
		AppLifeCycleContext.current().getWindowContext().closeView(viewId);
	}

	/* 包含主实体信息的匹配模式 */
	private static final Pattern PTN_CONDITION_WITH_MAIN =
			Pattern.compile("([^" + IFromWhereSQLConst.SEP_CONDITION + "]*?)" + IFromWhereSQLConst.SEP_CONDITION + "([^" + IFromWhereSQLConst.SEP_META_PATH + "]*?)" + IFromWhereSQLConst.SEP_META_PATH + "([^" + IFromWhereSQLConst.SEP_CONDITION + "]*?)" + IFromWhereSQLConst.SEP_CONDITION + "(\\d)$");
	/* 未包含主实体信息的匹配模式 */
	private static final Pattern PTN_CONDITION = Pattern.compile("(.*?)" + IFromWhereSQLConst.SEP_CONDITION + "(\\d)$");

	/**
	 * 给一个TranslatedRow添加查询条件，这个条件只能通过genFromWhereSQL方法解析
	 * 
	 * @param row
	 * @param metaPath
	 * @param operator
	 * @param value
	 * @return
	 * @author haoy 2012-4-28
	 */

	public static final TranslatedRow addCondition(TranslatedRow row, String[] metaPath, int operator, Object value) {
		if (!ArrayUtils.isEmpty(metaPath)) {
			String path = StringUtils.join(metaPath, IFromWhereSQLConst.SEP_META_PATH);
			path += IFromWhereSQLConst.SEP_CONDITION

			+ Integer.toString(operator);

			row.setValue(path, value);
		}
		return row;
	}

	/**
	 * 通过translatedRow（plugout输出的内容）生成FromWhereSQL对象，通常用于左侧边栏查询
	 * 
	 * @param mainEntity
	 * @param row
	 * @return
	 * @throws HrssException
	 * @author haoy 2012-3-13
	 */
	public static final FromWhereSQL genFromWhereSQL(Class<? extends SuperVO> mainEntity, TranslatedRow row) throws HrssException {
		/* 获取参数给定的主实体对应元数据 */
		IBean mainBean = null;
		if (null != mainEntity) {
			try {
				mainBean = MDBaseQueryFacade.getInstance().getBeanByFullClassName(mainEntity.getName());
			} catch (MetaDataException e1) {
				new HrssException(e1).alert();
			}
		}
		/* 解析条件 */

		String[] keys = row.getKeys();
		Map<String, String> alias = new HashMap<String, String>();
		StringBuffer bufFrom = new StringBuffer();
		StringBuffer bufWhere = new StringBuffer();
		String mainTable = null;
		for (String key : keys) {
			/* 获取实际主实体，key中包含的主实体如和参数主实体不一致，则抛错 */
			IBean bean = null;
			Matcher mWithMain = PTN_CONDITION_WITH_MAIN.matcher(key);
			// pk_psndoc#name@6

			Matcher mNoMain = PTN_CONDITION.matcher(key);
			if (mWithMain.matches()) {
				/* 名称中包含主实体信息 */
				String ns = mWithMain.group(1);
				String name = mWithMain.group(2);
				try {
					bean = MDBaseQueryFacade.getInstance().getBeanByName(ns, name);
				} catch (MetaDataException e) {
					new HrssException(e).alert();
				}
				if (!bean.equals(mainBean)) {
					Logger.error("Can Not Parse With More Than One Main Entity: " + mainBean.getOwnerComponent().getNamespace() + "::" + mainBean.getName() + " vs " + bean.getOwnerComponent().getNamespace() + "::" + bean.getName());

				}

			} else if (mNoMain.matches()) {
				/* 名称中未包含主实体信息 */
				bean = mainBean;
			} else {
				/* 名称不合法 */
				Logger.error("Bad Format Of Query Key : " + key);
				continue;
			}
			if (null == bean) {
				Logger.error("Can Not Locate Bean By Name: " + key);
				continue;
			}
			/* 设置主表 */

			if (null == mainTable) {
				mainTable = bean.getTable().getName();
				alias.put(".", mainTable);
				bufFrom.append(mainTable).append(" ");
			}
			/* 如果value是空，则不添加此条件 */

			if (row.getValue(key) == null || StringUtils.isEmpty(String.valueOf(row.getValue(key)))) {
				continue;
			}
			/* 递归构造 */

			if (mWithMain.matches()) {
				getSQL(mWithMain.group(3) + IFromWhereSQLConst.SEP_CONDITION + mWithMain.group(4), bean, bufFrom, bufWhere, alias, row.getValue(key));

			} else if (mNoMain.matches()) {
				getSQL(key, bean, bufFrom, bufWhere, alias, row.getValue(key));

			}
		}
		Logger.debug(bufFrom);
		Logger.debug(bufWhere);
		if (StringUtils.isEmpty(bufWhere.toString())) {
			bufWhere.append(" 1=1 ");
		}

		FromWhereSQLImpl fws = new FromWhereSQLImpl(bufFrom.toString(), bufWhere.toString());

		fws.setAttrpath_alias_map(alias);
		return fws;
	}

	/**
	 * 递归解析元数据查询条件
	 * 
	 * @param id 当前元数据路径及操作符，形如：属性1#属性2...@操作符
	 * @param parent 当前元数据路径所属的父实体
	 * @param bufFrom from子句
	 * @param bufWhere where子句
	 * @param alias 别名映射表
	 * @param value 条件取值
	 * @throws HrssException
	 * @author haoy 2011-11-23
	 */
	private static void getSQL(String id, IBean parent, StringBuffer bufFrom, StringBuffer bufWhere, Map<String, String> alias, Object value)
			throws HrssException {
		String[] cond = id.split(IFromWhereSQLConst.SEP_CONDITION);
		String meta = cond[0];
		int op = Integer.parseInt(cond[1]);
		if (meta.indexOf(IFromWhereSQLConst.SEP_META_PATH) > 0) {
			/* 仍包含下级 */

			IAttribute attr = parent.getAttributeByName(meta.substring(0, meta.indexOf(IFromWhereSQLConst.SEP_META_PATH)));

			if (null == attr || !(attr.getDataType() instanceof CollectionType || attr.getDataType() instanceof RefType)) {
				/* 如果当前属性没有下级，则记录错误，不做任何处理 */

				Logger.error("Attribute is null or is not Collection Type: " + id);

				return;
			} else {

				IBean self = attr.getAssociation().getEndBean();
				// /* 添加Alias */

				IColumn col = attr.getColumn();
				String subTable = "";
				if (self instanceof BusinessEntity) {
					subTable = ((BusinessEntity) self).getDefaultTableName();
				}

				String parentTable = col.getOwnerTableView().getName();
				// String fkField = col.getName();

				String fkField = ((BusinessEntity) self).getPrimaryKey().getPKColumn().getName();
				String parentPK = col.getName();
				if (!alias.containsKey(subTable)) {
					alias.put(subTable, "T" + alias.size());
					/* 添加From子句 */
					String al = alias.get(subTable);
					String apl = parentTable;
					if (alias.get(parentTable) != null) {
						apl = alias.get(parentTable);
					}
					bufFrom.append(" inner join ").append(subTable).append(" ").append(al).append(" on ").append(apl).append(".").append(parentPK).append("=").append(al).append(".").append(fkField);
				}
				/** 递归到下一层 */
				String cid = id.substring(id.indexOf(IFromWhereSQLConst.SEP_META_PATH) + 1);
				getSQL(cid, self, bufFrom, bufWhere, alias, value);

			}

		} else {
			/* 叶子属性 ，直接添加where条件 */
			IAttribute attr = parent.getAttributeByName(meta);
			String al = alias.get("."); // 主表
			if (null != alias.get(parent.getTable().getName()))
				al = alias.get(parent.getTable().getName());
			if (!StringUtils.isEmpty(bufWhere.toString().trim()))
				bufWhere.append(" and ");
			bufWhere.append(" ").append(al).append(".").append(attr.getColumn().getName()).append(" ").append(IFromWhereSQLConst.OPCHAR[op]).append(" ").append(getValueInSQL(op, value));
		}
	}

	private static String getValueInSQL(int op, Object value) {

		boolean isNumber = (value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof UFDouble);

		String v = value.toString();
		if (isNumber)
			return v;
		if (IFromWhereSQLConst.OP_LIKE == op)

			return "'%" + v + "%'";
		if (IFromWhereSQLConst.OP_LIKE_LEFT == op)

			return "'%" + v + "'";
		if (IFromWhereSQLConst.OP_LIKE_RIGHT == op)

			return "'" + v + "%'";
		if (IFromWhereSQLConst.OP_IN == op) {
			String[] ps = v.split(IFromWhereSQLConst.SEP_CONDITION);

			String[] vs = ps[0].split(",");
			String type = ps[1];
			StringBuffer con = new StringBuffer();
			con.append("(");
			if (IFromWhereSQLConst.SEP_TYPE_INTEGER.equals(type)) {

				con.append(ps[0]);
			} else if (IFromWhereSQLConst.SEP_TYPE_STRING.equals(type)) {

				for (int i = 0; i < vs.length; i++) {
					if (i == vs.length - 1) {
						con.append("'" + vs[i] + "'");
					} else {
						con.append("'" + vs[i] + "',");
					}
				}
			} else if (IFromWhereSQLConst.SEP_TYPE_SUB_QUERY.equals(type)) {

				con.append(ps[0]);
			}
			con.append(")");
			return con.toString();
		}
		return "'" + v + "'";
	}

	public static final <T extends SuperVO> List<T> getSuperVOByTranslatedRows(Class<T> clazz, TranslatedRows rows) {

		ArrayList<T> result = new ArrayList<T>();
		/* TranslatedRows的属性列表 */

		String[] keys = rows.getKeys();
		HashSet<String> kSet = new HashSet<String>();
		for (String k : keys)
			kSet.add(k);
		int len = rows.getValue(keys[0]).size();
		try {
			/* supervo的属性列表 */
			T temp = clazz.newInstance();
			String[] attr = ((SuperVO) temp).getAttributeNames();
			HashSet<String> aSet = new HashSet<String>();
			for (String a : attr)
				aSet.add(a);
			/**/

			for (int i = 0; i < len; i++) {
				T vo = clazz.newInstance();
				for (Iterator<String> it = kSet.iterator(); it.hasNext();) {
					String key = it.next();
					if (aSet.contains(key)) {
						/* 为VO属性赋值 */
						Object value = rows.getValue(key).get(i);
						vo.setAttributeValue(key, value);
					}
				}
				result.add(vo);
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return result;
	}

	public static FromWhereSQL getUAPFromWhereSQL(nc.uap.lfw.core.cmd.base.FromWhereSQL lfwFromWhereSQL) {
		FromWhereSQLImpl fws = new FromWhereSQLImpl();
		fws.setFrom(lfwFromWhereSQL.getFrom());
		fws.setWhere(lfwFromWhereSQL.getWhere());
		if (lfwFromWhereSQL instanceof nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) {
			fws.setAttrpath_alias_map(((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) lfwFromWhereSQL).getAttrpath_alias_map());
		}

		return fws;
	}

	/**
	 * 时间管理模块, 获取左侧查询条件的开始日期和结束日期的默认值.<br/>
	 * 方法一<br/>
	 * 
	 * @return 自然日期所在月的第一天和最后一天
	 */
	public static UFLiteralDate[] getDefaultBeginEndDate() {
		UFLiteralDate d = new UFLiteralDate();
		// 所属月的第一天

		UFLiteralDate beginDate = getFirstDateOfMonth(d);
		// 所属月的最后一天

		UFLiteralDate endDate = getLastDateOfMonth(d);
		return new UFLiteralDate[] { beginDate, endDate };
	}

	/**
	 * 时间管理模块, 获取左侧查询条件的开始日期和结束日期的默认值.<br/>
	 * 方法一<br/>
	 * 
	 * @return 自然日期所在月的第一天和最后一天
	 */
	public static String[] getStringDefaultBeginEndDate() {
		UFLiteralDate d = new UFLiteralDate();
		// 所属月的第一天

		UFLiteralDate beginDate = getFirstDateOfMonth(d);
		// 所属月的最后一天

		UFLiteralDate endDate = getLastDateOfMonth(d);
		return new String[] { beginDate.toString(), endDate.toString() };
	}

	/**
	 * 获取指定日期所属月上的第一天.
	 * 
	 * @param date 当前日期
	 * @return
	 * @author haoy 2011-7-27
	 */
	public static UFLiteralDate getFirstDateOfMonth(UFLiteralDate d) {
		if (d == null) {
			return null;
		}
		return UFLiteralDate.getDate(d.toString().substring(0, 7) + "-01");
	}

	/**
	 * 获取指定日期所属月上的第一天.
	 * 
	 * @param date 当前日期
	 * @return
	 * @author haoy 2011-7-27
	 */
	public static UFLiteralDate getLastDateOfMonth(UFLiteralDate d) {
		if (d == null) {
			return null;
		}
		return UFLiteralDate.getDate(d.toString().substring(0, 7) + "-" + UFLiteralDate.getDaysMonth(d.getYear(), d.getMonth()));
	}

	/**
	 * 获得系统的默认年度
	 * 
	 * @return 系统年度
	 */
	public static int getSystemYear() {
		return new UFLiteralDate().getYear();
	}

	/**
	 * 通过缓存获取OrgVO。 执行参照查询时，很多情况下需要根据当前委托组织获取其所属集团，如果每次查询数据库效率会受影响， 所以提供这个通过缓存获取的方式
	 * 
	 * @param pk_org
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static OrgVO getOrgVOByPK(String pk_org) {
		HashMap<String, OrgVO> orgMap = (HashMap<String, OrgVO>) getCacheValue("cache_org");
		if (null == orgMap) {
			orgMap = new HashMap<String, OrgVO>();
			setCacheValue("cache_org", orgMap);
		}
		OrgVO org = orgMap.get(pk_org);
		if (null == org) {
			try {

				org = (OrgVO) ServiceLocator.lookup(IPersistenceRetrieve.class).retrieveByPk(OrgVO.class, pk_org, null);

				orgMap.put(pk_org, org);
			} catch (BusinessException e) {
				new HrssException(e).deal();
			} catch (HrssException e) {
				e.deal();
			}
		}
		return org;
	}

	/**
	 * 附件功能，只需要当前的数据集即可
	 * 
	 * @param ds
	 */

	public static void Attachment(Dataset ds, Boolean isPower) {
		Row selRow = ds.getSelectedRow();
		Attachment(ds, selRow, isPower);
	}

	/**
	 * 附件功能
	 * 
	 * @param ds
	 */

	public static void Attachment(Dataset ds, Row row, Boolean isPower) {
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute("isPower", isPower);

		if (row == null) {
			throw new LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_rm-res", "0c_rm-res0008")/*
																															 * @
																															 * res
																															 * "请选择记录！"
																															 */);
		}

		String primaryField = DatasetUtil.getPrimaryField(ds).getId();
		String primarykey = row.getString(ds.nameToIndex(primaryField));
		AppUtil.addAppAttr(HrssConsts.HRWEB_PRIMARY_KEY, primarykey);
		CommonUtil.showWindowDialog("HrssFilemanager", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pe-res", "0c_pe-res0023")/*
																																			 * @ res "文档管理"
																																			 */, "750", "450", null, null);

	}

	/**
	 * 获取临时文件的暂存目录
	 * 
	 * @param withClear
	 * @return
	 * @throws IOException
	 */
	public static File getExportDir(boolean withClear) throws IOException {
		String path = getAbsoluteExportPath();
		File dir = new File(path);
		if (!dir.exists())
			FileUtils.forceMkdir(dir);
		else {
			if (withClear) {
				FileUtils.cleanDirectory(dir);
			}
		}

		return dir;
	}

	/**
	 * 获取导出目录的绝对路径
	 * 
	 * @return
	 */
	public static String getAbsoluteExportPath() {
		String path = ContextResourceUtil.getCurrentAppPath() + File.separator + getExportPath().replace('/', File.separatorChar);

		return path;
	}

	/**
	 * 获取导出目录的相对路径
	 * 
	 * @return
	 */
	public static String getExportPath() {
		return "exportfiles/" + SessionUtil.getPk_psnjob();
	}

	/**
	 * 获取当前请求参数，当前的参数可能是如下三种情况之一： 1、ajax请求中，通过proxy.setParameter设置的参数。优先级最高 2、url参数，在pagemodel中解析时，可以直接通过request获取
	 * 3、url参数，在controller中解析时，由于已经不是原来的请求，所以request中已经无法获取原参数，只能用 WebContext.getOriginalParameter获取，这个参数是存储在session中的
	 * 
	 * @param paramKey
	 * @return
	 */
	public static String getParameter(String paramKey) {
		// Proxy param
		String param = AppLifeCycleContext.current().getParameter(paramKey);
		if (null != param)
			return param;
		// Request Param for pagemodel

		HttpServletRequest request = LfwRuntimeEnvironment.getWebContext().getRequest();

		param = request.getParameter(paramKey);
		if (null != param)
			return param;
		// Request Param for controller
		param = LfwRuntimeEnvironment.getWebContext().getOriginalParameter(paramKey);

		return param;
	}

	/**
	 * 用于经理节点部门切换 将左侧查询条件清空 通过拿到左侧查询ds清空操作
	 */
	public static void clearSimpleQuery() {
		LfwView leftwidget = AppLifeCycleContext.current().getWindowContext().getViewContext("pubview_simplequery").getView();

		Dataset ds = leftwidget.getViewModels().getDataset(SimpleQueryWidgetProvider.MAINDS);

		ds.clear();
		Row row = ds.getEmptyRow();
		ds.addRow(row);
		ds.setRowSelectIndex(0);
	}

	/**
	 * 1.自有的附件片段,点超链接打开文件,如果文件格式不兼容,则会出现弹出的window快速关闭的问题 2.在此情况下判断:是否公共片段\是否是查看文件 3.如果自有片段及查看无法直接打开的文件,则将执行在iframe中下载相应文件的操作 4.ViewOrDown只能传递"view"或者是"down"
	 * 
	 * @param filepath
	 * @param ViewOrDown
	 * @param pub
	 */
	public static void fileDownOrView(String filepath, String ViewOrDown, boolean pubView) {
		// 拿到文件的类型,判断文件类型是否支持在线打开,如果type为空则不支持Ie直接打开
		int lastIndex = filepath.lastIndexOf(".");
		String fileType = filepath.substring(lastIndex + 1);
		String type = fileMap.get(fileType);
		// 不直接直接打开\直接查看片段\不是公共附件片段而是自由片段

		if (type == null && "view".equals(ViewOrDown) && !pubView) {
			String url = "/portal/pt/erfile/down?id=" + filepath;
			AppLifeCycleContext.current().getWindowContext().downloadFileInIframe(url);
		} else {
			String execScript = "window.open('/portal/pt/erfile/" + ViewOrDown + "?id=" + filepath + "'" + "," + "'_blank');";
			AppLifeCycleContext.current().getApplicationContext().addExecScript(execScript);
		}

	}

	/**
	 * 拿到附件中能够直接打开的文件类型的map
	 * 
	 * @return
	 */
	public static HashMap<String, String> getFileMap() {
		return fileMap;
	}

	/**************************************************************
	 * 返回指定用户的语种langcode Created on 2012-4-26 16:09:51<br>
	 * 
	 * @param strPk_user
	 * @return String
	 **************************************************************/
	public static String getLangCodeByUser(String strPk_user) {
		String strPk_lang = UserLangCodeFindHelper.getUserLangDefCode(strPk_user);

		LanguageVO currLangVO = null;

		LanguageVO[] enableLangVOs = MultiLangContext.getInstance().getEnableLangVOs();

		if (enableLangVOs != null) {
			for (int i = 0; i < enableLangVOs.length; i++) {
				if (ObjectUtils.equals(enableLangVOs[i].getPk_multilang(), strPk_lang)) {
					currLangVO = enableLangVOs[i];
					break;
				}
			}
		}

		if (currLangVO == null) {
			if (enableLangVOs != null && enableLangVOs.length > 0) {
				currLangVO = enableLangVOs[0];
			}
		}

		String langcode = currLangVO == null ? "simpchn" : currLangVO.getLangcode();

		if (StringUtil.isEmpty(langcode)) {
			return "simpchn";
		}

		return langcode;
	}

	/**
	 * 用途：在新增/修改的保存操作后，在刷新后的数据中重新选中操作行的数据.
	 * 
	 * @param vo
	 * @param fieldId 设置选中行时,使用的标准字段
	 */
	public static void setAppAtrriValueForRowIndex(SuperVO vo, String fieldId) {
		if (vo != null) {
			ApplicationContext appCtx = AppUtil.getCntAppCtx();
			appCtx.addAppAttribute(getAppAttriFieldIdKey(), fieldId);
			appCtx.addAppAttribute(getAppAtrriCntValueKey(), (String) vo.getAttributeValue(fieldId));
		}

	}

	public static void setAppAtrriValueForRowIndex(SuperVO vo) {
		if (vo != null) {
			String fieldId = vo.getPKFieldName();
			setAppAtrriValueForRowIndex(vo, fieldId);
		}
	}

	public static void setAppAtrriValueForRowIndex(AggregatedValueObject aggVO) {
		if (aggVO != null) {
			SuperVO vo = (SuperVO) aggVO.getParentVO();
			String fieldId = vo.getPKFieldName();
			setAppAtrriValueForRowIndex(vo, fieldId);
		}

	}

	/**
	 * 操作行值的关键字
	 * 
	 * @return
	 */
	public static String getAppAtrriCntValueKey() {
		return "App_" + AppUtil.getCntAppCtx().getAppId();
	}

	/**
	 * 取值字段ID的关键字
	 * 
	 * @return
	 */
	public static String getAppAttriFieldIdKey() {
		return "App_" + AppUtil.getCntAppCtx().getAppId() + "_field";
	}

	public static String getAppAttriSelectedIndexKey() {
		return "App_" + AppUtil.getCntAppCtx().getAppId() + "_selectIndex";
	}

	public static int getAppAttriSelectedIndex() {
		Integer selectedIndex = (Integer) AppUtil.getCntAppCtx().getAppAttribute(CommonUtil.getAppAttriSelectedIndexKey());
		AppUtil.getCntAppCtx().addAppAttribute(CommonUtil.getAppAttriSelectedIndexKey(), 0);
		if (selectedIndex == null) {
			return 0;
		}
		return selectedIndex.intValue();
	}

	public static String genRandomID(int len, String availableChars) {
		char[] ac = availableChars.toCharArray();
		char[] pwd = new char[len];
		Random random = new Random();
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = ac[random.nextInt(ac.length)];
		}
		return new String(pwd);
	}

	public static String genRandomID(int len) {
		String availableChars = "123456789";
		String engchar = "abcdefghijklmnpqrstuvwxyz";
		availableChars = availableChars + engchar;
		availableChars = availableChars + engchar.toUpperCase();

		return genRandomID(len, availableChars);
	}

	public static void sendMail(String[] reciever, String subject, String content) throws BusinessException {
		String strHost = MailConfigAccessor.getSMTPServer();
		String strUser = MailConfigAccessor.getUser();
		String strPass = MailConfigAccessor.getPassword();
		String strFromAddr = MailConfigAccessor.getMailFrom();
		boolean bAuth = MailConfigAccessor.isAuthen();
		if (StringUtils.isEmpty(strHost)) {
			throw new BusinessException(ResHelper.getString("6001msgtmp", "16001msgtmp0005"));
		}

		if (StringUtils.isEmpty(strUser)) {
			throw new BusinessException(ResHelper.getString("6001msgtmp", "16001msgtmp0006"));
		}

		MailSender sender = new MailSender(strHost, strUser, strPass, bAuth);
		try {
			sender.sendMail(strFromAddr, reciever, null, subject, content, null);
		} catch (SendFailedException ex) {
			Address[] invalidAddress = ex.getInvalidAddresses();
			Address[] unsentAddress = ex.getValidUnsentAddresses();
			String errorMsg = ResHelper.getString("6001msgtmp", "06001msgtmp0046");

			String failEmailAddress = "";
			if (!ArrayUtils.isEmpty(invalidAddress)) {
				for (Address address : invalidAddress) {
					if (StringUtils.isEmpty(failEmailAddress)) {
						failEmailAddress = address.toString();
					} else {
						failEmailAddress = failEmailAddress + "," + address.toString();
					}
				}
			}

			if (!ArrayUtils.isEmpty(unsentAddress)) {
				for (Address address : unsentAddress) {
					if (StringUtils.isEmpty(failEmailAddress)) {
						failEmailAddress = address.toString();
					} else {
						failEmailAddress = failEmailAddress + "," + address.toString();
					}
				}
			}

			if (StringUtils.isNotEmpty(failEmailAddress)) {
				errorMsg = errorMsg + ResHelper.getString("6001msgtmp", "06001msgtmp0047", new String[] { failEmailAddress });
			}

			throw new BusinessException(errorMsg, ex);
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage(), ex);
		}
	}

	public CommonUtil() {
	}
}
