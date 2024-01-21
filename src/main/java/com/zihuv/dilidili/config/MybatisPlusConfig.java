package com.zihuv.dilidili.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Configuration
public class MybatisPlusConfig {

    /**
     * 元数据填充
     */
    @Bean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    /**
     * 自定义雪花算法 ID 生成器
     */
    @Bean
    @Primary
    public IdentifierGenerator idGenerator() {
        return new CustomIdGenerator();
    }

    public static class CustomIdGenerator implements IdentifierGenerator {

        @Override
        public Number nextId(Object entity) {
            Snowflake snowflake = IdUtil.getSnowflake();
            return snowflake.nextId();
        }
    }

    /**
     * 元数据处理器
     */
    public static class MyMetaObjectHandler implements MetaObjectHandler {

        /**
         * 数据新增时填充
         *
         * @param metaObject 元数据
         */
        @Override
        public void insertFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
        }

        /**
         * 数据修改时填充
         *
         * @param metaObject 元数据
         */
        @Override
        public void updateFill(MetaObject metaObject) {
            this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        }
    }
}