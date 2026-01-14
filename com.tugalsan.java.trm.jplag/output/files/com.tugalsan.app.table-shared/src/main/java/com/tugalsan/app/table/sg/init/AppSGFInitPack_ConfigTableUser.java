package com.tugalsan.app.table.sg.init;

import com.tugalsan.lib.rql.client.*;

public class AppSGFInitPack_ConfigTableUser /*implements Serializable*/ {//cannot serilize! GWT does not like u

    private AppSGFInitPack_ConfigTableUser(TGS_LibRqlTbl table, String colHideIdxes, boolean allowFileWrite, Integer editableDays) {
        this.table = table;
        this.colHideIdxes = colHideIdxes;
        this.allowFileWrite = allowFileWrite;
        this.editableDays = editableDays;
    }

    public TGS_LibRqlTbl table;
    public String colHideIdxes;
    public boolean allowFileWrite;
    public Integer editableDays;

    public static AppSGFInitPack_ConfigTableUser of(TGS_LibRqlTbl table, String colHideIdxes, boolean allowFileWrite, Integer editableDays) {
        return new AppSGFInitPack_ConfigTableUser(table, colHideIdxes, allowFileWrite, editableDays);
    }

    @Override
    public String toString() {
        return AppSGFInitPack_ConfigTableUser.class.getSimpleName() + "{" + "table=" + table + ", colHideIdxes=" + colHideIdxes + ", allowFileWrite=" + allowFileWrite + ", editableDays=" + editableDays + '}';
    }
}
