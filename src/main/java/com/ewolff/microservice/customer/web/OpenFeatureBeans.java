package com.ewolff.microservice.customer.web;

import dev.openfeature.contrib.providers.flagd.FlagdProvider;

import dev.openfeature.sdk.OpenFeatureAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenFeatureBeans {

    @Bean
    public OpenFeatureAPI OpenFeatureAPI() {
        final OpenFeatureAPI openFeatureAPI = OpenFeatureAPI.getInstance();
        openFeatureAPI.setProvider(new FlagdProvider());
        return openFeatureAPI;
    }
}