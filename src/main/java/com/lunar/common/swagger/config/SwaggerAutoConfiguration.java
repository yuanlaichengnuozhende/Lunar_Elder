package com.lunar.common.swagger.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * swagger.enabled = true才启用。没有或不为"true"不起用
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@EnableAutoConfiguration
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true", matchIfMissing = false)
public class SwaggerAutoConfiguration implements WebMvcConfigurer {

    /**
     * 默认扫描的包
     */
    private static final String BASE_PACKAGE = "com.carbonstop";

    /**
     * 默认的排除路径，排除Spring Boot默认的错误处理路径和端点
     */
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    private static final String BASE_PATH = "/**";

    @Bean
    @ConditionalOnMissingBean
    public SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
    }

    @Bean
    public Docket api(SwaggerProperties swaggerProperties) {
        // package处理 可以屏蔽basic-error-controller、operation-handler、web-mvc-links-handler
        if (StringUtils.isBlank(swaggerProperties.getBasePackage())) {
            swaggerProperties.setBasePackage(BASE_PACKAGE);
        }

        // base-path处理
        if (CollectionUtils.isEmpty(swaggerProperties.getBasePath())) {
            swaggerProperties.getBasePath().add(BASE_PATH);
        }
        // noinspection unchecked
        List<Predicate<String>> basePath = Lists.newArrayList();
        swaggerProperties.getBasePath().forEach(path -> basePath.add(PathSelectors.ant(path)));

        // exclude-path处理
        if (swaggerProperties.getExcludePath().isEmpty()) {
            swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
        }

//        List<Predicate<String>> excludePath = new ArrayList<>();
//        swaggerProperties.getExcludePath().forEach(path -> excludePath.add(PathSelectors.ant(path)));

        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2).host(swaggerProperties.getHost())
            .apiInfo(apiInfo(swaggerProperties)).select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()));

        swaggerProperties.getBasePath().forEach(p -> builder.paths(PathSelectors.ant(p)));
        swaggerProperties.getExcludePath().forEach(p -> builder.paths(PathSelectors.ant(p).negate()));

        Docket docket = builder.build()
            .securitySchemes(securitySchemes())
            .securityContexts(securityContexts())
            .pathMapping("/");
        return docket;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /** swagger-ui 地址 */
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }

    /**
     * 安全模式，这里指定token通过Authorization头请求头传递
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> apiKeyList = new ArrayList<SecurityScheme>();
        apiKeyList.add(new ApiKey("Authorization", "Authorization", "header"));
        return apiKeyList;
    }

    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
            SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
                .build());
        return securityContexts;
    }

    /**
     * 默认的全局鉴权策略
     *
     * @return
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }

    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
            .title(swaggerProperties.getTitle())
            .description(swaggerProperties.getDescription())
            .license(swaggerProperties.getLicense())
            .licenseUrl(swaggerProperties.getLicenseUrl())
            .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
            .contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(),
                                 swaggerProperties.getContact().getEmail()))
            .version(swaggerProperties.getVersion())
            .build();
    }
}
