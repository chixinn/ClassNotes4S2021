package DSPPCode.mapreduce.average_score.impl;

import DSPPCode.mapreduce.average_score.question.ScoreReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
/* 步骤1：确定输入键值对[K2,List(V2)]的数据类型为[Text, IntWritable]，输出键值对[K3,V3]的数据类型为[Text,IntWritable] */
public class ScoreReducerImpl extends ScoreReducer {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum=0;
        for(IntWritable value:values){
            sum+=value.get();
        }
        sum=sum/3;
        context.write(key,new IntWritable(sum));
    }
}
