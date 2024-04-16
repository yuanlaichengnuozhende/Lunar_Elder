package com.lunar.common.core.response;

import com.lunar.common.core.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumResp {

    private int code;
    private String name;

    @JsonInclude(Include.NON_NULL)
    private List<EnumResp> subList;

    @JsonInclude(Include.NON_NULL)
    private String score;

    public EnumResp(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static EnumResp valueOf(BaseEnum baseEnum) {
        return new EnumResp(
            baseEnum.getCode(),
            baseEnum.getName()
        );
    }
}
