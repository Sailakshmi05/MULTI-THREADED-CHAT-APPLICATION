import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_IP = "127.0.0.1"; // Localhost
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println(in.readLine()); // Prompt for name
            String username = userInput.readLine();
            out.println(username);

            // Start a new thread to read messages from the server
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            receiveThread.start();

            // Read user input and send messages
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }
}