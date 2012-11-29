package infoBasis.portlet.component;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import javax.servlet.http.HttpSession;
import org.exoplatform.portal.webui.util.Util;

@ComponentConfig(
        lifecycle = UIApplicationLifecycle.class,
        template =  "app:/groovy/InfoBasis/portlet/UIInfoBasisPortlet.gtmpl")
public class UIInfoBasisPortlet extends UIPortletApplication {

    public UIInfoBasisPortlet( ) throws Exception {
    }
    public String getFolderNumber(){
		HttpSession httpSession = Util.getPortalRequestContext().getRequest().getSession();
		if (httpSession.getAttribute("basisFolderNumber") != null) {
		   return httpSession.getAttribute("basisFolderNumber").toString();
		}
		return null;
	}
    public String getDocumentId(){
		HttpSession httpSession = Util.getPortalRequestContext().getRequest().getSession();
		if (httpSession.getAttribute("basisDocumentId") != null) {
		   return httpSession.getAttribute("basisDocumentId").toString();
		}
		return null;
    }	
}

