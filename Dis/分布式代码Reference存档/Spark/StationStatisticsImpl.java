package DSPPCode.spark.station_statistics.impl;

import DSPPCode.spark.station_statistics.question.StationStatistics;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.python.modules.math;
import scala.Tuple2;
import scala.Tuple3;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 假定一群人早晨徒步从A地区去往B地区踏青。
 * 其中，一部分人早晨出发得早，傍晚就徒步从B地区返回A地区，并于当天18:00:00之前抵达A地区；
 * 而另一部分人由于早晨出发得晚，当天未从B地区返回A地区，直接在B地区留宿一晚。
 * 在来回途中，每个人都会经过多个基站。
 * 现统计了当天每个人往返途中进入每个基站的时刻和从每个基站出来的时刻，要求计算出每个人在每个基站停留的总时间（以秒为单位）。
 */

public class StationStatisticsImpl extends StationStatistics {

  @Override
  public JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines) {
    JavaPairRDD<Tuple2<String,String>,String > nameStationRDD= lines.flatMap(v-> Arrays.asList(v.split("\n")).iterator()).mapToPair(
        v->{
          String[] tokens=v.split(" ");
          // System.out.println(tokens);
          Tuple2 namestationTuple=new Tuple2(tokens[0],tokens[1]);
          return new Tuple2<>(namestationTuple,tokens[2]);
        }
    );

    //按key聚合
    JavaPairRDD<Tuple2<String,String>,Long> stationStatisticsRDD=nameStationRDD.groupByKey().mapToPair(
        new PairFunction<Tuple2<Tuple2<String, String>, Iterable<String>>, Tuple2<String, String>, Long>() {
          @Override
          public Tuple2<Tuple2<String, String>, Long> call(
              Tuple2<Tuple2<String, String>, Iterable<String>> tuple2IterableTuple2)
              throws Exception {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            List<Date> times = new ArrayList<>();
            for (String i : tuple2IterableTuple2._2) {
              Date timei = sdf.parse(i);
              times.add(timei);
            }
            Long diff=Long.valueOf(0);
            for(int j=0;j<times.size();j=j+2){
              diff=diff+Math.abs(times.get(j).getTime()-times.get(j+1).getTime())/1000;
            }

            // System.out.println(diff);
            // System.out.println(tuple2IterableTuple2._1._1);
            // System.out.println(tuple2IterableTuple2._1._2);

            return new Tuple2<Tuple2<String, String>, Long>(tuple2IterableTuple2._1,diff);


          }
        }
    );
    JavaRDD<Tuple3<String, String, Integer>> statistics=stationStatisticsRDD.map(v->new Tuple3<String, String, Integer>(v._1._1,v._1._2,
        Math.toIntExact(v._2)));
    return statistics;
  }
}
// package DSPPCode.spark.station_statistics.impl;
//
// import DSPPCode.spark.station_statistics.question.StationStatistics;
// import org.apache.spark.api.java.JavaPairRDD;
// import org.apache.spark.api.java.JavaRDD;
// import org.apache.spark.api.java.function.Function;
// import org.apache.spark.api.java.function.PairFunction;
// import scala.Tuple2;
// import scala.Tuple3;
// import java.util.ArrayList;
// import java.util.List;
//
//
// public class StationStatisticsImpl extends StationStatistics {
//
//   @Override
//   public JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines) {
//     //先join
//     JavaPairRDD<String, String> pairs = lines.mapToPair(new PairFunction<String, String, String>() {
//       public Tuple2<String, String> call(String x) throws Exception {
//         String key = x.split(" ")[0]+" "+x.split(" ")[1];
//         System.out.println(key);
//         return new Tuple2(key , x.split(" ")[2]);
//       }
//     });
//
//     JavaPairRDD<String, Integer> wordCounts = pairs.groupByKey().mapToPair(
//         new PairFunction<Tuple2<String, Iterable<String>>, String, Integer>() {
//           @Override
//           public Tuple2<String, Integer> call(
//               Tuple2<String, Iterable<String>> t) throws Exception {
//             Integer sum = Integer.valueOf(0);
//             List<String> s2 = new ArrayList<String>();
//             for (String s1: t._2){
//               s2.add(s1);
//             }
//             for(int j=0;j<s2.size();j=j+2){
//               sum+=Math.abs(Integer.parseInt(s2.get(j).split(":")[0])-Integer.parseInt(s2.get(j+1).split(":")[0]))*3600
//                   +Math.abs(Integer.parseInt(s2.get(j).split(":")[1])-Integer.parseInt(s2.get(j+1).split(":")[1]))*60
//                   +Math.abs(Integer.parseInt(s2.get(j).split(":")[2])-Integer.parseInt(s2.get(j+1).split(":")[2]));
//             }
//             return new Tuple2<String, Integer>(t._1, sum);
//           }
//         }
//     );
//
//     JavaRDD<Tuple3<String,String,Integer>> result = wordCounts.map(
//         new Function<Tuple2<String, Integer>, Tuple3<String, String, Integer>>() {
//           @Override
//           public Tuple3<String, String, Integer> call(Tuple2<String, Integer> v1) throws Exception {
//             return new Tuple3<String, String, Integer>(v1._1.split(" ")[0],v1._1.split(" ")[1],v1._2);
//           }
//         }
//
//     );
//
//     return result;
//   }
// }
//
