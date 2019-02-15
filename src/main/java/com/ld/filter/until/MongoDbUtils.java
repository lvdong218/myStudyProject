package com.ld.filter.until;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * java原生代码实现mongodb
 * @author lvdong
 *
 */
import com.mongodb.client.MongoDatabase;
public class MongoDbUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(MongoDbUtils.class);
	//mongClient（内置连接池）
	private MongoClient client;
	//文档集合
	private MongoCollection<Document> doc;
	//mongodb数据库
	private MongoDatabase db;
	
	@Before
	public void init() {
		client=new MongoClient("47.105.134.108",27022);
		db=client.getDatabase("lvdong");
		doc=db.getCollection("users");
	}
	/**
	 * mongo 插入多条数据方式
	 */
	@Test
	public void insert() {
		Document tempDoc=new Document("username","cang");
		tempDoc.append("age", 18);
		tempDoc.append("salary", new BigDecimal("19565.215"));
		//添加子文档
		Map<String,String> map=new HashMap<String,String>();
		map.put("acode","0000");
		map.put("code", "xxx0000");
		tempDoc.append("adress",map);
		//添加favorite子文档，其中两个属性是数组
		Map<String,Object> favoriteMap=new HashMap<String,Object>();
		favoriteMap.put("movies",Arrays.asList("钢铁","蜘蛛","你猜猜看"));
		favoriteMap.put("cites",Arrays.asList("长沙","江都"));
		tempDoc.append("favorite",favoriteMap);
		System.out.println("list.size"+Arrays.asList(tempDoc,tempDoc).size());
		doc.insertMany(Arrays.asList(tempDoc,tempDoc));
	}
	
	/**
	 * mongodb   all查询方法
	 */
	@Test
	public void textFind() {
		final List<Document> ret = new ArrayList<>();
		Block<Document> printBlock=new Block<Document>() {
			public void apply(Document t) {
				logger.info(t.toJson());
				ret.add(t);
			}
		};
		//db.users.find({ "favorites.cites" : { "$all" : [ "东莞" , "东京"]}})
		Bson all=all("favorites.cites",Arrays.asList("东莞"));
		FindIterable<Document> findResult=doc.find(all);
		findResult.forEach(printBlock);
		logger.info("------------------------>"+ret.size());
		ret.removeAll(ret);
	}
}
