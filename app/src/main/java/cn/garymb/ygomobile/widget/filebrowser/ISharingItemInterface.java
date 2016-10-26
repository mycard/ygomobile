/*
 * ISharingItemInterface.java
 *
 *  Created on: 2014年3月15日
 *      Author: mabin
 */
package cn.garymb.ygomobile.widget.filebrowser;

import cn.garymb.ygomobile.widget.filebrowser.SharingItemBase.SharingItemSelectListener;


/**
 * @author mabin
 *
 */
public interface ISharingItemInterface {
	void setListener(SharingItemSelectListener listener);
	void setUrl(String url);
	void toggoleBackground(boolean isPressed);
}
