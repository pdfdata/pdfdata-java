# pdfdata-java ![](https://travis-ci.org/pdfdata/pdfdata-java.svg?branch=master)

A Java client library for [PDFDATA.io](https://www.pdfdata.io), the API for
PDF data extraction.

PDFDATA.io is designed to be incredibly easy to use while providing impeccable
PDF data extraction quality over a range of configurable extraction targets
(text, forms, metadata, images, tables, and more all the time). While
PDFDATA.io's API is itself an approachable HTTP+JSON affair, `pdfdata-java`
is an idiomatic client library that any Java developer can have up and running
in less than a minute.

While `pdfdata-java` is written in Java, its API is intended to be easy
to use from any programming language that runs on the JVM and provides
reasonable Java-language interoperability, including Scala, Clojure, Groovy,
Kotlin, JRuby, Jython, and others.

<strong>For detailed documentation and extensive examples, head over to our
[API docs](https://www.pdfdata.io/apidoc/).</strong>

## Quick Start

pdfdata-java requires JDK 1.8 or greater.

### Installation

`pdfdata-java` is available in Maven Central. You can thus easily add it to your
project by adding its Maven coordinates to the dependencies section of your
build tool's configuration file:

#### Maven

```xml
<dependency>
  <groupId>io.pdfdata</groupId>
  <artifactId>pdfdata-java</artifactId>
  <version>0.2.7</version>
</dependency>
```

#### sbt

```
libraryDependencies += "io.pdfdata" % "pdfdata-java" % "0.9.2"
```

#### Gradle

```
compile 'io.pdfdata:pdfdata-java:0.9.2'
```

#### Leiningen

```
[io.pdfdata/pdfdata-java "0.9.2"]
```

### Usage

You will need a PDFDATA.io API key to use this library. (If you don't have one
already, you can get one free by
[registering](https://www.pdfdata.io/register).)

#### Credentials

First, you'll need to plug in your PDFDATA.io API key; there are two ways you
can do this. Either provide it as a constructor argument to the root
`io.pdfdata.API` class:

```java
io.pdfdata.API pdfdata = new io.pdfdata.API("test_YOUR_API_KEY_HERE");
```

Or, you can set the `PDFDATA_APIKEY` environment variable (<em>OR</em> JVM system
property) appropriately for your operating system, e.g.:

```sh
export PDFDATA_APIKEY=test_YOUR_API_KEY_HERE
```

OR

```sh
java -DPDFDATA_APIKEY=test_YOUR_API_KEY_HERE [...remaining application arguments...]
```

and then omit the extra argument when requiring `pdfdata`:

```java
io.pdfdata.API pdfdata = new io.pdfdata.API();
```

#### Running a proc (data extraction process)

Assuming you have a PDF document `test.pdf` in your current directory which
contains text you'd like to extract:

```java
```

This will yield something like this:

```java
```

There are many different data extraction operations available; unstructured text
as is shown above, as well as access to bitmap image data, metadata, and
structured data options like forms, and custom named-region page template
extractions.

### Learn more

Check out our
[API documentation](https://www.pdfdata.io/apidoc/), which includes a tonne of
examples, descriptions of all of the data extraction operations PDFDATA.io
offers, and details about important things like data retention policies, usage
limits, and so on.

Questions? We're on Twitter [@pdfdataio](https://twitter.com/pdfdataio), or you
can [contact us](https://www.pdfdata.io/page/contact) otherwise.

## Testing

(This is only relevant if you are modifying / contributing to `pdfdata-node`.)

Set your environment, e.g.:

```
export PDFDATA_APIKEY=<YOUR API KEY>
export PDFDATA_ENDPOINT=https://localhost:8081/v1
```

`PDFDATA_ENDPOINT` defaults to `https://api.pdfdata.io/v1`.

Run the tests via `mvn test`.

## License

MIT.
