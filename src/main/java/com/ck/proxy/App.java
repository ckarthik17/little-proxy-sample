package com.ck.proxy;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Queue;

public class App {
  private static int PORT = 9080;
  private static Logger log;

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
      @Override
      public InetSocketAddress getChainedProxyAddress() {
        return new InetSocketAddress("45.64.156.204", 8080);
      }
    };

    return new ChainedProxyManager() {
      public void lookupChainedProxies(HttpRequest httpRequest, Queue<ChainedProxy> chainedProxies) {
        chainedProxies.add(adapter);
      }
    };
  }

  private static HttpFiltersSource getFiltersSource() {
    return new HttpFiltersSourceAdapter() {

      public HttpFilters filterRequest(HttpRequest originalRequest) {
        return new HttpFiltersAdapter(originalRequest) {

          public HttpResponse clientToProxyRequest(HttpObject httpObject) {
            log.info("clientToProxyRequest");
            return null;
          }

          public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
            log.info("proxyToServerResolutionStarted");
            return null;
          }

          public HttpResponse proxyToServerRequest(HttpObject httpObject) {
            log.info("proxyToServerRequest");
            return null;
          }

          public HttpObject serverToProxyResponse(HttpObject httpObject) {
            log.info("serverToProxyResponse");
            return httpObject;
          }

          public HttpObject proxyToClientResponse(HttpObject httpObject) {
            log.info("proxyToClientResponse");
            return httpObject;
          }
        };
      }
    };
  }
}
