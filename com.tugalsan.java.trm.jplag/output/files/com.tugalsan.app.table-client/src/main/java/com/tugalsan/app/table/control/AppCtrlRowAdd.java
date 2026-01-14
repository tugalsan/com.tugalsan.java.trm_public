package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.gui.client.click.TGC_ClickUtils;
import com.tugalsan.api.gui.client.focus.TGC_FocusUtils;
import com.tugalsan.api.gui.client.focus.TGS_FocusSides4;
import com.tugalsan.api.gui.client.key.TGC_KeyUtils;
import com.tugalsan.api.gui.client.widget.TGC_ButtonUtils;
import com.tugalsan.api.icon.client.TGS_IconUtils;
import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.sg.init.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.boot.client.*;

public class AppCtrlRowAdd {

    final private static TGC_Log d = TGC_Log.of(AppCtrlRowAdd.class);

    public AppCtrlRowAdd(AppModuleTable tm) {
        this.tm = tm;
    }
    final private AppModuleTable tm;
    public PushButton btn;

    public void configFocus() {
        TGC_FocusUtils.addKeyDown(btn, nativeKeyCode -> {
            var filterFocusSides = new TGS_FocusSides4(tm.page.btnLast, tm.settings.btn, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            TGC_FocusUtils.focusSide(btn, filterFocusSides, nativeKeyCode);
        });
    }

    public void configLayout(HorizontalPanel p) {
        p.add(btn);
        btn.addStyleName(AppModuleTable.class.getSimpleName() + "_btn");
    }

    public void configActions() {
        TGC_ClickUtils.add(btn, () -> onAdd());
        TGC_KeyUtils.add(btn, () -> onAdd(), null);
    }

    public void createWidgets() {
        btn = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_PLUS(), "Ekle");
        var cfg = App.userTableConfig.stream()
                .filter(_cfg -> _cfg.table.nameSql.equals(App.curTableName))
                .findAny().orElse(null);
        if (cfg == null) {
            d.ce("createWidgets", "cfg == null");
            btn.setEnabled(false);
            return;
        }
        if (cfg.editableDays == 0) {
            btn.setEnabled(false);
        }
    }

    public void onAdd() {
//        if (tm.filter.isActive()) {//NO NEEDUYARI VERIYOR ZATEN
//            d.ce("onAdd", "Uyarı: Filitre aktif!");
//            tm.filter.popAdv.onResetCommon.run();
//        }
        AppCtrlCellRowUtils.createNewRow(tm.dbCfg, tm.curTable, row -> {//(List<TGS_SQLResultSetValue> row) -> {
            AppCtrlCellRowUtils.insertNewRow(tm, row, (Long id) -> {
                tm.page.onPageFirst();
                tm.page.updatePage_IfIdIsNotShown_WarnUser(id);
                TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredLeftMostCell());
            });
        });
    }
}
