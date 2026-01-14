package com.tugalsan.app.table.sg.row;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.table.client.*;

public class AppSGFRowRemove extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFRowRemove.class.getSimpleName();
    }

    public AppSGFRowRemove() {
    }

    public AppSGFRowRemove(TGS_LibTableDbSub dbCfg, TGS_LibRqlTbl table, long id) {
        input_dbCfg = dbCfg;
        input_table = table;
        input_Id = id;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private TGS_LibRqlTbl input_table;
    private Long input_Id;
    private Boolean output_result;

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

    public Long getInput_Id() {
        return input_Id;
    }

    public void setInput_Id(Long input_Id) {
        this.input_Id = input_Id;
    }

    public Boolean getOutput_result() {
        return output_result;
    }

    public void setOutput_result(Boolean output_result) {
        this.output_result = output_result;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFRowRemove.class.getSimpleName());
        sb.append("{output_result=").append(output_result);
        sb.append(", input_table=").append(input_table);
        sb.append(", input_Id=").append(input_Id);
        sb.append('}');
        return sb.toString();
    }

}
