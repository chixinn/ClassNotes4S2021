package DSPPCode.mapreduce.max_temperature.impl;

import DSPPCode.mapreduce.max_temperature.question.MaxTemperatureMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class MaxTemperatureMapperImpl extends MaxTemperatureMapper {
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] datas=value.toString().split(" ");
        context.write(new Text(datas[0]),new IntWritable(Integer.parseInt(datas[1])));
    }
}
