package DSPPCode.spark.pagerank_top3.impl;

import DSPPCode.spark.pagerank_top3.question.PageRankTop3;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PageRankTop3Impl extends PageRankTop3 {

  @Override
  public JavaPairRDD<String, Double> getTop3(JavaRDD<String> text, int iterateNum) {

    double factor = 0.85;
    // 将文本数据转换成[网页, {链接列表}]键值对
    JavaPairRDD<String, List<Tuple2<String,Double>>> links =
        text.mapToPair(
            line -> {
              String[] tokens = line.split(" ");
              List<Tuple2<String,Double>> list = new ArrayList<>();
              for (int i = 2; i < tokens.length; i+=2) {
                list.add(new Tuple2<>(tokens[i], Double.parseDouble(tokens[i + 1])) );
              }
              return new Tuple2<>(tokens[0], list);
            })
            .cache(); // 持久化到内存

    long N = links.count();

    // 初始化每个页面的排名值，得到[网页, 排名值]键值对
    JavaPairRDD<String, Double> ranks =
        text.mapToPair(
             line -> {
              String[] tokens = line.split(" ");
              return new Tuple2<>(tokens[0], Double.valueOf(tokens[1]));
            });

    // 执行iterateNum次迭代计算
    for (int i = 0; i < iterateNum; i++) {
      JavaPairRDD<String, Double> contributions =
          links
              // 将links和ranks做join，得到[网页, {{链接列表}, 排名值}]
              .join(ranks)
              // 计算出每个网页对其每个链接网页的贡献值
              .flatMapToPair(
                   t -> {
                    List<Tuple2<String, Double>> list = new ArrayList<>();
                    Double value = t._2._2 / t._2._1.size();
                    for (Tuple2<String,Double> outer: t._2._1) {
                      // 网页排名值除以链接总数
                      list.add(new Tuple2<>(outer._1,
                          value * outer._2 ));
                    }
                    return list.iterator();
                  });

      ranks =
          contributions
              // 聚合对相同网页的贡献值，求和得到对每个网页的总贡献值
              .reduceByKey(Double::sum)
              // 根据公式计算得到每个网页的新排名值
              .mapValues(
                  v -> (1.0 - factor) / N  + factor * v);
    }


    List<Tuple2<String, Double>> tops = ranks.top(3,new TupleComparator());

    JavaPairRDD<String,Double> ans = ranks.
        flatMapToPair(t->{
          List<Tuple2<String, Double>> list = new ArrayList<>();
          for(Tuple2<String,Double> top:tops){
            if(top._1.equals(t._1)){
              list.add(t);
            }
          }

          return list.iterator();
        });


    return  ans;
  }

  public static class TupleComparator implements
      Comparator<Tuple2<String, Double>>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Tuple2<String, Double> o1,
        Tuple2<String, Double> o2) {
      return Double.compare(o1._2(), o2._2());
    }
  }
}
