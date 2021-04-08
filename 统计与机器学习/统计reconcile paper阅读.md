# 统计reconcile paper阅读

This “double-descent” curve subsumes the textbookU-shaped bias–variance trade-off curve by showing how increas-ing model capacity beyond the point of interpolation results inimproved  performance.

We  provide  evidence  for  the  existence and  ubiquity  of  double  descent  for  a  wide  spectrum  of  mod-els and datasets, and we posit a mechanism for its emergence.

This  connection  between  the  performance  and  the  structure  of machine-learning models delineates the limits of classical analy-ses and has implications for both the theory and the practice of machine learning.

经验风险Link:https://zhuanlan.zhihu.com/p/159189617

The challenge stems from the mis-match between the goals of minimizing the empirical risk (theexplicit goal of ERM algorithms, optimization) and minimizingthe true (or test) riskE(x,y)∼P[`(h(x),y)](the goal of machinelearning).

## Preface

  In  thiswork, we show how classical theory and modern practice canbe reconciled within a single unified performance curve andpropose a mechanism underlying its emergence.

## Classical U-shaped risk curve

Why?

- Classical underfit : all predictors inHmay underfit the train-ing data (i.e., have large empirical risk) and hence predict poorlyon new data. 2)

- Classical overfit:IfHis too large, the empirical risk minimizermay  overfit  spurious  patterns  in  the  training  data,  resulting  inpoor accuracy on new examples (small empirical risk but largetrue risk)

- The  control  of  thefunction class capacity may be explicit, via the choice ofH(e.g.,picking the neural network architecture), or it may be implicit,using regularization (e.g., early stopping).
-  zero training error 是什么 ？as large neural networks and other non-linear predictors that have very low or zero training risk.

When a suitable bal-ance  is  achieved,  the  performance  ofhnon  the  training  datais  said  to  generalize  to  the  populationP.

a model with zero training error isoverfit to the training data and will typically generalize poorly

 Indeed,  this  behavior  has  guided  a  best  practice  in  deeplearning for choosing neural network architectures, specificallythat  the  network  should  be  large  enough  to  permit  effortlesszero-loss  training  (called  interpolation)  of  the  training  data(4).  

### function  class  capacity:

In this paper, function class capacity is iden-tified with the number of parameters needed to specify a functionwithin the class

 Althoughthe  learned  predictors  obtained  at  the  interpolation  thresholdtypically  have  high  risk,  we  show  that  increasing  the  functionclass capacity beyond this point leads to decreasing risk, typicallygoing below the risk achieved at the sweet spot in the “classical”regime

how well the predictor matches the **inductive bias** appro-priate  for  the  problem  at  hand. 

### Inductive bias

