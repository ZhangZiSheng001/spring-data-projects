package cn.zzs.springdata.pojo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @ClassName: Role
 * @Description: 角色实体类
 * @author: zzs
 * @date: 2019年9月2日 下午2:58:04
 */
@Entity
@Table(name = "jpa_role")
public class Role {
	/**
	 * 角色id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long id;

	/**
	 * 角色名
	 */
	@Column(name = "role_name", unique = true)
	private String name;

	/**
	 * 角色关联的用户
	 */
	@SuppressWarnings("deprecation")
	@OneToMany(mappedBy = "role")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private Set<User> users = new HashSet<User>();

	/**
	 * 角色包含的权限菜单
	 */
	//@JoinTable:配置中间表信息
	//@joinColumns:建立当前表在中间表中的外键字段
	@ManyToMany
	@JoinTable(name = "jpa_role_menu", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"), foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Set<Menu> menus = new HashSet<Menu>();

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

	public Role(String name, Date create, Date modified) {
		super();
		this.name = name;
		this.create = create;
		this.modified = modified;
	}

	public Role() {
		super();
		// TODO Auto-generated constructor stub
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

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
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

	public Set<Menu> getMenus() {
		return menus;
	}

	public void setMenus(Set<Menu> menus) {
		this.menus = menus;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", create=" + create + ", modified=" + modified + "]";
	}

}
