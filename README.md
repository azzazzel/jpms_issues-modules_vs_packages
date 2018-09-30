# Shortcomings of JMPS dependencies

This demo project aims to demonstrate the shortcomings of the decision to express dependencies between modules, rather than between packages in JPMS.

## The demo code

The demo is as simple as it gets. It contains two libraries
`hello-runtime` and `hello-service`. Assume those are developed and maintained by different and unrelated teams/organizations and have totally different release cycles. In this demo we have 1 version of `hello-runtime` and 3 versions of `hello-service` _(the initial one plus 2 that illustrate potential future changes)_. Thus there are 4 Maven projects:

 - [hello-service](./hello-service) contains the `test.jpms.mod.version.change.hello.service` module which contains and exports one package with the same name which contains one class `HelloService`.
 - [hello-runtime](./hello-runtime) contains the `test.jpms.mod.version.change.hello.runtime` module which contains one package with the same name which contains one class `Main` which uses `HelloService` _(build against the initial version of `hello-service`)_.
  - [hello-service-2](./hello-service-2) is a future version of `hello-service` with only one change - the **package name has changed** to `test.jpms.mod.version.change.hello.service.changed`.
  - [hello-service-3](./hello-service-3) is a future version of `hello-service` with only one change - the **module name has changed** to `test.jpms.mod.version.change.hello.service.changed`.

All of them are Maven projects that can be build in a standard Maven way. Make sure to 

 - install `hello-service` in your local repo before you can build `hello-runtime`.
 - [configure your toolchain](https://maven.apache.org/guides/mini/guide-using-toolchains.html) to use Java 10

## All works just fine initially

The [run.sh](./run.sh) will start `hello-runtime` with `hello-service` on module path. As expected you'll see:

    JPMS was wired properly! Ready to rock!
    Hello JPMS

## Package name change

Now assume the maintainer of `hello-service` has refactored the code and changed the package name. Later on you try to run `hello-runtime` with `hello-service-2`. With a module system in place you may expect it will warn you of the incompatibility _(that's what OSGi would do)_. However since JPMS handles dependencies between modules and not packages, it will resolve just fine and the actual error will pop up to you at runtime _(according to Murphy's law - at peak time in a place not covered by tests)_! To demonstrate that, run [run2.sh](./run2.sh) and you'll see:

    JPMS was wired properly! Ready to rock!
    Exception in thread "main" java.lang.NoClassDefFoundError: test/jpms/mod/version/change/hello/service/HelloService
	    at test.jpms.mod.version.change.hello.runtime/test.jpms.mod.version.change.hello.runtime.Main.main(Main.java:11)
    Caused by: java.lang.ClassNotFoundException: test.jpms.mod.version.change.hello.service.HelloService
	    at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:582)
	    at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:190)
	    at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:499)
	    ... 1 more

## Module name change

Now assume the maintainer of `hello-service` has refactored the code and changed the module name. Later on you try to run `hello-runtime` with `hello-service-3`. 

You may expect that to work just fine. After all _(in general but not per JPMS rules)_ a module is nothing more than a group of packages and services with proper metadata. What it is called should be irrelevant for as long as the name is unique in the context of given modular runtime _(which is the case in OSGi)_.  

However since JPMS handles dependencies between modules and not packages, the module names are glorified and hardcoded as dependencies inside `module-info.java` files that need to be compiled. 

This is why if you run [run3.sh](./run3.sh) you'll see:

    Error occurred during initialization of boot layer
    java.lang.module.FindException: Module test.jpms.mod.version.change.hello.service not found, required by test.jpms.mod.version.change.hello.runtime

## Workarounds _(kind of)_

You may try to work around the issues by starting `hello-runtime` with more than one version of `hello-service` in your module path and let JPMS pick one. This is demonstrated by:

 - [run2.1.sh](./run2.1.sh) for the package name change. 
 - [run3.1.sh](./run3.1.sh) for the module name change. 
 
 However this approach is no different than what we had on the good old classpath. Furthermore it will not help you in the case of another module using the new package, as JPMS does not allow 2 modules with the same name to be wired in the same runtime _(unless you have custom module layers)_.