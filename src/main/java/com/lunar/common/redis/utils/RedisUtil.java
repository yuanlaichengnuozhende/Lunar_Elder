package com.lunar.common.redis.utils;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * redis工具
 *
 * @author szx
 * @date 2021/02/22 10:34
 */
@SuppressWarnings("all")
@Slf4j
public class RedisUtil {

//    private static final RedisUtil staticInstance = new RedisUtil();
//
//    @Autowired(required = false)
//    private RedisTemplate redisTemplate;
//
//    @PostConstruct
//    public void init() {
//        redisTemplate = redisTemplate;
//    }

    private static RedisTemplate redisTemplate;

    /**
     * 初始化
     *
     * @param redisTemplate
     */
    public static void init(RedisTemplate redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
        log.info("初始化RedisUtil");
    }

    //=============================common============================

    /**
     * 设置key失效时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 单位
     * @return
     */
    public static boolean expire(final String key, final long time, final TimeUnit timeUnit) {
        Assert.notBlank(key, "key cannot be null");
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("设置key失效时间失败。key={}, time={}, timeUnit={}", key, time, timeUnit, e);
            return false;
        }
    }

    /**
     * 设置key失效时间（秒）
     *
     * @param key  键
     * @param time 时间
     * @return
     */
    public static boolean expire(final String key, final long time) {
        return expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 设置key失效时间
     *
     * @param key  键
     * @param date
     * @return
     */
    public static boolean expireAt(final String key, final Date date) {
        Assert.notBlank(key, "key cannot be null");
        try {
            if (date != null) {
                redisTemplate.expireAt(key, date);
            }
            return true;
        } catch (Exception e) {
            log.error("设置key失效时间失败。key={}, date={}", key, date, e);
            return false;
        }
    }

    /**
     * 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) -1永久有效；-2key不存在
     */
    public static long ttl(final String key) {
        Assert.notBlank(key, "key cannot be null");
        //-1永久有效；-2key不存在
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return
     */
    public static boolean hasKey(final String key) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("判断key是否存在失败。key={}", key, e);
            return false;
        }
    }

    /**
     * 删除key
     *
     * @param key
     */
    public static void del(final String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 删除keyList
     *
     * @param keyList
     */
    public static void del(final List<String> keyList) {
        redisTemplate.delete(keyList);
    }

    /**
     * 删除以prefix为前缀的key
     *
     * @param prefix 前缀
     */
    public static void delPrefix(final String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return;
        }

        //每次遍历数量
        int limit = 100;

        String pattern = prefix + "*";
        Set<String> keySet = scan(pattern, limit);
        if (!CollectionUtils.isEmpty(keySet)) {
            redisTemplate.delete(keySet);
        }
    }

    /**
     * scan查询key
     *
     * @param pattern 正则
     * @param limit   每次遍历个数
     * @return 所有匹配的key。与limit无关
     */
    public static Set<String> scan(final String pattern, final int limit) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(limit).build();

        return (Set<String>) redisTemplate.execute((RedisCallback) connection -> {
            Set<String> binaryKeys = new HashSet<>();

            Cursor<byte[]> cursor = connection.scan(options);
            while (cursor.hasNext()) {
                binaryKeys.add(new String(cursor.next()));
            }
            return binaryKeys;
        });
    }

    //============================String=============================

    /**
     * string get
     *
     * @param key 键
     * @return 值
     */
    public static <T> T get(final String key) {
        Assert.notBlank(key, "key cannot be null");
        ValueOperations<String, T> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * string set
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean set(final String key, final T value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("string set失败。key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * string set并设置超时时间
     *
     * @param key      键
     * @param value    值
     * @param time     如果time小于等于0 将设置无限期
     * @param timeUnit
     * @return
     */
    public static <T> boolean set(final String key, final T value, final long time, final TimeUnit timeUnit) {
        Assert.notBlank(key, "key cannot be null");
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("string set并设置超时时间失败。key={}, value={}, time={}, timeUnit={}", key, value, time, timeUnit,
                      e);
            return false;
        }
    }

    /**
     * string设置并设置超时时间(秒)
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) 如果time小于等于0 将设置无限期
     * @param <T>
     * @return
     */
    public static <T> boolean set(final String key, final T value, final long time) {
        Assert.notBlank(key, "key cannot be null");
        return set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * string setnx
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean setnx(final String key, final T value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            log.error("string setnx失败。key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * string setnx
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean setnx(final String key, final T value, final long time, final TimeUnit timeUnit) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
        } catch (Exception e) {
            log.error("string setnx失败。key={}, value={}, time={}, timeUnit={}", key, value, time, timeUnit, e);
            return false;
        }
    }

    /**
     * string setnx(秒)
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean setnx(final String key, final T value, final long time) {
        Assert.notBlank(key, "key cannot be null");
        return setnx(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * string value增加delta
     *
     * @param key   键
     * @param delta 可以为负值
     * @return
     */
    public static long incr(final String key, final long delta) {
        Assert.notBlank(key, "key cannot be null");
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * string value自增
     *
     * @param key 键
     * @return
     */
    public static long incr(final String key) {
        Assert.notBlank(key, "key cannot be null");
        return redisTemplate.opsForValue().increment(key);
    }

    //=============================hash============================

    /**
     * hash get
     *
     * @param key   键 不能为null
     * @param filed 项 不能为null
     * @return 值
     */
    public static <T> T hget(final String key, final String filed) {
        Assert.notBlank(key, "key cannot be null");
        checkNotNull(filed, "filed cannot be null");
        HashOperations<String, String, T> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, filed);
    }

    /**
     * hash set
     *
     * @param key   键
     * @param filed 项
     * @param value 值
     * @return
     */
    public static <T> boolean hset(final String key, final String filed, final T value) {
        Assert.notBlank(key, "key cannot be null");
        Assert.notBlank(filed, "filed cannot be null");
        try {
            redisTemplate.opsForHash().put(key, filed, value);
            return true;
        } catch (Exception e) {
            log.error("hash set失败。key={}, filed={}, value={}", key, filed, value, e);
            return false;
        }
    }

    /**
     * hash set
     *
     * @param key      键
     * @param filed    项
     * @param value    值
     * @param time     时间
     * @param timeUnit
     * @return
     */
    public static <T> boolean hset(final String key, final String filed, final T value,
                                   final long time, final TimeUnit timeUnit) {
        Assert.notBlank(key, "key cannot be null");
        Assert.notBlank(filed, "filed cannot be null");
        try {
            redisTemplate.opsForHash().put(key, filed, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("hash set失败。key={}, filed={}, value={}", key, filed, value, e);
            return false;
        }
    }

    /**
     * hash set（秒）
     *
     * @param key   键
     * @param filed 项
     * @param value 值
     * @param time  时间
     * @return
     */
    public static <T> boolean hset(final String key, final String filed, final T value, final long time) {
        Assert.notBlank(key, "key cannot be null");
        Assert.notBlank(filed, "filed cannot be null");
        return hset(key, filed, value, time, TimeUnit.SECONDS);
    }

    /**
     * hash mset
     *
     * @param key 键
     * @param map 对应多个键值
     * @return
     */
    public static <T> boolean hmset(final String key, final Map<String, T> map) {
        Assert.notBlank(key, "key cannot be null");
        checkNotNull(map, "map cannot be null");
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("hash mset失败。key={}, map={}", key, map);
            return false;
        }
    }

    /**
     * hash mset
     *
     * @param key 键
     * @param map 对应多个键值
     * @return
     */
    public static <T> boolean hmset(final String key, final Map<String, T> map, final long time,
                                    final TimeUnit timeUnit) {
        Assert.notBlank(key, "key cannot be null");
        checkNotNull(map, "map cannot be null");
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("hash mset失败。key={}, map={}", key, map, e);
            return false;
        }
    }

    /**
     * hash mset（秒）
     *
     * @param key 键
     * @param map 对应多个键值
     * @return
     */
    public static <T> boolean hmset(final String key, final Map<String, T> map, final long time) {
        Assert.notBlank(key, "key cannot be null");
        checkNotNull(map, "map cannot be null");
        return hmset(key, map, time, TimeUnit.SECONDS);
    }

    /**
     * hash 删除filed
     *
     * @param key   键 不能为null
     * @param filed 项 可以是多个 不能为null
     */
    public static void hdel(final String key, final String... filed) {
        Assert.notBlank(key, "key cannot be null");
        redisTemplate.opsForHash().delete(key, filed);
    }

    /**
     * hash 判断hash中是否有field
     *
     * @param key   键 不能为null
     * @param filed 项 不能为null
     * @return
     */
    public static boolean hexists(final String key, final String filed) {
        Assert.notBlank(key, "key cannot be null");
        Assert.notBlank(filed, "filed cannot be null");
        return redisTemplate.opsForHash().hasKey(key, filed);
    }

    /**
     * hash 获取key对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static <T> Map<String, T> hgetall(final String key) {
        Assert.notBlank(key, "key cannot be null");
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * hash 增加delta
     *
     * @param key   键
     * @param filed 项
     * @param delta 允许负值
     * @return
     */
    public static double hincr(final String key, final String filed, final long delta) {
        Assert.notBlank(key, "key cannot be null");
        Assert.notBlank(filed, "filed cannot be null");
        return redisTemplate.opsForHash().increment(key, filed, delta);
    }

    /**
     * hash 自增
     *
     * @param key   键
     * @param filed 项
     * @return
     */
    public static double hincr(final String key, final String filed) {
        Assert.notBlank(key, "key cannot be null");
        Assert.notBlank(filed, "filed cannot be null");
        return redisTemplate.opsForHash().increment(key, filed, 1L);
    }

    //============================set=============================

    /**
     * set members
     *
     * @param key 键
     * @return
     */
    public static <T> Set<T> smembers(final String key) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("set members失败。key={}", key, e);
            return null;
        }
    }

    /**
     * set sismember
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean sismember(final String key, final T value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("set sismember失败。key={}", key, e);
            return false;
        }
    }

    /**
     * set sadd
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static <T> long sadd(final String key, final T... values) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("set sadd失败。key={}", key, e);
            return 0;
        }
    }

    /**
     * set sadd
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 单位
     * @param values   值 可以是多个
     * @return 成功个数
     */
    public static <T> long saddWithTime(final String key, final long time, final TimeUnit timeUnit, final T... values) {
        Assert.notBlank(key, "key cannot be null");
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return count;
        } catch (Exception e) {
            log.error("set sadd失败。key={}", key, e);
            return 0;
        }
    }

    /**
     * set sadd
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static <T> long saddWithSecond(final String key, final long time, final T... values) {
        Assert.notBlank(key, "key cannot be null");
        return sadd(key, time, TimeUnit.SECONDS, values);
    }

    /**
     * set scard
     *
     * @param key 键
     * @return
     */
    public static long scard(final String key) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("set scard失败。key={}", key, e);
            return 0;
        }
    }

    /**
     * set 移除值为value的member
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static <T> long sRemove(final String key, final T... values) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("sRemove失败。key={}", key, e);
            return 0;
        }
    }

    //============================list=============================

    /**
     * list lrange 获取start到end之间元素
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public static <T> List<T> lrange(final String key, final long start, final long end) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("list lrange失败。key={}", key, e);
            return null;
        }
    }

    /**
     * list llen list长度
     *
     * @param key 键
     * @return
     */
    public static long llen(final String key) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("list llen失败。key={}", key, e);
            return 0;
        }
    }

    /**
     * list lindex 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public static <T> T lindex(final String key, final long index) {
        Assert.notBlank(key, "key cannot be null");
        try {
            ListOperations<String, T> listOperations = redisTemplate.opsForList();
            return listOperations.index(key, index);
        } catch (Exception e) {
            log.error("list lindex失败。key={}", key, e);
            return null;
        }
    }

    /**
     * list rpush 在列表中添加一个或多个值
     *
     * @param key
     * @param list
     * @param <T>
     * @return
     */
    public static <T> boolean rpushList(final String key, List<T> list) {
        Assert.notBlank(key, "key cannot be null");
        try {
            redisTemplate.opsForList().rightPushAll(key, list.toArray());
            return true;
        } catch (Exception e) {
            log.error("list rpush失败。key={}", key, e);
            return false;
        }
    }

    /**
     * list rpush 在列表中添加一个或多个值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean rpushListWithTime(final String key, final long time, final TimeUnit timeUnit,
                                                List<T> list) {
        Assert.notBlank(key, "key cannot be null");
        try {
            rpushList(key, list);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("list rpush失败。key={}", key, e);
            return false;
        }
    }

    /**
     * list rpush 在列表中添加一个或多个值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean rpush(final String key, final T... value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("list rpush失败。key={}", key, e);
            return false;
        }
    }

    /**
     * list rpush 在列表中添加一个或多个值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean rpushWithTime(final String key, final long time, final TimeUnit timeUnit,
                                            final T... value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            rpush(key, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("list rpush失败。key={}", key, e);
            return false;
        }
    }

    /**
     * list rpush 在列表中添加一个或多个值（秒）
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> boolean rpushWithSecond(final String key, final long time, final T... value) {
        Assert.notBlank(key, "key cannot be null");
        return rpush(key, time, TimeUnit.SECONDS, value);
    }

    //============================zset=============================

    /**
     * zset sadd
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return
     */
    public static <T> boolean zadd(final String key, final T value, final double score) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("zset add失败。key={}", key, e);
            return false;
        }
    }

    /**
     * zset scard
     *
     * @param key 键
     * @return
     */
    public static long zcard(final String key) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("zset scard失败。key={}", key, e);
            return 0;
        }
    }

    /**
     * zset score
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> Double zscore(final String key, final T value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("zset zscore失败。key={}", key, e);
            return null;
        }
    }

    /**
     * zincrby
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public static <T> Double zincrby(final String key, final T value, final double score) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, score);
        } catch (Exception e) {
            log.error("zincrby失败。key={}", key, e);
            return null;
        }
    }

    /**
     * zset rank
     *
     * @param key   键
     * @param value 值
     * @return 从0开始
     */
    public static <T> Long zrank(final String key, final T value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            log.error("zset rank失败。key={}", key, e);
            return null;
        }
    }

    /**
     * zset range
     *
     * @param key   键
     * @param start
     * @param end
     * @return [start, end]
     */
    public static <T> Set<T> zrange(final String key, final long start, final long end) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("zset range失败。key={}", key, e);
            return null;
        }
    }

    /**
     * zrevrank
     *
     * @param key   键
     * @param value 值
     * @return 从0开始
     */
    public static <T> Long zrevrank(final String key, final T value) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Exception e) {
            log.error("zrevrank失败。key={}", key, e);
            return null;
        }
    }

    /**
     * zrevrange
     *
     * @param key   键
     * @param start
     * @param end
     * @return [start, end]
     */
    public static <T> Set<T> zrevrange(final String key, final long start, final long end) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("zrevrange失败。key={}", key, e);
            return null;
        }
    }

    /**
     * zset rem
     *
     * @param key    键
     * @param values
     * @return
     */
    public static <T> long zrem(final String key, final T... values) {
        Assert.notBlank(key, "key cannot be null");
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("zset rem失败。key={}", key, e);
            return 0;
        }
    }
}
