package org.aliece.mine.MineClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoBioServer implements Runnable {

    private static final byte[] FIRST_BYTES = "Welcome to MineCat Server.".getBytes();

    private final ServerSocket serverSocket;

    public EchoBioServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new BioConnection(socket)).start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class BioConnection implements Runnable {

        private Socket socket;
        private InputStream input;
        private OutputStream output;
        private byte[] readBuffer;
        private byte[] writeBuffer;

        private BioConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
            this.readBuffer = new byte[4096];
            this.writeBuffer = new byte[4096];
        }

        @Override
        public void run() {
            try {
                output.write(FIRST_BYTES);
                output.flush();
                while (true) {
                    int got = input.read(readBuffer);
                    System.out.println(new String(readBuffer));
                    System.out.println(new String(writeBuffer));
                    output.write(writeBuffer, 0, got);
                    // output.flush();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                if (socket != null)
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Thread(new EchoBioServer(7878)).start();
    }


}
