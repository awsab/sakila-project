package com.me.learning.framework.web.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

@DisplayName("HeaderUtil")
class HeaderUtilTest {

    @Test
    @DisplayName("createEntityAlert adds X-appName-alert and X-appName-params headers")
    void createEntityAlertAddsHeaders() {
        HttpHeaders headers = HeaderUtil.createEntityAlert("myApp", "actor.created", "42");

        assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("actor.created");
        assertThat(headers.getFirst("X-myApp-params")).isEqualTo("42");
    }

    @Test
    @DisplayName("createEntityCreationAlert with translation uses key-based message")
    void createEntityCreationAlertWithTranslation() {
        HttpHeaders headers =
                HeaderUtil.createEntityCreationAlert("myApp", true, "actor", "99");

        assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("myApp.actor.created");
        assertThat(headers.getFirst("X-myApp-params")).isEqualTo("99");
    }

    @Test
    @DisplayName("createEntityCreationAlert without translation uses human-readable message")
    void createEntityCreationAlertWithoutTranslation() {
        HttpHeaders headers =
                HeaderUtil.createEntityCreationAlert("myApp", false, "actor", "99");

        assertThat(headers.getFirst("X-myApp-alert"))
                .isEqualTo("A new actor is created with identifier 99");
    }

    @Test
    @DisplayName("createEntityUpdateAlert with translation uses key-based message")
    void createEntityUpdateAlertWithTranslation() {
        HttpHeaders headers =
                HeaderUtil.createEntityUpdateAlert("myApp", true, "film", "5");

        assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("myApp.film.updated");
    }

    @Test
    @DisplayName("createEntityUpdateAlert without translation uses human-readable message")
    void createEntityUpdateAlertWithoutTranslation() {
        HttpHeaders headers =
                HeaderUtil.createEntityUpdateAlert("myApp", false, "film", "5");

        assertThat(headers.getFirst("X-myApp-alert"))
                .isEqualTo("A film is updated with identifier 5");
    }

    @Test
    @DisplayName("createEntityDeletionAlert with translation uses key-based message")
    void createEntityDeletionAlertWithTranslation() {
        HttpHeaders headers =
                HeaderUtil.createEntityDeletionAlert("myApp", true, "language", "3");

        assertThat(headers.getFirst("X-myApp-alert")).isEqualTo("myApp.language.deleted");
    }

    @Test
    @DisplayName("createEntityDeletionAlert without translation uses human-readable message")
    void createEntityDeletionAlertWithoutTranslation() {
        HttpHeaders headers =
                HeaderUtil.createEntityDeletionAlert("myApp", false, "language", "3");

        assertThat(headers.getFirst("X-myApp-alert"))
                .isEqualTo("A language is deleted with identifier 3");
    }

    @Test
    @DisplayName("createFailureAlert with translation uses error key as message")
    void createFailureAlertWithTranslation() {
        HttpHeaders headers =
                HeaderUtil.createFailureAlert("myApp", true, "actor", "actor.duplicate", "Actor exists");

        assertThat(headers.getFirst("X-myApp-error")).isEqualTo("error.actor.duplicate");
        assertThat(headers.getFirst("X-myApp-params")).isEqualTo("actor");
    }

    @Test
    @DisplayName("createFailureAlert without translation uses defaultMessage")
    void createFailureAlertWithoutTranslation() {
        HttpHeaders headers =
                HeaderUtil.createFailureAlert("myApp", false, "actor", "actor.duplicate", "Actor exists");

        assertThat(headers.getFirst("X-myApp-error")).isEqualTo("Actor exists");
    }

    @Test
    @DisplayName("createEntityAlert URL-encodes param with special characters")
    void createEntityAlertEncodesSpecialCharsInParam() {
        HttpHeaders headers = HeaderUtil.createEntityAlert("app", "msg", "value with spaces");

        assertThat(headers.getFirst("X-app-params")).isEqualTo("value+with+spaces");
    }
}
