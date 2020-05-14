package com.scrcu.ebank.ebap.batch.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelData {

    //名称
    String name() default "";
    //该行所在单元格索引
    int cellIndex();
    //校验正则表达式
    String regex() default "";

}

