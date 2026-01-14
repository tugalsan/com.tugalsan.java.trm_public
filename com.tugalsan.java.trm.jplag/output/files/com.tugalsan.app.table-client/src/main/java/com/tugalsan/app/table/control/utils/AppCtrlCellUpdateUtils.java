package com.tugalsan.app.table.control.utils;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.gui.client.browser.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.math.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.stream.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.*;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.app.table.sg.path.*;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.app.table.sg.row.*;
import com.tugalsan.lib.rql.txt.client.*;
import com.tugalsan.lib.table.client.*;
import java.util.*;
import java.util.stream.*;

public class AppCtrlCellUpdateUtils {

    private AppCtrlCellUpdateUtils() {

    }

    final private static TGC_Log d = TGC_Log.of(AppCtrlCellUpdateUtils.class);

    public static void date(AppModuleTable tm, String inpuText) {
        var oldPageNr = tm.page.pageNrCurrent;
        if (tm.dbCfg.isAny() && !App.loginCard.userAdmin) {
            d.ce("date", "revizyon tablosunda değişiklik yapılamaz hatası!");
            return;
        }
        var cell = tm.cells.getActiveCell();
        var id = tm.cells.getActiveRowId();
        var cn = tm.curTable.columns.get(cell.colIdx).getColumnName();
        d.ci("date", "btn", cell.getClass().getSimpleName(), "ri", cell.rowIdx, "ci", cell.colIdx, "id", id, "cn", cn);

        var cellDate = (AppCell_LNGDATE) cell;
        d.ci("date", "cell instanceof TMCellButton_LNGDATE");
        var inputDate = TGS_Time.ofDate_D_M_Y(inpuText);
        if (inputDate == null) {
            d.ce("date", "UYARI: Geçersiz giriş tespit edildi; hücre içerik değiştirme işlemi durduruldu!");
            return;
        }
        if (!inputDate.isProperDate_zeroDayOrMonthIsProper()) {
            d.ce("date", "UYARI: Geçersiz tarih tespit edildi; hücre içerik değiştirme işlemi durduruldu!", inputDate.toString_dateOnly());
            return;
        }
        d.ci("date", "inputDate:", inputDate.toString_dateOnly());
        TGC_SGWTCalller.async(new AppSGFCellUpdateLNG(tm.dbCfg, tm.curTable, id, cn, inputDate.getDate()), r -> {
            if (!r.getOutput_result()) {
                d.ce("date", "Bir hata oluştu. CODE:" + AppSGFCellUpdateLNG.class.getSimpleName());
                return;
            }
            d.cr("date", "değişiklik yapıldı");
            if (oldPageNr != tm.page.pageNrCurrent) {
                return;
            }
            cellDate.setValueDate(inputDate);
        });
    }

    public static void time(AppModuleTable tm, String inpuText) {
        var oldPageNr = tm.page.pageNrCurrent;
        if (tm.dbCfg.isAny() && !App.loginCard.userAdmin) {
            d.ce("time", "revizyon tablosunda değişiklik yapılamaz hatası!");
            return;
        }
        var cell = tm.cells.getActiveCell();
        var id = tm.cells.getActiveRowId();
        var cn = tm.curTable.columns.get(cell.colIdx).getColumnName();
        d.ci("time", "btn", cell.getClass().getSimpleName(), "ri", cell.rowIdx, "ci", cell.colIdx, "id", id, "cn", cn);

        var cellTime = (AppCell_LNGTIME) cell;
        var inputTime = TGS_Time.ofTime_HH_MM(inpuText);
        if (inputTime == null) {
            d.ce("time", "UYARI: Geçersiz giriş tespit edildi; hücre içerik değiştirme işlemi durduruldu!");
            return;
        }
        TGC_SGWTCalller.async(new AppSGFCellUpdateLNG(tm.dbCfg, tm.curTable, id, cn, inputTime.getTime()), r -> {
            if (!r.getOutput_result()) {
                d.ce("time", "Bir hata oluştu. CODE:" + AppSGFCellUpdateLNG.class.getSimpleName());
                return;
            }
            d.cr("time", "değişiklik yapıldı");
            if (oldPageNr != tm.page.pageNrCurrent) {
                return;
            }
            cellTime.setValueTime(inputTime);
        });
    }

