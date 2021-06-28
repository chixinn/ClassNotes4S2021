package DSPPCode.spark.moving_averages.impl;

import DSPPCode.spark.moving_averages.question.MovingAverages;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.ArrayList;

public class MovingAveragesImpl extends MovingAverages {
  @Override
  public JavaRDD<String> movingAverages(JavaRDD<String> lines) {
    // 将 **t 时间点本身以及前后各 k 个时间点的值的平均值**当作 t 时间点的值，
    //  例如当t=2;k=2时; 前2个点：0，1；t本身2;后2个点：3,4;
    //t时间点键值对转换为多个[time,value]键值对
    long n=lines.count();
    JavaPairRDD<Integer,Integer> posAndValues=
        lines.flatMapToPair(
            (PairFlatMapFunction<String, Integer, Integer>)s->{
              System.out.println("S在这里是什么"+s);
              ArrayList<Tuple2<Integer,Integer>> posValue=new ArrayList<>();
              String[] posAndValue1=s.substring(1,s.length()-1).split(",");
              int pos=Integer.parseInt(posAndValue1[0]);
              int value=Integer.parseInt(posAndValue1[1]);
              posValue.add(new Tuple2<>(pos,value));
              for(int k=1;k<=2;k++){//对哦 灵光乍现只需要变posValue的值 本质还是一行line
                int left=pos-k;
                int right=pos+k;
                if(left>=1){
                  posValue.add(new Tuple2<>(left,value));
                }
                if(right<=n){
                  posValue.add(new Tuple2<>(right,value));
                }
              }
              System.out.println("posValue是什么");
              for (Tuple2<Integer, Integer> integerIntegerTuple2 : posValue) {
                System.out.println(integerIntegerTuple2);
              }
              return posValue.iterator();// ArrayList变iterator
            }
        );
    //聚合某个时间点的所有键值对
    JavaPairRDD<Integer, Iterable<Integer>> posIntermediateResult = posAndValues.groupByKey();
    //对各个时间点求平均值并返回结果
    JavaRDD<String>timeSeries=posIntermediateResult.map(v->{
      int sum=0,num=0;
      for(Integer value:v._2){
        sum=sum+value;
        num++;
      }
      int avg=sum/num;
      return "["+v._1+","+avg+"]";
    });




    return timeSeries;
  }
}
