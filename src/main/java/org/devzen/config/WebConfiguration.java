package org.devzen.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.Charset;
import java.util.List;

/**
 * TODO: 这里需要写注释
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {


    @Bean
    public FastJsonHttpMessageConverter customJackson2HttpMessageConverter() {
        FastJsonHttpMessageConverter jsonConverter = new FastJsonHttpMessageConverter();
        jsonConverter.setCharset(Charset.forName("UTF-8"));
        jsonConverter.setFeatures(SerializerFeature.WriteDateUseDateFormat);
        return jsonConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(customJackson2HttpMessageConverter());
        super.addDefaultHttpMessageConverters(converters);
    }


}
