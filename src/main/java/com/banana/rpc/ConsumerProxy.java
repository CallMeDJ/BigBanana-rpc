package com.banana.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Consumer 调用的代理
 */

public class ConsumerProxy {
	public static ExecutorService executorService = Executors.newFixedThreadPool(10);
	public static Map<String,MessageCallBack> messageCallBackMap = new ConcurrentHashMap<>();
	public static Socket socket;
	public static Random random = new Random();

	@Override
	protected void finalize() throws Throwable {
		executorService.shutdownNow();
		if(socket!=null){
			socket.close();
		}
		super.finalize();
	}

	public void init(){
		try {
			socket = new Socket("127.0.0.1",8888);
			executorService.submit(()->{
				try {
					revice();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}


	}


	public Object call(Class interfaceClass, String methodName, Object[] params) throws InterruptedException, IOException {
		MessageCallBack messageCallBack = new MessageCallBack();
		String requestId = random.nextInt()+"";

		JSONObject request = new JSONObject();
		JSONArray paramArray = new JSONArray();
		for(Object param : params){
			paramArray.add(param);
		}

		request.put("interfaceName",interfaceClass.getName());

		request.put("method",methodName);
		request.put("param",paramArray);
		request.put("requestId",requestId);



		String requestStr = request.toJSONString();

		request.put("interface",interfaceClass);
		request.put("params",params);

		/**
		 * 请求的序列化模式为JSON，这在 Provider 端也是一样的约定。
		 */


		messageCallBack.setRequest(request);

		messageCallBackMap.put(requestId,messageCallBack);


		send(socket,requestStr);



		return messageCallBack.call();
	}


	public void send(Socket currentSocket , String requestStr) throws IOException {

		System.out.println("send request "+requestStr);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(currentSocket.getOutputStream()));

		bufferedWriter.write(requestStr+"\n");
		bufferedWriter.flush();
	}


	public void revice() throws IOException, NoSuchMethodException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		while (true) {

			String reqponseStr = bufferedReader.readLine();
			if(reqponseStr == null){continue;}

			System.out.println(reqponseStr);

			JSONObject response = JSON.parseObject(reqponseStr);
			String requestId = response.getString("requestId");
			MessageCallBack messageCallBack = messageCallBackMap.get(requestId);

			JSONObject request = messageCallBack.getRequest();

			Class interfaceClass = (Class)request.get("interface");
			String methodName = request.getString("method");
			JSONArray param = request.getJSONArray("param");

			Object[] params = (Object[])request.get("params");

			param.size();


			Class[] argClasses = new Class[params.length];

			argClasses = Arrays.stream(params).map(x -> x.getClass()).collect(Collectors.toList()).toArray(argClasses);


			/**
			 * 找到所调用的方法的反射
			 */
			Method method = interfaceClass.getMethod(methodName,argClasses);

			/**
			 * 对返回结果进行反序列化
			 */
			Object result = response.get("result");

			messageCallBack.over(result);

		}
	}



}
