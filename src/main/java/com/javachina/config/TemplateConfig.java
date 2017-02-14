package com.javachina.config;

import com.blade.annotation.Order;
import com.blade.config.BaseConfig;
import com.blade.config.Configuration;
import com.blade.ioc.annotation.Component;
import com.blade.mvc.view.ViewSettings;
import com.blade.mvc.view.template.JetbrickTemplateEngine;
import com.javachina.Constant;
import com.javachina.ext.Funcs;
import com.javachina.ext.Methods;
import jetbrick.template.JetGlobalContext;
import jetbrick.template.resolver.GlobalResolver;

/**
 * Created by biezhi on 2016/12/25.
 */
@Component
@Order(sort = 2)
public class TemplateConfig implements BaseConfig {

    @Override
    public void config(Configuration configuration) {
        JetbrickTemplateEngine templateEngine = new JetbrickTemplateEngine();
        JetGlobalContext context = templateEngine.getGlobalContext();
        GlobalResolver resolver = templateEngine.getGlobalResolver();
        resolver.registerFunctions(Funcs.class);
        resolver.registerMethods(Methods.class);
        Constant.VIEW_CONTEXT = context;

        Constant.VIEW_CONTEXT.set("cdn_url", Constant.CDN_URL);
        Constant.VIEW_CONTEXT.set("cdn", Constant.CDN_URL);
        ViewSettings.$().templateEngine(templateEngine);
    }
}