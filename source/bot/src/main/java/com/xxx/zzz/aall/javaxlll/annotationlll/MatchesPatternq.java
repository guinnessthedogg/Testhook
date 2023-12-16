package com.xxx.zzz.aall.javaxlll.annotationlll;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierz;
import com.xxx.zzz.aall.javaxlll.annotationlll.metann.Whenz;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierValidator;


@Documented
@TypeQualifierz(applicableTo = String.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchesPatternq {
    @RegExq
    String value();

    int flags() default 0;

    static class Checker implements TypeQualifierValidator<MatchesPatternq> {
        public Whenz forConstantValue(MatchesPatternq annotation, Object value) {
            Pattern p = Pattern.compile(annotation.value(), annotation.flags());
            if (p.matcher(((String) value)).matches())
                return Whenz.ALWAYS;
            return Whenz.NEVER;
        }

    }
}
