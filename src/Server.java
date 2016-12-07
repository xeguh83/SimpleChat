import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by например Андрей
 * on 07.12.2016.
 */
public class Server {


    public static final int PORT = 80;

    private final List<Connection> connections =
            Collections.synchronizedList(new ArrayList<>());
    private ServerSocket server;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        Server server = new Server();
        server.getNewConnection();

    }

    private void getNewConnection() throws NoSuchAlgorithmException, IOException {

        server = new ServerSocket(PORT);

        System.out.println("Server has started on 127.0.0.1:80.\r\nWaiting for a connection...");

        Socket client = server.accept();

        System.out.println("A client connected.");


        InputStream in = client.getInputStream();

        OutputStream out = client.getOutputStream();

        //translate bytes of request to string
        String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();

        Matcher get = Pattern.compile("^GET").matcher(data);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + DatatypeConverter
                    .printBase64Binary(
                            MessageDigest
                                    .getInstance("SHA-1")
                                    .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                            .getBytes("UTF-8")))
                    + "\r\n\r\n")
                    .getBytes("UTF-8");


            out.write(response, 0, response.length);

            Connection con = new Connection(client, this);
            connections.add(con);
            con.start();
        } else {

        }
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void closeAll() {
        try {
            this.server.close();

            // ??????? ???? Connection ? ????? ?????? close() ??? ???????. ????
            // synchronized {} ????????? ??? ??????????? ??????? ? ????? ??????
            // ?? ?????? ?????
            synchronized(connections) {
                for (Connection connection : connections) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            System.err.println("?????? ?? ???? ???????!");
        }

    }
}
