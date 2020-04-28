package ntunjic;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class HangmanClient {
    private Socket server;
    private BufferedWriter toServer;
    private BufferedReader fromServer;

    public HangmanClient(String addr, String port) throws IOException {
        InetAddress ipa = null;
        int portI = 107;
        try {
            ipa = InetAddress.getByName(addr);
        } catch (UnknownHostException u) {
            System.out.println("Address could not be resolved. Using default value (localhost).");
            ipa = InetAddress.getByName(null);
        }
        try {
            portI = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port specified. Using default port (107).");
        }
        this.server = new Socket(ipa, portI);
        this.toServer = new BufferedWriter(new OutputStreamWriter(this.server.getOutputStream())); //damit Client dem Server senden kann.
        this.fromServer = new BufferedReader(new InputStreamReader(this.server.getInputStream())); //damit Client vom Server empfangen kann.
    }

    public void start() {
        Scanner scan = new Scanner(System.in); //Mit dem werden Inputs gelesen.
        loop: while(true) {
            try {
                do {
                    String msg = this.fromServer.readLine();
                    if (msg.equals("-END-")) break;
                    if (msg.equals("GAME OVER")) break loop;
                    System.out.println(msg);
                } while (true);
                System.out.print("Input: ");
                String input = scan.nextLine();
                toServer.write(input + "\r\n");
                toServer.flush(); //Damit die Nachricht direkt versendet wird.
            } catch (IOException e) {
                if (e instanceof SocketException) System.out.println("Connection reset by server.");
                break;
            }
        }
        try {
            this.toServer.close();
            this.fromServer.close();
            this.server.close();
        } catch (IOException e) {
            if (e instanceof SocketException) System.out.println("Connection reset by server.");
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) args = new String[]{"localhost", "107"};
        try {
            HangmanClient client = new HangmanClient(args[0], args[1]);
            client.start();
        } catch (IOException i) {
            System.out.println("Failed to start client.");
            if (i instanceof ConnectException) System.out.println("Connection refused - is the server running?");
        }
    }
}
