package com.javachina.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.blade.Blade;
import com.blade.annotation.Order;
import com.blade.config.BaseConfig;
import com.blade.config.Configuration;
import com.blade.ioc.annotation.Component;
import com.blade.jdbc.ActiveRecord;
import com.blade.jdbc.ar.SampleActiveRecord;
import com.blade.kit.base.Config;
import com.javachina.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by biezhi on 2016/12/25.
 */
@Component
@Order(sort = 1)
public class DBConfig implements BaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConfig.class);

    public ActiveRecord activeRecord;

    @Override
    public void config(Configuration configuration) {
        try {
            InputStream in = DBConfig.class.getClassLoader().getResourceAsStream("druid.properties");
            Properties props = new Properties();
            props.load(in);
            DataSource dataSource = DruidDataSourceFactory.createDataSource(props);
            activeRecord = new SampleActiveRecord(dataSource);
            Blade.$().ioc().addBean(activeRecord);

            Config config = Blade.$().config();
            Constant.config = config;

            Constant.SITE_URL = config.get("app.site_url");
            Constant.CDN_URL = config.get("app.cdn_url");
            Constant.AES_SALT = config.get("app.aes_salt", "0123456789abcdef");
            Constant.UPLOAD_DIR = config.get("app.upload_dir");

            /**
             * github密钥配置
             */
            Constant.GITHUB_CLIENT_ID = config.get("github.client_id");
            Constant.GITHUB_CLIENT_SECRET = config.get("github.client_secret");
            Constant.GITHUB_REDIRECT_URL = config.get("github.redirect_url");

            /**
             * 邮件配置
             */
            Constant.MAIL_HOST = config.get("mail.smtp.host");
            Constant.MAIL_USER = config.get("mail.user");
            Constant.MAIL_USERNAME = config.get("mail.from");
            Constant.MAIL_PASS = config.get("mail.pass");
        } catch (Exception ex) {
            LOGGER.error("初始化数据库配置失败", ex);
        }
    }
}
