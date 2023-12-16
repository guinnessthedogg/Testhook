package com.xxx.zzz.aall.javaxlll.annotationlll.metann;

import java.lang.annotation.Annotation;

import com.xxx.zzz.aall.javaxlll.annotationlll.Nonnullq;

public interface TypeQualifierValidator<A extends Annotation> {

    public @Nonnullq
    Whenz forConstantValue(@Nonnullq A annotation, Object value);
}
