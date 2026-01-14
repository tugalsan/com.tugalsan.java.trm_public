package com.tugalsan.app.table.sg;

import com.tugalsan.api.servlet.gwt.webapp.client.TGS_SGWTFuncBase;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AppSGFConfigValueSet extends TGS_SGWTFuncBase implements IsSerializable {

    @Override
    public String getSuperClassName() {
        return AppSGFConfigValueSet.class.getSimpleName();
    }

    public AppSGFConfigValueSet() {
    }

    public AppSGFConfigValueSet(String targetObject, String parameter, String value) {
        input_targetObject = targetObject;
        input_parameter = parameter;
        input_value = value;
    }

    private String input_targetObject = null, input_parameter = null, input_value = null;
    private Long output_id = null;

    public String getInput_targetObject() {
        return input_targetObject;
    }

    public void setInput_targetObject(String input_targetObject) {
        this.input_targetObject = input_targetObject;
    }

    public String getInput_parameter() {
        return input_parameter;
    }

    public void setInput_parameter(String input_parameter) {
        this.input_parameter = input_parameter;
    }

    public String getInput_value() {
        return input_value;
    }

    public void setInput_value(String input_value) {
        this.input_value = input_value;
    }

    public Long getOutput_id() {
        return output_id;
    }

    public void setOutput_id(Long output_id) {
        this.output_id = output_id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(AppSGFConfigValueSet.class.getSimpleName());
        sb.append("{output_id=").append(output_id);
        sb.append(", input_targetObject=").append(input_targetObject);
        sb.append(", input_parameter=").append(input_parameter);
        sb.append(", input_value=").append(input_value);
        sb.append('}');
        return sb.toString();
    }
}
