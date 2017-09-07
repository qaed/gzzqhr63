package nc.uap.portal.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.landray.sso.client.EKPSSOUserData;
import com.landray.ssoclient.SSOProperties;

import nc.uap.lfw.app.filter.AppFilter;

public class PortalAppFilter extends AppFilter implements Filter {

	public PortalAppFilter() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//YiXin add begin 2016-06-04
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		if (null != uri && uri.contains("portal/app/mockapp/login.jsp")) {
			//userData.isUserChanged()有时检查不到用户改变，重新跳单点登录页面会出现验证异常，所以把单点注销了，让用户重新登录
			String returnUrl = SSOProperties.getInstance().getProperty("SSOLogoutURL");// 获取单点注销地址
			if (null != returnUrl){
				returnUrl = returnUrl.replace("${URL}", req.getRequestURL());// 跳转注销页面
				res.sendRedirect(returnUrl);
				return;
			}
//			res.sendRedirect("/portal/loginsso.jsp?lrid=1");
//			return;
		}
		//YiXin add end
		super.doFilter(request, response, chain);
	}
}
