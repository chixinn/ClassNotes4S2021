package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerMapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ConsumerMapperImpl extends ConsumerMapper {
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split("\t");
        Consumer consumer = new Consumer(values[0],Integer.parseInt(values[2]),values[3].equals("vip"));
        context.write(new Text(values[3]),consumer );
    }
}