    public static void floating(AppModuleTable tm, String inpuText) {
        var oldPageNr = tm.page.pageNrCurrent;
        if (tm.dbCfg.isAny() && !App.loginCard.userAdmin) {
            d.ce("floating", "revizyon tablosunda değişiklik yapılamaz hatası!");
            return;
        }
        var cell = tm.cells.getActiveCell();
        var id = tm.cells.getActiveRowId();
        var cn = tm.curTable.columns.get(cell.colIdx).getColumnName();
        d.ci("floating", "btn", cell.getClass().getSimpleName(), "ri", cell.rowIdx, "ci", cell.colIdx, "id", id, "cn", cn);

        var cellDouble = (AppCell_LNGDOUBLE) cell;
        inpuText = inpuText.replace(',', '.');
        var inputDouble = TGS_CastUtils.toDouble(inpuText).orElse(null);
        if (inputDouble == null) {
            d.ce("floating", "UYARI: Geçersiz giriş tespit edildi; hücre içerik değiştirme işlemi durduruldu!");
            return;
        }
        d.ci("floating", "value: " + inputDouble);
        var inputLong = (long) TGS_MathUtils.double2Long(inputDouble, ((AppCell_LNGDOUBLE) cell).getCommaInt());
        d.ci("floating", "is converted to: " + inputLong);
        TGC_SGWTCalller.async(new AppSGFCellUpdateLNG(tm.dbCfg, tm.curTable, id, cn, inputLong), r -> {
            if (!r.getOutput_result()) {
                d.ce("floating", "Bir hata oluştu. CODE:" + AppSGFCellUpdateLNG.class.getSimpleName());
                return;
            }
            d.cr("floating", "değişiklik yapıldı");
            if (oldPageNr != tm.page.pageNrCurrent) {
                return;
            }
            cellDouble.setValueDouble(inputDouble);
        });
    }

    public static void number(AppModuleTable tm, String inpuText) {
        var oldPageNr = tm.page.pageNrCurrent;
        if (tm.dbCfg.isAny() && !App.loginCard.userAdmin) {
            d.ce("number", "revizyon tablosunda değişiklik yapılamaz hatası!");
            return;
        }
        var cell = tm.cells.getActiveCell();
        var id = tm.cells.getActiveRowId();
        var cn = tm.curTable.columns.get(cell.colIdx).getColumnName();
        d.ci("number", "btn", cell.getClass().getSimpleName(), "ri", cell.rowIdx, "ci", cell.colIdx, "id", id, "cn", cn);

        var cellLng = (AppCell_LNG) cell;
        var inputLong = TGS_CastUtils.toLong(inpuText).orElse(null);
        if (inputLong == null) {
            d.ce("number", "UYARI: Geçersiz giriş tespit edildi; hücre içerik değiştirme işlemi durduruldu!");
            return;
        }
        TGC_SGWTCalller.async(new AppSGFCellUpdateLNG(tm.dbCfg, tm.curTable, id, cn, inputLong), r -> {
            if (!r.getOutput_result()) {
                d.ce("number", "Bir hata oluştu. CODE:" + AppCell_LNGLINK.class.getSimpleName());
                return;
            }
            d.cr("number", "değişiklik yapıldı");
            if (oldPageNr != tm.page.pageNrCurrent) {
                return;
            }
            if (cellLng instanceof AppCell_LNGLINK cellLngLink) {
                cellLngLink.setValueLong(inputLong);
                return;
            }
            cellLng.setValueLong(inputLong);//ITS OK
        });
    }

