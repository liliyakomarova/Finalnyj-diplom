import java.io.File;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    private static final int PORT = 8989;
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine;
        engine = new BooleanSearchEngine(new File("pdfs"));
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(engine.search("бизнес"));
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    out.println("");
                    String word = in.readLine().toLowerCase();
                    Gson gson = new Gson();
                    List<PageEntry> searchList = engine.search(word);
                    String json = gson.toJson(searchList);
                    out.println(json);
                } catch (IOException e) {
                    System.out.println("Не могу стартовать");
                    e.printStackTrace();
                }
            }
        }
    }
}