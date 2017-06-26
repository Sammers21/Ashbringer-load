package ashbringer.core;


import io.netty.channel.Channel;

public interface ReceivedResponse {
    void receive(int status, long reqRemain, Channel channelFuture);
}
