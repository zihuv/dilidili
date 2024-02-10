package com.zihuv.dilidili.listener.canal.core;

import com.zihuv.dilidili.common.enums.CanalStrategyEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CanalStrategyContext implements ApplicationContextAware {

    private static Map<String, ICanalService> canalServiceMap;

    public static ICanalService<?> getCanalService(String tableName) {
        String className = CanalStrategyEnum.getClassNameByTableName(tableName);
        System.out.println("className：" + className);
        return canalServiceMap.get(className);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        canalServiceMap = applicationContext.getBeansOfType(ICanalService.class);
        System.out.println("map：" + canalServiceMap);
    }
}