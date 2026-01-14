package com.tugalsan.app.table.sg.rev;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.List;
import com.tugalsan.api.sql.cell.client.TGS_SQLCellAbstract;

public class AppSGFRevGetRowData extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFRevGetRowData.class.getSimpleName();
    }

    public AppSGFRevGetRowData() {
    }

    public AppSGFRevGetRowData(CharSequence tableName, Long id) {
        input_tableName = tableName;
        input_id = id;
        output_row = null;
    }

    private CharSequence input_tableName;
    private Long input_id;
    private List<TGS_SQLCellAbstract> output_row;

    public CharSequence getInput_tableName() {
        return input_tableName;
    }

    public void setInput_tableName(CharSequence input_tableName) {
        this.input_tableName = input_tableName;
    }

    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public List<TGS_SQLCellAbstract> getOutput_row() {
        return output_row;
    }

    public void setOutput_row(List<TGS_SQLCellAbstract> output_row) {
        this.output_row = output_row;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(AppSGFRevGetRowData.class.getSimpleName());
        sb.append("{output_row=").append(output_row);
        sb.append(", input_id=").append(input_id);
        sb.append('}');
        return sb.toString();
    }

}
