package com.hehe120.common.context;

import java.util.Date;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

public class VersionServletContext implements ServletContextAware {

	private ServletContext servletContext;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext=servletContext; 
        getServletContext().setAttribute("resouceVersion", new Date().getTime()); 
	}
	
	public ServletContext getServletContext() {  
        return servletContext;  
    }  
}
