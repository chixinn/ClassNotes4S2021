package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConsumerReducerImpl extends ConsumerReducer {


    @Override
    protected void reduce(Text key, Iterable<Consumer> values, Context context) throws IOException, InterruptedException {
        BigInteger cnt = BigInteger.ZERO;
        BigInteger sum = BigInteger.ZERO;
        Set idSet = new HashSet<String>();
        for (Consumer consumer:values){
            idSet.add(consumer.getId());
            cnt = cnt.add(BigInteger.ONE);
            sum = sum.add(BigInteger.valueOf(consumer.getMoney()));
        }
        String ans = key.toString() + "\t" + idSet.size() + "\t" + sum.toString();
        context.write(new Text(ans),NullWritable.get());
    }
}
