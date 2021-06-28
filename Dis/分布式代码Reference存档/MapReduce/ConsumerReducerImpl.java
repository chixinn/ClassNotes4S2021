package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ConsumerReducerImpl extends ConsumerReducer {
    @Override
    protected void reduce(Text key, Iterable<Consumer> values, Context context) throws IOException, InterruptedException {
        Long vipmoneySum=Long.valueOf(0);
        Long nonvipmoneySum=Long.valueOf(0);
        Long vipCount=Long.valueOf(0);
        Long nonvipCount=Long.valueOf(0);
        StringBuilder result=new StringBuilder();
//        System.out.println("****************");
//        System.out.println(key);
        for(Consumer value:values){
//            System.out.println(value.getMoney());
            if(value.isVip()){
                vipCount=vipCount +1;
                vipmoneySum=vipmoneySum+value.getMoney();
            }else {
                nonvipCount=nonvipCount+1;
                nonvipmoneySum=nonvipmoneySum+value.getMoney();
            }
        }
        if(key.equals(new Text("vip"))){
            result.append("vip").append("\t").append(vipCount).append("\t").append(vipmoneySum);
        }
        else{
            result.append("non-vip").append("\t").append(nonvipCount).append("\t").append(nonvipmoneySum);
        }
        System.out.println(result);
        context.write(new Text(result.toString()),NullWritable.get());
    }
}
