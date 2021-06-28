package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import spire.macros.Auto;

import java.io.IOException;
/**
 *   3819	2021-03-27 21:30	357	vip
 *   3231	2021-03-27 21:30	77	non-vip
 *
 */


public class ConsumerMapperImpl extends ConsumerMapper {
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] datas = value.toString().split("\t");
        Consumer consumer=new Consumer();
//        for(String data:datas){
//            System.out.println(data);
//        }
        if (datas[3].equals("vip")){
//            System.out.println("yes");
            consumer=new Consumer(datas[0],Integer.parseInt(datas[2]),true);
        }
        else{
            consumer=new Consumer(datas[0],Integer.parseInt(datas[2]),false);

        }
//        System.out.println(consumer);
        context.write(new Text(datas[3]),consumer);
    }
}
