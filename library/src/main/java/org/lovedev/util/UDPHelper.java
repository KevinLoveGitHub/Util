package org.lovedev.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Kevin
 * @date 2018/4/18
 */
public class UDPHelper {

    private static final String TAG = "UDPHelper";

    public static void sendUDPMessage(final String message, final String address, final int port) {
        int activeCount = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getActiveCount();
        int corePoolSize = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getCorePoolSize();
        int largestPoolSize = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getLargestPoolSize();
        long taskCount = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getTaskCount();

        LogUtils.i(TAG, "activeCount: " + activeCount + " corePoolSize: " + corePoolSize + " largestPoolSize: " + largestPoolSize + " " +
                "taskCount: " + taskCount);
        ExecutorHelpers.getNetworkIO().execute(new Runnable() {

            @Override
            public void run() {
                LogUtils.d(TAG, "sendUDPMessage: " + message + " : " + address + " : " + port);
                byte[] buf = message.getBytes();
                DatagramSocket sendSocket = null;
                try {
                    sendSocket = new DatagramSocket();
                    InetAddress serverAddr = InetAddress.getByName(address);
                    DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr, port);
                    sendSocket.send(outPacket);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (sendSocket != null) {
                    sendSocket.close();
                }
            }
        });
    }

    public static void sendUDPMessage(final String message, final String address, final int port, final MessageListener listener) {
        int activeCount = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getActiveCount();
        int corePoolSize = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getCorePoolSize();
        int largestPoolSize = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getLargestPoolSize();
        long taskCount = ((ThreadPoolExecutor) ExecutorHelpers.getNetworkIO()).getTaskCount();

        LogUtils.i(TAG, "activeCount: " + activeCount + " corePoolSize: " + corePoolSize + " largestPoolSize: " + largestPoolSize + " " +
                "taskCount: " + taskCount);
        ExecutorHelpers.getNetworkIO().execute(new Runnable() {

            @Override
            public void run() {
                LogUtils.d(TAG, "sendUDPMessage: " + message + " : " + address + " : " + port);
                byte[] buf = message.getBytes();
                DatagramSocket sendSocket = null;
                try {
                    sendSocket = new DatagramSocket();
                    InetAddress serverAddr = InetAddress.getByName(address);
                    DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr, port);
                    sendSocket.send(outPacket);
                    listener.onComplete();
                } catch (SocketException e) {
                    e.printStackTrace();
                    listener.onError(e);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    listener.onError(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onError(e);
                }
                if (sendSocket != null) {
                    sendSocket.close();
                }
            }
        });
    }


    public static void openUDPPort(final int port, final UDPMessageListener listener) {
        ExecutorHelpers.getNewCachedThreadPool().execute(new Runnable() {
            DatagramSocket mDatagramSocket;

            @Override
            public void run() {
                // 定义每个数据报的最大大小为4KB
                final int dataLen = 4096;
                // 定义接收网络数据的字节数组
                byte[] inBuff = new byte[dataLen];
                // 以指定字节数组创建准备接收数据的DatagramPacket对象
                DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
                // 创建DatagramSocket对象
                try {
                    if (mDatagramSocket == null) {
                        mDatagramSocket = new DatagramSocket(null);
                        mDatagramSocket.setReuseAddress(true);
                        mDatagramSocket.bind(new InetSocketAddress(port));
                    }
                    // 采用循环接收数据
                    while (mDatagramSocket != null && !mDatagramSocket.isClosed()) {
                        // 读取Socket中的数据，读到的数据放入inPacket封装的数组里
                        mDatagramSocket.receive(inPacket);
                        String msg = new String(inBuff, 0, inPacket.getLength());
                        LogUtils.i(TAG, msg);
                        listener.onMessageArrived(msg);
                    }
                } catch (IOException e) {
                    LogUtils.d(TAG, "receive message: " + e.getMessage());
                    listener.onError(e);
                }
            }
        });
    }

    public interface UDPMessageListener {
        void onMessageArrived(String message);

        void onError(Exception e);
    }

    /**
     * 发送消息的回调方法
     */
    public interface MessageListener {

        /**
         * 发送完成
         */
        void onComplete();

        /**
         * 发送出错
         *
         * @param e 错误信息
         */
        void onError(Exception e);
    }
}
