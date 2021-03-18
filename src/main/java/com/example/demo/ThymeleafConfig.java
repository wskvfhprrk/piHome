package com.example.demo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * @author hejz
 * @version 1.0
 * @date 2021/3/8 10:35
 * 这里之所以选用"/static/"路径，是为了兼容静态资源路径。静态资源配置的路径为"/static/html"，在访问路径设置的是"/html/**"，访问静态资源的路径为"/html/……"。这样，在访问thymeleaf构建的html页面时，需要加上"/html/"，和普通的静态html页面路径一致。
 */
@EnableAutoConfiguration
public class ThymeleafConfig {
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver =
                new SpringResourceTemplateResolver();
        templateResolver.setCharacterEncoding("utf-8");//设置编码集
        //设置模版引擎页面的路径
        templateResolver.setPrefix("classpath:/static/");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine
                = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver =
                new ThymeleafViewResolver();
        viewResolver.setCharacterEncoding("utf-8");
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setOrder(1);
        //定义html和xhtml后缀的文件被thymeleaf引擎翻译
        viewResolver.setViewNames(
                new String[] {"*.html", "*.xhtml"});
        return viewResolver;
    }
}
