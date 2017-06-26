package ashbringer.core;


import ashbringer.core.session.SessionHttpInitializer;
import ashbringer.core.single.SingleHttpInitializer;
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

    public URI uri;
    public final String host;
    public final String path;
    public final int port;
    public EventLoopGroup group;
    public boolean ssl;

    public NettyShooter(String host, int port, String path, int nThreads) {
        this(host, port, path, nThreads, false);
    }

    public NettyShooter(String host, int port, String path, int nThreads, boolean ssl) {

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
        this.ssl = ssl;
        group = new NioEventLoopGroup(nThreads);
    }

    public void shoot(ShootComplete shootComplete) {

        Bootstrap b = new Bootstrap();
        b.group(group)               .channel(NioSocketChannel.class)
                .handler(new SingleHttpInitializer());
        // Make the connection attempt.
        b.connect(host, port).addListener(
                (ChannelFutureListener) channelFuture -> {
                    sendHttpRequestAndClose(channelFuture, shootComplete);
                });
    }

    @Override
    public void startShootConnection(long times, ShootComplete oneShootComplete, ShootComplete shootSessionComplete) {
        Bootstrap b = new Bootstrap();
        SessionHttpInitializer sessionHttpInitializer = new SessionHttpInitializer(times, (status, remain, ch) -> {
            System.out.println("status " + status);

            if (status == 200 && remain > 0)
                sendHttp(ch);
        });


        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(sessionHttpInitializer);
        // Make the connection attempt.
        b.connect(host, port).addListener(
                (ChannelFutureListener) channelFuture -> {
                    sessionHttpInitializer.sessionHttpHandler.channel = channelFuture.channel();
                    sendHttp(channelFuture.channel());
                });
    }

    private void sendHttpRequestAndClose(ChannelFuture channelFuture, ShootComplete shootComplete) {
        // Send the HTTP request.
        Channel channel = channelFuture.channel();

        HttpRequest request = get();

        channel.writeAndFlush(request).addListener((ChannelFutureListener) l -> {
            close(l, shootComplete);
        });
    }

    private void sendHttp(Channel channel) {
        System.out.println("send http");
        HttpRequest request = get();
        channel.writeAndFlush(request);
    }

    private void close(ChannelFuture channelFuture, ShootComplete shootComplete) {
        // Wait for the server to close the connection and call shootComplete
        channelFuture.channel()
                .closeFuture()
                .addListener(cf -> shootComplete.shootComplete());
    }

    HttpRequest get() {
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.HEAD, uri.getRawPath());

        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        return request;
    }

    public void killNettyThreads() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        NettyShooter nettyShooter = new NettyShooter("0.0.0.0", 8080, "", 1);
        nettyShooter.startShootConnection(3,
                () -> {
                    System.out.println("complete one");
                },
                () -> {
                    System.out.println("complete all");
                });
        /*nettyShooter.shoot(() -> {
            System.out.println("kek21");
        });*/
        Thread.sleep(10000);

        nettyShooter.killNettyThreads();

    }
}

