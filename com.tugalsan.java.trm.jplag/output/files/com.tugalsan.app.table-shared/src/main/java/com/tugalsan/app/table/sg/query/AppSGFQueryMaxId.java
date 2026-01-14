package com.tugalsan.app.table.sg.query;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.table.client.*;

public class AppSGFQueryMaxId extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFQueryMaxId.class.getSimpleName();
    }

    public AppSGFQueryMaxId() {
    }

    public AppSGFQueryMaxId(TGS_LibTableDbSub dbCfg, String tableName, String columnName) {
        this(dbCfg, tableName, columnName, null);
    }

    public AppSGFQueryMaxId(TGS_LibTableDbSub dbCfg, String tableName, String columnName, String where) {
        this.input_dbCfg = dbCfg;
        this.input_tableName = tableName;
        this.input_columnName = columnName;
        this.input_where = where;
    }

    private TGS_LibTableDbSub input_dbCfg;
    private String input_tableName, input_columnName, input_where;
    private Long output_id;

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public String getInput_where() {
        return input_where;
    }

    public void setInput_where(String input_where) {
        this.input_where = input_where;
    }

    public String getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(String input_tableName) {
        this.input_tableName = input_tableName;
    }

    public String getInput_columnName() {
        return input_columnName;
    }

    public void setInput_columnName(String input_columnName) {
        this.input_columnName = input_columnName;
    }

    public Long getOutput_id() {
        return output_id;
    }

    public void setOutput_id(Long output_id) {
        this.output_id = output_id;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFQueryMaxId.class.getSimpleName());
        sb.append("{input_tableName=").append(input_tableName);
        sb.append(", input_columnName=").append(input_columnName);
        sb.append(", input_where=").append(input_where);
        sb.append(", output_id=").append(output_id);
        sb.append('}');
        return sb.toString();
    }

}
