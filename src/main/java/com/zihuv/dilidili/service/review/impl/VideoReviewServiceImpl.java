package com.zihuv.dilidili.service.review.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.StringMap;
import com.zihuv.dilidili.config.QiNiuConfig;
import com.zihuv.dilidili.service.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VideoReviewServiceImpl implements ReviewService {

    @Autowired
    private QiNiuConfig qiNiuConfig;

    static String videoUrl = "http://ai.qiniuapi.com/v3/video/censor";
    static String videoBody = """
            {
                "data": {
                    "uri": "${url}",
                    "id": "video_censor_test"
                },
                "params": {
                    "scenes": [
                        "pulp",
                        "terror",
                        "politician"
                    ],
                    "cut_param": {
                        "interval_msecs": 5000
                    }
                }
            }""";

    static final String contentType = "application/json";
    static ObjectMapper objectMapper = new ObjectMapper();

    public boolean review(String url) {
        String body = videoBody.replace("${url}", url);
        String method = "POST";
        // 获取token
        final String token = qiNiuConfig.getToken(videoUrl, method, body, contentType);
        StringMap header = new StringMap();
        header.put("Host", "ai.qiniuapi.com");
        header.put("Authorization", token);
        header.put("Content-Type", contentType);
        Configuration cfg = new Configuration(Region.region2());
        final Client client = new Client(cfg);
        try {
            Response response = client.post(videoUrl, body.getBytes(), header, contentType);
            final Map map = objectMapper.readValue(response.getInfo().split(" \n")[2], Map.class);
            final Object job = map.get("job");
            url = "http://ai.qiniuapi.com/v3/jobs/video/" + job.toString();
            method = "GET";
            header = new StringMap();
            header.put("Host", "ai.qiniuapi.com");
            header.put("Authorization", qiNiuConfig.getToken(url, method, null, null));

            Response response1 = client.get(url, header);
            System.out.println(response1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("复核视频");
        return true;
    }
}