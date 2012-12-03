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
	
    private String getSessionAttribute(String sessionAttribute){
    	HttpSession httpSession = Util.getPortalRequestContext().getRequest().getSession();
		if (httpSession.getAttribute(sessionAttribute) != null) {
		   return httpSession.getAttribute(sessionAttribute).toString();
		}
		return null;
    }
	public UIInfoBasisPortlet( ) throws Exception {
    }
    public String getFolderNumber(){
		return getSessionAttribute("basisFolderNumber");
	}
    public String getDocumentId(){
		return getSessionAttribute("basisDocumentId");
    }
    public String getFolderNodePath(){
    	if (getSessionAttribute("basisFolderNodePath") != null) {
    		return Util.getPortalRequestContext().getRequestURI() + "?path=Order Desk" + getSessionAttribute("basisFolderNodePath");
    	}
    	return null;
	}
    public String getDocumentNodePath(){
    	if (getSessionAttribute("basisDocumentNodePath") != null) {
    		return Util.getPortalRequestContext().getRequestURI() + "?path=Order Desk" + getSessionAttribute("basisDocumentNodePath");
    	}
    	return null;
    }
}

