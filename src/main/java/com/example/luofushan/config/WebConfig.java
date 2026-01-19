package com.example.luofushan.config;

import com.example.luofushan.security.AdminFilter;
import com.example.luofushan.security.MerchantTokenFilter;
import com.example.luofushan.security.TokenFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 开启 CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",
                    "https://www.picarran.xyz"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Bean
    public FilterRegistrationBean<Filter> tokenFilterRegistration(TokenFilter tokenFilter) {
        FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
        reg.setFilter(tokenFilter);
//        reg.addUrlPatterns("/*");
        reg.addUrlPatterns(
            "/post/*", 
            "/checkin/*",
            "/user/*",
            "/exchange/*", 
            "/mall/item/exchange"
        );
        reg.setOrder(1);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<Filter> merchantTokenFilterRegistration(MerchantTokenFilter merchantTokenFilter) {
        FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
        reg.setFilter(merchantTokenFilter);
//        reg.addUrlPatterns("/*");
        reg.addUrlPatterns(
                "/merchant/*"
        );
        reg.setOrder(10);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<Filter> adminTokenFilterRegistration(AdminFilter adminFilter) {
        FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
        reg.setFilter(adminFilter);
//        reg.addUrlPatterns("/*");
        reg.addUrlPatterns(
                "/admin/*"
        );
        reg.setOrder(100);
        return reg;
    }
}