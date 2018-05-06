package rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Consumer {
	public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException, NotBoundException {

		String rmi = "rmi://127.0.0.1:8888/hello";

		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {

			Registry registry = LocateRegistry.getRegistry(8888);
			HelloService masterService = (HelloService)registry.lookup("hello");


			String command = scanner.nextLine();
			System.out.println(masterService.hello(command));
		}

	}
}
