package com.ops.management.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExecuteInspectionRequest {

    @NotNull(message = "巡检结果不能为空")
    private Integer result;

    private String remark;
}
