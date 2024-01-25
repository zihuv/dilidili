package com.zihuv.dilidili.schedul;

import com.zihuv.dilidili.model.vo.HotVideoVO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

@Slf4j
public class TopVideoQueue {

    private int maxSize = 0;

    private final Queue<HotVideoVO> queue;

    public TopVideoQueue(int maxSize, Queue<HotVideoVO> queue) {
        this.maxSize = maxSize;
        this.queue = queue;
    }

    public void add(HotVideoVO hotVideo) {
        if (hotVideo == null) {
            log.warn("[热度排行榜] 添加的视频不能为 null");
            return;
        }

        if (queue.size() < maxSize) {
            queue.add(hotVideo);
        } else if (queue.peek() != null && queue.peek().getHotness() < hotVideo.getHotness()) {
            queue.poll();
            queue.add(hotVideo);
        }
    }

    public List<HotVideoVO> get() {
        final ArrayList<HotVideoVO> list = new ArrayList<>(queue.size());
        while (!queue.isEmpty()) {
            list.add(queue.poll());
        }
        Collections.reverse(list);
        return list;
    }

}