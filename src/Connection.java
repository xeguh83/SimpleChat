import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

/**
 * Created by �������� ������
 * on 07.12.2016.
 */
public class Connection extends Thread {
    private final Socket client;
    private final Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String name = "";

    public Connection(Socket client, Server server) {
        this.client = client;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    /**
     * ??????????? ??? ???????????? ? ??????? ?? ???? ?????????. ???
     * ????????? ??????? ?????????, ??? ?????? ? ?????? ????????????
     * ???????????? ???? ?????????.
     */
    @Override
    public void run() {
        try {

            String str;

            while (true) {
                str = in.readLine();
                if(str.equals("exit")) break;

                // ?????????? ???? ???????? ????????? ?????????
                synchronized(server.getConnections()) {
                    Iterator<Connection> iter = server.getConnections().iterator();
                    while(iter.hasNext()) {
                        ((Connection) iter.next()).out.println(name + ": " + str);
                    }
                }
            }

            synchronized(server.getConnections()) {
                for (Connection connection : server.getConnections()) {
                    (connection).out.println(name + " has left");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * ????????? ??????? ? ???????? ?????? ? ?????
     */
    public void close() {
        try {
            in.close();
            out.close();
            client.close();

            // ???? ?????? ?? ???????? ??????????, ????????? ??, ??? ???? ?
            // ????????? ?????? ???????
            server.getConnections().remove(this);
            if (server.getConnections().size() == 0) {
                server.closeAll();
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("?????? ?? ???? ???????!");
        }
    }
}