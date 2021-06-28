package DSPPCode.spark.station_statistics.impl;

import DSPPCode.spark.station_statistics.question.StationStatistics;
import com.clearspring.analytics.util.Lists;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.codehaus.janino.Java;
import scala.Tuple2;
import scala.Tuple3;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StationStatisticsImpl extends StationStatistics {

  @java.lang.Override
  public JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines) {
    // kv[姓名 基站名，时刻]
    JavaPairRDD<String,String>pairs=
        //将每一行映射为【姓名 基站名，时刻】的kv
        lines.mapToPair(
            new PairFunction<String, String, String>() {
              @Override
              public Tuple2<String, String> call(String s) throws Exception {
                String []splits=s.split(" ");
                String groupKey=splits[0]+" "+splits[1];
                String groupValue=splits[2];
                return new Tuple2<>(groupKey,groupValue);
              }
            }
        );
    // 按key进行分组
    // kv[姓名 基站名，{时刻}]
    JavaPairRDD<String, Iterable<String>> groups = pairs.groupByKey();
    //遍历v{时刻}
    //mapValues只改变Value
    JavaPairRDD<String,Integer> statistics=groups.mapValues(
        new Function<Iterable<String>, Integer>() {
          @Override
          public Integer call(Iterable<String> times) throws Exception {
            int sum=0;
            List<String> timeList= Lists.newArrayList(times);
            //注意collections.sort这个Java的工具类
            Collections.sort(timeList);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            for(int i=0;i<timeList.size();i+=2){

              sum= (int) (sum+sdf.parse(timeList.get(i+1)).getTime()-sdf.parse(timeList.get(i)).getTime());
            }
            // System.out.println(sum/1000);
            return sum/1000;
          }
        }
    );
    JavaRDD<Tuple3<String, String, Integer>> res=statistics.map(
        new Function<Tuple2<String, Integer>, Tuple3<String, String, Integer>>() {
          @Override
          public Tuple3<String, String, Integer> call(Tuple2<String, Integer> v2)
              throws Exception {
            System.out.println(v2._1);
            String []tokens=v2._1.split(" ");
            System.out.println(v2._2);
            return new Tuple3<>(tokens[0],tokens[1],v2._2);
          }
        }
    );
    return res;
  }
}
