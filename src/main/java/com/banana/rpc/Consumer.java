package com.banana.rpc;

import com.big.rpc.commom.HelloService;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定义一个Consumer
 */

public class Consumer {
	public static ExecutorService executorService = Executors.newFixedThreadPool(10);
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
		try {
			hello = consumerProxy.call(HelloService.class, "hello", params);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	});


}


	}

}
