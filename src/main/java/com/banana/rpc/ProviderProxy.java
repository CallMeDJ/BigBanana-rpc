package com.banana.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProviderProxy {
	public static Map<String,ProviderModel> services = new HashMap<String, ProviderModel>();

	private static String  getServiceName(String interfaceName , String methodName){
		return interfaceName+"-BRPC-"+methodName;
	}

	static {
		Provider provider = new Provider();
		ProviderModel providerModel = new ProviderModel();
		providerModel.setObject(provider);
		providerModel.setClassName(Provider.class);

		Class anInterface = provider.getInterface();

		Method[] methods = anInterface.getMethods();
		for(Method method : methods){
			services.put(getServiceName(anInterface.getName(),method.getName()) , providerModel);
		}




		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(8888);

			while (true){
				Socket accept = serverSocket.accept();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));

				String requestStr = bufferedReader.readLine();

				JSONObject request = JSON.parseObject(requestStr);

				String interfaceName = request.getString("interfaceName");
				String methodName = request.getString("method");
				JSONArray param = request.getJSONArray("param");

				Class[] argClasses = new Class[param.size()];

				argClasses = param.stream().map(x -> x.getClass()).collect(Collectors.toList()).toArray(argClasses);
				

				ProviderModel currentService = services.get(getServiceName(interfaceName, methodName));


				Class className = currentService.getClassName();

				Method method = className.getMethod(methodName, argClasses);

				String invoke = JSON.toJSONString(method.invoke(currentService.getObject(), param.toArray()));


				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
				bufferedWriter.write(invoke+"\n");

				bufferedWriter.flush();
			}


		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			if(serverSocket != null){
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


	public static void main(String[]  args){
		scheduledExecutorService.schedule((Runnable) () -> {

		}
		,1, TimeUnit.DAYS);

	}



}
