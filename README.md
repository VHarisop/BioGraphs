# BioGraphs
A Java library to process biological sequence data using [N-Gram Graphs](http://www.nist.gov/tac/publications/2009/participant.papers/DemokritosGR.proceedings.pdf) and the [JInsect][jinsect] library.

Contains classes that represent biological sequences using n-gram graphs as well as
utilities for creating efficient graph databases. For now, indexing using canonical 
coding as well as a custom similarity metric is supported.

## Dependencies 
BioGraphs depends on the following Java libraries:
* [JInsect][jinsect] 
* [Apache Commons][apache-commons] 
* [Biojava][biojava] 
* [jgrapht][jgrapht]

## Building in Maven
Since BioGraphs is in its early development stages, building it in Maven the best 
thing to do for people who want to contribute to its development.

First of all, you need to download the latest [core release][jinsect-core] 
of JInsect and install it via `mvn install`. Then you can use the provided `pom.xml`
file to build BioGraphs in Maven.


[biojava]: http://mvnrepository.com/artifact/org.biojava/biojava-core/4.0.0
[jinsect]: https://github.com/VHarisop/JInsect/tree/maven-core
[jinsect-core]: https://vharisop.github.io/BioGraphs/assets/jinsect-core-1.0a.jar
[jgrapht]: http://mvnrepository.com/artifact/org.jgrapht/jgrapht-core/0.9.1
[apache-commons]: http://mvnrepository.com/artifact/org.apache.commons/commons-collections4/4.0
