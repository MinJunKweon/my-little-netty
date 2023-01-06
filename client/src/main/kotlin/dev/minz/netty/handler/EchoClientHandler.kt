package dev.minz.netty.handler

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.CharsetUtil

/**
 * 핸들러로 비즈니스 로직 구현에만 집중할 수 있도록 분리
 *
 * `ChannelInboundHandler`와의 차이는?
 *
 * `channelRead0()`이 완료되는 시점에는 이미 메시지가 확보되었고 이용도 끝났기 때문에
 * `ByteBuf` 메모리 참조를 해제해야하기 때문에 편의를 위해 `SimpleChannelInboundHandler`를 사용
 * 반면, EchoServerHandler는 그대로 다시 전송해야하기 때문에 ByteBuf의 메모리를 해제하면 안됨
 *
 * 그러면 언제 메모리를 해제하느냐?
 * => `channelReadComplete()`로 읽기가 완전히 종료된 후 메모리 해제하도록 작성되어있음
 */
@Sharable // 인스턴스를 여러 채널에서 공유할 수 잇음을 나타냄
class EchoClientHandler : SimpleChannelInboundHandler<ByteBuf>() {
    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8)) // 채널 활성화 알림을 받으면 메시지 전송
    }

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: ByteBuf?) {
        println("Client received: ${msg?.toString(CharsetUtil.UTF_8)}") // 수신한 메시지의 덤프를 로깅
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        cause?.printStackTrace() // 예외 시 오류 로깅
        ctx?.close() // 채널 종료
    }
}
