package com.ops.management.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CompleteWorkOrderRequest {

    @NotBlank(message = "解决方案不能为空")
    private String solution;
}
