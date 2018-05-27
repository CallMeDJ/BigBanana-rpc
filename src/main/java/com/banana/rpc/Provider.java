package com.banana.rpc;


import com.big.rpc.commom.HelloService;

import java.rmi.RemoteException;

/**
 * 定义一个 Provider
 */

public class Provider implements HelloService,BRPC {
	@Override
	public Integer hello(String str) {
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
