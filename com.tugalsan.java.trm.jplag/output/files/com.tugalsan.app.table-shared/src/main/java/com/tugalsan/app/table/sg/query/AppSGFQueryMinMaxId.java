package com.tugalsan.app.table.sg.query;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.table.client.*;

public class AppSGFQueryMinMaxId extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFQueryMinMaxId.class.getSimpleName();
    }

    public AppSGFQueryMinMaxId() {

    }

    public AppSGFQueryMinMaxId(TGS_LibTableDbSub dbCfg, String tableName, String columnName) {
        this(dbCfg, tableName, columnName, null);
    }

    public AppSGFQueryMinMaxId(TGS_LibTableDbSub dbCfg, String tableName, String columnName, String where) {
        input_dbCfg = dbCfg;
        input_tableName = tableName;
        input_columnName = columnName;
        input_where = where;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private String input_tableName, input_columnName, input_where;
    private Long output_minId, output_maxId;

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

    public Long getOutput_minId() {
        return output_minId;
    }

    public void setOutput_minId(Long output_minId) {
        this.output_minId = output_minId;
    }

    public Long getOutput_maxId() {
        return output_maxId;
    }

    public void setOutput_maxId(Long output_maxId) {
        this.output_maxId = output_maxId;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFQueryMinMaxId.class.getSimpleName());
        sb.append("{input_tableName=").append(input_tableName);
        sb.append(", input_columnName=").append(input_columnName);
        sb.append(", input_where=").append(input_where);
        sb.append(", output_minId=").append(output_minId);
        sb.append(", output_maxId=").append(output_maxId);
        sb.append('}');
        return sb.toString();
    }

}
