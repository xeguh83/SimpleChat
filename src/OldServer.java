import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by �������� ������
 * on 04.12.2016.
 */
public class OldServer {

    private static final int PORT = 8283;
    /**
     * ����������� "������" ��� ArrayList, ������� ������������ ������ �
     * ������� �� ������ �����
     */
    private final List<Connection> connections =
            Collections.synchronizedList(new ArrayList<>());
    private ServerSocket server;

    public static void main(String[] args) {
        new OldServer();
    }

    /**
     *
     * ����������� ������ ������. ����� ��� ������� ����������� ��������
     * ������ Connection � ��������� ��� � ������ �����������.
     */
//    @SuppressWarnings("InfiniteLoopStatement")
//    public OldServer() {
//        try {
//            server = new ServerSocket(PORT);
//
//            while (true) {
//                Socket socket = server.accept();
//
//                // ������ ������ Connection � ��������� ��� � ������
//                Connection con = new Connection(socket, server);
//                connections.add(con);
//
//                // �������������� ���� � ��������� ����� run(),
//                // ������� ����������� ������������ � ��������� ����������
//                con.start();
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            closeAll();
//        }
//    }

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

}
