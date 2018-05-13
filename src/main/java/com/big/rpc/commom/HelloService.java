package com.big.rpc.commom;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloService  {
	Integer hello(String str) throws RemoteException;
}
