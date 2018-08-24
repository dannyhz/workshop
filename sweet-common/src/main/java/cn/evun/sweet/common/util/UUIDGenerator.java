package cn.evun.sweet.common.util;

import java.util.UUID;

/**
 * UUID生成器
 *
 * @author shentao
 * @date 2017/5/27 15:51
 * @since 1.0.0
 */
public class UUIDGenerator {

    public static String genUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replace("-", "");
    }

    /**
     * 获取指定数量的UUID
     *
     * @param number
     * @return
     */
    public static String[] genUUID(int number) {
        if (number < 1) {
            return new String[]{genUUID()};
        }
        String[] ss = new String[number];
        for (int i = 0; i < number; i++) {
            ss[i] = genUUID();
        }
        return ss;
    }

}
