package DSPPCode.spark.topk_power.impl;

import DSPPCode.spark.topk_power.question.TopKPower;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TopKPowerImpl extends TopKPower {

  @Override
  public int topKPower(JavaRDD<String> lines) {
    JavaRDD<String> boxs =
        lines.flatMap(
             line -> Arrays.asList(line.split(" ")).iterator());

    JavaPairRDD<String, Integer> pairs =
        boxs.mapToPair(
             word -> new Tuple2<>(word, 1));

    JavaPairRDD<String, Integer> boxCounts =
        pairs
            .groupByKey()
            .mapToPair(
                 t -> {
                  Integer sum = 0;
                  for (Integer i : t._2) {
                    sum += i;
                  }
                  return new Tuple2<>(t._1, sum * sum);
                });

    List<Tuple2<String, Integer>> outputs = boxCounts
        .top(5, new TupleComparator());

    // 输出词频统计结果
    int sum = 0;
    for (Tuple2<String,Integer> output:outputs){
      sum += output._2;
    }

    return sum;
  }


  public static class TupleComparator implements
      Comparator<Tuple2<String, Integer>>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Tuple2<String, Integer> o1,
        Tuple2<String, Integer> o2) {
      return Integer.compare(o1._2(), o2._2());
    }
  }
}
