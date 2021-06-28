package DSPPCode.spark.pagerank_top3.impl;

import DSPPCode.spark.pagerank_top3.question.PageRankTop3;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PageRankTop3Impl extends PageRankTop3 {


  @Override

  public JavaPairRDD<String, Double> getTop3(JavaRDD<String> text, int iterateNum) {
    double factor = 0.85; // 指定系数
    // 将文本数据转换成[网页, {链接列表}]键值对
    JavaPairRDD<String, List<String>> linksRDD=text.mapToPair(
        new PairFunction<String, String, List<String>>() {
          @Override
          public Tuple2<String, List<String>> call(String s) throws Exception {
            String[] tokens=s.split(" ");
            List<String> list=new ArrayList<>();
            for (int i=2;i< tokens.length;i+=2){
              list.add(tokens[i]);
            }
            return new Tuple2<>(tokens[0],list);
          }
        }
    ).cache();//持久化到内存
    // 从输入中获取网页总数N
    long N=linksRDD.distinct().count();
    // 初始化每个页面的排名值，得到[网页, 排名值]键值对
    JavaPairRDD<String, Double> ranks =
        text.mapToPair(
            new PairFunction<String, String, Double>() {
              @Override
              public Tuple2<String, Double> call(String line) throws Exception {
                String[] tokens = line.split(" ");
                return new Tuple2<>(tokens[0], Double.valueOf(tokens[1]));
              }
            });
    //执行iterateNum次迭代计算
    for(int iter=0;iter<iterateNum;iter++){
      JavaPairRDD<String, Double> contributions =linksRDD.join(ranks).flatMapToPair(
          new PairFlatMapFunction<Tuple2<String, Tuple2<List<String>, Double>>, String, Double>() {
            @Override
            public Iterator<Tuple2<String, Double>> call(
                Tuple2<String, Tuple2<List<String>, Double>> stringTuple2Tuple2) throws Exception {
              List<Tuple2<String,Double>> list= new ArrayList<>();
              for( int i=0;i<stringTuple2Tuple2._2._1.size();i++){
                list.add(new Tuple2<>(stringTuple2Tuple2._2._1.get(i), stringTuple2Tuple2._2._2 / stringTuple2Tuple2._2._1.size()));
              }
              return list.iterator();
            }
          }
      );
      // 聚合对相同网页的贡献值，求和得到对每个网页的总贡献值

      ranks=contributions.reduceByKey(
          new Function2<Double, Double, Double>() {
            @Override
            public Double call(Double aDouble, Double aDouble2) throws Exception {
              return aDouble+aDouble2;
            }
          }
      ).mapValues(
          new Function<Double, Double>() {
            @Override
            public Double call(Double v) throws Exception {
              return (1 - factor) * 1.0 / N + factor * v;
            }
          }
      );
    }
    //输出PageRanktop3的结果
    //果然不能新建context
    JavaPairRDD<Double,String> top3_=ranks.mapToPair(v->new Tuple2<>(v._2,v._1));
    List<Tuple2<Double, String>> top3List = top3_.sortByKey(false).take(3);
    List<Double> top3ListPR= new ArrayList<>();
    for(int i=0;i<3;i++){
      top3ListPR.add(top3List.get(i)._1);
    }
    JavaPairRDD<Double,String>top3=top3_.filter(new Function<Tuple2<Double, String>, Boolean>() {
      @Override
      public Boolean call(Tuple2<Double, String> doubleStringTuple2) throws Exception {
        if (top3ListPR.contains(doubleStringTuple2._1)){
          return true;
        }
        else
          return false;
      }
    });
    JavaPairRDD<String,Double> top3ans=top3.sortByKey(false).mapToPair(v->new Tuple2<>(v._2,v._1));
    // top3ans.foreach(v->System.out.println(v));
    // JavaPairRDD<Double,String> ans=top3.mapToPair(v->new Tuple2<>(v._2,v._1));
    return top3ans;
  }
}
