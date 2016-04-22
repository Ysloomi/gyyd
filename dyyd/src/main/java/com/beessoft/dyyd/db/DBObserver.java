package com.beessoft.dyyd.db;


/**
* @Title DBObserver 
* @Description 数据库改变观察者
* @Company yunzhixun
* @author zhuqian
* @date 2015-12-2 下午12:25:01 
*/
public class DBObserver extends YZXObservable<OnDbChangeListener> {

	/**
	* @Description 通知观察者
	* @param notifyId	通知id
	* @date 2015-12-2 下午12:26:51 
	* @author zhuqian  
	* @return void
	 */
	public void notify(String notifyId){
		synchronized (observers) {
			for(int i = observers.size() - 1;i>=0;i--){
				if(observers.get(i) != null){
					observers.get(i).onChange(notifyId);
				}
			}
		}
	}
}
