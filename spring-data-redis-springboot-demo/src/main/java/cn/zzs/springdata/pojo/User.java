package cn.zzs.springdata.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户的实体类
 * @author zzs
 *
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	private Long id;

	/**
	 * 用户名
	 */
	private String name;

	/**
	 * 用户年龄
	 */
	private Integer age;

	/**
	 * 记录创建时间
	 */
	private Date create;

	/**
	 * 记录最后一次修改时间
	 */
	private Date modified;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String name, Integer age, Date create, Date modified) {
		super();
		this.name = name;
		this.age = age;
		this.create = create;
		this.modified = modified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getCreate() {
		return create;
	}

	public void setCreate(Date create) {
		this.create = create;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + ", create=" + create + ", modified=" + modified
				+ "]";
	}
}
