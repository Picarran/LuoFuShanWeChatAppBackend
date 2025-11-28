package com.example.luofushan.dto.req;

import lombok.Data;

@Data
public class NearbyResourceReq {

    private String type;  // 资源类型：景点/住宿/餐饮/商家

    private Double latitude;

    private Double longitude;

    private Integer page;

    private Integer size;

    private String sortBy;  // 排序方式：distance（按距离）、hot（按热度），默认 distance

    public void initDefault() {
        if (this.page == null || this.page < 1) {
            this.page = 1;
        }
        if (this.size == null || this.size < 1) {
            this.size = 10;
        }
        if (this.sortBy == null || (!"distance".equals(this.sortBy) && !"hot".equals(this.sortBy))) {
            this.sortBy = "distance";
        }
    }
}
