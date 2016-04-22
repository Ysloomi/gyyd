package com.beessoft.dyyd.db;

import java.util.ArrayList;
import java.util.List;


/**
* @Title YZXObservable 
* @Description 数据库改变观察者
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 下午12:04:59 
*/
public abstract class YZXObservable<T> {

	protected final List<T> observers = new ArrayList<T>();
	
	/**
	* @Description 添加观察者
	* @param observer	要添加的观察者
	* @date 2015-12-2 下午12:15:14 
	* @author zhuqian  
	* @return void
	 */
	public void addObserver(T observer){
		if(observer == null){
			throw new IllegalArgumentException("The addObserver is null");
		}
		synchronized (observers) {
			if(observers.contains(observer)){
				throw new IllegalStateException("The observer : "+observer+" is already added");
			}
			observers.add(observer);
		}
	}
	
	/**
	* @Description 移出观察者
	* @param observer	要移出的观察者
	* @date 2015-12-2 下午12:16:23 
	* @author zhuqian  
	* @return void
	 */
	public void removeObserver(T observer){
		if(observer == null){
			throw new IllegalArgumentException("The addObserver is null");
		}
		synchronized (observers) {
			int i = observers.indexOf(observer);
			if(i == -1){
//				CustomLog.d("YZXObservable removeObserver observer "+observer+" is not existed");
				return;
			}
			observers.remove(i);
		}
	}
	/**
	* @Description 清空观察者
	* @date 2015-12-2 下午12:16:41 
	* @author zhuqian  
	* @return void
	 */
	public void clear(){
		synchronized (observers) {
			observers.clear();
		}
	}
}
