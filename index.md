---
layout: default
title:  "BioGraphs"
date:   2015-12-30 20:21:23 +0200
---

BioGraphs is a Java library that uses n-gram graphs to index, analyze and classify biological sequence data. It is efficient and simple to use:

{% highlight java %}
BioJGraph bGraph = new BioJGraph("ACTAGTACTA");
System.out.println(bGraph.toDot());
{% endhighlight %}

Check out the [Jekyll docs][jekyll-docs] for more info on how to get the most out of Jekyll. File all bugs/feature requests at [Jekyll’s GitHub repo][jekyll-gh]. If you have questions, you can ask them on [Jekyll Talk][jekyll-talk].

[jekyll-docs]: http://jekyllrb.com/docs/home
[jekyll-gh]:   https://github.com/jekyll/jekyll
[jekyll-talk]: https://talk.jekyllrb.com/
