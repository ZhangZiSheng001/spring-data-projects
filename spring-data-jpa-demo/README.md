# SpringData

## 简介
`Spring Data`提供了针对数据库（包括`SQL`和`NOSQL`）的整合方案，对`Hibernate JPA`、`Jedis`等工具的api进行高级的封装，为我们提供简单方便地操作接口。    

`Spring Data JPA` 是`Spring Data`项目下的一个模块。整合了基于`JPA`的持久层框架（默认`Hibernate JPA`），并对API进行了封装，让我们可以更方便地操作数据库。使用过程中基本不需要编写DAO实现类，只要定义好接口就行了。  

学习`Spring Data JPA`主要包括以下内容：  
1. **entityManagerFactory、transactionManager的配置**  
2. **DAO接口的定义：实现Repository子接口**  
3. **基于方法命名规则的查询**  
4. **基于@Query注解的查询或更新：JPQL和SQL（对应Hibernate的HQL和SQL）**  
5. **Respository接口的继承体系**  
5. **CrudRepository的api使用**  
6. **PagingAndSortingRepository的api使用**  
7. **JpaSpecificationExecutor的api使用（对应Hibernate的QBC）**  
8. **一对一、一对多、多对多、自关联的配置和操作**  
9. **自定义Repository的使用**  

## 项目工程环境
JDK：1.8.0_201  
maven：3.6.1  
IDE：Spring Tool Suites4 for Eclipse：4.12   
mysql：5.7  
Hibernate：5.4.4.Final  
c3p0：0.9.5.4  

## 项目实现的需求
采用`Spring Data JPA`的API(均采用注解方式配置对象映射)，针对三个实体进行增删改查操作：  

1. 用户：  

2. 角色：和用户是一对多关系  

3. 菜单：和角色是多对多关系，本身**自关联**  

## applicationContext.xml文件的配置  
这个文件配置需要注意以下几点：  

1. 数据库的方言配置：一般使用`MySQL5Dialect`、`MySQL55Dialect`、`MySQL57Dialect`，分别对应不同版本的数据库；  

2. 数据库引擎：`MyISAM`和`InnoDB`，默认是`MyISAM`，一般都需要修改。  

3. 自动建表：`update/create-drop/create/none`。一般使用`update`或者`none`。  

4. 由于采用的连接池是`c3p0`，Spring在创建`ComboPooledDataSource`实例时会默认读取`resources`下的`c3p0-config.xml`，我们可以在该文件集中管理连接池的配置信息，向DBCP就不能这样操作。这就是使用`c3p0`的好处。  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx"
    xmlns:jpa="http://www.springframework.org/schema/data/jpa"
    xsi:schemaLocation="
	    http://www.springframework.org/schema/beans 
	    http://www.springframework.org/schema/beans/spring-beans.xsd
	    http://www.springframework.org/schema/context
	    http://www.springframework.org/schema/context/spring-context.xsd
	    http://www.springframework.org/schema/aop
	    http://www.springframework.org/schema/aop/spring-aop.xsd
	    http://www.springframework.org/schema/tx 
	    http://www.springframework.org/schema/tx/spring-tx.xsd
	    http://www.springframework.org/schema/data/jpa
	    http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
    ">
    
    <!-- 配置数据源 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!-- 这里会自动读取resources下的c3p0-config.xml -->
    </bean>
    
    <!-- Spring 整合JPA 配置EntityManagerFactory -->
    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="dataSource" ref="dataSource" />
        <!-- 指定Jpa持久化实现厂商类,这里以Hibernate为例 -->
        <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"></bean>
        </property>
        <!-- 指定JPA属性；如Hibernate中指定是否显示SQL的是否显示、方言等 -->
        <property name="jpaProperties">
	       <props>
		      <prop key="hibernate.dialect">org.hibernate.dialect.MySQL55Dialect</prop>
		      <prop key="hibernate.dialect.storage_engine">innodb</prop>
		      <prop key="hibernate.show_sql">true</prop>
		      <!-- <prop key="hibernate.format_sql">true</prop> -->
		      <prop key="hibernate.hbm2ddl.auto">update</prop>
	       </props>
        </property>
            
        <!-- 扫描实体的包 -->
        <property name="packagesToScan">
            <list>
                <value>cn.zzs.springdata.pojo</value>
            </list>
        </property>
    </bean>
    
    <!-- Spring Data JPA 的配置-->
    <!-- base-package：扫描dao 接口所在的包-->
    <jpa:repositories base-package="cn.zzs.springdata.dao"/>
    
    <!-- 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>
    
    <!-- 配置开启注解事务处理 -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
    <!-- 配置springIOC的注解扫描 -->
    <context:component-scan base-package="cn.zzs.springdata"/>
