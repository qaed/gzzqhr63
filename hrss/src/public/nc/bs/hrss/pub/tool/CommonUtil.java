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
	// fileMapΪ�����������ʱ,�ܹ���ҳ��ֱ�Ӵ򿪵��ļ����͵ļ���,07��office������,��ע����
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
	 * ��ȡHRSSר�û���
	 * 
	 * @return
	 * @author haoy 2012-4-26
	 */
	public static ILfwCache getCache() {
		return LfwCacheManager.getStrongCache(HrssConsts.CACHE_ID, null);
	}

	/**
	 * ��ȡHRSS�������Ŀ����
	 * 
	 * @param key
	 * @return
	 * @author haoy 2012-4-26
	 */
	public static Object getCacheValue(String key) {
		return getCache().get(key);
	}

	/**
	 * ����HRSS������ĳ��Ŀ������
	 * 
	 * @param key
	 * @param value
	 * @author haoy 2012-4-26
	 */
	public static void setCacheValue(String key, Object value) {
		getCache().put(key, value);
	}

	/**
	 * ��pfconfig.xml�л������������Ϣ����HRSSר�û����д��
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
	 * ���ݹ��ܽڵ����,�жϵ�ǰ��¼�û��Ƿ��й���Ȩ��
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
	 * ��ʾ��Ϣ��ʾ��
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showMessageDialog(String message) {
		showMessageDialog(ResHelper.getString("c_pub-res", "0c_pub-res0166")/*
																			* @ res "��ʾ��Ϣ"
																			*/, message);
	}

	public static void showMessageDialog(String title, String message) {
		AppInteractionUtil.showMessageDialog(message, title, ResHelper.getString("c_pub-res", "0c_pub-res0165")/*
																												* @ res "�ر�"
																												*/, false);
	}

	/**
	 * ��ʾ���Զ���ʧ����Ϣ��ʾ��
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showShortMessage(String message) {
		AppInteractionUtil.showShortMessage(message);
	}

	/**
	 * ��ʾ����Ի���
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showErrorDialog(String message) {
		showErrorDialog(ResHelper.getString("c_pub-res", "0c_pub-res0167")/* @res "����"*/, message);
	}

	/**
	 * ��ʾ����Ի���
	 * 
	 * @param message
	 * @author haoy 2012-3-13
	 */
	public static void showErrorDialog(String title, String message) {
		AppInteractionUtil.showErrorDialog(message, title, ResHelper.getString("c_pub-res", "0c_pub-res0165")/*
																												* @ res "�ر�"
																												*/);
	}

	/**
	 * ��������Ϣ��ʾ��ָ����Form/Grid�ؼ�
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
	 * ��ʾ����Ի���
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
	 * ��ʾ����Ի��򣬿������ùر�ʱ�Ƿ���ʾȷ���رպͰ�ť������
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
	 * ��ʾ����Ի���,����ť������
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
	 * ��ʾ����Ի���,�ɲ���ʾ��ť�����ߣ��ɲ���ʾ�ر���Ϣ
	 * 
	 * @param winID
	 * @param title
	 * @param width
	 * @param height
	 * @param paramMap
	 * @param type
	 * @param isPopclose �Ƿ���ʾ��ť������
	 * @param buttonZone �Ƿ���ʾȷ�Ϲر���Ϣ
	 */
	public static void showWindowDialog(String winID, String title, String width, String height, Map<String, String> paramMap, String type, boolean isPopclose, boolean buttonZone) {
		OpenProperties prop = new OpenProperties(winID, title, width, height, paramMap, type, isPopclose, buttonZone);

		AppLifeCycleContext.current().getViewContext().navgateTo(prop);
	}

	/**
	 * ��ʾview�Ի���
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
	 * ��ʾview�Ի���
	 * 
	 * @param viewId
	 * @param title
	 * @param size
	 * @param showCloseHint �Ƿ��ڹر�ʱ��ʾ���ѶԻ���
	 * @author haoy 2012-3-13
	 */
	public static void showViewDialog(String viewId, String title, DialogSize size, boolean showCloseHint) {

		OpenProperties prop =
				new OpenProperties(viewId, title, Integer.toString(size.getWidth()), Integer.toString(size.getHeight()), showCloseHint, false);

		AppLifeCycleContext.current().getWindowContext().popView(prop);
	}

	/**
	 * ��ʾview�Ի���\��������Լ��Ի���ĸ߶ȿ��
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
	 * ��ʾview�Ի��򣬿ɿ����Ƿ���ʾ��ť�����߼��ر�ʱ�Ƿ���ʾȷ���ر�
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
	 * ����ȷ����ʾ��
	 * 
	 * @param title -����
	 * @param message -��ʾ��Ϣ
	 */
	public static boolean showConfirmDialog(String title, String message) {
		return AppInteractionUtil.showConfirmDialog(title, message);
	}

	public static boolean showConfirmDialog(String message) {
		return AppInteractionUtil.showConfirmDialog(ResHelper.getString("c_pub-res", "0c_pub-res0146")/* @res "ѯ��"*/, message);
	}

	/**
	 * ��ʾ �ǡ���ȡ�� ����ť�Ի���
	 * 
	 * @param title
	 * @param message
	 * @return
	 */

	public static TbsDialogResult showChoiceDialog(String title, String message) {
		return AppInteractionUtil.show3ButtonsDialog(title, message, null);
	}

	public static TbsDialogResult showChoiceDialog(String message) {
		return AppInteractionUtil.show3ButtonsDialog(ResHelper.getString("c_pub-res", "0c_pub-res0146")/* @res "ѯ��"*/, message, null);
	}

	/**
	 * �ر�view�Ի���
	 * 
	 * @param viewId
	 * @author haoy 2012-3-13
	 */
	public static void closeViewDialog(String viewId) {
		AppLifeCycleContext.current().getWindowContext().closeView(viewId);
	}

	/* ������ʵ����Ϣ��ƥ��ģʽ */
	private static final Pattern PTN_CONDITION_WITH_MAIN =
			Pattern.compile("([^" + IFromWhereSQLConst.SEP_CONDITION + "]*?)" + IFromWhereSQLConst.SEP_CONDITION + "([^" + IFromWhereSQLConst.SEP_META_PATH + "]*?)" + IFromWhereSQLConst.SEP_META_PATH + "([^" + IFromWhereSQLConst.SEP_CONDITION + "]*?)" + IFromWhereSQLConst.SEP_CONDITION + "(\\d)$");
	/* δ������ʵ����Ϣ��ƥ��ģʽ */
	private static final Pattern PTN_CONDITION = Pattern.compile("(.*?)" + IFromWhereSQLConst.SEP_CONDITION + "(\\d)$");

	/**
	 * ��һ��TranslatedRow��Ӳ�ѯ�������������ֻ��ͨ��genFromWhereSQL��������
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
	 * ͨ��translatedRow��plugout��������ݣ�����FromWhereSQL����ͨ��������������ѯ
	 * 
	 * @param mainEntity
	 * @param row
	 * @return
	 * @throws HrssException
	 * @author haoy 2012-3-13
	 */
	public static final FromWhereSQL genFromWhereSQL(Class<? extends SuperVO> mainEntity, TranslatedRow row) throws HrssException {
		/* ��ȡ������������ʵ���ӦԪ���� */
		IBean mainBean = null;
		if (null != mainEntity) {
			try {
				mainBean = MDBaseQueryFacade.getInstance().getBeanByFullClassName(mainEntity.getName());
			} catch (MetaDataException e1) {
				new HrssException(e1).alert();
			}
		}
		/* �������� */

		String[] keys = row.getKeys();
		Map<String, String> alias = new HashMap<String, String>();
		StringBuffer bufFrom = new StringBuffer();
		StringBuffer bufWhere = new StringBuffer();
		String mainTable = null;
		for (String key : keys) {
			/* ��ȡʵ����ʵ�壬key�а�������ʵ����Ͳ�����ʵ�岻һ�£����״� */
			IBean bean = null;
			Matcher mWithMain = PTN_CONDITION_WITH_MAIN.matcher(key);
			// pk_psndoc#name@6

			Matcher mNoMain = PTN_CONDITION.matcher(key);
			if (mWithMain.matches()) {
				/* �����а�����ʵ����Ϣ */
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
				/* ������δ������ʵ����Ϣ */
				bean = mainBean;
			} else {
				/* ���Ʋ��Ϸ� */
				Logger.error("Bad Format Of Query Key : " + key);
				continue;
			}
			if (null == bean) {
				Logger.error("Can Not Locate Bean By Name: " + key);
				continue;
			}
			/* �������� */

			if (null == mainTable) {
				mainTable = bean.getTable().getName();
				alias.put(".", mainTable);
				bufFrom.append(mainTable).append(" ");
			}
			/* ���value�ǿգ�����Ӵ����� */

			if (row.getValue(key) == null || StringUtils.isEmpty(String.valueOf(row.getValue(key)))) {
				continue;
			}
			/* �ݹ鹹�� */

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
	 * �ݹ����Ԫ���ݲ�ѯ����
	 * 
	 * @param id ��ǰԪ����·���������������磺����1#����2...@������
	 * @param parent ��ǰԪ����·�������ĸ�ʵ��
	 * @param bufFrom from�Ӿ�
	 * @param bufWhere where�Ӿ�
	 * @param alias ����ӳ���
	 * @param value ����ȡֵ
	 * @throws HrssException
	 * @author haoy 2011-11-23
	 */
	private static void getSQL(String id, IBean parent, StringBuffer bufFrom, StringBuffer bufWhere, Map<String, String> alias, Object value)
			throws HrssException {
		String[] cond = id.split(IFromWhereSQLConst.SEP_CONDITION);
		String meta = cond[0];
		int op = Integer.parseInt(cond[1]);
		if (meta.indexOf(IFromWhereSQLConst.SEP_META_PATH) > 0) {
			/* �԰����¼� */

			IAttribute attr = parent.getAttributeByName(meta.substring(0, meta.indexOf(IFromWhereSQLConst.SEP_META_PATH)));

			if (null == attr || !(attr.getDataType() instanceof CollectionType || attr.getDataType() instanceof RefType)) {
				/* �����ǰ����û���¼������¼���󣬲����κδ��� */

				Logger.error("Attribute is null or is not Collection Type: " + id);

				return;
			} else {

				IBean self = attr.getAssociation().getEndBean();
				// /* ���Alias */

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
					/* ���From�Ӿ� */
					String al = alias.get(subTable);
					String apl = parentTable;
					if (alias.get(parentTable) != null) {
						apl = alias.get(parentTable);
					}
					bufFrom.append(" inner join ").append(subTable).append(" ").append(al).append(" on ").append(apl).append(".").append(parentPK).append("=").append(al).append(".").append(fkField);
				}
				/** �ݹ鵽��һ�� */
				String cid = id.substring(id.indexOf(IFromWhereSQLConst.SEP_META_PATH) + 1);
				getSQL(cid, self, bufFrom, bufWhere, alias, value);

			}

		} else {
			/* Ҷ������ ��ֱ�����where���� */
			IAttribute attr = parent.getAttributeByName(meta);
			String al = alias.get("."); // ����
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
		/* TranslatedRows�������б� */

		String[] keys = rows.getKeys();
		HashSet<String> kSet = new HashSet<String>();
		for (String k : keys)
			kSet.add(k);
		int len = rows.getValue(keys[0]).size();
		try {
			/* supervo�������б� */
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
						/* ΪVO���Ը�ֵ */
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
	 * ʱ�����ģ��, ��ȡ����ѯ�����Ŀ�ʼ���ںͽ������ڵ�Ĭ��ֵ.<br/>
	 * ����һ<br/>
	 * 
	 * @return ��Ȼ���������µĵ�һ������һ��
	 */
	public static UFLiteralDate[] getDefaultBeginEndDate() {
		UFLiteralDate d = new UFLiteralDate();
		// �����µĵ�һ��

		UFLiteralDate beginDate = getFirstDateOfMonth(d);
		// �����µ����һ��

		UFLiteralDate endDate = getLastDateOfMonth(d);
		return new UFLiteralDate[] { beginDate, endDate };
	}

	/**
	 * ʱ�����ģ��, ��ȡ����ѯ�����Ŀ�ʼ���ںͽ������ڵ�Ĭ��ֵ.<br/>
	 * ����һ<br/>
	 * 
	 * @return ��Ȼ���������µĵ�һ������һ��
	 */
	public static String[] getStringDefaultBeginEndDate() {
		UFLiteralDate d = new UFLiteralDate();
		// �����µĵ�һ��

		UFLiteralDate beginDate = getFirstDateOfMonth(d);
		// �����µ����һ��

		UFLiteralDate endDate = getLastDateOfMonth(d);
		return new String[] { beginDate.toString(), endDate.toString() };
	}

	/**
	 * ��ȡָ�������������ϵĵ�һ��.
	 * 
	 * @param date ��ǰ����
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
	 * ��ȡָ�������������ϵĵ�һ��.
	 * 
	 * @param date ��ǰ����
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
	 * ���ϵͳ��Ĭ�����
	 * 
	 * @return ϵͳ���
	 */
	public static int getSystemYear() {
		return new UFLiteralDate().getYear();
	}

	/**
	 * ͨ�������ȡOrgVO�� ִ�в��ղ�ѯʱ���ܶ��������Ҫ���ݵ�ǰί����֯��ȡ���������ţ����ÿ�β�ѯ���ݿ�Ч�ʻ���Ӱ�죬 �����ṩ���ͨ�������ȡ�ķ�ʽ
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
	 * �������ܣ�ֻ��Ҫ��ǰ�����ݼ�����
	 * 
	 * @param ds
	 */

	public static void Attachment(Dataset ds, Boolean isPower) {
		Row selRow = ds.getSelectedRow();
		Attachment(ds, selRow, isPower);
	}

	/**
	 * ��������
	 * 
	 * @param ds
	 */

	public static void Attachment(Dataset ds, Row row, Boolean isPower) {
		AppLifeCycleContext.current().getApplicationContext().addAppAttribute("isPower", isPower);

		if (row == null) {
			throw new LfwRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_rm-res", "0c_rm-res0008")/*
																															 * @
																															 * res
																															 * "��ѡ���¼��"
																															 */);
		}

		String primaryField = DatasetUtil.getPrimaryField(ds).getId();
		String primarykey = row.getString(ds.nameToIndex(primaryField));
		AppUtil.addAppAttr(HrssConsts.HRWEB_PRIMARY_KEY, primarykey);
		CommonUtil.showWindowDialog("HrssFilemanager", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_pe-res", "0c_pe-res0023")/*
																																			 * @ res "�ĵ�����"
																																			 */, "750", "450", null, null);

	}

	/**
	 * ��ȡ��ʱ�ļ����ݴ�Ŀ¼
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
	 * ��ȡ����Ŀ¼�ľ���·��
	 * 
	 * @return
	 */
	public static String getAbsoluteExportPath() {
		String path = ContextResourceUtil.getCurrentAppPath() + File.separator + getExportPath().replace('/', File.separatorChar);

		return path;
	}

	/**
	 * ��ȡ����Ŀ¼�����·��
	 * 
	 * @return
	 */
	public static String getExportPath() {
		return "exportfiles/" + SessionUtil.getPk_psnjob();
	}

	/**
	 * ��ȡ��ǰ�����������ǰ�Ĳ��������������������֮һ�� 1��ajax�����У�ͨ��proxy.setParameter���õĲ��������ȼ���� 2��url��������pagemodel�н���ʱ������ֱ��ͨ��request��ȡ
	 * 3��url��������controller�н���ʱ�������Ѿ�����ԭ������������request���Ѿ��޷���ȡԭ������ֻ���� WebContext.getOriginalParameter��ȡ����������Ǵ洢��session�е�
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
	 * ���ھ���ڵ㲿���л� ������ѯ������� ͨ���õ�����ѯds��ղ���
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
	 * 1.���еĸ���Ƭ��,�㳬���Ӵ��ļ�,����ļ���ʽ������,�����ֵ�����window���ٹرյ����� 2.�ڴ�������ж�:�Ƿ񹫹�Ƭ��\�Ƿ��ǲ鿴�ļ� 3.�������Ƭ�μ��鿴�޷�ֱ�Ӵ򿪵��ļ�,��ִ����iframe��������Ӧ�ļ��Ĳ��� 4.ViewOrDownֻ�ܴ���"view"������"down"
	 * 
	 * @param filepath
	 * @param ViewOrDown
	 * @param pub
	 */
	public static void fileDownOrView(String filepath, String ViewOrDown, boolean pubView) {
		// �õ��ļ�������,�ж��ļ������Ƿ�֧�����ߴ�,���typeΪ����֧��Ieֱ�Ӵ�
		int lastIndex = filepath.lastIndexOf(".");
		String fileType = filepath.substring(lastIndex + 1);
		String type = fileMap.get(fileType);
		// ��ֱ��ֱ�Ӵ�\ֱ�Ӳ鿴Ƭ��\���ǹ�������Ƭ�ζ�������Ƭ��

		if (type == null && "view".equals(ViewOrDown) && !pubView) {
			String url = "/portal/pt/erfile/down?id=" + filepath;
			AppLifeCycleContext.current().getWindowContext().downloadFileInIframe(url);
		} else {
			String execScript = "window.open('/portal/pt/erfile/" + ViewOrDown + "?id=" + filepath + "'" + "," + "'_blank');";
			AppLifeCycleContext.current().getApplicationContext().addExecScript(execScript);
		}

	}

	/**
	 * �õ��������ܹ�ֱ�Ӵ򿪵��ļ����͵�map
	 * 
	 * @return
	 */
	public static HashMap<String, String> getFileMap() {
		return fileMap;
	}

	/**************************************************************
	 * ����ָ���û�������langcode Created on 2012-4-26 16:09:51<br>
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
	 * ��;��������/�޸ĵı����������ˢ�º������������ѡ�в����е�����.
	 * 
	 * @param vo
	 * @param fieldId ����ѡ����ʱ,ʹ�õı�׼�ֶ�
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
	 * ������ֵ�Ĺؼ���
	 * 
	 * @return
	 */
	public static String getAppAtrriCntValueKey() {
		return "App_" + AppUtil.getCntAppCtx().getAppId();
	}

	/**
	 * ȡֵ�ֶ�ID�Ĺؼ���
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
