package dev.minz.netty

import dev.minz.netty.handler.EchoClientHandler
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress

class Client(
    private val host: String,
    private val port: Int
) {
    fun start() {
        val group = NioEventLoopGroup()
        runCatching {
            val bootstrap = Bootstrap().apply {
                group(group) // 클라이언트 이벤트를 처리할 이벤트 루프 그룹을 지정함, NIO 구현이 이용됨
                channel(NioSocketChannel::class.java) // 채널 유형으로 NIO 전송 유형 중 하나를 지정
                remoteAddress(InetSocketAddress(host, port)) // 서버의 호스트 IP와 포트를 설정
                handler(object : ChannelInitializer<SocketChannel>() { // 채널이 생성될 때 파이프라인에 핸들러를 하나 추가
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(EchoClientHandler()) // @Sharable이기 때문에 인스턴스를 공통으로 사용함
                    }
                })
            }
            val channelFuture = bootstrap.connect().sync() // 원격 피어로 연결하고 연결이 완료되기를 대기
            channelFuture.channel().closeFuture().sync() // 채널이 닫힐 때까지 스레드 블로킹
        }
        group.shutdownGracefully().sync() // 스레드 풀을 종료하고 모든 리소스를 해제함
    }
}
