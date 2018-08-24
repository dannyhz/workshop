package cn.evun.sweet.core.bigdata;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.NullAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.*;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 根据配置文件动态生成Log4j2的Appender和Logger，负责数据日志的管理。
 *
 * @author xiangli
 * @since V1.1.1
 */
@Component
public class Log4j2ConfigurationInterceptor implements InitializingBean {

    /**是否启用专门的数据日志**/
    @Value("${sweet.bigdata.logging.enable:false}")
    private Boolean enabled;

    /**数据日志存放目录**/
    @Value("${sweet.bigdata.logging.fileDir:/var/log}")
    private String fileDir;

    /**数据日志文件名**/
    @Value("${sweet.bigdata.logging.fileName:sweet-data.log}")
    private String fileName;

    /**数据日志备份文件命名规则**/
    @Value("${sweet.bigdata.logging.filePattern:sweet-data-%d{yyyy-MM-dd}.log}")
    private String filePattern;

    /**多久产生一次备份文件，需要配合file pattern定义，当前以天为单位**/
    @Value("${sweet.bigdata.logging.rollover.time.interval:1}")
    private String interval;

    /**单个文件的大小上限，达到后进行rollover，暂时无作用**/
    @Value("${sweet.bigdata.logging.rollover.size:1KB}")
    private String size;

    /**日志文件的清理时间，超出期限的日志文件将被删除，S代表秒，M代表分钟，H代表小时，D代表天**/
    @Value("${sweet.bigdata.logging.clean.total.time.limit:7D}")
    private String totalTimeLimit;

    /**清理日志文件时检查的文件名规则，匹配规则并且超时的日志文件将被删除**/
    @Value("${sweet.bigdata.logging.clean.filepattern:sweet-data-*.log}")
    private String cleanFilePattern;

    private static final String LOGGER_NAME = DataCollect.class.getName();
    private static final String APPENDER_NAME = "DataRollingFile";

    public void afterPropertiesSet(){

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        Appender appender;
        if (enabled) {
            appender = createRollingFileAppender(config);
        } else {
            appender = createNullAppender();
        }

        appender.start();
        config.addAppender(appender);

        AppenderRef ref = AppenderRef.createAppenderRef(APPENDER_NAME, null, null);
        AppenderRef[] refs = new AppenderRef[] {ref};

        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.INFO, LOGGER_NAME, "true", refs, null, config, null);

        loggerConfig.addAppender(appender, null, null);

        config.addLogger(LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();

    }

    private Appender createNullAppender() {
        return NullAppender.createAppender(APPENDER_NAME);
    }

    private Appender createRollingFileAppender(Configuration config) {
        PatternLayout layout = PatternLayout.createDefaultLayout(config);
        TimeBasedTriggeringPolicy timePolicy = TimeBasedTriggeringPolicy.createPolicy(this.interval, "true");

        if (! totalTimeLimit.startsWith("P")) {
            totalTimeLimit = "P" + totalTimeLimit;
        }

        Action[] actions = createRollingActions(config);

        DefaultRolloverStrategy strategy = DefaultRolloverStrategy.createStrategy(
                "10", "0", "3", null, actions, true, config);

        RollingFileManager fileManager =RollingFileManager.getFileManager(fileDir+"/"+fileName, fileDir+"/"+filePattern, false, false, timePolicy, strategy, null, layout, 128, false, false, config);
        timePolicy.initialize(fileManager);

        RollingFileAppender appender = RollingFileAppender.newBuilder().withAdvertise(false)
                .withAdvertiseUri(null)
                .withAppend(true)
                .withConfiguration(config)
                .withFileName(fileDir+"/"+fileName)
                .withFilePattern(fileDir+"/"+filePattern)
                .withPolicy(timePolicy)
                .withStrategy(strategy)
                .withCreateOnDemand(false)
                .withLocking(false)
                .withName(APPENDER_NAME)
                .withIgnoreExceptions(true)
                .withLayout(layout)
                .withBufferedIo(true).withBufferSize(8192)
                .withFilter(null)
                .withImmediateFlush(true)
                .build();

        return appender;
    }

    private Action[] createRollingActions(Configuration config) {
        PathCondition lastModified = IfLastModified.createAgeCondition(Duration.parse(totalTimeLimit));
        PathCondition fileNameMatch = IfFileName.createNameCondition(cleanFilePattern, null);
        PathCondition[] conditionss = new PathCondition[] {fileNameMatch, lastModified};

        DeleteAction action = DeleteAction.createDeleteAction(fileDir, false, 1, false, null, conditionss, null, config);
        return new Action[]{action};
    }
}
