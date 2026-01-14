package com.tugalsan.app.table.control;

import com.tugalsan.app.table.*;
import com.tugalsan.lib.boot.client.*;

public class AppCtrlPageHeader {

//    final private static TGC_Log d = TGC_Log.of(AppCtrlPageHeader.class);

    public AppCtrlPageHeader(AppModuleTable tm) {
        this.tm = tm;
    }
    private final AppModuleTable tm;

    public void setTempTitle(String tempTitle) {
        TGC_LibBootGUIBody.title.setHTML(tempTitle);
    }

    public void resetTitle() {
        TGC_LibBootGUIBody.title.setHTML(tm.getBodyTitle());
    }
}
