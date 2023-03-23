package com.cntaiping.domain;

import lombok.Data;

@Data
public class HttpJsonParam {
    private String channel;
    private String data;
    private String userId;
    private String agentOrganId;
    private String category;
    private String agentChannel;
    private String organCode;
}
