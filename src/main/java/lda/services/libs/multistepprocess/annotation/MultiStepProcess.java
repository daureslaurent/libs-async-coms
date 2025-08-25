package lda.services.libs.multistepprocess.annotation;


import jakarta.inject.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Singleton
public @interface MultiStepProcess {
    String name();
}
