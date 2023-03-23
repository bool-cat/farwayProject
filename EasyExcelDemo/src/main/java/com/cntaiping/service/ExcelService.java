package com.cntaiping.service;

import com.cntaiping.domain.QaReq;

import java.util.List;

public interface ExcelService {
    void HandleExcelQuestion(List<QaReq> cachedDataList);
}
