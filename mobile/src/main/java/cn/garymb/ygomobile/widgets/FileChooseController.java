package cn.garymb.ygomobile.widgets;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

public class FileChooseController extends BaseDialogConfigController{
	private String mUrl;

	public FileChooseController(DialogConfigUIBase configUI, View view, Bundle param) {
		super(configUI, view);

		configUI.setPositiveButton(configUI.getContext().getString(android.R.string.ok));
		configUI.setCancelButton(configUI.getContext().getString(android.R.string.cancel));
	}

	@Override
	public int enableSubmitIfAppropriate() {
		Button positive = mConfigUI.getPositiveButton();
		if (positive == null)
			return 0;
		if (TextUtils.isEmpty(mUrl)) {
			positive.setEnabled(false);
		} else {
			positive.setEnabled(true);
		}
		return 0;
	}

	public String getUrl() {
		return mUrl;
	}

}
