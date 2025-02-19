/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.appengine.tools.development.devappserver2;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A Filter that makes the X-Appengine-Dev-Request-Id header available within the request thread.
 *
 */
public class RequestIdFilter implements Filter {
  private static final ThreadLocal<String> threadRequestId = new ThreadLocal<>();

  @Override public void init(FilterConfig filterConfig) {}

  @Override public void destroy() {}

  @Override public void doFilter(
      ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String requestId = httpRequest.getHeader("X-Appengine-Dev-Request-Id");
      threadRequestId.set(requestId);
    }
    try {
      chain.doFilter(request, response);
    } finally {
      threadRequestId.remove();
    }
  }

  static String threadRequestId() {
    return threadRequestId.get();
  }
}
