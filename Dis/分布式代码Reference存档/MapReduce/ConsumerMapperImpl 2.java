package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ConsumerMapperImpl extends ConsumerMapper {
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] lines=value.toString().split("\t");
//        for( String li:lines){
//            System.out.println(li);
//        }
        System.out.println(lines.length);
        Consumer cos=new Consumer();
        cos.setId(lines[0]);
        System.out.println("lines[3]"+lines[3]);
        if(lines[3].equals("vip")){
          boolean  isVip=true;
            cos.setVip(isVip);
            cos.setMoney(Integer.parseInt(lines[2]));
            context.write(new Text(lines[lines.length-1]),cos);
        }else if (lines[3].equals("non-vip")){
          boolean isVip=false;
            cos.setVip(isVip);
            cos.setMoney(Integer.parseInt(lines[2]));
            context.write(new Text(lines[lines.length-1]),cos);
        }


//        for(String data:lines){
//            System.out.println("dat打印测试"+data);
//            String[] datas=data.split(" ");
//            boolean isVip = false;
//            if (datas[datas.length - 1].equals("vip")){
//                isVip=true;
//            }else if(datas[datas.length - 1].equals("non-vip")) {
//                isVip=false;
//            }
//            Consumer cos=new Consumer(datas[0],Integer.parseInt(datas[datas.length-2]),isVip);
//            context.write(new Text(datas[datas.length-1]),cos);
//        }
        System.out.println("一次map作业");
    }
}
