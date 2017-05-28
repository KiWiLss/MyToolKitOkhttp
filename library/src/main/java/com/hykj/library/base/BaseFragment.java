package com.hykj.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;
import me.leefeng.promptlibrary.PromptDialog;

/**
 * Author: KiWi刘少帅 on 2017/5/21   17:01
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public abstract class BaseFragment extends Fragment {

    private Unbinder mUnbinder;
    private ListCompositeDisposable listCompositeDisposable =
            new ListCompositeDisposable();//rxjava生命周期管理
    private PromptDialog mPromptDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        //界面和数据的操作
        initViewAndData();
        return view;
    }

    protected void addDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            listCompositeDisposable.add(disposable);
        }
    }
    protected void reDisposable(Disposable disposable) {
        if (disposable != null) {
            listCompositeDisposable.remove(disposable);
        }
    }
    protected void clear() {
        if (!listCompositeDisposable.isDisposed()) {
            listCompositeDisposable.clear();
        }
    }

    /**
     * 正在加载中对话框
     */
    public void showLoadingDialog(){
        if (mPromptDialog==null) {
            mPromptDialog = new PromptDialog(getActivity());
        }
        mPromptDialog.getDefaultBuilder().backAlpha(0);
        mPromptDialog.showLoading("拼命加载中......",true);
    }

    /**
     * 去除正在加载中对话框
     */
    public void dismissLoadingDialog(){
        if (mPromptDialog!=null){
            mPromptDialog.dismiss();
        }
    }
    /**
     * 加载成功对话框
     */
    public void showSuccessDialog(String msg){
        if (mPromptDialog==null) {
            mPromptDialog = new PromptDialog(getActivity());
        }
        mPromptDialog.getDefaultBuilder().backAlpha(0);
        if (TextUtils.isEmpty(msg)){
            mPromptDialog.showSuccess("成功",true);
        }else {
            mPromptDialog.showSuccess(msg,true);
        }
    }
    /**
     * 加载失败对话框
     */
    public void showErrorDialog(String msg){
        if (mPromptDialog==null) {
            mPromptDialog = new PromptDialog(getActivity());
        }
        mPromptDialog.getDefaultBuilder().backAlpha(0);
        if (TextUtils.isEmpty(msg)){
            mPromptDialog.showSuccess("操作失败,请重新操作",true);
        }else {
            mPromptDialog.showSuccess(msg,true);
        }
    }
    /**
     * 提示信息对话框
     */
    public void showHintDialog(String msg){
        if (mPromptDialog==null) {
            mPromptDialog = new PromptDialog(getActivity());
        }
        mPromptDialog.getDefaultBuilder().backAlpha(0);
        if (TextUtils.isEmpty(msg)){
            mPromptDialog.showSuccess("hint",true);
        }else {
            mPromptDialog.showSuccess(msg,true);
        }
    }
    protected abstract void initViewAndData();

    protected abstract int getLayoutId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder!=null){
            mUnbinder.unbind();
            mUnbinder=null;
        }
    }
}
