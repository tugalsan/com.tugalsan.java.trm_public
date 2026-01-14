package com.tugalsan.app.table.sg.cell;

import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.google.gwt.user.client.rpc.*;
import com.tugalsan.lib.rql.link.client.*;
import com.tugalsan.lib.table.client.*;

@Deprecated //DO NOT ABUSE IT!
public class AppSGFCellGet extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFCellGet.class.getSimpleName();
    }

    public AppSGFCellGet() {
    }

    public AppSGFCellGet(TGS_LibTableDbSub input_dbCfg, String lookFromTablename, CharSequence tablename, long id) {
        this.input_dbCfg = input_dbCfg;
        this.input_lookFromTablename = lookFromTablename;
        this.input_tableName = tablename;
        this.input_id = id;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private String input_lookFromTablename;
    private CharSequence input_tableName;
    private Long input_id;
    private TGS_LibRqlLink output_cell;

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public String getInput_lookFromTablename() {
        return input_lookFromTablename;
    }

    public void setInput_lookFromTablename(String input_lookFromTablename) {
        this.input_lookFromTablename = input_lookFromTablename;
    }

    public CharSequence getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(CharSequence input_tableName) {
        this.input_tableName = input_tableName;
    }

    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public TGS_LibRqlLink getOutput_cell() {
        return output_cell;
    }

    public void setOutput_cell(TGS_LibRqlLink output_cell) {
        this.output_cell = output_cell;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFCellGet.class.getSimpleName());
        sb.append("{output_cell=").append(output_cell);
        sb.append(", input_lookFromTablename=").append(input_lookFromTablename);
        sb.append(", input_tableName=").append(input_tableName);
        sb.append(", input_id=").append(input_id);
        sb.append('}');
        return sb.toString();
    }

}
