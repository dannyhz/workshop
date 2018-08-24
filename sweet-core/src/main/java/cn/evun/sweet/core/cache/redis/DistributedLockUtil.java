package cn.evun.sweet.core.cache.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.dao.DataAccessException;

import cn.evun.sweet.core.spring.SpringContext;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 利用Redis实现分布式锁<br/><br/>
 * &nbsp;&nbsp;使用方法：<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;try {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (DistributedLockUtil.acquireLock(key, -1)) {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//获取锁成功后的操作<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;} else {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//获取锁失败后的操作<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;finally {<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DistributedLockUtil.releaseLock(key);<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;}
 *
 * @author shentao
 * @since 1.1.0
 */
public class DistributedLockUtil {

    private static final RedisTemplate<String, Object> redisTemplate = SpringContext.getBean("redisTemplate");

    @SuppressWarnings("unchecked")
    private static final RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();

    private static final FSTRedisSerializer<Long> fSTRedisSerializer = SpringContext.getBean("fstRedisSerializer");

    /**
     * 通过SETNX试图获取一个锁
     *
     * @param key
     * @param pexpire 存活时间(毫秒)
     * @return 获得锁是否成功(true|false)
     */
    public static Boolean pAcquireLock(String key, final long pexpire) {
        final byte[] _key = keySerializer.serialize(key);
        return redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    //获取当前系统时间，加上key存活时间，再加一毫秒
                    Long value = System.currentTimeMillis() + pexpire + 1;
                    byte[] _value = fSTRedisSerializer.serialize(value);
                    boolean isDo = connection.setNX(_key, _value);
                    if (isDo) { // SETNX成功，则成功获取一个锁
                        if (pexpire > 0) {
                            connection.pExpire(_key, pexpire);
                        }
                        return Boolean.TRUE;
                    } else { // SETNX失败，说明锁仍然被其他对象保持，检查其是否已经超时
                        Long oldValue = fSTRedisSerializer.deserialize(connection.get(_key));
                        if (oldValue.longValue() < System.currentTimeMillis()) { // 超时
                            Long getValue = fSTRedisSerializer.deserialize(connection.getSet(_key, _value));
                            if (getValue.longValue() == oldValue.longValue()) { // 获取锁成功
                                return Boolean.TRUE;
                            } else { // 已被其他进程捷足先登了
                                return Boolean.FALSE;
                            }
                        } else { // 未超时，则直接返回失败
                            return Boolean.FALSE;
                        }
                    }
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
            }

        });
    }

    /**
     * 通过SETNX试图获取一个锁
     *
     * @param key
     * @param expire 存活时间(秒)
     * @return 获得锁是否成功(true|false)
     */
    public static Boolean acquireLock(String key, final long expire) {
        return pAcquireLock(key, expire * 1000);
    }

    /**
     * 释放锁
     *
     * @param key
     */
    public static void releaseLock(String key) {
        final byte[] _key = keySerializer.serialize(key);
        redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] value = connection.get(_key);
                if (value == null) {
                    return null;
                }
                Long getValue = fSTRedisSerializer.deserialize(value);
                // 避免删除非自己获取得到的锁
                if (System.currentTimeMillis() < getValue.longValue()) {
                    connection.del(_key);
                }

                return null;
            }

        });
    }

}
