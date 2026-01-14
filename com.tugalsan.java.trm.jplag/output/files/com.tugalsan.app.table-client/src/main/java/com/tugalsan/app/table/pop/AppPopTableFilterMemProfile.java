package com.tugalsan.app.table.pop;

import com.tugalsan.api.log.client.TGC_Log;
import com.tugalsan.api.log.client.TGS_Log;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.client.TGS_StringUtils;
import java.util.ArrayList;
import java.util.List;

public class AppPopTableFilterMemProfile {

    final private static TGC_Log d = TGC_Log.of(AppPopTableFilterMemProfile.class);

    public AppPopTableFilterMemProfile(int ci) {
        this(ci, ci == 0, false, false, false, false, "", "", "", "");
    }

    public AppPopTableFilterMemProfile(
            int ci,
            boolean rbSortBy,
            boolean cbAscending,
            boolean cbActive,
            boolean cbNegative,
            boolean cbNull,
            String btnBySelected,
            String tfByContent,
            String btnMin,
            String btnMax) {
        this.ci = ci;
        this.rbSortBy = rbSortBy;
        this.cbAscending = cbAscending;
        this.cbActive = cbActive;
        this.cbNegative = cbNegative;
        this.cbNull = cbNull;
        this.btnBySelected = btnBySelected;
        this.tfByContent = tfByContent;
        this.btnMin = btnMin;
        this.btnMax = btnMax;
    }
    public final int ci;
    public boolean rbSortBy;
    public boolean cbAscending;
    public boolean cbActive;
    public boolean cbNegative;
    public boolean cbNull;
    public String btnBySelected;
    public String tfByContent;
    public String btnMin;
    public String btnMax;

    private final static String TAG_rbSortBy = "rbSortBy ";
    private final static String TAG_cbAscending = "cbAscending ";
    private final static String TAG_cbActive = "cbActive ";
    private final static String TAG_cbNegative = "cbNegative ";
    private final static String TAG_cbNull = "cbNull ";
    private final static String TAG_btnBySelected = "btnBySelected ";
    private final static String TAG_tfByContent = "tfByContent ";
    private final static String TAG_btnMin = "btnMin ";
    private final static String TAG_btnMax = "btnMax ";

    @Override
    public String toString() {
        return AppPopTableFilterMemProfile.class.getSimpleName() + "{" + "ci=" + ci + ", rbSortBy=" + rbSortBy + ", cbAscending=" + cbAscending + ", cbActive=" + cbActive + ", cbNegative=" + cbNegative + ", cbNull=" + cbNull + ", btnBySelected=" + btnBySelected + ", tfByContent=" + tfByContent + ", btnMin=" + btnMin + ", btnMax=" + btnMax + '}';
    }

