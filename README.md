Sorted insertion microbenchmark
===

I saw a student implementing a sorted list by doing add + `Collections.sort`,
and I wanted to evaluate how much of a performance hit that was when compared to
the usual binary search + insertion.

This is a simple [jmh](http://openjdk.java.net/projects/code-tools/jmh/)-based
benchmark that evaluates this question. To run it, you'll need to
use [Maven](http://maven.apache.org/) from the command line:

    mvn clean install
    java -jar target/benchmarks.jar

On my machine, a 2016 Lenovo X1 Carbon with an i7-6600U CPU running Oracle Java
1.8.0_102 on Ubuntu Linux 16.04, these are the results:

    # Run complete. Total time: 00:13:26

    Benchmark                               Mode  Cnt  Score   Error   Units
    SortedInsertionBenchmark.appendSort    thrpt  200  2.044 ± 0.053  ops/ms
    SortedInsertionBenchmark.searchInsert  thrpt  200  3.558 ± 0.104  ops/ms
