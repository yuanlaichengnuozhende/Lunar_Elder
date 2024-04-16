package com.lunar.common.core.thirdapi.baidu.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author szx
 * @date 2022/06/20 14:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageClassifyInput implements Serializable {

    /**
     * 和image二选一
     * <p>
     * 图片完整URL，URL长度不超过1024字节，URL对应的图片base64编码后大小不超过4M，最短边至少15px，最长边最大4096px,支持jpg/png/bmp格式，当image字段存在时url字段失效。
     */
    @JsonInclude(Include.NON_NULL)
    private String url;

    /**
     * 图像数据，base64编码，要求base64编码后大小不超过4M，最短边至少15px，最长边最大4096px,支持jpg/png/bmp格式。注意：图片需要base64编码、去掉编码头（data:image/jpg;base64,）后，再进行urlencode。
     */
    @JsonInclude(Include.NON_NULL)
    private String image;

//    /**
//     * 用于控制返回结果是否带有百科信息，若不输入此参数，则默认不返回百科结果；若输入此参数，会根据输入的整数返回相应个数的百科信息
//     */
//    private Integer baike_num;
}
