package cn.evun.sweet.core.test.common;

import cn.evun.sweet.core.common.JsonResultDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author shentao
 * @date 2018/1/10 11:05
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class JsonResultDOTester {

    @Test
    public void testeGetTimeMillis() {
        System.out.println(new JsonResultDO().toJson());
    }

}
