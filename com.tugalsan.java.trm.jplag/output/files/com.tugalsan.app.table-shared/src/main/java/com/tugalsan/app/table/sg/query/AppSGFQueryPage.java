package com.tugalsan.app.table.sg.query;

import com.google.gwt.user.client.rpc.*;
import com.tugalsan.api.servlet.gwt.webapp.client.*;
import com.tugalsan.api.sql.cell.client.*;
import com.tugalsan.lib.table.client.*;
import java.util.*;

public class AppSGFQueryPage extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFQueryPage.class.getSimpleName();
    }

    public AppSGFQueryPage() {
    }

    public AppSGFQueryPage(TGS_LibTableDbSub dbCfg, CharSequence tableName, Integer rowStart, Integer rowSize, String wherestmt, String orderbystmt,
            String aramaJoinConfig, String aramaJoinValue) {
        input_dbCfg = dbCfg;
        input_tableName = tableName;
        input_rowStart = rowStart;
        input_rowSize = rowSize;
        input_wherestmt = wherestmt;
        input_orderbystmt = orderbystmt;
        input_aramaJoinConfig = aramaJoinConfig;
        input_aramaJoinValue = aramaJoinValue;
        output_skippedForCpu = false;
    }
    private TGS_LibTableDbSub input_dbCfg;
    private CharSequence input_tableName;
    private Integer input_rowStart, input_rowSize;
    private String input_wherestmt, input_orderbystmt;
    private String input_aramaJoinConfig, input_aramaJoinValue;
    private List<List<TGS_SQLCellAbstract>> output_column_values;
    private List<List<TGS_SQLCellLNG>> output_column_idsIfValueTypeIsLngLink;
    private Boolean output_skippedForCpu;

    public String getInput_aramaJoinConfig() {
        return input_aramaJoinConfig;
    }

    public void setInput_aramaJoinConfig(String input_aramaJoinConfig) {
        this.input_aramaJoinConfig = input_aramaJoinConfig;
    }

    public String getInput_aramaJoinValue() {
        return input_aramaJoinValue;
    }

    public void setInput_aramaJoinValue(String input_aramaJoinValue) {
        this.input_aramaJoinValue = input_aramaJoinValue;
    }

    public Boolean getOutput_skippedForCpu() {
        return output_skippedForCpu;
    }

    public void setOutput_skippedForCpu(Boolean output_skippedForCpu) {
        this.output_skippedForCpu = output_skippedForCpu;
    }

    public CharSequence getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(CharSequence input_tableName) {
        this.input_tableName = input_tableName;
    }

    public TGS_LibTableDbSub getInput_dbCfg() {
        return input_dbCfg;
    }

    public void setInput_dbCfg(TGS_LibTableDbSub input_dbCfg) {
        this.input_dbCfg = input_dbCfg;
    }

    public List<List<TGS_SQLCellAbstract>> getOutput_column_values() {
        return output_column_values;
    }

    public void setOutput_column_values(List<List<TGS_SQLCellAbstract>> output_column_values) {
        this.output_column_values = output_column_values;
    }

    public List<List<TGS_SQLCellLNG>> getOutput_column_idsIfValueTypeIsLngLink() {
        return output_column_idsIfValueTypeIsLngLink;
    }

    public void setOutput_column_idsIfValueTypeIsLngLink(List<List<TGS_SQLCellLNG>> output_column_idsIfValueTypeIsLngLink) {
        this.output_column_idsIfValueTypeIsLngLink = output_column_idsIfValueTypeIsLngLink;
    }

    public Integer getInput_rowStart() {
        return input_rowStart;
    }

    public void setInput_rowStart(Integer input_rowStart) {
        this.input_rowStart = input_rowStart;
    }

    public Integer getInput_rowSize() {
        return input_rowSize;
    }

    public void setInput_rowSize(Integer input_rowSize) {
        this.input_rowSize = input_rowSize;
    }

    public String getInput_wherestmt() {
        return input_wherestmt;
    }

    public void setInput_wherestmt(String input_wherestmt) {
        this.input_wherestmt = input_wherestmt;
    }

    public String getInput_orderbystmt() {
        return input_orderbystmt;
    }

    public void setInput_orderbystmt(String input_orderbystmt) {
        this.input_orderbystmt = input_orderbystmt;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFQueryPage.class.getSimpleName());
        sb.append("{output_columns_strOrBytes=").append(output_column_values);
        sb.append(", output_columns_IdsIfLngLink=").append(output_column_idsIfValueTypeIsLngLink);
        sb.append(", input_table=").append(input_tableName);
        sb.append(", input_rowStart=").append(input_rowStart);
        sb.append(", input_rowSize=").append(input_rowSize);
        sb.append(", input_aramaJoinConfig=").append(input_aramaJoinConfig);
        sb.append(", input_aramaJoinValue=").append(input_aramaJoinValue);
        sb.append(", input_wherestmt=").append(input_wherestmt);
        sb.append(", input_orderbystmt=").append(input_orderbystmt);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppSGFQueryPage other = (AppSGFQueryPage) obj;
        if (!Objects.equals(this.input_wherestmt, other.input_wherestmt)) {
            return false;
        }
        if (!Objects.equals(this.input_orderbystmt, other.input_orderbystmt)) {
            return false;
        }
        if (!Objects.equals(this.input_aramaJoinConfig, other.input_aramaJoinConfig)) {
            return false;
        }
        if (!Objects.equals(this.input_aramaJoinValue, other.input_aramaJoinValue)) {
            return false;
        }
        if (!Objects.equals(this.input_dbCfg, other.input_dbCfg)) {
            return false;
        }
        if (!Objects.equals(this.input_tableName, other.input_tableName)) {
            return false;
        }
        if (!Objects.equals(this.input_rowStart, other.input_rowStart)) {
            return false;
        }
        return Objects.equals(this.input_rowSize, other.input_rowSize);
    }

}
