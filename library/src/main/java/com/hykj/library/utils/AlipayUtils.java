package com.hykj.library.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.hykj.library.utils.common.TT;

/**
 * Author: KiWi刘少帅 on 2017/5/21   16:53
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class AlipayUtils {
    private Activity activity;
    private PaySuccessResult psr;
    public AlipayUtils(Activity activity, PaySuccessResult psr){
        this.activity=activity;
        this.psr=psr;
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String strRet = (String) msg.obj;
            switch (msg.what) {
                case 1:
                    try {
                        // 获取交易状态码，具体状态代码请参看文档
                        String tradeStatus = "resultStatus={";
                        int imemoStart = strRet.indexOf("resultStatus=");
                        imemoStart += tradeStatus.length();
                        int imemoEnd = strRet.indexOf("};memo=");
                        tradeStatus = strRet.substring(imemoStart, imemoEnd);
                        // 支付成功,处理结果
                        if (tradeStatus.equals("9000")) {
                            TT.show("支付成功!");
                            psr.successResult();
                        } else {
                            TT.show("支付失败!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case -2:
                    TT.show("您尚未安装支付宝！！");

                    break;
                case -1:
                    TT.show("交易状态获取失败！！");
                default:
                    break;
            }

        }
    };

    public interface PaySuccessResult{
        void successResult();
    }

    public void alipay( String orderInfo,String packageName){
        if (!checkApkExist(activity,packageName)) {
            Message msg = new Message();
            msg.what = -2;
            mHandler.sendMessage(msg);
        } else {
            realAlipay(orderInfo);
        }
    }

    private void realAlipay(final String orderInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    PayTask payTask = new PayTask(activity);
                    String result = payTask.pay(orderInfo, true);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }catch (Exception e){
                    Message msg = new Message();
                    msg.what = -1;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();


    }


    /**
     * com.eg.android.AlipayGphone 根据包名判断应用程序是否已经存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
