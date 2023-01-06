package dev.minz.netty

import dev.minz.netty.handler.EchoServerHandler
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.net.InetSocketAddress

/**
 * 비즈니스 로직이 포함된 핸들러를 어떤 채널 파이프라인에 추가할지 결정
 *
 * 입출력을 위한 서버를 띄우는 작업을 부트스트랩으로 처리
 */
class Server(
    private val port: Int
) {
    fun start() {
        val serverHandler = EchoServerHandler()
        val group: EventLoopGroup = NioEventLoopGroup() // EventLoopGroup 생성
        runCatching {
            val bootstrap = ServerBootstrap().apply { // ServerBootstrap 생성
                group(group)
                channel(NioServerSocketChannel::class.java) // NIO 전송 채널을 이용하도록 지정
                localAddress(InetSocketAddress(port)) // 지정된 포트를 이용해 소켓 주소 설정
                childHandler(object : ChannelInitializer<SocketChannel>() { // 핸들러를 ChannelPipeline으로 추가
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(serverHandler) // 핸들러가 @Sharable 이므로 동일한 항목을 이용할 수 있음
                    }
                })
            }

            val channelFuture = bootstrap.bind().sync() // 서버를 비동기식으로 바인딩. sync()는 바인딩이 완료되기를 대기
            channelFuture.channel().closeFuture().sync() // 채널의 CloseFuture를 얻고 완료될 때까지 스레드 블로킹
        }
        group.shutdownGracefully().sync() // 이벤트 루프 그룹을 종료하고 모든 리소스를 해제
    }
}
