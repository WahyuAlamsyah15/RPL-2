package config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContext {

    private static ApplicationContext context;

    public static void init() {
        if (context == null) {
            context = new AnnotationConfigApplicationContext(AppConfig.class);
        }
    }

    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) {
            init();
        }
        return context.getBean(beanClass);
    }
}
