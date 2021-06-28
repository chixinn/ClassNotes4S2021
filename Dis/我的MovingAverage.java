package DSPPCode.spark.moving_averages.impl;

import DSPPCode.spark.moving_averages.question.MovingAverages;
import org.apache.commons.lang.StringUtils;
import org.apache.ivy.osgi.p2.P2Artifact;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.python.modules.math;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MovingAveragesImpl extends MovingAverages {
  private static final String MODE = "local";
  Integer window_size = 5;

  @Override
  public JavaRDD<String> movingAverages(JavaRDD<String> lines) {
    // 将函数应用于RDD中的每个元素，将返回的迭代器的所有内容构成新的RDD，通常用来切分单词。与map的区别是：这个函数返回的值是list的一个，去除原有的格式
    // JavaPairRDD<Integer,Integer> indexTimeRDD=lines.flatMapToPair(
    //     v->{
    //         String vStriped= StringUtils.strip(v,"[]");
    //         String[] tokens=vStriped.split(",");
    //         Tuple2<Integer,Integer>[] sumTupleArrayList=new Tuple2[window_size];
    //         for(int i=0;i<window_size;i++){
    //           Integer key=i-2+Integer.parseInt(tokens[0]);
    //           sumTupleArrayList[i]=new Tuple2<>(key,Integer.parseInt(tokens[1]));
    //         }
    //         return Arrays.asList(sumTupleArrayList).iterator();
    //     }
    // );
    JavaPairRDD<Integer, Integer> indexTimeRDD =
        lines.flatMapToPair(
            new PairFlatMapFunction<String, Integer, Integer>() {
              @Override
              public Iterator<Tuple2<Integer, Integer>> call(String s) throws Exception {
                String[] ls = s.split("\\[|,|]");
                Tuple2<Integer, Integer>[] t2 = new Tuple2[5];
                for(int i = 0; i < window_size; i ++){
                  Integer key = i - 2 + Integer.parseInt(ls[1]);
                  t2[i] = new Tuple2<>(key, Integer.parseInt(ls[2]));
                }
                return Arrays.asList(t2).iterator();
              }
            }
        );
    System.out.println(indexTimeRDD);




    return null;
  }
}
