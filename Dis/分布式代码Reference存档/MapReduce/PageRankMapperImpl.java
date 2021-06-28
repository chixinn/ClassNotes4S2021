package DSPPCode.mapreduce.pagerank.impl;

import DSPPCode.mapreduce.pagerank.question.PageRankMapper;
import DSPPCode.mapreduce.pagerank.question.PageRankRunner;
import DSPPCode.mapreduce.pagerank.question.ReducePageRankWritable;
import DSPPCode.mapreduce.pagerank.question.utils.Rank;
import DSPPCode.mapreduce.pagerank.question.utils.RanksOperation;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageRankMapperImpl extends PageRankMapper {
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        List<Rank> ranks=new ArrayList<>();
        String [] pageInfo;
        if(PageRankRunner.iteration==0){
            String[] data=value.toString().split(" ");
            //获取广播的数据
            String pagerank=context.getCacheFiles()[0].toString();
            //将聚类中心加载
            ranks= RanksOperation.getRanks(pagerank,false);
            String rankValue="";
            String pageName=data[0];
//            System.out.println("ranks打印"+ranks);
            //连接网页名与网页排名
            //构造课本上那种形式
            for (Rank rank : ranks) {
                if (rank.getPageName().equals(pageName)) {
                    rankValue = String.valueOf(rank.getRank());
                    break;
                }
            }
            pageInfo=new String[data.length*2];//为什么这里data.lenght*2
            for(int i=0;i< data.length;i++){
                pageInfo[i*2]=data[i];
                if(i==0){
                    pageInfo[1]=rankValue;
                }else{
                    pageInfo[i*2+1]="1.0";
                }
            }
        }else{
            //以空格为分隔符切分
            pageInfo=value.toString().split(" ");
        }
        System.out.println("pageInfo打印测试");
        for(int i=0;i< pageInfo.length;i++){
            System.out.print("pageInfo"+" "+i);
            System.out.print(pageInfo[i]+"\n");
        }
        //输入 A 1.0 B 1.0 D 1.0
        //把输入 A B D A 1变成A 1.0 B 1.0 D 1.0
        double pageRank=Double.parseDouble(pageInfo[1]);
        int outLink=(pageInfo.length-2)/2;
        ReducePageRankWritable writable;
        writable=new ReducePageRankWritable();
        //计算贡献值并保存
        writable.setData(String.valueOf(pageRank/outLink));
        //设置对应标识
        writable.setTag(ReducePageRankWritable.PR_L);
        //对于每一个出站链接输出贡献值
        for(int i=2;i<pageInfo.length;i+=2){
            context.write(new Text(pageInfo[i]),writable);
        }
        writable = new ReducePageRankWritable();
        //保存网页信息并标识
        StringBuilder pageInfoResult=new StringBuilder();
        if(PageRankRunner.iteration==0){
            for(String data:pageInfo){
                pageInfoResult.append(data).append(" ");
            }
            writable.setData(pageInfoResult.toString());
        }else{
            writable.setData(value.toString());
        }
//        writable.setData(value.toString());
        writable.setTag(ReducePageRankWritable.PAGE_INFO);
        //以输入的网页信息的网页名称为key进行输出
        context.write(new Text(pageInfo[0]),writable);
    }
}
