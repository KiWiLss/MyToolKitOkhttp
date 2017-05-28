package com.hykj.library.config;


import com.hykj.library.utils.common.TT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: KiWi刘少帅 on 2017/5/21   17:05
 * EMail: 1771050446@qq.com
 * User: Administrator
 * Describe:
 */

public class AppBack {

    /**
     * 成功0
     * 错误-1
     */
    private int status;
    private Object result;
    private Object result_en;
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

    public Map<String, String> getMap() {
        return getInMap(result);
    }

    private Map<String, String> getInMap(Object object) {
        Map<String, String> map;
        if (object == null) {
            return new HashMap<>();
        }
        try {
            return (Map<String, String>) result;
        } catch (Exception e) {
            try {
                Map<String, Object> map2 = (Map<String, Object>) result;
                map = new HashMap<>();
                for (String key : map2.keySet()) {
                    map.put(key, String.valueOf(map2.get(key)));
                }
                return map;
            } catch (Exception e1) {
                return new HashMap<>();
            }
        }

    }

    public List<Map<String, String>> getList() {
        List<Map<String, String>> list;
        if (result == null) {
            return Collections.emptyList();
        }
        try {

            return (List<Map<String, String>>) result;
        } catch (Exception e) {
            try {
                list = new ArrayList<>();
                for (Object o : (List<Object>) result) {
                    list.add(getInMap(o));
                }
                return list;
            } catch (Exception e1) {
                return Collections.emptyList();
            }
        }
    }


    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
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

    public Object getResult_en() {
        return result_en;
    }

    public void setResult_en(Object result_en) {
        this.result_en = result_en;
    }
}
