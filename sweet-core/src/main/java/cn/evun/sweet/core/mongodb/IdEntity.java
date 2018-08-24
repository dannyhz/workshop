package cn.evun.sweet.core.mongodb;

import java.io.Serializable;

/**
 * mongodb 基础DO类，id都是字符串型的
 * 
 * @author shentao
 *
 */
public class IdEntity implements Serializable {

	private static final long serialVersionUID = -3985193436830982141L;

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
