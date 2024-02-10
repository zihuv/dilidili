package com.zihuv.dilidili.listener.canal.core;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.zihuv.dilidili.config.CanalConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CanalListener {

    @Autowired
    private CanalConfig config;

    @PostConstruct
    public void listenCancel() {
        new Thread(() -> {
            CanalConnector connect = CanalConnectors.newSingleConnector(
                    new InetSocketAddress(config.getHostname(), config.getPort()),
                    config.getDestination(), config.getUsername(), config.getPassword());
            int bachChSize = 1000;
            //2.建立连接
            connect.connect();
            //回滚上次请求的信息放置防止数据丢失
            connect.rollback();
            // 订阅匹配日志
            connect.subscribe();
            while (true) {
                Message message = connect.getWithoutAck(bachChSize);
                // 获取batchId
                long batchId = message.getId();
                // 获取binlog数据的条数
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {

                } else {
                    printSummary(message);
                }
                // 确认指定的batchId已经消费成功
                connect.ack(batchId);
            }
        }).start();
    }

    private void printSummary(Message message) {

        // 遍历整个batch中的每个binlog实体
        for (CanalEntry.Entry entry : message.getEntries()) {
            // 事务开始
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            // 获取binlog文件名
            String logfileName = entry.getHeader().getLogfileName();
            // 获取logfile的偏移量
            long logfileOffset = entry.getHeader().getLogfileOffset();
            // 获取sql语句执行时间戳
            long executeTime = entry.getHeader().getExecuteTime();
            // 获取数据库名
            String schemaName = entry.getHeader().getSchemaName();
            // 获取表名
            String tableName = entry.getHeader().getTableName();
            // 获取事件类型 insert/update/delete
            String eventTypeName = entry.getHeader().getEventType().toString().toLowerCase();

            System.out.println("logfileName" + ":" + logfileName);
            System.out.println("logfileOffset" + ":" + logfileOffset);
            System.out.println("executeTime" + ":" + executeTime);
            System.out.println("schemaName" + ":" + schemaName);
            System.out.println("tableName" + ":" + tableName);
            System.out.println("eventTypeName" + ":" + eventTypeName);

            CanalEntry.RowChange rowChange = null;
            try {
                // 获取存储数据，并将二进制字节数据解析为RowChange实体
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            ICanalService<?> canalService = CanalStrategyContext.getCanalService(tableName);
            // 迭代每一条变更数据
            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                Map<String, Object> columnMap = new HashMap<>();
                // 判断是否为删除事件
                if (entry.getHeader().getEventType() == CanalEntry.EventType.DELETE) {
                    columnMap = getColumnMap(rowData.getBeforeColumnsList());
                    canalService.remove(columnMap);

                }
                // 判断是否为更新事件
                else if (entry.getHeader().getEventType() == CanalEntry.EventType.UPDATE) {
                    columnMap = getColumnMap(rowData.getAfterColumnsList());
                    canalService.update(columnMap);
                }
                // 判断是否为插入事件
                else if (entry.getHeader().getEventType() == CanalEntry.EventType.INSERT) {
                    columnMap = getColumnMap(rowData.getAfterColumnsList());
                    canalService.insert(columnMap);
                }
                log.info("[Canal] 操作：{} 执行完毕，INFO：{}", entry.getHeader().getEventType(), columnMap);
            }
        }
    }

    // 打印所有列名和列值
    private Map<String, Object> getColumnMap(List<CanalEntry.Column> columnList) {
        Map<String, Object> map = new HashMap<>();
        for (CanalEntry.Column column : columnList) {
            map.put(column.getName(), column.getValue());
        }
        return map;
    }
}