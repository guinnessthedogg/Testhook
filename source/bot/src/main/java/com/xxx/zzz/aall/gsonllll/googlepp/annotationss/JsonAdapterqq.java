

package com.xxx.zzz.aall.gsonllll.googlepp.annotationss;

import com.xxx.zzz.aall.gsonllll.googlepp.GsonBuilderq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonDeserializerq;
import com.xxx.zzz.aall.gsonllll.googlepp.JsonSerializerq;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterqdscvvf;
import com.xxx.zzz.aall.gsonllll.googlepp.TypeAdapterFactoryqqeeqw;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface JsonAdapterqq {

  
  Class<?> value();

  
  boolean nullSafe() default true;

}
