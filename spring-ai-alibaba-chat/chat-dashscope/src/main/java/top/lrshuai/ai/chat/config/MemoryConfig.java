package top.lrshuai.ai.chat.config;

import com.alibaba.cloud.ai.autoconfigure.memory.MysqlChatMemoryProperties;
import com.alibaba.cloud.ai.autoconfigure.memory.redis.RedisChatMemoryProperties;
import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 */

@Configuration
public class MemoryConfig {

//	@Value("${spring.ai.memory.redis.host}")
//	private String redisHost;
//	@Value("${spring.ai.memory.redis.port}")
//	private int redisPort;
//	@Value("${spring.ai.memory.redis.password}")
//	private String redisPassword;
//	@Value("${spring.ai.memory.redis.timeout}")
//	private int redisTimeout;

	@Resource
	private RedisChatMemoryProperties redisChatMemoryProperties;

	@Resource
	private MysqlChatMemoryProperties mysqlChatMemoryProperties;
//
//	@Bean
//	public SQLiteChatMemoryRepository sqliteChatMemoryRepository() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("org.sqlite.JDBC");
//		dataSource.setUrl("jdbc:sqlite:spring-ai-alibaba-chat-memory-example/src/main/resources/chat-memory.db");
//		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//		return SQLiteChatMemoryRepository.sqliteBuilder()
//				.jdbcTemplate(jdbcTemplate)
//				.build();
//	}

	@Bean
	public MysqlChatMemoryRepository mysqlChatMemoryRepository() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(mysqlChatMemoryProperties.getDriverClassName());
		dataSource.setUrl(mysqlChatMemoryProperties.getJdbcUrl());
		dataSource.setUsername(mysqlChatMemoryProperties.getUsername());
		dataSource.setPassword(mysqlChatMemoryProperties.getPassword());
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return MysqlChatMemoryRepository.mysqlBuilder()
				.jdbcTemplate(jdbcTemplate)
				.build();
	}

	@Bean
	public RedissonRedisChatMemoryRepository redissonRedisChatMemoryRepository() {
		return RedissonRedisChatMemoryRepository.builder()
				.host(redisChatMemoryProperties.getHost())
				.port(redisChatMemoryProperties.getPort())
				.password(redisChatMemoryProperties.getPassword())
				.timeout(redisChatMemoryProperties.getTimeout())
				.build();
	}
}
