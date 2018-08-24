package cn.evun.sweet.core.mongodb.log4j2;

import cn.evun.sweet.common.util.DateUtils;
import cn.evun.sweet.common.util.StringUtils;
import cn.evun.sweet.core.mongodb.MongoDBBaseDao;
import cn.evun.sweet.core.mybatis.page.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

/**
 * Log4j2输出到MongoDB的日志对象操作基类<br/>
 * Created by shentao on 2017/3/23.
 */
public class MongoDBLogDAO<T extends MongoDBLogDO> extends MongoDBBaseDao<T> {

    /**
     * 不分页查询
     *
     * @param queryDto
     * @return
     */
    public List<T> queryAll(MongoDBLogDTO queryDto) {
        Query query = new Query();
        if (StringUtils.hasText(queryDto.getLevel())) {
            query.addCriteria(Criteria.where("level").is(queryDto.getLevel()));
        }
        if (StringUtils.hasText(queryDto.getLoggerName())) {
            query.addCriteria(Criteria.where("loggerName").regex(queryDto.getLoggerName(), "i"));
        }
        if (StringUtils.hasText(queryDto.getMessage())) {
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("message").regex(queryDto.getMessage(), "i"),
                    Criteria.where("thrown.type").regex(queryDto.getMessage(), "i"),
                    Criteria.where("thrown.message").regex(queryDto.getMessage(), "i"));
            query.addCriteria(criteria);
        }
        if (StringUtils.hasText(queryDto.getMarker())) {
            query.addCriteria(Criteria.where("marker.name").is(queryDto.getMarker()));
        }
        String startTime = queryDto.getStartTime();
        String endTime = queryDto.getEndTime();
        if (StringUtils.hasText(startTime)) {
            Criteria criteria = Criteria.where("millis").gte(DateUtils.getFormatedDate(startTime, DateUtils.LONG_DATE_PATTERN).getTime());
            if (StringUtils.hasText(endTime)) {
                criteria.lte(DateUtils.getFormatedDate(endTime, DateUtils.LONG_DATE_PATTERN).getTime());
            } else {
                criteria.lte(new Date().getTime());
            }
            query.addCriteria(criteria);
        } else if (StringUtils.hasText(endTime)) {
            Criteria criteria = Criteria.where("millis").gte(0);
            criteria.lte(DateUtils.getFormatedDate(endTime, DateUtils.LONG_DATE_PATTERN).getTime());
            query.addCriteria(criteria);
        }

        return find(query);
    }

    /**
     * 分页查询
     *
     * @param queryDto
     * @return
     */
    public Page<T> queryByPage(MongoDBLogDTO queryDto) {
        Query query = new Query();
        if (StringUtils.hasText(queryDto.getLevel())) {
            query.addCriteria(Criteria.where("level").is(queryDto.getLevel()));
        }
        if (StringUtils.hasText(queryDto.getLoggerName())) {
            query.addCriteria(Criteria.where("loggerName").regex(queryDto.getLoggerName(), "i"));
        }
        if (StringUtils.hasText(queryDto.getMessage())) {
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where("message").regex(queryDto.getMessage(), "i"),
                    Criteria.where("thrown.type").regex(queryDto.getMessage(), "i"),
                    Criteria.where("thrown.message").regex(queryDto.getMessage(), "i"));
            query.addCriteria(criteria);
        }
        if (StringUtils.hasText(queryDto.getMarker())) {
            query.addCriteria(Criteria.where("marker.name").is(queryDto.getMarker()));
        }
        String startTime = queryDto.getStartTime();
        String endTime = queryDto.getEndTime();
        if (StringUtils.hasText(startTime)) {
            Criteria criteria = Criteria.where("millis").gte(DateUtils.getFormatedDate(startTime, DateUtils.LONG_DATE_PATTERN).getTime());
            if (StringUtils.hasText(endTime)) {
                criteria.lte(DateUtils.getFormatedDate(endTime, DateUtils.LONG_DATE_PATTERN).getTime());
            } else {
                criteria.lte(new Date().getTime());
            }
            query.addCriteria(criteria);
        } else if (StringUtils.hasText(endTime)) {
            Criteria criteria = Criteria.where("millis").gte(0);
            criteria.lte(DateUtils.getFormatedDate(endTime, DateUtils.LONG_DATE_PATTERN).getTime());
            query.addCriteria(criteria);
        }
        if (queryDto.getPageNum() <= 0) {
            queryDto.setPageNum(1);
        }
        if (queryDto.getPageSize() <= 0) {
            queryDto.setPageSize(10);
        }
        if (StringUtils.hasText(queryDto.getOrderBy())) {
            if (queryDto.isDesc()) {
                query.with(new Sort(Sort.Direction.DESC, queryDto.getOrderBy()));
            } else {
                query.with(new Sort(Sort.Direction.ASC, queryDto.getOrderBy()));
            }
        }

        return findPage(queryDto.getPageNum(), queryDto.getPageSize(), query);
    }

}