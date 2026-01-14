package com.tugalsan.app.table.cell;

import com.tugalsan.api.math.client.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.app.table.*;
import com.tugalsan.app.table.control.utils.*;
import com.tugalsan.lib.rql.client.*;
import java.util.stream.IntStream;

public class AppCell_LNGDOUBLE extends AppCell_LNG {

//    final private static TGC_Log d = TGC_Log.of(AppCell_LNGDOUBLE.class);

    private final int commaInt;

    public int getCommaInt() {
        return commaInt;
    }

    public AppCell_LNGDOUBLE(AppModuleTable tm, int row, int col, TGS_LibRqlCol ct) {
        super(tm, row, col, ct);
        commaInt = ct.getDataInt_STRFamilyMaxCharSize_or_LNGDOUBLEPrecision();
    }

    @Override
    public void setValueLong(long valueLong) {
        super.setValueLong(valueLong);
        var s = new StringBuilder(String.valueOf(getValueDouble()));
        var di = s.indexOf(".");
        if (di == -1) {//ADD .00 to 1
            s.append(".");
            IntStream.range(0, getCommaInt()).forEach(i -> s.append("0"));
        } else {////ADD 00 to 1.0
            var ldi = s.substring(di + 1).length();
            IntStream.range(ldi, getCommaInt()).forEach(i -> s.append("0"));
        }
        var dbl = TGS_StringDouble.of(s);
        if (dbl.isExcuse()) {
            setText(s);
        } else {
            var htmlText = TGS_StringUtils.cmn().concat(String.valueOf(dbl.value().left), "<sub>", dbl.value().dim(), dbl.value().rightZeros(), String.valueOf(dbl.value().right), "</sub>");
            setHTML(htmlText);
        }
    }

    public void setValueDouble(double valueDouble) {
        setValueLong(TGS_MathUtils.double2Long(valueDouble, commaInt));
    }

    public double getValueDouble() {
        return TGS_MathUtils.long2Double(getValueLong(), commaInt);
    }

    @Override
    public void onClick_showCellUpdate(boolean shiftDown, boolean altDown, boolean controlDown, boolean metaDown) {
        tm.input.showBox(getText(),
                inputText -> AppCtrlCellUpdateUtils.floating(tm, inputText),
                this,
                "<b>Noktalı Sayı:</b>"
        );
    }
}
