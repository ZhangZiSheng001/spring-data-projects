package cn.zzs.springdata.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import cn.zzs.springdata.pojo.User;
import cn.zzs.springdata.respository.MyRespository;

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
	public List<User> findByNameLikeWithPage(String name, Integer firstResult, Integer maxResults) {
		//定义sql语句
		String sql = "from User where name like ?1 ";
		//获得Query对象
		Query query = entityManager.createQuery(sql);
		//设置参数
		query.setParameter(1, name);
		//设置分页参数
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);
		//执行查询,并返回结果
		return query.getResultList();
	}

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

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findByNameLikeWithPageDesc(String name, Integer firstResult, Integer maxResults,
			String... fields) {
		//定义sql语句
		StringBuilder sqlBuilder = new StringBuilder("from User where name like ?1 ");
		//拼接sql语句
		int length = 0;
		if (fields != null && (length = fields.length) != 0) {
			sqlBuilder.append("order by");
			for (int i = 0; i < fields.length; i++) {
				if(i == length - 1) {
					sqlBuilder.append(" ?" + (i + 2) + " DESC ");
					continue;
				}
				sqlBuilder.append(" ?" + (i + 2) + " DESC " + " ,");
			}
		}
		System.err.println(sqlBuilder.toString());
		//获得Query对象
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
