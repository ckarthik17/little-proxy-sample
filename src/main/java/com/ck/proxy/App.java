package com.ck.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.littleshoot.proxy.*;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;

@Slf4j
public class App {
  private static int PORT = 9080;

  public static void main(String[] args) {
    HttpFiltersSource filtersSource = getFiltersSource();

    HttpProxyServerBootstrap server = DefaultHttpProxyServer.bootstrap()
      .withPort(PORT)
      .withAllowLocalOnly(false)
      .withFiltersSource(filtersSource);

    server.start();
  }

  private static HttpFiltersSource getFiltersSource() {
    return new HttpFiltersSourceAdapter() {

      public HttpFilters filterRequest(HttpRequest originalRequest) {
        return new HttpFiltersAdapter(originalRequest) {

          public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
            log.info("Filter: p2SResolutionStarted for: " + resolvingServerHostAndPort);
            log.info("Filter: originalRequest: " + originalRequest.getUri());

            if (originalRequest.getUri().contains("node1")) {
              log.info("Routing to server 1");
              return new InetSocketAddress("210.212.230.22", 80);
            } else {
              log.info("Routing to server 2");
              return new InetSocketAddress("182.73.100.147", 8080);
            }
          }

          public void serverToProxyResponseTimedOut() {
            log.info("serverToProxyResponseTimedOut");
          }

          public void serverToProxyResponseReceiving() {
            log.info("serverToProxyResponseReceiving");

          }

          public void serverToProxyResponseReceived() {
            log.info("serverToProxyResponseReceived");
          }

          public HttpObject serverToProxyResponse(HttpObject httpObject) {
            log.info("serverToProxyResponse");
            return httpObject;
          }

          public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
            log.info("proxyToServerResolutionSucceeded");
          }
          
          public void proxyToServerResolutionFailed(String hostAndPort) {
            log.info("proxyToServerResolutionFailed");
          }

          public void proxyToServerRequestSent() {
            log.info("proxyToServerRequestSent");
          }

          public void proxyToServerRequestSending() {
            log.info("proxyToServerRequestSending");
          }

          public HttpResponse proxyToServerRequest(HttpObject httpObject) {
            log.info("proxyToServerRequest");
            return null;
          }

          public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
            log.info("proxyToServerConnectionSucceeded");
          }

          public void proxyToServerConnectionStarted() {
            log.info("proxyToServerConnectionStarted");
          }

          public void proxyToServerConnectionSSLHandshakeStarted() {
            log.info("proxyToServerConnectionSSLHandshakeStarted");
          }

          public void proxyToServerConnectionQueued() {
            log.info("proxyToServerConnectionQueued");
          }

          public void proxyToServerConnectionFailed() {
            log.info("proxyToServerConnectionFailed");
          }

          public HttpObject proxyToClientResponse(HttpObject httpObject) {
            log.info("proxyToClientResponse");
            return httpObject;
          }

          public HttpResponse clientToProxyRequest(HttpObject httpObject) {
            log.info("clientToProxyRequest");
            return null;
          }
        };
      }
    };
  }
}
