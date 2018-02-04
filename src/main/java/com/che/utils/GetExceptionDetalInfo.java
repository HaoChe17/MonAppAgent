package com.che.utils;
/**
 * 获取异常的详细信息
 * @author chehao
 * @version 2017年10月7日 上午11:12:01
 */
public class GetExceptionDetalInfo {
    public String getExceptionDetail(Exception e) {
        StringBuffer stringBuffer = new StringBuffer(e.toString() + "\n");
        StackTraceElement[] messages = e.getStackTrace();
        int length = messages.length;
        for (int i = 0; i < length; i++) {
            stringBuffer.append("\t"+messages[i].toString()+"\n");
        }
        return stringBuffer.toString();
    }
}
