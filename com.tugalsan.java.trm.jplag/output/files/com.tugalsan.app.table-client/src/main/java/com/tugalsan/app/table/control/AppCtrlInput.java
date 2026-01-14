package com.tugalsan.app.table.control;

import com.google.gwt.user.client.ui.*;
import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.charset.client.TGS_CharSet;
import com.tugalsan.api.charset.client.TGS_CharSetLocaleTypes;
import com.tugalsan.api.function.client.maythrowexceptions.checked.TGS_FuncMTCUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTU_In1;
import com.tugalsan.api.gui.client.dim.*;
import com.tugalsan.api.gui.client.focus.*;
import com.tugalsan.api.gui.client.pop.*;
import com.tugalsan.api.gui.client.widget.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.time.client.TGS_Time;
import com.tugalsan.api.time.client.TGS_TimeUtils;

import com.tugalsan.app.table.*;

public class AppCtrlInput {

    final private static TGC_Log d = TGC_Log.of(AppCtrlInput.class);

    public AppCtrlInput(AppModuleTable tm) {
        this.tm = tm;
    }
    private final AppModuleTable tm;
    private TGC_PopLblYesNoTextArea popArea;
    private TGC_PopLblYesNoTextBoxExtraBtn2 popBox;
    private TGC_PopLblYesNoDateBoxExtraBtn4Status popDate;
    private TGC_PopLblYesNo popDate_ForFuture;
    private TGC_PopLblYesNo popDate_ForPastYears;
    private TGC_PopLblYesNoTextBoxExtraBtn2 popTime;
    private TGC_PopLblYesNo popTime_outOfWorkHours;
    private TGC_PopLblYesNoTextBoxExtraBtn2 popMultiply;
    private TGC_PopLblYesNo popMultiply_dates;
    private TGC_PopLblYesNo popMultiply_empty;

    public void setAreaEditable(boolean editable) {
        popArea.setEditable(editable);
    }

    public void setAreaText(String text) {
        popArea.textArea.setText(text);
    }

    public void showArea_selectAll() {
        popArea.textArea.selectAll();
    }

    public void showBox_selectAll() {
        popBox.textBox.selectAll();
    }

    public void showArea(String optional_initText, TGS_FuncMTU_In1<String> afterInput, UIObject optional_center_nullForFullscreen, Integer maxCharCount, String lblHtml) {
        if (optional_initText != null) {//IF NULL, DONT CHANGE!
            popArea.textArea.setText(optional_initText);
        }
        if (lblHtml != null) {
            popArea.setLabelHTML(lblHtml);
        }
        if (maxCharCount != null) {
            popArea.maxCharCount = maxCharCount;
            popArea.startMaxCharCheck_untilUnVisible();
        }
        this.afterInput = afterInput;
        popArea.btnExe.setVisible(afterInput != null);
        if (optional_center_nullForFullscreen == null) {
            popArea.getPop().setVisibleFullScreen();
            return;
        }
        popArea.getPop().setVisible_beCeneteredAt(optional_center_nullForFullscreen);
    }

    public void showMultiply(String initText, TGS_FuncMTU_In1<String> afterInput, UIObject optional_center, String lblHtml,
            String optional_iconClass1Name, String optional_Add1BtnText, String optional_Add1BtnVal,
            String optional_iconClass2Name, String optional_Add2BtnText, String optional_Add2BtnVal) {
        popMultiply.textBox.setText(initText);
        if (lblHtml != null) {
            popMultiply.setLabelHTML(lblHtml);
        }
        this.afterInput = afterInput;
        popMultiply.btnAddShowAs(optional_iconClass1Name, optional_Add1BtnText, optional_iconClass2Name, optional_Add2BtnText);
        add1Text_onClickValue = optional_Add1BtnVal;
        add2Text_onClickValue = optional_Add2BtnVal;
        popMultiply.getPop().setVisible_beCeneteredAt(optional_center);
    }

    public void showBox(String initText, TGS_FuncMTU_In1<String> afterInput, UIObject optional_center, String lblHtml) {
        showBox(initText, afterInput, optional_center, lblHtml, null, null, null, null, null, null);
    }

    public void showBox(String initText, TGS_FuncMTU_In1<String> afterInput, UIObject optional_center, String lblHtml,
            String optional_iconClass1Name, String optional_Add1BtnText, String optional_Add1BtnVal,
            String optional_iconClass2Name, String optional_Add2BtnText, String optional_Add2BtnVal) {
        popBox.textBox.setText(initText);
        if (lblHtml != null) {
            popBox.setLabelHTML(lblHtml);
        }
        this.afterInput = afterInput;
        popBox.btnAddShowAs(optional_iconClass1Name, optional_Add1BtnText, optional_iconClass2Name, optional_Add2BtnText);
        add1Text_onClickValue = optional_Add1BtnVal;
        add2Text_onClickValue = optional_Add2BtnVal;
        if (optional_center == null) {
            popBox.getPop().setVisible(true);
        } else {
            popBox.getPop().setVisible_beCeneteredAt(optional_center);
        }
    }

