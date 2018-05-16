package com.banana.rpc;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageCallBack {

	private ReentrantLock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	private Object response;
	private JSONObject request;

	public JSONObject getRequest() {
		return request;
	}

	public void setRequest(JSONObject request) {
		this.request = request;
	}

	public Object call() throws InterruptedException {
		try {
			lock.lock();
			condition.await(1000000, TimeUnit.SECONDS);

			if (response != null) {
				return response;
			} else {
				throw new InterruptedException("RPC error");
			}
		}finally {
			lock.unlock();
		}


	}


	public void over(Object response){
		try {
			lock.lock();
			this.response = response;
			condition.signal();
		}
		finally {
			lock.unlock();
		}
	}
}
