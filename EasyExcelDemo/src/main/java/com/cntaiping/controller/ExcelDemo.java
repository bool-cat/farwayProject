package com.cntaiping.controller;

import com.alibaba.excel.EasyExcel;
import com.cntaiping.domain.QaReq;
import com.cntaiping.listener.QaReadListener;
import com.cntaiping.service.impl.ExcelServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelDemo {
    private static final Logger logger = LoggerFactory.getLogger(ExcelDemo.class);
    public static void main(String[] args) {
        String fileName = "F:\\YHHL_api.xlsx";
        logger.info("fileName[{}]",fileName);
        EasyExcel.read(fileName, QaReq.class, new QaReadListener(new ExcelServiceImpl())).sheet("yytt").doRead();
    }
}
