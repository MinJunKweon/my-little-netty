package dev.minz.netty.handler

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.CharsetUtil

/**
 * Handler를 나눔으로서 비즈니스 로직을 구현함
 *
 * 서버를 띄우고 입출력을 처리하는 부분은 부트스트랩으로 처리
 */
@Sharable // ChannelHandler를 여러 채널 간에 안전하게 공유할 수 있음을 나타냄
class EchoServerHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val inBuf = msg as ByteBuf
        println("Server received: ${inBuf.toString(CharsetUtil.UTF_8)}")
        ctx.write(inBuf) // 아웃바운드 메시지를 플러시하지 않은 채로 받은 메시지를 발신자로 출력 (Echo)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        ctx?.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기중인 메시지를 원격 피어로 플러시하고 채널 종료
            ?.addListener(ChannelFutureListener.CLOSE)
    }

    /**
     * 예외 포착이 필요한 이유?
     *
     * 모든 채널에는 Handler 체인을 포함하는 ChannelPipeline이 연결되어 있음
     * 체인의 어디에도 `exceptionCaught()`가 구현되지 않은 경우,
     * 파이프라인의 끝까지 이동된 후 로깅됨
     * **따라서, 구현해주는 것이 바람직함**
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        cause?.printStackTrace() // 예외 스택 추적 출력
        ctx?.close() // 채널 종료
    }
}
