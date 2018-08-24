package cn.evun.sweet.core.test.hystrix;

import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.common.R;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;

/**
 * @author shentao
 * @date 2017/4/27 20:42
 * @since 1.0.0
 */
@Service
public class TestService {

    @HystrixCommand(scene = R.hystrix.CURRENTLIMITING_SCENE, highConcurrency = false, fallbackMethod = "currentLimitingFallback123")
    public JsonResultDO compute(String arg) throws Exception {
        throw new Exception("test compute error");
//        System.out.println("run compute");
//        return new JsonResultDO("test compute");
    }

    /**
     * 接口限流默认降级处理方法
     */
    public JsonResultDO currentLimitingFallback123(String arg) {
        System.out.println("run currentLimitingFallback");
        System.out.println(arg);
        JsonResultDO result = new JsonResultDO();
        result.setSuccess(false);
        result.setMsgCode(R.exception.excode_hystrix_current_limiting);
        return result;
    }

    /**
     * 容错隔离默认降级处理方法
     */
    public JsonResultDO faultTolerantFallback123(String arg) {
        System.out.println("run faultTolerantFallback");
        JsonResultDO result = new JsonResultDO();
        result.setSuccess(false);
        result.setMsgCode(R.exception.excode_hystrix_fault_tolerant);
        return result;
    }

}
