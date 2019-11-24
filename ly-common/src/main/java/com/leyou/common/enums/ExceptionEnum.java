package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_IS_NULL(400,"商品价格不能为空"),
    CATEGORY_NOT_BE_FOUND(404,"商品不能找到"),
    BRAND_NOT_FOUND(404,"未能查到任何品牌"),
    BRAND_SAVE_ERROR(500,"品牌保存失败"),
    UPLOAD_FILE_ERROR(500,"上传文件失败"),
    FILE_TYPE_NOT_SUPPORT(400,"文件类型不支持"),
    SPECIFICATION_GROUP_NOT_FOUND(404,"未能查到规格组"),
    SPECIFICATION_GROUP_SAVE_FILE(500,"规格组保存失败"),
    SPECIFICATION_PARAMS_NOT_FOUND(404,"规格参数未找到"),
    GOODS_SAVE_ERROR(500,"商品保存失败"),
    GOODS_NOT_FOUND(404,"商品未能查询到"),
    GOODS_DETELE_ERROR(500,"商品删除失败"),
    INVAILD_DATA_TYPE(400,"用户数据校验请求参数有误"),
    INVAILD_VERIFY_CODE(400,"验证码错误"),
    REGISTER_USER_ERROR(500,"用户注册失败"),
    USERNAME_PASSWORD_ERROR(400,"用户名或密码错误"),
    CREATED_KEY_ERROR(400,"创建私钥公钥失败"),
    CREATED_TOKEN_ERROR(400,"生成令牌失败"),
    VERIFY_TOKEN_ERROR(400,"解析令牌失败"),
    ORDER_CREATED_FAIL(500,"订单创建失败"),
    Order_Select_FAIL(500,"订单查询失败"),
    WEI_XIN_PAY_FAIL(500,"微信下单失败"),
    INVALID_ORDER_PARAM(404,"无效的订单参数"),
    INVALID_SIGN_ERROR(404,"无效的签名参数"),
    UPDATE_ORDER_STATUS_ERROR(404,"跟新订单状态失败")
    ;
    private int code;
    private String message;
}
