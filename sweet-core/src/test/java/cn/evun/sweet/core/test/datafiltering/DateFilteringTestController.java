package cn.evun.sweet.core.test.datafiltering;

import cn.evun.sweet.core.common.JsonResultDO;
import cn.evun.sweet.core.datafiltering.DataFiltering;
import cn.evun.sweet.core.datafiltering.SerializedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shentao
 * @date 2017/5/4 14:42
 * @since 1.0.0
 */
@RestController
public class DateFilteringTestController {

    @Autowired
    DateFilteringTestService tstService;

    @DataFiltering(serializedFields = {@SerializedField(includes = {"name", "age", "gmtCreate"})})
    public JsonResultDO testIndex() {
        JsonResultDO result = new JsonResultDO();
        result.setSuccess(true);
        result.addAttribute(JsonResultDO.RETURN_OBJECT_KEY, tstService.getUserList());
        return result;
    }

}
