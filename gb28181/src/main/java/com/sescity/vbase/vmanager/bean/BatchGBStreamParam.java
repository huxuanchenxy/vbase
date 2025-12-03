package com.sescity.vbase.vmanager.bean;

import com.sescity.vbase.gb28181.bean.GbStream;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "多个推流信息")
public class BatchGBStreamParam {
    @Schema(description = "推流信息列表")
    private List<GbStream> gbStreams;

    public List<GbStream> getGbStreams() {
        return gbStreams;
    }

    public void setGbStreams(List<GbStream> gbStreams) {
        this.gbStreams = gbStreams;
    }
}