    public void showTime(String initText, TGS_FuncMTU_In1<String> afterInput, UIObject optional_center, String lblHtml,
            String optional_iconClass1Name, String optional_Add1BtnText, String optional_Add1BtnVal,
            String optional_iconClass2Name, String optional_Add2BtnText, String optional_Add2BtnVal) {
        popTime.textBox.setText(initText);
        if (lblHtml != null) {
            popTime.setLabelHTML(lblHtml);
        }
        this.afterInput = afterInput;
        popTime.btnAddShowAs(optional_iconClass1Name, optional_Add1BtnText, optional_iconClass2Name, optional_Add2BtnText);
        add1Text_onClickValue = optional_Add1BtnVal;
        add2Text_onClickValue = optional_Add2BtnVal;
        popTime.getPop().setVisible_beCeneteredAt(optional_center);
    }

    public void showDate(String initText, TGS_FuncMTU_In1<String> afterInput, UIObject optional_center, String lblHtml,
            String optional_iconClass1Name, String optional_Add1BtnText, String optional_Add1BtnVal,
            String optional_iconClass2Name, String optional_Add2BtnText, String optional_Add2BtnVal,
            String optional_iconClass3Name, String optional_Add3BtnText,
            String optional_iconClass4Name, String optional_Add4BtnText
    ) {
        popDate.dateBox.getTextBox().setText(initText);
        if (lblHtml != null) {
            popDate.setLabelHTML(lblHtml);
        }
        this.afterInput = afterInput;
        popDate.btnAddShowAs(
                optional_iconClass1Name, optional_Add1BtnText,
                optional_iconClass2Name, optional_Add2BtnText,
                optional_iconClass3Name, optional_Add3BtnText,
                optional_iconClass4Name, optional_Add4BtnText
        );
        add1Text_onClickValue = optional_Add1BtnVal;
        add2Text_onClickValue = optional_Add2BtnVal;
        popDate.getPop().setVisible_beCeneteredAt(optional_center);
    }

    public void configInit() {
        popBox.btnAddSet(
                p -> p.textBox.setText(add1Text_onClickValue),
                p -> p.textBox.setText(add2Text_onClickValue)
        );
        popDate.btnAddSet(
                p -> {
                    p.dateBox.getTextBox().setText(add1Text_onClickValue);
                    p.reProcessStatus();
                },
                p -> {
                    p.dateBox.getTextBox().setText(add2Text_onClickValue);
                    p.reProcessStatus();
                },
                p -> {
                    var valStr = p.dateBox.getTextBox().getText();
                    var valDate = TGS_Time.ofDate_D_M_Y(valStr);
                    if (valDate == null) {
                        d.ce("configInit.popDate.btnAddSet", "valDate == null");
                        p.reProcessStatus();
                        return;
                    }
                    valDate = valDate.incrementDay(-1);
                    d.ci("configInit.popDate.btnAddSet", "modified date", valDate.getDate());
                    p.dateBox.getTextBox().setText(valDate.toString_dateOnly());
                    p.reProcessStatus();
                },
                p -> {
                    var valStr = p.dateBox.getTextBox().getText();
                    var valDate = TGS_Time.ofDate_D_M_Y(valStr);
                    if (valDate == null) {
                        d.ce("configInit.popDate.btnAddSet", "valDate == null");
                        return;
                    }
                    valDate = valDate.incrementDay(1);
                    d.ci("configInit.popDate.btnAddSet", "modified date", valDate.getDate());
                    p.dateBox.getTextBox().setText(valDate.toString_dateOnly());
                    p.reProcessStatus();
                }
        );
        popTime.btnAddSet(
                p -> p.textBox.setText(add1Text_onClickValue),
                p -> p.textBox.setText(add2Text_onClickValue)
        );
        popMultiply.btnAddSet(
                p -> p.textBox.setText(add1Text_onClickValue),
                p -> p.textBox.setText(add2Text_onClickValue)
        );
    }
    private String add1Text_onClickValue = "", add2Text_onClickValue = "";

