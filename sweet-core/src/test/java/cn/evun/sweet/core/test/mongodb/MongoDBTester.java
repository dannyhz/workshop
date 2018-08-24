package cn.evun.sweet.core.test.mongodb;

import cn.evun.sweet.core.mongodb.log4j2.MongoDBLogDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Administrator on 2017/2/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:beans-test.xml"})
public class MongoDBTester {

    @Autowired
    TestDoMongoDBDao testDoMongoDBDao;
    @Autowired
    Log4j2DAO log4j2DAO;
    @Autowired
    FastpayMongoDBLogDao fastpayMongoDBLogDao;

    @Before
    public void setUp() {
    }

    @Test
    public void testMongoDB1() {
        TestDO testDO = new TestDO(22, "cctv", "usa");
        testDoMongoDBDao.save(testDO);
        TestDO testDO1 = testDoMongoDBDao.findUniqueBy("addr", "usa");
        System.out.println(testDO1);
    }

    @Test
    public void testMongoDBLog() {
//        String id = "58c12c6ce81f90d5b863defd";
//        Log4j2 log4j2 = log4j2DAO.findById(id);
//        System.out.println(log4j2);

        MongoDBLogDTO queryDto = new MongoDBLogDTO();
//        queryDto.setLevel("WARN");
//        queryDto.setStartTime("2017-03-23 09:00:00");
//        queryDto.setEndTime("2017-03-23 18:00:00");
//        List<Log4j2> list = log4j2DAO.queryAll(queryDto);
//        for (Log4j2 log4j21 : list) {
//            System.out.println(log4j21);
//        }

        queryDto.setLevel("ERROR");
        queryDto.setPageSize(100);
        List<Log4j2> list = log4j2DAO.queryByPage(queryDto);
        for (Log4j2 log4j21 : list) {
            System.out.println(log4j21);
        }
        System.out.println();
        queryDto.setPageNum(2);
        list = log4j2DAO.queryByPage(queryDto);
        for (Log4j2 log4j21 : list) {
            System.out.println(log4j21);
        }
    }

    @Test
    public void testMongoDBLog2() {
        Query query = new Query();
        query.addCriteria(Criteria.where("level").is("WARN"));
//        query.addCriteria(Criteria.where("threadId").gte(1).lte(12));
//        query.addCriteria(Criteria.where("threadPriority").is(new Integer(5)));
        query.addCriteria(Criteria.where("millis").gte(1389055255723l).lte(1589055255723l));
        List<Log4j2> list = log4j2DAO.find(query);
        for (Log4j2 log4j21 : list) {
            System.out.println(log4j21);
        }
    }

    @Test
    public void testFastpayMongoDBLogDao() {
        MongoDBLogDTO query = new MongoDBLogDTO();
        query.setLevel("ERROR");
        List<FastpayMongoDBLogDO> list = fastpayMongoDBLogDao.queryAll(query);
        for (FastpayMongoDBLogDO fastpay : list) {
            System.out.println(fastpay);
        }
    }

}
