package SearchBasis.portlet.component;

import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.form.UIForm;

/**
 * Created with IntelliJ IDEA.
 * User: rousselle-k
 * Date: 21/11/12
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
@ComponentConfig(
        lifecycle = UIFormLifecycle.class,
        template =  "app:/groovy/SearchBasis/portlet/UISimpleSearchForm.gtmpl"

)
public class UISimpleSearchForm extends UIForm {
}