</beans>
```

## 对象映射的配置-注解
### 常规配置
这里需要注意主键的配置，支持`IDENTITY`、`SEQUENCE`、`TABLE`和`AUTO`。  

```java
/**
 * @ClassName: User
 * @Description: 用户实体类
 * @author: zzs
 * @date: 2019年9月2日 上午11:14:53
 */
@Entity
@Table(name = "native_user")
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
	 * 记录创建时间
	 */
	@Column(name = "gmt_create")
	private Date create;

	/**
	 * 记录最后一次修改时间
	 */
	@Column(name = "gmt_modified")
	private Date modified;

    //以下方法省略
}
```

如果是`uuid`的主键，配置方式如下：  

```java
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@GeneratedValue(generator = "uuid")
	@Column(name = "menu_id")
	private String id;
```

### 一对多配置
用户只有一个角色，一个角色下有多个用户。  

#### 一方
```java
	/**
	 * 角色关联的用户
	 */
	@OneToMany(mappedBy = "role")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private Set<User> users = new HashSet<User>();
```

#### 多方
```java
	/**
	 * 用户角色
	 */
	@ManyToOne
	@JoinColumn(name = "user_role_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Role role;
```

### 多对多配置
一个角色可以有多个权限菜单，一个菜单可以分配多个角色。  

#### 多方1
```java
	/**
	 * 角色包含的权限菜单
	 */
	//@JoinTable:配置中间表信息
	//@joinColumns:建立当前表在中间表中的外键字段
	@ManyToMany
	@JoinTable(name = "native_role_menu", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"), foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Set<Menu> menus = new HashSet<Menu>();
```

#### 多方2
```java
	/**
	 * 包含的角色
	 */
	@ManyToMany(mappedBy = "menus")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private Set<Role> roles = new HashSet<Role>();
```

### 自关联配置
一个权限菜单有多个子菜单，且指向父菜单。  

```java
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
	@OneToMany(targetEntity = Menu.class, cascade = { CascadeType.ALL }, mappedBy = "parent")
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("order")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private Set<Menu> children = new HashSet<Menu>();
```

### 关于取消外键自动生成
实际项目中，我们都不会在数据库中建立外键，因为外键会降低数据库操作的效率。一般都是通过代码逻辑来控制。但是`Hibernate`会自动地更新外键，这里说说解决办法。  

第一种比较简单，即配置`hibernate.hbm2ddl.auto`为`none`，这样`Hibernate`就不会自动维护外键，当然，我们要提前建好表。这种方式其实对性能也是有好处的，推荐使用。  

第二种就是本项目采取的方式。其实，主要是不想手动建表。这种方式需要在一方和多方都配置，缺点就是`@org.hibernate.annotations.ForeignKey`注解已经过时，但目前我还没找到替代方案。  
```java
	/**
	 * 多方的@JoinColumn中增加以下foreignKey属性
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_parent_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
	private Menu parent;

	/**
	 * 一方增加@org.hibernate.annotations.ForeignKey属性
	 */
	@SuppressWarnings("deprecation")
	@OneToMany(targetEntity = Menu.class, cascade = { CascadeType.ALL }, mappedBy = "parent")
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("order")
	@org.hibernate.annotations.ForeignKey(name = "none")
	private Set<Menu> children = new HashSet<Menu>();
```

## DAO接口的定义
`Spring Data JPA` 会为我们生成DAO实现类，只要我们定义好接口就行了，具体就是实现`Repository`的子接口，当然，它的子接口有多个，建议实现`JpaRepositoryImplementation`，看看以下继承体系图就明白了。  

```java
/**
 * @ClassName: UserDao
 * @Description: 用户操作的接口
 * @author: zzs
 * @date: 2019年9月2日 上午11:30:11
 */
public interface UserDao extends JpaRepositoryImplementation<User, Long> {}
```

## Repository接口的继承体系
![Repository接口的继承体系图](https://github.com/ZhangZiSheng001/spring-data-projects/blob/master/spring-data-jpa-demo/img/Repository.png)  

如上图所示，`Respository`的实现类通过继承不同的接口来获取相应的功能：  

1. `CrudRepository`：基本的CRUD操作。  

2. `PagingAndSortingRepository`：分页和排序操作。继承了`CrudRepository`。  

3. `JpaSpecificationExecutor`：条件查询操作。  

4. `JpaRepository`：对父接口进行适配处理。继承了`PagingAndSortingRepository`。  

5. `JpaRepositoryImplementation`：继承了`JpaRepository`和`JpaSpecificationExecutor`。 

## CrudRepository的API使用
```java
	/**
	 * 测试添加或更新用户
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testSaveOrUpdate() {
		User user = new User("zzs002", 18, new Date(), new Date());
		user.setRole(roleDao.findByName("销售经理"));
		//如果存在且有差异就更新，不存在就插入
		userDao.save(user);
	}

	/**
	 * 测试删除用户
	 */
	@Test
	@Transactional
	@Rollback //这里我不想删除，所以开启了回滚
	public void testDelete() {
		//获取用户对象
		User user = userDao.findByNameIs("zzs001");
		//删除用户
		userDao.delete(user);
	}
```

## PagingAndSortingRepository的API使用
```java
	/**
	 * 
	 * @Title: testPagingAndSortingRepository
	 * @Description: 测试PagingAndSortingRepository接口的方法:分页+排序
	 * @author: zzs
	 * @date: 2019年9月3日 上午2:18:26
	 * @return: void
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testPagingAndSortingRepository() {
		//设置分页参数，排序参数
		Pageable pageable = PageRequest.of(0, 3, Direction.ASC, "age", "name");
		//执行查询，获得分页模型
		Page<User> page = userDao.findAll(pageable);
		//获取分页模型数据
		System.out.println("总记录数：" + page.getTotalElements());
		System.out.println("总页数：" + page.getTotalPages());
		List<User> list = page.getContent();
		if (list != null) {
			for (User user : list) {
				System.out.println(user);
			}
		}
	}
```

## JpaSpecificationExecutor的API使用
这个接口功能还是非常强大的，可以满足多条件+分页+排序的需求。  

```java
	/**
	 * 
	 * @Title: testJpaSpecificationExecutor
	 * @Description: 测试JpaSpecificationExecutor接口的方法：多条件查询+分页+排序
	 * @author: zzs
	 * @date: 2019年9月3日 上午2:22:12
	 * @return: void
	 */
	@SuppressWarnings("serial")
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testJpaSpecificationExecutor() {
		//设置查询条件
		Specification<User> spec = new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				//创建多个查询条件
				Predicate pre1 = criteriaBuilder.like(root.get("name"), "zzs%");
				Predicate pre2 = criteriaBuilder.ge(root.get("age"), 17);
				return criteriaBuilder.and(pre1,pre2);
				//return criteriaBuilder.or(pre1, pre2);
			}
		};

		//设置分页参数和排序规则，并执行查询
		Page<User> page = userDao.findAll(spec, PageRequest.of(0, 3, Direction.ASC, "age", "name"));
		//遍历结果集
		System.out.println("总记录数：" + page.getTotalElements());
		System.out.println("总页数：" + page.getTotalPages());
		List<User> list = page.getContent();
		if (list != null) {
			for (User user : list) {
				System.out.println(user);
			}
		}

	}
