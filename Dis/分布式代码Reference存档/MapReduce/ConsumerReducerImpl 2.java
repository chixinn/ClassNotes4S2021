package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerReducer;
import org.apache.calcite.rel.core.SetOp;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.HashMap;

public class ConsumerReducerImpl extends ConsumerReducer {
    @Override
    //果然Reduce有一个需要对消费者id进行去重。
    protected void reduce(Text key, Iterable<Consumer> values, Context context) throws IOException, InterruptedException {
        HashMap<String,Integer> cosIdVip = new HashMap<String,Integer>();
        HashMap<String,Integer> cosIdNonVip = new HashMap<String,Integer>();
        Long sumVip=0L;
        Long sumNonVip=0L;
        System.out.println("++++++++++++++++++++++++++++++++");
        System.out.println(key);
        System.out.println("AHHHHHHHHH++++++++++++++++++++++++++++++++");
        for(Consumer cos:values){
            System.out.println("cos.getId()"+cos.getId());
            System.out.println("cos.isVip()"+cos.isVip());
            if(cos.isVip()){
                sumVip=sumVip+cos.getMoney();
                if(cosIdVip.containsKey(cos.getId())){
                    continue;
                }else{
                    cosIdVip.put(cos.getId(),1);
                }
            }else{
                sumNonVip=sumNonVip+cos.getMoney();
                if(cosIdNonVip.containsKey(cos.getId())){
                    continue;
                }else{
                    cosIdNonVip.put(cos.getId(),1);
                }
            }
        }

        if(key.toString().equals("vip")){
            StringBuilder res= new StringBuilder();
            res.append(key).append("\t").append(cosIdVip.size()).append("\t").append(sumVip);
            System.out.println("res"+res);
            context.write(new Text(res.toString()), NullWritable.get());
        }else if(key.toString().equals("non-vip")){
            StringBuilder res= new StringBuilder();
            System.out.println("res"+res);
            res.append(key).append("\t").append(cosIdNonVip.size()).append("\t").append(sumNonVip);
            context.write(new Text(res.toString()), NullWritable.get());
        }


    }
}
