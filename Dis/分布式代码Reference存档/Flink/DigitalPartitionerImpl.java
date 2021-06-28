package DSPPCode.flink.digital_conversion.impl;

import DSPPCode.flink.digital_conversion.question.DigitalPartitioner;

public class DigitalPartitionerImpl extends DigitalPartitioner {

  @Override
  public int partition(String key, int numPartitions) {
    float numKey= Float.parseFloat(key.toString());
    if(numKey<=4.0)
      return 0;//暂时没看懂这里是什么意思
    else
      return 1;
  }
}
