package com.ld.filter.until;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Projections.*;
import org.junit.Test;

@ContextConfiguration("classpath:applicationContext.xml")
public class MongoDBQurey {
	
	@Resource(name="mongo")
	private MongoClient client;
	
	private MongoDatabase db;
	
	private MongoCollection<Document> collection;
	
	private MongoCollection<Document> orderCollection;
	
	private Logger logger=LoggerFactory.getLogger(MongoDBQurey.class);
	@Before
	public void init() {
		db=client.getDatabase("lvdong");
		collection=db.getCollection("users");
		orderCollection=db.getCollection("ordersTest");
	}
	//返回对象的处理器，打印每一行数据
	private Block<Document> getBlock(final List<Document> ret) {
		Block<Document> printBlock = new Block<Document>() {
			@Override
			public void apply(Document t) {
				logger.info("---------------------");
				logger.info(t.toJson());
				logger.info("---------------------");
				ret.add(t);
			}
		};
		return printBlock;
	}
	//打印查询出来的数据和查询的数据量
	private void printOperation( FindIterable<Document> find) {
		final List<Document> ret = new ArrayList<Document>();
		Block<Document> printBlock = getBlock(ret);
		find.forEach(printBlock);
		System.out.println(ret.size());
		ret.removeAll(ret);
	}

	
	private void printOperation(List<Document> ret, Block<Document> printBlock,
			AggregateIterable<Document> aggregate) {
		aggregate.forEach(printBlock);
		System.out.println(ret.size());
		ret.removeAll(ret);

	}
	@Test
	public void testInOper() {
		Bson in = in("username", "lison", "mark", "james");
		FindIterable<Document> find = collection.find(in);
		printOperation(find);
	}
	
	
}
