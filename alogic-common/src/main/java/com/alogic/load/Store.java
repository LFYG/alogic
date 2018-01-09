package com.alogic.load;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.anysoft.util.Pager;

/**
 * Store
 * 
 * <p>
 * Store是在Loader的基础上增加存储接口
 * 
 * @author yyduan
 *
 * @param <O> 
 * 
 * @version 1.6.11.1 [20171215 duanyy] <br>
 * - 增加有效期的判定 <br>
 */
public interface Store<O extends Loadable> extends Loader<O> {
	
	/**
	 * 存储对象
	 * @param id 对象id
	 * @param o 对象实例
	 * @param overwrite 是否覆盖
	 * 
	 */
	public void save(String id,O o,boolean overwrite);
	
	/**
	 * 删除指定的对象
	 * @param id 对象id
	 */
	public void del(String id);
	
	/**
	 * 遍历Store的内容
	 * @param result 查询结果 
	 * @param pager Pager
	 */
	public void scan(List<O> result,Pager pager);
	
	/**
	 * 基于本地内存ConcurrentHashMap的Store
	 * 
	 * @author yyduan
	 *
	 */
	public static class HashStore<O extends Loadable> extends Loader.Sinkable<O> implements Store<O>{
		protected Map<String,O> data = new ConcurrentHashMap<String,O>();
		
		@Override
		public void save(String id, O o, boolean overwrite) {
			O found = data.get(id);
			if (found == null){
				data.put(id, o);
			}else{
				if (overwrite){
					data.put(id, o);
				}
			}
		}

		@Override
		protected O loadFromSelf(String id, boolean cacheAllowed) {
			O found = null;
			if (cacheAllowed){
				found = data.get(id);
				if (isExpired(found)){
					data.remove(id);
					found = null;
				}
			}
			return found;
		}
		
		@Override
		public void del(String id){
			data.remove(id);
		}

		@Override
		public void scan(List<O> result,Pager pager) {
			Collection<O> list = data.values();
			
			String keyword = pager.getKeyword();
			int offset = pager.getOffset();
			int limit = pager.getLimit();
			
			int current = 0;
			for (O o:list){
				String id = o.getId();
				boolean match = StringUtils.isEmpty(pager.getKeyword()) || id.contains(keyword);
				if (match){
					if (current >= offset && current < offset + limit){
						result.add(o);
					}
					current ++;
				}
			}
			
			pager.setAll(data.size()).setTotal(current);
		}
	}
}