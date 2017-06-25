package ashbringer.core;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;

public class NettyShooter implements Shooter {

    URI uri;
    public final String host;
    public final String path;
    public final int port;
    EventLoopGroup group;

    public NettyShooter(String host, int port, String path, int nThreads) {

        //make uri
        uri = null;
        try {
            uri = new URI(String.format("http://%s:%d%s", host, port, path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Prepare the HTTP request.
        this.host = host;
        this.port = port;
        this.path = path;
        group = new NioEventLoopGroup(nThreads);
    }

    public void shoot(ShootComplete shootComplete) {

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new HttpClientInitializer());

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

