package cn.garymb.ygomobile.data.wrapper;

import cn.garymb.ygomobile.model.data.ResourcesConstants;


/**
 * @author mabin
 * 
 */
public interface IBaseJob extends ResourcesConstants {
	
	interface JobStatusCallback {
		void onJobFinish(BaseRequestJob wrapper);
		void onJobContinue(BaseRequestJob wrapper);
	}

	int STATUS_SUCCESS = 0;
	int STATUS_FAILED = 1;
	int STATUS_CANCELED = 2;

	/**
	 * 
	 * @brief clear all data and force GC. Never use this object after recycle()
	 * @author: mabin
	 * 
	 */
	void recyle();

	/**
	 * 
	 * @brief get certain url to connect
	 * @author: mabin
	 * 
	 */
	String getUrl(int index);
}
