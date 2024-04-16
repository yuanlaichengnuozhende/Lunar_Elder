package com.lunar.common.core.domain;

import com.lunar.common.core.request.PageParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.lunar.common.core.request.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author szx
 */
@SuppressWarnings("rawtypes")
@Data
@AllArgsConstructor
public class IPage<T> {

    /**
     * 页码，从1开始
     */
    @ApiModelProperty(value = "页码，从1开始")
    private int pageNum;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小")
    private int pageSize;

    /**
     * 当前页的数量
     */
    @ApiModelProperty(value = "当前页的数量")
    private int size;

    /**
     * 总数
     */
    @ApiModelProperty(value = "总数")
    private int total;

    /**
     * 总页数
     */
    @ApiModelProperty(value = "总页数")
    private int pages;

    /**
     * list
     */
    @ApiModelProperty(value = "list")
    private List<T> list = Lists.newArrayList();

    public IPage() {
        this.pageNum = 1;
        this.pageSize = 10;
        this.list = Collections.emptyList();
    }

    public IPage(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public IPage(int pageNum, int pageSize, int size, int total, int pages) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.size = size;
        this.total = total;
        this.pages = pages;
    }

    public IPage(Page page) {
        this.pageNum = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.size = page.size();
        this.total = (int) page.getTotal();
        this.pages = page.getPages();
        this.list = page.getResult();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(list);
    }

    public static IPage emptyPage(PageParam pageParam) {
        return emptyPage(pageParam.getPageNum(), pageParam.getPageSize());
    }

    public static IPage emptyPage(int pageNum, int pageSize) {
        return new IPage(pageNum, pageSize);
    }

    public static <T, K> IPage<K> reNew(IPage<T> oldPage, List<K> newList) {
        IPage newPage = new IPage<>(
            oldPage.getPageNum(),
            oldPage.getPageSize(),
            oldPage.getSize(),
            oldPage.getTotal(),
            oldPage.getPages());
        newPage.setList(newList);
        return newPage;
    }

}