    public void createPops() {
      
        popArea = new TGC_PopLblYesNoTextArea(
                new TGC_Dimension(350, 350, true),
                "<b>Girdi:</b>", "Değiştir", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    run(p.textArea.getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    tm.cells.focusActiveCell();
                },
                null
        );
        var btnOkText = "Değiştir";
        popBox = new TGC_PopLblYesNoTextBoxExtraBtn2(
                new TGC_Dimension(180, 102, true),
                "Girdi:", btnOkText, "İptal",
                p -> {
                    setPopBoxOkText(btnOkText);//SO I CAN CHANGE OK TEXT
                    p.getPop().setVisible(false);
                    run(p.textBox.getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    setPopBoxOkText(btnOkText);//SO I CAN CHANGE OK TEXT
                    p.getPop().setVisible(false);
                    if (setCustomBoxEscFocusWidget == null) {
                        tm.cells.focusActiveCell();
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(setCustomBoxEscFocusWidget);
                        setCustomBoxEscFocusWidget = null;
                    }
                },
                () -> popBox.textBox.selectAll()
        );
        popMultiply = new TGC_PopLblYesNoTextBoxExtraBtn2(
                new TGC_Dimension(180, 102, true),
                "Adet:", "Çoğalt", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    var txt = p.textBox.getText();
                    var intCount = TGS_CastUtils.toInteger(txt).orElse(null);
                    if (intCount == null) {
                        d.ce("createPops.popMultiply", "UYARI: Geçersiz giriş tespit edildi; çoğaltm işlemi durduruldu!");
                        tm.cells.focusActiveCell();
                        return;
                    }
                    if (tm.curTable.emptyColumn_onRowClone.isEmpty()) {
                        popMultiply_dates.getPop().setVisible(true);
                    } else {
                        popMultiply_empty.getPop().setVisible(true);
                    }
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    if (setCustomBoxEscFocusWidget == null) {
                        tm.cells.focusActiveCell();
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(setCustomBoxEscFocusWidget);
                        setCustomBoxEscFocusWidget = null;
                    }
                },
                () -> popMultiply.textBox.selectAll()
        );
        popMultiply_empty = new TGC_PopLblYesNo(
                new TGC_Dimension(180 * 2, 102, true),
                "Ayarlanmış kolonlar boşaltılsın mı?", "Boşaltılsın", "Aynı kalsın",
                p -> {
                    p.getPop().setVisible(false);
                    AppCtrlInput.multiply_emptyColumn_onRowClone = true;
                    popMultiply_dates.getPop().setVisible(true);
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    AppCtrlInput.multiply_emptyColumn_onRowClone = false;
                    popMultiply_dates.getPop().setVisible(true);
                    tm.cells.focusActiveCell();
                },
                null
        );
        popMultiply_dates = new TGC_PopLblYesNo(
                new TGC_Dimension(180 * 2, 102, true),
                "Tarihleri ve Saatleri ne yapalım?", "Sıfırlansın", "Aynı kalsın",
                p -> {
                    p.getPop().setVisible(false);
                    AppCtrlInput.multiply_emptyDatesAndHours = true;
                    run(popMultiply.textBox.getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    AppCtrlInput.multiply_emptyDatesAndHours = false;
                    run(popMultiply.textBox.getText());
                    tm.cells.focusActiveCell();
                },
                null
        );
        var timeWorkStarts = TGS_Time.ofTime(70000L);
        var timeWorkEnds = TGS_Time.ofTime(173000L);
        var timeEmpty = TGS_Time.ofTime(0L);
        popTime = new TGC_PopLblYesNoTextBoxExtraBtn2(
                new TGC_Dimension(180, 112, true),
                "Girdi:", "Değiştir", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    var txt = p.textBox.getText();
                    var timeEntry = TGS_Time.ofTime_HH_MM(txt);
                    if (timeEntry == null) {
                        d.ce("createPops.popTime", "UYARI: Geçersiz giriş tespit edildi; hücre içerik değiştirme işlemi durduruldu!");
                        tm.cells.focusActiveCell();
                        return;
                    }
                    if (!timeEntry.isProperTime_99HourMinOrSecIsProper_shiftedSecondAsMillisecond()) {
                        d.ce("createPops.popTime", "UYARI: Geçersiz zaman tespit edildi; hücre içerik değiştirme işlemi durduruldu!");
                        tm.cells.focusActiveCell();
                        return;
                    }
                    if (!timeEmpty.hasEqualTimeWith(timeEntry)) {
                        if (!tm.curTable.disableTimeOrDateCheck_OnEntry.isEmpty()) {
                            if (tm.curTable.disableTimeOrDateCheck_OnEntry.stream().noneMatch(ci -> ci == tm.cells.getActiveColIdx().intValue())) {
                                if (timeWorkEnds.hasSmallerTimeThan(timeEntry)) {
                                    popTime_outOfWorkHours.getPop().setVisible(true);
                                    return;
                                }
                                if (timeWorkStarts.hasGreaterTimeThan(timeEntry)) {
                                    popTime_outOfWorkHours.getPop().setVisible(true);
                                    return;
                                }
                            }
                        }
                    }
                    run(p.textBox.getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    if (setCustomBoxEscFocusWidget == null) {
                        tm.cells.focusActiveCell();
                    } else {
                        TGC_FocusUtils.setFocusAfterGUIUpdate(setCustomBoxEscFocusWidget);
                        setCustomBoxEscFocusWidget = null;
                    }
                },
                () -> popTime.textBox.selectAll()
        );
        popTime_outOfWorkHours = new TGC_PopLblYesNo(
                new TGC_Dimension(180 * 2, 102, true),
                TGS_CharSet.cmn().UTF8_HOURGLASS(), "Değiştir", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    run(popTime.textBox.getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    tm.cells.focusActiveCell();
                },
                () -> popTime_outOfWorkHours.label.setHTML("Girdi çalışma saatlerinin dışında! [" + popTime.textBox.getText() + "]'ı onaylıyor musunuz?")
        );
        var dateEmpty = TGS_Time.ofDate(TGS_TimeUtils.zeroDateLng());
        popDate = new TGC_PopLblYesNoDateBoxExtraBtn4Status(
                new TGC_Dimension(180, 140, true),
                "Girdi:", "Değiştir", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    var txt = p.dateBox.getTextBox().getText();
                    var dateEntry = TGS_Time.ofDate_D_M_Y(txt);
                    if (dateEntry == null) {
                        d.ce("createPops.popDate", "UYARI: Geçersiz giriş tespit edildi; hücre içerik değiştirme işlemi durduruldu!");
                        tm.cells.focusActiveCell();
                        return;
                    }
                    if (!dateEmpty.hasEqualDateWith(dateEntry)) {
                        if (!tm.curTable.disableTimeOrDateCheck_OnEntry.isEmpty()) {
                            if (tm.curTable.disableTimeOrDateCheck_OnEntry.stream().noneMatch(ci -> ci == tm.cells.getActiveColIdx().intValue())) {
                                var dateToday = TGS_Time.of();
                                if (dateToday.hasSmallerDateThan(dateEntry)) {
                                    popDate_ForFuture.getPop().setVisible(true);
                                    return;
                                }
                                if (dateToday.getYear() > dateEntry.getYear()) {
                                    popDate_ForPastYears.getPop().setVisible(true);
                                    return;
                                }
                            }
                        }
                    }
                    run(p.dateBox.getTextBox().getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    tm.cells.focusActiveCell();
                },
                () -> popDate.dateBox.getTextBox().selectAll(),
                p -> {
                    var txt = p.dateBox.getTextBox().getText();
                    var dateEntry = TGS_Time.ofDate_D_M_Y(txt);
                    if (dateEntry == null) {
                        return "?";
                    }
                    var computedDayOfWeek = dateEntry.getDayOfWeekName(TGS_CharSetLocaleTypes.TURKISH);
                    d.ci("createPops.popDate", dateEntry.toString_dateOnly(), computedDayOfWeek);
                    return computedDayOfWeek.orElse("?");
                }
        );
        popDate_ForFuture = new TGC_PopLblYesNo(
                new TGC_Dimension(180 * 2, 102, true),
                TGS_CharSet.cmn().UTF8_HOURGLASS(), "Değiştir", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    run(popDate.dateBox.getTextBox().getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    tm.cells.focusActiveCell();
                },
                () -> popDate_ForFuture.label.setHTML("Girdi gelecek bir tarih! [" + popDate.dateBox.getTextBox().getText() + "]'ı onaylıyor musunuz?")
        );
        popDate_ForPastYears = new TGC_PopLblYesNo(
                new TGC_Dimension(180 * 2, 102, true),
                TGS_CharSet.cmn().UTF8_HOURGLASS(), "Değiştir", "İptal",
                p -> {
                    p.getPop().setVisible(false);
                    run(popDate.dateBox.getTextBox().getText());
                    tm.cells.focusActiveCell();
                },
                p -> {
                    p.getPop().setVisible(false);
                    tm.cells.focusActiveCell();
                },
                () -> popDate_ForPastYears.label.setHTML("Girdi geçmiş bir yıl! [" + popDate.dateBox.getTextBox().getText() + "]'ı onaylıyor musunuz?")
        );
    }

    public FocusWidget setCustomBoxEscFocusWidget = null;

    private void run(String inputText) {
        TGS_FuncMTCUtils.run(() -> {
            if (afterInput == null) {
                d.ce("run", "HATA: İşlem tanımlaması yapılmamış!");
                return;
            }
            afterInput.run(inputText);
            afterInput = null;
        }, e -> d.ct("run", e));
    }
    private TGS_FuncMTU_In1<String> afterInput = null;
    public static boolean multiply_emptyDatesAndHours = true;
    public static boolean multiply_emptyColumn_onRowClone = true;

    public void setPopBoxOkText(String okText) {
        TGC_ButtonUtils.setIcon(popBox.btnExe, TGS_IconUtils.CLASS_CHECKMARK(), okText);
    }
}
