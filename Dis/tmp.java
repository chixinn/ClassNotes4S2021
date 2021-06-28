//其实这个绕是没有必要的
    List<Tuple2<Integer,Integer>> indexTimeList=indexTimeRDD.collect();
    ArrayList<Tuple2<Integer,Integer>> indexTimeArrayList = new ArrayList<>(indexTimeList);
    // System.out.println(indexTimeArrayList);
    List<Tuple2<Integer,String>> averageResultList=new ArrayList<>();
    Tuple2<Integer, Integer> ele0 = indexTimeArrayList.get(0);
    Tuple2<Integer, Integer> ele1 = indexTimeArrayList.get(1);
    Tuple2<Integer, Integer> ele2 = indexTimeArrayList.get(2);
    Tuple2<Integer, Integer> ele3 = indexTimeArrayList.get(3);
    Tuple2<Integer, Integer> ele0_ = indexTimeArrayList.get(indexTimeArrayList.size()-1);
    Tuple2<Integer, Integer> ele1_ = indexTimeArrayList.get(indexTimeArrayList.size()-2);
    Tuple2<Integer, Integer> ele2_ = indexTimeArrayList.get(indexTimeArrayList.size()-3);
    Tuple2<Integer, Integer> ele3_ = indexTimeArrayList.get(indexTimeArrayList.size()-4);

    for(int i=0;i<indexTimeArrayList.size();i++){
      if(i==0){
        Double averTmp = math.floor((ele0._2 + ele1._2 + ele2._2) * 1.0 / 3);
        String averageResult = "[" + ele0._1.toString() + "," + averTmp.toString() + "]" + "\n";
        averageResultList.add(new Tuple2<>(ele0._1,averageResult));
      }
      else if(i==1){
        Double averTmp = math.floor((ele0._2 + ele1._2 + ele2._2+ele3._2) * 1.0 / 4);
        String averageResult = "[" + ele1._1.toString() + "," + averTmp.toString() + "]" + "\n";
        averageResultList.add(new Tuple2<>(ele1._1,averageResult));
      }
      else if(i==indexTimeArrayList.size()-1){
        Double averTmp = math.floor((ele0_._2 + ele1_._2 + ele2_._2) * 1.0 / 3);
        String averageResult = "[" + ele0_._1.toString() + "," + averTmp.toString() + "]" + "\n";
        averageResultList.add(new Tuple2<>(ele0_._1,averageResult));
      }
      else if(i==indexTimeArrayList.size()-2){
        Double averTmp = math.floor((ele0_._2 + ele1_._2 + ele2_._2+ele3_._2) * 1.0 / 4);
        String averageResult = "[" + ele1_._1.toString() + "," + averTmp.toString() + "]" + "\n";
        averageResultList.add(new Tuple2<>(ele1_._1,averageResult));
      }
      else{
        Double averTmp=math.floor(
            (indexTimeArrayList.get(i)._2+indexTimeArrayList.get(i-1)._2+
                indexTimeArrayList.get(i-2)._2+indexTimeArrayList.get(i+1)._2+indexTimeArrayList.get(i+2)._2)*1.0/5
        );
        String averageResult = "[" + indexTimeArrayList.get(i)._1.toString() + "," + averTmp.toString() + "]" + "\n";
        averageResultList.add(new Tuple2<>(indexTimeArrayList.get(i)._1,averageResult));
      }
    }
    // 将 t 时间点本身以及前后各 k 个时间点的值的平均值 当作 t 时间点的值，
    Integer k=3;
    System.out.println(averageResultList);
    JavaRDD<String> timeSeries=indexTimeRDD.map(
        new Function<Tuple2<Integer, Integer>, String>() {
          @Override
          public String call(Tuple2<Integer, Integer> integerIntegerTuple2) throws Exception {
            for (Tuple2<Integer, String> integerStringTuple2 : averageResultList) {
              if (integerStringTuple2._1.equals(integerIntegerTuple2._1)) {
                return integerStringTuple2._2;
              }
            }
            return "";
          }
        }
    );


    // Queue<String> queue = new LinkedList<String>();


    // while(indexTimeArrayList.size()>3){
    //   Tuple2<Integer, Integer> ele0 = indexTimeArrayList.get(0);
    //   Tuple2<Integer, Integer> ele1 = indexTimeArrayList.get(1);
    //   Tuple2<Integer, Integer> ele2 = indexTimeArrayList.get(2);
    //   Double averTmp = math.floor((ele0._2 + ele1._2 + ele2._2) * 1.0 / 3);
    //   String averageResult =
    //       "[" + ele0._1.toString() + "," + averTmp.toString() + "]" + "\n";
    //   averageResultList.add(new Tuple2<>(ele0._1,averageResult));
    //   indexTimeArrayList.remove(0);
    // }
    // System.out.println(indexTimeArrayList.size());
    // Tuple2<Integer, Integer> ele0 = indexTimeArrayList.get(0);
    // Tuple2<Integer, Integer> ele1 = indexTimeArrayList.get(1);
    // Tuple2<Integer, Integer> ele2 = indexTimeArrayList.get(2);
    // Double averTmp = math.floor((ele0._2 + ele1._2 + ele2._2) * 1.0 / 3);
    // String averageResult1 =
    //     "[" + ele0._1.toString() + "," + averTmp.toString() + "]" + "\n";
    // averageResultList.add(new Tuple2<>(ele0._1,averageResult1));
    // String averageResult2 =
    //     "[" + ele1._1.toString() + "," + averTmp.toString() + "]" + "\n";
    // averageResultList.add(new Tuple2<>(ele1._1,averageResult2));
    // String averageResult3 =
    //     "[" + ele2._1.toString() + "," + averTmp.toString() + "]" + "\n";
    // averageResultList.add(new Tuple2<>(ele2._1,averageResult3));
    // // System.out.println(averageResultList.size());
    // System.out.println(averageResultList);

    timeSeries.foreach(System.out::println);

    return timeSeries;
    // return null;