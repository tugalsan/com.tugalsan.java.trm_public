package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.key.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.math.client.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.app.table.sg.*;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.app.table.sg.rev.*;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.boot.client.*;
import com.tugalsan.lib.rql.client.*;
import java.util.*;
import java.util.stream.IntStream;
import com.tugalsan.api.gui.client.panel.*;

public class AppCtrlOperation {
    
    final private static TGC_Log d = TGC_Log.of(AppCtrlOperation.class);
    
    public AppCtrlOperation(AppModuleTable tm) {
        this.tm = tm;
    }
    private AppModuleTable tm;
    public PushButton btn;
    public TGC_PopLblYesNoListBox pop;
    
    public void configFocus() {
        TGC_FocusUtils.addKeyDown(btn, nativeKeyCode -> {
            TGS_FocusSides4 opFocusSides;
            if (tm.cells.isRowModifyVisible()) {
                opFocusSides = new TGS_FocusSides4(tm.cells.popRowModify.btnSubRecords, tm.report.btn, null, tm.cells.getPreferredActiveCell());
            } else {
                opFocusSides = new TGS_FocusSides4(tm.settings.btn, tm.report.btn, TGC_LibBootGUIBody.windowOperatorButton, tm.cells.getPreferredActiveCell());
            }
            TGC_FocusUtils.focusSide(btn, opFocusSides, nativeKeyCode);
        });
    }
    
    public void configLayout(HorizontalPanel p) {
        p.add(btn);
        btn.addStyleName(AppModuleTable.class.getSimpleName() + "_btn");
    }
    
    public void configActions() {
        TGC_ClickUtils.add(btn, () -> onOperations());
        TGC_KeyUtils.add(btn, () -> onOperations(), () -> {
            if (tm.cells.isRowModifyVisible()) {
                tm.cells.popRowModify.onClose.run(true);
            }
        });
    }
    
    public void configInit() {
        TGC_DOMUtils.setListBoxItemEnableAt(pop.listBox, 0, App.loginCard.userAdmin);
    }
    
