package com.lunar.common.core.utils;

import com.lunar.common.core.domain.IPage;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.lunar.common.core.domain.IPage;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 分页工具类
 */
public class PageUtil {

    /**
     * 设置请求分页数据
     */
    public static void startPage(int pageNum, int pageSize) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(pageSize, 1);
//        pageSize = Math.min(pageSize, PageParam.MAX_PAGE_SIZE);
        PageHelper.startPage(pageNum, pageSize);
    }

    /**
     * 设置请求分页数据-排序
     */
    public static void startPage(int pageNum, int pageSize, String orderBy) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.max(pageSize, 1);
//        pageSize = Math.min(pageSize, PageParam.MAX_PAGE_SIZE);
        PageHelper.startPage(pageNum, pageSize).setOrderBy(orderBy);
    }

    /**
     * 手动分页
     *
     * @param pageNum
     * @param pageSize
     * @param list
     * @param <T>
     * @return
     */
    public static <T> IPage<T> page(int pageNum, int pageSize, List<T> list) {
        List<T> dataList = pageList(pageNum, pageSize, list);
        int total = CollectionUtils.size(list);
        int pages = (total == 0 ? 0 : total / pageSize + 1);
        int size = CollectionUtils.size(dataList);

        IPage<T> page = new IPage<>(pageNum, pageSize, size, total, pages, dataList);

        return page;
    }

    /**
     * 手动分页
     *
     * @param pageNum  页码
     * @param pageSize 页数
     * @param list     分页前的集合
     * @param <T>
     * @return 分页后的集合
     */
    public static <T> List<T> pageList(int pageNum, int pageSize, List<T> list) {
        if (pageSize < 1) {
            return Lists.newArrayList();
        }
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }

        // 计算总页数
        int page = (list.size() % pageSize == 0
            ? list.size() / pageSize
            : list.size() / pageSize + 1);
        // 兼容性分页参数错误
        pageNum = pageNum <= 0 ? 1 : pageNum;
        // 页码大于总页数，返回空数组
        if (pageNum > page) {
            return Lists.newArrayList();
        }
//        pageNum = pageNum >= page ? page : pageNum;

        // 开始索引
        int begin = 0;
        // 结束索引
        int end = 0;
        if (pageNum != page) {
            begin = (pageNum - 1) * pageSize;
            end = begin + pageSize;
        } else {
            begin = (pageNum - 1) * pageSize;
            end = list.size();
        }

        return list.subList(begin, end);
    }

    public static void main(String[] args) {
        List<String> list = Lists.newArrayList("a", "b", "c", "d", "e", "f", "g");

        System.out.println(pageList(1, 5, list));
        System.out.println(pageList(2, 5, list));
        System.out.println(pageList(3, 5, list));

        System.out.println(page(1, 5, list));
        System.out.println(page(2, 5, list));
        System.out.println(page(1, 5, Lists.newArrayList()));
    }

}