```

## 基于方法名称命名规则查询
`Spring Data JPA`除了以上API，还支持按方法规则自定义查询。  

### 方法定义规则
|关键字	|方法命名		|sql where语句|
|------	|-------		|------------|
|And	|findByNameAndPwd		|where name= ? and pwd =? Or	findByNameOrSex		|where name= ? or sex=?|
|Is,Equal	|findById,findByIdEqual,findByIdIs		|where id= ?|
|Between	|findByIdBetween		|where id between ? and ?|
|LessThan	|findByIdLessThan		|where id < ?|
|LessThanEqual	|findByIdLessThanEqual		|where id <= ?|
|GreaterThan	|findByIdGreaterThan		|where id > ?|
|GreaterThanEqual	|findByIdGreaterThanEqual		|where id > = ?|
|After	|findByIdAfter		|where id > ?|
|Before	|findByIdBefore		|where id < ?|
|IsNull	|findByNameIsNull		|where name is null|
|isNotNull,Not	|findByNameNotNull		|where name is not|
|Like	|findByNameLike		|where name like ?|
|NotLike	|findByNameNotLike		|where name not like ?|
|StartingWith	|findByNameStartingWith		|where name like '?%'|
|EndingWith	|findByNameEndingWith		|where name like '%?'|
|Containing	|findByNameContaining		|where name like '%?%'|
|OrderBy	|findByIdOrderByXDesc		|id=? order by x desc|
|Not	|findByNameNot		|where name <> ?|
|In	|findByIdIn(Collection<?> c)		|where id in (?)|
|NotIn	|findByIdNotIn(Collection<?> c)		|where id not in (?)|
|True	|findByAaaTrue		|where aaa = true|
|False	|findByAaaFalse		|where aaa = false|
|IgnoreCase	|findByNameIgnoreCase		|where UPPER(name)=UPPER(?)|

### 在接口中定义方法
注意：必须严格按照规则定义方法。  

```java
	/**
	 * 
	 * @Title: findByNameLike
	 * @Description: 根据指定字符模糊查询用户
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:31:16
	 * @param name
	 * @return
	 * @return: List<User>
	 */
	List<User> findByNameLike(String name);
