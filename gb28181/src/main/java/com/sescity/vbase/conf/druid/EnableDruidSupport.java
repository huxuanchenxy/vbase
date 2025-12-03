package com.sescity.vbase.conf.druid;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        DruidConfiguration.class,
})
@ServletComponentScan
public @interface EnableDruidSupport {
}
