package com.tugalsan.app.table.control.utils;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUUtils;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.servlet.upload.client.TGS_SUploadUtils;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.*;
import com.tugalsan.app.table.pop.*;
import com.tugalsan.app.table.sg.path.*;
import com.tugalsan.app.table.sg.rev.*;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.file.client.*;
import com.tugalsan.lib.rql.cfg.client.*;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.table.client.*;
import java.util.*;
import java.util.stream.*;

public class AppCtrlCellCreateUtils {

    private AppCtrlCellCreateUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(AppCtrlCellCreateUtils.class);

    public static void createPops(AppModuleTable tm) {
        var curTableName = tm.curTable.nameSql;
        var dim = new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true);
        tm.cells.popEditCellLNGLINK = new AppPopEditCellLNGLINK(tm);
        tm.cells.popEditCellSTRLINK = new AppPopEditCellSTRLINK(tm);
        tm.cells.popFileChooser = new TGC_PopLblYesNoListBox(
                dim, null, "Dosya Seçimi:", "İşlem", "İptal",
                p -> {
                    d.ci("createPops", "popFileChooser.act", "init");
                    p.getPop().setVisible(false);
                    var si = p.listBox.getSelectedIndex();
                    if (si == -1) {
                        d.cr("createPops", "popupListFileChooser", "Seçim yapılmadı.", si);
                        return;
                    }
                    var file = tm.cells.popFileChooser.listBox.getItemText(si);
                    var colName = tm.curTable.columns.get(tm.cells.getActiveColIdx()).getColumnName();
                    d.ci("createPops", "popFileChooser.act", "file", file, "colName", colName);
                    if (tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX2_TMPL_SHOWREV()
                    || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX6_FILE_SHOWREV()) {
                        d.ci("createPops", "popFileChooser.act", "mode.show");
                        var file_tablename = curTableName;
                        var file_columnname = colName;
                        d.ci("createPops", "popupListFileChooser", "file_urls,file_tablename,file_columnname", file_tablename, file_columnname);
                        TGC_SGWTCalller.async(new AppSGFPathHttpInboxFileUrl(file_tablename, file_columnname, file), r -> {
                            if (r.getOutput_url() == null) {
                                d.ce("createPops", "popupListFileChooser", "URL çekilirken bir hata oluştu!" + " reKolon:" + tm.cells.getActiveColIdx() + ", refDosya:" + file + ", hata:o.getData():false");
                            } else {
                                var url = r.getOutput_url();
                                d.cr("createPops", "popupListFileChooser", "url", url);
                                TGC_BrowserWindowUtils.openNew(TGS_Url.of(url));
                            }
                        });
                    } else if (tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX7_FILE_DELREV()) {
                        d.ci("createPops", "popFileChooser.act", "mode.del");
                        TGC_SGWTCalller.async(new AppSGFPathInboxDelete(curTableName, colName, file), r -> {
                            if (!r.getOutput_result()) {
                                d.ce("createPops", "popupListFileChooser", "Dosya silme işlemi: BAŞARISIZ! Tomcat'i yeniden başlatmayı deneyin." + file);
                                return;
                            }
                            d.cr("createPops", "popupListFileChooser", "Dosya silme işlemi: başarılı. " + file);
                        });
                    }
                },
                p -> p.getPop().setVisible(false),
                null, null
        );
        tm.cells.popFileOperations = new TGC_PopLblYesNoListBox(
                dim,
                TGS_ListUtils.of(
                        "Şablonu göster",//0
                        "Şablon yükle",//1
                        "Şablon revizyonlarını dök, göster",//2
                        "-----------------------------",//3
                        "Dosyayı göster",//4
                        "Dosya yükle",//5
                        "Dosya revizyonlarını dök, göster",//6
                        "Dosya revizyonlarını dök, sil"//7
                ),
                "Dosya İşlemleri:", "İşlemi Başlat", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    tm.cells.popFileOperationsIdx = p.listBox.getSelectedIndex();
                    d.ci("createPops", "popupListFileMenu", "Seçilen işlem: " + tm.cells.popFileOperations.listBox.getItemText(tm.cells.popFileOperationsIdx));
                    d.ci("createPops", "popupListFileMenu", ". Dosyalar süzülüyor, lütfen bekleyin ...");
                    AppCtrlCellUpdateUtils.file(tm);
                },
                p -> p.getPop().setVisible(false),
                null, () -> tm.cells.popFileOperations.listBox.setSelectedIndex(AppCtrlCell.FILE_MENU_IDX4_FILE_SHOWLAST())
        );
        tm.cells.popFileUpload = new TGC_LibFileUploadPop(dim, (pop, reply) -> {
            reply = reply.replace("\n", "").replace("<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">", "").replace("</pre>", "");
            if (reply.contains(TGS_SUploadUtils.RESULT_UPLOAD_USER_SUCCESS())) {
                d.cr("createPops", "manPopUpUpload", "BAŞARILI: Yükleme Başarılı!");
                if (!tm.dbCfg.isAny()) {
                    tm.cells.popRowModify.imageHandler.reloadImageById(tm.curTable.nameSql, pop.id);
                }
                return;
            }
            if (reply.contains(TGS_SUploadUtils.RESULT_UPLOAD_USER_TARGETCOMPILED_NULL())) {
                d.ce("createPops", "manPopUpUpload", "HATA: Yükleme Başarısız!", reply, "UYARI: Yetkilendirilmiş gün sayısından daha önceki bir kayda yükleme yapılmaya çalışılmış olabilir.");
            } else {
                d.ce("createPops", "manPopUpUpload", "HATA: Yükleme Başarısız!", reply);
            }
        }, pop -> {
            d.ce("createPops", "manPopUpUpload", "BİLGİ: Dosya yükleme iptal edildi.");
        },
                null,
                TGC_LibTableUploadTblFileUtils.url()
        );
        tm.cells.popRowRestoreOperations = new TGC_PopLblYesNoTextArea(
                dim, "Yedeklemiş satır verisi:", "Geri al", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    var tn = tm.curTable.nameSql;
                    TGC_SGWTCalller.async(new AppSGFRowExists(tm.dbCfg.clear(), tn, tm.cells.restoreRefId), response -> {
                        if (response.getOutput_result()) {
                            d.cr("createPops", "Konu tabloda konu id var. Ne yapalım?" + " tablo: " + tn + ", id: " + tm.cells.restoreRefId);
                            tm.cells.popRestoreConfirm.listBox.setSelectedIndex(1);
                            tm.cells.popRestoreConfirm.getPop().setVisible(true);
                        } else {
                            TGC_SGWTCalller.async(new AppSGFRevRestoreRow(tm.cells.restoreData, tn, tm.cells.restoreRefId, false), response2 -> {
                                d.cr("createPops", "Konu tabloda konu id yoktu. Yerine geri alma tamamlandı." + " tablo: " + tn + ", id: " + tm.cells.restoreRefId);
                            });
                        }
                    });
                },
                p -> p.getPop().setVisible(false),
                null
        );
        tm.cells.popRowRestoreOperations.setEditable(false);
        tm.cells.popRestoreConfirm = new TGC_PopLblYesNoListBox(
                dim, null, "Satır verisi nasıl geri alınsın?", "Geri al", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    var si = p.listBox.getSelectedIndex();
                    if (si == -1) {
                        d.ce("createPops", "popup.actionPerformed", "Seçim yapılmadı.", si);
                        return;
                    }
                    var tn = tm.curTable.nameSql;
                    if (si == 0) {
                        TGC_SGWTCalller.async(new AppSGFRevRestoreRow(tm.cells.restoreData, tn, tm.cells.restoreRefId, true), response2 -> {
                            d.cr("createPops", "Yerine geri alma tamamlandı." + " tablo: " + tn + ", id: " + tm.cells.restoreRefId);
                        });
                    } else if (si == 1) {
                        TGC_SGWTCalller.async(new AppSGFRevRestoreRow(tm.cells.restoreData, tn, null, false), response2 -> {
                            d.cr("createPops", "Yeni bir satıra geri alma tamamlandı." + " tablo: " + tn + ", id: " + tm.cells.restoreRefId);
                        });
                    }
                },
                p -> p.getPop().setVisible(false),
                null, null
        );
        tm.cells.popRowModify = new AppPopRowModify(tm);
    }

    public static void createWidgets(AppModuleTable tm) {
        tm.cells = new AppCtrlCell(tm);
        tm.cells.headers = TGS_ListUtils.of();
        tm.cells.rows = TGS_ListUtils.of();
        curTable_config_check(tm);
        curTable_config_cells_load(tm);
        cells_create(tm);
        AppCtrlCellLayoutUtils.cells_layout_refresh("createWidgets", tm);//WHY NEEDED?
//        AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, null);//WHY NEEDED?
    }

    private static void curTable_config_check(AppModuleTable tm) {
        if (tm.curTable.tableOrder == null) {
            d.ce("curTable_config_check", "tm.curTable.tableOrder == null");
        }
        if (tm.curTable.tableOrderGroups == null) {
            d.ce("curTable_config_check", "tm.curTable.tableOrderGroups == null");
        }
        var defWidth = TGS_LibRqlCfgUtils.DEFAULT_CELL_DIM().width;
        IntStream.range(0, tm.curTable.columns.size()).forEach(ci -> {
            if (ci >= tm.curTable.columnConfig.size()) {
                d.ce("curTable_config_check", "defwidth added");
                tm.curTable.columnConfig.add(defWidth);
                return;
            }
            var val = tm.curTable.columnConfig.get(ci);
            if (val == null) {
                d.ce("curTable_config_check", "defwidth added because colCfg is null");
                tm.curTable.columnConfig.add(defWidth);
                return;
            }
            tm.curTable.columnConfig.add(val);
        });
    }

    private static void curTable_config_cells_load(AppModuleTable tm) {
        tm.cells.colWidths = tm.curTable.columnConfig;
        tm.cells.isColHidden = new boolean[tm.curTable.columns.size()];
        Arrays.fill(tm.cells.isColHidden, false);

        //ADMIN
        if (App.loginCard.userAdmin) {
            return;
        }

        //USER
        var tn = tm.curTable.nameSql;
        var hideIndexes = AppModuleTableUtils.getHideIdxs(tn);
        if (hideIndexes.isEmpty()) {
            return;
        }
        IntStream.range(0, hideIndexes.size()).forEach(i -> {
            var ci = hideIndexes.get(i);
            if (ci == null) {
                d.ce("curTable_config_cells_load", "ERROR: columnHide.tag is null", "idx", i);
                return;
            }
            if (ci < 0) {
                d.ce("curTable_config_cells_load", "ERROR: smaller than 0 @ TMTableWidgetsHolder_Layout.columnHidden." + ci);
                return;
            }
            if (ci >= tm.cells.isColHidden.length) {
                d.ce("curTable_config_cells_load", "ERROR: larger than column size @ TMTableWidgetsHolder_Layout.", ".allowColumnHide.", ci, tm.cells.isColHidden.length);
                return;
            }
            tm.cells.isColHidden[ci] = true;
        });
    }

    private static void cells_create(AppModuleTable tm) {
        IntStream.range(0, tm.curTable.getColumns().size()).forEachOrdered(ci -> {//CREATE AND PLACE HEADER CELLS IF NOT_HIDDEN
            var fw = new AppCell_STR(tm, -1, ci, tm.curTable.columns.get(ci));
            tm.cells.headers.add(fw);
        });
        IntStream.range(0, tm.cellsRowSize).forEachOrdered(ri -> {//CREATE AND PLACE DATA CELLS IF NOT_HIDDEN
            List<FocusWidget> tableDataRow = TGS_ListUtils.of();
            IntStream.range(0, tm.curTable.getColumns().size()).forEachOrdered(ci -> {
                var fw = createCell(tm, ri, ci, tm.curTable.columns.get(ci));
                tableDataRow.add(fw);
            });
            tm.cells.rows.add(tableDataRow);
        });
    }

    private static FocusWidget createCell(AppModuleTable tm, int r, int c, TGS_LibRqlCol ct) {
        var tc = TGS_LibRqlColUtils.toSqlCol(ct);
        if (tc.typeBytes()) {
            return new AppCell_BYTES(tm, r, c, ct);
        }
        if (tc.typeBytesRow()) {
            return new AppCell_BYTESROW(tm, r, c, ct);
        }
        if (tc.typeBytesStr()) {
            return new AppCell_BYTESSTR(tm, r, c, ct);
        }
        if (tc.typeLng()) {
            return new AppCell_LNG(tm, r, c, ct);
        }
        if (tc.typeLngDate()) {
            return new AppCell_LNGDATE(tm, r, c, ct);
        }
        if (tc.typeLngDbl()) {
            return new AppCell_LNGDOUBLE(tm, r, c, ct);
        }
        if (tc.typeLngLnk()) {
            return new AppCell_LNGLINK(tm, r, c, ct);
        }
        if (tc.typeLngTime()) {
            return new AppCell_LNGTIME(tm, r, c, ct);
        }
        if (tc.typeStr()) {
            return new AppCell_STR(tm, r, c, ct);
        }
        if (tc.typeStrFile()) {
            return new AppCell_STRFILE(tm, r, c, ct);
        }
        if (tc.typeStrLnk()) {
            return new AppCell_STRLINK(tm, r, c, ct);
        }
        return TGS_FuncMTUUtils.thrw(d.className(), "createCell", "ct.getType().equals(TK_GWTPOJO_SQLColumnType.?");
    }
}
