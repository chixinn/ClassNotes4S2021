package DSPPCode.flink.water_problem.impl;

import DSPPCode.flink.water_problem.question.WaterProblem;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class WaterProblemImpl extends WaterProblem {
  @Override
  public DataStream<String> execute(DataStream<String> dataStream) {
    DataStream<Tuple4<Integer,Boolean,Integer,Integer>> query=dataStream.map(
        new MapFunction<String, Tuple4<Integer,Boolean, Integer, Integer>>() {
          @Override
          public Tuple4<Integer,Boolean, Integer, Integer> map(String s) throws Exception {
            System.out.println(s);
            String[] tokens=s.split(",");
            return new Tuple4<>(Integer.parseInt(tokens[1]),Boolean.parseBoolean(tokens[0]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3]));
          }
        }
    );
    //按泳池id聚合键值对
    KeyedStream<Tuple4<Integer, Boolean, Integer, Integer>, Integer> keyedStream = query
        .keyBy(x -> x.f0);
    // keyedStream.print();
    DataStream<String> queryStream = keyedStream.process(new waterDetector());
    queryStream.print();
    return queryStream;
  }
}
class waterDetector extends KeyedProcessFunction<Integer,Tuple4<Integer, Boolean, Integer, Integer>,String>{
  private transient ValueState<Integer> speedState;
  private transient ValueState<Integer> timeState;
  private transient ValueState<Long> historyVolumnState;

  @Override
  public void processElement(
      Tuple4<Integer, Boolean, Integer, Integer> v4,
      Context context, Collector<String> collector) throws Exception {
    String queryResult="";
    Integer timeStamp=v4.f2;
    Integer poolId=v4.f0;
    Long volumn=Long.valueOf(0);
    if(v4.f1){
      //如果没有历史history说明没有改过速度，只在改速度时计算history
      if(historyVolumnState.value()==null){
        if(speedState.value()==null){
          //一上来就查询
          volumn=0L;
          historyVolumnState.update(0L);
        }
        else{
          volumn=speedState.value().longValue()*timeStamp;
        }
      }else{
        //如果有历史history说明改过速度，history+全新计算
        Long volHistory=historyVolumnState.value();
        Integer lastTime=timeState.value();
        volumn=volHistory+(timeStamp-lastTime)*speedState.value();
      }
      if(volumn<0){
        volumn=0L;
      }//计算出已放空水
      queryResult=String.valueOf(poolId)+","+String.valueOf(timeStamp)+","+String.valueOf(volumn);
      collector.collect(queryResult);
    }
    else{
      //如果没有设置过速度：
      if(speedState.value()==null){
        //更新速度
        speedState.update(v4.f3);
        //更新时间
        timeState.update(timeStamp);
        //也没有history
      }
      else{
        //如果设置过速度：
        // System.out.println("************************");
        Long lastSpeed=speedState.value().longValue();
        Integer lastSpeedChangeTime=timeState.value();
        //保存最新速度
        speedState.update(v4.f3);
        if(historyVolumnState.value()==null){
          historyVolumnState.update(0L);
        }
        // System.out.println("++++++++++++++++++++++");
          Long historyVolumn=historyVolumnState.value();
          // System.out.println("historyVolumn"+historyVolumn);
          Long volumnAdd=(timeStamp-lastSpeedChangeTime)*lastSpeed;
          Long vol=volumnAdd+historyVolumn;
          historyVolumnState.update(vol);
          timeState.update(timeStamp);
      }
    }

  }
  @Override
  public void open(Configuration parameters) throws Exception {
    ValueStateDescriptor<Integer> speedDescriptor=new ValueStateDescriptor<Integer>(
        "speed", Types.INT
    );
    ValueStateDescriptor<Integer> timeDescriptor=new ValueStateDescriptor<Integer>(
        "timeFlag", Types.INT
    );
    ValueStateDescriptor<Long> volumeDescriptor=new ValueStateDescriptor<Long>(
        "volumeFlag", Types.LONG
    );
    speedState = getRuntimeContext().getState(speedDescriptor);
    timeState= getRuntimeContext().getState(timeDescriptor);
    historyVolumnState=getRuntimeContext().getState(volumeDescriptor);
  }
}





