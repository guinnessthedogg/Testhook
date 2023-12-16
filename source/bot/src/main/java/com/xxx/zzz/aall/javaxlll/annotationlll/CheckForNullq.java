package com.xxx.zzz.aall.javaxlll.annotationlll;

import com.xxx.zzz.aall.javaxlll.annotationlll.metann.TypeQualifierNickname;
import com.xxx.zzz.aall.javaxlll.annotationlll.metann.Whenz;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@TypeQualifierNickname
@Nonnullq(when = Whenz.MAYBE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckForNullq {

}
