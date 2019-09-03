package cn.zzs.springdata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import cn.zzs.springdata.pojo.Role;

/**
 * @ClassName: RoleDao
 * @Description: 角色操作的接口
 * @author: zzs
 * @date: 2019年9月2日 上午11:30:11
 */
public interface RoleDao  extends JpaRepositoryImplementation<Role, Long>{
	/**
	 * 
	 * @Title: findByName
	 * @Description: 根据名称查询角色对象
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:39:03
	 * @param name
	 * @return: Role
	 */
	Role findByName(String name);
	
	/**
	 * 
	 * @Title: findByNameLike
	 * @Description: 根据名称模糊查询角色对象
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:39:03
	 * @param name
	 * @return: List<Role>
	 */
	List<Role> findByNameLike(String name);
}
