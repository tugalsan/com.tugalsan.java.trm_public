package com.tugalsan.app.table.sg;

import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.google.gwt.user.client.rpc.*;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.table.client.*;
import java.util.Arrays;
import java.util.Objects;

public class AppSGFExportExcel extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFExportExcel.class.getSimpleName();
    }

    public AppSGFExportExcel() {
    }

    public AppSGFExportExcel(TGS_LibTableDbSub dbCfg, int[] tableOrder, boolean[] isColumnHidden,
            TGS_LibRqlTbl table, String wherestmt, String orderbystmt,
            String aramaJoinConfig, String aramaJoinValue) {
        input_dbCfg = dbCfg;
        input_tableOrder = tableOrder;
        input_isColumnHidden = isColumnHidden;
        input_table = table;
        input_wherestmt = wherestmt;
        input_orderbystmt = orderbystmt;
        input_aramaJoinConfig = aramaJoinConfig;
        input_aramaJoinValue = aramaJoinValue;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private int[] input_tableOrder = null;
    private boolean[] input_isColumnHidden = null;
    private TGS_LibRqlTbl input_table = null;
    private String input_wherestmt = null;
    private String input_orderbystmt = null;
    private String input_aramaJoinConfig, input_aramaJoinValue;
    private String output_url = null;

    public String getInput_aramaJoinConfig() {
        return input_aramaJoinConfig;
    }

    public void setInput_aramaJoinConfig(String input_aramaJoinConfig) {
        this.input_aramaJoinConfig = input_aramaJoinConfig;
    }

    public String getInput_aramaJoinValue() {
        return input_aramaJoinValue;
    }

    public void setInput_aramaJoinValue(String input_aramaJoinValue) {
        this.input_aramaJoinValue = input_aramaJoinValue;
    }

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public int[] getInput_tableOrder() {
        return input_tableOrder;
    }

    public void setInput_tableOrder(int[] input_tableOrder) {
        this.input_tableOrder = input_tableOrder;
    }

    public boolean[] getInput_isColumnHidden() {
        return input_isColumnHidden;
    }

    public void setInput_isColumnHidden(boolean[] input_isColumnHidden) {
        this.input_isColumnHidden = input_isColumnHidden;
    }

    public TGS_LibRqlTbl getInput_table() {
        return input_table;
    }

    public void setInput_table(TGS_LibRqlTbl input_table) {
        this.input_table = input_table;
    }

    public String getInput_wherestmt() {
        return input_wherestmt;
    }

    public void setInput_wherestmt(String input_wherestmt) {
        this.input_wherestmt = input_wherestmt;
    }

    public String getInput_orderbystmt() {
        return input_orderbystmt;
    }

    public void setInput_orderbystmt(String input_orderbystmt) {
        this.input_orderbystmt = input_orderbystmt;
    }

    public String getOutput_url() {
        return output_url;
    }

    public void setOutput_url(String output_url) {
        this.output_url = output_url;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFExportExcel.class.getSimpleName());
        sb.append("{output_url=").append(output_url);
        sb.append(", input_tableOrder=").append(input_tableOrder);
        sb.append(", input_isColumnHidden=").append(input_isColumnHidden);
        sb.append(", input_table=").append(input_table);
        sb.append(", input_wherestmt=").append(input_wherestmt);
        sb.append(", input_orderbystmt=").append(input_orderbystmt);
        sb.append(", input_aramaJoinConfig=").append(input_aramaJoinConfig);
        sb.append(", input_aramaJoinValue=").append(input_aramaJoinValue);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppSGFExportExcel other = (AppSGFExportExcel) obj;
        if (!Objects.equals(this.input_wherestmt, other.input_wherestmt)) {
            return false;
        }
        if (!Objects.equals(this.input_orderbystmt, other.input_orderbystmt)) {
            return false;
        }
        if (!Objects.equals(this.input_aramaJoinConfig, other.input_aramaJoinConfig)) {
            return false;
        }
        if (!Objects.equals(this.input_aramaJoinValue, other.input_aramaJoinValue)) {
            return false;
        }
        if (!Objects.equals(this.input_dbCfg, other.input_dbCfg)) {
            return false;
        }
        if (!Arrays.equals(this.input_tableOrder, other.input_tableOrder)) {
            return false;
        }
        if (!Arrays.equals(this.input_isColumnHidden, other.input_isColumnHidden)) {
            return false;
        }
        return Objects.equals(this.input_table, other.input_table);
    }

}
