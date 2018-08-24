package cn.evun.sweet.core.test.hystrix;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author shentao
 * @date 2017/4/27 20:41
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class HystrixTest {

    @Autowired
    TestService testService;

    @Test
    public void testAspect() throws Exception {
        testService.compute("test");
        System.out.println("test testAspect compute");
    }

}
