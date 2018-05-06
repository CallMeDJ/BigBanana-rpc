package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

	public Integer hello(String str) {
		return str.hashCode();
	}


	protected HelloServiceImpl() throws RemoteException {
		super();
	}
}
