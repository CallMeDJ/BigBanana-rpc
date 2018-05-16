package com.banana.rpc;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TestCallBack {

	public static ExecutorService scheduledExecutorService = Executors.newFixedThreadPool(10);


	public static Map<String,MessageCallBack> messageCallBackMap = new ConcurrentHashMap<>();


	public static void main(String[] args) throws InterruptedException {
		for(int i = 0 ; i < 10 ; i++){
			final int j = i;
			scheduledExecutorService.submit( ()-> {
				MessageCallBack messageCallBack = new MessageCallBack();
				messageCallBackMap.put(j + "", messageCallBack);
				try {
					System.out.println(messageCallBack.call());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}


		Scanner scanner = new Scanner(System.in);

		while (scanner.hasNext()){
			String str = scanner.nextLine();
			String[] responseMessage = str.split(" ");

			String messageId = responseMessage[0];
			String messageResponse = responseMessage[1];

			MessageCallBack messageCallBack = messageCallBackMap.get(messageId);

			if(messageCallBack != null){
				messageCallBack.over(messageResponse);
			}

		}




	}


}
