package com.mycompany.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TopN {


  public static void main(String args[])
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    final Map<String, Double> map = new HashMap<String, Double>() {{
      put("A", 0.0);
      put("B", 3.14);
      put("C", 3.14);
      put("D", 8.8);
      put("E", 2.1);
      put("F", 1.01);
      put("G", 2.1);
    }};

    final int N = 200000;
    //generate fake data.
    for(int i=0; i < N; ++i) {
      map.put("XZY"+String.valueOf(i), Double.valueOf("1." + String.valueOf(i)));
      map.put("ABC"+String.valueOf(i), Double.valueOf("2." + String.valueOf(i)));
      map.put("IJK"+String.valueOf(i), Double.valueOf("2." + String.valueOf(i)));
      map.put("XZY"+String.valueOf(i), Double.valueOf("0." + String.valueOf(i)));
    }

    TopN t = new TopN();

    final List<String> funcNames = Arrays.asList("getTopN1", "getTopN2", "getTopN3");

    final HashMap<String, Long> times = new HashMap<>();
    funcNames.stream().forEach(name -> times.put(name, Long.valueOf(0)));

    final int M = 100;
    final int L= 50;

    for (int j = 0; j < M; ++j) {
      for (String name : funcNames) {
        System.out.println("answer= " + t.timedTest(map, L, name, times).toString());
      }
    }

    System.out.println("--------AVERAGE TIMES----------");
    funcNames.stream().forEach(name -> System.out.println(
        String.format("average time for %s: %d", name, times.get(name)/M)));
  }

  public List<String> timedTest(final Map<String, Double> map, int limit, final String methodName, Map<String, Long> times)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    final long startTime = System.currentTimeMillis();
    TopN t = new TopN();
    Method m = TopN.class.getMethod(methodName, Map.class, int.class);
    List<String> returnVal = (List<String>) m.invoke(t, map, limit);
    final long endTime = System.currentTimeMillis();
    final long totalTime = endTime - startTime;

    final Long sum = times.get(methodName);
    times.put(methodName, sum + totalTime);
    System.out.println("Total execution time for " + methodName + ": " + totalTime );
    return returnVal;
  }

  public List<String> getTopN1(final Map<String, Double> map, int limit) {
    // Creating priority queue with size limit
    PriorityQueue<Entry<String, Double>> pq = new PriorityQueue<>(limit, Entry.comparingByValue());
    for (Entry<String, Double> entry : map.entrySet()) {
      pq.add(entry);
      if (pq.size() > limit) {
        pq.poll();
      }
    }
    final ArrayList<String> answer = new ArrayList<>();
    while (!pq.isEmpty() && limit-- > 0) {
      answer.add(pq.poll().getKey());
    }
    Collections.reverse(answer);
    return answer;
  }

  public List<String> getTopN2(final Map<String, Double> map, int limit) {
    return map.entrySet().stream()
        .sorted(Comparator.<Entry<String, Double>>comparingDouble(Entry::getValue)
                    .reversed())
        .limit(limit)
        .map(Entry::getKey)
        .collect(Collectors.toList());
  }

  public List<String> getTopN3(Map<String, Double> map, int n) {

    TreeSet<Map.Entry<String, Double>> topN = new TreeSet<>(
        Map.Entry.<String, Double>comparingByValue()
            .reversed()                         // by value descending, then by key
            .thenComparing(Map.Entry::getKey)); // to allow entries with repeated values

    map.entrySet().forEach(e -> {
      topN.add(e);
      if (topN.size() > n) topN.pollLast();
    });

    return topN.stream()
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }
}
