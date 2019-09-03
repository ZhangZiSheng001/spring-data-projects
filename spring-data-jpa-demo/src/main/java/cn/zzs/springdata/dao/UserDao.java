package cn.zzs.springdata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import cn.zzs.springdata.pojo.User;
import cn.zzs.springdata.respository.MyRespository;

/**
 * @ClassName: UserDao
 * @Description: 用户操作的接口
 * @author: zzs
 * @date: 2019年9月2日 上午11:30:11
 */
public interface UserDao extends JpaRepositoryImplementation<User, Long>, MyRespository<User, Integer> {
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

	/**
	 * 
	 * @Title: findByNameIs
	 * @Description: 根据指定用户名查询用户
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:31:16
	 * @param name
	 * @return: User
	 */
	User findByNameIs(String name);

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

}
