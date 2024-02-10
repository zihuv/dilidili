package com.zihuv.dilidili.common.enums;

import cn.hutool.core.util.StrUtil;
import com.zihuv.dilidili.exception.ServiceException;
import com.zihuv.dilidili.listener.canal.core.ICanalService;
import com.zihuv.dilidili.listener.canal.VideoCanalServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CanalStrategyEnum {

    VIDEO("tb_video", getNameByClass(VideoCanalServiceImpl.class)),

    USER("tb_user", null);

    /**
     * 数据库表名
     */
    private final String tableName;

    /**
     * 接口 {@link ICanalService} 实现类的名称（类名首字母需要小写）
     */
    private final String className;

    private static <T> String getNameByClass(Class<T> tClass) {
        return StrUtil.lowerFirst(tClass.getSimpleName());
    }

    public static String getClassNameByTableName(String tableName) {
        for (CanalStrategyEnum strategy : CanalStrategyEnum.values()) {
            if (strategy.getTableName().equals(tableName)) {
                return strategy.getClassName();
            }
        }
        throw new ServiceException(StrUtil.format("[Canal-策略枚举] 不存在 tableName:[{}] 在 Canal 策略枚举中", tableName));
    }
}
