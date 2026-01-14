package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.gui.client.widget.table.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.*;
import java.util.stream.*;

public class AppCtrlCellConfigUtils {

    private AppCtrlCellConfigUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(false, AppCtrlCellConfigUtils.class);

    public static void configInit(AppModuleTable tm) {
        d.ci("configInit", "configRestore...");
        configRestore(tm);
        d.ci("configInit", "configFile...");
        configFile(tm);
        d.ci("configInit", "configCells...");
        configCells(tm);
        d.ci("configInit", "done.");
    }

    private static void configFile(AppModuleTable tm) {
        //DISABLE ALL
        var listBox = tm.cells.popFileOperations.listBox;
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX1_TMPL_UPLOAD(), false);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX2_TMPL_SHOWREV(), false);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX3_SPC(), false);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX5_FILE_UPLOAD(), false);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX6_FILE_SHOWREV(), false);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX7_FILE_DELREV(), false);

        //DECIDE
        var curTableName = tm.curTable.nameSql;
        var writeFile = AppModuleTableUtils.getAllowWrite(curTableName);
        var writeTempl = App.loginCard.userAdmin;
        var deleteFile = App.loginCard.userAdmin;

        //ENABLE SOME
        d.ci("createPops", "Enabling...(" + writeFile + ", " + writeTempl + ", " + deleteFile + ")");
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX1_TMPL_UPLOAD(), writeTempl);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX2_TMPL_SHOWREV(), writeTempl);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX5_FILE_UPLOAD(), writeFile);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX6_FILE_SHOWREV(), writeFile);
        TGC_DOMUtils.setListBoxItemEnableAt(listBox, AppCtrlCell.FILE_MENU_IDX7_FILE_DELREV(), deleteFile);

        //LOG
        d.ci("createPops", writeFile ? "Dosya yükleme açıldı." : "Dosya yükleme kilitlendi.");
        d.ci("createPops", writeTempl ? "Şablon yükleme açıldı." : "Şablon yükleme kilitlendi.");
        d.ci("createPops", deleteFile ? "Dosya silme açıldı." : "Dosya silme kilitlendi.");
    }

    private static void configRestore(AppModuleTable tm) {
        var listBox = tm.cells.popRestoreConfirm.listBox;
        listBox.addItem("Üzerine geri al");
        listBox.addItem("Yeni bir satıra geri al");
    }

    private static void configCells(AppModuleTable tm) {
        var curTableNameVisible = tm.curTable.nameReadable;
        d.ci("configCells", curTableNameVisible + " tablosu görüntüleniyor...", tm.cells.headers.size(), tm.cells.rows.size());
        IntStream.range(0, tm.cells.headers.size()).forEachOrdered(ci -> {
            d.ci("configCells", "headers.ci", ci, "#1");
            AppCtrlCellHeaderUtils.refreshHeaderTitle(tm, ci);
            d.ci("configCells", "headers.ci", ci, "#2");
            tm.cells.headers.get(ci).setStyleName(TGC_TableHeaderStyled.class.getSimpleName());
            d.ci("configCells", "headers.ci", ci, "#3");
        });
        tm.cells.rows.forEach(row -> {
            row.forEach(cell -> {
                if (cell instanceof AppCell_LNGLINK cellLngLink) {
                    cellLngLink.setGui(tm.cells.popEditCellLNGLINK);
                } else if (cell instanceof AppCell_STRLINK cellStrLink) {
                    cellStrLink.setGui(tm.cells.popEditCellSTRLINK);
                }
                cell.setStyleName(TGC_TableRow.class.getSimpleName());
            });
        });
    }
}
