package com.yonyou.portal.hrss.wabasch.wfm;

import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.comp.MenubarComp;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.conf.EventConf;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.uimodel.WindowConfig;
import nc.uap.lfw.jsp.uimeta.UIFlowhLayout;
import nc.uap.lfw.jsp.uimeta.UIMenubarComp;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.uap.wfm.constant.WfmConstants;
import nc.uap.wfm.pubview.ExecuteTaskWidgetProvider;
import nc.uap.wfm.utils.AppUtil;
import nc.uap.wfm.utils.WfmTaskUtil;
import uap.lfw.core.ml.LfwResBundle;

import com.yonyou.portal.hrss.wabasch.WabaschCardWinApproveViewCtrl;
import com.yonyou.portal.hrss.wabasch.WabaschCardWinMainViewCtrl;

public class WabascheTaskWidgetProvider extends ExecuteTaskWidgetProvider {
	public static final String SimpleExeMenubar = "simpleExeMenubar";

	@Override
	protected void initWidget(LfwView widget, String taskPk, String billstate) {
		//重写simple公共view的Controller
		widget.setControllerClazz(WabaschCardWinApproveViewCtrl.class.getName());

		Object task = WfmTaskUtil.getTaskFromSessionCache(taskPk);

		MenubarComp menubar = new MenubarComp();
		menubar.setId(SimpleExeMenubar);
		widget.getViewMenus().addMenuBar(menubar);

		//暂存
		MenuItem tempSaveitem = new MenuItem(BTN_SAVE);
		tempSaveitem.setText(getText(WfmConstants.INTERIM, task));
		EventConf savee = MouseEvent.getOnClickEvent();
		savee.setMethodName("simpleBtnsave_click");
		tempSaveitem.addEventConf(savee);
		//menubar.addMenuItem(tempSaveitem);

		//提交
		MenuItem okMenuItem = new MenuItem();
		okMenuItem.setId(BTN_OK);
		okMenuItem.setText(getText(WfmConstants.SUBMIT, task));
		EventConf oke = MouseEvent.getOnClickEvent();
		oke.setMethodName("simpleBtnok_click");
		okMenuItem.addEventConf(oke);
		//menubar.addMenuItem(okMenuItem);

		String assignWindowId = "assignuser";
		WindowConfig assignWindow = new WindowConfig();
		assignWindow.setId(assignWindowId);
		widget.addInlineWindow(assignWindow);

		//附件管理
		MenuItem attchFile = new MenuItem();
		attchFile.setText(getText(WfmConstants.ATTACHFILE, task));
		attchFile.setId(LINK_ADDATTACH);
		EventConf attchFileevent = MouseEvent.getOnClickEvent();
		attchFileevent.setMethodName("simpleAttachClick");
		attchFile.addEventConf(attchFileevent);
		//menubar.addMenuItem(attchFile);

		String attachwindowId = "filemgr";
		WindowConfig attachWindow = new WindowConfig();
		attachWindow.setId(attachwindowId);
		widget.addInlineWindow(attachWindow);

		MenuItem allFlowMenu = new MenuItem();
		allFlowMenu.setId("allFlow");
		allFlowMenu.setText(LfwResBundle.getInstance().getStrByID("wfm", "SimpleExeTaskWidgetProvider-000000")/*流程*/);

		//流程进度
		MenuItem flowImageMenu = new MenuItem();
		flowImageMenu.setText(getText(WfmConstants.FLOWIMG, task));
		flowImageMenu.setId(LINK_FLOWIMG);
		EventConf flowImageEvent = MouseEvent.getOnClickEvent();
		flowImageEvent.setMethodName("simpleFlowImageClick");
		flowImageMenu.addEventConf(flowImageEvent);

		allFlowMenu.addMenuItem(flowImageMenu);

		//流程进度Window
		String windowId = "wfm_flowhistory";
		WindowConfig scratchWindow = new WindowConfig();
		scratchWindow.setId(windowId);
		widget.addInlineWindow(scratchWindow);

		//收回
		MenuItem recallMenu = new MenuItem();
		recallMenu.setText(getText(WfmConstants.BACK, task));
		recallMenu.setId(BTN_RECALL);
		EventConf recallEvent = MouseEvent.getOnClickEvent();
		recallEvent.setMethodName("simplerecallClick");
		recallMenu.addEventConf(recallEvent);

		//驳回
		MenuItem rejectMenu = new MenuItem();
		rejectMenu.setText(getText(WfmConstants.REJECT, task));
		rejectMenu.setId(EXE_REJECT);
		EventConf rejectEvent = MouseEvent.getOnClickEvent();
		rejectEvent.setMethodName("simpleRejectClick");
		rejectMenu.addEventConf(rejectEvent);

		//阅毕
		MenuItem readEndMenu = new MenuItem();
		readEndMenu.setText(getText(WfmConstants.READOVER, task));
		readEndMenu.setId(Exe_ReadEnd);
		EventConf readEvent = MouseEvent.getOnClickEvent();
		readEvent.setMethodName("simpleReadEndClick");
		readEndMenu.addEventConf(readEvent);

		//制单态
		if (WfmConstants.BILLSTATE_MAKEBILL == billstate) {
			//按钮：暂存、提交、附件、流程进度；
			menubar.addMenuItem(tempSaveitem);
			menubar.addMenuItem(okMenuItem);
			menubar.addMenuItem(attchFile);
			menubar.addMenuItem(allFlowMenu);
		}
		//浏览态
		else if (WfmConstants.BILLSTATE_BROWSE == billstate) {
			//按钮：收回（显示后台逻辑控制）、附件、流程进度；
			if (WfmTaskUtil.isCanReCall(task))
				menubar.addMenuItem(recallMenu);
			menubar.addMenuItem(attchFile);
			menubar.addMenuItem(allFlowMenu);
		}
		//审批态
		else if (WfmConstants.BILLSTATE_APPROVE == billstate) {
			//按钮：暂存、提交、驳回、附件、流程进度；
			menubar.addMenuItem(tempSaveitem);
			menubar.addMenuItem(okMenuItem);
			//前加签，后加签，承办不需要驳回按钮
			if (!WfmTaskUtil.isAddSignOrAssist(task))
				menubar.addMenuItem(rejectMenu);
			menubar.addMenuItem(attchFile);
			menubar.addMenuItem(allFlowMenu);
		}
		//传阅态
		else if (WfmConstants.BILLSTATE_READ == billstate) {
			//按钮：阅毕、附件、流程进度；
			menubar.addMenuItem(readEndMenu);
			menubar.addMenuItem(attchFile);
			menubar.addMenuItem(allFlowMenu);

		}
	}

	public UIMeta getDefaultUIMeta(LfwView widget) {
		String widgetId = widget.getId();
		UIMeta um = new UIMeta();
		String isNC = (String) AppUtil.getAppAttr(WfmConstants.WfmAppAttr_IsNC);
		if (!"Y".equals(isNC))
			um.setIncludejs("wfinclude.js");
		um.setId(widgetId + "_um");
		um.setFlowmode(Boolean.TRUE);
		//创建只有上面一排按钮的widget
		//	getMakeBillUIMeta(widget, isNC, um);
		UIFlowhLayout flowh = new UIFlowhLayout();
		flowh.setId("flowhlayout5101");
		um.setElement(flowh);

		UIMenubarComp uiMenubar = new UIMenubarComp();
		uiMenubar.setId(SimpleExeMenubar);

		flowh.addElementToPanel(uiMenubar);

		um.adjustUI(widgetId);
		return um;
	}
}
