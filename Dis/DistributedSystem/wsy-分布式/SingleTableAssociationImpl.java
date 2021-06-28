package DSPPCode.spark.single_table_association.impl;


import DSPPCode.spark.single_table_association.question.SingleTableAssociation;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

public class SingleTableAssociationImpl extends SingleTableAssociation
{

  @Override
  public JavaRDD<Tuple2<String, String>> singleTableAssociation(JavaRDD<String> lines) {
    JavaPairRDD<String, String> c2p =
        lines.mapToPair(line->{
          String[] words = line.split(" ");
          return new Tuple2<>(words[0], words[1]);
        });



    JavaPairRDD<String, String> p2c =
        lines.mapToPair(line->{
          String[] words = line.split(" ");
          return new Tuple2<>(words[1], words[0]);
        });

    JavaPairRDD<String,Tuple2<String,String>> c2g =
        p2c.join(c2p);

        return c2g.map(t->t._2);
  }
}
