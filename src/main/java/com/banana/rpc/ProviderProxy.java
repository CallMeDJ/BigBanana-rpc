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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *  Provider 提供服务的代理
 */
public class ProviderProxy {
	/**
	 * 所提供的所有服务的引用。
	 */
	public static Map<String,ProviderModel> services = new HashMap<String, ProviderModel>();

	private static String  getServiceName(String interfaceName , String methodName){
		return interfaceName+"-BRPC-"+methodName;
	}

	public static ExecutorService executorService = Executors.newFixedThreadPool(6);

	@Override
	protected void finalize() throws Throwable {
		executorService.shutdownNow();
		super.finalize();
	}

	public static void  initServices(){
		/**
		 * 初始化服务列表，相当于服务注册
		 */
		Provider provider = new Provider();
		ProviderModel providerModel = new ProviderModel();
		providerModel.setObject(provider);
		providerModel.setClassName(Provider.class);

		Class anInterface = provider.getInterface();

		/**
		 * 统一服务名称为：服务类名-BRPC-方法名。如  com.banana.rpc.Provide-BRPC-hello
		 */
		Method[] methods = anInterface.getMethods();
		for(Method method : methods){
			services.put(getServiceName(anInterface.getName(),method.getName()) , providerModel);
		}
	}

	static {

		initServices();

		/**
		 * 这里开始对外提供服务，这里使用Socket的方式提供
		 */
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(8888);
			Socket accept = serverSocket.accept();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
			while (true) {

				try {
					accept.sendUrgentData(0);
				} catch (IOException e) {
					accept = serverSocket.accept();
					bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
				}


				final String requestStr = bufferedReader.readLine();
				if(requestStr == null){continue;}

					try {

						/**
						 * 请求的序列化模式为JSON，这在Consumer 端也是一样的约定。
						 */
						System.out.println("receive request "+requestStr);
						JSONObject request = JSON.parseObject(requestStr);

						String requestId = request.getString("requestId");
						String interfaceName = request.getString("interfaceName");
						String methodName = request.getString("method");
						JSONArray param = request.getJSONArray("param");

						Class[] argClasses = new Class[param.size()];

						argClasses = param.stream().map(x -> x.getClass()).collect(Collectors.toList()).toArray(argClasses);


						/**
						 * 找到对应的服务
						 */

						ProviderModel currentService = services.get(getServiceName(interfaceName, methodName));


						/**
						 * 找到服务对应的方法，然后用反射去调用
						 */

						Class className = currentService.getClassName();

						Method method = className.getMethod(methodName, argClasses);

						/**
						 * 将调用结果进行JSON化，然后回写到 Socket 中返回。
						 */


						JSONObject result = new JSONObject();
						result.put("requestId", requestId);
						result.put("result", method.invoke(currentService.getObject(), param.toArray()));

						String invoke = result.toJSONString();

						System.out.println("send response "+invoke);
						BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
						/**
						 * 因为我们使用了readLine，所以强行加上了\n来表示结束，这在Consumer 端也是一样的约定。
						 */
						bufferedWriter.write(invoke + "\n");

						bufferedWriter.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}

			}

		} catch (IOException e) {
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
