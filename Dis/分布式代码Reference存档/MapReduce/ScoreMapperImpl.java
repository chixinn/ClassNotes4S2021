package DSPPCode.mapreduce.average_score.impl;

import DSPPCode.mapreduce.average_score.question.ScoreMapper;
import DSPPCode.mapreduce.average_score.question.Util;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class ScoreMapperImpl  extends ScoreMapper {
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] datas=value.toString().split(",");
//        for(String data:datas){
//            System.out.println(data);
//        }
//        System.out.println(datas.length);
//        System.out.println(datas);
//        for (int i=0;i<3;i++){
//            System.out.println(Util.getCourseName(i));
//        }
        for (int i=1;i< 4;i++){
            context.write(new Text( Util.getCourseName(i-1)),new IntWritable(Integer.parseInt(datas[i])));
        }

    }
}
