package cn.zzs.springdata.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.zzs.springdata.pojo.User;

/**
 * @ClassName: UserDaoTest
 * @Description: 测试UserDao
 * @author: zzs
 * @date: 2019年9月2日 上午11:59:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

	@Autowired
	private UserDao userDao;
	@Autowired
	private RoleDao roleDao;

	/**
	 * 测试添加或更新用户
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testSaveOrUpdate() {
		User user = new User("zzs005", 18, new Date(), new Date());
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

	/**
	 * 测试根据name模糊查询用户（按方法命名规则定义方法）
	 * @throws Exception 
	 */
	@Test
	@Transactional(readOnly = true)
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFindByNameLike() throws Exception {
		//查询用户
		List<User> list = userDao.findByNameLike("zzs%");
		if (list != null && list.size() != 0) {
			for (User user : list) {
				System.err.println(user);
				System.err.println(user.getRole());
			}
		}
	}

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
	@Transactional(readOnly = true)
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFindByAgeLessThanUseJPQL() {
		//查询用户
		List<User> list = userDao.findByAgeLessThanUseJPQL(22);
		//遍历查询结果
		if (list != null && list.size() != 0) {
			for (User user : list) {
				System.err.println(user);
				System.err.println(user.getRole());
			}
		}
	}

	/**
	 * 测试使用@Query+SQL分页查询用户
	 */
	@Test
	@Transactional(readOnly = true)
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFindByAgeLessThanUseSQL() {
		//查询用户
		List<User> list = userDao.findByAgeLessThanWithPageUseSQL(22, 0, 3);
		//遍历查询结果
		if (list != null && list.size() != 0) {
			for (User user : list) {
				System.err.println(user);
				System.err.println(user.getRole());
			}
		}
	}

	/**
	 * 
	 * @Title: testMyRespository
	 * @Description: 测试自定义Respository的方法
	 * @author: zzs
	 * @date: 2019年9月3日 上午1:47:37
	 * @return: void
	 */
	@Test
	@Transactional(readOnly = true)
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testMyRespository() {
		//根据对象名分页查询对象
		List<User> list = userDao.findByNameLikeWithPage("zzs%", 0, 3);
		if (list != null && list.size() != 0) {
			for (User user : list) {
				System.err.println(user);
			}
		}
		System.err.println("-------------------------");
		//根据对象名分页查询对象并按指定属性升序排序
		List<User> list2 = userDao.findByNameLikeWithPageAsc("zzs%", 0, 3, "age", "name");
		if (list2 != null && list2.size() != 0) {
			for (User user : list2) {
				System.err.println(user);
			}
		}

		System.err.println("-------------------------");
		//根据对象名分页查询对象并按指定属性倒序排序
		List<User> list3 = userDao.findByNameLikeWithPageDesc("zzs%", 0, 3, "age", "name");
		if (list3 != null && list3.size() != 0) {
			for (User user : list3) {
				System.err.println(user);
			}
		}
	}

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
		System.err.println("总记录数：" + page.getTotalElements());
		System.err.println("总页数：" + page.getTotalPages());
		List<User> list = page.getContent();
		if (list != null) {
			for (User user : list) {
				System.err.println(user);
			}
		}
	}

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
		System.err.println("总记录数：" + page.getTotalElements());
		System.err.println("总页数：" + page.getTotalPages());
		List<User> list = page.getContent();
		if (list != null) {
			for (User user : list) {
				System.err.println(user);
			}
		}

	}

}
