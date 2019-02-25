package com.style.common.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.style.common.constant.Constants;
import com.style.common.model.Page;
import com.style.common.web.servlet.ServletUtils;
import com.style.utils.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class BaseController {

    /**
     * 将指定参数转换为Like
     * @param params
     * @param likes
     * @return
     */
    protected Map<String, Object> paramsToLike(Map<String, Object> params, String... likes){
        for (String like : likes){
            String val = (String)params.get(like);
            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(val)){
                params.put(like, "%" + val + "%");
            }else {
                params.put(like, null);
            }
        }
        return params;
    }

}
