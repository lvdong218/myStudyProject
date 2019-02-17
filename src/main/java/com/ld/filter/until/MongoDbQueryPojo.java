package com.ld.filter.until;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.junit.Before;
import org.junit.Test;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class MongoDbQueryPojo {
	private Logger logger=LoggerFactory.getLogger(MongoDbQueryPojo.class);
	
	private MongoClient client;
	
	private MongoCollection<User> doc;
	
	private MongoDatabase db;
	@Before
	public void init() {
	 	//编解码器的list
    	List<CodecRegistry> codecResgistes = new ArrayList<>();
    	//list加入默认的编解码器集合
    	codecResgistes.add(MongoClient.getDefaultCodecRegistry());
    	//生成一个pojo的编解码器
    	CodecRegistry pojoCodecRegistry = CodecRegistries.
    			fromProviders(PojoCodecProvider.builder().automatic(true).build());
    	//list加入pojo的编解码器
    	codecResgistes.add(pojoCodecRegistry);
    	//通过编解码器的list生成编解码器注册中心
    	CodecRegistry registry = CodecRegistries.fromRegistries(codecResgistes);
    	
    	//把编解码器注册中心放入MongoClientOptions
    	//MongoClientOptions相当于连接池的配置信息
    	MongoClientOptions build = MongoClientOptions.builder().
    			codecRegistry(registry).build();
		ServerAddress serverAddress=new ServerAddress("47.105.134.108",27022);
		client=new MongoClient(serverAddress,build);
		db=client.getDatabase("lvdong");
		doc=db.getCollection("users",User.class);
	}
	/**
	 * 使用java 原生pojo进行插入
	 */
	@Test
	public void insert() {
		User user=new User();
		user.setUsername("zhangsi");
		user.setCountry("UUU");
		user.setSalary(new BigDecimal("2233.23"));
		Address address=new Address();
		address.setAdd("this is my 地址");
		address.setCode("22388");
		user.setAddress(address);
		Favorites favorites=new Favorites();
		favorites.setCites(Arrays.asList("长沙","河北"));
		favorites.setMovies(Arrays.asList("小电影222","小电影333"));
		user.setFavorites(favorites);
		
		User userTwo=new User();
		userTwo.setUsername("what");
		userTwo.setCountry("SSS");
		userTwo.setSalary(new BigDecimal("22565.23"));
		Address addressTwo=new Address();
		addressTwo.setAdd("this is my 地址two");
		addressTwo.setCode("22389");
		user.setAddress(addressTwo);
		Favorites favoritesTwo=new Favorites();
		favoritesTwo.setCites(Arrays.asList("东莞","东京"));
		favoritesTwo.setMovies(Arrays.asList("肉肉肉","呦呦呦"));
		userTwo.setFavorites(favoritesTwo);
		doc.insertMany(Arrays.asList(user,userTwo));
	}
	
	@Test
	public void testFind() {
		final List<User> ret=new ArrayList<User>();
		Block<User> printBlock=new Block<User>() {
			@Override
			public void apply(User t) {
				System.out.println(t.toString());
				ret.add(t);
			}
		};
		//db.users.find({ "favorites.cites" : { "$all" : [ "东莞" , "东京"]}})
		Bson all=all("favorites.cites",Arrays.asList("东莞","东京"));
		FindIterable<User> find= doc.find(all);
		find.forEach(printBlock);
		logger.info("----------->"+String.valueOf(ret.size()));
		ret.removeAll(ret);
		
		// db.users.find({ "$and" : [ { "username" : { "$regex" : ".*s.*"}} ,
		//{ "$or" : [ { "country" : "English"} , { "country" : "USA"}]}]})
		Bson regex=regex("username",".*z.*");
		Bson or=or(eq("country","English"),eq("country","UUU"));
		FindIterable<User> findtwo=doc.find(and(regex,or));
		findtwo.forEach(printBlock);
		logger.info("----------->"+ret.size());
		ret.removeAll(ret);
	}
	@Test
	public void testUpdate() {
		//db.users.updateMany({ "username" : "lison"},{ "$set" : { "age" : 6}},true)
		Bson eq=eq("username","zhangsi");
		Bson set=set("salary",new BigDecimal("22239885.232"));
		UpdateResult uResult= doc.updateMany(eq,set);
		logger.info("-------->"+String.valueOf(uResult.getModifiedCount()));
	}
	@Test
	public void testDelete() {
	  	//db.users.deleteMany({ "username" : "lison"} )
		DeleteResult dResult=doc.deleteMany(eq("username","zhangsi"));
		logger.info("------->"+String.valueOf(dResult.getDeletedCount()));
		
		//db.users.deleteMany({"$and" : [ {"age" : {"$gt": 8}} , 
		//{"age" : {"$lt" : 25}}]})
		Bson gt=gt("salary",8);
		Bson lt=lt("salary",22566);
		DeleteResult dResultTwo = doc.deleteMany(and(gt,lt));
		logger.info("------->"+String.valueOf(dResultTwo.getDeletedCount()));
		
	}
}
