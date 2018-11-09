package fanpeihua.huamusic.di.scope;

import javax.inject.Qualifier;

@Qualifier
public @interface ContextLife {
    String value() default "Application";
}
