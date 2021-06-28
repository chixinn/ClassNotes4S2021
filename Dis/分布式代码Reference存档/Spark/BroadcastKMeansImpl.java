package DSPPCode.spark.broadcast_k_means.impl;

import DSPPCode.spark.broadcast_k_means.question.BroadcastKMeans;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import java.util.List;

public class BroadcastKMeansImpl extends BroadcastKMeans {


  @Override
  //请在此方法中实现选择距离p点最近的聚类中心点的功能
  //return 距离中心点中，距离p最近的那个点的下标
  public Integer closestPoint(List<Integer> p, Broadcast<List<List<Double>>> kPoints) {
    Integer bestIndex=0;
    Double closest=Double.MAX_VALUE;
    for(int i=0;i<kPoints.getValue().size();i++){
      Double dist=distanceSquared(p,kPoints.getValue().get(i));
      if(dist < closest){
        closest=dist;
        bestIndex=Integer.valueOf(i);
      }
    }
    return bestIndex;
    // return null;
  }
  /**
   * TODO 请完成该方法
   * <p>
   * 请在次方法中创建广播变量
   *
   * @param localVariable 本地变量
   * @return 广播变量
   */
  // 把聚类中心点设为广播变量
  // Broadcast<List<List<Double>>> broadcastKPoints = createBroadcastVariable(sc, kPoints);
  @Override
  public Broadcast<List<List<Double>>> createBroadcastVariable(JavaSparkContext sc,
      List<List<Double>> localVariable) {
    Broadcast<List<List<Double>>>broadcastKPoints=sc.broadcast(localVariable);
    return broadcastKPoints;
    // return null;
  }
}
