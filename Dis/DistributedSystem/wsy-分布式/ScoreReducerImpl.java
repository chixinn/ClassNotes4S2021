package DSPPCode.mapreduce.average_score.impl;

import DSPPCode.mapreduce.average_score.question.ScoreReducer;
import DSPPCode.mapreduce.average_score.question.Util;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ScoreReducerImpl extends ScoreReducer {
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        String className = Util.getCourseName(Integer.parseInt(key.toString()));
        int sum = 0;
        int cnt = 0;
        for (IntWritable value:values){
            sum += value.get();
            cnt++;
        }
        int ans = sum/cnt;
        context.write(new Text(className),new IntWritable(ans));
    }
}
