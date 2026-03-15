package com.ops.management.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class InspectionTaskRequest {

    @NotBlank(message = "任务名称不能为空")
    private String name;

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "执行人ID不能为空")
    private Long executorId;

    @NotNull(message = "计划时间不能为空")
    private LocalDateTime planTime;
}
