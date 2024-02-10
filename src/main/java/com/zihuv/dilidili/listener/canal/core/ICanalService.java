package com.zihuv.dilidili.listener.canal.core;

import java.util.Map;

public interface ICanalService<T> {

    T deserialize(Map<String, Object> map);

    void insert(Map<String, Object> map);

    void update(Map<String, Object> map);

    void remove(Map<String, Object> map);
}
