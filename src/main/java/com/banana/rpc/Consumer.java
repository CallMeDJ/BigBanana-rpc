package com.banana.rpc;

import com.big.rpc.commom.HelloService;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定义一个Consumer
 */

public class Consumer {
	public static ExecutorService executorService = Executors.newFixedThreadPool(10);
	public static HelloService helloService = (HelloService) Proxy.newProxyInstance(HelloService.class.getClassLoader(), new Class[]{HelloService.class},
			new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					Object call = Consumer.consumerProxy.call(proxy.getClass(), method.getName(), args);
					return call;
				}
			});
	public static 	ConsumerProxy consumerProxy = new ConsumerProxy();
	public static Random random = new Random();

	@Override
	protected void finalize() throws Throwable {
		executorService.shutdownNow();
		super.finalize();
	}

	static {
		consumerProxy.init();
	}

	public static void main(String args[]){
		Scanner scanner = new Scanner(System.in);



for(int i = 0 ; i<10 ; i++) {
	String line = random.nextLong()+"";
	executorService.submit(() -> {
		Object[] params = new Object[1];
		params[0] = line;
		Object hello = null;
			hello = helloService.hello(line);

	});


}


	}

}
