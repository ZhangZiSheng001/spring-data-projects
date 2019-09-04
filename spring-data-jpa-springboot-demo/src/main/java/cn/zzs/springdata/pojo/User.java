package cn.zzs.springdata.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @ClassName: User
 * @Description: 用户实体类
 * @author: zzs
 * @date: 2019年9月2日 上午11:14:53
 */
@Entity
@Table(name = "jpa_user")
public class User {
	/**
	 * 用户id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //使用主键自动增长
	@Column(name = "user_id")
	private Long id;

	/**
	 * 用户名
	 */
	@Column(name = "user_name", unique = true)
	private String name;

	/**
	 * 用户年龄
	 */
	@Column(name = "user_age")
	private Integer age;

	/**
	 * 用户角色
	 */
	@ManyToOne
	@JoinColumn(name = "user_role_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Role role;

	/**
	 * 记录创建时间
	 */
	@Column(name = "gmt_create")
	private Date create;

	/**
	 * 记录最后一次修改时间
	 */
	@Column(name = "gmt_modified")
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + ", create=" + create + ", modified=" + modified
				+ "]";
	}

}
