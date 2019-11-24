package com.leyou.order.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderStatusEnum {
    UN_PAY(1,"未支付"),
    PAY_UN_GET(2,"付款但为收到"),
    SUCCESS(3,"购物成功"),
    FAIL(4,"购物失败")
    ;



    private int status;
    private String description;
}
