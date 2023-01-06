import dev.minz.netty.Client

fun main(args: Array<String>) {
    if (args.size != 2) {
        error("Usage: ${Client::class.simpleName} <host> <port>")
    }
    val host = args[0]
    val port = args[1].toInt()

    Client(host, port).start()
}
