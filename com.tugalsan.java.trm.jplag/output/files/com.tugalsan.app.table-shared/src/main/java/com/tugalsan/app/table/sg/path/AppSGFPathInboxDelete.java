package com.tugalsan.app.table.sg.path;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AppSGFPathInboxDelete extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFPathInboxDelete.class.getSimpleName();
    }

    public AppSGFPathInboxDelete() {
    }

    public AppSGFPathInboxDelete(String tablename, String columnname, String filename) {
        input_tablename = tablename;
        input_columnname = columnname;
        input_filename = filename;
    }

    private String input_tablename = null, input_columnname = null, input_filename = null;
    private Boolean output_result = null;

    public String getInput_tablename() {
        return input_tablename;
    }

    public void setInput_tablename(String input_tablename) {
        this.input_tablename = input_tablename;
    }

    public String getInput_columnname() {
        return input_columnname;
    }

    public void setInput_columnname(String input_columnname) {
        this.input_columnname = input_columnname;
    }

    public String getInput_filename() {
        return input_filename;
    }

    public void setInput_filename(String input_filename) {
        this.input_filename = input_filename;
    }

    public Boolean getOutput_result() {
        return output_result;
    }

    public void setOutput_result(Boolean output_result) {
        this.output_result = output_result;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFPathInboxDelete.class.getSimpleName());
        sb.append("{output_result=").append(output_result);
        sb.append(", input_tablename=").append(input_tablename);
        sb.append(", input_columnname=").append(input_columnname);
        sb.append(", input_filename=").append(input_filename);
        sb.append('}');
        return sb.toString();
    }

}
