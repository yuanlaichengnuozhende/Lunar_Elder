package com.lunar.common.security.resolver;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * [ON-DEMAND]
 *
 * @param <T>
 */
@Deprecated
public class JsonView<T> extends AbstractView {

    private T result;
    private String contentType;

    public JsonView(T result, String contentType) {
        this.result = result;
        this.contentType = contentType;
    }

    public JsonView(T result) {
        this(result, null);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        new JsonWriter().write(response, result, contentType);
    }

    public T getResult() {
        return result;
    }
}
