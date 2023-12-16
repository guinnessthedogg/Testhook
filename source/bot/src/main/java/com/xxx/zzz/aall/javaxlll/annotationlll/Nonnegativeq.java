package com.xxx.zzz.aall.javaxlll.annotationlll;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierz;
import com.xxx.zzz.aall.javaxlll.annotationlll.metann.Whenz;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierValidator;


@Documented
@TypeQualifierz(applicableTo = Number.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnegativeq {
    Whenz when() default Whenz.ALWAYS;

    class Checker implements TypeQualifierValidator<Nonnegativeq> {

        public Whenz forConstantValue(Nonnegativeq annotation, Object v) {
            if (!(v instanceof Number))
                return Whenz.NEVER;
            boolean isNegative;
            Number value = (Number) v;
            if (value instanceof Long)
                isNegative = value.longValue() < 0;
            else if (value instanceof Double)
                isNegative = value.doubleValue() < 0;
            else if (value instanceof Float)
                isNegative = value.floatValue() < 0;
            else
                isNegative = value.intValue() < 0;

            if (isNegative)
                return Whenz.NEVER;
            else
                return Whenz.ALWAYS;

        }
    }
}
