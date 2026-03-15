package com.ops.management.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProcessWorkOrderRequest {

    @NotBlank(message = "处理说明不能为空")
    private String processNote;
}
