package com.tugalsan.app.table.sg.cell;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.table.client.*;

public class AppSGFCellUpdateSTR extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFCellUpdateSTR.class.getSimpleName();
    }

    public AppSGFCellUpdateSTR() {
    }

    public AppSGFCellUpdateSTR(TGS_LibTableDbSub dbCfg, TGS_LibRqlTbl table, long id, String columnname, String value) {
        this.input_dbCfg = dbCfg;
        this.input_table = table;
        this.input_id = id;
        this.input_columnname = columnname;
        this.input_value = value;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private TGS_LibRqlTbl input_table = null;
    private Long input_id = null;
    private String input_columnname = null, input_value = null;
    private Boolean output_result = null;

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public TGS_LibRqlTbl getInput_table() {
        return input_table;
    }

    public void setInput_table(TGS_LibRqlTbl input_table) {
        this.input_table = input_table;
    }

    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public String getInput_columnname() {
        return input_columnname;
    }

    public void setInput_columnname(String input_columnname) {
        this.input_columnname = input_columnname;
    }

    public String getInput_value() {
        return input_value;
    }

    public void setInput_value(String input_value) {
        this.input_value = input_value;
    }

    public Boolean getOutput_result() {
        return output_result;
    }

    public void setOutput_result(Boolean output_result) {
        this.output_result = output_result;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFCellUpdateSTR.class.getSimpleName());
        sb.append("{output_result=").append(output_result);
        sb.append(", input_table=").append(input_table);
        sb.append(", input_id=").append(input_id);
        sb.append(", input_columnname=").append(input_columnname);
        sb.append(", input_value=").append(input_value);
        sb.append('}');
        return sb.toString();
    }

}
