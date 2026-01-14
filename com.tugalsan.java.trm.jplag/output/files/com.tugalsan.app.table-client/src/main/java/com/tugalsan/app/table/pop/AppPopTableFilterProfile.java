package com.tugalsan.app.table.pop;

import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.charset.client.TGS_CharSet;
import com.tugalsan.api.gui.client.theme.*;
import com.tugalsan.api.gui.client.widget.table.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.math.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.app.table.cell.*;
import com.tugalsan.app.table.control.utils.AppCtrlCellLayoutUtils;
import com.tugalsan.lib.rql.client.*;

public class AppPopTableFilterProfile {

    final private static TGC_Log d = TGC_Log.of(AppPopTableFilterProfile.class);

    public final AppPopTableFilter pop;
    public TGS_LibRqlCol ct;
    public boolean tbRenderable;
    private boolean bbytes, blink, blink2, bstr, bstrFile, blng;
    public boolean isDate;
    public boolean isTime;
    public final int ci;//SAVE
    public boolean rbSortBy;//SAVE
    public boolean cbAscending;//SAVE
    public boolean cbActive;//SAVE
    public boolean cbNegative;//SAVE
    public boolean cbNull;//SAVE
    public String btnBySelected;//SAVE
    public String tfByContent;//SAVE
    public String btnMin;//SAVE
    public String btnMax;//SAVE
    public String initMinMaxValue;
    public String initListBoxItemText;
    public String htmlColumnNameTitle;

    public AppPopTableFilterProfile(AppPopTableFilter pop, int colSqlIdx) {
        this.pop = pop;
        this.ci = colSqlIdx;
        this.ct = pop.tm.curTable.columns.get(colSqlIdx);
        htmlColumnNameTitle = "<b>Seçilen Kolon: </b>" + pop.tm.curTable.columns.get(colSqlIdx).getColumnNameVisible();
        initListBoxItemText = pop.lb.getItemText(colSqlIdx);
        resetProfile();
    }

    public boolean loadFromMem(AppPopTableFilterMemProfile memItem) {
        if (memItem.ci != ci) {
            return false;
        }
        rbSortBy = memItem.rbSortBy;
        cbAscending = memItem.cbAscending;
        cbActive = memItem.cbActive;
        cbNegative = memItem.cbNegative;
        cbNull = memItem.cbNull;
        btnBySelected = memItem.btnBySelected;
        tfByContent = memItem.tfByContent;
        btnMin = memItem.btnMin;
        btnMax = memItem.btnMax;
        renderPopLb();
        return true;
    }

    public AppPopTableFilterMemProfile toMemItem() {
        return new AppPopTableFilterMemProfile(ci, rbSortBy, cbAscending, cbActive, cbNegative, cbNull, btnBySelected, tfByContent, btnMin, btnMax);
    }

    public final void resetProfile() {
        rbSortBy = ci == 0;
        cbAscending = false;
        cbActive = false;
        AppCtrlCellLayoutUtils.isRenderable_clear();
        tbRenderable = AppCtrlCellLayoutUtils.isRenderable(pop.tm, ci, true);
        cbNegative = false;
        cbNull = false;
        isDate = TGS_SQLColTypedUtils.TYPE_LNGDATE().equals(ct.getType());
        isTime = TGS_SQLColTypedUtils.TYPE_LNGTIME().equals(ct.getType());
        btnBySelected = "";
        tfByContent = "";
        var tc = TGS_LibRqlColUtils.toSqlCol(ct);
        if (tc.typeLngDate()) {
            initMinMaxValue = TGS_Time.toString_dateOnly_today();
        } else if (tc.typeLngTime()) {
            initMinMaxValue = TGS_Time.toString_timeOnly_now_simplified();
        } else if (tc.typeLngDbl()) {
            initMinMaxValue = "0.0";
        } else {
            initMinMaxValue = "0";
        }
        btnMin = initMinMaxValue;
        btnMax = initMinMaxValue;
        bbytes = ct.getType().startsWith(TGS_SQLColTypedUtils.TYPE_BYTES());
        blink = ct.getType().startsWith(TGS_SQLColTypedUtils.TYPE_LNGLINK());
        blink2 = ct.getType().startsWith(TGS_SQLColTypedUtils.TYPE_STRLINK());
        bstr = ct.getType().equals(TGS_SQLColTypedUtils.TYPE_STR());
        bstrFile = ct.getType().equals(TGS_SQLColTypedUtils.TYPE_STRFILE());
        blng = ct.getType().startsWith(TGS_SQLColTypedUtils.TYPE_LNG());
        renderPopLb();
    }

