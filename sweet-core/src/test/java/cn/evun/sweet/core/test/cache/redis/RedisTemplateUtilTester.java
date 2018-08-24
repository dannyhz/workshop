package cn.evun.sweet.core.test.cache.redis;

import cn.evun.sweet.core.cache.redis.RedisTemplateUtil;
import cn.evun.sweet.core.test.mongodb.TestDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class RedisTemplateUtilTester {

    @Test
    public void testRedisList() {
        String key = "RedisTemplateUtilTester_testRedisList_0001";
        List<TestDO> list = new ArrayList<>();
        list.add(new TestDO(30, "starty", "bingjiang"));
        list.add(new TestDO(21, "dfd", "bingjidrjhkhkjfswang"));
        list.add(new TestDO(33, "stcvxarty", "fdsf"));
        RedisTemplateUtil.addInList(key, list);
        int size = new Double(Math.random() * 20).intValue();
        for (int i = 0; i < size; i++) {
            RedisTemplateUtil.addInList(key, new TestDO(25 + i, "cctv" + i, "hz" + i), 10);
        }
        List<TestDO> listNew = RedisTemplateUtil.getInList(key, 0, -1);
        System.out.println("list size: " + size);
        for (TestDO testDO : listNew) {
            System.out.println(testDO);
        }
    }

    @Test
    public void testList() {
        String key = "RedisTemplateUtilTester_testRedisList_0001";
        List<TestDO> listNew = RedisTemplateUtil.getInList(key, 0, -1);
        for (TestDO testDO : listNew) {
            System.out.println(testDO);
        }
    }

    @Test
    public void testExpire() {
        String key = "RedisTemplateUtilTester_testRedisList_0001";
        RedisTemplateUtil.expire(key, 3600);
    }

    @Test
    public void testDelete() {
        String key = "RedisTemplateUtilTester_testRedisList_0001";
        RedisTemplateUtil.delete(key);
    }

    @Test
    public void testOther() {
        String key = "RedisTemplateUtilTester_testOther_0001";
        for (int i = 0; i < 10; i++) {
            System.out.println(RedisTemplateUtil.atomicLong(key));
        }
        RedisTemplateUtil.delete(key);
    }

    @Test
    public void testOther1() {
        String key = "RedisTemplateUtilTester_testOther_0002";
        System.out.println(RedisTemplateUtil.atomicLong(key));
        System.out.println(RedisTemplateUtil.atomicLong(key, 10));
        System.out.println(RedisTemplateUtil.getString(key));
        RedisTemplateUtil.delete(key);
    }

    @Test
    public void testOther2() {
        String key = "RedisTemplateUtilTester_testOther_0003";
        RedisTemplateUtil.setString(key, "", 60);
        System.out.println(RedisTemplateUtil.exists(key));
    }

    @Test
    public void testHashMulti() {
        String key = "RedisTemplateUtilTester_testHashMulti_0001";
        String subKey = "subKey_0001";
        String field = "field_0001";
        String value = "testHashMulti_value_0001";
        Object obj = RedisTemplateUtil.hashMultiSet(key, subKey, field, value);
        System.out.println(obj);
        RedisTemplateUtil.delete(key);
    }

}
