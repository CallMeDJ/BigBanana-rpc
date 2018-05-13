package com.banana.rpc;


import com.big.rpc.commom.HelloService;

import java.rmi.RemoteException;

public class Provider implements HelloService,BRPC {
	@Override
	public Integer hello(String str) throws RemoteException {
		return str.hashCode();
	}

	@Override
	public String getName() {
		return "provider";
	}

	@Override
	public Class getInterface() {
		return HelloService.class;
	}

}