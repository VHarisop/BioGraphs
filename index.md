---
layout: default
title:  "BioGraphs"
date:   2015-12-30 20:21:23 +0200
---
# What is it?
BioGraphs is a Java library that uses n-gram graphs to index, analyze and classify biological sequence data. It is efficient and simple to use:

{% highlight java %}
BioGraph bGraph = new BioGraph("ACTAGTACTA");
System.out.println(bGraph.toDot());
{% endhighlight %}

## Uses
For now, BioGraphs contains utilities for indexing biological sequences using their n-gram graph representation. A custom graph similarity metric is employed to build an efficient tree-based index.  

In the near future, BioGraphs will be expanded with facilities for classification and data analysis.

## License
BioGraphs is released under the [GPLv3][license] license.

Check out the [Jekyll docs][jekyll-docs] for more info on how to get the most out of Jekyll. File all bugs/feature requests at [Jekyll’s GitHub repo][jekyll-gh]. If you have questions, you can ask them on [Jekyll Talk][jekyll-talk].

[jekyll-docs]: http://jekyllrb.com/docs/home
[jekyll-gh]:   https://github.com/jekyll/jekyll
[jekyll-talk]: https://talk.jekyllrb.com/
[license]: https://www.gnu.org/licenses/gpl.html
