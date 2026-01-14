package com.tugalsan.app.table.sg.path;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AppSGFPathHttpInboxFileUrl extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFPathHttpInboxFileUrl.class.getSimpleName();
    }

    public AppSGFPathHttpInboxFileUrl() {
    }

    public AppSGFPathHttpInboxFileUrl(String tablename, String columnname, String filename) {
        input_tablename = tablename;
        input_columnname = columnname;
        input_filename = filename;
    }

    private String input_tablename = null;
    private String input_columnname = null;
    private String input_filename = null;
    private String output_url = null;

    public String getInput_filename() {
        return input_filename;
    }

    public void setInput_filename(String input_filename) {
        this.input_filename = input_filename;
    }

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

    public String getOutput_url() {
        return output_url;
    }

    public void setOutput_url(String output_url) {
        this.output_url = output_url;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFPathHttpInboxFileUrl.class.getSimpleName());
        sb.append("{output_url=").append(output_url);
        sb.append(", input_tablename=").append(input_tablename);
        sb.append(", input_columnname=").append(input_columnname);
        sb.append(", input_filename=").append(input_filename);
        sb.append('}');
        return sb.toString();
    }

}
