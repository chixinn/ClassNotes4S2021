
package DSPPCode.mapreduce.average_score.impl;


import DSPPCode.mapreduce.average_score.question.ScoreMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.StringTokenizer;
/**
 * Mapper:
 * Map的输入类型<key,value>;最终确定为<Object,Text>;
 * 这里value为Text类型，指每次读人文件的一行
 * Map输出类型为<Text,IntWritable>
 */

public class ScoreMapperImpl extends ScoreMapper {
    private final Text outKey= new Text();
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line=value.toString();
        StringTokenizer itr = new StringTokenizer(line);
        //先按行分，再按空格分
        while (itr.hasMoreTokens()){
            String str=itr.nextToken();
            String[] splits=str.split(" ");//按空格
            // 一共三门课
            for (int i=1;i<=3;i++){
                String name=Integer.toString(i);
                int score = Integer.parseInt(splits[i]);
                outKey.set(name);
                context.write(outKey,new IntWritable(score));
            }
        }

    }
}
---
package DSPPCode.mapreduce.average_score.impl;

import DSPPCode.mapreduce.average_score.question.ScoreReducer;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.IOException;

public class ScoreReducerImpl extends ScoreReducer {
    private IntWritable result = new IntWritable();
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int courseCount=0;
        int sum=0;
        float avgScore=0;
        for (IntWritable val : values) {
            sum += val.get();
            courseCount++;
        }
        avgScore=sum/courseCount;
        result.set((int) avgScore);

        context.write(key,result);
    }
}
