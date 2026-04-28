/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 23/07/2025
 * Usage    : Well-known Spring profile name constants shared across all Sakila microservices
 * Since    : Version 1.0
 */
package com.me.learning.framework.config;

/**
 * Canonical Spring profile name constants used across all Sakila microservices.
 *
 * <p>Services reference these constants when activating profiles programmatically
 * (e.g., via {@link DefaultProfileConfig#addDefaultProfile}) to avoid hard-coding
 * string literals.
 */
public final class SakilaProfileConstants {

    private SakilaProfileConstants() {
    }

    /** Local developer workstation profile. */
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    /** System Integration Test environment. */
    public static final String SPRING_PROFILE_SIT = "sit";

    /** User Acceptance Test environment. */
    public static final String SPRING_PROFILE_UAT = "uat";

    /** End-to-end test environment. */
    public static final String SPRING_PROFILE_E2E = "e2e";

    /** API documentation / OpenAPI UI profile. */
    public static final String SPRING_PROFILE_API_DOCS = "api-docs";

    /** Production environment. */
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    /** Kubernetes-specific overrides profile. */
    public static final String SPRING_PROFILE_K8S = "k8s";
}

