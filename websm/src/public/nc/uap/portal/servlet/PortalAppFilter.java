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
			//userData.isUserChanged()��ʱ��鲻���û��ı䣬�����������¼ҳ��������֤�쳣�����԰ѵ���ע���ˣ����û����µ�¼
			String returnUrl = SSOProperties.getInstance().getProperty("SSOLogoutURL");// ��ȡ����ע����ַ
			if (null != returnUrl){
				returnUrl = returnUrl.replace("${URL}", req.getRequestURL());// ��תע��ҳ��
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
