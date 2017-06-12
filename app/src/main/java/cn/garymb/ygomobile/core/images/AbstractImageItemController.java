package cn.garymb.ygomobile.loader;

import cn.garymb.ygomobile.model.data.ImageItem;

import android.graphics.Bitmap;

public abstract class AbstractImageItemController {
	protected boolean mIsLoaded = false;
	protected ImageItem mImageItem = null;
	
	public boolean isLoaded(ImageItem item) {
		return mIsLoaded && mImageItem != null && mImageItem.equals(item);
	}
	
	public void setImageItem(ImageItem item) {
		mImageItem = item;
		mIsLoaded = false;
		onImageItemChanged(item.width, item.height);
	}
	
	public void setImageItemWithoutDealChange(ImageItem item) {
		mImageItem = item;
		mIsLoaded = false;
	}
	
	protected abstract void onImageItemChanged(int width, int height);
	
	public abstract void setBitmap(Bitmap bmp, boolean isAnimationNeeded);
	
	public ImageItem getImageItem() {
		return mImageItem;
	}
}
