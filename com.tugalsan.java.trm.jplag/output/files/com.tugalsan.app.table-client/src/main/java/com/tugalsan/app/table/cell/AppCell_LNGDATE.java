package com.tugalsan.app.table.cell;

import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.log.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.rql.client.*;


public class AppCell_LNGDATE extends AppCell_LNG {

    final private static TGC_Log d = TGC_Log.of(AppCell_LNGDATE.class);

    private final TGS_Time date;

    public AppCell_LNGDATE(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        date = TGS_Time.of();
        setValueLong(getValueLong());
        TGC_DOMUtils.setTextAlignCenter(getElement());
    }

    @Override
    public final void setValueLong(long valueLong) {
        super.setValueLong(valueLong);
        d.ci("setValueLong", valueLong);
        date.setDate(valueLong);
        setText(date.toString_dateOnly());
    }

    public void setValueDate(TGS_Time valueDate) {
        setValueLong(valueDate.getDate());
    }

    public TGS_Time getValueDate() {
        return date.cloneIt();
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.input.showDate(getText(),
                inputText -> AppCtrlCellUpdateUtils.date(tm, inputText),
                this,
                "<b>Tarih:</b>",
                TGS_IconUtils.CLASS_CALENDAR(), "Bugün şQ", TGS_Time.toString_dateOnly_today(),
                TGS_IconUtils.CLASS_RADIO_UNCHECKED(), "Sıfırla şW", TGS_TimeUtils.zeroDateReadable(),
                TGS_IconUtils.CLASS_MINUS(), "Geri şA", 
                TGS_IconUtils.CLASS_PLUS(), "İleri şS"
        );
    }
}
