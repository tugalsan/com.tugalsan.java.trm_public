package com.tugalsan.app.table.sg.row;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.table.client.*;

public class AppSGFRowExists extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFRowExists.class.getSimpleName();
    }

    public AppSGFRowExists() {
    }

    public AppSGFRowExists(TGS_LibTableDbSub dbCfg, String tableName, Long id) {
        input_dbCfg = dbCfg;
        input_tableName = tableName;
        input_id = id;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private String input_tableName;
    private Long input_id;
    private Boolean output_result;

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public String getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(String input_tableName) {
        this.input_tableName = input_tableName;
    }

    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public Boolean getOutput_result() {
        return output_result;
    }

    public void setOutput_result(Boolean output_result) {
        this.output_result = output_result;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFRowExists.class.getSimpleName());
        sb.append("{output_result=").append(output_result);
        sb.append(", input_tableName=").append(input_tableName);
        sb.append(", input_id=").append(input_id);
        sb.append('}');
        return sb.toString();
    }

}
