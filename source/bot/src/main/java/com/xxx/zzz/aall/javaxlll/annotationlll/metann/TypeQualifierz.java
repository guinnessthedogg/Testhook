package com.xxx.zzz.aall.javaxlll.annotationlll.metann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeQualifierz {

    
    Class<?> applicableTo() default Object.class;

}
