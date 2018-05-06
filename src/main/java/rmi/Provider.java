package rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Provider {

	public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException {


		scheduledExecutorService.schedule(new Runnable() {
			public void run() {
				try {
					HelloService helloService  = new HelloServiceImpl();
					Registry registry = LocateRegistry.createRegistry(8888);
					Naming.bind("rmi://127.0.0.1:8888/hello",helloService);
				} catch (RemoteException e) {
					e.printStackTrace();
				}catch (AlreadyBoundException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				System.out.println(">>>>>INFO:远程IHello对象绑定成功！");

			}
		} , 0, TimeUnit.NANOSECONDS);

	}
}
