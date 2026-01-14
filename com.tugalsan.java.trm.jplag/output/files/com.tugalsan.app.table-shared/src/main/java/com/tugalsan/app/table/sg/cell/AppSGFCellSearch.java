package com.tugalsan.app.table.sg.cell;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.tugalsan.lib.table.client.*;
import java.util.List;

public class AppSGFCellSearch extends TGS_SGWTFuncBase implements IsSerializable {

    public static int MIN_SIZE_DISABLE() {
        return -1;
    }

    public static int MIN_SIZE() {
        return 30;
    }

    public static int MAX_SIZE() {
        return 200;
    }

    public static int MAX_SECS() {//INIT MAX SEC
        return 60;
    }

    final public static transient int THRESHOLD_MAX_SECS = 60 * 5;

    @Override
    public String getSuperClassName() {
        return AppSGFCellSearch.class.getSimpleName();
    }

    public AppSGFCellSearch() {
    }

    public AppSGFCellSearch(TGS_LibTableDbSub dbCfg, Long minId, Long maxId,
            String lookFromTablename, String tarTablename,
            Integer minSize, Integer maxSize, Integer maxSecs,
            String spacedTags, boolean isKeyWordsAnd) {
        input_dbCfg = dbCfg;
        input_minId = minId;
        input_maxId = maxId;
        input_lookFromTablename = lookFromTablename;
        input_tarTablename = tarTablename;
        input_minSize = minSize;
        input_maxSize = maxSize;
        input_maxSecs = maxSecs;
        input_spacedTags = spacedTags;
        input_isKeyWordsAnd = isKeyWordsAnd;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private Long input_minId;
    private Long input_maxId;
    private String input_lookFromTablename;
    private String input_tarTablename;
    private Integer input_minSize;
    private Integer input_maxSize;
    private Integer input_maxSecs;
    private String input_spacedTags;
    private Boolean input_isKeyWordsAnd;
    private Boolean output_isProcessedAsStar;
    private Long output_ctm;
    private List<String> output_list;
    private String output_status;

    public Boolean getOutput_isProcessedAsStar() {
        return output_isProcessedAsStar;
    }

    public void setOutput_isProcessedAsStar(Boolean output_isProcessedAsStar) {
        this.output_isProcessedAsStar = output_isProcessedAsStar;
    }

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public Long getInput_minId() {
        return input_minId;
    }

    public void setInput_minId(Long input_minId) {
        this.input_minId = input_minId;
    }

    public Long getInput_maxId() {
        return input_maxId;
    }

    public void setInput_maxId(Long input_maxId) {
        this.input_maxId = input_maxId;
    }

    public String getInput_lookFromTablename() {
        return input_lookFromTablename;
    }

    public void setInput_lookFromTablename(String input_lookFromTablename) {
        this.input_lookFromTablename = input_lookFromTablename;
    }

    public String getInput_tarTablename() {
        return input_tarTablename;
    }

    public void setInput_tarTablename(String input_tarTablename) {
        this.input_tarTablename = input_tarTablename;
    }

    public Integer getInput_minSize() {
        return input_minSize;
    }

    public void setInput_minSize(Integer input_minSize) {
        this.input_minSize = input_minSize;
    }

    public Integer getInput_maxSize() {
        return input_maxSize;
    }

    public Integer getInput_maxSecs() {
        return input_maxSecs;
    }

    public void setInput_maxSecs(Integer input_maxSecs) {
        this.input_maxSecs = input_maxSecs;
    }

    public void setInput_maxSize(Integer input_maxSize) {
        this.input_maxSize = input_maxSize;
    }

    public String getInput_spacedTags() {
        return input_spacedTags;
    }

    public void setInput_spacedTags(String input_spacedTags) {
        this.input_spacedTags = input_spacedTags;
    }

    public Boolean getInput_isKeyWordsAnd() {
        return input_isKeyWordsAnd;
    }

    public void setInput_isKeyWordsAnd(Boolean input_isKeyWordsAnd) {
        this.input_isKeyWordsAnd = input_isKeyWordsAnd;
    }

    public Long getOutput_ctm() {
        return output_ctm;
    }

    public void setOutput_ctm(Long output_ctm) {
        this.output_ctm = output_ctm;
    }

    public List<String> getOutput_list() {
        return output_list;
    }

    public void setOutput_list(List<String> output_list) {
        this.output_list = output_list;
    }

    public String getOutput_status() {
        return output_status;
    }

    public void setOutput_status(String output_status) {
        this.output_status = output_status;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFCellSearch.class.getSimpleName());
        sb.append("{output_ctm=").append(output_ctm);
        sb.append(", output_status=").append(output_status);
        sb.append(", output_list=").append(output_list);
        sb.append(", output_isProcessedAsStar=").append(output_isProcessedAsStar);
        sb.append(", input_minId=").append(input_minId);
        sb.append(", input_maxId=").append(input_maxId);
        sb.append(", input_lookFromTablename=").append(input_lookFromTablename);
        sb.append(", input_tarTablename=").append(input_tarTablename);
        sb.append(", input_minSize=").append(input_minSize);
        sb.append(", input_maxSize=").append(input_maxSize);
        sb.append(", input_maxSecs=").append(input_maxSecs);
        sb.append(", input_spacedTags=").append(input_spacedTags);
        sb.append(", input_isKeyWordsAnd=").append(input_isKeyWordsAnd);
        sb.append('}');
        return sb.toString();
    }

}