    public static void file(AppModuleTable tm) {
        if (tm.dbCfg.isAny() && !App.loginCard.userAdmin) {
            d.ce("file", "revizyon tablosunda değişiklik yapılamaz hatası!");
            return;
        }
        var cell = tm.cells.getActiveCell();
        var id = tm.cells.getActiveRowId();
        var cn = tm.curTable.columns.get(cell.colIdx).getColumnName();
        d.ci("file", "btn", cell.getClass().getSimpleName(), "ri", cell.rowIdx, "ci", cell.colIdx, "id", id, "cn", cn);

        if (tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX1_TMPL_UPLOAD()
                || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX5_FILE_UPLOAD()) {
            var tn = tm.curTable.nameSql;
            var rowId = tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX1_TMPL_UPLOAD() ? 0 : id;
            var suffix = tm.curTable.columns.get(tm.cells.getActiveColIdx()).getDataString1_LnkTargetTableName();
            tm.cells.popFileUpload.reConfigure(tn, cn, rowId, suffix);
            tm.cells.popRowModify.imageHandler.bufferUnSet(tn, rowId);
            tm.cells.popFileUpload.getPop().setVisible(true);
        } else if (tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX0_TMPL_SHOWLAST()
                || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX2_TMPL_SHOWREV()
                || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX4_FILE_SHOWLAST()
                || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX6_FILE_SHOWREV()
                || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX7_FILE_DELREV()) {
            d.ci("file", ". Dosya adları çekiliyor...");
            var fileId = (tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX0_TMPL_SHOWLAST()
                    || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX2_TMPL_SHOWREV()) ? 0 : id;
            var copyTempIfNeeded = tm.cells.popFileOperationsIdx != AppCtrlCell.FILE_MENU_IDX7_FILE_DELREV();

            TGC_SGWTCalller.async(new AppSGFPathInBoxGetFileNames(tm.curTable, tm.cells.getActiveColIdx(), fileId, copyTempIfNeeded), r -> {
                if (r.getOutput_names() == null) {
                    d.ce("file", "ERROR: fileGetInBoxFileList returns null");
                    return;
                }
                d.ci("file", "run", "cellValueUpdate", "TMCellButton_STRFILE", r.getOutput_names());
                if (r.getOutput_names().isEmpty()) {
                    d.ce("file", "HATA: Dosya sayısı 0 hatası, dosyasının varlığını kontrol edin.");
                    return;
                }
                if (d.infoEnable) {
                    for (var i = 0; i < r.getOutput_names().size(); i++) {
                        d.ci("file", "run", "cellValueUpdate", AppSGFPathInBoxGetFileNames.class.getSimpleName(), "dosyaadı[" + i + "] = [" + r.getOutput_names().get(i) + "]");
                    }
                }
                if (tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX0_TMPL_SHOWLAST()
                        || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX4_FILE_SHOWLAST()) {
                    var filteredList = TGS_StreamUtils.toLst(r.getOutput_names().stream().filter(nm -> !nm.contains(".JPG.")));
                    var filename = filteredList.isEmpty()
                            ? r.getOutput_names().get(r.getOutput_names().size() - 1)
                            : filteredList.get(filteredList.size() - 1);
                    var table_tablename = tm.curTable.nameSql;
                    d.ci("file", "table_urls,file_tablename,file_columnname", table_tablename, cn);
                    TGC_SGWTCalller.async(new AppSGFPathHttpInboxFileUrl(table_tablename, cn, filename), resp -> {
                        if (resp.getOutput_url() == null) {
                            d.ce("file", "Dosya yolu çekme hatası!");
                        } else {
                            d.ci("file", "cellValueUpdate", "TMCellButton_STRFILE", AppSGFPathHttpInboxFileUrl.class.getSimpleName(), "resp.getOutput_url()", resp.getOutput_url(), "filename", filename);
                            TGC_BrowserWindowUtils.openNew(TGS_Url.of(resp.getOutput_url()));
                            d.cl("file", "[" + filename + "] Dosyayı tekrar indirmek için , tıklayınız.", resp.getOutput_url());
                        }
                    });
                } else if (tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX2_TMPL_SHOWREV()
                        || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX6_FILE_SHOWREV()
                        || tm.cells.popFileOperationsIdx == AppCtrlCell.FILE_MENU_IDX7_FILE_DELREV()) {
                    tm.cells.popFileChooser.listBox.clear();
                    r.getOutput_names().forEach(s -> tm.cells.popFileChooser.listBox.addItem(s));
                    if (Objects.equals(tm.cells.popFileOperationsIdx, AppCtrlCell.FILE_MENU_IDX2_TMPL_SHOWREV())) {
                        tm.cells.popFileChooser.label.setText("Şablon Revizyonunu Göster:");
                    } else if (Objects.equals(tm.cells.popFileOperationsIdx, AppCtrlCell.FILE_MENU_IDX6_FILE_SHOWREV())) {
                        tm.cells.popFileChooser.label.setText("Dosya Revizyonunu Göster:");
                    } else if (Objects.equals(tm.cells.popFileOperationsIdx, AppCtrlCell.FILE_MENU_IDX7_FILE_DELREV())) {
                        tm.cells.popFileChooser.label.setText("Dosya Revizyonunu Sil:");
                    }
                    tm.cells.popFileChooser.getPop().setVisible(true);
                }
            });
        }
    }

