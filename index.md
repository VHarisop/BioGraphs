---
layout: default
title:  "BioGraphs"
date:   2015-12-30 20:21:23 +0200
---
# What is it?
BioGraphs is a Java library that uses n-gram graphs to index, analyze and classify biological sequence data. It is efficient and simple to use:

{% highlight java %}
// DNA string: ACTAGTACTA
BioGraph bGraph = new BioGraph("ACTAGTACTA");

// output graph to DOT format
System.out.println(bGraph.toDot());
{% endhighlight %}

## Uses
For now, BioGraphs contains utilities for indexing biological sequences using their n-gram graph representation. A custom graph similarity metric is employed to build an efficient tree-based index.

In the near future, BioGraphs will be expanded with facilities for classification and data analysis.

## License
BioGraphs is released under the [GPLv3][license] license.

### Read more:
* [A preprocessing method based on hashing]({{ site.baseurl }}/biographs/2016/02/15/indexing.html)

[license]: https://www.gnu.org/licenses/gpl.html
