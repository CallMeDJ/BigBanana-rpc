import java.io.Serializable;

public class HelloServiceImpl implements HelloService,Serializable {
	public Integer hello(String str) {
		return str.hashCode();
	}
}
