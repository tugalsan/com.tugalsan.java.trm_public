package com.tugalsan.app.table.sg.cell;

import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.google.gwt.user.client.rpc.*;
import com.tugalsan.lib.rql.link.client.*;
import com.tugalsan.lib.table.client.*;
import java.util.*;

public class AppSGFCellGetList extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFCellGetList.class.getSimpleName();
    }

    public AppSGFCellGetList() {
    }

    public AppSGFCellGetList(TGS_LibTableDbSub input_dbCfg, String lookFromTablename, CharSequence tablename, List<Long> ids) {
        this.input_dbCfg = input_dbCfg;
        this.input_lookFromTablename = lookFromTablename;
        this.input_tableName = tablename;
        this.input_ids = ids;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private String input_lookFromTablename;
    private CharSequence input_tableName;
    private List<Long> input_ids;

    public List<String> output_linkText_WPrefixIds;
    public List<String> output_linkTexts;
    public List<String> output_errTexts;

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

    public List<Long> getInput_ids() {
        return input_ids;
    }

    public void setInput_ids(List<Long> input_ids) {
        this.input_ids = input_ids;
    }

    public List<String> getOutput_linkText_WPrefixIds() {
        return output_linkText_WPrefixIds;
    }

    public void setOutput_linkText_WPrefixIds(List<String> output_linkText_WPrefixIds) {
        this.output_linkText_WPrefixIds = output_linkText_WPrefixIds;
    }

    public List<String> getOutput_linkTexts() {
        return output_linkTexts;
    }

    public void setOutput_linkTexts(List<String> output_linkTexts) {
        this.output_linkTexts = output_linkTexts;
    }

    public List<String> getOutput_errTexts() {
        return output_errTexts;
    }

    public void setOutput_errTexts(List<String> output_errTexts) {
        this.output_errTexts = output_errTexts;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFCellGetList.class.getSimpleName());
        sb.append("{output_linkText_WPrefixIds=").append(output_linkText_WPrefixIds);
        sb.append("{output_linkTexts=").append(output_linkTexts);
        sb.append("{output_errTexts=").append(output_errTexts);
        sb.append(", input_lookFromTablename=").append(input_lookFromTablename);
        sb.append(", input_tableName=").append(input_tableName);
        sb.append(", input_id=").append(input_ids);
        sb.append('}');
        return sb.toString();
    }

}
