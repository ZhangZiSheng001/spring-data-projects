package cn.zzs.springdata.respository;

import java.util.List;

/**
 * @ClassName: MyRespository
 * @Description: 对象的自定义Respository
 * @author: zzs
 * @date: 2019年9月3日 上午12:54:22
 */
public interface MyRespository<T, ID> {
	/**
	 * 
	 * @Title: findByNameLikeWithPage
	 * @Description: 根据对象名分页查询对象
	 * @author: zzs
	 * @date: 2019年9月3日 上午1:04:49
	 * @param name
	 * @param firstResult
	 * @param maxResults
	 * @return: List<T>
	 */
	List<T> findByNameLikeWithPage(String name, Integer firstResult, Integer maxResults);
	
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
	
	/**
	 * 
	 * @Title: findByNameLikeWithPageDesc
	 * @Description: 根据对象名分页查询对象并按指定属性倒序排序
	 * @author: zzs
	 * @date: 2019年9月3日 上午1:04:49
	 * @param name
	 * @param firstResult
	 * @param maxResults
	 * @param fields
	 * @return: List<T>
	 */
	List<T> findByNameLikeWithPageDesc(String name, Integer firstResult, Integer maxResults, String... fields);
}
