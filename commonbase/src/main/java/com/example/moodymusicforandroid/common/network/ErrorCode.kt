package com.example.moodymusicforandroid.common.network

/**
 * 业务错误码定义
 * 所有后端返回的业务错误码都应该在这里定义
 */
object ErrorCode {

    // ========== 通用状态码 ==========
    const val SUCCESS = 0                // 成功
    const val SUCCESS_200 = 200          // 成功（HTTP标准）

    // ========== 客户端错误 1xxx ==========
    const val CLIENT_ERROR = 1000        // 客户端错误
    const val INVALID_PARAM = 1001       // 参数错误
    const val MISSING_PARAM = 1002       // 缺少参数
    const val INVALID_SIGNATURE = 1003   // 签名验证失败
    const val REQUEST_EXPIRED = 1004     // 请求过期
    const val DUPLICATE_REQUEST = 1005   // 重复请求

    // ========== 认证授权错误 10xx ==========
    const val UNAUTHORIZED = 1020        // 未授权（未登录）
    const val TOKEN_EXPIRED = 1021       // Token过期
    const val TOKEN_INVALID = 1022       // Token无效
    const val LOGIN_FAILED = 1023        // 登录失败
    const val ACCOUNT_LOCKED = 1024      // 账号锁定
    const val ACCOUNT_DISABLED = 1025    // 账号禁用
    const val PASSWORD_ERROR = 1026      // 密码错误
    const val VERIFICATION_CODE_ERROR = 1027  // 验证码错误
    const val PERMISSION_DENIED = 1028   // 权限不足

    // ========== 用户相关错误 11xx ==========
    const val USER_NOT_FOUND = 1100      // 用户不存在
    const val USER_ALREADY_EXISTS = 1101 // 用户已存在
    const val USERNAME_ALREADY_EXISTS = 1102  // 用户名已存在
    const val EMAIL_ALREADY_EXISTS = 1103     // 邮箱已存在
    const val PHONE_ALREADY_EXISTS = 1104     // 手机号已存在
    const val USER_INFO_INCOMPLETE = 1105     // 用户信息不完整

    // ========== 音乐资源错误 12xx ==========
    const val MUSIC_NOT_FOUND = 1200     // 音乐不存在
    const val MUSIC_NOT_AVAILABLE = 1201 // 音乐不可用
    const val MUSIC_COPYRIGHTED = 1202   // 音乐版权限制
    const val MUSIC_DELETED = 1203       // 音乐已删除
    const val PLAYLIST_NOT_FOUND = 1204  // 歌单不存在
    const val PLAYLIST_EMPTY = 1205      // 歌单为空
    const val ALBUM_NOT_FOUND = 1206     // 专辑不存在
    const val ARTIST_NOT_FOUND = 1207    // 歌手不存在

    // ========== VIP/会员错误 13xx ==========
    const val VIP_EXPIRED = 1300         // VIP已过期
    const val VIP_REQUIRED = 1301        // 需要VIP权限
    const val VIP_NOT_ENOUGH = 1302      // VIP次数不足
    const val NOT_VIP = 1303             // 非VIP用户

    // ========== 文件上传错误 14xx ==========
    const val FILE_TOO_LARGE = 1400      // 文件过大
    const val FILE_TYPE_ERROR = 1401     // 文件类型错误
    const val FILE_UPLOAD_FAILED = 1402  // 文件上传失败
    const val FILE_NOT_FOUND = 1403      // 文件不存在
    const val FILE_DOWNLOAD_FAILED = 1404  // 文件下载失败

    // ========== 数据操作错误 15xx ==========
    const val DATA_NOT_FOUND = 1500      // 数据不存在
    const val DATA_ALREADY_EXISTS = 1501 // 数据已存在
    const val DATA_SAVE_FAILED = 1502    // 数据保存失败
    const val DATA_DELETE_FAILED = 1503  // 数据删除失败
    const val DATA_UPDATE_FAILED = 1504  // 数据更新失败

    // ========== 业务逻辑错误 16xx ==========
    const val OPERATION_FAILED = 1600    // 操作失败
    const val OPERATION_NOT_ALLOWED = 1601  // 不允许的操作
    const val RESOURCE_CONFLICT = 1602   // 资源冲突
    const val QUOTA_EXCEEDED = 1603      // 超出配额
    const val FREQUENCY_LIMIT = 1604     // 访问频率限制

    // ========== 服务器错误 20xx ==========
    const val SERVER_ERROR = 2000        // 服务器错误
    const val SERVER_BUSY = 2001         // 服务器繁忙
    const val SERVER_MAINTENANCE = 2002  // 服务器维护中
    const val DATABASE_ERROR = 2003      // 数据库错误
    const val CACHE_ERROR = 2004         // 缓存错误
    const val THIRD_PARTY_ERROR = 2005   // 第三方服务错误

    // ========== 网络错误 30xx（非HTTP错误）==========
    const val NETWORK_ERROR = 3000       // 网络错误
    const val CONNECTION_TIMEOUT = 3001  // 连接超时
    const val READ_TIMEOUT = 3002        // 读取超时
    const val WRITE_TIMEOUT = 3003       // 写入超时
    const val DNS_ERROR = 3004           // DNS解析失败
    const val HOST_NOT_FOUND = 3005      // 主机不存在

