package com.me.learning.framework.web.errors;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProblemDetailWithReason")
class ProblemDetailWithReasonTest {

    @Test
    @DisplayName("builder sets all standard fields")
    void builderSetsAllFields() {
        URI type = URI.create("https://example.com/problem/test");
        URI instance = URI.create("/api/actors/1");

        ProblemDetailWithReason detail =
                ProblemDetailWithReason.ProblemDetailWithReasonBuilder.instance()
                        .withStatus(404)
                        .withType(type)
                        .withTitle("Not Found")
                        .withDetail("Actor with id 1 was not found")
                        .withInstance(instance)
                        .withProperty("field", "id")
                        .build();

        assertThat(detail.getStatus()).isEqualTo(404);
        assertThat(detail.getType()).isEqualTo(type);
        assertThat(detail.getTitle()).isEqualTo("Not Found");
        assertThat(detail.getDetail()).isEqualTo("Actor with id 1 was not found");
        assertThat(detail.getInstance()).isEqualTo(instance);
        assertThat(detail.getProperties()).containsEntry("field", "id");
    }

    @Test
    @DisplayName("withProperties replaces all properties")
    void builderWithPropertiesMap() {
        Map<String, Object> props = Map.of("key1", "val1", "key2", 42);

        ProblemDetailWithReason detail =
                ProblemDetailWithReason.ProblemDetailWithReasonBuilder.instance()
                        .withStatus(400)
                        .withProperties(props)
                        .build();

        assertThat(detail.getProperties()).containsEntry("key1", "val1");
        assertThat(detail.getProperties()).containsEntry("key2", 42);
    }

    @Test
    @DisplayName("withCause nests a ProblemDetailWithReason as reason")
    void builderWithCauseSetsReason() {
        ProblemDetailWithReason inner =
                ProblemDetailWithReason.ProblemDetailWithReasonBuilder.instance()
                        .withStatus(400)
                        .withTitle("Inner cause")
                        .build();

        ProblemDetailWithReason outer =
                ProblemDetailWithReason.ProblemDetailWithReasonBuilder.instance()
                        .withStatus(422)
                        .withCause(inner)
                        .build();

        assertThat(outer.getReason()).isSameAs(inner);
    }

    @Test
    @DisplayName("setReason and getReason are symmetric")
    void setAndGetReasonAreSymmetric() {
        ProblemDetailWithReason detail =
                ProblemDetailWithReason.ProblemDetailWithReasonBuilder.instance()
                        .withStatus(500)
                        .build();

        ProblemDetailWithReason reason =
                ProblemDetailWithReason.ProblemDetailWithReasonBuilder.instance()
                        .withStatus(400)
                        .build();

        detail.setReason(reason);

        assertThat(detail.getReason()).isSameAs(reason);
    }
}
