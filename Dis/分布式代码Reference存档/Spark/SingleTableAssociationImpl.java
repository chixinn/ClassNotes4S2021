package DSPPCode.spark.single_table_association.impl;

import DSPPCode.spark.single_table_association.question.SingleTableAssociation;
import javafx.beans.property.adapter.JavaBeanBooleanProperty;
import org.apache.ivy.osgi.p2.P2Artifact;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.ml.param.JavaParams;
import org.codehaus.janino.Java;
import scala.Tuple2;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SingleTableAssociationImpl extends SingleTableAssociation {

  @Override
  public JavaRDD<Tuple2<String, String>> singleTableAssociation(JavaRDD<String> lines) {
    //读入child-Parent表
    JavaPairRDD<String, String> childParentRDD=lines.flatMap(v->Arrays.asList(v.split("\n")).iterator()).mapToPair(
        v->{
          String[] tokens=v.split(" ");
          return new Tuple2<>(tokens[0],tokens[1]);
        }
    );
    JavaPairRDD<String,String> parentChildRDD=childParentRDD.mapToPair(v->new Tuple2<>(v._2,v._1));

    JavaPairRDD<String, Tuple2<String, String> >  joinRdd = parentChildRDD.join(childParentRDD);
    JavaRDD<Tuple2<String,String>>associations=joinRdd.map(v->new Tuple2<>(v._2._1,v._2._2));
    return associations;
    // List<Tuple2<String,String>> associations_=joinRdd.mapToPair(v->new Tuple2<>(v._2._1,v._2._2)).collect();
    // return (JavaRDD<Tuple2<String, String>>) associations_;

    // 雇员表：[Dept,Name EmpId]
    // 部门表: [Dept,Manager]
    // 孩子-父母表：child-parent AB,BC;A,C; AB,BC(CB),B 相同join?
    // TableA join TableB on TableA.parent=TableB.child


  }
}