    /**
     * 获取错误码对应的默认提示信息
     */
    fun getDefaultMessage(code: Int): String {
        return when (code) {
            // 通用
            SUCCESS, SUCCESS_200 -> "成功"
            INVALID_PARAM -> "参数错误"
            MISSING_PARAM -> "缺少必要参数"
            INVALID_SIGNATURE -> "签名验证失败"
            REQUEST_EXPIRED -> "请求已过期"
            DUPLICATE_REQUEST -> "请勿重复提交"

            // 认证授权
            UNAUTHORIZED -> "请先登录"
            TOKEN_EXPIRED -> "登录已过期，请重新登录"
            TOKEN_INVALID -> "登录信息无效，请重新登录"
            LOGIN_FAILED -> "登录失败，请检查账号密码"
            ACCOUNT_LOCKED -> "账号已被锁定"
            ACCOUNT_DISABLED -> "账号已被禁用"
            PASSWORD_ERROR -> "密码错误"
            VERIFICATION_CODE_ERROR -> "验证码错误"
            PERMISSION_DENIED -> "权限不足"

            // 用户相关
            USER_NOT_FOUND -> "用户不存在"
            USER_ALREADY_EXISTS -> "用户已存在"
            USERNAME_ALREADY_EXISTS -> "用户名已被占用"
            EMAIL_ALREADY_EXISTS -> "邮箱已被注册"
            PHONE_ALREADY_EXISTS -> "手机号已被注册"
            USER_INFO_INCOMPLETE -> "用户信息不完整"

            // 音乐资源
            MUSIC_NOT_FOUND -> "音乐不存在"
            MUSIC_NOT_AVAILABLE -> "音乐暂时不可用"
            MUSIC_COPYRIGHTED -> "该音乐受版权限制"
            MUSIC_DELETED -> "音乐已下架"
            PLAYLIST_NOT_FOUND -> "歌单不存在"
            PLAYLIST_EMPTY -> "歌单为空"
            ALBUM_NOT_FOUND -> "专辑不存在"
            ARTIST_NOT_FOUND -> "歌手不存在"

            // VIP/会员
            VIP_EXPIRED -> "会员已过期"
            VIP_REQUIRED -> "该功能需要会员权限"
            VIP_NOT_ENOUGH -> "会员次数不足"
            NOT_VIP -> "该功能仅供会员使用"

            // 文件上传
            FILE_TOO_LARGE -> "文件过大"
            FILE_TYPE_ERROR -> "文件类型不支持"
            FILE_UPLOAD_FAILED -> "文件上传失败"
            FILE_NOT_FOUND -> "文件不存在"
            FILE_DOWNLOAD_FAILED -> "文件下载失败"

            // 数据操作
            DATA_NOT_FOUND -> "数据不存在"
            DATA_ALREADY_EXISTS -> "数据已存在"
            DATA_SAVE_FAILED -> "保存失败"
            DATA_DELETE_FAILED -> "删除失败"
            DATA_UPDATE_FAILED -> "更新失败"

            // 业务逻辑
            OPERATION_FAILED -> "操作失败"
            OPERATION_NOT_ALLOWED -> "当前不允许此操作"
            RESOURCE_CONFLICT -> "资源冲突"
            QUOTA_EXCEEDED -> "已超出使用配额"
            FREQUENCY_LIMIT -> "操作过于频繁，请稍后再试"

            // 服务器
            SERVER_ERROR -> "服务器错误"
            SERVER_BUSY -> "服务器繁忙，请稍后再试"
            SERVER_MAINTENANCE -> "服务器维护中"
            DATABASE_ERROR -> "数据错误"
            CACHE_ERROR -> "缓存错误"
            THIRD_PARTY_ERROR -> "第三方服务错误"

            // 网络
            NETWORK_ERROR -> "网络连接失败"
            CONNECTION_TIMEOUT -> "连接超时"
            READ_TIMEOUT -> "读取超时"
            WRITE_TIMEOUT -> "写入超时"
            DNS_ERROR -> "DNS解析失败"
            HOST_NOT_FOUND -> "服务器地址错误"

            else -> "未知错误"
        }
    }

    /**
     * 判断是否为认证错误
     */
    fun isAuthError(code: Int): Boolean {
        return code in 1020..1029
    }

    /**
     * 判断是否为Token错误
     */
    fun isTokenError(code: Int): Boolean {
        return code == TOKEN_EXPIRED || code == TOKEN_INVALID
    }

    /**
     * 判断是否为VIP权限错误
     */
    fun isVipError(code: Int): Boolean {
        return code in 1300..1303
    }

    /**
     * 判断是否为网络错误
     */
    fun isNetworkError(code: Int): Boolean {
        return code in 3000..3099
    }

    /**
     * 判断是否需要重新登录
     */
    fun shouldRelogin(code: Int): Boolean {
        return code == TOKEN_EXPIRED ||
                code == TOKEN_INVALID ||
                code == UNAUTHORIZED
    }

    /**
     * 判断错误是否可以重试
     */
    fun canRetry(code: Int): Boolean {
        return code == SERVER_BUSY ||
                code == CONNECTION_TIMEOUT ||
                code == READ_TIMEOUT ||
                code == FREQUENCY_LIMIT
    }
}
