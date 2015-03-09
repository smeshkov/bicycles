#!/bin/sh

JAVA_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
JAVA_AGENT="-javaagent:jarpath[=options]"
LOGGING="-Dlog4j.configuration=log4j-production.xml"
JAVA_CUSTOM_JVM_ARGS="-Djava.library.path=/usr/lib/jni"
JAVA_TUNE="-XX:+DoEscapeAnalysis -XX:+AggressiveOpts -XX:+OptimizeStringConcat -XX:+EliminateLocks -XX:+UseFastAccessorMethods -XX:+TieredCompilation -XX:+UseCompressedOops -Djava.net.preferIPv4Stack=true -Xverify:none -Xoptimize -XX:+UseNUMA"
