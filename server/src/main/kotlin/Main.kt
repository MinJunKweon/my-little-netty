import dev.minz.netty.Server

fun main(args: Array<String>) {
    if (args.size != 1) {
        error("Usage: ${Server::class.simpleName} <port>")
    }
    val port = args[0].toInt()
    Server(port).start()
}
