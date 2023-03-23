package com.cntaiping.domain;

import lombok.Data;

@Data
public class QaReq {
   private String caseId;
   private String StandardQuestion;
   private String title;
   private String answer;

}
