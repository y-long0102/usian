package com.usian.config;

import com.usian.quartz.OrderQuartz;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz配置类
 */
@Configuration
public class QuartzConfig {
	
	/**
	 * 1.创建Job对象
	 */
	@Bean
	public JobDetailFactoryBean jobDetailFactoryBean(){
		JobDetailFactoryBean factory = new JobDetailFactoryBean();
		//关联我们自己的Job类
		factory.setJobClass(OrderQuartz.class);
		return factory;
	}
	
	/**
	 * Cron Trigger
	 */
   @Bean
	public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean
                                                         jobDetailFactoryBean){
		CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
		factory.setJobDetail(jobDetailFactoryBean.getObject());
		//设置触发时间
		//factory.setCronExpression("0/2 * * * * ?");
	    factory.setCronExpression("*/5 * * * * ?");
		return factory;
	}

	/**
	 * 3.创建Scheduler对象
	 */
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean
					cronTriggerFactoryBean, MyAdaptableJobFactory myAdaptableJobFactory){
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		//关联trigger
		factory.setTriggers(cronTriggerFactoryBean.getObject());
		factory.setJobFactory(myAdaptableJobFactory);
		return factory;
	}
}