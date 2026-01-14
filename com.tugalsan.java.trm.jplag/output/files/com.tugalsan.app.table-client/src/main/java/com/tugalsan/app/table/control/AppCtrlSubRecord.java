package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.tuple.client.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.sg.cell.*;
import com.tugalsan.app.table.sg.query.*;
import com.tugalsan.lib.rql.client.*;
import java.util.*;
import java.util.stream.*;

public class AppCtrlSubRecord {

    final private static TGC_Log d = TGC_Log.of(AppCtrlSubRecord.class);

    public AppCtrlSubRecord(AppModuleTable tm) {
        this.tm = tm;
    }
    final private AppModuleTable tm;

    public TGC_PopLblYesNoTextArea pop;
    final private TGC_Dimension dim = new TGC_Dimension(400, 400, true);

    public void createPops() {
        pop = new TGC_PopLblYesNoTextArea(
                dim, "<b>Bağlı Tabloda, Kayıt Önizleme</b>", "N/A", "Kapat",
                p -> {
                    //button not visible 
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getActiveCell());
                },
                null
        );
        pop.getPop().setVisible_focus = pop.btnEsc;
    }

    public void configInit() {
        pop.setEditable(false);
        pop.btnExe.setVisible(false);
    }

    final public void onClick_TableLinkSniff(AppCell_Abstract cell) {
        var ct = cell.ct;
        var errorAndTable = onClick_TableLinkSniff_returnErrorAndTable(ct);
        if (errorAndTable.value0 != null) {
            d.ce("onClick_TableLinkSniff.cell", errorAndTable.value0);
            return;
        }
        var tc = TGS_LibRqlColUtils.toSqlCol(ct);
        if (tc.typeLngLnk()) {
            var cellLink = (AppCell_LNGLINK) cell;
            var id = cellLink.getValueLong();
            onClick_TableLinkSniff_do(errorAndTable, id, cellLink);
            return;
        }
        if (tc.typeStrLnk()) {
            var sb = new StringBuilder();
            if (App.loginCard.userAdmin) {
                sb.append("SQL ADI: ").append(errorAndTable.value1.nameSql).append("\n");
                sb.append("\n");
            }
            sb.append("GROUP ADI: ").append(errorAndTable.value1.nameGroup).append("\n");
            sb.append("TABLO ADI: ").append(errorAndTable.value1.nameReadable).append("\n");
            sb.append("\n");
            var cellLink = (AppCell_STRLINK) cell;
            var val = cellLink.getValueString();
            var tags = TGS_StringUtils.gwt().toList_spc(val);
            var targetTable = errorAndTable.value1;
            sb.append(targetTable.getColumns().get(cell.colIdx).getColumnNameVisible()).append(":\n");
            if (tags.isEmpty()) {
                sb.append(" - '': {BOŞ}\n");
                tm.subRecord.pop.textArea.setText(sb.toString());
                tm.subRecord.pop.getPop().setVisible_beCeneteredAt(cell);
                return;
            }
            var values = new String[tags.size()];
            var idx_tag_ids = TGS_StreamUtils.toLst(
                    IntStream.range(0, values.length).mapToObj(i -> {
                        var tag = tags.get(i);
                        if ("*".equals(tag)) {
                            values[i] = TGS_StringUtils.cmn().concat(tag, ": {HEPSİ}");
                            onClick_TableLinkSniff_watchdog(cell, sb, values);
                            return null;
                        }
                        var id = TGS_CastUtils.toLong(tag).orElse(null);
                        if (tag == null) {
                            values[i] = TGS_StringUtils.cmn().concat(tag, ": {id sayıya çevirilemiyor hatası}");
                            onClick_TableLinkSniff_watchdog(cell, sb, values);
                            return null;
                        }
                        return new TAG_ID(values[i], i, tag, id);
                    })
            );
            TGC_SGWTCalller.async(new AppSGFCellGetList(tm.dbCfg, null, targetTable.nameSql, TGS_StreamUtils.toLst(idx_tag_ids.stream().map(o -> o.id))), r -> {
                IntStream.range(0, idx_tag_ids.size()).forEachOrdered(i -> {
                    var pck_input = idx_tag_ids.get(i);
                    var errorText = r.getOutput_errTexts().get(i);
                    var linkText = r.getOutput_linkTexts().get(i);
                    values[i] = TGS_StringUtils.cmn().concat(pck_input.tag, ": {", (errorText == null ? linkText : errorText), "}");
                });
                onClick_TableLinkSniff_watchdog(cell, sb, values);
            });
            return;
        }
        d.ce("onClick_TableLinkSniff", "ERROR: Column type error");
    }

    private class TAG_ID {

        public TAG_ID(String value, int i, String tag, Long id) {
            this.value = value;
            this.i = i;
            this.tag = tag;
            this.id = id;
        }

        final public String value;
        final public int i;
        final public String tag;
        final public Long id;
    }

    private void onClick_TableLinkSniff_watchdog(AppCell_Abstract cell, StringBuilder sb, CharSequence[] values) {
        var nonNullCount = Arrays.stream(values).filter(str -> str != null).count();
        if (nonNullCount != values.length) {
//            d.ce("onClick_TableLinkSniff_watchdog", "waiting...", values.length, "<>", nonNullCount);
            return;
        }
        Arrays.stream(values).forEachOrdered(value -> {
            sb.append(" - ").append(value).append("\n");
        });
        tm.subRecord.pop.textArea.setText(sb.toString());
        tm.subRecord.pop.getPop().setVisible_beCeneteredAt(cell);
    }

    final public void onClick_TableLinkSniff(UIObject optional_uiObject, TGS_LibRqlCol ct, long id) {
        var errorAndTable = onClick_TableLinkSniff_returnErrorAndTable(ct);
        if (errorAndTable.value0 != null) {
            d.ce("onClick_TableLinkSniff.ct", errorAndTable.value0);
            return;
        }
        onClick_TableLinkSniff_do(errorAndTable, id, optional_uiObject);
    }

    private void onClick_TableLinkSniff_do(TGS_Tuple2<String, TGS_LibRqlTbl> errorAndTable, long id, UIObject optional_uiObject) {
        var tn = errorAndTable.value1.nameSql;
        TGC_SGWTCalller.async(new AppSGFQueryPage(tm.dbCfg, tn, null, null, tn + ".LNG_ID = " + id, null, null, null), r -> {
            var columnsValues = r.getOutput_column_values();
            String text;
            if (columnsValues.isEmpty()) {
                text = "HATA: kolon id " + id + " bulunamadı  hatası!";
            } else if (columnsValues.get(0).size() != 1) {
                text = "HATA: satır sayısı 1 değil hatası! " + columnsValues.get(0).size();
            } else {
                var sb = new StringBuilder();
                if (App.loginCard.userAdmin) {
                    sb.append("SQL ADI: ").append(tn).append("\n");
                    sb.append("\n");
                }
                sb.append("GROUP ADI: ").append(errorAndTable.value1.nameGroup).append("\n");
                sb.append("TABLO ADI: ").append(errorAndTable.value1.nameReadable).append("\n");
                sb.append("\n");

                //TRANSPOSE AND SIMPLIFY ROW
                List<TGS_SQLCellAbstract> rowData = TGS_ListUtils.of();
                IntStream.range(0, columnsValues.size()).forEachOrdered(ci -> {
                    var columnRow = columnsValues.get(ci);
                    rowData.add(columnRow.get(0));
                });

                IntStream.range(0, rowData.size()).forEachOrdered(ci -> {
                    var cellValue = rowData.get(ci);
                    sb.append(errorAndTable.value1.getColumns().get(ci).getColumnNameVisible()).append(": ").append(cellValue.toString()).append("\n");
                });

                text = sb.toString();
            }
            tm.subRecord.pop.textArea.setText(text);
            tm.subRecord.pop.getPop().setVisible_beCeneteredAt(optional_uiObject);
        });
    }

    private TGS_Tuple2<String, TGS_LibRqlTbl> onClick_TableLinkSniff_returnErrorAndTable(TGS_LibRqlCol ct) {
        TGS_Tuple2<String, TGS_LibRqlTbl> pack = new TGS_Tuple2();
        var tc = TGS_LibRqlColUtils.toSqlCol(ct);
        if (!tc.groupLnk()) {
            pack.value0 = "HATA: Bu kolonun başka bir tabloya bağlantısı yok!";
        }
        var subTableName = ct.getDataString1_LnkTargetTableName();
        pack = isSubTableAllowed_returnErrorAndTable(subTableName);
        if (pack.value0 != null) {
            return pack;
        }
        if (App.loginCard.userNone) {
            pack.value0 = "HATA: Kullanıcı çıkışı algılandı!";
            return pack;
        }
        return pack;
    }

    public boolean onClick_TableLinkOpen(AppCell_Abstract cell) {
        if (Objects.equals(cell.ct.getType(), TGS_SQLColTypedUtils.TYPE_LNGLINK()) && cell.rowIdx != -1 /*<-BUG-FIX*/) {
            var cellLink = (AppCell_LNGLINK) cell;
            var cellIdStr = String.valueOf(cellLink.getValueLong());
            return onClick_TableLinkOpen(cell.ct, cellIdStr);
        }
        return onClick_TableLinkOpen(cell.ct, null);
    }

    public boolean onClick_TableLinkOpen(TGS_LibRqlCol ct, String optional_idStr) {
        var tc = TGS_LibRqlColUtils.toSqlCol(ct);
        if (!tc.groupLnk()) {
            d.ce("onClick_showSubTable", "HATA: Bu kolonun başka bir tabloya bağlantısı yok!");
            return false;
        }
        var subTableName = ct.getDataString1_LnkTargetTableName();
        var errorAndTable = isSubTableAllowed_returnErrorAndTable(subTableName);
        if (errorAndTable.value0 != null) {
            d.ce("onClick_showSubTable", errorAndTable.value0);
            return false;
        }
        AppModuleTableUtils.openNewTableModify(errorAndTable.value1, optional_idStr);
        return true;
    }

    public TGS_Tuple2<String, TGS_LibRqlTbl> isSubTableAllowed_returnErrorAndTable(String subTableName) {
        TGS_Tuple2<String, TGS_LibRqlTbl> pack = new TGS_Tuple2();
        if (App.loginCard.userNone) {
            pack.value0 = "HATA: Kullanıcı çıkışı algılandı!";
            return pack;
        }
        if (App.loginCard.userAdmin) {
            pack.value1 = App.tables.stream()
                    .filter(t -> Objects.equals(subTableName, t.nameSql))
                    .findAny().orElse(null);
            return pack;
        }
        pack.value1 = App.userTableConfig.stream()
                .map(cfg -> cfg.table)
                .filter(t -> Objects.equals(subTableName, t.nameSql))
                .findAny().orElse(null);
        if (pack.value1 == null) {
            pack.value0 = "HATA: Kullanıcı'nın bu alt tabloyu açma yetkisi yok! " + subTableName;
            return pack;
        }
        if (pack.value1 == null) {
            pack.value0 = "HATA: Alt tablo bulunamıyor hatası! " + subTableName;
            return pack;
        }
        return pack;
    }

}
