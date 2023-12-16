

package com.xxx.zzz.aall.gsonllll.googlepp.annotationss;

import com.xxx.zzz.aall.gsonllll.googlepp.Gsonq;
import com.xxx.zzz.aall.gsonllll.googlepp.GsonBuilderq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Exposeqq {
  

  public boolean serialize() default true;


  public boolean deserialize() default true;
}
