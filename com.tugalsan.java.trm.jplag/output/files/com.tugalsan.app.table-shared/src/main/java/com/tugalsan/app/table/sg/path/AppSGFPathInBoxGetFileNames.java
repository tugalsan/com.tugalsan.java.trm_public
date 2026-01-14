package com.tugalsan.app.table.sg.path;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.List;
import com.tugalsan.lib.rql.client.*;

public class AppSGFPathInBoxGetFileNames extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFPathInBoxGetFileNames.class.getSimpleName();
    }

    public AppSGFPathInBoxGetFileNames() {
    }

    public AppSGFPathInBoxGetFileNames(TGS_LibRqlTbl table, int columnIdx, long id, boolean copyTemplateFileIfNeeded) {
        input_table = table;
        input_columnIdx = columnIdx;
        input_id = id;
        input_copyTemplateFileIfNeeded = copyTemplateFileIfNeeded;
    }

    private TGS_LibRqlTbl input_table;
    private Integer input_columnIdx = null;
    private Long input_id = null;
    private Boolean input_copyTemplateFileIfNeeded = null;
    private List<String> output_names = null;

    public TGS_LibRqlTbl getInput_table() {
        return input_table;
    }

    public void setInput_table(TGS_LibRqlTbl input_table) {
        this.input_table = input_table;
    }

    public Integer getInput_columnIdx() {
        return input_columnIdx;
    }

    public void setInput_columnIdx(Integer input_columnIdx) {
        this.input_columnIdx = input_columnIdx;
    }

    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public Boolean getInput_copyTemplateFileIfNeeded() {
        return input_copyTemplateFileIfNeeded;
    }

    public void setInput_copyTemplateFileIfNeeded(Boolean input_copyTemplateFileIfNeeded) {
        this.input_copyTemplateFileIfNeeded = input_copyTemplateFileIfNeeded;
    }

    public List<String> getOutput_names() {
        return output_names;
    }

    public void setOutput_names(List<String> output_names) {
        this.output_names = output_names;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFPathInBoxGetFileNames.class.getSimpleName());
        sb.append("{output_names=").append(output_names);
        sb.append(", input_table=").append(input_table);
        sb.append(", input_columnIdx=").append(input_columnIdx);
        sb.append(", input_id=").append(input_id);
        sb.append(", input_copyTemplateFileIfNeeded=").append(input_copyTemplateFileIfNeeded);
        sb.append('}');
        return sb.toString();
    }

}
