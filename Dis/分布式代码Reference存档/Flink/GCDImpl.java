package DSPPCode.flink.gcd.impl;

import DSPPCode.flink.gcd.question.GCD;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.util.Collector;

public class GCDImpl extends GCD {
  @Override
  public DataStream<Tuple3<String, Integer, Integer>> calGCD(
      IterativeStream<Tuple3<String, Integer, Integer>> iteration) {
      DataStream<Tuple3<String, Integer, Integer>>iteratedStream=iteration.flatMap(
          new FlatMapFunction<Tuple3<String, Integer, Integer>, Tuple3<String, Integer, Integer>>() {
            @Override
            public void flatMap(Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3,
                Collector<Tuple3<String, Integer, Integer>> collector) throws Exception {
              // |例如迭代算子的输入输入为(A, 5, 2)，此处转换将(A, 5, 2)转换为(A, 2, 1)|
              Integer a=stringIntegerIntegerTuple3.f1;
              Integer b=stringIntegerIntegerTuple3.f2;
              Integer tmp=0;
              if(a<b){
              tmp=a;
              a=b;
              b=tmp;
              }
              Integer mod=a % b;
              Tuple3<String, Integer, Integer> feedbackValue =
                  new Tuple3(stringIntegerIntegerTuple3.f0,b,mod);
              // |例如迭代算子的输入输入为(A, 1, 0)，此处转换将(A, 1, 0)转换为(A, 1, 0)|
              // Tuple3<String, Integer, Integer> outputValue =
              //     new Tuple3(stringIntegerIntegerTuple3.f0, b, Integer.valueOf(0));
              collector.collect(feedbackValue);
              // collector.collect(outputValue);
            }
          }
      );
    // iteratedStream.print();
    // |创建反馈流|
    // |选择第三位置不为Min的元组，例如(A, 2, 3)|
    DataStream<Tuple3<String, Integer, Integer>>feedback=iteratedStream.filter(
        new FilterFunction<Tuple3<String, Integer, Integer>>() {
          @Override
          public boolean filter(Tuple3<String, Integer, Integer> stringIntegerIntegerTuple3)
              throws Exception {
            return stringIntegerIntegerTuple3.f2!=0;
          }
        }
    );
    // feedback.print();
    iteration.closeWith(feedback);

    return iteratedStream;
  }
}
