package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.sg.*;
import com.tugalsan.lib.rql.cfg.client.*;
import com.tugalsan.lib.rql.client.*;
import java.util.stream.*;

public class AppCtrlCellHeaderUtils {

    private AppCtrlCellHeaderUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(false, AppCtrlCellHeaderUtils.class);

    public static void executeHeaderUpdate(AppModuleTable tm, String inputText) {
        var curTableName = tm.curTable.nameSql;
        var maxChar = 254;
        if (inputText.length() > maxChar) {
            inputText = inputText.substring(0, maxChar);
            d.ce("executeHeaderUpdate", "Girdi çok uzun; boyu kısaltılıyor (maxChar=" + maxChar + ")");
        }
        var inputText2 = inputText;
        var ci = tm.cells.getActiveColIdx();
        var ct = tm.curTable.columns.get(ci);
        var ref = TGS_LibRqlCfgUtils.PARAM_PREFIX_COLNAME() + ct.getColumnName();
        TGC_SGWTCalller.async(new AppSGFConfigValueSet(curTableName, ref, inputText2), r -> {
            if (r.getOutput_id() == null) {
                d.ce("executeHeaderUpdate", "HATA: Başlık değişikliği yapılırken bir hata oluştu!");
                return;
            }
            d.cr("executeHeaderUpdate", "Başlık değişikliği yapıldı. config.id = " + r.getOutput_id());
            ct.setColumnNameVisible(inputText2);
            refreshHeaderTitle(tm, ci);
        });
    }

    public static void refreshHeaderTitle(AppModuleTable tm, int ci) {
        d.ci("refreshHeaderTitle", ci, "begin");
        var curTableName = tm.curTable.nameSql;
        var ct = tm.curTable.columns.get(ci);
        var cn = ct.getColumnName();
        var cnv = ct.getColumnNameVisible();
        d.ci("refreshHeaderTitle", ci, cn, cnv);
        var cell = (AppCell_STR) tm.cells.headers.get(ci);
        d.ci("refreshHeaderTitle", ci, "#1");
        cell.setValueString(cnv);
        if (App.loginCard.userAdmin) {
            d.ci("refreshHeaderTitle", ci, "#2.begin");
            var cell_colIdx_str = String.valueOf(cell.colIdx);
            d.ci("refreshHeaderTitle", ci, "#2.1");
            var cell_element = cell.getElement();
            d.ci("refreshHeaderTitle", ci, "#2.2");
            var cell_width = TGC_DOMUtils.getWidth(cell_element);
            d.ci("refreshHeaderTitle", ci, "#2.3");
            cell.setTitle(TGS_StringUtils.cmn().concat("sql:", curTableName, ".", cn, ", colIdx:", cell_colIdx_str, ", width:" + cell_width));
            d.ci("refreshHeaderTitle", ci, "#2.end");
        }
        d.ci("refreshHeaderTitle", ci, "#3");
        var filterProfile = tm.filter.popMain.profiles.get(ci);
        d.ci("refreshHeaderTitle", ci, "#4");
        filterProfile.initListBoxItemText = cnv;
        d.ci("refreshHeaderTitle", ci, "#5");
        filterProfile.renderPopLb();
        d.ci("refreshHeaderTitle", ci, "fin");
    }

    public static void tempHeaderTitle(AppModuleTable tm, int ci, String tempLabel) {
        var cell = (AppCell_STR) tm.cells.headers.get(ci);
        cell.setValueString(tempLabel);
    }

    public static void refreshHeaderTitles(AppModuleTable tm, boolean lngFamilyOnly) {
        IntStream.range(0, tm.curTable.columns.size()).forEachOrdered(ci -> {
            var tc = TGS_LibRqlColUtils.toSqlCol(tm.curTable.columns.get(ci));
            if (!lngFamilyOnly || tc.groupLnk()) {
                refreshHeaderTitle(tm, ci);
            }
        });
    }

    public static void tempHeaderTitles(AppModuleTable tm, String tempLabel, boolean lngFamilyOnly) {
        IntStream.range(0, tm.curTable.columns.size()).forEachOrdered(ci -> {
            var tc = TGS_LibRqlColUtils.toSqlCol(tm.curTable.columns.get(ci));
            if (!lngFamilyOnly || tc.groupLnk()) {
                tempHeaderTitle(tm, ci, tempLabel);
            }
        });
    }
}
