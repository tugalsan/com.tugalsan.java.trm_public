package com.tugalsan.app.table.sg.query;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.table.client.*;

public class AppSGFQueryCount extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFQueryCount.class.getSimpleName();
    }

    public AppSGFQueryCount() {
    }

    public AppSGFQueryCount(TGS_LibTableDbSub dbCfg, String tableName, String where, String aramaJoinConfig, String aramaJoinValue) {
        input_dbCfg = dbCfg;
        input_tableName = tableName;
        input_where = where;
        input_aramaJoinConfig = aramaJoinConfig;
        input_aramaJoinValue = aramaJoinValue;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private String input_tableName, input_where;
    private Long output_count;
    private String input_aramaJoinConfig, input_aramaJoinValue;

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

    public Long getOutput_count() {
        return output_count;
    }

    public void setOutput_count(Long output_count) {
        this.output_count = output_count;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFQueryCount.class.getSimpleName());
        sb.append("{input_tableName=").append(input_tableName);
        sb.append(", input_where=").append(input_where);
        sb.append(", input_aramaJoinConfig=").append(input_aramaJoinConfig);
        sb.append(", input_aramaJoinValue=").append(input_aramaJoinValue);
        sb.append(", output_count=").append(output_count);
        sb.append('}');
        return sb.toString();
    }

}
