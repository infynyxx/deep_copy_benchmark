deep_copy_benchmark
===================

Micro benchmark comparing deep copy using [Kryo](https://github.com/EsotericSoftware/kryo#copyingcloning) and standard JDK output stream powered by [open-jmh](http://hg.openjdk.java.net/code-tools/jmh).

In oder to compile / run:

```
$ mvn clean package
$ java -jar ./target/deep_copy_benchmark-1.0-SNAPSHOT.jar
```