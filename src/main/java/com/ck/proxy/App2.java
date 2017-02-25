package com.ck.proxy;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSource;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

@Slf4j
public class App2 {
  private static int PORT = 9081;

  public static void main(String[] args) {
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");

    DefaultHttpProxyServer.bootstrap()
      .withPort(PORT)
      .withAllowLocalOnly(false)
      .withFiltersSource(getFiltersSource())
      .start();
  }

  private static HttpFiltersSource getFiltersSource() {
    return new HttpFiltersSourceAdapter() {

      public HttpFilters filterRequest(HttpRequest originalRequest) {
        return new HttpFiltersAdapter(originalRequest) {

          public HttpResponse clientToProxyRequest(HttpObject httpObject) {
            if(httpObject instanceof HttpRequest) {
              HttpRequest request = (HttpRequest) httpObject;
              log.info("clientToProxyRequest {}", request.getUri());
            }
            return null;
          }
        };
      }
    };
  }
}
