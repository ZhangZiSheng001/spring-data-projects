
# Spring Data

## 简介
`SpringData`提供了针对数据库（包括`SQL`和`NOSQL`）的整合方案，对`Hibernate` `JPA`、`Jedis`等工具的api进行高级的封装，为我们提供简单方便地操作接口。    

### Spring Data JPA
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

### Spring Data Redis
`Spring Data Redis` 是`Spring Data`的项目下的一个模块。整合了`jedis`，并对API进行了封装，让我们可以更方便地操作`redis`。  

学习`Spring Data Redis`主要包括以下内容：  
1. 单机和集群的配置  

2. 使用`RedisTemplate`操作分别操作不同的类型数据：`string`、`list`、`set`、`sorted set`、`hash`.  

3. 使用`RedisTemplate`操作事务。


## 项目实现的需求
###  Spring Data JPA
采用`Spring Data JPA`针对以下三个实体进行增删改查操作：  

1. 用户：  

2. 角色：和用户是一对多关系  

3. 菜单：和角色是多对多关系，本身**自关联**  

项目路径：  
[spring-data-jpa-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-jpa-demo)  

[spring-data-jpa-springboot-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-jpa-springboot-demo)  

###  Spring Data Redis
1. 使用`Spring Data Redis`，以`json`格式存取不同类型数据：`string`、`list`、`set`、`sorted set`、`hash`  

2. 测试`Spring Data Redis`的事务管理。  

项目路径：   
[spring-data-redis-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-redis-demo)   

[spring-data-redis-springboot-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-redis-springboot-demo)   

## 项目工程环境

JDK：1.8.0_201  

maven：3.6.1  

IDE：Spring Tool Suites4 for Eclipse：4.12   

Redis：3.2.100（windows版）  

Spring Data Redis：2.1.10.RELEASE  

Spring Boot：2.1.7.RELEASE  


> 学习使我快乐！！
