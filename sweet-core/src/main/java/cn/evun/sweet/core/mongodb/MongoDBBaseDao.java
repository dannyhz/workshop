package cn.evun.sweet.core.mongodb;

import cn.evun.sweet.common.util.reflect.ReflectionUtils;
import cn.evun.sweet.core.mybatis.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * mongodb 基础操作类
 *
 * @author shentao
 */
public class MongoDBBaseDao<T extends IdEntity> {

    protected static final Logger logger = LogManager.getLogger();

    protected static final int DEFAULT_SKIP = 0;
    protected static final int DEFAULT_LIMIT = 200;

    @Autowired
    protected MongoTemplate mongoTemplate;

    public void save(T t) {
        mongoTemplate.save(t);
        if (logger.isDebugEnabled()) {
            logger.debug("mongoDB save entity: {}", t);
        }
    }

    public void insertAll(List<T> list) {
        mongoTemplate.insertAll(list);
    }

    /**
     * 删除对象
     */
    public void delete(T t) {
        mongoTemplate.remove(t);
        if (logger.isDebugEnabled()) {
            logger.debug("mongoDB delete entity: {}", t);
        }
    }

    /**
     * 根据id 删除对象
     *
     * @param id
     */
    public void deleteById(String id) {
        Criteria criteria = Criteria.where("id").is(id);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, this.getEntityClass());
        if (logger.isDebugEnabled()) {
            logger.debug("mongoDB delete entity: [id:{}]", id);
        }
    }

    /**
     * 根据条件删除
     */
    public void delete(Query query) {
        mongoTemplate.remove(query, this.getEntityClass());
    }

    /**
     * 删除该collection 的所有的数据
     */
    public void deleteAll() {
        mongoTemplate.dropCollection(this.getEntityClass());
    }

    public void update(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    public void update(String propertyName, Object value) {
        Query query = new Query();
        buildQuery(propertyName, value, query);
        Update update = Update.update(propertyName, value);
        mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    public void update(T t) throws IllegalAccessException {
        Query query = new Query();
        buildQuery("id", t.getId(), query);
        Update update = new Update();
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.equals("id") || fieldName.equals("serialVersionUID"))
                continue;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Object value = field.get(t);
            if (value == null)
                continue;
            update.set(fieldName, value);
        }
        mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    public List<T> findAll() {
        return mongoTemplate.findAll(this.getEntityClass());
    }

    /**
     * 根据查询query 查找list
     */
    public List<T> find(Query query) {
        return mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 按照字段排序 － 顺序 <br/>
     *
     * @param query      查询条件 <br/>
     * @param properties 排序字段 <br/>
     */
    public List<T> findWithOrderAsc(Query query, String... properties) {
        Sort sort = new Sort(Direction.ASC, properties);
        query.with(sort);
        return mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 按照字段排序 － 逆序 <br/>
     *
     * @param query      查询条件 <br/>
     * @param properties 排序字段 <br/>
     */
    public List<T> findWithOrderDesc(Query query, String... properties) {
        Sort sort = new Sort(Direction.DESC, properties);
        query.with(sort);
        return mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 根据查询query 查找一个对象
     *
     * @param query Query
     */
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * 根据 id 查询对象
     *
     * @param id String
     */
    public T findById(String id) {
        return mongoTemplate.findById(id, this.getEntityClass());
    }

    /**
     * 根据id 和 集合名字查询对象
     *
     * @param id             String
     * @param collectionName String
     */
    public T findById(String id, String collectionName) {
        return mongoTemplate.findById(id, this.getEntityClass(), collectionName);
    }

    /**
     * 查询分页 tips：［不要skip太多的页数，如果跳过太多会严重影响效率。最大不要skip20000页］
     *
     * @param pageNum
     * @param pageSize
     * @param query
     */
    public Page<T> findPage(int pageNum, int pageSize, Query query) {
        long count = this.count(query);
        query.skip((pageNum - 1) * pageSize).limit(pageSize);
        if (query.getSortObject() == null) { //默认按id倒序排列
            query.with(new Sort(Direction.DESC, "id"));
        }
        List<T> list = this.find(query);
        Page<T> page = new Page<T>(pageNum, pageSize);
        page.addAll(list);
        page.setTotal(count);

        return page;
    }

    public long count(Query query) {
        return mongoTemplate.count(query, this.getEntityClass());
    }

    /**
     * 查询名为propertyName的属性值等于value的数据，支持正则表达式
     *
     * @param propertyName
     * @param value
     */
    public List<T> findBy(String propertyName, Object value) {
        Query query = new Query();
        buildQuery(propertyName, value, query);
        return find(query);
    }

    /**
     * 查询名为propertyName的属性值等于value的一条数据，支持正则表达式
     *
     * @param propertyName
     * @param value
     */
    public T findUniqueBy(String propertyName, Object value) {
        Query query = new Query();
        buildQuery(propertyName, value, query);
        return findOne(query);
    }

    /**
     * 查询所有属性key的值都等于value的数据，支持正则表达式
     *
     * @param propMap
     */
    public List<T> findBy(Map<String, Object> propMap) {
        Query query = new Query();
        for (String key : propMap.keySet()) {
            Object value = propMap.get(key);
            if (value != null) {
                buildQuery(key, value, query);
            }
        }
        return find(query);
    }

    /**
     * 构建查询条件
     *
     * @param key
     * @param value
     * @param query
     */
    protected void buildQuery(String key, Object value, Query query) {
        if (key.contains("id")) { //按id查询
            query.addCriteria(Criteria.where(key).is(new ObjectId(value.toString())));
        } else {
            if (value instanceof Number || value instanceof Boolean) {
                query.addCriteria(Criteria.where(key).is(value));
            } else {
                // 如果是字符串，表示正则表达式
                // i为查询, 大小写不敏感
                query.addCriteria(Criteria.where(key).regex(value.toString(), "i"));
            }
        }
    }

    /**
     * 获取需要操作的实体类class <br/>
     * 例如: StudentScoreDao extends MongodbDao <b>&lt;StudentScore&gt;</b> <br/>
     * 返回的是 <b>StudentScore</b> 的Class
     */
    protected Class<T> getEntityClass() {
        return ReflectionUtils.getSuperClassGenricType(getClass());
    }

    /**
     * 获取collection的名字，默认是dao范型T的名字 <br/>
     * 例如: StudentScoreDao extends MongodbDao <b>&lt;StudentScore&gt;</b> <br/>
     * 则返回的名字是：<b>StudentScore</b>
     */
    protected String getCollectionName() {
        return getEntityClass().getSimpleName();
    }

}
