package cn.exrick.xboot.common.constant;

/**
 * 常量
 * @author Exrickx
 */
public interface OssConstant {

    /**
     * 本地文件存储
     */
    Integer OSS_LOCAL = 0;
    /**
     * 七牛云OSS存储
     */
    Integer OSS_QINIU = 1;
    /**
     * 阿里云OSS存储
     */
    Integer OSS_ALI = 2;
    /**
     * 腾讯云COS存储
     */
    Integer OSS_TENCENT = 3;
    /**
     * MINIO存储
     */
    Integer OSS_MINIO = 4;
}
