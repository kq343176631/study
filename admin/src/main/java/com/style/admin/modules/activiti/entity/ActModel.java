package com.style.admin.modules.activiti.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Api(tags="模型")
public class ActModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模型名称")
    @NotBlank(message="{model.name.require}")
    private String name;

    @ApiModelProperty(value = "模型标识")
    @NotBlank(message="{model.key.require}")
    private String key;

    @ApiModelProperty(value = "模型描述")
    private String description;
}
