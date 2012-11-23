package SearchBasis.portlet.component;

/**
 * Created with IntelliJ IDEA.
 * User: rousselle-k
 * Date: 08/11/12
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;


//this part is configuration of the portlet, we set the path to the template groovy.
@ComponentConfig(
        lifecycle = UIApplicationLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UISearchBasisPortlet.gtmpl",
        events = { @EventConfig(listeners = UISearchBasisPortlet.SwitchTabActionListener.class) }

)
public class UISearchBasisPortlet extends UIPortletApplication {

    final public static String SWTCH_TAB = "SwitchTab".intern() ;


    public UISearchBasisPortlet() throws Exception {

        addChild(UIComponentForm.class, null, "Advanced search");
        addChild(UISimpleSearchForm.class,null,"Simple search");
        addChild(UIResultForm.class,null,"Result");
        setRenderedChild("Advanced search");


    }

    static public class SwitchTabActionListener extends EventListener<UISearchBasisPortlet>
    {
        public void execute(Event<UISearchBasisPortlet> event) throws Exception
        {
            UISearchBasisPortlet uiSearchBasisPortlet = event.getSource();
            String id = event.getRequestContext().getRequestParameter(OBJECTID);
            if (id == null)
                return;
            for(UIComponent uicomponent : uiSearchBasisPortlet.getChildren()){
                if(!uicomponent.getId().equals(id)){
                    uicomponent.setRendered(false);
                }
                else{
                    uicomponent.setRendered(true);
                }
            }
            event.getRequestContext().addUIComponentToUpdateByAjax(uiSearchBasisPortlet) ;
        }
    }

}

