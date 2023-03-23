package com.cntaiping.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import com.cntaiping.domain.QaReq;

import com.cntaiping.service.ExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QaReadListener implements ReadListener<QaReq> {
    private static final Logger logger = LoggerFactory.getLogger(QaReadListener.class);
    /**
     * 定义list大小缓存excel中的数据，每BATCH_COUNT条进行一次clear，防止oom
     */
    private ExcelService excelService;

    private static final int BATCH_COUNT = 1000;

    private List<QaReq> cachedDataList = new ArrayList<>(BATCH_COUNT);

    public QaReadListener(){

    }
    public QaReadListener(ExcelService excelService){
        this.excelService=excelService;
    }
    @Override
    public void onException(Exception exception, AnalysisContext analysisContext) throws Exception {
        logger.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            logger.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
        }
    }


    @Override
    public void invokeHead(Map<Integer, CellData> map, AnalysisContext analysisContext) {
        

    }

    @Override
    public void invoke(QaReq qaReq, AnalysisContext analysisContext) {
        logger.info("[{}]解析到一条数据:QA{}",qaReq.getCaseId(),JSON.toJSONString(qaReq));
        cachedDataList.add(qaReq);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            excelService.HandleExcelQuestion(cachedDataList);
            // 存储完成清理 list
            cachedDataList = new ArrayList(BATCH_COUNT);
        }
    }

    @Override
    public void extra(CellExtra cellExtra, AnalysisContext analysisContext) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        excelService.HandleExcelQuestion( cachedDataList);
        logger.info("所有数据解析完成！");
    }

    @Override
    public boolean hasNext(AnalysisContext analysisContext) {
        return true;
    }
}