    public void renderPopProfile_and_lb() {
        pop.rbSortBy.setVisible(true);
        pop.cbAscending.setVisible(true);
        pop.cbActive.setVisible(!bstrFile);
        pop.cbNegative.setVisible(cbActive);
        pop.cbNull.setVisible(cbActive && bbytes);
        pop.lblByContent.setVisible(cbActive && bstr);
        pop.tfByContent.setVisible(cbActive && bstr);
        pop.lblBySelected.setVisible(cbActive && (blink || blink2));
        pop.btnBySelected.setVisible(cbActive && (blink || blink2));
        pop.lblMin.setVisible(cbActive && (blng && !blink));
        pop.lblMax.setVisible(cbActive && (blng && !blink));
        pop.btnMin.setVisible(cbActive && (blng && !blink));
        pop.btnMax.setVisible(cbActive && (blng && !blink));
        pop.cbAscending.setVisible(cbAscending);

        pop.rbSortBy.setValue(rbSortBy);
        pop.cbAscending.setVisible(rbSortBy);
        pop.cbAscending.setValue(cbAscending);
        pop.cbActive.setValue(cbActive);
        pop.cbNegative.setValue(cbNegative);
        pop.cbNull.setValue(cbNull);
        pop.tfByContent.setText(tfByContent);
        pop.btnBySelected.setText(btnBySelected);
        pop.btnMin.setText(btnMin);
        pop.btnMax.setText(btnMax);

        if (isDate) {
            pop.popMinDate.dateBox.getTextBox().setText(btnMin);
            pop.popMaxDate.dateBox.getTextBox().setText(btnMax);
            pop.popMinDate.btnAddShowAs(
                    TGS_IconUtils.CLASS_CALENDAR(), "Bugün şQ",
                    TGS_IconUtils.CLASS_RADIO_UNCHECKED(), "Sıfırla şW",
                    TGS_IconUtils.CLASS_MINUS(), "Geri şA",
                    TGS_IconUtils.CLASS_PLUS(), "İleri şS"
            );
            pop.popMaxDate.btnAddShowAs(
                    TGS_IconUtils.CLASS_CALENDAR(), "Bugün şQ",
                    TGS_IconUtils.CLASS_RADIO_UNCHECKED(), "Sıfırla şW",
                    TGS_IconUtils.CLASS_MINUS(), "Geri şA",
                    TGS_IconUtils.CLASS_PLUS(), "İleri şS"
            );
        } else if (isTime) {
            pop.popMinText.textBox.setText(btnMin);
            pop.popMaxText.textBox.setText(btnMax);
            pop.popMinText.btnAddShowAs(
                    TGS_IconUtils.CLASS_CLOCK(), "Şimdi",
                    TGS_IconUtils.CLASS_RADIO_UNCHECKED(), "Sıfırla"
            );
            pop.popMaxText.btnAddShowAs(
                    TGS_IconUtils.CLASS_CLOCK(), "Şimdi",
                    TGS_IconUtils.CLASS_RADIO_UNCHECKED(), "Sıfırla"
            );
        } else {
            pop.popMinText.textBox.setText(btnMin);
            pop.popMaxText.textBox.setText(btnMax);
            pop.popMaxText.btnAddHide(true, true);
        }

        pop.lblSelectedColumnNameVisible.setHTML(htmlColumnNameTitle);
        renderPopLb();
    }

