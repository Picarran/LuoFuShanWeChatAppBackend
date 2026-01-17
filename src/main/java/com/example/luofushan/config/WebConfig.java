package com.example.luofushan.config;

import com.example.luofushan.security.TokenFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<Filter> tokenFilterRegistration(TokenFilter tokenFilter) {
        FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
        reg.setFilter(tokenFilter);
//        reg.addUrlPatterns("/*");
        reg.addUrlPatterns("/post/*", "/checkin/*","/user/*","/exchange/*", "/mall/item/exchange", "/common/uploads");
        reg.setOrder(1);
        return reg;
    }
}