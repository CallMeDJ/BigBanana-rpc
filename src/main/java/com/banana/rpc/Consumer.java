package com.banana.rpc;

import com.big.rpc.commom.HelloService;

import java.util.Scanner;

/**
 * 定义一个Consumer
 */

public class Consumer {
	public static void main(String args[]){
		Scanner scanner = new Scanner(System.in);

		while (scanner.hasNext()){
			String line = scanner.nextLine();
			ConsumerProxy consumerProxy = new ConsumerProxy();

			Object[] params = new Object[1];
			params[0] = line;
			Object hello = consumerProxy.call(HelloService.class, "hello", params);

			System.out.println(hello);

		}
	}

}