    public void renderPopLb() {
        if (pop == null) {
            d.ce("renderPopLb", "ERROR: filter gui update skipped!", "pop == null");
            return;
        }
        if (pop.tm == null) {
            d.ce("renderPopLb", "ERROR: filter gui update skipped!", "pop.tm == null ");
            return;
        }
        if (pop.tm.cells == null) {
            d.ce("renderPopLb", "ERROR: filter gui update skipped!", "pop.tm.cells == null ");
            return;
        }
        if (pop.tm.cells.headers == null) {
            d.ce("renderPopLb", "ERROR: filter gui update skipped!", "pop.tm.cells.headers == null ");
            return;
        }
        if (pop.tm.cells.headers == null) {
            d.ce("renderPopLb", "ERROR: filter gui update skipped!", "pop.tm.cells.headers == null ");
            return;
        }
        if (ci < 0) {
            d.ce("renderPopLb", "ERROR: filter gui update skipped!", "ci < 0", ci);
            return;
        }
        if (ci >= pop.tm.cells.headers.size()) {
            d.ce("renderPopLb", "ERROR: filter gui update skipped!", "ci >= pop.tm.cells.headers.size()", ci, pop.tm.cells.headers.size());
            return;
        }
        var cell = (AppCell_STR) pop.tm.cells.headers.get(ci);
        if (cbActive) {
            cell.setStyleName(TGC_PanelRed.class.getSimpleName());
        } else {
            cell.setStyleName(TGC_TableHeaderStyled.class.getSimpleName());
        }
        var tagRenderable = tbRenderable ? TGS_CharSet.cmn().UTF8_HOURGLASS() : "";
        var tagSortDirection = cbAscending ? TGS_CharSet.cmn().UTF8_SORTASC() : TGS_CharSet.cmn().UTF8_SORTDESC();
        var tagSortDirectionIfSortabale = rbSortBy ? tagSortDirection : "";
        var tagNegative = cbNegative ? TGS_CharSet.cmn().UTF8_CROSS() : "";
        var tagNegativeOrActive = cbActive ? (TGS_CharSet.cmn().UTF8_FILTER() + tagNegative) : "";
        pop.lb.setItemText(ci, tagRenderable + tagSortDirectionIfSortabale + tagNegativeOrActive + " " + initListBoxItemText);
    }

