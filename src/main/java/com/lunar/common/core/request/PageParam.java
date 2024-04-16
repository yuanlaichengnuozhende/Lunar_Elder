package com.lunar.common.core.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author szx
 * @date 2022/09/15 14:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("PageParam")
public class PageParam {

    /**
     * 每次查询最大页面数
     */
//    public final static int MAX_PAGE_SIZE = 200;

    @ApiModelProperty(value = "页码", required = true, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页条数", required = true, example = "10")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为1")
//    @Max(value = MAX_PAGE_SIZE, message = "每页条数最大值为 200")
    private Integer pageSize = 10;

}