    public List<String> toLst() {
        List<String> lst = new ArrayList();
        var defaultRbSort = ci == 0;
        if (!cbActive && rbSortBy == defaultRbSort && cbAscending == false) {
            return lst;
        }
        if (rbSortBy != defaultRbSort) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_rbSortBy, String.valueOf(rbSortBy)));
        }
        if (cbAscending) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_cbAscending, String.valueOf(cbAscending)));
        }
        if (!cbActive) {
            return lst;
        }
        lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_cbActive, String.valueOf(cbActive)));
        if (cbNegative) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_cbNegative, String.valueOf(cbNegative)));
        }
        if (cbNull) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_cbNull, String.valueOf(cbNull)));
        }
        btnBySelected = btnBySelected.trim();
        if (TGS_StringUtils.cmn().isPresent(btnBySelected)) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_btnBySelected, btnBySelected));
        }
        tfByContent = tfByContent.trim();
        if (TGS_StringUtils.cmn().isPresent(tfByContent)) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_tfByContent, tfByContent));
        }
        btnMin = btnMin.trim();
        if (TGS_StringUtils.cmn().isPresent(btnMin)) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_btnMin, btnMin));
        }
        btnMax = btnMax.trim();
        if (TGS_StringUtils.cmn().isPresent(btnMax)) {
            lst.add(TGS_StringUtils.cmn().concat(String.valueOf(ci), " ", TAG_btnMax, btnMax));
        }
        return lst;
    }

    public boolean loadAll(List<String> lst) {
        var tag_startsWith = ci + " ";
        List<String> data = TGS_StreamUtils.toLst(lst.stream().filter(line -> line.startsWith(tag_startsWith)));
        if (data.isEmpty()) {
            return false;
        }
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "tag_startsWith", tag_startsWith);
        }
        var tag_startsWithLen = tag_startsWith.length();
        var mappedData = TGS_StreamUtils.toLst(data.stream().map(line -> line.substring(tag_startsWithLen)));
        if (d.infoEnable) {
            mappedData.forEach(line -> {
                d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "mappedData", line);
            });
        }
        rbSortBy = mappedData.stream().filter(line -> line.startsWith(TAG_rbSortBy)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_rbSortBy.length()))).orElse(ci == 0);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "rbSortBy", rbSortBy);
        }
        cbAscending = mappedData.stream().filter(line -> line.startsWith(TAG_cbAscending)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbAscending.length()))).orElse(false);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "cbAscending", cbAscending);
        }
        cbActive = mappedData.stream().filter(line -> line.startsWith(TAG_cbActive)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbActive.length()))).orElse(false);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "cbActive", cbActive);
        }
        cbNegative = mappedData.stream().filter(line -> line.startsWith(TAG_cbNegative)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbNegative.length()))).orElse(false);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "cbNegative", cbNegative);
        }
        cbNull = mappedData.stream().filter(line -> line.startsWith(TAG_cbNull)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbNull.length()))).orElse(false);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "cbNull", cbNull);
        }
        btnBySelected = mappedData.stream().filter(line -> line.startsWith(TAG_btnBySelected)).findAny().map(val -> val.substring(TAG_btnBySelected.length())).orElse("");
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "btnBySelected", btnBySelected);
        }
        tfByContent = mappedData.stream().filter(line -> line.startsWith(TAG_tfByContent)).findAny().map(val -> val.substring(TAG_tfByContent.length())).orElse("");
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "tfByContent", tfByContent);
        }
        btnMin = mappedData.stream().filter(line -> line.startsWith(TAG_btnMin)).findAny().map(val -> val.substring(TAG_btnMin.length())).orElse("");
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "btnMin", btnMin);
        }
        btnMax = mappedData.stream().filter(line -> line.startsWith(TAG_btnMax)).findAny().map(val -> val.substring(TAG_btnMax.length())).orElse("");
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(),"loadAll", "btnMax", btnMax);
        }
        return true;
    }

    public static AppPopTableFilterMemProfile ofLst(List<String> lst, int _ci) {
        var tag_startsWith = _ci + " ";
        List<String> data = TGS_StreamUtils.toLst(lst.stream().filter(line -> line.startsWith(tag_startsWith)));
        if (data.isEmpty()) {
            return null;
        }
        var tag_startsWithLen = tag_startsWith.length();
        var mappedData = TGS_StreamUtils.toLst(data.stream().map(line -> line.substring(tag_startsWithLen)));
        var _rbSortBy = mappedData.stream().filter(line -> line.startsWith(TAG_rbSortBy)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_rbSortBy.length()))).orElse(false);
        var _cbAscending = mappedData.stream().filter(line -> line.startsWith(TAG_cbAscending)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbAscending.length()))).orElse(false);
        var _cbActive = mappedData.stream().filter(line -> line.startsWith(TAG_cbActive)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbActive.length()))).orElse(false);
        var _cbNegative = mappedData.stream().filter(line -> line.startsWith(TAG_cbNegative)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbNegative.length()))).orElse(false);
        var _cbNull = mappedData.stream().filter(line -> line.startsWith(TAG_cbNull)).findAny().map(val -> Boolean.valueOf(val.substring(TAG_cbNull.length()))).orElse(false);
        var _btnBySelected = mappedData.stream().filter(line -> line.startsWith(TAG_btnBySelected)).findAny().map(val -> val.substring(TAG_btnBySelected.length())).orElse("");
        var _tfByContent = mappedData.stream().filter(line -> line.startsWith(TAG_tfByContent)).findAny().map(val -> val.substring(TAG_tfByContent.length())).orElse("");
        var _btnMin = mappedData.stream().filter(line -> line.startsWith(TAG_btnMin)).findAny().map(val -> val.substring(TAG_btnMin.length())).orElse("");
        var _btnMax = mappedData.stream().filter(line -> line.startsWith(TAG_btnMax)).findAny().map(val -> val.substring(TAG_btnMax.length())).orElse("");
        return new AppPopTableFilterMemProfile(_ci, _rbSortBy, _cbAscending, _cbActive, _cbNegative, _cbNull, _btnBySelected, _tfByContent, _btnMin, _btnMax);
    }

}
