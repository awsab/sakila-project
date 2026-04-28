package com.me.learning.framework.web.errors;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ProblemDetail;

public class ProblemDetailWithReason extends ProblemDetail {

    private static final long serialVersionUID = 1L;

    private ProblemDetailWithReason causeDetail;

    /* default */
    ProblemDetailWithReason(int status) {
        super(status);
    }

    /* default */
    ProblemDetailWithReason(int status, ProblemDetailWithReason causeDetail) {
        super(status);
        this.causeDetail = causeDetail;
    }

    public ProblemDetailWithReason getReason() {
        return causeDetail;
    }

    public void setReason(ProblemDetailWithReason causeDetail) {
         this.causeDetail = causeDetail;
    }

    public static class ProblemDetailWithReasonBuilder {

        private URI type;
        private String title;
        private int status;
        private String detail;
        private URI instanceUri;
        private Map<String, Object> properties = new HashMap<>();
        private ProblemDetailWithReason reason;

        public static ProblemDetailWithReasonBuilder instance() {
            return new ProblemDetailWithReasonBuilder();
        }

        public ProblemDetailWithReasonBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public ProblemDetailWithReasonBuilder withType(URI type) {
            this.type = type;
            return this;
        }

        public ProblemDetailWithReasonBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public ProblemDetailWithReasonBuilder withDetail(String detail) {
            this.detail = detail;
            return this;
        }

        public ProblemDetailWithReasonBuilder withInstance(URI instance) {
            this.instanceUri = instance;
            return this;
        }

        public ProblemDetailWithReasonBuilder withCause(ProblemDetailWithReason cause) {
            this.reason = cause;
            return this;
        }

        public ProblemDetailWithReasonBuilder withProperties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public ProblemDetailWithReasonBuilder withProperty(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }

        public ProblemDetailWithReason build() {
            ProblemDetailWithReason result = new ProblemDetailWithReason(this.status);
            result.setType(this.type);
            result.setTitle(this.title);
            result.setDetail(this.detail);
            result.setInstance(this.instanceUri);
            this.properties.forEach(result::setProperty);
            result.setReason(this.reason);
            return result;
        }

    }
}
