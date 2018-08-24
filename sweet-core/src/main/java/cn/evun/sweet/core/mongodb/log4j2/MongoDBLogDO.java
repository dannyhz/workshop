package cn.evun.sweet.core.mongodb.log4j2;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.mongodb.IdEntity;

import java.io.Serializable;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Log4j2输出到MongoDB的日志对象基类<br/>
 * Created by shentao on 2017/3/23.
 */
public class MongoDBLogDO extends IdEntity {

    private static final long serialVersionUID = -7945523922905121452L;

    /* 日志级别 */
    private String level;
    /* 日志名称 */
    private String loggerName;
    /* 日志信息 */
    private String message;
    /* 日志日期 */
    private Date date;
    /* unix timestamp */
    private Long millis;
    /* 线程ID */
    private Long threadId;
    /* 线程名称 */
    private String threadName;
    /* 源代码信息 */
    private Source source;
    private String marker;
    private Thrown thrown;
    private Map<String, String> contextMap;

    @Override
    public String toString() {
        return "MongoDBLogDO{" +
                "level='" + level + '\'' + ", loggerName='" + loggerName + '\'' + ", message='" + message + '\'' +
                ", date=" + date + ", millis=" + millis + ", threadId=" + threadId + ", threadName='" + threadName + '\'' +
                ", source=" + source + ", marker='" + marker + '\'' + ", thrown=" + thrown + ", contextMap=" + contextMap +
                '}';
    }

    public class Source implements Serializable {

        private static final long serialVersionUID = 1996528984178206850L;
        /* 类名 */
        private String className;
        /* 方法名 */
        private String methodName;
        /* 文件名 */
        private String fileName;
        /* 行号 */
        private Integer lineNumber;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(Integer lineNumber) {
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return "Source{className='" + className + '\'' + ", methodName='" + methodName + '\'' +
                    ", fileName='" + fileName + '\'' + ", lineNumber=" + lineNumber + '}';
        }
    }

    @SuppressWarnings("unused")
    public class Thrown implements Serializable {

        private static final long serialVersionUID = -188678542094932227L;
        private String type;
        private String message;
        private String stackTrace;
        private List<StackTrace> stackTraceList;

        public Thrown(String type, String message, String stackTrace) {
            this.type = type;
            this.message = message;
            this.stackTrace = stackTrace;
            if (StringUtils.hasText(stackTrace)) {
                JSONArray jsonArray = JSONObject.parseArray(stackTrace);
                stackTraceList = new ArrayList<StackTrace>(jsonArray.size());
                for (Object obj : jsonArray) {
                    JSONObject o = (JSONObject) obj;
                    stackTraceList.add(new StackTrace(o.getString("className"), o.getString("methodName"),
                            o.getString("fileName"), o.getString("lineNumber")));
                }
            }
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<StackTrace> getStackTraceList() {
            return stackTraceList;
        }

        public void setStackTraceList(List<StackTrace> stackTraceList) {
            this.stackTraceList = stackTraceList;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String result = "Thrown{type='" + type + '\'' + ", message='" + message + '\'' + ", stackTrace=[";
            for (StackTrace item : stackTraceList) {
                sb.append(item.toString());
            }
            sb.append("]}");
            return sb.toString();
        }
    }

    public class StackTrace implements Serializable {

        private static final long serialVersionUID = -8227682470674847529L;

        private String className;
        private String methodName;
        private String fileName;
        private String lineNumber;

        public StackTrace(String className, String methodName, String fileName, String lineNumber) {
            this.className = className;
            this.methodName = methodName;
            this.fileName = fileName;
            this.lineNumber = lineNumber;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getLineNumber() {
            return lineNumber;
        }

        @Override
        public String toString() {
            return " [className=" + className + ", methodName=" + methodName +
                    ", fileName=" + fileName + ", lineNumber=" + lineNumber + "]";
        }
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getMillis() {
        return millis;
    }

    public void setMillis(Long millis) {
        this.millis = millis;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public Thrown getThrown() {
        return thrown;
    }

    public void setThrown(Thrown thrown) {
        this.thrown = thrown;
    }

    public Map<String, String> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, String> contextMap) {
        this.contextMap = contextMap;
    }
}