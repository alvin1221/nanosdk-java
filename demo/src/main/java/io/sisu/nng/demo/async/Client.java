package io.sisu.nng.demo.async;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Client {
    private final String url;
    private final int millis;

    public Client(String url, int millis) {
        this.url = url;
        this.millis = millis;
    }

    public void run() throws NngException {
        long start, end;

        Socket sock = new Req0Socket();
        sock.dial(url);

        start = System.currentTimeMillis();

        Message msg = new Message();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putInt(millis);
        buffer.flip();
        msg.append(buffer);

        sock.sendMessage(msg);

        msg = sock.receiveMessage();

        end = System.currentTimeMillis();
        sock.close();

        System.out.println(String.format("Request took %d milliseconds.", end - start));
    }

    public static void main(String argv[]) {
        if (argv.length != 2) {
            System.err.println(String.format("Usage: client <url> <msecs>"));
            System.exit(1);
        }

        try {
            Client client = new Client(argv[0], Integer.parseUnsignedInt(argv[1]));
            client.run();
        } catch (NngException nngErr) {
            System.err.println(nngErr);
        }
    }
}