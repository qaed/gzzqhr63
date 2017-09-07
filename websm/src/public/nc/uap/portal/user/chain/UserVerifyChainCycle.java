package nc.uap.portal.user.chain;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.portal.plugins.PluginManager;
/**
 * Íø±¨µ¥µãµÇÂ¼
 * @author YiXin
 *
 */
public class UserVerifyChainCycle {
	public static final void verify() {
		List<IUserVerifyChain> chains = PluginManager.newIns().getExtInstances("UserVerifyChain", IUserVerifyChain.class);
		MySSOVerifyChain myVerifychain = new MySSOVerifyChain();
		chains.add(myVerifychain);
		if (LfwRuntimeEnvironment.getWebContext() == null)
			return;
		HttpServletRequest request = LfwRuntimeEnvironment.getWebContext().getRequest();

		if (chains.size() > 0) {
			VerifyAtomChain chainBase = new VerifyAtomChain();
			for (int k = 0; k < chains.size(); k++) {
				if (!chainBase.isChainBreak()) {
					((IUserVerifyChain) chains.get(k)).doVerify(request, chainBase);
				}
			}
		}
	}
}
