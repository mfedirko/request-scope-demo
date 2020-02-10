package com.example.scopedemo.controller;

import com.example.scopedemo.service.ExecutionMetaInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Order(3)
@Component
public class MetaInfoContextInitializingFilter implements Filter {

  @Autowired
  private ExecutionMetaInfoContext context;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    context.setOriginalRequestThread();
    chain.doFilter(request, response);
  }

}