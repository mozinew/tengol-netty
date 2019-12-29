package com.tengol.netty.bio.hello;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO 服务端：Hello 实例
 *
 * 实例说明：
 * （1）使用 BIO 模型编写一个服务器端，监听 6666 端口
 * （2）当有客户端连接时，就启动一个线程与之通讯，要求使用线程池机制改善，可以连接多个客户端
 * （3）服务器端可以接收客户端发送的数据( telnet 方式即可)。
 *
 * 实现思路：
 * （1）先创建一个线程池，用于处理连接
 * （2）创建 ServerSocket，监听 6666 端口
 * （3）监听连接，无客户端时会阻塞
 * （4）对每一个连接创建一个线程，处理请求
 * （5）处理请求
 *
 * @author dongrui
 * @date 2019/12/29 13:13
 */
public class HelloBIOServer {
    public static void main(String[] args) throws IOException {
        //（1）先创建一个线程池，用于处理连接
        ExecutorService threadPool = Executors.newCachedThreadPool();

        //（2）创建 ServerSocket，监听 6666 端口
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动成功");

        while (true){
            //（3）监听连接，无客户端时会阻塞
            System.out.println("当前线程为" + Thread.currentThread().getName() + ",等待客户端连接...");
            final Socket socket = serverSocket.accept();

            //（4）对每一个连接创建一个线程，处理请求
            threadPool.execute(()->{
                //（5）处理请求
                handler(socket);
            });
        }



    }

    //与客户端的请求处理
    private static void handler(Socket socket) {
        String ip = socket.getInetAddress().getHostAddress();
        int port = socket.getPort();
        String clientId = ip.concat(":").concat(String.valueOf(port));
        System.out.println("当前线程" + Thread.currentThread().getName()+",客户端" + clientId + "连接成功");

        //创建字节数组，用于读取输入流
        byte[] bytes = new byte[1024];
        //输入流
        try {
            while (true){
                InputStream inputStream = socket.getInputStream();
                int read = inputStream.read(bytes);
                if(read != -1){
                    System.out.println(clientId + " : " + new String(bytes,0,read));
                }else{
                    System.out.println("本次读取完毕");
                    break;
                }
            }
            System.out.println("-------");

        }catch (IOException e){
            e.printStackTrace();
        } finally {
            System.out.println("关闭和客户端"+clientId+"的连接");
            try {
                socket.close();
                System.out.println("连接关闭成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
