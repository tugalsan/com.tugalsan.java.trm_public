package com.tugalsan.app.table.sg.path;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.List;

public class AppSGFPathHttpInboxImageUrls extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFPathHttpInboxImageUrls.class.getSimpleName();
    }

    public AppSGFPathHttpInboxImageUrls() {
    }

    public AppSGFPathHttpInboxImageUrls(String tablename, Long id, boolean copyTemplateFileIfNeeded) {
        input_tablename = tablename;
        input_id = id;
        input_copyTemplateFileIfNeeded = copyTemplateFileIfNeeded;
    }

    private String input_tablename = null;
    private Long input_id = null;
    private Boolean input_copyTemplateFileIfNeeded = null;
    private List<String> output_imageUrls = null;

    public List<String> getOutput_imageUrls() {
        return output_imageUrls;
    }

    public void setOutput_imageUrls(List<String> output_imageUrls) {
        this.output_imageUrls = output_imageUrls;
    }

    public String getInput_tablename() {
        return input_tablename;
    }

    public void setInput_tablename(String input_tablename) {
        this.input_tablename = input_tablename;
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

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFPathHttpInboxImageUrls.class.getSimpleName());
        sb.append("{output_imageUrls=").append(output_imageUrls);
        sb.append(", input_tablename=").append(input_tablename);
        sb.append(", input_id=").append(input_id);
        sb.append(", input_copyTemplateFileIfNeeded=").append(input_copyTemplateFileIfNeeded);
        sb.append('}');
        return sb.toString();
    }

}
