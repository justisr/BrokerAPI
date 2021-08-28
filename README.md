# BrokerAPI - A transaction abstraction library 

An abstraction layer enabling communication between unlimited unknown callers and unlimited unknown implementations which facilitate transactions of any object, with integrated pre and post transaction events, as well as end-user control over implementation inclusion, prioritization and greediness.

It's a lightweight but powerful library for instantly enabling extensive cross compatibility and flexibility without the overhead, 3rd party dependencies, version reliance, or licensing burdens and conflicts.

## Include with Maven
[![](https://jitci.com/gh/justisr/BrokerAPI/svg)](https://jitci.com/gh/justisr/BrokerAPI) 
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.justisr</groupId>
        <artifactId>BrokerAPI</artifactId>
        <version>master-SNAPSHOT</version>
    </dependency>
</dependencies>
```
Using `master-SNAPSHOT` in place of a version will always provide you the latest build for the master branch. Alternatively, use the short form of your target commit hash.

### JavaDocs 
Here you can find the latest version of BrokerAPI’s public [JavaDocs](http://jitpack.io/com/github/justisr/BrokerAPI/latest/javadoc/).

## Implementations
Broker currently has a [Spigot](https://github.com/justisr/Broker-Spigot) server implementation and a Sponge server implementation is in the works.
This library was designed with the present needs of the Minecraft plugin development community in mind, but with outside future use potential in heart.

Seek details and examples of API usage in the README of the implementing project relevant to you.

## Contributing
Public classes and methods should ensure JavaDoc validity and maintain backwards compatibility at all times. For major changes, please create an issue to propose your idea.

Libraries specific to individual implementations should be avoided. Implementation details belong in the repo dedicated to that specific implementation of the API.


## License
Copyright (C) 2020 Justis Root justis.root@gmail.com
([MIT License](https://choosealicense.com/licenses/mit/))

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.