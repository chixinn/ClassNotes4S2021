package DSPPCode.mapreduce.kmeans.impl;

import DSPPCode.mapreduce.kmeans.question.KMeansReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeansReducerImpl extends KMeansReducer {
    // 计算同属一个聚类中心的数据点的新聚类中心
    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        List<List<Double>> points = new ArrayList<>();
        // 解析数据点并保存到集合points
        for (Text text : values) {
            String value = text.toString();
            List<Double> point = new ArrayList<>();
            for (String s : value.split(",")) {
                point.add(Double.parseDouble(s));
            }
            points.add(point);
        }
        StringBuilder newCenter = new StringBuilder();
        // 计算每个维度的平均值从而得到新的聚类中心
        for (int i = 0; i < points.get(0).size(); i++) {
            double sum = 0;
            // 计算第i个维度值的和
            for (List<Double> data : points) {
                sum += data.get(i);
            }
            // 计算平均值得到新的聚类中心的第i个维度值并生成需要输出的数据
            newCenter.append(sum / points.size());
            newCenter.append(",");
        }
        context.write(new Text(newCenter.toString()), NullWritable.get());
    }
}
