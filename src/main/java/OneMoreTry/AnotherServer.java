package OneMoreTry;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AnotherServer {
    private static int PORT = 9876;
    public static void main (String args[]) {
        ByteBuffer sharedBuffer = ByteBuffer.allocate(8192);
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Selector selector = null;
        ServerSocket serverSocket = null;
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(PORT));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.err.println("Unable to setup environment");
            System.exit(-1);
        }
        try {
            while (true) {
                int count = selector.select();
                // нечего обрабатывать
                if (count == 0) {
                    continue;
                }
                Set keySet = selector.selectedKeys();
                Iterator itor = keySet.iterator();
                while (itor.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) itor.next();
                    itor.remove();
                    Socket socket = null;
                    SocketChannel channel = null;
                    if (selectionKey.isAcceptable()) {
                        System.out.println("Got acceptable key");
                        try {
                            socket = serverSocket.accept();
                            System.out.println("Connection from: " + socket);
                            channel = socket.getChannel();
                        } catch (IOException e) {
                            System.out.println("не можем подсоеденить канал");
                            selectionKey.cancel();
                        }
                        if (channel != null) {
                            try {
                                System.out.println("Watch for something to read");
                                channel.configureBlocking(false);
                                channel.register(selector, SelectionKey.OP_READ);
                            } catch (IOException e) {
                                System.out.println("не можем заюзать канал");
                                selectionKey.cancel();
                            }
                        }
                    }
                    if (selectionKey.isReadable()) {
//                        System.out.println("читаем че он там настрочил");
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
//                        ByteBuffer buffer = ByteBuffer.allocate(1024);
//                        socketChannel.read(buffer);
//                        String line = new String(buffer.array()).trim();
//                        System.out.println(line);
//                        sharedBuffer = buffer;
                        sharedBuffer.clear();
                        int bytes = -1;
                        try {
                            ByteBuffer buffer = ByteBuffer.allocate(8192);
                            socketChannel.read(buffer);
                            String data = new String(buffer.array()).trim();
                            if (data.length() > 0){
                                System.out.println("полученное сообщение - " + data);

                            }
                            socketChannel.write(ByteBuffer.wrap((data.toUpperCase(Locale.ROOT) + " gay").getBytes()));

//                            while ((bytes = socketChannel.read(sharedBuffer)) > 0) {
//                                System.out.println("Reading...");
//                                sharedBuffer.flip();
//                                while (sharedBuffer.hasRemaining()) {
//                                    System.out.println("Writing...");
//                                    System.out.println();
////                                    ByteBuffer buffer = ByteBuffer.allocate(1024);
//
//
//                                    socketChannel.read(sharedBuffer); //считали то че пришло
//
////                                    String line = new String(buffer.array()).trim();
////                                    System.out.println(line.equals(""));
////                                    System.out.println(line);
////                                    String news = new String(sharedBuffer.array()).trim();
////                                    ByteBuffer bf = ByteBuffer.wrap(news.getBytes());
//                                    socketChannel.write(sharedBuffer);
//
//                                    String cmd = new String(sharedBuffer.array()).trim();
//                                    System.out.println(cmd);
//
////                                    System.out.println(new String(sharedBuffer.array()).trim());
////                                    String convert = new String(buffer.array(), "UTF-8").trim();
////                                    System.out.println(convert);
//                                }
//                                sharedBuffer.clear();
//                            }
                        } catch (IOException e) {
                            System.out.println("клиент отлетел");
                            selectionKey.cancel();
                        }
                        try {
                            System.out.println("закрываем поток...");
                            socketChannel.close();
                        } catch (IOException e) {
                            System.out.println("клиент отлетел");
                            selectionKey.cancel();
                        }
                    }
                    System.out.println("сервер ожидает дальнейших действий.");
                }
            }
        } catch (IOException e) {
            System.out.println("ошибка при select()");
        }
    }
}