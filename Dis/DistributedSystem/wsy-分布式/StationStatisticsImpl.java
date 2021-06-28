package DSPPCode.spark.station_statistics.impl;

import DSPPCode.spark.station_statistics.question.StationStatistics;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import scala.Tuple3;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StationStatisticsImpl extends StationStatistics {

  @Override
  public JavaRDD<Tuple3<String, String, Integer>> stationStatistics(JavaRDD<String> lines) {

    JavaPairRDD<Tuple2<String,String>,String> timestamps
        = lines.mapToPair(line->{
          String[] l = line.split(" ");
          return new Tuple2<>(new Tuple2<>(l[0], l[1]), l[2]);
    });


    JavaRDD<Tuple3<String,String,Integer>> timeDiff
        = timestamps
        .groupByKey()
        .map(t->{
          List<Long> timeList = new ArrayList<>();
          for(String time:t._2){
            timeList.add(new SimpleDateFormat("hh:mm:ss").parse(time).getTime());
          }
          int tot = 0;
          timeList.sort(Comparator.comparing(Long::intValue));
          for (int i = 0; i < timeList.size(); i+=2) {
            tot += new Long(timeList.get(i+1) - timeList.get(i)).intValue();
          }


          Integer seconds =  (tot/ 1000);
          return new Tuple3<>(t._1._1, t._1._2, seconds);
        });


    return timeDiff;
  }
}
