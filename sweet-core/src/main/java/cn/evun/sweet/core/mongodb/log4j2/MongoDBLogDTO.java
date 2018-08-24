package cn.evun.sweet.core.mongodb.log4j2;

/**
 * Log4j2输出到MongoDB的日志查询参数对象<br/>
 * Created by Administrator on 2017/3/23.
 */
public class MongoDBLogDTO {

    /* 日志级别 */
    private String level;
    /* 日志名称 */
    private String loggerName;
    /* 日志信息 */
    private String message;
    /* 开始时间，格式必须为"yyyy-MM-dd HH:mm:ss" */
    private String startTime;
    /* 结束时间，格式必须为"yyyy-MM-dd HH:mm:ss" */
    private String endTime;
    /* marker名称 */
    private String marker;
    /* 页码 */
    private int pageNum = 1;
    /* 页大小 */
    private int pageSize = 10;
    /* 排序字段名 */
    private String orderBy;
    /* 是否倒序 */
    private boolean desc = true;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

}
