package ashbringer.core;


import io.netty.channel.Channel;

public interface ReceivedResponse {
    void receive(int status, Channel channelFuture);
}
