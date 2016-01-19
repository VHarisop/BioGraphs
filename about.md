---
layout: page
title: About
permalink: /about/
---

BioGraphs builds heavily on [Jinsect][jinsect] and [JGraphT][jgrapht]. It released under the GPLv3 license.

## Requirements

* [Apache Commons][commons] v4.0
* [JGraphT][jgrapht] v0.9.1
* [BioJava][biojava] v4.0.0
* JInsect v1.0 (a release can be found [here]({{ site.baseurl }}/assets/jinsect-core-1.0b.jar)).


## Using JInsect in Maven

As of yet, JInsect is not hosted in any central maven repository, therefore you'll have to install it locally using the linked `.jar` file and the [maven-install][mvn-install] plugin.

With version 2.5 of the plugin, all required info is extracted from the .jar file, therefore it all comes down to:

{% highlight bash %}
mvn install:install-file -Dfile=<path-to-downloaded-jar>
{% endhighlight %}

If, for some reason, a version older than 2.5 is the only option, run the following command:

{% highlight bash %}
mvn install:install-file -Dfile=<path-to-downloaded-jar> \
	-DgroupId=gr.demokritos.iit.jinsect -DartifactId=jinsect-core \
	-Dversion=<version> -Dpackaging=jar
{% endhighlight %}

In the above code, `<version>` is the version indicated by the jar's filename. For example, installing `jinsect-core-1.0a.jar` means that you should use `-Dversion=1.0a`. 

--------

## Getting BioGraphs

You can find the source code for BioGraphs
{% include icon-github.html username="VHarisop" %} /
[BioGraphs](https://github.com/VHarisop/BioGraphs). For now, you can only build 
biographs using maven. The included `pom.xml` file should be sufficient if you have installed JInsect properly.

[commons]: http://mvnrepository.com/artifact/org.apache.commons/commons-collections4/4.0
[jgrapht]: http://mvnrepository.com/artifact/org.jgrapht/jgrapht-core/0.9.1
[biojava]: http://mvnrepository.com/artifact/org.biojava/biojava-core/4.0.0
[jinsect]: https://github.com/VHarisop/JInsect
[jgrapht]: https://github.com/jgrapht/jgrapht
[mvn-install]: http://maven.apache.org/plugins/maven-install-plugin/
