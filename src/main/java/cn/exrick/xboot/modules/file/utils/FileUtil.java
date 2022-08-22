package cn.exrick.xboot.modules.file.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author exrick
 */
public class FileUtil {

    /**
     * 自动删除天数
     */
    private static final Integer AUTO_DEL_DAY = 7;

    public static Boolean isOvertime(Date time) {

        if (time == null) {
            return false;
        }
        Date curr = DateUtil.date();
        Date timeout = DateUtil.offset(time, DateField.DAY_OF_MONTH, AUTO_DEL_DAY);
        int ret = DateUtil.compare(curr, timeout);
        return ret > 0;
    }

    public static String getRestTime(Date time) {

        if (time == null) {
            return "";
        }

        String formatBetween = "";
        Date curr = DateUtil.date();
        Date timeout = DateUtil.offset(time, DateField.DAY_OF_MONTH, AUTO_DEL_DAY);
        Long betweenHour = DateUtil.between(curr, timeout, DateUnit.HOUR);
        if (betweenHour > 24) {
            formatBetween = DateUtil.between(curr, timeout, DateUnit.DAY) + 1 + "天";
        } else if (betweenHour > 1 && betweenHour <= 24) {
            formatBetween = DateUtil.between(curr, timeout, DateUnit.HOUR) + 1 + "小时";
        } else {
            formatBetween = DateUtil.between(curr, timeout, DateUnit.MINUTE) + 1 + "分";
        }
        return formatBetween;
    }
}
