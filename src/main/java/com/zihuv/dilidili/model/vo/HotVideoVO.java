package com.zihuv.dilidili.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Data
public class HotVideoVO {

    private Long videoId;

    private Double hotness;

    private String hotFormat;

    public HotVideoVO(Long videoId, Double hotness) {
        this.videoId = videoId;
        this.hotness = hotness;
    }


    public void hotFormat() {
        BigDecimal bigDecimal = new BigDecimal(this.hotness);
        BigDecimal decimal = bigDecimal.divide(new BigDecimal("10000"), RoundingMode.HALF_UP);
        DecimalFormat formatter = new DecimalFormat("0.0");
        formatter.setRoundingMode(RoundingMode.HALF_UP);    // 5000008.89
        String formatNum = formatter.format(decimal);
        this.setHotFormat(formatNum + "万");
    }
}