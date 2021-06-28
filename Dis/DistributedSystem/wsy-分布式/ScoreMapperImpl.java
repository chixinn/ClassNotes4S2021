package DSPPCode.mapreduce.average_score.impl;

import DSPPCode.mapreduce.average_score.question.ScoreMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ScoreMapperImpl extends ScoreMapper {

    @Override
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException{
        String[] scores = value.toString().split(",");
        for (int i = 1; i < scores.length; i++) {
            context.write(new Text(Integer.toString(i-1)),new IntWritable(Integer.parseInt(scores[i])));
        }
    }

}