    public void createPops() {
        pop = new TGC_PopLblYesNoListBox(
                new TGC_Dimension(TGC_PanelLayoutUtils.MAX_GRID_WIDTH(), null, true),
                TGS_ListUtils.of(
                        " İçeri (CSV) Aktar",//selectedIndex=0
                        " Tabloyu Dışarıya Aktar",//selectedIndex=1
                        tm.dbCfg.rev ? " Veriyi Göster (Geri Al)" : " Satırı Çoğalt"//selectedIndex=2
                ),
                "Tablo İşlemleri:", "Çalıştır", "İptal",
                p -> {
                    d.ci("createPops", "pop", "exe", "init");
                    p.getPop().setVisible(false);
                    var si = p.listBox.getSelectedIndex();
                    d.ci("createPops", "pop", "exe", "si", si);
                    d.ci("createPops", "pop", "exe", "tm==null", tm == null);
                    d.ci("createPops", "pop", "exe", "tm.cells==null", tm.cells == null);
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify==null", tm.cells.popRowModify == null);
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify.getPop()==null", tm.cells.popRowModify.getPop() == null);
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify.getPop().isVisible()", tm.cells.popRowModify.getPop().isVisible());
                    d.ci("createPops", "pop", "exe", "tm.cells.popRowModify.onClose==null", tm.cells.popRowModify.onClose == null);
                    if (tm.cells.isRowModifyVisible()) {
                        tm.cells.popRowModify.onClose.run(true);
                    }
                    d.ci("createPops", "pop", "exe", "swich_init");
                    switch (si) {
                        case -1:
                            d.ce("Listeden bir işlem seçilmedi hatası!");
                            break;
                        case 0:
                            showImport();//İÇERİ AKTAR
                            break;
                        case 1:
                            executeExport();//DIŞARI AKTAR
                            break;
                        case 2:
                            d.ci("createPops", "pop", "exe", "swich_case 2");
                            if (tm.dbCfg.rev) {
                                showBackupInfo();//SHOW BACKUP
                            } else {
                                multiplyRow();//COGALT
                            }
                            break;                        
                        default:
                            d.ce("init", "Listeden seçilen işlem bulunamadı hatası! (" + si + ")");
                    }
                    d.ci("createPops", "pop", "exe", "swich_end");
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btn);
                },
                null, () -> TGC_FocusUtils.setFocusAfterGUIUpdate(pop.btnEsc)
        );
    }
    
    public void createWidgets() {
        btn = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_AID_KIT(), "İşlemler");
    }
    
    public void onOperations() {
        pop.getPop().setVisible(true);
    }
    
    public void showImport() {
        tm.input.showArea(
                "",
                afterInput_importCSV,
                btn, //fullscreen
                TGC_PopLblYesNoTextArea.MAX_CHAR_SQL_BLOB(),
                "<b>CSV Girdisi:</b>"
        );
    }
    
    public TGS_FuncMTU_In1<String> afterInput_importCSV = inputText -> {
        d.ci("afterInput_importCSV", "initilizing importCSV.....");
        d.ci("afterInput_importCSV", "parsing lines...");
        var lines = TGS_StringUtils.gwt().toList(inputText, "\n");
        d.ci("afterInput_importCSV", "parsing datas ...");
        var datas = TGS_StringUtils.gwt().toList(lines.get(0), ";");
        List<Integer> columnIndexes = TGS_ListUtils.of();
        d.ci("afterInput_importCSV", "filling columnIndexes...");
        for (var i = 0; i < datas.size(); i++) {//NO STREAM: HAS RETURN!
            d.ci("afterInput_importCSV", "for columnIndexes " + datas.get(i));
            columnIndexes.add(TGS_CastUtils.toInteger(datas.get(i)).orElse(null));
            if (columnIndexes.get(i) == null) {
                d.ce("afterInput_importCSV", "Kolon index okuma hatası; datas.get(" + i + ") =" + datas.get(i));
                return;
            }
            d.ci("afterInput_importCSV", "columnIndexes parsed. " + i);
        }
        AppCtrlCellRowUtils.createNewRow(tm.dbCfg, tm.curTable, row -> executeImport2(tm, ((TGS_SQLCellLNG) row.get(0)).getValueLong(), lines, datas, 1, columnIndexes));
        TGC_FocusUtils.setFocusAfterGUIUpdate(btn);
    };
    
    private void executeImport2(AppModuleTable tm, long newId, final List<String> lines, final List<String> datas, final int lineI, final List<Integer> columnIndexes) {
        d.ci("executeImport2", "starting import. " + lineI);
        
        if (lineI == lines.size()) {
            d.ci("executeImport2", "import finalized successfully.");
            tm.page.update(null, false, true);
            return;
        }
        var line = lines.get(lineI);
        d.ci("executeImport2", "line", line);
        TGS_StringUtils.gwt().toList(line, datas, ";");
        d.ci("executeImport2", "parsedData", TGS_StringUtils.cmn().toString(datas, ","));
        if (datas.size() != tm.cells.getColumnSize()) {
            d.ce("executeImport2", "ColumnSizeWarning: cs:" + tm.cells.getColumnSize() + ", ds:" + datas.size());
        }
        if (datas.size() != columnIndexes.size()) {
            d.ce("executeImport2", "CSV columnIndexes boyut hatası ci:" + columnIndexes.size() + ", ds:" + datas.size());
            return;
        }
        var row = TGS_LibRqlTblUtils.newRow(tm.curTable, newId);
        Long l;
        String s;
        for (var ci = 0; ci < columnIndexes.size(); ci++) {
            var c = columnIndexes.get(ci);
            if (row.get(c) instanceof TGS_SQLCellLNG sqlCellLng) {
                l = TGS_CastUtils.toLong(datas.get(ci)).orElse(null);
                if (l == null) {
                    d.ce("executeImport2", "Error casting to long,lineI:'" + lineI + "', datas.get(" + ci + ") : '" + datas.get(ci) + "'");
                    return;
                }
                if (tm.curTable.columns.get(c).getType().equals(TGS_SQLColTypedUtils.TYPE_LNGDOUBLE())) {
                    l = TGS_MathUtils.double2Long(l, tm.curTable.columns.get(c).getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision());
                }
                sqlCellLng.imitateValueLong(l);
            } else if (row.get(c) instanceof TGS_SQLCellSTR sqlCellStr) {
                s = datas.get(ci);
                if (s == null) {
                    d.ce("executeImport2.a", "Error getting string,lineI:'" + lineI + "', datas.get(" + ci + ") : '" + datas.get(ci) + "'");
                    return;
                }
                sqlCellStr.imitateValueString(s);
            } else if (row.get(c) instanceof TGS_SQLCellBYTESSTR sqlCellBytesStr) {
                s = datas.get(ci);
                if (s == null) {
                    d.ce("executeImport2.b", "Error getting string,lineI:'" + lineI + "', datas.get(" + ci + ") : '" + datas.get(ci) + "'");
                    return;
                }
                sqlCellBytesStr.imitateValueString(s);
            } else {
                d.ce("executeImport2", "uncoded type: " + row.get(c).toString());
                return;
            }
        }
        d.cr("executeImport2", "satir hafızaya alındı.");
        d.ci("executeImport2", "id=" + ((TGS_SQLCellLNG) row.get(0)).getValueLong() + " satırı ekleme kodu çalıştırılıyor...");
        AppCtrlCellRowUtils.insertNewRow(tm, row, null);
        executeImport2(tm, newId + 1, lines, datas, lineI + 1, columnIndexes);
    }
    
    public void executeExport() {
        var cfg = App.userTableConfig.stream()
                .filter(_cfg -> _cfg.table.nameSql.equals(App.curTableName))
                .findAny().orElse(null);
        if (cfg == null) {
            d.ce("multiplyRow", "cfg == null");
            btn.setEnabled(false);
            return;
        }
        if (cfg.editableDays == 0) {
            d.ce("executeExport", "HATA: Sayfa salt okunur!");
            return;
        }
        
        d.cr("executeExport", "Aktarma işlemi başladı... (BİLGİ: Aktarmayı durdurmak için sayfayı numarasını, sırasını veya filitresini değiştirebilirsiniz)");
        var f = new AppSGFExportExcel(
                tm.dbCfg, tm.curTable.tableOrder, tm.cells.isColHidden, tm.curTable,
                tm.filter.getWhereStmt(),
                tm.filter.getOrderByStmt(),
                tm.filter.join.aramaJoinConfig,
                tm.filter.join.aramaJoinValue
        );
        d.ci("executeExport", f);
        TGC_SGWTCalller.async(f, r -> {
            if (r.getOutput_url() == null) {
                d.ce("executeExport", "Tablo dışarıya aktarılken bir HATA oluştu. (BİLGİ: Sayfayı dışarı aktarırken sayfayı numarası, sırası veya filitresi ile oynayamazsınız; bu bilgiyi tablou dışarı aktarmayı durdurmak için de kullanabilirsiniz) (r.getOutput_url() == null)");
            } else {
                d.cr("executeExport", "Yeni sayfada görüntüleniyor...", r.getOutput_url());
                TGC_BrowserWindowUtils.openNew(TGS_Url.of(r.getOutput_url()));
                TGC_LibBootGUISoundStatic.play();
            }
        });
        d.ci("executeExport", "called");
    }
    
    public void multiplyRow() {
        var cfg = App.userTableConfig.stream()
                .filter(_cfg -> _cfg.table.nameSql.equals(App.curTableName))
                .findAny().orElse(null);
        if (cfg == null) {
            d.ce("multiplyRow", "cfg == null");
            btn.setEnabled(false);
            return;
        }
        if (cfg.editableDays == 0) {
            d.ce("showBackupInfoOrMultiplyRow", "HATA: Sayfa salt okunur!");
            return;
        }
        
        d.ci("multiplyRow", "#1");
        var activeCell = tm.cells.getActiveCell();
        if (activeCell == null) {
            d.ce("showBackupInfoOrMultiplyRow", "HATA: Önce coğaltmak istediğiniz satırdan bir hücre seçiniz!");
            return;
        }
        d.ci("multiplyRow", "#2");
        if (activeCell.rowIdx == AppCtrlCell.HEADER_ROW_IDX) {
            d.ce("multiplyRow", "HATA: Önce coğaltmak istediğiniz satırdan bir hücre seçiniz! (Not: Başlık çoğaltılamaz!)");
            return;
        }
        d.ci("multiplyRow", "#3");
        var rowId = tm.cells.getActiveRowId();
        d.ci("multiplyRow", "#4");
        if (rowId < 1) {
            d.ce("multiplyRow", "HATA: Satır id 1 den küçük olamaz hatası");
            return;
        }
        d.ci("multiplyRow", "#5");
        tm.input.showMultiply(
                String.valueOf(MULTIPLACATION_ROW_COUNT_MIN_MAX.value0),
                actionAfter_rowMultiply,
                activeCell,
                "Kaç Adet Satır(id:" + rowId + "):",
                TGS_IconUtils.CLASS_FIRST(), "Min", String.valueOf(MULTIPLACATION_ROW_COUNT_MIN_MAX.value0),
                TGS_IconUtils.CLASS_LAST(), "Mak", String.valueOf(MULTIPLACATION_ROW_COUNT_MIN_MAX.value1)
        );
        d.ci("multiplyRow", "#8");
    }
    
    public void showBackupInfo() {
        d.ci("showBackupInfo", "backup tablosu algılandı...");
        var cb = tm.cells.getActiveCell();
        if (cb == null) {
            d.ce("showBackupInfo", "HATA1: Önce görmek istediğiniz satırdan bir hücre seçiniz!");
            return;
        }
        d.ci("showBackupInfo", "secim algılandı...");
        var rowToRestore = cb.rowIdx;
        if (rowToRestore < 0) {
            d.ce("showBackupInfo", "HATA2: Önce görmek istediğiniz satırdan bir hücre seçiniz!");
            return;
        }
        d.ci("showBackupInfo", "satır algılandı...");
        var cell = (AppCell_LNG) tm.cells.rows.get(rowToRestore).get(0);
        d.ci("showBackupInfo", "id hücresi seçildi...");
        var idToSniff = cell.getValueLong();
        if (idToSniff < 1l) {
            d.ce("showBackupInfo", "HATA: Satır id 1 den küçük olamaz hatası");
            return;
        }
        d.ci("showBackupInfo", "backup satır id:" + idToSniff);
        tm.cells.restoreRefId = ((AppCell_LNG) tm.cells.rows.get(rowToRestore).get(1)).getValueLong();
        d.ci("showBackupInfo", "ref id satır id:" + tm.cells.restoreRefId);
        
        var tn = tm.curTable.nameSql;
        TGC_SGWTCalller.async(new AppSGFRevGetRowData(tn, idToSniff), r -> {
            tm.cells.restoreData = r.getOutput_row();
            if (tm.cells.restoreData == null) {
                d.ce("showBackupInfo", "ERROR: VERİ okunamadı hatası!");
            } else {
                var curTableUnTouced = App.tbl_mayThrow(tn);
                tm.cells.popRowRestoreOperations.setTextArea("[ -- " + curTableUnTouced.nameGroup + " > " + curTableUnTouced.nameReadable + " (" + curTableUnTouced.nameSql + ")" + " -- ]");
                IntStream.range(0, tm.cells.restoreData.size()).forEachOrdered(i -> {
                    var title = "\nKolon: ";
                    if (i < curTableUnTouced.columns.size()) {
                        var col = curTableUnTouced.columns.get(i);
                        title += col.getColumnNameVisible() + " (" + tn + "." + col.getColumnName() + ")";
                    } else {
                        title += "(" + tn + ".[?])";
                    }
                    title += "\n";
                    tm.cells.popRowRestoreOperations.append(title);
                    tm.cells.popRowRestoreOperations.append(String.valueOf(tm.cells.restoreData.get(i) + "\n"));
                });
                tm.cells.popRowRestoreOperations.getPop().setVisible(true);
            }
        });
    }
    
    public TGS_FuncMTU_In1<String> actionAfter_rowMultiply = inputText -> {
        TGS_FuncMTCUtils.run(() -> {
            var rowId = tm.cells.getActiveRowId();
            var multiplyCount = TGS_CastUtils.toInteger(inputText).orElse(null);
            if (multiplyCount == null || multiplyCount < MULTIPLACATION_ROW_COUNT_MIN_MAX.value0 || multiplyCount > MULTIPLACATION_ROW_COUNT_MIN_MAX.value1) {
                d.ce("actionAfter_rowMultiply", "HATA: sayı " + MULTIPLACATION_ROW_COUNT_MIN_MAX.value0 + " ile " + MULTIPLACATION_ROW_COUNT_MIN_MAX.value1 + " arasında olamalıydı!");
                tm.cells.focusActiveCell();
                return;
            }
            var activeRowIdx = tm.cells.getActiveRowIdx();
            d.ci("actionAfter_rowMultiply", "activeRowIdx", activeRowIdx);
            var activeRow = tm.cells.rows.get(activeRowIdx);
            var disableBYTESSTR = false;
            if (disableBYTESSTR) {
                for (var ci = 0; ci < activeRow.size(); ci++) {
                    var cn = tm.curTable.columns.get(ci).getColumnName();
                    var cell = activeRow.get(ci);
                    if (cell instanceof AppCell_STR cellStr) {
                        var cellVal = cellStr.getValueString();
                        if (cellVal.startsWith("BYTESSTR ")) {
                            d.ci("actionAfter_rowMultiply", "ci", ci, cn, cellVal, "detected");
                            var cnv = tm.curTable.columns.get(ci).getColumnNameVisible();
                            d.ce("actionAfter_rowMultiply", "Satır içinde " + cnv + " kolonunda uzun yazı bulunduğu için çoğaltılamıyor. Henüz kodu yazılmamış. Uzun yazıyı kısaltıp tekrar deneyin");
                            return;
                        } else {
                            d.ci("actionAfter_rowMultiply", "ci", ci, cn, cellVal, "skipped");
                        }
                    } else {
                        d.ci("actionAfter_rowMultiply", "skipCell", cn);
                    }
                }
            }
            TGC_SGWTCalller.async(new AppSGFRowMultiply(tm.dbCfg, tm.curTable, rowId, multiplyCount, AppCtrlInput.multiply_emptyDatesAndHours, AppCtrlInput.multiply_emptyColumn_onRowClone), r -> {
                if (!r.getOutput_result()) {
                    d.ce("actionAfter_rowMultiply", "HATA Ekleme başarısız!");
                    tm.cells.focusActiveCell();
                    return;
                }
                d.ci("actionAfter_rowMultiply", "#88");
                if (r.getOutput_result()) {
                    d.cr("actionAfter_rowMultiply", "SONUÇ: Satır başarı ile çoğaltıldı.");
                } else {
                    d.ce("actionAfter_rowMultiply", "HATA: Satır çoğaltma başarısız!");
                }
                tm.page.update(null, false, true);
                tm.cells.focusActiveCell();
            });
            tm.cells.focusActiveCell();
        });
    };
    final public static TGS_Tuple2<Long, Long> MULTIPLACATION_ROW_COUNT_MIN_MAX = new TGS_Tuple2(1L, 10L);
}
