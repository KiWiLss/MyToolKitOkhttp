package com.hykj.library.base;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.library.R;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * Author: KiWi刘少帅 on 2017/5/21   17:02
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:适用于一般的标题,(左侧返回图标,中间标题,右侧图标或是文字)
 */

public abstract class RelativeActivity extends BaseActivity {

    private AutoRelativeLayout rlActionbar;
    private ImageView imgBack;
    private ImageView imgRight;
    private TextView tvTitle;
    private TextView tvRightTitle;

    @Override
    protected void initDataAndView() {
        initViewAndData();
        //引入需要的头布局
        inflaterRelativieTitle();
        //返回图标点击监听
        backListener();

    }
    private void backListener() {
        if (imgBack==null) {
            imgBack = (ImageView) findViewById(R.id.img_base_back);
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //设置标题内容和颜色
    public void setActionbarTitle(String msg,int color){
        if (tvTitle==null){
            tvTitle = (TextView) findViewById(R.id.tv_base_title);
        }
        if (!TextUtils.isEmpty(msg)){
            tvTitle.setText(msg);
        }
        if (color!=0){
            tvTitle.setTextColor(ContextCompat.getColor(this,color));
        }
    }
    //设置右侧图标
    public void setRightIcon(int resoutIcon, final ActionbarRightClickListener arcl){
        if (imgRight==null){
            imgRight = (ImageView) findViewById(R.id.img_base_right);
        }
        imgRight.setVisibility(View.VISIBLE);
        if (resoutIcon!=0){
            imgRight.setImageResource(resoutIcon);
        }
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arcl.rightClickListener();
            }
        });
    }
    //设置右侧文字
    public void setRightText(String msg, int color, final ActionbarRightClickListener arcl){
        if (tvRightTitle==null){
            tvRightTitle = (TextView) findViewById(R.id.tv_base_rightTitle);
        }
        if (!TextUtils.isEmpty(msg)){
            tvRightTitle.setText(msg);
            tvRightTitle.setVisibility(View.VISIBLE);
            if (color!=0){
                tvRightTitle.setTextColor(ContextCompat.getColor(this,color));
            }
            tvRightTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arcl.rightClickListener();
                }
            });
        }
    }
    //设置左侧返回按钮是否显示
    public void setLeftBack(boolean isVisible){
        if (imgBack==null){
            imgBack = (ImageView) findViewById(R.id.img_base_back);
        }
        if (!isVisible){
            imgBack.setVisibility(View.GONE);
        }
    }
    //设置actionbar的背景色
    public void setActionbarBack(int color){
        if (rlActionbar==null) {
            rlActionbar = (AutoRelativeLayout) findViewById(R.id.rl_base_actionbar);
        }
        if (color!=0){
            rlActionbar.setBackgroundColor(ContextCompat.getColor(this,color));
        }
    }
    public interface ActionbarRightClickListener{
        void rightClickListener();
    }
    private void inflaterRelativieTitle() {
        rlActionbar = (AutoRelativeLayout) findViewById(R.id.rl_base_actionbar);
        imgBack = (ImageView) findViewById(R.id.img_base_back);
        imgRight = (ImageView) findViewById(R.id.img_base_right);
        tvTitle = (TextView) findViewById(R.id.tv_base_title);
        tvRightTitle = (TextView) findViewById(R.id.tv_base_rightTitle);
    }

    protected abstract void initViewAndData();

    @Override
    protected int getLayoutId() {
        return getContentLayoutId();
    }

    protected abstract int getContentLayoutId();//获取布局的id
}
