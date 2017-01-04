package com.bestpay.posp.utils.crypto.essc.communicate;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class ConnectPool {
	private String HsmHost = "";
	private int HsmPort = 0; // 密码机服务端口
	private static ConnectPool instance = null;
	private PriorityBlockingQueue pbQueue = null;
	private ClientPCollator clientCollator = null;
	private int count = 0;
	private static Object lock = new Object();

	public static ConnectPool getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ConnectPool();
				}

			}
		}
		return instance;
	}

	public static synchronized ConnectPool getInstance(String HsmHost, int HsmPort, int poolNumber) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new ConnectPool(HsmHost, HsmPort, poolNumber);
				}
			}
		}
		return instance;
	}

	private ConnectPool() {
		initialize(5);
	}

	private ConnectPool(String HsmHost, int HsmPort, int poolNumber) {
		this.HsmHost = HsmHost;
		this.HsmPort = HsmPort;
		initialize(poolNumber);
	}

	public void initialize(int poolNumber) { // throws Exception
		try {
			count = poolNumber;
			clientCollator = new ClientPCollator();
			pbQueue = new PriorityBlockingQueue(count + 1, clientCollator);
			for (int i = 0; i < count; i++) {
				pbQueue.put(new SocketIO(i));
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void removeConnect(SocketIO socket) { 

		pbQueue.remove(socket);
	}

	public synchronized SocketIO getConnect() {
		SocketIO socket = null;	
		try {
			socket = (SocketIO) pbQueue.take();
		} catch (Exception e) {
			e.getMessage();
			return new SocketIO();
		}
		return socket;
	}

	public void putConnect(SocketIO socket) {
		if (pbQueue.size() < 3*count) {
			pbQueue.put(socket);
		}
	}

	class ClientPCollator implements Comparator {
		public int compare(Object o1, Object o2) { // throws ClassCastException
			if (o1 instanceof SocketIO && o2 instanceof SocketIO) {
				SocketIO client1 = (SocketIO) o1;
				SocketIO client2 = (SocketIO) o2;
				int prior1 = client1.IsConnected() ? 2 : 1; // 已连接的优先级别为2，未连接为1
				int prior2 = client2.IsConnected() ? 2 : 1;

				if (prior1 > prior2) { // 优先级别高的
					return 1;
				} else if (prior1 == prior2) {
					return 0;
				} else {
					return -1;
				}
			} else {
				ClassCastException cce = new ClassCastException("比较时应输入UnionSocket类");
				throw cce;
			}
		}
	}

}
