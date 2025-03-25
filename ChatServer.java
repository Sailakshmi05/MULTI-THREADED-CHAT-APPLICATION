import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Broadcast messages to all connected clients
    public static synchronized void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Remove a client from the list
    public static synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    // Inner class to handle client communication
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your name: ");
                username = in.readLine();
                System.out.println(username + " has joined the chat.");

                broadcast(username + " has joined the chat!", this);

                String message;
                while ((message = in.readLine()) != null) {
                    if ("exit".equalsIgnoreCase(message)) {
                        break;
                    }
                    System.out.println(username + ": " + message);
                    broadcast(username + ": " + message, this);
                }

                System.out.println(username + " has left the chat.");
                broadcast(username + " has left the chat.", this);
                removeClient(this);
                socket.close();
            } catch (IOException e) {
                System.out.println("Error in client communication: " + e.getMessage());
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}