    //Noval as bytesstr
    public static void noval(AppModuleTable tm, String inpuText) {
        var oldPageNr = tm.page.pageNrCurrent;
        if (tm.dbCfg.isAny() && !App.loginCard.userAdmin) {
            d.ce("noval", "revizyon tablosunda değişiklik yapılamaz hatası!");
            return;
        }
        var inpuTextFixed = inpuText.replace("'", "\"");//JAVASCRIPT FIX
        var cell = tm.cells.getActiveCell();
        var id = tm.cells.getActiveRowId();
        var cn = tm.curTable.columns.get(cell.colIdx).getColumnName();
        d.ci("noval", "btn", cell.getClass().getSimpleName(), "ri", cell.rowIdx, "ci", cell.colIdx, "id", id, "cn", cn);

        d.ci("noval", "Değişiklik yapılıyor, lütfen bekleyin ... (TMCellButton_BYTESSTR)");
        TGC_SGWTCalller.async(new AppSGFCellUpdateBytesSTR(tm.dbCfg, tm.curTable, id, cn, inpuTextFixed), r -> {
            if (!r.getOutput_result()) {
                d.ce("noval", "HATA: Bir Hata oluştu. TK_GSF_SQLCellUpdateBytesSTR CODEERROR:909");
                return;
            }
            d.cr("noval", "Değişiklik yapıldı");
            if (oldPageNr != tm.page.pageNrCurrent) {
                return;
            }
            ((AppCell_BYTESSTR) cell).setValueString(inpuTextFixed);
        });
    }

    public static void text(AppModuleTable tm, String inpuText) {
        var oldPageNr = tm.page.pageNrCurrent;
        if (tm.dbCfg.isAny() && !App.loginCard.userAdmin) {
            d.ce("text", "revizyon tablosunda değişiklik yapılamaz hatası!");
            return;
        }
        inpuText = inpuText.replace("'", "\"");//JAVASCRIPT FIX
        var cell = tm.cells.getActiveCell();
        var id = tm.cells.getActiveRowId();
        var cn = tm.curTable.columns.get(cell.colIdx).getColumnName();
        d.ci("text", "btn", cell.getClass().getSimpleName(), "ri", cell.rowIdx, "ci", cell.colIdx, "id", id, "cn", cn);

        var cellSTR = (AppCell_STR) cell;
        d.ci("text", "cellValueUpdate", "TMCellButton_STR");
        if (inpuText.length() < cellSTR.getMaxChar() && inpuText.startsWith("BYTESSTR")) {
            if (!App.loginCard.userAdmin) {
                d.ce("text", "Sadece admin yönlendirme yapabilir.");
                return;
            }
            for_textRedirect_editByAdmin(tm, cellSTR, id, cn, inpuText, oldPageNr);
            return;
        }
        if (inpuText.length() < cellSTR.getMaxChar()) {
            for_text_short_decideAccording2ExensionId(tm, cellSTR, id, cn, inpuText, oldPageNr);
            return;
        }
        //if (inpuText.length() >= cellSTR.getMaxChar()...
        if (tm.dbCfg.isAny()) {
            d.ce("text", "SUB tabloda uzun yazı kullanılamaz. Kırma uygulandı!");
            inpuText = inpuText.substring(0, cellSTR.getMaxChar());
            for_text_short_decideAccording2ExensionId(tm, cellSTR, id, cn, inpuText, oldPageNr);
            return;
        }
        for_text_long_decideAccordion2ItsExtensionId(tm, cellSTR, id, cn, inpuText, oldPageNr);
    }

