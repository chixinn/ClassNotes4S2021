package DSPPCode.flink.speed_measurement.impl;

import DSPPCode.flink.speed_measurement.question.SpeedKeyedProcessFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
// 统计是否超速
//     DataStream<String> overSpeedStream = keyedStream.process(new SpeedKeyedProcessFunctionImpl());
public class SpeedKeyedProcessFunctionImpl extends SpeedKeyedProcessFunction {

  /**
   * TODO 请完成该方法
   * @param parameters
   * @throws Exception
   */
  public void open(Configuration parameters) throws Exception {

  }

  /**
   * TODO 请完成该方法
   * @param t
   * @param context
   * @param collector
   * @throws Exception
   */
  @Override
  public void processElement(Tuple2<String, String> t, Context context, Collector<String> collector)
      throws Exception {


  }

  // @Override
  // public float averageSpeed(String[] times) throws ParseException {
  //   return 0;
  // }

  /**
   * TODO 请完成该方法
   *
   * @param times 包含一个进入A入口的时刻和一个离开B出口的时刻
   * @return 返回A入口到B出口区间内的平均速度，单位为km/h
   */
  @Override
  public float averageSpeed(String[] times) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    Date timeA = sdf.parse(times[0]);
    Date timeB = sdf.parse(times[1]);
    Long diff=timeB.getTime()-timeA.getTime();
    diff=diff/1000/3600;
    float speed= (float) (1.0*10/diff);//单位为km/h
    return speed;
  }
}
