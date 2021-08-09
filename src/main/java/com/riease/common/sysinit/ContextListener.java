package com.riease.common.sysinit;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ContextListener implements ServletContextListener {
	
	private static Logger logger = LoggerFactory.getLogger(ContextListener.class);
	
	private List<ContextJob> jobs = new ArrayList<ContextJob>();
		
	public ContextListener() {
		init();
	}
	
	/**
	 * 實作註冊ContextJob
	 */
	public abstract void init();
	
	
	/**
	 * Add ContextJob for initialize or destroy.
	 * @param job
	 */
	public void addJob(ContextJob job) {
		if(job != null) {
			jobs.add(job);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent ce) {
		for(ContextJob job : jobs) {
			int r = job.destroy();
			if(r==0) {
				logger.info("{} ................... destroyed", job.getName());
			}else {
				logger.error("{} ................... destroy failed", job.getName());
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent ce) {
		/*
		 * Path information
		 */
		Path.getInstance().setRealPath(ce.getServletContext().getRealPath(""));
		Path.getInstance().setWebContextPath(ce.getServletContext().getContextPath());
		logger.info("Context Real Path  ...... {}", Path.getInstance().getRealPath());
		logger.info("WebContext Path  ........ {}", Path.getInstance().getWebContextPath());
		/*
		 * Context Jobs
		 */
		for(ContextJob job : jobs) {
			int r = job.initialize();
			if(r==0) {
				logger.info("{} ................... initialized", job.getName());
			}else {
				logger.error("{} ................... initialize failed", job.getName());
			}
		}
	}

}
