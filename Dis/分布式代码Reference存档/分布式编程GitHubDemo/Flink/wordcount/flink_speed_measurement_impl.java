package DSPPCode.flink.speed_measurement.impl;

import DSPPCode.flink.speed_measurement.question.SpeedKeyedProcessFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.util.Collector;
import scala.Array;
import scala.tools.nsc.doc.model.Val;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// 统计是否超速
//     DataStream<String> overSpeedStream = keyedStream.process(new SpeedKeyedProcessFunctionImpl());
public class SpeedKeyedProcessFunctionImpl extends SpeedKeyedProcessFunction {

  /**
   * TODO 请完成该方法
   * @param parameters
   * @throws Exception
   */
  /**
   * 最直接的实现方式是使用一个 boolean 型的标记状态来表示是否刚处理过一个小额交易。
   * 当处理到该账户的一个大额交易时，你只需要检查这个标记状态来确认上一个交易是是否小额交易即可。
   * 然而，仅使用一个标记作为 FraudDetector 的类成员来记录账户的上一个交易状态是不准确的。
   * Flink 会在同一个 FraudDetector 的并发实例中处理多个账户的交易数据，
   * 假设，当账户 A 和账户 B 的数据被分发的同一个并发实例上处理时，
   * 账户 A 的小额交易行为可能会将标记状态设置为真，随后账户 B 的大额交易可能会被误判为欺诈交易。
   * 当然，我们可以使用如 Map 这样的数据结构来保存每一个账户的状态，但是常规的类成员变量是无法做到容错处理的，当任务失败重启后，之前的状态信息将会丢失。
   * 这样的话，如果程序曾出现过失败重启的情况，将会漏掉一些欺诈报警。
   * 为了应对这个问题，Flink 提供了一套支持容错状态的原语，这些原语几乎与常规成员变量一样易于使用。
   * Flink 中最基础的状态类型是 ValueState，这是一种能够为被其封装的变量添加容错能力的类型。
   * ValueState 是一种 keyed state，也就是说它只能被用于 keyed context 提供的 operator 中，即所有能够紧随 DataStream#keyBy 之后被调用的operator。
   * 一个 operator 中的 keyed state 的作用域默认是属于它所属的 key 的。
   * 这个例子中，key 就是当前正在处理的交易行为所属的信用卡账户（key 传入 keyBy() 函数调用），而 FraudDetector 维护了每个帐户的标记状态。
   * ValueState 需要使用 ValueStateDescriptor 来创建，ValueStateDescriptor 包含了 Flink 如何管理变量的一些元数据信息。
   * 状态在使用之前需要先被注册。 状态需要使用 open() 函数来注册状态。
   */
  private static final float MAX_SPEED= (float) 60.00;
  private transient ValueState<Boolean> flagState;
  private transient ValueState<Boolean> isTimeState;
  private transient HashMap<Integer,String> times;
  public void open(Configuration parameters) throws Exception {
    ValueStateDescriptor<Boolean>flagDescriptor=new ValueStateDescriptor<Boolean>(
        "flag", Types.BOOLEAN
    );
    flagState = getRuntimeContext().getState(flagDescriptor);
    ValueStateDescriptor<Boolean>isTimeDescriptor=new ValueStateDescriptor<Boolean>(
        "timeflag", Types.BOOLEAN
    );
    isTimeState=getRuntimeContext().getState(isTimeDescriptor);
    times=new HashMap<>();
  }

  /**
   * TODO 请完成该方法
   * @param t
   * @param context
   * @param collector
   * @throws Exception
   */
  /**
   *ValueState 是一个包装类，类似于 Java 标准库里边的 AtomicReference 和 AtomicLong。
   * 它提供了三个用于交互的方法。
   * update 用于更新状态，value 用于获取状态值，还有 clear 用于清空状态。
   * 如果一个 key 还没有状态，例如当程序刚启动或者调用过 ValueState#clear 方法时，ValueState#value 将会返回 null。
   * 如果需要更新状态，需要调用 ValueState#update 方法，直接更改 ValueState#value 的返回值可能不会被系统识别。
   * 容错处理将在 Flink 后台自动管理，你可以像与常规变量那样与状态变量进行交互。
   */
  // 将数据源中的数据解析成[车牌号, 时刻]或[车牌号, 速度]的键值对
  // 按车牌号聚合键值对
  // KeyedStream<Tuple2<String, String>, String> keyedStream = inputStream.keyBy(x -> x.f0);
  @Override
  public void processElement(Tuple2<String, String> t, Context context, Collector<String> collector)
      throws Exception {
    //看看现在有没有超速
    //如果没有超速则继续
    //我觉得根本不需要记录有没有超速
    String[] timesList = {"",""};
    // System.out.println(times[0]);
    if(t.f1.contains(":")) {
      // Boolean lastSpeedNotExceed=flagState.value();
      //看看这个数据是不是时间类型
      Boolean lastIsTimeType = isTimeState.value();
      if (lastIsTimeType != null) {
        times.put(1,t.f1);
        timesList[0]=times.get(0);
        timesList[1]=times.get(1);
        float speed = averageSpeed(timesList);
        System.out.println(speed);
        if (speed > MAX_SPEED) {
          collector.collect(t.f0);
        }
        isTimeState.clear();
      }
      times.put(0,t.f1);
      timesList[0]=times.get(0);
      isTimeState.update(true);
    }
   else{
      if(Float.parseFloat(t.f1)>MAX_SPEED){
        collector.collect(t.f0);
      }
    }
  }


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
    System.out.println(timeA);
    Date timeB = sdf.parse(times[1]);
    double diff=timeB.getTime()-timeA.getTime();
    System.out.println(diff);

    diff=diff*1.0/1000/3600;
    float speed= (float) (1.0*10/diff);//单位为km/h
    return speed;
  }
}
