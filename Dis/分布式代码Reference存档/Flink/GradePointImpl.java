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
    // |将lines中的每一个文本行按空格分割成单个单词|
    DataStream<String> lines=text.flatMap(
        new FlatMapFunction<String, String>() {
          @Override
          public void flatMap(String s, Collector<String> collector) throws Exception {
           String[]line=s.split("\n");
           collector.collect(line[0]);
          }
        }
    );
    // System.out.println("lines.print();");
    // lines.print();
    DataStream<Tuple3<String,Integer,Float>> stuInfoRaw=lines.map(
        new MapFunction<String, Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> map(String s) throws Exception {
            String[] tokens=s.split(" ");
            Float tmpScore=Integer.parseInt(tokens[1])*Float.parseFloat(tokens[2]);
            if(Float.parseFloat(tokens[2])==-1){
              tmpScore= (float) 0;
            }
            return new Tuple3<>(tokens[0],Integer.parseInt(tokens[1]),tmpScore);
          }
        }
    );
    // System.out.println("stuInfoRaw.print()");
    // stuInfoRaw.print();
    DataStream<Tuple3<String,Integer,Float>> stuInfo=stuInfoRaw.keyBy(0).reduce(
        new ReduceFunction<Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> reduce(
              Tuple3<String, Integer, Float> stringIntegerFloatTuple3,
              Tuple3<String, Integer, Float> t1) throws Exception {
            return new Tuple3<>(stringIntegerFloatTuple3.f0,stringIntegerFloatTuple3.f1+t1.f1,stringIntegerFloatTuple3.f2+t1.f2);
          }
        }
    )
            .filter(
        new FilterFunction<Tuple3<String, Integer, Float>>() {
          @Override
          public boolean filter(Tuple3<String, Integer, Float> stringIntegerFloatTuple3)
              throws Exception {
            return stringIntegerFloatTuple3.f1.equals(10);
          }
        }
    )
        .map(
        new MapFunction<Tuple3<String, Integer, Float>, Tuple3<String, Integer, Float>>() {
          @Override
          public Tuple3<String, Integer, Float> map(
              Tuple3<String, Integer, Float> stringIntegerFloatTuple3) throws Exception {
            BigDecimal b=new BigDecimal(stringIntegerFloatTuple3.f2/10);
            Float f1=b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
            return new Tuple3<>(stringIntegerFloatTuple3.f0,stringIntegerFloatTuple3.f1,f1);
          }
        }
    );
    // System.out.println("stuInfo.print();");
    stuInfo.print();

    return stuInfo;

    // DataStream<Tuple2<String,Integer>> stuCredit=lines.map(
    //     new MapFunction<String, Tuple2<String, Integer>>() {
    //       @Override
    //       public Tuple2<String, Integer> map(String s) throws Exception {
    //         String []tokens=s.split(" ");
    //         String key=tokens[0]+ "Credit";
    //         return new Tuple2<>(key,Integer.parseInt(tokens[1]));
    //       }
    //     }
    // ).keyBy(0).sum(1).map(
    //     new MapFunction<Tuple2<String, Integer>, Tuple2<String, Integer>>() {
    //       @Override
    //       public Tuple2<String, Integer> map(Tuple2<String, Integer> stringIntegerTuple2)
    //           throws Exception {
    //         String key=stringIntegerTuple2.f0.replace("Credit","");
    //         return new Tuple2<>(key,stringIntegerTuple2.f1);
    //       }
    //     }
    // );
    // stuCredit.print();
    // DataStream<Tuple2<String,Float>> stuSum=lines.map(
    //     new MapFunction<String, Tuple2<String, Float>>() {
    //       @Override
    //       public Tuple2<String,Float> map(String s) throws Exception {
    //         String []tokens=s.split(" ");
    //         String key=tokens[0]+ "Sum";
    //
    //         return new Tuple2<>(key,tmpScore);
    //       }
    //     }
    // ).keyBy(0).sum(1).map(
    //     new MapFunction<Tuple2<String, Float>, Tuple2<String, Float>>() {
    //       @Override
    //       public Tuple2<String, Float> map(Tuple2<String, Float> stringFloatTuple2)
    //           throws Exception {
    //         String key=stringFloatTuple2.f0.replace("Sum","");
    //         return new Tuple2<>(key,stringFloatTuple2.f1);
    //       }
    //     }
    // );
    // stuSum.print();
    //
    //
    // JoinedStreams<Tuple2<String, Integer>, Tuple2<String, Float>> stuInfo = stuCredit
    //     .join(stuSum);
    // // System.out.println(stuInfo);
    //
    // // return stuInfo;
    // return null;
  }
}
