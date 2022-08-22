package cn.exrick.xboot.common.constant;

/**
 * 常量
 * @author Exrickx
 */
public interface CommonConstant {

    /**
     * 全部数据权限
     */
    Integer DATA_TYPE_ALL = 0;

    /**
     * 自定义数据权限
     */
    Integer DATA_TYPE_CUSTOM = 1;

    /**
     * 本部门及以下
     */
    Integer DATA_TYPE_UNDER = 2;

    /**
     * 本部门
     */
    Integer DATA_TYPE_SAME = 3;

    /**
     * 自己
     */
    Integer DATA_TYPE_SELF = 4;

    /**
     * 正常状态
     */
    Integer STATUS_NORMAL = 0;

    /**
     * 禁用状态
     */
    Integer STATUS_DISABLE = -1;

    /**
     * 逻辑删除标志 false
     */
    Integer DEL_FLAG_FALSE = 0;

    /**
     * 逻辑删除标志 true
     */
    Integer DEL_FLAG_TRUE = 1;

    /**
     * 删除至回收站标志
     */
    Integer DEL_FLAG_TRASH = 2;

    /**
     * 限流前缀
     */
    String LIMIT_PRE = "XBOOT_LIMIT:";

    /**
     * 限流标识
     */
    String LIMIT_ALL = "XBOOT_LIMIT_ALL";

    /**
     * 顶部菜单类型权限
     */
    Integer PERMISSION_NAV = -1;

    /**
     * 页面类型权限
     */
    Integer PERMISSION_PAGE = 0;

    /**
     * 操作类型权限
     */
    Integer PERMISSION_OPERATION = 1;

    /**
     * 1级菜单父id
     */
    String PARENT_ID = "0";

    /**
     * 0级菜单
     */
    Integer LEVEL_ZERO = 0;

    /**
     * 1级菜单
     */
    Integer LEVEL_ONE = 1;

    /**
     * 2级菜单
     */
    Integer LEVEL_TWO = 2;

    /**
     * 3级菜单
     */
    Integer LEVEL_THREE = 3;

    /**
     * 短信验证码key前缀
     */
    String PRE_SMS = "XBOOT_PRE_SMS:";

    /**
     * 邮件验证码key前缀
     */
    String PRE_EMAIL = "XBOOT_PRE_EMAIL:";

    /**
     * 部门负责人类型 主负责人
     */
    Integer HEADER_TYPE_MAIN = 0;

    /**
     * 部门负责人类型 副负责人
     */
    Integer HEADER_TYPE_VICE = 1;
}
