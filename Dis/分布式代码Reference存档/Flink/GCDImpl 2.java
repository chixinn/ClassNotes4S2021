package DSPPCode.flink.gcd.impl;

import DSPPCode.flink.gcd.question.GCD;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.IterativeStream;

public class GCDImpl extends GCD {

  @Override
  public DataStream<Tuple3<String, Integer, Integer>> calGCD(
      IterativeStream<Tuple3<String, Integer, Integer>> iteration) {
    //实现每一轮迭代计算的逻辑
    DataStream<Tuple3<String, Integer, Integer>> stepStream=iteration.map(
        new MapFunction<Tuple3<String, Integer, Integer>, Tuple3<String, Integer, Integer>>() {
          @Override
          public Tuple3<String, Integer, Integer> map(
              Tuple3<String, Integer, Integer> v3) throws Exception {
            int a=v3.f1;
            int b=v3.f2;
            if(a<b){
              int tmp=a;
              a=b;
              b=tmp;
            }
            //有的时候没有collector直接return 谢谢。
            return new Tuple3<>(v3.f0,b,a%b);
          }
        }
    );
    // 创建反馈流
    DataStream<Tuple3<String, Integer, Integer>> feedbackStream=stepStream.filter(v->v.f2!=0);
    iteration.closeWith(feedbackStream);
    // 创建输出流
    DataStream<Tuple3<String, Integer, Integer>> outputStream=stepStream;
    return outputStream;
  }
}