```

### 编写测试
```java
	/**
	 * 测试根据name模糊查询用户（按方法命名规则定义方法）
	 * @throws Exception 
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFind() throws Exception {
		//查询用户
		List<User> list = userDao.findByNameLike("zzs%");
		if (list != null && list.size() != 0) {
			for (User user : list) {
				System.out.println(user);
				System.out.println(user.getRole());
			}
		}
	}
```

## 基于@Query 注解查询、更新
有两种，一种是`HQL`演变过来的`JPQL`，另一种是普通的`SQL`  

### 在接口中注解定义方法
注意：`JPQL`占位符如果使用了?，会报错：`IllegalArgumentException`，因为`JPQL`并不支持?格式的占位符。  

改用命名参数（`:abc`）或者（`?123`）两种方法，第二种占位符索引必须大于0。  

这里我想通过`JPQL`实现分页查询的，但好像不行，因为`JPQL`并不支持`limit`等非通用语法，目前暂无解决方案。  

```java
	/**
	 * 
	 * @Title: findByAgeLessThanUseJPQL
	 * @Description: 根据年龄使用JPQL查询用户
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:31:39
	 * @param age
	 * @return: List<User>
	 */
	@Query("from User where age < ?1 ")
	List<User> findByAgeLessThanUseJPQL(Integer age);

	/**
	 * 
	 * @Title: findByAgeLessThanWithPageUseSQL
	 * @Description: 根据年龄使用SQL分页查询用户并分页
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:31:39
	 * @param age
	 * @param firstResult
	 * @param maxResults
	 * @return: List<User>
	 */
	@Query(value = "select * from jpa_user where user_age < ?1 limit ?2 , ?3 ", nativeQuery = true)
	List<User> findByAgeLessThanWithPageUseSQL(Integer age, Integer firstResult, Integer maxResults);

	/**
	 * 
	 * @Title: updateAgeByName
	 * @Description: 更新指定用户的年龄
	 * @author: zzs
	 * @date: 2019年9月3日 上午12:42:11
	 * @param age
	 * @param id
	 * @return: void
	 */
	@Query("update User set age = ?1 , modified=now() where name = ?2")
	@Modifying //表示当前语句为DML语句
	void updateAgeByName(Integer age, String name);
