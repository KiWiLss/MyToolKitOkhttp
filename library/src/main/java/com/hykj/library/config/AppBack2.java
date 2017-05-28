package com.hykj.library.config;


import com.hykj.library.utils.common.TT;

/**
 * Created by 刘少帅 on 2017/4/10.
 * Email:1771050446@qq.com
 */

public class AppBack2<T> {

    /**
     * 成功0
     * 错误-1
     */
    private int status;
    private T result;
    private int totalcount;//记录总数
    private String hasNext; //是否有下一页

    /**
     * 判断正常返回，错误返回就toast
     *
     * @return
     */
    public boolean unSuccess() {
        if (status == 0) {
            return false;
        } else {
            if (result != null) {
                if (result instanceof String) {
                    //   MyToast.showToast( result.toString());
                    TT.show(result.toString());
                }
            }
            return true;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }


    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean getHasNext() {
        if ("1".equals(hasNext)) {
            return true;
        } else {
            return false;
        }
    }

    public void setHasNext(String hasNext) {
        this.hasNext = hasNext;
    }
}
