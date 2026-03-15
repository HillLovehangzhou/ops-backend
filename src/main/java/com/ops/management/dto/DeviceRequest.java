package com.ops.management.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DeviceRequest {

    @NotBlank(message = "设备编号不能为空")
    private String code;

    @NotBlank(message = "设备名称不能为空")
    private String name;

    private String model;

    @NotBlank(message = "设备类型不能为空")
    private String type;

    @NotBlank(message = "安装位置不能为空")
    private String location;

    @NotNull(message = "设备状态不能为空")
    private Integer status;

    private String parameters;
}
