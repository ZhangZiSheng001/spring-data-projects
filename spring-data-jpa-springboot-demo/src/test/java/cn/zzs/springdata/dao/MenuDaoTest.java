package cn.zzs.springdata.dao;

import java.util.Date;
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

/**
 * @ClassName: MenuDaoTest
 * @Description: 测试MenuDao
 * @author: zzs
 * @date: 2019年9月2日 上午11:59:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MenuDaoTest {

	@Autowired
	private MenuDao menuDao;

	/**
	 * 测试添加菜单
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testSave() {
		//创建菜单
		Menu menu = new Menu("基本管理", "http://", 0, new Date(), new Date());
		//设置父菜单
		menu.setParent(menuDao.findByName("系统菜单"));
		//保存菜单
		menuDao.save(menu);
	}

	/**
	 * 测试根据name模糊查询菜单
	 * @throws Exception 
	 */
	@Test
	@Transactional(readOnly = true)
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testFind() throws Exception {
		//查询菜单
		List<Menu> list = menuDao.findByNameLike("销售%");
		if (list != null && list.size() != 0) {
			for (Menu menu : list) {
				System.err.println(menu);
				System.err.println(menu.getRoles());
			}
		}
	}

	/**
	 * 测试更新菜单
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testUpdate() {
		//获取菜单对象
		Menu menu = menuDao.findByName("销售管理");
		//修改菜单
		menu.setUrl("http://localhost:8080/");
		menu.setModified(new Date());
	}

	/**
	 * 测试删除菜单
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下
	public void testDelete() {
		//获取菜单对象
		Menu menu = menuDao.findByName("销售管理");
		//删除菜单
		menuDao.delete(menu);
	}

	/**
	 * 测试遍历菜单树
	 */
	@Test
	@Transactional
	@Rollback(value = false) //spring的测试默认会回滚事务，这里关闭下	
	public void testShowTree() {
		//获取根菜单
		Menu menu = menuDao.findRoot();
		//递归遍历子菜单
		showTree(menu);
	}

	/**
	 * 递归遍历菜单
	 */
	private void showTree(Menu menu) {
		System.err.println(menu);
		Set<Menu> children = menu.getChildren();
		if (children != null && children.size() != 0) {
			for (Menu menu2 : children) {
				showTree(menu2);
			}
		}
	}
}
