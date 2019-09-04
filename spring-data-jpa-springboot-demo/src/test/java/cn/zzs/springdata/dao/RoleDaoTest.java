package cn.zzs.springdata.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.zzs.springdata.pojo.Menu;
import cn.zzs.springdata.pojo.Role;
import cn.zzs.springdata.pojo.User;

/**
 * @ClassName: RoleDaoTest
 * @Description: 测试RoleDao
 * @author: zzs
 * @date: 2019年9月2日 上午11:59:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleDaoTest {

	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;

	/**
	 * 测试添加角色
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testSave() {
		//创建角色
		Role role = new Role("仓库主管", new Date(), new Date());
		//设置角色权限菜单
		Set<Menu> menus = new HashSet<>(4);
		menus.add(menuDao.findByName("销售订单出库"));
		menus.add(menuDao.findByName("采购订单入库"));
		role.setMenus(menus);
		//保存角色
		roleDao.save(role);
	}

	/**
	 * 测试根据name模糊查询角色
	 * @throws Exception 
	 */
	@Test
	@Transactional(readOnly = true)
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFind() throws Exception {
		//查询角色
		List<Role> list = roleDao.findByNameLike("销售%");
		if (list != null && list.size() != 0) {
			for (Role role : list) {
				System.err.println(role);
				System.err.println(role.getMenus());
			}
		}
	}

	/**
	 * 测试更新角色
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testUpdate() {
		//获取角色对象
		Role role = roleDao.findByName("销售经理");
		//修改角色
		role.setModified(new Date());
	}

	/**
	 * 测试删除角色
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testDelete() {
		//获取角色对象
		Role role = roleDao.findByName("销售经理");
		//遍历该角色的用户，取消关联
		Set<User> users = role.getUsers();
		if (users != null && users.size() != 0) {
			for (User user : users) {
				user.setRole(null);
			}
		}
		//删除角色
		roleDao.delete(role);
	}

}
