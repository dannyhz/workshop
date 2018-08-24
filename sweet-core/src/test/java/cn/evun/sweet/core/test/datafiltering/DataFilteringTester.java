package cn.evun.sweet.core.test.datafiltering;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author shentao
 * @date 2017/5/4 14:42
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class DataFilteringTester {

    @Autowired
    DateFilteringTestController dateFilteringTestController;

    @Test
    public void testIndex() {
        dateFilteringTestController.testIndex();
    }

}
