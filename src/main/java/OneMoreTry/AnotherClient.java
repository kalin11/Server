package OneMoreTry;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class AnotherClient {
    private static final int LOOP_COUNT = 10;
    private static final int SLEEP_TIME = 500;
    private static final int PORT = 9876;
    public static void main(String args[]) throws IOException, InterruptedException {
//        Socket socket = new Socket("localhost", PORT);
//        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
//        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//        while (true){
//            String cmd = keyboard.readLine();
//            out.println(cmd);
//        }

        LinkedList<String> list = new LinkedList<>();
        while (true){
            Socket socket = new Socket("localhost", PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            out.println(bufferedReader.readLine());

            String line;
            while ((line = in.readLine()) != null) {
                try {
                    if (line.equals("first")) {
                        System.out.println(list.getFirst());
                    }
                }catch (NoSuchElementException e){
                    System.out.println("пусто");
                }
                list.add(line);
                System.out.println(list);
                System.out.println( ": " + line);

            }
            socket.close();
        }
    }
}