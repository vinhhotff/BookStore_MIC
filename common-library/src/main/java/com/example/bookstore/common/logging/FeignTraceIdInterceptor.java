package com.example.bookstore.common.logging;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class FeignTraceIdInterceptor implements RequestInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void apply(RequestTemplate template) {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId != null) {
            template.header(TRACE_ID_HEADER, traceId);
        }
    }
}
