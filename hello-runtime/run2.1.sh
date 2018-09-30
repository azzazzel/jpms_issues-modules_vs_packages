java \
	-p target/hello-runtime-0.0.1-SNAPSHOT.jar:../hello-service/target/hello-service-0.0.1-SNAPSHOT.jar:../hello-service-2/target/hello-service-2-0.0.1-SNAPSHOT.jar \
	-m test.jpms.mod.version.change.hello.runtime/test.jpms.mod.version.change.hello.runtime.Main