package com.tugalsan.app.table.pop;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.cast.client.*;
import com.tugalsan.api.charset.client.TGS_CharSetLocaleTypes;
import com.tugalsan.api.gui.client.click.*;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.key.*;
import com.tugalsan.api.gui.client.panel.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.theme.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.shape.client.*;
import com.tugalsan.api.sql.col.typed.client.*;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.time.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.*;
import com.tugalsan.app.table.control.utils.AppCtrlCellLayoutUtils;
import com.tugalsan.lib.rql.client.*;
import java.util.*;
import java.util.stream.*;

public class AppPopTableFilter implements TGC_PopInterface {

    final private static TGC_Log d = TGC_Log.of(AppPopTableFilter.class);
    public List<AppPopTableFilterProfile> profiles = new ArrayList();
    public AppPopTableFilterMemProfiles profiles_last = null;
    public List<AppPopTableFilterMemProfiles> profilesListCustom = new ArrayList();

    @Override
    public void createWidgets() {
        profiles_last = new AppPopTableFilterMemProfiles("last",
                TGS_StreamUtils.toLst(
                        IntStream.range(0, tm.curTable.columns.size())
                                .mapToObj(ci -> new AppPopTableFilterMemProfile(ci))
                )
        );

        lblMin = new HTML("Min:");
        lblMax = new HTML("Max:");
        lblBySelected = new HTML("Seçilene göre:");
        lblByContent = new HTML("İçeriğe göre:");
        lblSelectedColumnNameVisible = new HTML("N/A");
        tfByContent = new TextBox();

        btnCloseAndCancel = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CROSS(), "İptal");
        btnCloseAndApply = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_CHECKMARK(), "Uygula");
        btnReset = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_SPINNER11(), "Başlangıç Ayarları");
        btnCustom = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_FILTER(), "Kayıtlı Süzgeçler");
        btnJoin = TGC_ButtonUtils.createIcon(TGS_IconUtils.CLASS_LINK(), "Ek İlişki Ayarla");

        rbSortBy = TGC_RadioButtonUtils.createIcon("GROUP_SORT", TGS_IconUtils.CLASS_SORT_ALPHA_DESC(), "Bu kolona göre sırala:");
        cbAscending = TGC_CheckBoxUtils.createIcon(TGS_IconUtils.CLASS_SORT_ALPHA_ASC(), "Artan şekilde");

        cbActive = TGC_CheckBoxUtils.createIcon(TGS_IconUtils.CLASS_FILTER(), "Kolon süzgeci aktif?");
        cbNegative = TGC_CheckBoxUtils.createIcon(TGS_IconUtils.CLASS_EYE_BLOCKED(), "Olumsuz yap");

        btnBySelected = new PushButton("");
        btnMin = new PushButton("");
        btnMax = new PushButton("");
        cbNull = TGC_CheckBoxUtils.createIcon(TGS_IconUtils.CLASS_FILE_EMPTY(), "Boşsa?");

        lb = TGC_ListBoxUtils.create(false);
    }
    public HTML lblMin, lblMax, lblSelectedColumnNameVisible;
    public HTML lblBySelected, lblByContent;
    public TextBox tfByContent;
    public PushButton btnCloseAndCancel, btnCloseAndApply, btnReset, btnCustom, btnJoin, btnBySelected, btnMin, btnMax;
    public RadioButton rbSortBy;
    public CheckBox cbAscending, cbActive, cbNegative, cbNull;
    public ListBox lb;

    @Override
    public void createPops() {
        var dimBtn2 = new TGC_Dimension(180, 102, true);
        var dimBtn4 = new TGC_Dimension(180, 140, true);
        popFilterLINKBySelected = new AppPopTableFilterLINK(tm, btnBySelected);
        popMinText = new TGC_PopLblYesNoTextBoxExtraBtn2(dimBtn2, lblMin.getText(), "Uygula", "İptal",
                p -> {
                    var inputText = p.textBox.getText();
                    var pr = profiles.get(lb.getSelectedIndex());
                    var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
                    if (tc.typeLng()) {
                        var lng = TGS_CastUtils.toLong(inputText.trim()).orElse(null);
                        if (lng == null) {
                            d.ce("createPops", "popMinText", "lng", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMin = lng.toString();
                    } else if (tc.typeLngDbl()) {
                        var dbl = TGS_CastUtils.toDouble(inputText.trim()).orElse(null);
                        if (dbl == null) {
                            d.ce("createPops", "popMinText", "dbl", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMin = dbl.toString();
                    } else if (tc.typeLngDate()) {
                        var date = TGS_Time.ofDate_D_M_Y(inputText.trim());
                        if (date == null) {
                            d.ce("createPops", "popMinText", "date", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMin = date.toString_dateOnly();
                    } else if (tc.typeLngTime()) {
                        var time = TGS_Time.ofTime_HH_MM(inputText.trim());
                        if (time == null) {
                            d.ce("createPops", "popMinText", "time", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMin = time.toString_timeOnly_simplified();
                    }
                    reloadSelectedProfile();
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMin);
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMin);
                },
                () -> popMinText.textBox.selectAll()
        );
        popMaxText = new TGC_PopLblYesNoTextBoxExtraBtn2(dimBtn2, lblMax.getText(), "Uygula", "İptal",
                p -> {
                    var inputText = p.textBox.getText();
                    var pr = profiles.get(lb.getSelectedIndex());
                    var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
                    if (tc.typeLng()) {
                        var l = TGS_CastUtils.toLong(inputText.trim()).orElse(null);
                        if (l == null) {
                            d.ce("createPops", "popMaxText", "lng", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMax = l.toString();
                    } else if (tc.typeLngDbl()) {
                        var f = TGS_CastUtils.toDouble(inputText.trim()).orElse(null);
                        if (f == null) {
                            d.ce("createPops", "popMaxText", "dbl", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMax = f.toString();
                    } else if (tc.typeLngDate()) {
                        var date = TGS_Time.ofDate_D_M_Y(inputText.trim());
                        if (date == null) {
                            d.ce("createPops", "popMaxText", "date", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMax = date.toString_dateOnly();
                    } else if (tc.typeLngTime()) {
                        var time = TGS_Time.ofTime_HH_MM(inputText.trim());
                        if (time == null) {
                            d.ce("createPops", "popMaxText", "time", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMax = time.toString_timeOnly_simplified();
                    }
                    reloadSelectedProfile();
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMax);
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMax);
                },
                () -> popMaxText.textBox.selectAll()
        );
        popMinDate = new TGC_PopLblYesNoDateBoxExtraBtn4Status(dimBtn4, lblMin.getText(), "Uygula", "İptal",
                p -> {
                    var inputText = p.dateBox.getTextBox().getText();
                    var pr = profiles.get(lb.getSelectedIndex());
                    var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
                    if (tc.typeLngDate()) {
                        var date = TGS_Time.ofDate_D_M_Y(inputText.trim());
                        if (date == null) {
                            d.ce("createPops", "popMinDate", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMin = date.toString_dateOnly();
                    }
                    reloadSelectedProfile();
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMin);
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMin);
                },
                () -> popMinText.textBox.selectAll(),
                p -> {
                    var txt = p.dateBox.getTextBox().getText();
                    var dateEntry = TGS_Time.ofDate_D_M_Y(txt);
                    if (dateEntry == null) {
                        return "?";
                    }
                    var computedDayOfWeek = dateEntry.getDayOfWeekName(TGS_CharSetLocaleTypes.TURKISH);
                    d.ci("createPops", "popMinDate", dateEntry.toString_dateOnly(), computedDayOfWeek);
                    return computedDayOfWeek.orElse("?");
                }
        );
        popMaxDate = new TGC_PopLblYesNoDateBoxExtraBtn4Status(dimBtn4, lblMax.getText(), "Uygula", "İptal",
                p -> {
                    var inputText = p.dateBox.getTextBox().getText();
                    var pr = profiles.get(lb.getSelectedIndex());
                    var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
                    if (tc.typeLngDate()) {
                        var date = TGS_Time.ofDate_D_M_Y(inputText.trim());
                        if (date == null) {
                            d.ce("createPops", "popMaxDate", "HATA: Hatalı Girdi!");
                            return;
                        }
                        pr.btnMax = date.toString_dateOnly();
                    }
                    reloadSelectedProfile();
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMax);
                },
                p -> {
                    p.getPop().setVisible(false);
                    TGC_FocusUtils.setFocusAfterGUIUpdate(btnMax);
                },
                () -> popMaxText.textBox.selectAll(),
                p -> {
                    var txt = p.dateBox.getTextBox().getText();
                    var dateEntry = TGS_Time.ofDate_D_M_Y(txt);
                    if (dateEntry == null) {
                        return "?";
                    }
                    var computedDayOfWeek = dateEntry.getDayOfWeekName(TGS_CharSetLocaleTypes.TURKISH);
                    d.ci("createPops", "popMaxDate", dateEntry.toString_dateOnly(), computedDayOfWeek);
                    return computedDayOfWeek.orElse("?");
                }
        );
    }

    @Override
    public void configInit() {
        popMinText.textBox.setText(btnMin.getText());
        popMinText.btnAddSet(p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngTime()) {
                d.ce("configInit", "popMinText", "btnAddSet.onAdd1", "!tc.typeLngTime()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAddClick");
                return;
            }
            p.textBox.setText(TGS_Time.toString_timeOnly_now_simplified());
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngTime()) {
                d.ce("configInit", "popMinText", "btnAddSet.onAdd2", "!tc.typeLngTime()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                return;
            }
            p.textBox.setText("00:00");
        });
        popMinDate.dateBox.getTextBox().setText(btnMin.getText());
        popMinDate.btnAddSet(p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMinDate", "btnAddSet.onAdd1", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAddClick");
                p.reProcessStatus();
                return;
            }
            p.dateBox.getTextBox().setText(TGS_Time.toString_dateOnly_today());
            p.reProcessStatus();
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMinDate", "btnAddSet.onAdd2", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                p.reProcessStatus();
                return;
            }
            p.dateBox.getTextBox().setText(TGS_TimeUtils.zeroDateReadable());
            p.reProcessStatus();
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMinDate", "btnAddSet.onAdd3", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                p.reProcessStatus();
                return;
            }
            var valStr = p.dateBox.getTextBox().getText();
            var valDate = TGS_Time.ofDate_D_M_Y(valStr);
            if (valDate == null) {
                d.ce("configInit", "popMinDate", "btnAddSet.onAdd3", "valDate == null");
                p.reProcessStatus();
                return;
            }
            valDate = valDate.incrementDay(-1);
            d.ci("configInit", "popMinDate", "btnAddSet.onAdd3", "modified date", valDate.getDate());
            p.dateBox.getTextBox().setText(valDate.toString_dateOnly());
            p.reProcessStatus();
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMinDate", "btnAddSet.onAdd4", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                p.reProcessStatus();
                return;
            }
            var valStr = p.dateBox.getTextBox().getText();
            var valDate = TGS_Time.ofDate_D_M_Y(valStr);
            if (valDate == null) {
                d.ce("configInit", "popMinDate", "btnAddSet.onAdd4", "valDate == null");
                p.reProcessStatus();
                return;
            }
            valDate = valDate.incrementDay(1);
            d.ci("configInit", "popMinDate", "btnAddSet.onAdd4", "modified date", valDate.getDate());
            p.dateBox.getTextBox().setText(valDate.toString_dateOnly());
            p.reProcessStatus();
        }
        );
        popMaxText.textBox.setText(btnMax.getText());
        popMaxText.btnAddSet(p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngTime()) {
                d.ce("configInit", "popMaxText", "btnAddSet.onAdd1", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAddClick");
                return;
            }
            p.textBox.setText(TGS_Time.toString_timeOnly_now_simplified());
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngTime()) {
                d.ce("configInit", "popMaxText", "btnAddSet.onAdd2", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                return;
            }
            p.textBox.setText("00:00");
        });
        popMaxDate.dateBox.getTextBox().setText(btnMax.getText());
        popMaxDate.btnAddSet(p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMaxDate", "btnAddSet.onAdd1", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAddClick");
                p.reProcessStatus();
                return;
            }
            p.dateBox.getTextBox().setText(TGS_Time.toString_dateOnly_today());
            p.reProcessStatus();
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMaxDate", "btnAddSet.onAdd2", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                p.reProcessStatus();
                return;
            }
            p.dateBox.getTextBox().setText(TGS_TimeUtils.zeroDateReadable());
            p.reProcessStatus();
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMaxDate", "btnAddSet.onAdd3", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                p.reProcessStatus();
                return;
            }
            var valStr = p.dateBox.getTextBox().getText();
            var valDate = TGS_Time.ofDate_D_M_Y(valStr);
            if (valDate == null) {
                d.ce("configInit", "popMaxDate", "btnAddSet.onAdd3", "valDate == null");
                p.reProcessStatus();
                return;
            }
            valDate = valDate.incrementDay(-1);
            d.ci("configInit", "popMaxDate", "btnAddSet.onAdd3", "modified date", valDate.getDate());
            p.dateBox.getTextBox().setText(valDate.toString_dateOnly());
            p.reProcessStatus();
        }, p -> {
            var pr = profiles.get(lb.getSelectedIndex());
            var tc = TGS_LibRqlColUtils.toSqlCol(pr.ct);
            if (!tc.typeLngDate()) {
                d.ce("configInit", "popMaxDate", "btnAddSet.onAdd4", "!tc.typeLngDate()", "HATA: Düğme Saklı Olmalıydı Hatası!! TK_GWTTableFilterPanel.btnAdd2Click");
                p.reProcessStatus();
                return;
            }
            var valStr = p.dateBox.getTextBox().getText();
            var valDate = TGS_Time.ofDate_D_M_Y(valStr);
            if (valDate == null) {
                d.ce("configInit", "popMaxDate", "btnAddSet.onAdd4", "valDate == null");
                p.reProcessStatus();
                return;
            }
            valDate = valDate.incrementDay(1);
            d.ci("configInit", "popMaxDate", "btnAddSet.onAdd4", "!tc.typeLngDate()", "modified date", valDate.getDate());
            p.dateBox.getTextBox().setText(valDate.toString_dateOnly());
            p.reProcessStatus();
        });
        IntStream.range(0, tm.curTable.columns.size()).forEachOrdered(ci -> {
            lb.addItem(tm.curTable.columns.get(ci).getColumnName());
            profiles.add(new AppPopTableFilterProfile(this, ci));
        });
        profiles.get(0).resetProfile();
        profiles.get(0).renderPopProfile_and_lb();
        lb.setSelectedIndex(0);
    }
    public TGC_PopLblYesNoTextBoxExtraBtn2 popMaxText, popMinText;
    public TGC_PopLblYesNoDateBoxExtraBtn4Status popMaxDate, popMinDate;
    public AppPopTableFilterLINK popFilterLINKBySelected;

    @Override
    public void configActions() {
        TGC_ClickUtils.add(btnCloseAndCancel, onCloseAndCancel);
        TGC_ClickUtils.add(btnCloseAndApply, onCloseAndApply);
        TGC_ClickUtils.add(btnReset, onResetFromPopMain);
        TGC_ClickUtils.add(btnCustom, onCustom);
        TGC_ClickUtils.add(btnJoin, onJoin);
        TGC_KeyUtils.add(btnCloseAndCancel, onCloseAndCancel, onCloseAndCancel);
        TGC_KeyUtils.add(btnCloseAndApply, onCloseAndApply, onCloseAndApply);
        TGC_KeyUtils.add(btnReset, onResetFromPopMain, onResetFromPopMain);
        TGC_KeyUtils.add(btnCustom, onCustom, onCustom);
        TGC_KeyUtils.add(btnJoin, onJoin, onJoin);

        TGC_ClickUtils.add(lb, () -> onSelectLbItem(), null);
        TGC_KeyUtils.add(lb, () -> onSelectLbItem(), onCloseAndApply, () -> onSelectLbItem(), () -> onSelectLbItem());

        TGC_ClickUtils.add(rbSortBy, () -> onSelectRbSortBy());
        TGC_ClickUtils.add(cbAscending, () -> onSelectCbAscending());
        TGC_KeyUtils.add(rbSortBy, () -> onSelectRbSortBy(), () -> TGC_FocusUtils.setFocusAfterGUIUpdate(lb));
        TGC_KeyUtils.add(cbAscending, () -> onSelectCbAscending(), () -> TGC_FocusUtils.setFocusAfterGUIUpdate(lb));

        TGC_ClickUtils.add(cbActive, () -> onSelectCbActive(cbActive.getValue()));
        TGC_ClickUtils.add(cbNegative, () -> onSelectCbNegative());
        TGC_KeyUtils.add(cbActive, () -> onSelectCbActive(cbActive.getValue()), () -> TGC_FocusUtils.setFocusAfterGUIUpdate(lb));
        TGC_KeyUtils.add(cbNegative, () -> onSelectCbNegative(), () -> onSelectCbActive(false));

        TGC_ClickUtils.add(cbNull, () -> onSelectCbNull());
        TGC_KeyUtils.add(cbNull, () -> onSelectCbNull(), () -> onSelectCbActive(false));

        TGC_KeyUtils.add(tfByContent, onCloseAndApply, () -> onSelectCbActive(false));

        TGC_ClickUtils.add(btnBySelected, () -> onSelectBtnBySelected());
        TGC_KeyUtils.add(btnBySelected, () -> onSelectBtnBySelected(), () -> onSelectCbActive(false));

        TGC_ClickUtils.add(btnMin, () -> onSelectBtnMin());
        TGC_ClickUtils.add(btnMax, () -> onSelectBtnMax());
        TGC_KeyUtils.add(btnMin, () -> onSelectBtnMin(), () -> onSelectCbActive(false));
        TGC_KeyUtils.add(btnMax, () -> onSelectBtnMax(), () -> onSelectCbActive(false));
    }

    private void onSelectBtnMin() {
        var pr = profiles.get(lb.getSelectedIndex());
        if (Objects.equals(TGS_SQLColTypedUtils.TYPE_LNGDATE(), pr.ct.getType())) {
            popMinDate.getPop().setVisible_beCeneteredAt(btnMin);
        } else {
            popMinText.getPop().setVisible_beCeneteredAt(btnMin);
        }
    }

    private void onSelectBtnMax() {
        var pr = profiles.get(lb.getSelectedIndex());
        if (Objects.equals(TGS_SQLColTypedUtils.TYPE_LNGDATE(), pr.ct.getType())) {
            popMaxDate.getPop().setVisible_beCeneteredAt(btnMax);
        } else {
            popMaxText.getPop().setVisible_beCeneteredAt(btnMax);
        }
    }

    private void onSelectBtnBySelected() {
        popFilterLINKBySelected.onSetVisible.run(profiles.get(lb.getSelectedIndex()));
    }

    private void onSelectCbNull() {
        profiles.get(lb.getSelectedIndex()).cbNull = cbNull.getValue();
        reloadSelectedProfile();
    }

    private void onSelectCbNegative() {
        profiles.get(lb.getSelectedIndex()).cbNegative = cbNegative.getValue();
        reloadSelectedProfile();
    }

    private void onSelectCbAscending() {
        profiles.get(lb.getSelectedIndex()).cbAscending = cbAscending.getValue();
        reloadSelectedProfile();
    }

    private void onSelectRbSortBy() {
        IntStream.range(0, profiles.size()).forEachOrdered(i -> profiles.get(i).rbSortBy = i == lb.getSelectedIndex());
        reloadSelectedProfile();
        profiles.forEach(p -> p.renderPopLb());
    }

    private void onSelectLbItem() {
        reloadSelectedProfile();
    }

    private void reloadSelectedProfile() {
        profiles.get(lb.getSelectedIndex()).renderPopProfile_and_lb();
    }

    public void onSelectCbActive(boolean value) {
        profiles.get(lb.getSelectedIndex()).cbActive = value;
        reloadSelectedProfile();
        profiles.forEach(p -> p.renderPopLb());
        if (value) {
            focusPreferredFilter();
        } else {
            TGC_FocusUtils.setFocusAfterGUIUpdate(cbActive);
        }
    }

    @Override
    public void configFocus() {
        TGC_FocusUtils.addKeyUp(btnCloseAndCancel, new TGS_FocusSides4(btnJoin, btnCloseAndApply, null, lb));
        TGC_FocusUtils.addKeyUp(btnCloseAndApply, new TGS_FocusSides4(btnCloseAndCancel, btnReset, null, lb));
        TGC_FocusUtils.addKeyUp(btnReset, new TGS_FocusSides4(btnCloseAndApply, btnCustom, null, lb));
        TGC_FocusUtils.addKeyUp(btnCustom, new TGS_FocusSides4(btnReset, btnJoin, null, lb));
        TGC_FocusUtils.addKeyUp(btnJoin, new TGS_FocusSides4(btnCustom, btnCloseAndCancel, null, lb));
        TGC_FocusUtils.addKeyUp(lb, new TGS_FocusSides4(btnCloseAndCancel, rbSortBy, btnCloseAndCancel, null), sid -> reloadSelectedProfile());

        TGC_FocusUtils.addKeyUp(rbSortBy, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(lb);
                        break;
                    case KeyCodes.KEY_RIGHT:
                        if (cbAscending.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(cbAscending);
                        }
                        break;
                    case KeyCodes.KEY_UP:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(btnReset);
                        break;
                    case KeyCodes.KEY_DOWN:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(cbActive);
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(cbAscending, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(rbSortBy);
                        break;
                    case KeyCodes.KEY_UP:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(btnReset);
                        break;
                    case KeyCodes.KEY_DOWN:
                        if (cbNegative.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(cbNegative);
                        } else {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(cbActive);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(cbActive, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(lb);
                        break;
                    case KeyCodes.KEY_RIGHT:
                        if (cbNegative.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(cbNegative);
                        }
                        break;
                    case KeyCodes.KEY_UP:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(rbSortBy);
                        break;
                    case KeyCodes.KEY_DOWN:
                        focusPreferredFilter();
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(cbNegative, nativeKeyCode -> {
            if (null != nativeKeyCode) {
                switch (nativeKeyCode) {
                    case KeyCodes.KEY_LEFT:
                        TGC_FocusUtils.setFocusAfterGUIUpdate(cbActive);
                        break;
                    case KeyCodes.KEY_UP:
                        if (cbAscending.isVisible()) {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(cbAscending);
                        } else {
                            TGC_FocusUtils.setFocusAfterGUIUpdate(rbSortBy);
                        }
                        break;
                    case KeyCodes.KEY_DOWN:
                        focusPreferredFilter();
                        break;
                    default:
                        break;
                }
            }
        });
        TGC_FocusUtils.addKeyUp(tfByContent, new TGS_FocusSides4(lb, null, cbNegative, null));
        TGC_FocusUtils.addKeyUp(tfByContent, nativeKeyCode -> {
            profiles.get(lb.getSelectedIndex()).tfByContent = tfByContent.getText();
            reloadSelectedProfile();
        });
        TGC_FocusUtils.addKeyUp(btnBySelected, new TGS_FocusSides4(lb, null, cbNegative, null));
        TGC_FocusUtils.addKeyUp(cbNull, new TGS_FocusSides4(lb, null, cbNegative, null));
        TGC_FocusUtils.addKeyUp(btnMin, new TGS_FocusSides4(lb, null, cbNegative, btnMax));
        TGC_FocusUtils.addKeyUp(btnMax, new TGS_FocusSides4(lb, null, btnMin, null));
    }

    private void focusPreferredFilter() {
        if (tfByContent.isVisible()) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(tfByContent);
        } else if (btnBySelected.isVisible()) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnBySelected);
        } else if (cbNull.isVisible()) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(cbNull);
        } else if (btnMin.isVisible()) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnMin);
        } else if (btnMax.isVisible()) {
            TGC_FocusUtils.setFocusAfterGUIUpdate(btnMax);
        }
    }

    @Override
    public void configLayout() {
        btnCloseAndCancel.addStyleName("AppModuleTable_btn");
        btnCloseAndApply.addStyleName("AppModuleTable_btn");
        btnReset.addStyleName("AppModuleTable_btn");
        btnCustom.addStyleName("AppModuleTable_btn");
        btnJoin.addStyleName("AppModuleTable_btn");

        var dim = new TGC_Dimension(990, 500, false);
        content = TGC_PanelAbsoluteUtils.create(dim);//#101010
        var height = 30;
        {
            var x_offset = 10;
            var y_offset = 10;
            var width = 150;
            var x = x_offset;
            TGC_PanelAbsoluteUtils.setWidget(content, btnCloseAndCancel, TGS_ShapeRectangle.of(x, y_offset, width, height));
            x += width + x_offset;
            TGC_PanelAbsoluteUtils.setWidget(content, btnCloseAndApply, TGS_ShapeRectangle.of(x, y_offset, width, height));
            x += width + x_offset;
            TGC_PanelAbsoluteUtils.setWidget(content, btnReset, TGS_ShapeRectangle.of(x, y_offset, width, height));
            x += width + x_offset;
            TGC_PanelAbsoluteUtils.setWidget(content, btnCustom, TGS_ShapeRectangle.of(x, y_offset, width, height));
            x += width + x_offset;
            TGC_PanelAbsoluteUtils.setWidget(content, btnJoin, TGS_ShapeRectangle.of(x, y_offset, width, height));
        }
        {
            var columnNameWidth = 640;

            var x0Width = 310;
            var x1Width = 180;
            var xSeperator = 20;
            var x0 = 10;
            var x1 = x0 + x0Width + xSeperator;
            var x2 = x1 + x1Width + xSeperator;

            var lbHeight = 440;

            var yHeight = 20;
            var ySeperator = 30;
            var y0 = 50;
            var y1 = y0 + yHeight + ySeperator;
            var y2 = y1 + yHeight + ySeperator;
            var y3 = y2 + yHeight + ySeperator;

            TGC_PanelStyleUtils.green(rbSortBy, cbAscending);

            TGC_PanelAbsoluteUtils.setWidget(content, lb, TGS_ShapeRectangle.of(x0, y0, x0Width, lbHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, lblSelectedColumnNameVisible, TGS_ShapeRectangle.of(x1, y0, columnNameWidth, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, rbSortBy, TGS_ShapeRectangle.of(x1, y1, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, cbAscending, TGS_ShapeRectangle.of(x2, y1, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, cbActive, TGS_ShapeRectangle.of(x1, y2, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, cbNegative, TGS_ShapeRectangle.of(x2, y2, x1Width, yHeight));

            TGC_PanelAbsoluteUtils.setWidget(content, lblByContent, TGS_ShapeRectangle.of(x1, y3, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, tfByContent, TGS_ShapeRectangle.of(x2, y3, x1Width, yHeight));

            TGC_PanelAbsoluteUtils.setWidget(content, lblBySelected, TGS_ShapeRectangle.of(x1, y3, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, btnBySelected, TGS_ShapeRectangle.of(x2, y3, x1Width, yHeight));

            TGC_PanelAbsoluteUtils.setWidget(content, lblMin, TGS_ShapeRectangle.of(x1, y3, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, btnMin, TGS_ShapeRectangle.of(x2, y3, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, lblMax, TGS_ShapeRectangle.of(x1, y3 + yHeight + ySeperator, x1Width, yHeight));
            TGC_PanelAbsoluteUtils.setWidget(content, btnMax, TGS_ShapeRectangle.of(x2, y3 + yHeight + ySeperator, x1Width, yHeight));

            TGC_PanelAbsoluteUtils.setWidget(content, cbNull, TGS_ShapeRectangle.of(x1, y3, x1Width, yHeight));
        }
        panelPopup = new TGC_Pop(content, dim, null);

    }
    private AbsolutePanel content;
    private TGC_Pop panelPopup;

    @Override
    public TGC_Pop getPop() {
        return panelPopup;
    }

    public AppPopTableFilter(AppModuleTable tm) {
        this.tm = tm;
        createWidgets();
        createPops();
        configInit();
        configActions();
        configFocus();
        configLayout();
    }
    public AppModuleTable tm;

    public TGS_FuncMTU_In1<Integer> onSetVisibleCol = colIdx -> {
        if (!tm.filter.btn.isVisible()) {
            d.ce("onSetVisibleCol", "YASAK: Süzgeç kilitli yasağı!");
            TGC_FocusUtils.setFocusAfterGUIUpdate(tm.filter.btn);
            return;
        }
        if (tm.cells.isRowModifyVisible()) {
            d.ci("onSetVisibleCol", "BİLGİ: Satır düzenlerken filtre açılamaz.");
            tm.cells.focusActiveCell();
            return;
        }
        if (colIdx == null) {
            colIdx = 0;
        }
        lb.setSelectedIndex(colIdx);
        profiles.get(colIdx).renderPopProfile_and_lb();
        lst_saveCurrentAsLast();
        getPop().setVisible(true);
        TGC_FocusUtils.setFocusAfterGUIUpdate(btnCloseAndCancel);
    };

    public void lst_saveCurrentAsLast() {
        profiles_last.profiles.clear();
        profiles.forEach(p -> profiles_last.profiles.add(p.toMemItem()));
    }

    public boolean mem_saveCurrentAsNew(String name) {
        var newProfiles = new AppPopTableFilterMemProfiles(name, TGS_StreamUtils.toLst(profiles.stream().map(p -> p.toMemItem())));
        if (newProfiles.toLst().isEmpty()) {
            return false;
        }
        profilesListCustom.add(newProfiles);
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "newProfiles eklendi", newProfiles.name);
        }
        if (d.infoEnable) {
            IntStream.range(0, newProfiles.profiles.size()).forEachOrdered(i -> {
                d.debug(TGS_Log.TYPE_INF(), d.className(), newProfiles.profiles.get(i).toString());
            });
        }
        return true;
    }

    public void mem_loadAsCurrentFrom(AppPopTableFilterMemProfiles selectedProfiles) {
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "selectedProfiles.name", selectedProfiles.name);
        }
        IntStream.range(0, profiles.size()).forEach(profileIdx -> {
            var memItem = selectedProfiles.profiles.get(profileIdx);
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "memItem", memItem);
            }
            var profile = profiles.get(profileIdx);
            profile.loadFromMem(memItem);
            profile.renderPopLb();
            if (d.infoEnable) {
                d.debug(TGS_Log.TYPE_INF(), d.className(), "profileIdx set", profileIdx);
            }
        });
        lb.setSelectedIndex(0);
        reloadSelectedProfile();
        if (d.infoEnable) {
            d.debug(TGS_Log.TYPE_INF(), d.className(), "profileIdx set as last", 0);
        }
    }

    public void lst_loadLastAsCurrent() {
        mem_loadAsCurrentFrom(profiles_last);
    }

    public void mem_loadAsCurrentFrom(int customIdx) {
        mem_loadAsCurrentFrom(profilesListCustom.get(customIdx));
    }

    public final TGS_FuncMTU onCloseAndCancel = () -> {
        getPop().setVisible(false);
        lst_loadLastAsCurrent();
        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.filter.btn);
    };
    public final TGS_FuncMTU onCloseAndApply = () -> {
        AppCtrlCellLayoutUtils.isRenderable_clear();
        AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, null);
        AppCtrlCellLayoutUtils.cells_layout_refresh(d.className() + "createPops.popMem.default", tm);
        getPop().setVisible(false);
        tm.page.onPageFirst();
        lst_saveCurrentAsLast();
        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.filter.btn);
    };
    public final TGS_FuncMTU onResetCommon = () -> {
        profiles.forEach(p -> p.resetProfile());
        reloadSelectedProfile();
        d.ci("onResetCommon", "Süzgeç ayarları sıfırlandı");
    };
    private final TGS_FuncMTU onResetFromPopMain = () -> {
        onResetCommon.run();
        TGC_FocusUtils.setFocusAfterGUIUpdate(btnReset);
    };
    public final TGS_FuncMTU onResetFromPopLst = () -> {
        onResetCommon.run();
        tm.page.onPageFirst();
        lst_saveCurrentAsLast();
        d.cr("onResetFromPopLst", "Süzgeç ayarları sıfırlandı.");
        TGC_FocusUtils.setFocusAfterGUIUpdate(tm.cells.getPreferredLeftMostCell());
    };

    public final TGS_FuncMTU onCustom = () -> {
        tm.page.onPageFirst();
        lst_saveCurrentAsLast();
        tm.filter.showPopMemLst();
    };

    public final TGS_FuncMTU onJoin = () -> {
        if (tm.filter.join.isJoin()) {
            tm.filter.join.cmd_release();
            TGC_ButtonUtils.setIcon(btnJoin, TGS_IconUtils.CLASS_LINK(), "Ek İlişki Ayarla");
            AppCtrlCellLayoutUtils.isRenderable_clear();
            AppCtrlCellLayoutUtils.cells_visibility_refresh(tm, null);
            AppCtrlCellLayoutUtils.cells_layout_refresh(d.className() + "createPops.popMem.default", tm);
            getPop().setVisible(false);
        } else {
            tm.filter.join.cmd_annoint();
        }
    };

}
