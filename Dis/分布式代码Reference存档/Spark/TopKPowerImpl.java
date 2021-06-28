package DSPPCode.spark.topk_power.impl;

import DSPPCode.spark.topk_power.question.TopKPower;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.python.antlr.op.In;
import scala.Int;
import scala.Tuple2;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TopKPowerImpl extends TopKPower {

  @Override
  public int topKPower(JavaRDD<String> lines) {
    Integer kMax=5;
    JavaPairRDD<String,Integer> boxIdRDD=lines.flatMap(
        new FlatMapFunction<String, String>() {
          @Override
          public Iterator<String> call(String s) throws Exception {
            return Arrays.asList(s.split(" ")).iterator();
          }
        }
    ).mapToPair(v-> new Tuple2<>(v,1));
    JavaPairRDD<String,Integer> boxCountsRDD=boxIdRDD.groupByKey().mapToPair(
        new PairFunction<Tuple2<String, Iterable<Integer>>, String, Integer>() {
          @Override
          public Tuple2<String, Integer> call(
              Tuple2<String, Iterable<Integer>> stringIterableTuple2) throws Exception {
            int sum= 0;
            for(Integer i:stringIterableTuple2._2){
              sum=sum+i;
            }
            return new Tuple2<String,Integer>(stringIterableTuple2._1,sum);
          }
        }
    );
    JavaPairRDD<Integer,String> countsBoxRDD=boxCountsRDD.mapToPair(v->new Tuple2<>(v._2,v._1));
    // countsBoxRDD.sortByKey(false).foreach(v->System.out.println(v));
    int sum_= 0;
    if (countsBoxRDD.count()<kMax){
      kMax= Math.toIntExact(countsBoxRDD.count());
    }
    List<Tuple2<Integer,String>> countsKList=countsBoxRDD.sortByKey(false).take(kMax);
    for (Tuple2<Integer, String> integerStringTuple2 : countsKList) {
      sum_ = sum_ + (integerStringTuple2._1) * (integerStringTuple2._1);
    }

    return sum_;
  }
}
