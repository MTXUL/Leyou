package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

import java.util.Date;
@Data
public class ExceptionResult {
    private int code;
    private String message;
    private Date date;
    public ExceptionResult(ExceptionEnum enums){
        this.code=enums.getCode();
        this.message=enums.getMessage();
        this.date=findDate();
    }

    private static Date findDate(){
        long l = System.currentTimeMillis()+1000*60*60*8;
        return new Date(l);
    }
}
