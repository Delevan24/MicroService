package com.eternity.microservice.base.common;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 错误代码定义: 公共部分(数值在6000以内)
 */
public class BaseErrorCode {

    /**
     * 通过key从资源文件中获取对应的错误信息描述
     *
     * @param key
     * @return
     */
    public static String getMessage(String key) {
        return ResourceBundle.getBundle("i18n/message").getString(key);
    }

    /**
     * 通过key和指定(EN/US)的资源文件获取对应的错误信息描述
     *
     * @param key
     * @param locale
     * @return
     */
    public static String getMessage(String key, Locale locale) {
        return ResourceBundle.getBundle("i18n/message", locale).getString(key);
    }

    /**
     * 系统异常
     */
    public final static int SYSTEM_EXCEPTION = 1110;

    /**
     * 系统参数配置异常
     */
    public final static int SYSTEM_PARAMETER_EXCEPTION = 1115;

    /**
     * IO异常
     */
    public final static int IO_EXCEPTION = 1120;

    /**
     * 接口未实现
     */
    public final static int intterface_not_implemented = 1130;

    /**
     * 参数为空
     */
    public final static int PATAMETER_IS_NULL = 1210;

    /**
     * 参数无效
     */
    public final static int PATAMETER_IS_INVALID = 1220;

    /**
     * 参数无效,包含除中文,数字,字母,下划线以外的符号
     */
    public final static int PATAMETER_NAME_IS_INVALID = 1230;

    /**
     * 令牌不存在
     */
    public final static int TOKEN_NOT_EXIST = 1310;

    /**
     * 令牌无效
     */
    public final static int TOKEN_IS_INVALID = 1320;

    /**
     * 没有令牌
     */
    public final static int VISIT_WITHOUT_TOKEN = 1330;

    /**
     * Response Json解析异常
     */
    public final static int RESPONSE_JSON_EXCEPTION = 1340;

    /**
     * 消息发送异常
     */
    public final static int SEND_MESSAGE_EXCEPTION = 1350;
}


