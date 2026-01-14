package com.tugalsan.app.table.sg.row;

import java.util.List;
import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.tuple.client.*;

public class AppSGFRowUsage extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFRowUsage.class.getSimpleName();
    }

    public AppSGFRowUsage() {
    }

    public AppSGFRowUsage(String tablename, Long id) {
        this.input_tableName = tablename;
        this.input_id = id;
    }
    private String input_tableName;
    private Long input_id;
    private Boolean output_used;
    private String output_summary;
    private List<TGS_Tuple2<String, List<Long>>> output_tableSQLNames;

    public List<TGS_Tuple2<String, List<Long>>> getOutput_tableSQLNames() {
        return output_tableSQLNames;
    }

    public void setOutput_tableSQLNames(List<TGS_Tuple2<String, List<Long>>> output_tableSQLNames) {
        this.output_tableSQLNames = output_tableSQLNames;
    }

    public String getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(String input_tableName) {
        this.input_tableName = input_tableName;
    }

    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public Boolean getOutput_used() {
        return output_used;
    }

    public void setOutput_used(Boolean output_used) {
        this.output_used = output_used;
    }

    public String getOutput_summary() {
        return output_summary;
    }

    public void setOutput_summary(String output_summary) {
        this.output_summary = output_summary == null ? "" : output_summary.trim();
        this.output_used = !output_summary.isEmpty();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFRowUsage.class.getSimpleName());
        sb.append("{output_tableSQLNames=").append(output_tableSQLNames);
        sb.append("{output_used=").append(output_used);
        sb.append(", output_summary=").append(output_summary);
        sb.append(", input_tableName=").append(input_tableName);
        sb.append(", input_id=").append(input_id);
        sb.append('}');
        return sb.toString();
    }

}
