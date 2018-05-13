package com.banana.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Consumer 调用的代理
 */

public class ConsumerProxy {
	public Object call(Class interfaceClass, String methodName, Object[] params){

		Object result = null;
		try (Socket socket = new Socket("127.0.0.1",8888)) {

			/**
			 * 构造请求参数
			 */
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


			JSONObject request = new JSONObject();
			JSONArray paramArray = new JSONArray();
			for(Object param : params){
				paramArray.add(param);
			}

			request.put("interfaceName",interfaceClass.getName());
			request.put("method",methodName);
			request.put("param",paramArray);


			/**
			 * 请求的序列化模式为JSON，这在 Provider 端也是一样的约定。
			 */

			String requestStr = request.toJSONString();

			bufferedWriter.write(requestStr+"\n");
			bufferedWriter.flush();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String reqponseStr = bufferedReader.readLine();

			Class[] argClasses = new Class[params.length];

			argClasses = Arrays.stream(params).map(x -> x.getClass()).collect(Collectors.toList()).toArray(argClasses);


			/**
			 * 找到所调用的方法的反射
			 */
			Method method = interfaceClass.getMethod(methodName,argClasses);

			/**
			 * 对返回结果进行反序列化
			 */
			result = JSON.parseObject(reqponseStr, method.getReturnType());


		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return result;
	}
}
