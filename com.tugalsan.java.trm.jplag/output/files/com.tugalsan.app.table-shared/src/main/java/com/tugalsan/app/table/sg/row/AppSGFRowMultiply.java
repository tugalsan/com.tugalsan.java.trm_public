package com.tugalsan.app.table.sg.row;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.rql.client.*;
import com.tugalsan.lib.table.client.*;

public class AppSGFRowMultiply extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFRowMultiply.class.getSimpleName();
    }

    public AppSGFRowMultiply() {
    }

    public AppSGFRowMultiply(TGS_LibTableDbSub dbCfg, TGS_LibRqlTbl table, Long fromRowid, Integer count, Boolean emptyDatesAndHours, Boolean emptyColumn_onRowClone) {
        this.input_dbCfg = dbCfg;
        this.input_table = table;
        this.input_fromRowid = fromRowid;
        this.input_count = count;
        this.input_emptyDatesAndHours = emptyDatesAndHours;
        this.input_emptyColumn_onRowClone = emptyColumn_onRowClone;
        this.output_result = false;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private TGS_LibRqlTbl input_table;
    private Long input_fromRowid;
    private Integer input_count;
    private Boolean input_emptyDatesAndHours;
    private Boolean input_emptyColumn_onRowClone;
    private Boolean output_result;

    public Boolean getInput_emptyColumn_onRowClone() {
        return input_emptyColumn_onRowClone;
    }

    public void setInput_emptyColumn_onRowClone(Boolean input_emptyColumn_onRowClone) {
        this.input_emptyColumn_onRowClone = input_emptyColumn_onRowClone;
    }

    public Boolean getInput_emptyDatesAndHours() {
        return input_emptyDatesAndHours;
    }

    public void setInput_emptyDatesAndHours(Boolean input_emptyDatesAndHours) {
        this.input_emptyDatesAndHours = input_emptyDatesAndHours;
    }

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

    public Long getInput_fromRowid() {
        return input_fromRowid;
    }

    public void setInput_fromRowid(Long input_fromRowid) {
        this.input_fromRowid = input_fromRowid;
    }

    public Integer getInput_count() {
        return input_count;
    }

    public void setInput_count(Integer input_count) {
        this.input_count = input_count;
    }

    public Boolean getOutput_result() {
        return output_result;
    }

    public void setOutput_result(Boolean output_result) {
        this.output_result = output_result;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFRowMultiply.class.getSimpleName());
        sb.append("{output_result=").append(output_result);
        sb.append(", input_table=").append(input_table);
        sb.append(", input_fromRowid=").append(input_fromRowid);
        sb.append(", input_count=").append(input_count);
        sb.append(", input_emptyDatesAndHours=").append(input_emptyDatesAndHours);
        sb.append(", input_emptyColumn_onRowClone=").append(input_emptyColumn_onRowClone);
        sb.append('}');
        return sb.toString();
    }

}
