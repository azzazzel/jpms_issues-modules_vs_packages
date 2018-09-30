package test.jpms.mod.version.change.hello.runtime;

import test.jpms.mod.version.change.hello.service.HelloService;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("JPMS was wired properly! Ready to rock! ");
		
		new HelloService("JPMS").print();
	}
}
