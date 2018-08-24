package cn.evun.sweet.core.test.hystrix;

import cn.evun.sweet.core.hystrix.CustomHystrixCommandAspect;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

/**
 * @author shentao
 * @date 2017/4/27 20:39
 * @since 1.0.0
 */
//@Configuration
public class HystrixConfig {

    @Bean
    public CustomHystrixCommandAspect createCustomHystrixCommandAspect() {
        return new CustomHystrixCommandAspect();
    }

}
