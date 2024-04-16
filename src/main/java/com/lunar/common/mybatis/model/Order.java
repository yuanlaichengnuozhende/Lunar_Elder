package com.lunar.common.mybatis.model;

import com.lunar.common.core.utils.collection.Pair;
import com.google.common.collect.Lists;
import com.lunar.common.core.utils.collection.Pair;

import java.util.List;
import java.util.StringJoiner;

/**
 * mysql排序
 *
 * @author szx
 * @date 2023/02/21 15:33
 */
public class Order {

    public static final String ID = "id";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";

    public static String order(List<Pair<String, OrderDirection>> orders) {
        StringJoiner joiner = new StringJoiner(",");
        orders.forEach(x -> {
            joiner.add(order(x.getKey(), x.getValue()));
        });

        return joiner.toString();
    }

    @SafeVarargs
    public static String order(Pair<String, OrderDirection>... orders) {
        return order(Lists.newArrayList(orders));
    }

    public static String order(String field, OrderDirection orderDirection) {
        return field + " " + orderDirection.getKey();
    }

    public static String idAsc() {
        return order(ID, OrderDirection.ASC);
    }

    public static String idDesc() {
        return order(ID, OrderDirection.DESC);
    }

    public static String createTimeAsc() {
        return order(CREATE_TIME, OrderDirection.ASC);
    }

    public static String createTimeDesc() {
        return order(CREATE_TIME, OrderDirection.DESC);
    }

    public static String updateTimeAsc() {
        return order(UPDATE_TIME, OrderDirection.ASC);
    }

    public static String updateTimeDesc() {
        return order(UPDATE_TIME, OrderDirection.DESC);
    }

    public static void main(String[] args) {
        System.out.println(createTimeAsc());

        System.out.println(order(Pair.of("create_time", OrderDirection.DESC), Pair.of("id", OrderDirection.ASC)));

        System.out.println(order());

        System.out.println(idAsc());

        System.out.println(
            Order.order(Pair.of(Order.CREATE_TIME, OrderDirection.DESC), Pair.of(Order.ID, OrderDirection.DESC)));
    }

}
