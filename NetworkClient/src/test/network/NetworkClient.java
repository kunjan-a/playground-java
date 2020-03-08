package test.network;

import com.google.common.util.concurrent.SettableFuture;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;

public class NetworkClient {

    public static final String LOGTAG = "SocketClient";


    public static SettableFuture<ConnectionMetric> connect(final String host, final int port, final boolean useSecure, final int timeoutInSec,
                                                           final String requestString, final boolean isDoorProtocol, final ConnectionMetric metricHolder, final Proxy proxy,
                                                           final boolean invalidateSession) {
        final SettableFuture<ConnectionMetric> future = SettableFuture.create();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    Socket socket = null;

                    if (useSecure) {
                        System.out.println(LOGTAG+" - property: javax.net.debug=" + System.getProperty("javax.net.debug"));
                        System.setProperty("javax.net.debug", "all");
                        System.out.println(LOGTAG + " - property: javax.net.debug=" + System.getProperty("javax.net.debug"));
                        SSLSocketFactory factory =
                                (SSLSocketFactory) SSLSocketFactory.getDefault();
                        System.out.println(LOGTAG + " - created factory:" + factory);
                        socket = factory.createSocket();


                    } else {
                        if (proxy != null) {
                            socket = new Socket(proxy);
                        } else {
                            socket = new Socket();
                        }
                    }
                    socket.setSoTimeout(timeoutInSec * 1000);
                    System.out.println(LOGTAG + " - Created socket");

                    metricHolder.startDnsResolutionTimer();
                    final InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
                    metricHolder.finishDnsResolutionTimer(inetSocketAddress);
                    System.out.println(LOGTAG + " - DNS resolution done:" + inetSocketAddress.toString());

                    metricHolder.startTcpHandshakeTimer();
                    socket.connect(inetSocketAddress, timeoutInSec * 1000);
                    metricHolder.finishTcpHandshakeTimer();
                    System.out.println(LOGTAG + " - Connection established");

                    if (useSecure) {
                        metricHolder.startSSLHandshakeTimer();
                        ((SSLSocket) socket).startHandshake();
                        final SSLSocket sslSocket = (SSLSocket) socket;
                        sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                            @Override
                            public void handshakeCompleted(HandshakeCompletedEvent event) {
                                if (invalidateSession) {
                                    sslSocket.getSession().invalidate();
                                }
                                metricHolder.finishSSLHandshakeTimer();
                                System.out.println(LOGTAG + " - Handshake completed");
                            }
                        });
                    }


            /* send request */
                    final OutputStream out = socket.getOutputStream();
                    if (isDoorProtocol) {

                        int capacity = getLength(requestString);
                        byte[] result = getBytes(capacity, requestString);
                        out.write(result);
                        metricHolder.startResponseTimer(requestString);
                        out.flush();
                    } else {

                        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
                        printWriter.print(requestString);
                        metricHolder.startResponseTimer(requestString);
                        printWriter.flush();

                        if (printWriter.checkError()) {
                            System.out.println(LOGTAG + " -  java.io.PrintWriter error");
                            throw new RuntimeException("java.io.PrintWriter error");
                        }
                    }


            /* read response */
                    final InputStream in = socket.getInputStream();
                    if (isDoorProtocol) {
                        int messageLength = readLength(in);
                        String messageReceived = readMessageFromSocket(messageLength, in);
                        metricHolder.finishResponseTimer(messageReceived);

                    } else {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()));

                        String inputLine = bufferedReader.readLine();
                        metricHolder.finishResponseTimer(inputLine);
                    }

                    out.close();
                    in.close();
                    socket.close();

                } catch (Exception e) {
                    future.setException(e);
                } finally {
                    future.set(metricHolder);
                }
            }
        });
        return future;
    }

    private static byte[] getBytes(int capacity, String requestString) {
        final byte[] payload = requestString.getBytes();
        ByteBuffer b = ByteBuffer.allocate(capacity);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(payload.length);
        b.put(payload, 0, payload.length);

        byte[] result = new byte[capacity];
        b.rewind();
        b.get(result);
        return result;
    }

    private static int getLength(String requestString) {
        return requestString.getBytes().length + 4;
    }

    private static String readMessageFromSocket(int messageLength, InputStream socket) throws IOException {
        byte[] messageBuffer = readNBytestFromSocket(messageLength, socket);
        return new String(messageBuffer);
    }

    private static int readLength(InputStream inputStream) throws IOException {
        byte[] frameBuffer = readNBytestFromSocket(4, inputStream);
        int messageLength = getIntegerFromByteArray(frameBuffer);
        return messageLength;
    }

    public static int getIntegerFromByteArray(byte[] frameBuffer) {
        ByteBuffer lengthConversionBuffer;
        int messageLength;
        lengthConversionBuffer = ByteBuffer.wrap(frameBuffer);
        lengthConversionBuffer.order(ByteOrder.BIG_ENDIAN);

        messageLength = lengthConversionBuffer.getInt();
        return messageLength;
    }

    private static byte[] readNBytestFromSocket(int n, InputStream inputStream) throws IOException {
        byte[] messageBuffer = new byte[n];
        int messageOffset = 0;

        while (messageOffset < n) {
            int read = inputStream.read(messageBuffer, messageOffset, n - messageOffset);
            if (read > 0) {
                messageOffset += read;
            }
            if (read == -1) {
                System.out.println(LOGTAG + " - end of stream while reading message size: " + n);
                throw new IOException("end of stream");
            }
        }
        return messageBuffer;
    }

}