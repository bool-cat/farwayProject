package com.cntaiping.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import lombok.Data;
import org.apache.poi.ss.usermodel.FillPatternType;

@HeadStyle(fillPatternType=FillPatternType.SOLID_FOREGROUND,fillForegroundColor = 14)
@HeadFontStyle(fontHeightInPoints = 11)
@Data
public class QaResp {
    @ExcelProperty("case_id")
    private String caseId;
    @ExcelProperty("interface")
    private String interfaceName;
    @ExcelProperty("url")
    private String url;
    @ExcelProperty("qa_status")
    private String httpStatus;
    @ExcelProperty("title")
    private String title;
    @ExcelProperty("request_param")
    private String paramData;
    @ExcelProperty("expected")
    private String answer;
    @ExcelProperty("response_param")
    private String response;
    @ExcelProperty("is_same")
    private Boolean isSame;
}
