package com.my_hourly.common.initializer.values;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.super-admin")
public class SuperAdminProperties {

    private String username;
    private String email;
    private String password;

}
