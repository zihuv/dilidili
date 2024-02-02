package com.zihuv.dilidili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zihuv.dilidili.mq.core.LocalMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<LocalMessage> {
}
