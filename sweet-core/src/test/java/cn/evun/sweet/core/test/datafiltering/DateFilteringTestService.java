package cn.evun.sweet.core.test.datafiltering;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author shentao
 * @date 2017/5/4 14:43
 * @since 1.0.0
 */
@Service
public class DateFilteringTestService {

    public UserDO getSingleUser() {
        return new UserDO("st", "男", 25, "cctv", "13654896321", "a@b.c", new Date());
    }

    public List<UserDO> getUserList() {
        List<UserDO> list = new ArrayList<>();
        list.add(new UserDO("st", "男", 25, "cctv", "13654896321", "a@b.c", new Date()));
        list.add(new UserDO("fgf", "男", 44, "bztv", "15698652145", "b@c.d", new Date()));
        list.add(new UserDO("hfr", "女", 18, "hztv", "14698563214", "f@g.u", new Date()));
        return list;
    }

}
