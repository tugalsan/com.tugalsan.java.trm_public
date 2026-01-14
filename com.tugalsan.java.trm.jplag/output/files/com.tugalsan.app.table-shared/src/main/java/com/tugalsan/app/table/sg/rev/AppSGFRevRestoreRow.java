package com.tugalsan.app.table.sg.rev;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.List;
import com.tugalsan.api.sql.cell.client.TGS_SQLCellAbstract;

public class AppSGFRevRestoreRow extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFRevRestoreRow.class.getSimpleName();
    }

    public AppSGFRevRestoreRow() {
    }

    public AppSGFRevRestoreRow(List<TGS_SQLCellAbstract> data, CharSequence tableName, Long overwriteId, boolean overwrite) {
        this.input_data = data;
        this.input_tableName = tableName;
        this.input_overwriteId = overwriteId;
    }

    private List<TGS_SQLCellAbstract> input_data = null;
    CharSequence input_tableName = null;
    private Long input_overwriteId = null;

    public List<TGS_SQLCellAbstract> getInput_data() {
        return input_data;
    }

    public void setInput_data(List<TGS_SQLCellAbstract> input_data) {
        this.input_data = input_data;
    }

    public CharSequence getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(CharSequence input_tableName) {
        this.input_tableName = input_tableName;
    }

    public Long getInput_overwriteId() {
        return input_overwriteId;
    }

    public void setInput_overwriteId(Long input_overwriteId) {
        this.input_overwriteId = input_overwriteId;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFRevRestoreRow.class.getSimpleName());
        sb.append("{input_data=").append(input_data);
        sb.append(", input_table=").append(input_tableName);
        sb.append(", input_overwriteId=").append(input_overwriteId);
        sb.append('}');
        return sb.toString();
    }

}