    public String getWhereStmt() {
        if (!cbActive) {
            return null;
        }
        var curTableName = pop.tm.curTable.nameSql;
        var tn_dot_cn = curTableName + "." + pop.tm.curTable.columns.get(ci).getColumnName();
        var tc = TGS_LibRqlColUtils.toSqlCol(ct);
        if (tc.typeBytes()) {
            if (cbNegative ? !cbNull : cbNull) {//IF NULL
                return "(" + "LENGTH(" + tn_dot_cn + ") <= " + TGS_LibRqlCol.BLOB_EMPTY_SIZE() + " OR " + tn_dot_cn + " = NULL" + ")";
            }
            return "LENGTH(" + tn_dot_cn + ") > " + TGS_LibRqlCol.BLOB_EMPTY_SIZE();
        }
        if (tc.typeStr()) {
            var parsedTokens = TGS_StringUtils.gwt().toList_spc(tfByContent);
            if (parsedTokens.size() < 1) {
                cbActive = false;
                renderPopProfile_and_lb();
                d.ce("getWhereStmt", "HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (" + tn_dot_cn + "), [parsedTokens.size() = " + parsedTokens.size() + "]");
                return null;
            }
            var sb = new StringBuilder("(");
            for (var i = 0; i < parsedTokens.size(); i++) {
                sb.append("UCASE(").append(tn_dot_cn).append(")").append(cbNegative ? " NOT" : "").append(" LIKE UCASE('%").append(parsedTokens.get(i)).append("%')");
                if (i != parsedTokens.size() - 1) {
                    sb.append(cbNegative ? " AND " : " OR ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
        if (tc.groupLnk()) {
            var parsedTokens = TGS_StringUtils.gwt().toList_spc(btnBySelected);
            if (parsedTokens.size() < 1) {
                cbActive = false;
                renderPopProfile_and_lb();
                d.ce("getWhereStmt", "HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (" + tn_dot_cn + "), [parsedTokens.size() = " + parsedTokens.size() + "]");
                return null;
            }
            var parsedIntegers = TGS_ListUtils.of();
            for (var s : parsedTokens) {
                var i = TGS_CastUtils.toInteger(s).orElse(null);
                if (i == null) {
                    d.ce("HATA: Girdi hatası: Tanımlanamayan sayı süzerken çalıştırılmayacak!!! -> '" + s + "'");
                    continue;
                }
                parsedIntegers.add(i);
            }
            if (parsedIntegers.isEmpty()) {
                d.ce("getWhereStmt", "HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (Liste boş hatası)");
                return null;
            }

            var sb = new StringBuilder("(");
            for (var i = 0; i < parsedIntegers.size(); i++) {
                var pi = parsedIntegers.get(i);
                if (ct.getType().equals(TGS_SQLColTypedUtils.TYPE_STRLINK())) {
                    sb.append("(");
                    sb.append(tn_dot_cn).append(cbNegative ? " <> " : " = '").append(pi).append("'");
                    sb.append(cbNegative ? " AND " : " OR ");
                    sb.append(tn_dot_cn).append(cbNegative ? " NOT LIKE " : " LIKE '").append(pi).append(" %'");
                    sb.append(cbNegative ? " AND " : " OR ");
                    sb.append(tn_dot_cn).append(cbNegative ? " NOT LIKE " : " LIKE '% ").append(pi).append("'");
                    sb.append(cbNegative ? " AND " : " OR ");
                    sb.append(tn_dot_cn).append(cbNegative ? " NOT LIKE " : " LIKE '% ").append(pi).append(" %'");
                    sb.append(")");
                } else {
                    sb.append(tn_dot_cn).append(cbNegative ? " <> " : " = ").append(pi);
                }
                if (i != parsedTokens.size() - 1) {
                    sb.append(cbNegative ? " AND " : " OR ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
        if (tc.typeLng()) {
            var min = TGS_CastUtils.toLong(btnMin).orElse(null);
            var max = TGS_CastUtils.toLong(btnMax).orElse(null);
            if (min == null || max == null) {
                cbActive = false;
                renderPopProfile_and_lb();
                d.ce("getWhereStmt", "HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (" + tn_dot_cn + "), [" + min + "], [" + max + "]");
                return null;
            }
            if (cbNegative) {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " < ", min.toString(), " OR ", tn_dot_cn, " > ", max.toString(), " )");
            } else {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " >= ", min.toString(), " AND ", tn_dot_cn, " <= ", max.toString(), " )");
            }
        }
        if (tc.typeLngDbl()) {
            var min = TGS_CastUtils.toDouble(btnMin).orElse(null);
            if (min == null) {
                cbActive = false;
                renderPopProfile_and_lb();
                d.ce("getWhereStmt", "HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (" + tn_dot_cn + "), [min:" + min + "]");
                return null;
            }
            var max = TGS_CastUtils.toDouble(btnMax).orElse(null);
            if (max == null) {
                cbActive = false;
                renderPopProfile_and_lb();
                d.ce("getWhereStmt", "HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (" + tn_dot_cn + "), [max:" + max + "]");
                return null;
            }
            if (cbNegative) {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " < ", String.valueOf(TGS_MathUtils.double2Long(min, ct.getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision())), " OR ", tn_dot_cn, " > ", String.valueOf(TGS_MathUtils.double2Long(max, ct.getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision())), " )");
            } else {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " >= ", String.valueOf(TGS_MathUtils.double2Long(min, ct.getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision())), " AND ", tn_dot_cn, " <= ", String.valueOf(TGS_MathUtils.double2Long(max, ct.getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision())), " )");
            }
        }
        if (tc.typeLngDate()) {
            var min = TGS_Time.ofDate_D_M_Y(btnMin);
            var max = TGS_Time.ofDate_D_M_Y(btnMax);
            if (min == null || max == null) {
                cbActive = false;
                renderPopProfile_and_lb();
                d.ce("HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (" + tn_dot_cn + "), [" + min + "], [" + max + "]");
                return null;
            }
            if (cbNegative) {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " < ", String.valueOf(min.getDate()), " OR ", tn_dot_cn, " > ", String.valueOf(max.getDate()), " )");
            } else {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " >= ", String.valueOf(min.getDate()), " AND ", tn_dot_cn, " <= ", String.valueOf(max.getDate()), " )");
            }
        }
        if (tc.typeLngTime()) {
            var min = TGS_Time.ofTime_HH_MM(btnMin);
            var max = TGS_Time.ofTime_HH_MM(btnMax);
            if (min == null || max == null) {
                cbActive = false;
                renderPopProfile_and_lb();
                d.ce("getWhereStmt", "HATA: Girdi hatasi yakalandı, kolon filtresi atlanarak devam edilecek! (" + tn_dot_cn + "), [" + min + "], [" + max + "]");
                return null;
            }
            if (cbNegative) {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " < ", String.valueOf(min.getTime()), " OR ", tn_dot_cn, " > ", String.valueOf(max.getTime()), " )");
            } else {
                return TGS_StringUtils.cmn().concat("( ", tn_dot_cn, " >= ", String.valueOf(min.getTime()), " AND ", tn_dot_cn, " <= ", String.valueOf(max.getTime()), " )");
            }
        }
        return null;
    }

}
