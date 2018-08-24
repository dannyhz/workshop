package cn.evun.sweet.core.mongodb;

import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.common.R;
import cn.evun.sweet.core.exception.SweetException;

import com.mongodb.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB基础配置
 * Created by shentao on 2017/2/23.
 */
@Configuration
public class MongoDBConfig {

    protected static final Logger logger = LogManager.getLogger();

    /* MongoDB服务器地址和端口，例如192.168.0.1:27017，多个用半角逗号分隔 */
    @Value("${mongo.hostport:}")
    private String serverAddress;

    /* 每个host允许的最大连接数 */
    @Value("${mongo.connectionsPerHost:10}")
    private int connectionsPerHost;

    /* 每个连接允许阻塞的最大线程数，超过将抛异常 */
    @Value("${mongo.threadsAllowedToBlockForConnectionMultiplier:5}")
    private int threadsAllowedToBlockForConnectionMultiplier;

    /* 连接超时的毫秒数,0表示不超时 */
    @Value("${mongo.connectTimeout:10000}")
    private int connectTimeout;

    /* 一个线程等待连接可用的最大毫秒数，0表示不等待，负数表示等待时间不确定 */
    @Value("${mongo.maxWaitTime:120000}")
    private int maxWaitTime;

    /* 连接超时是否重试，否则抛异常 */
    @Value("${mongo.autoConnectRetry:true}")
    private boolean autoConnectRetry;

    /* socket是否保持长连接 */
    @Value("${mongo.socketKeepAlive:true}")
    private boolean socketKeepAlive;

    /* socket I/O读写超时时间(毫秒), 0为不超时 */
    @Value("${mongo.socketTimeout:10000}")
    private int socketTimeout;

    /* 是否实现读写分离 */
    @Value("${mongo.slaveOk:false}")
    private boolean slaveOk;

    /* 当前连接的数据库名 */
    @Value("${mongo.database:}")
    private String database;

    /* 数据库连接认证用户名 */
    @Value("${mongo.username:}")
    private String username;

    /* 数据库连接认证用户密码 */
    @Value("${mongo.password:}")
    private String password;

    /**
     * 创建MongoDB连接客户端
     *
     * @return
     */
    @Bean(name = "mongo")
    public Mongo mongo() throws UnknownHostException {
        if (!StringUtils.hasText(serverAddress) || serverAddress.equals("localhost")) {
            logger.warn("Nothing has configed for mongoDB client, it will be ignored!");
            return null;
        }

        // 创建连接参数对象
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(connectionsPerHost)
                .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier)
                .connectTimeout(connectTimeout).maxWaitTime(maxWaitTime).socketKeepAlive(socketKeepAlive)
                .socketTimeout(socketTimeout);

        // 生成MongoDB服务器地址列表
        List<ServerAddress> seedList = new ArrayList<ServerAddress>();
        String[] addrs = serverAddress.split(",");
        for (String addr : addrs) {
            String[] serverAddr = addr.split(":");
            seedList.add(new ServerAddress(serverAddr[0], Integer.parseInt(serverAddr[1])));
        }

        List<MongoCredential> credentialsList = new ArrayList<>();
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(username, database, password.toCharArray());
        credentialsList.add(mongoCredential);

        MongoClient mongoClient = new MongoClient(seedList, credentialsList, builder.build());
        if (slaveOk) { // 读写分离
            mongoClient.addOption(Bytes.QUERYOPTION_SLAVEOK);
        }
        return mongoClient;
    }

    /**
     * 创建MongoDB客户端工具类对象
     *
     * @param mongo
     * @return
     */
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("mongo") Mongo mongo) {
        if (mongo == null) {
            return null;
        }
        if (StringUtils.isEmpty(database)) {
            throw new SweetException(R.exception.excode_default, "mongoDB database must be configed!");
        }
        return new MongoTemplate(mongo, database);
    }

}
