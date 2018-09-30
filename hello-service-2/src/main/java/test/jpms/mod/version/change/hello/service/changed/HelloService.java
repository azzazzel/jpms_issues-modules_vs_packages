package test.jpms.mod.version.change.hello.service.changed;

public class HelloService {

	String name;
	
	public HelloService(String name) {
		this.name = name;
	}
	
	public void print() {
		System.out.println("Hello " + name);
	}
}
