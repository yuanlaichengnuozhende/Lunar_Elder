package com.lunar.common.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author szx
 * @date 2022/03/03 20:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coordinate implements Serializable {

    private List<String> xaxis;

    private List<String> yaxis;

}
