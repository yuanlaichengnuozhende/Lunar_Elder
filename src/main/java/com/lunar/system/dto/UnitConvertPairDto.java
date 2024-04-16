package com.lunar.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author szx
 * @date 2022/11/07 19:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitConvertPairDto implements Serializable {

    private static final long serialVersionUID = -2236891638675955974L;

    private String unitFrom;

    private String unitTo;

}


