package cn.evun.sweet.core.test.datafiltering;

import java.io.Serializable;
import java.util.Date;

/**
 * @author shentao
 * @date 2017/5/4 14:43
 * @since 1.0.0
 */
public class UserDO implements Serializable {

    private static final long serialVersionUID = -3199058737305866816L;

    private String name;

    private String sex;

    private Integer age;

    private String address;

    private String phone;

    private String email;

    private Date gmtCreate;

    public UserDO() {
    }

    public UserDO(String name, String sex, Integer age, String address, String phone, String email, Date gmtCreate) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.gmtCreate = gmtCreate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}
