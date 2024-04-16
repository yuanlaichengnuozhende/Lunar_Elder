package com.lunar.system.entity;

import com.lunar.common.mybatis.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("FactorGas")
public class FactorGas extends BaseEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 排放因子ID
     */
    @ApiModelProperty(value = "排放因子ID")
    private Long factorId;

    /**
     * 温室气体类型
     */
    @ApiModelProperty(value = "温室气体类型")
    private String gasType;

    /**
     * 温室气体-氢氟烷、全氟碳水合物需要字典
     */
    @ApiModelProperty(value = "温室气体-氢氟烷、全氟碳水合物需要字典")
    private String gas;

    /**
     * 因子数值
     */
    @ApiModelProperty(value = "因子数值")
    private String factorValue;

    /**
     * 因子单位-分子
     */
    @ApiModelProperty(value = "因子单位-分子")
    private String factorUnitZ;

    /**
     * 因子单位-分母
     */
    @ApiModelProperty(value = "因子单位-分母")
    private String factorUnitM;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private Long updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 数据有效
     */
    public boolean isValid() {
        return !StringUtils.isAnyBlank(gasType, factorValue, factorUnitZ, factorUnitM);
    }

}
