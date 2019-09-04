package cn.zzs.springdata.pojo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

/** 
 * @ClassName: Menu 
 * @Description: 菜单的实体类
 * @author: zzs
 * @date: 2019年8月23日 下午10:09:54  
 */
@Entity
@Table(name = "jpa_menu")
public class Menu {
	/**
	 * id
	 */
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@GeneratedValue(generator = "uuid")
	@Column(name = "menu_id")
	private String id;

	/**
	 * 名称
	 */
	@Column(name = "menu_name",unique = true)
	private String name;

	/**
	 * url
	 */
	@Column(name = "menu_url")
	private String url;

	/**
	 * 同一级菜单中的顺序
	 */
	@Column(name = "menu_order")
	private Integer order;

	/**
	 * 父菜单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_parent_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Menu parent;

	/**
	 * 子菜单
	 */
	@SuppressWarnings("deprecation")
	@OneToMany(targetEntity = Menu.class, cascade = { CascadeType.ALL }, mappedBy = "parent" )
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("order")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private Set<Menu> children = new HashSet<Menu>();

	/**
	 * 包含的角色
	 */
	@SuppressWarnings("deprecation")
	@ManyToMany(mappedBy = "menus")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private Set<Role> roles = new HashSet<Role>();

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

	public Menu() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Menu(String name, String url, Integer order, Date create, Date modified) {
		super();
		this.name = name;
		this.url = url;
		this.order = order;
		this.create = create;
		this.modified = modified;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public Set<Menu> getChildren() {
		return children;
	}

	public void setChildren(Set<Menu> children) {
		this.children = children;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
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
		return "Menu [id=" + id + ", name=" + name + ", url=" + url + ", order=" + order + ", create=" + create
				+ ", modified=" + modified + "]";
	}

}
