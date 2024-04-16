package com.lunar.system.query;

import com.lunar.common.mybatis.query.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictEnumQuery implements Query {

    private Long id;
    private String dictType;
    private Character status;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
    /** 字典排序 */
    private Integer dictSort;
    /** 字典标签 */
    private String dictLabel;
    /** 字典键值 */
    private String dictValue;
    /** 所属分类 */
    private String sourceType;
    /** 关联值 */
    private String relatedValue;

    private Collection<Long> idList;

    private String likeDictLabel;

    private List<String> dictTypeList;

}