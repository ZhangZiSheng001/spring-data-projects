
# SpringData

## 简介
SpringData提供了针对数据库（包括SQL和NOSQL）的整合方案，对Hibernate JPA、Jedis等工具的api进行高级的封装，为我们提供简单方便地操作接口。    

### Spring Data JPA
Spring Data JPA 是spring data项目下的一个模块。整合了基于JPA的持久层框架（默认Hibernate JPA），并对API进行了封装，让我们可以更方便地操作数据库。 
 
学习Spring Data JPA主要包括以下内容：  
1. entityManagerFactory、transactionManager的配置  
2. DAO接口的定义：实现Repository子接口  
3. 基于方法命名规则的查询  
4. 基于@Query注解的查询或更新：JPQL和SQL（对应Hibernate的HQL和SQL）  
5. Respository接口的继承体系  
5. CrudRepository的api使用  
6. PagingAndSortingRepository的api使用  
7. JpaSpecificationExecutor的api使用（对应Hibernate的QBC）  
8. 一对一、一对多、多对多的配置和操作  

### Spring Data Redis
Spring Data Redis 是Spring Data的项目下的一个模块。整合了jedis，并对API进行了封装，让我们可以更方便地操作redis。  
学习Spring Data Redis主要包括以下内容：  
1. 单机和集群的配置  
2. redisTemplate API的操作，重点使用序列化器操作对象  

## 项目实现的需求
###  Spring Data JPA
使用Spring Data JPA，针对三个实体进行增删改查操作：  
1. 用户：  
2. 角色：和用户是一对多关系  
3. 菜单：和角色是多对多关系，本身自关联  

项目路径：  
[spring-data-jpa-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-jpa-demo)  

###  Spring Data Redis
使用Spring Data Redis，以json格式存取数据  

项目路径：   
[spring-data-redis-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-redis-demo)   

## 项目工程环境
JDK：1.8.0_201  
maven：3.6.1  
IDE：Spring Tool Suites4 for Eclipse：4.12   
mysql：5.7  
Hibernate：5.4.4.Final  
Redis：3.2.100（windows版）  


> 学习使我快乐！！
