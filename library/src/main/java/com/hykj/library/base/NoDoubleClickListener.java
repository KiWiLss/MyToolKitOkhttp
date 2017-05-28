package com.hykj.library.base;

import android.view.View;

/**
 * Author: KiWi刘少帅 on 2017/5/21   16:59
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public   abstract class NoDoubleClickListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public abstract void onNoDoubleClick(View v);

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }
}
