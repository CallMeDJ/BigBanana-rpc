package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloService extends Remote {
	Integer hello(String str) throws RemoteException;
}
