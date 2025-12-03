package com.sescity.vbase;

import java.util.logging.LogManager;

import com.sescity.vbase.conf.druid.EnableDruidSupport;
import com.sescity.vbase.utils.GitUtil;
import com.sescity.vbase.utils.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan("com.sescity.vbase.conf")
@SpringBootApplication
@EnableScheduling
@EnableDruidSupport
public class VbaseBootstrap extends LogManager {

	private final static Logger logger = LoggerFactory.getLogger(VbaseBootstrap.class);

	private static String[] args;
	private static ConfigurableApplicationContext context;
	public static void main(String[] args) {
		VbaseBootstrap.args = args;
		VbaseBootstrap.context = SpringApplication.run(VbaseBootstrap.class, args);
		GitUtil gitUtil1 = SpringBeanFactory.getBean("gitUtil");
		logger.info("构建版本： {}", gitUtil1.getBuildVersion());
		logger.info("构建时间： {}", gitUtil1.getBuildDate());
		logger.info("GIT最后提交时间： {}", gitUtil1.getCommitTime());
	}
	// 项目重启
	public static void restart() {
		context.close();
		VbaseBootstrap.context = SpringApplication.run(VbaseBootstrap.class, args);
	}
	

}
