import org.json.JSONObject
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

val localPort=13207
var localIp=""

var remoteport=13207
var remoteip=""

var wandorful=false

lateinit  var channel: DatagramChannel
private val buf: ByteBuffer = ByteBuffer.allocate(600)
private val bufReceive: ByteBuffer = ByteBuffer.allocate(600)
val byteArray=ByteArray(500){
    0.toByte()
}


fun bytebuffer2ByteArray(buffer: ByteBuffer): ByteArray? {
    buffer.flip()
    val len = buffer.limit() - buffer.position()
    val bytes = ByteArray(len)
    for (i in bytes.indices) {
        bytes[i] = buffer.get()
    }
    return bytes
}


fun initUdp(){
    try {
        channel = DatagramChannel.open();
        channel.socket().bind(InetSocketAddress(localPort));
    } catch (e: IOException) {

        e.printStackTrace();
    }
}
fun ip2String(s: InetAddress):String{
    var ip=s.toString()
    ip=ip.substring(ip.lastIndexOf("/")+1)
    return ip
}
fun StartListen() {
    while (true) {
        try {
            bufReceive.clear()
            val sourceAddress=channel.receive(bufReceive) as InetSocketAddress
            val sip=ip2String(sourceAddress.address)
            val sport=sourceAddress.port
           println("fuckgaga "+"$sip    $sport")

            val receiveByteArray=bytebuffer2ByteArray(bufReceive)
            if (receiveByteArray != null) {
                val receiveString=String(receiveByteArray)

                try {
                    if(receiveString.substring(0,1)=="{"){
                        val receiveJson= JSONObject(receiveString)
                        val ip=receiveJson.getString("innerIp")
                        val port=receiveJson.getInt("innerPort")
                        println("fuckget "+receiveString)
                        send2Destination("fuckyou ${System.currentTimeMillis()} from \n"+localIp,ip,port)
                    }else{

                        if(receiveString.contains("fuckyou") && !receiveString.contains("fuckyouYes")){
                            remoteip=sip
                            remoteport=sport
                            send2Destination("fuckyouYes ${System.currentTimeMillis()} from \n"+localIp,sip,sport)
                        }
                        if(receiveString.contains("fuckyouYes")){
                            remoteip=sip
                            remoteport=sport
                            wandorful=true
                            send2Destination("complete ${System.currentTimeMillis()} from \n"+localIp,sip,sport)
                        }
                        if(receiveString.contains("complete")){
                            remoteip=sip
                            remoteport=sport
                            wandorful=true
                        }
//                        MainScope().launch {
//                            binding.fuck.text=receiveString
//                        }
                       println("good "+receiveString)
                    }

                }catch (e:Exception){

                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}



fun send(message: String) {
    try {
        val configInfo = message.toByteArray()
        buf.clear()
        buf.put(configInfo)
        buf.flip()
        channel.send(buf, InetSocketAddress("49.234.92.213", 8888))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun send2Destination(message: String,ip:String,port:Int) {
    try {
        val buf: ByteBuffer = ByteBuffer.allocate(600)
        val configInfo = message.toByteArray()
        buf.clear()
        buf.put(configInfo)
        buf.flip()
        channel.send(buf, InetSocketAddress(ip,port))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun main(args: Array<String>) {
    initUdp()
    val addr = InetAddress.getLocalHost()
    val ipip= addr.hostAddress
    localIp=ipip
    Thread{
        while (true){

            val ga=org.json.JSONObject()
            ga.put("ip",ipip)
            ga.put("port",localPort)
            send(ga.toString())
            Thread.sleep(10000)
            // send2Destination("myDream ${System.currentTimeMillis()} from \n"+localIp,"65.49.212.218",8888)
//                send2Destination("我想吃屎","192.168.1.3",13207)
        }
    }.start()
    Thread{


        Thread.sleep(100)

        StartListen()
    }.start()

    Thread{
        while(true){
            Thread.sleep(10)
            if(wandorful){
                println("fuck"+"ppppp   $remoteip    $remoteport")
                send2Destination("myDream ${System.currentTimeMillis()} from \n"+localIp,remoteip,remoteport)
            }
        }


    }.start()
}