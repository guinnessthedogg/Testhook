package com.xxx.zzz.aall.javaxlll.annotationlll;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierz;
import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierValidator;
import com.xxx.zzz.aall.javaxlll.annotationlll.metann.Whenz;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@TypeQualifierz
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnullq {
    Whenz when() default Whenz.ALWAYS;

    class Checker implements TypeQualifierValidator<Nonnullq> {

        public Whenz forConstantValue(Nonnullq qualifierArgument, Object value) {
            if (value == null)
                return Whenz.NEVER;
            return Whenz.ALWAYS;
        }
    }
}
