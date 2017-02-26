package com.ck.proxy;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class App {
  private static int PORT = 9080;
  private static Logger log;
  private static boolean flag = true;

  public static void main(String[] args) {
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
    log = LoggerFactory.getLogger(App.class);

    DefaultHttpProxyServer.bootstrap()
      .withPort(PORT)
      .withAllowLocalOnly(false)
      .withFiltersSource(getFiltersSource())
      .withChainProxyManager(getChainedProxyManager())
      .withTransparent(true)
      .start();
  }

  private static ChainedProxyManager getChainedProxyManager() {

    final ChainedProxyAdapter adapter = new ChainedProxyAdapter() {
      public InetSocketAddress getChainedProxyAddress() {
        return new InetSocketAddress("127.0.0.1", 9081);
      }
    };

    final ChainedProxyAdapter adapter2 = new ChainedProxyAdapter() {
      public InetSocketAddress getChainedProxyAddress() {
        return new InetSocketAddress("127.0.0.1", 9082);
      }
    };

    return new ChainedProxyManager() {
      public void lookupChainedProxies(HttpRequest httpRequest, Queue<ChainedProxy> chainedProxies) {

        if(flag) {
          chainedProxies.add(adapter);
          chainedProxies.add(adapter2);
        } else {
          chainedProxies.add(adapter2);
          chainedProxies.add(adapter);
        }

        flag = !flag;
      }
    };
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
