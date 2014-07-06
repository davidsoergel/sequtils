sequtils
========

_A Java library for dealing with biological sequences_

 * Provides a few simple APIs and utility classes having to do with biological sequences and sequence metadata.

Documentation
-------------

 * [API docs](http://davidsoergel.github.io/sequtils/)

Download
--------

[Maven](http://maven.apache.org/) is by far the easiest way to make use of dsutils.  Just add these to your pom.xml:
```xml
<repositories>
	<repository>
		<id>dev.davidsoergel.com releases</id>
		<url>http://dev.davidsoergel.com/nexus/content/repositories/releases</url>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
	</repository>
	<repository>
		<id>dev.davidsoergel.com snapshots</id>
		<url>http://dev.davidsoergel.com/nexus/content/repositories/snapshots</url>
		<releases>
			<enabled>false</enabled>
		</releases>
	</repository>
</repositories>

<dependencies>
	<dependency>
		<groupId>com.davidsoergel</groupId>
		<artifactId>sequtils</artifactId>
		<version>0.9</version>
	</dependency>
</dependencies>
```

If you really want just the jar, you can get the [latest release](http://dev.davidsoergel.com/nexus/content/repositories/releases/com/davidsoergel/sequtils/) from the Maven repo; or get the [latest stable build](http://dev.davidsoergel.com/jenkins/job/dsutils/lastStableBuild/com.davidsoergel$sequtils/) from the build server.

