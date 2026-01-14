package com.tugalsan.app.table.sg.row;

import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.google.gwt.user.client.rpc.*;
import java.util.List;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.lib.table.client.*;

public class AppSGFRowAdd extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFRowAdd.class.getSimpleName();
    }

    public AppSGFRowAdd() {
    }

    public AppSGFRowAdd(TGS_LibTableDbSub dbCfg, CharSequence tableName, List<TGS_SQLCellAbstract> row) {
        input_dbCfg = dbCfg;
        output_result = null;
        input_tableName = tableName;
        input_row = row;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private CharSequence input_tableName;
    private List<TGS_SQLCellAbstract> input_row;
    private Boolean output_result;

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public Boolean getOutput_result() {
        return output_result;
    }

    public void setOutput_result(Boolean output_result) {
        this.output_result = output_result;
    }

    public CharSequence getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(CharSequence input_tableName) {
        this.input_tableName = input_tableName;
    }

    public List<TGS_SQLCellAbstract> getInput_row() {
        return input_row;
    }

    public void setInput_row(List<TGS_SQLCellAbstract> input_row) {
        this.input_row = input_row;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFRowAdd.class.getSimpleName());
        sb.append("{output_result=").append(output_result);
        sb.append(", input_table=").append(input_tableName);
        sb.append(", input_row=").append(input_row);
        sb.append('}');
        return sb.toString();
    }

}
