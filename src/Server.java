import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by �������� ������
 * on 04.12.2016.
 */
public class Server {

    private static final int PORT = 8283;
    /**
     * ����������� "������" ��� ArrayList, ������� ������������ ������ �
     * ������� �� ������ �����
     */
    private final List<Connection> connections =
            Collections.synchronizedList(new ArrayList<>());
    private ServerSocket server;

    public static void main(String[] args) {
        new Server();
    }

    /**
     *
     * ����������� ������ ������. ����� ��� ������� ����������� ��������
     * ������ Connection � ��������� ��� � ������ �����������.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public Server() {
        try {
            server = new ServerSocket(PORT);

            while (true) {
                Socket socket = server.accept();

                // ������ ������ Connection � ��������� ��� � ������
                Connection con = new Connection(socket);
                connections.add(con);

                // �������������� ���� � ��������� ����� run(),
                // ������� ����������� ������������ � ��������� ����������
                con.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    /**
     * ��������� ��� ������ ���� ���������� � ����� ��������� �����
     */
    private void closeAll() {
        try {
            server.close();

            // ������� ���� Connection � ����� ������ close() ��� �������. ����
            // synchronized {} ��������� ��� ����������� ������� � ����� ������
            // �� ������ �����
            synchronized(connections) {
                for (Connection connection : connections) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            System.err.println("������ �� ���� �������!");
        }
    }


    private class Connection extends Thread {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name = "";

        public Connection(Socket socket) {
            this.socket = socket;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }

        /**
         * ����������� ��� ������������ � ������� �� ���� ���������. ���
         * ��������� ������� ���������, ��� ������ � ������ ������������
         * ������������ ���� ���������.
         */
        @Override
        public void run() {
            try {
                name = in.readLine();
                // ���������� ���� �������� ��������� � ���, ��� ����� ����� ������������
                synchronized(connections) {
                    Iterator<Connection> iter = connections.iterator();
                    while(iter.hasNext()) {
                        ((Connection) iter.next()).out.println(name + " cames now");
                    }
                }
                String str;

                while (true) {
                    str = in.readLine();
                    if(str.equals("exit")) break;

                    // ���������� ���� �������� ��������� ���������
                    synchronized(connections) {
                        Iterator<Connection> iter = connections.iterator();
                        while(iter.hasNext()) {
                            ((Connection) iter.next()).out.println(name + ": " + str);
                        }
                    }
                }

                synchronized(connections) {
                    for (Connection connection : connections) {
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
         * ��������� ������� � �������� ������ � �����
         */
        public void close() {
            try {
                in.close();
                out.close();
                socket.close();

                // ���� ������ �� �������� ����������, ��������� ��, ��� ���� �
                // ��������� ������ �������
                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("������ �� ���� �������!");
            }
        }
    }
}
