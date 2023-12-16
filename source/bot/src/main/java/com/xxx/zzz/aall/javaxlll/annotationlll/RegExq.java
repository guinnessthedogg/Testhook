package com.xxx.zzz.aall.javaxlll.annotationlll;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierNickname;
import com.xxx.zzz.aall.javaxlll.annotationlll.metann.Whenz;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierValidator;


@Documented
@Syntaxqaxcx("RegEx")
@TypeQualifierNickname
@Retention(RetentionPolicy.RUNTIME)
public @interface RegExq {
    Whenz when() default Whenz.ALWAYS;

    static class Checker implements TypeQualifierValidator<RegExq> {

        public Whenz forConstantValue(RegExq annotation, Object value) {
            if (!(value instanceof String))
                return Whenz.NEVER;

            try {
                Pattern.compile((String) value);
            } catch (PatternSyntaxException e) {
                return Whenz.NEVER;
            }
            return Whenz.ALWAYS;

        }

    }

}
