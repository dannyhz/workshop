package cn.evun.sweet.core.test.cache.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Administrator on 2017/3/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class CommonTest {

    @Test
    public void testOne() throws Exception {
        String classPath = this.getClass().getClassLoader().getResource("").getPath();
        System.out.println(classPath);
        classPath = classPath.endsWith("/") ? classPath : classPath + "/";
        File dir = new File(classPath + "");
        if (!dir.isDirectory()) return;

        Properties properties = new Properties();
        File[] files = dir.listFiles();
        for (File file : files){
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (suffix.equals("properties")){
                properties.load(new FileInputStream(file));
            }
        }
        String mongoHostport = properties.getProperty("mongo.hostport");
        System.out.println(mongoHostport);
    }

}