```

### 编写测试
```java
	/**
	 * 测试@Query+JPQL更新用户
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testUpdateAgeByName() {
		//修改用户
		userDao.updateAgeByName(22, "zzs001");
	}

	/**
	 * 测试使用@Query+JPQL查询用户
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFindByAgeLessThanUseJPQL() {
		//查询用户
		List<User> list = userDao.findByAgeLessThanUseJPQL(22);
		//遍历查询结果
		if (list != null && list.size() != 0) {
			for (User user : list) {
				System.out.println(user);
				System.out.println(user.getRole());
			}
		}
	}

	/**
	 * 测试使用@Query+SQL分页查询用户
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFindByAgeLessThanUseSQL() {
		//查询用户
		List<User> list = userDao.findByAgeLessThanWithPageUseSQL(22, 0, 3);
		//遍历查询结果
		if (list != null && list.size() != 0) {
			for (User user : list) {
				System.out.println(user);
				System.out.println(user.getRole());
			}
		}
	}
```

## 用户自定义Repository接口
### 编写自定义Respository接口
```java
/**
 * @ClassName: MyRespository
 * @Description: 对象的自定义Respository
 * @author: zzs
 * @date: 2019年9月3日 上午12:54:22
 */
public interface MyRespository<T, ID> {
	/**
	 * 
	 * @Title: findByNameLikeWithPageAsc
	 * @Description: 根据对象名分页查询对象并按指定属性升序排序
	 * @author: zzs
	 * @date: 2019年9月3日 上午1:04:49
	 * @param name
	 * @param firstResult
	 * @param maxResults
	 * @param fields
	 * @return: List<T>
	 */
	List<T> findByNameLikeWithPageAsc(String name, Integer firstResult, Integer maxResults, String... fields);
}
```

### 在UserDao中实现该接口
```java
/**
 * @ClassName: UserDao
 * @Description: 用户操作的接口
 * @author: zzs
 * @date: 2019年9月2日 上午11:30:11
 */
public interface UserDao extends JpaRepositoryImplementation<User, Long>, MyRespository<User, Integer> {}
```

### 编写自定义Respository接口的实现类
注意：这个实现类的命名有严格要求：必须是`UserDao+Impl`
```java
/**
 * @ClassName: UserDaoimpl
 * @Description: 自定义Respository的实现类
 * @author: zzs
 * @date: 2019年9月3日 上午1:10:15
 */
public class UserDaoImpl implements MyRespository<User, Integer> {
	@PersistenceContext(name = "entityManagerFactory")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findByNameLikeWithPageAsc(String name, Integer firstResult, Integer maxResults,
			String... fields) {
		//定义sql语句
		StringBuilder sqlBuilder = new StringBuilder("from User where name like ?1 ");
		//拼接sql语句
		int length = 0;
		if (fields != null && (length = fields.length) != 0) {
			sqlBuilder.append("order by");
			for (int i = 0; i < fields.length; i++) {
				if(i == length - 1) {
					sqlBuilder.append(" ?" + (i + 2));
					continue;
				}
				sqlBuilder.append(" ?" + (i + 2) + " ,");
			}
		}
		//获得Query对象
		System.err.println(sqlBuilder.toString());
		Query query = entityManager.createQuery(sqlBuilder.toString());
		//设置参数
		query.setParameter(1, name);
		if (fields != null && fields.length != 0) {
			for (int i = 0; i < fields.length; i++) {
				query.setParameter(i+2, fields[i]);
			}
		}
		//设置分页参数
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		//执行查询,并返回结果
		return query.getResultList();
	}
}
```

### 编写测试
```java
	/**
	 * 
	 * @Title: testMyRespository
	 * @Description: 测试自定义Respository的方法
	 * @author: zzs
	 * @date: 2019年9月3日 上午1:47:37
	 * @return: void
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testMyRespository() {
		//根据对象名分页查询对象并按指定属性升序排序
		List<User> list2 = userDao.findByNameLikeWithPageAsc("zzs%", 0, 3, "age", "name");
		if (list2 != null && list2.size() != 0) {
			for (User user : list2) {
				System.out.println(user);
			}
		}
	}
```

## 项目路径：  
[spring-data-jpa-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-jpa-demo)  

> 学习使我快乐！！
