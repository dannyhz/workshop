package cn.evun.sweet.auth.dao;

import java.util.List;

import tk.mybatis.mapper.common.Mapper;
import cn.evun.sweet.auth.model.ItemDo;

public interface ItemMapper extends Mapper<ItemDo> {
	
	/*查询所有的功能标签分类*/
	List<String> selectItemTabList();
	
}