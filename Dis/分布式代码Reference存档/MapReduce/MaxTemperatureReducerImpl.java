package DSPPCode.mapreduce.max_temperature.impl;

import DSPPCode.mapreduce.max_temperature.question.MaxTemperatureReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class MaxTemperatureReducerImpl extends MaxTemperatureReducer {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        Integer maxInt=Integer.MIN_VALUE;
        for (IntWritable value:values){
            if(value.get()>maxInt){
                maxInt= value.get();
            }
        }
        context.write(key,new IntWritable(maxInt));
    }
}
