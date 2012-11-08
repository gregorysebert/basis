package be.bull.exo.scripts;

import java.util.List;
import java.util.ArrayList;

import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.services.cms.scripts.CmsScript;

public class FillSelectBoxWithRegions implements CmsScript {

    String[] regions;

    public FillSelectBoxWithRegions() {
    }

    public void execute(Object context) {
        UIFormSelectBox selectBox = (UIFormSelectBox) context;
        List options = new ArrayList();
        String currentRegion = " ";
        if (regions.length == 4) {
            currentRegion = regions[0];
            if (!currentRegion.trim().equals(""))
                options.add(new SelectItemOption(currentRegion, currentRegion));
        }
        options.add(new SelectItemOption("", ""));
        for (String region: regions) {
            if (!region.equals(currentRegion))
                options.add(new SelectItemOption(region, region));
        }
        selectBox.setOptions(options);
    }

    public void setParams(String[] params) {
        regions = params;
    }
}