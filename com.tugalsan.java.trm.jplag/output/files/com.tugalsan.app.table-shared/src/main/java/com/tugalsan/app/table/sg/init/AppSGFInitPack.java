package com.tugalsan.app.table.sg.init;

import java.util.*;
import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.lib.rql.client.TGS_LibRqlTbl;

public class AppSGFInitPack extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFInitPack.class.getSimpleName();
    }

    public AppSGFInitPack() {
    }

    public AppSGFInitPack(CharSequence tableName) {
        this.input_tableName = tableName;
    }
    private CharSequence input_tableName;
    private List<TGS_LibRqlTbl> output_tables;
    private List< TGS_LibRqlTbl> output_userTables;
    private List< String> output_userColHideIdxes;
    private List< Boolean> output_userAllowFileWrite;
    private List< Integer> output_userEditableDays;

    public CharSequence getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(CharSequence input_tableName) {
        this.input_tableName = input_tableName;
    }

    public List<TGS_LibRqlTbl> getOutput_tables() {
        return output_tables;
    }

    public void setOutput_tables(List<TGS_LibRqlTbl> output_tables) {
        this.output_tables = output_tables;
    }

    public List<TGS_LibRqlTbl> getOutput_userTables() {
        return output_userTables;
    }

    public void setOutput_userTables(List<TGS_LibRqlTbl> output_userTables) {
        this.output_userTables = output_userTables;
    }

    public List<String> getOutput_userColHideIdxes() {
        return output_userColHideIdxes;
    }

    public void setOutput_userColHideIdxes(List<String> output_userColHideIdxes) {
        this.output_userColHideIdxes = output_userColHideIdxes;
    }

    public List<Boolean> getOutput_userAllowFileWrite() {
        return output_userAllowFileWrite;
    }

    public void setOutput_userAllowFileWrite(List<Boolean> output_userAllowFileWrite) {
        this.output_userAllowFileWrite = output_userAllowFileWrite;
    }

    public List<Integer> getOutput_userEditableDays() {
        return output_userEditableDays;
    }

    public void setOutput_userEditableDays(List<Integer> output_userEditableDays) {
        this.output_userEditableDays = output_userEditableDays;
    }

    @Override
    public String toString() {
        return AppSGFInitPack.class.getSimpleName() + "{" + "input_tableName=" + input_tableName + ", output_tables=" + output_tables + ", output_userTables=" + output_userTables + ", output_userColHideIdxes=" + output_userColHideIdxes + ", output_userAllowFileWrite=" + output_userAllowFileWrite + ", output_userEditableDays=" + output_userEditableDays + '}';
    }

}
