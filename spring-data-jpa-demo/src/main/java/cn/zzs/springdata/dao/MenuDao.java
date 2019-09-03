package cn.zzs.springdata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import cn.zzs.springdata.pojo.Menu;

/**
 * @ClassName: MenuDao
 * @Description: 菜单操作的接口
 * @author: zzs
 * @date: 2019年9月2日 上午11:30:11
 */
public interface MenuDao  extends JpaRepositoryImplementation<Menu, String>{
	/**
	 * 
	 * @Title: findByName
	 * @Description: 根据名称查询菜单对象
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:41:06
	 * @param name
	 * @return: Menu
	 */
	Menu findByName(String name);
	/**
	 * 
	 * @Title: findByNameLike
	 * @Description: 根据名称查询菜单对象
	 * @author: zzs
	 * @date: 2019年9月2日 下午10:41:06
	 * @param name
	 * @return: List<Menu>
	 */
	List<Menu> findByNameLike(String name);
}
