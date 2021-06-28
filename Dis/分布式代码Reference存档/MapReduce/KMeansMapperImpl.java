package DSPPCode.mapreduce.kmeans.impl;

import DSPPCode.mapreduce.kmeans.question.KMeansMapper;
import DSPPCode.mapreduce.kmeans.question.KMeansRunner;
import DSPPCode.mapreduce.kmeans.question.utils.CentersOperation;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeansMapperImpl extends KMeansMapper {
    private List<List<Double>> centers = new ArrayList<>();
    // 寻找每个数据点距离最近的聚类中心，在Map阶段实现，其中采用广播方式读取聚类中心

    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] dimensions;
        List<Double> point = new ArrayList<>();
        int centerIndex = 0;//为什么这里centerIndex要改成0呢
        double minDistance = Double.MAX_VALUE;
//        int iteration = context.getConfiguration().getInt(KMeansRunner.ITERATION, 0);
        if (centers.size() == 0) {
            // 获取广播的聚类中心集路径
            String centersPath = context.getCacheFiles()[0].toString();
            // 将聚类中心加载到集合centers
            centers = CentersOperation.getCenters(centersPath, true);
        }
        // 解析数据点
        dimensions = value.toString().split("[,\\t]");

        for (int i = 0; i < dimensions.length - 1; i++) {
            point.add(Double.parseDouble(dimensions[i]));
        }
        // 遍历聚类中心集并计算与数据点的距离
        for (int i = 0; i < centers.size(); i++) {
            double distance = 0;
            List<Double> center = centers.get(i);
            // 计算数据点与当前聚类中心之间的距离
            for (int j = 0; j < center.size(); j++) {
                distance += Math.pow((point.get(j) - center.get(j)), 2);
            }
            distance = Math.sqrt(distance);
            // 如果距离小于当前记录的最小距离则将数据点分配给当前聚类中心（类别号标识）
            if (distance < minDistance) {
                minDistance = distance;
                centerIndex = i;
            }

            // 从输入值中截取数据点
            /**
             * 注:
             *
             *   （1）相邻两次迭代的聚类中心之间的距离指欧式距离，所有聚类中心点迭代前后的欧式距离都小于设定阈值时，即可停止迭代。
             *
             *   （2）测试阶段，后台会将所有测试用例的阈值设定为0.05进行测试)
             */
        }
        String pointData = value.toString().split("\t")[0];
        if(KMeansRunner.compareResult){
                        context.write(new Text(pointData), new Text(String.valueOf(centerIndex)));

        }else{
                        context.write(new Text(String.valueOf(centerIndex)), new Text(pointData));


        }

    }
}
