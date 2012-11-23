import java.util.List;
import java.util.ArrayList;

import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.services.cms.scripts.CmsScript;

public class FillSelectBoxWithTitles implements CmsScript {

    String[] titles;

    public FillSelectBoxWithTitles() {

    }

    public void execute(Object context) {
        UIFormSelectBox selectBox = (UIFormSelectBox) context;
        List options = new ArrayList();
        options.add(new SelectItemOption("", ""));
        for (String title: titles) {
            options.add(new SelectItemOption(title.replace(";", ", "), title.replace(";", ", ")));
        }
        selectBox.setOptions(options);
    }

    public void setParams(String[] params) {
        titles = params;
    }
}