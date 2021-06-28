package DSPPCode.flink.grade_point.impl;

import DSPPCode.flink.grade_point.question.GradePoint;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import java.math.BigDecimal;

public class GradePointImpl extends GradePoint {

  @Override
  public DataStream<Tuple3<String, Integer, Float>> calculate(DataStream<String> text) {
    //难道不是一个DataStream就相当于一行了吗,所以根本不需要"\n"分开。。。
    DataStream<Tuple3<String, Integer, Float>> points=text.map(
        new MapFunction<String, Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> map(String s) throws Exception {
            String[] infos=s.split(" ");
            // System.out.println("infos数组打印测试");
            // for(int i=0;i<infos.length;i++){
            //   System.out.println(infos[i]);
            // }
            float point=Float.parseFloat(infos[2]);
            if(point>=0&&point<=5){
              return new Tuple3<>(infos[0],Integer.parseInt(infos[1]),point*Integer.parseInt(infos[1]));
              // System.out.println("point打印测试"+point);
              // return new Tuple3<>(infos[0],Integer.parseInt(infos[1]),point);

            }
            point=Float.valueOf(0);
            return new Tuple3<>(infos[0],Integer.parseInt(infos[1]),point);
          }
        }
    );
    // 用reduce函数把k,v键值对里面的v换掉
    DataStream<Tuple3<String, Integer, Float>> pointsGroupByKey=points.keyBy(0).reduce(
        new ReduceFunction<Tuple3<String, Integer, Float>>() {
          //reduce函数模拟的是相邻两个RDD相加会怎样
          @Override
          public Tuple3<String, Integer, Float> reduce(
              Tuple3<String, Integer, Float> v3,
              Tuple3<String, Integer, Float> t1) throws Exception {
            //所以说要注意这里相加时候的顺序:D。。。
            //是因为这样不是累加的形式所以会计算加重复吗
            // System.out.println("v3.f2*v3.f1+t1.f2*t1.f1  "+v3.f2*v3.f1+t1.f2*t1.f1);
            return new Tuple3<>(v3.f0,v3.f1+t1.f1,v3.f2+t1.f2);
          }
        }
    ).filter(
        new FilterFunction<Tuple3<String, Integer, Float>>() {
          @Override
          public boolean filter(Tuple3<String, Integer, Float> stringIntegerFloatTuple3)
              throws Exception {
            return stringIntegerFloatTuple3.f1 == 10;
          }
        }
    ).map(
        new MapFunction<Tuple3<String, Integer, Float>, Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> map(
              Tuple3<String, Integer, Float> stringIntegerFloatTuple3) throws Exception {
            System.out.println(stringIntegerFloatTuple3.f2);
            return new Tuple3<>(stringIntegerFloatTuple3.f0,stringIntegerFloatTuple3.f1,
                stringIntegerFloatTuple3.f2/10);
          }
        }
    );
    // DataStream<Tuple3<String,Integer,Float>> pointsGroupByKey=points.keyBy(0).reduce(
    //     new ReduceFunction<Tuple3<String, Integer, Float>>() {
    //       @Override
    //       public Tuple3<String, Integer, Float> reduce(
    //           Tuple3<String, Integer, Float> stringIntegerFloatTuple3,
    //           Tuple3<String, Integer, Float> t1) throws Exception {
    //         return new Tuple3<>(stringIntegerFloatTuple3.f0,stringIntegerFloatTuple3.f1+t1.f1,stringIntegerFloatTuple3.f2+t1.f2);
    //       }
    //     }
    // )
    //     .filter(
    //         new FilterFunction<Tuple3<String, Integer, Float>>() {
    //           @Override
    //           public boolean filter(Tuple3<String, Integer, Float> stringIntegerFloatTuple3)
    //               throws Exception {
    //             return stringIntegerFloatTuple3.f1.equals(10);
    //           }
    //         }
    //     )
    //     .map(
    //         new MapFunction<Tuple3<String, Integer, Float>, Tuple3<String, Integer, Float>>() {
    //           @Override
    //           public Tuple3<String, Integer, Float> map(
    //               Tuple3<String, Integer, Float> stringIntegerFloatTuple3) throws Exception {
    //             BigDecimal b=new BigDecimal(stringIntegerFloatTuple3.f2/10);
    //             Float f1=b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    //             return new Tuple3<>(stringIntegerFloatTuple3.f0,stringIntegerFloatTuple3.f1,f1);
    //           }
    //         }
    //     );

    pointsGroupByKey.print();

    return pointsGroupByKey;
  }
}
