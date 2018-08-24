package cn.evun.sweet.core.test.mongodb;

import cn.evun.sweet.core.mongodb.IdEntity;

/**
 * Created by Administrator on 2017/2/24.
 */
public class TestDO extends IdEntity {

    private static final long serialVersionUID = 1645425240622915630L;

    private Integer age;

    private String name;

    private String addr;

    public TestDO(Integer age, String name, String addr) {
        this.age = age;
        this.name = name;
        this.addr = addr;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "TestDO{" + "age=" + age + ", name='" + name + '\'' + ", addr='" + addr + '\'' + '}';
    }
}
