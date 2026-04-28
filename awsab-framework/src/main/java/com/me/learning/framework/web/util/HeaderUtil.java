package com.me.learning.framework.web.util;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;

import lombok.extern.slf4j.Slf4j;

/**
 *  Utility for creating HTTP Headers
 */

@Slf4j
public final class HeaderUtil {

    private HeaderUtil() {
    }

    /**
     *
     * @param appName a {@link String} Object
     * @param message a {@link String} Object
     * @param param a {@link String} Object
     *
     * @return a {@link HttpHeaders} Object
     */
    public static HttpHeaders createEntityAlert(
            String appName,
            String message,
            String param
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + appName + "-alert", message);
        headers.add("X-" + appName + "-params", URLEncoder.encode(param, StandardCharsets.UTF_8));
        return headers;
    }

    /**
     * <p>createEntityCreationAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createEntityCreationAlert(
            String applicationName,
            boolean enableTranslation,
            String entityName,
            String param
    ) {
        String message = enableTranslation
                ? applicationName + "." + entityName + ".created"
                : "A new " + entityName + " is created with identifier " + param;
        return createEntityAlert(applicationName, message, param);
    }

    /**
     * <p>createEntityUpdateAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createEntityUpdateAlert(
            String applicationName,
            boolean enableTranslation,
            String entityName,
            String param
    ) {
        String message = enableTranslation
                ? applicationName + "." + entityName + ".updated"
                : "A " + entityName + " is updated with identifier " + param;
        return createEntityAlert(applicationName, message, param);
    }


    /**
     * <p>createEntityDeletionAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link String} object.
     * @param param a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createEntityDeletionAlert(
            String applicationName,
            boolean enableTranslation,
            String entityName,
            String param
    ) {
        String message = enableTranslation
                ? applicationName + "." + entityName + ".deleted"
                : "A " + entityName + " is deleted with identifier " + param;
        return createEntityAlert(applicationName, message, param);
    }

    /**
     * <p>createFailureAlert.</p>
     *
     * @param applicationName a {@link String} object.
     * @param enableTranslation a boolean.
     * @param entityName a {@link String} object.
     * @param errorKey a {@link String} object.
     * @param defaultMessage a {@link String} object.
     * @return a {@link HttpHeaders} object.
     */
    public static HttpHeaders createFailureAlert(
            String applicationName,
            boolean enableTranslation,
            String entityName,
            String errorKey,
            String defaultMessage
    ) {
        //log.error("Entity processing failed, {}", defaultMessage);

        String message = enableTranslation ? "error." + errorKey : defaultMessage;

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-error", message);
        headers.add("X-" + applicationName + "-params", entityName);
        return headers;
    }

}
