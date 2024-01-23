package com.zihuv.dilidili.config;

import cn.hutool.core.util.StrUtil;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class QiNiuConfig {

    /**
     * 账号
     */
    @Value("${qiniu.access-key}")
    private String accessKey;
    /**
     * 密钥
     */
    @Value("${qiniu.secret-key}")
    private String secretKey;

    /**
     * bucketName
     */
    @Value("${qiniu.bucket-name}")
    private String bucketName;

    /**
     * baseUrl
     */
    @Value("${qiniu.base-url}")
    private String baseUrl;

    public static final String CNAME = "http://s4ep712zo.hn-bkt.clouddn.com";
    public static final String VIDEO_URL = "http://ai.qiniuapi.com/v3/video/censor";
    public static final String IMAGE_URL = "http://ai.qiniuapi.com/v3/image/censor";

    public static final String fops = "avthumb/mp4";

    public Auth buildAuth() {
        String accessKey = this.getAccessKey();
        String secretKey = this.getSecretKey();
        return Auth.create(accessKey, secretKey);
    }

    public String uploadToken(String type) {
        final Auth auth = buildAuth();
        return auth.uploadToken(bucketName, null, 300, new
                StringMap().put("mimeLimit", "video/*;image/*"));
    }

    public String videoUploadToken() {
        final Auth auth = buildAuth();
        return auth.uploadToken(bucketName, null, 300, new
                StringMap().put("mimeLimit", "video/*"));
    }

    public String imageUploadToken() {
        final Auth auth = buildAuth();
        return auth.uploadToken(bucketName, null, 300, new
                StringMap().put("mimeLimit", "image/*"));
    }

    public String getDownloadUrl(String fileName) {
        return getDownloadUrl("", fileName);
    }

    public String getDownloadUrl(String filePath, String fileName) {
        if (StrUtil.isNotEmpty(filePath)) {
            filePath = processFilePath(filePath);
        }
        String url = baseUrl + filePath + fileName;
        final Auth auth = buildAuth();
        return auth.privateDownloadUrl(url);
    }

    public static String processFilePath(String filePath) {
        // 删除开头的 "/"
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        // 添加末尾的 "/"
        if (!filePath.endsWith("/")) {
            filePath = filePath + "/";
        }
        return filePath;
    }

    public String getToken(String url, String method, String body, String contentType) {
        final Auth auth = buildAuth();
        return "Qiniu " + auth.signQiniuAuthorization(url, method, body == null ? null : body.getBytes(), contentType);
    }
}