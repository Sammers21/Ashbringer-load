/*
 * Copyright 2017 Pavel Drankov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ashbringer.core;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.*;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;

public class NettyShooter implements Shooter {

    URI uri;
    public final String host;
    public final String path;
    public final int port;
    public final boolean ssl;
    EventLoopGroup group;

    public NettyShooter(String host, int port, String path, int nThreads, boolean ssl) {

        //make uri
        uri = null;
        try {
            String ssls = ssl ? "s" : "";
            uri = new URI(String.format("http%s://%s:%d%s", ssls, host, port, path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Prepare the HTTP request.
        this.host = host;
        this.port = port;
        this.path = path;
        this.ssl = ssl;
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            System.out.println("Use epoll");
            group = new EpollEventLoopGroup(nThreads);
        } else {
            System.out.println("Use nio");
            group = new NioEventLoopGroup(nThreads);
        }
    }

    public void shoot(ShootComplete shootComplete) {

        Bootstrap b = new Bootstrap();

        SslContext sslContext = null;
        if (ssl) {
            try {
                sslContext = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                e.printStackTrace();
            }
        }

        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new HttpClientInitializer(sslContext));

        // Make the connection attempt.
        b.connect(host, port).addListener(
                (ChannelFutureListener) channelFuture -> {
                    sendHttpRequest(channelFuture, shootComplete);
                });
    }

    private void sendHttpRequest(ChannelFuture channelFuture, ShootComplete shootComplete) {
        // Send the HTTP request.
        Channel channel = channelFuture.channel();

        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        channel.writeAndFlush(request);
        // Wait for the server to close the connection and call shootComplete
        channel.closeFuture().addListener(cf -> shootComplete.shootComplete());
    }

}

