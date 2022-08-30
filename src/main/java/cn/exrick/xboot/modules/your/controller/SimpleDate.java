package cn.exrick.xboot.modules.your.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 格式化时间
 */
public class SimpleDate {
    public String simple(Date date){
        String pattern="yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
