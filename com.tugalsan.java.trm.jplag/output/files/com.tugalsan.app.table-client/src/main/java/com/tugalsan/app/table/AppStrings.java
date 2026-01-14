package com.tugalsan.app.table;

import com.tugalsan.api.charset.client.TGS_CharSet;
import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.charset.client.TGS_CharSetLocaleTypes;

public class AppStrings {

    public static String TITLE_AppModuleDefault() {
        return TGS_CharSetCast.current().localType.equals(TGS_CharSetLocaleTypes.TURKISH) ? "Tablo" : "Table";
    }
    
}