    private static void for_textRedirect_editByAdmin(AppModuleTable tm, AppCell_STR cellSTR, long id, String columnname, String inpuText, int oldPageNr) {
        d.ci("for_textRedirect_editByAdmin", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR");
        if (!App.loginCard.userAdmin) {
            d.ci("for_textRedirect_editByAdmin", "Hücre içinde, BYTESSTR takısı ile başlayan değişiklik uygulanmadı!");
        }
        d.ci("for_textRedirect_editByAdmin", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "isUserAdmin", App.loginCard.userAdmin);
        var p = TGS_StringUtils.gwt().toList_spc(inpuText);
        if (p.size() != 2) {
            d.ce("for_textRedirect_editByAdmin", "p.size 2 != " + p.size());
            return;
        }
        if (!"BYTESSTR".equals(p.get(0))) {
            d.ce("for_textRedirect_editByAdmin", "!\"BYTESSTR\".equals(p.get(0))");
            return;
        }
        var pId = TGS_CastUtils.toLong(p.get(1)).orElse(null);
        if (pId == null) {
            d.ce("for_textRedirect_editByAdmin", "pId == null");
            return;
        }
        TGC_SGWTCalller.async(new AppSGFCellUpdateSTR(tm.dbCfg, tm.curTable, id, columnname, inpuText), r -> {
            if (!r.getOutput_result()) {
                d.ce("for_textRedirect_editByAdmin", "Bir hata oluştu. CODE:" + AppSGFCellUpdateSTR.class.getSimpleName());
                return;
            }
            d.cr("for_textRedirect_editByAdmin", "değişiklik yapıldı");
            if (oldPageNr != tm.page.pageNrCurrent) {
                return;
            }
            cellSTR.setValueString(inpuText);
            cellSTR.extendedId = pId;
        });
    }

    private static void for_text_short_decideAccording2ExensionId(AppModuleTable tm, AppCell_STR cellSTR, long id, String columnname, String inpuText, int oldPageNr) {
        d.ci("for_text_short_decideAccording2ExensionId", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "inpuText.length() < cellSTR.getMaxChar()", inpuText.length() < cellSTR.getMaxChar());
        if (cellSTR.extendedId == null) {
            for_text_short_noId_updateTextShort(tm, cellSTR, id, columnname, inpuText, oldPageNr);
            return;
        }
        for_text_short_hasextendedId_updateShortTextThenRemoveExtension(tm, cellSTR, id, columnname, inpuText, oldPageNr);
    }

    private static void for_text_short_noId_updateTextShort(AppModuleTable tm, AppCell_STR cellSTR, long id, String columnname, String inpuText, int oldPageNr) {
        d.ci("for_text_short_noId_updateTextShort", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "alreadySimple", "cellSTR.extendedId == null");
        TGC_SGWTCalller.async(new AppSGFCellUpdateSTR(tm.dbCfg, tm.curTable, id, columnname, inpuText), r -> {
            if (!r.getOutput_result()) {
                d.ce("for_text_short_noId_updateTextShort", "Bir hata oluştu. CODE:TK_GSF_SQLCellUpdateSTR_19");
                return;
            }
            d.cr("for_text_short_noId_updateTextShort", "değişiklik yapıldı");
            if (oldPageNr != tm.page.pageNrCurrent) {
                return;
            }
            cellSTR.setValueString(inpuText);
        });
    }

    private static void for_text_short_hasextendedId_updateShortTextThenRemoveExtension(AppModuleTable tm, AppCell_STR cellSTR, long id, String columnname, String inputText, int oldPageNr) {
        d.ci("for_text_short_hasextendedId_updateShortTextThenRemoveExtension", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "alreadySimple", "cellSTR.extendedId != null");
        TGC_SGWTCalller.async(new AppSGFCellUpdateSTR(tm.dbCfg, tm.curTable, id, columnname, inputText), r1 -> {
            if (!r1.getOutput_result()) {
                d.ce("for_text_short_hasextendedId_updateShortTextThenRemoveExtension", "Bir hata oluştu. CODE:" + AppSGFCellUpdateSTR.class.getSimpleName() + "_18");
                return;
            }
            TGC_SGWTCalller.async(new AppSGFRowRemove(new TGS_LibTableDbSub().txt(), tm.curTable, cellSTR.extendedId), r2 -> {
                if (!r2.getOutput_result()) {
                    d.ce("for_text_short_hasextendedId_updateShortTextThenRemoveExtension", "r.getOutput_result() = " + r2.getOutput_result());
                    return;
                }
                d.cr("for_text_short_hasextendedId_updateShortTextThenRemoveExtension", "değişiklik yapıldı");
                if (oldPageNr == tm.page.pageNrCurrent) {
                    cellSTR.setValueString(inputText);
                    cellSTR.extendedId = null;
                }
            });
        });
    }

    private static void for_text_long_decideAccordion2ItsExtensionId(AppModuleTable tm, AppCell_STR cellSTR, long id, String columnname, String inpuText, int oldPageNr) {
        d.ci("for_text_long_decideAccordion2ItsExtensionId", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "inpuText.length() > cellSTR.getMaxChar()", inpuText.length() > cellSTR.getMaxChar());
        d.ci("for_text_long_decideAccordion2ItsExtensionId", "Değişiklik yapılıyor, lütfen bekleyin ... (TMCellButton_STR)");
        if (cellSTR.extendedId == null) {//get a new Id first
            for_text_long_noId_addExentionAndUpdateShort(tm, cellSTR, id, columnname, oldPageNr, inpuText);
            return;
        }
        for_text_long_hasId_decideAccrodion2ExistanceOfExtension(tm, cellSTR, id, columnname, oldPageNr, inpuText);
    }

    private static void for_text_long_noId_addExentionAndUpdateShort(AppModuleTable tm, AppCell_STR cellSTR, long id, String columnname, int oldPageNr, String inpuText) {
        d.ci("for_text_long_noId_addExentionAndUpdateShort", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "cellSTR.extendedId == null");
        var txtParamVal = TGS_StringUtils.cmn().concat(columnname, ".", String.valueOf(id));
        var tn = tm.curTable.nameSql;
        var txt = new TGS_LibTableDbSub().txt();
        AppCtrlCellRowUtils.createNewRow(txt, tm.curTable, txtRow -> {
            d.ci("for_text_long_noId_addExentionAndUpdateShort", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "cellSTR.extendedId == null", "txtRow", txtRow);
            ((TGS_SQLCellSTR) txtRow.get(1)).imitateValueString(txtParamVal);
            TGC_SGWTCalller.async(new AppSGFRowAdd(txt, tn, txtRow), r1 -> {
                if (!r1.getOutput_result()) {
                    d.ce("for_text_long_noId_addExentionAndUpdateShort", "HATA Ekleme başarısız!!");
                    return;
                }
                cellSTR.extendedId = ((TGS_SQLCellLNG) txtRow.get(0)).getValueLong();
                var simplifiedText = "BYTESSTR " + cellSTR.extendedId;
                TGC_SGWTCalller.async(new AppSGFCellUpdateSTR(tm.dbCfg, tm.curTable, id, columnname, simplifiedText), r2 -> {
                    if (!r2.getOutput_result()) {
                        d.ce("for_text_long_noId_addExentionAndUpdateShort", "text_long_hasId_checked", "Bir hata oluştu. CODE:TK_GSF_SQLCellUpdateSTR_17");
                        return;
                    }
                    if (tm.page.pageNrCurrent == oldPageNr) {
                        cellSTR.setValueString(simplifiedText);
                    }
                    for_text_long_hasId_NotEmpty_updateTheExtension(tm, cellSTR, inpuText);
                });
            });
        });
    }

    private static void for_text_long_hasId_decideAccrodion2ExistanceOfExtension(AppModuleTable tm, AppCell_STR cellSTR, long id, String columnname, int oldPageNr, String inpuText) {
        d.ci("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "TMCellButton_STR", "BYTESSTR", "cellSTR.extendedId != null");
        var tn = tm.curTable.nameSql;
        var where = tn + ".LNG_ID = '" + cellSTR.extendedId + "'";
        d.cr("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "Hücre detayı okunuyor...");
        TGC_SGWTCalller.async(new AppSGFQueryPage(new TGS_LibTableDbSub().txt(), tn, null, null, where, null, null, null), resp -> {
            var columnValues = resp.getOutput_column_values();
            if (columnValues.size() < 3) {//COL COUNT
                d.ce("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "HATA: CHECK_REF: resultSet1.size() < 3 as size:" + columnValues.size(), where);
                return;
            }
            d.ci("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "TXT rowId", cellSTR.extendedId);
            if (d.infoEnable) {
                IntStream.range(0, columnValues.size()).forEachOrdered(i -> d.ci("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "columnData", i, columnValues.get(i)));
            }
            d.cr("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "Hücre detayı okundu.");
            if (columnValues.get(0).isEmpty()) {//ROWSIZE
                d.ci("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "columnData.isEmpty()");
                d.ce("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "UYARI: TXT rowId ", cellSTR.extendedId, " bulunamadı!");
                d.cr("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "YAMA: Yama yapılıyor...");
                cellSTR.extendedId = null;
                for_text_long_noId_addExentionAndUpdateShort(tm, cellSTR, id, columnname, oldPageNr, inpuText);
                return;
            }
            d.ci("for_text_long_hasId_decideAccrodion2ExistanceOfExtension", "!columnData.isEmpty()");
            for_text_long_hasId_NotEmpty_updateTheExtension(tm, cellSTR, inpuText);
        });
    }

    private static void for_text_long_hasId_NotEmpty_updateTheExtension(AppModuleTable tm, AppCell_STR cellSTR, String inpuText) {
        d.ci("for_text_long_hasId_NotEmpty_updateTheExtension", "Düzeltme için ön kontroller yapıldı");
        d.ci("for_text_long_hasId_NotEmpty_updateTheExtension", "cellValueUpdate", "TMCellButton_STR", "BYTESSTR", "cellSTR.extendedId != null", "sqlUpdateCellBytesSTR");
        var txtValCn = TGS_LibRqlTxtUtils.cols().get(2).columnName;
        TGC_SGWTCalller.async(new AppSGFCellUpdateBytesSTR(new TGS_LibTableDbSub().txt(), tm.curTable, cellSTR.extendedId, txtValCn, inpuText), r1 -> {
            if (!r1.getOutput_result()) {
                d.ce("for_text_long_hasId_NotEmpty_updateTheExtension", "Bir hata oluştu. TK_GSF_SQLCellUpdateBytesSTR CODE:1167");
                return;
            }
            d.cr("for_text_long_hasId_NotEmpty_updateTheExtension", "Değişiklik yapıldı");
        });
    }

}
