package com.ops.management.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WorkOrderRequest {

    @NotBlank(message = "工单标题不能为空")
    private String title;

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @NotBlank(message = "问题描述不能为空")
    private String description;
}
