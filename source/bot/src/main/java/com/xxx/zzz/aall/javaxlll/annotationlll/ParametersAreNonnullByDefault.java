package com.xxx.zzz.aall.javaxlll.annotationlll;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierDefault;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Nonnullq
@TypeQualifierDefault(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersAreNonnullByDefault {
}
