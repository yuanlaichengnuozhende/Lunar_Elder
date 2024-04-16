package com.lunar.system.model;

import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点工具
 */
public class OrgTreeUtil {

    private static List<OrgTree> all = null;

    /**
     * 转换为树形
     *
     * @param list
     * @return
     */
    public static List<OrgTree> toTree(final List<OrgTree> list) {
        return toTree(list, 0L);
    }

    /**
     * 转换为树形
     *
     * @param list
     * @param rootCode 传入rootCode
     * @return
     */
    public static List<OrgTree> toTree(final List<OrgTree> list, Long rootCode) {
        rootCode = (rootCode == null ? 0L : rootCode);
        all = new ArrayList<>(list);

        // 所有根节点
        List<OrgTree> roots = Lists.newArrayList();

        for (OrgTree tree : list) {
            if (NumberUtil.compare(rootCode, tree.getPcode()) == 0) {
                roots.add(tree);
            }
        }

        // 从all中移除根节点
        all.removeAll(roots);

        for (OrgTree tree : roots) {
            // 递归查询子节点
            tree.setChildren(getNodeChildren(tree)); ;
        }
        return roots;
    }

    /**
     * 递归查询子节点，没有子节点时终止
     *
     * @param parent 父节点
     * @return 子节点
     */
    private static List<OrgTree> getNodeChildren(final OrgTree parent) {
        // 当前节点child为null初始化
        List<OrgTree> childList = parent.getChildren() == null ? new ArrayList<>() : parent.getChildren();

        // 找到当前节点的所有子节点
        for (OrgTree child : all) {
            if (child.getPcode() == parent.getCode()) {
                childList.add(child);
            }
        }

        // 将当前节点的所有子节点从all中删除
        all.removeAll(childList);

        // 所有的子节点再寻找它们自己的子节点
        for (OrgTree tree : childList) {
            tree.setChildren(getNodeChildren(tree));
        }

        return childList;
    }

}