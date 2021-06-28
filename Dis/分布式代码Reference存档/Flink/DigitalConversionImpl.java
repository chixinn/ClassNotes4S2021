package DSPPCode.flink.digital_conversion.impl;

import DSPPCode.flink.digital_conversion.question.DigitalConversion;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import java.util.regex.Pattern;

public class DigitalConversionImpl extends DigitalConversion {

  @Override
  public DataStream<String> digitalConversion(DataStream<Tuple1<String>> digitals) {
    String[] num2Char = new String[]{"Zero","One","Two" ,"Three","Four","Five",
        "Six","Seven","Eight","Nine"};
    DataStream<String> digitPart=digitals.filter(
        new FilterFunction<Tuple1<String>>() {
          @Override
          public boolean filter(Tuple1<String> stringTuple1) throws Exception {
            Boolean integerFlag=isInteger(stringTuple1.f0);
           return integerFlag;
          }
        }
    ).map(
        new MapFunction<Tuple1<String>, String>() {
          @Override
          public String map(Tuple1<String> stringTuple1) throws Exception {
              Integer index=Integer.parseInt(stringTuple1.f0);
              return num2Char[index];
          }
        }
    );
    return digitPart;
  }
  public static boolean isInteger(String str) {
    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    return pattern.matcher(str).matches();
  }
}
