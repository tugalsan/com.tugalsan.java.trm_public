package com.tugalsan.app.table.cell;

import com.tugalsan.api.gui.client.dom.*;
import com.tugalsan.api.icon.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.rql.client.*;

public class AppCell_LNGTIME extends AppCell_LNG {

//    final private static TGC_Log d = TGC_Log.of(AppCell_LNGTIME.class);

    private final TGS_Time time;

    public AppCell_LNGTIME(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        time = TGS_Time.of();
        setValueLong(getValueLong());
        TGC_DOMUtils.setTextAlignCenter(getElement());
    }

    @Override
    final public void setValueLong(long valueLong) {
        super.setValueLong(valueLong);
        time.setTime(valueLong);
        setText(time.toString_timeOnly_simplified());
    }

    public void setValueTime(TGS_Time valueTime) {
        setValueLong(valueTime.getTime());
    }

    public TGS_Time getValueTime() {
        return time.cloneIt();
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.input.showTime(getText(),
                inputText -> AppCtrlCellUpdateUtils.time(tm, inputText),
                this,
                "<b>Zaman Girdisi:</b>",
                TGS_IconUtils.CLASS_CLOCK(), "Şimdi", TGS_Time.toString_timeOnly_now_simplified(),
                TGS_IconUtils.CLASS_RADIO_UNCHECKED(), "Sıfırla", "00:00"
        );
    }
}
