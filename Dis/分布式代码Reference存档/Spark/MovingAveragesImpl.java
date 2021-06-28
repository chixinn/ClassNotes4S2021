package DSPPCode.spark.moving_averages.impl;

import DSPPCode.spark.moving_averages.question.MovingAverages;
import org.apache.commons.lang.StringUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.List;

public class MovingAveragesImpl extends MovingAverages {

  @Override
  public JavaRDD<String> movingAverages(JavaRDD<String> lines) {
    int k=2;
    long l = lines.count();

    JavaPairRDD<Integer,Integer> idx =
        lines.flatMapToPair(line->{
          String[] nums = StringUtils.strip(line, "[]").split(",");
          List<Tuple2<Integer,Integer>> pair = new ArrayList<>();
          int i = Integer.parseInt(nums[0]);
          int v = Integer.parseInt(nums[1]);
          for(int lft=i-1;lft>0&&i-lft<=k;--lft){
            // System.out.println(lft);
            pair.add(new Tuple2<>(lft,v));
          }
          for(int rgt=i+1;rgt<=l&&rgt-i<=k;++rgt){
            pair.add(new Tuple2<>(rgt,v));
          }
          pair.add(new Tuple2<>(i,v));
          // System.out.println(pair.iterator());
          return pair.iterator();
        });

    JavaRDD<String> ave =
        idx.groupByKey()
            .map(t->{
              int sum = 0;
              int cnt = 0;
              for(Integer i:t._2){
                sum += i;
                cnt++;
              }
              return "[" + t._1.toString() + "," + (int) sum / cnt + "]";
            });


    return ave;
  }
}
