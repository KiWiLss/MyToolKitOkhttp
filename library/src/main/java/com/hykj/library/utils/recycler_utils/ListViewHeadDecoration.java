package com.hykj.library.utils.recycler_utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hykj.library.MyApp;


/**
 * Created by kiwi on 2017/5/24.
 * Email:2763015920@qq.com
 */

public class ListViewHeadDecoration extends RecyclerView.ItemDecoration {//从头部开始绘制

    private Drawable mDrawable;

    public ListViewHeadDecoration(int drawableId) {
        mDrawable= ContextCompat.getDrawable(MyApp.getContext(),drawableId);
        //mDrawable = ResCompat.getDrawable(MyApp.getInstance(), drawableId);

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            // 以下计算主要用来确定绘制的位置
            final int top = child.getTop() -mDrawable.getIntrinsicHeight()+
                    Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, mDrawable.getIntrinsicHeight(),0,0);
    }
}
