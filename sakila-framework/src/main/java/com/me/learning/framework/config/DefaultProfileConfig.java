/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 23/07/2025
 * Usage    :
 * Since    : Version 1.0
 */
package com.me.learning.framework.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;

public final class DefaultProfileConfig {

    private static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";

    private DefaultProfileConfig() {
    }

    public static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties = new HashMap<>();
        defProperties.put(SPRING_PROFILE_DEFAULT, SakilaProfileConstants.SPRING_PROFILE_DEVELOPMENT);
        app.setDefaultProperties(defProperties);
    }
}
