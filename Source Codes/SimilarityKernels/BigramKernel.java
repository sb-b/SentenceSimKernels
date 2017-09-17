/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SimilarityKernels;

import static SimilarityKernels.DTBigramKernel.unimportantType;
import dragon.nlp.tool.PorterStemmer;
import java.util.ArrayList;
import java.util.List;
import tree.TreeNode;

/**
 *
 * @author betul
 */
public class BigramKernel {
    
     public static double computeBigramSimilarity(TreeNode<ArrayList<String>> node1, TreeNode<ArrayList<String>> node2, List<String> first_sentence_terms, List first_sentence_tfidf, List<String> second_sentence_terms, List second_sentence_tfidf)
    {
        double sim=0;
        ArrayList<bigram> bigramList1 = new ArrayList<>();
        ArrayList<bigram> bigramList2 = new ArrayList<>();
        
        PorterStemmer stemmer = new PorterStemmer();
       
        bigramList1 = createBigrams(node1, bigramList1, stemmer, true);
        bigramList2 = createBigrams(node2, bigramList2, stemmer, true);

        sim = computeBigramSimilarity(bigramList1, bigramList2, first_sentence_terms, first_sentence_tfidf, second_sentence_terms, second_sentence_tfidf);
      
        return sim;
    }
     
      public static ArrayList<bigram> createBigrams(TreeNode<ArrayList<String>> node1, ArrayList<bigram> bigramList, PorterStemmer stemmer, boolean isFirstTime)
    {
        String node1_word = stemmer.stem(node1.data.get(0));//.toLowerCase();
                
          if(node1.children.size() ==0)
               return bigramList;
           else
           {
               
               TreeNode<ArrayList<String>> centerNode = node1;
             
               if(isFirstTime)
               {
                  
                   if(node1.data.get(7).equals("root"))
                   {
           
                       if(!unimportantType(node1.data.get(7)))//true)//!node1.data.get(2).equals("prep") && !node1.data.get(2).equals("det"))
                       {
                          bigram bigram_obj = new bigram();
                          bigram_obj.word = node1_word;
                          bigram_obj.type = node1.data.get(7);
                          bigram_obj.parent = "ROOT";
                          bigram_obj.node = node1;
                          bigram_obj.match = false;
                          
                          bigramList.add(bigram_obj);
               
                       }
                             
                   } 
                   else
                   {
                      String node1_parent_word = stemmer.stem(node1.parent.data.get(0));//.toLowerCase();
                  
                   if(!unimportantType(node1.data.get(7)))//true)//!node1.data.get(2).equals("prep") && !node1.data.get(2).equals("det"))
                       {
                      
                          bigram bigram_obj = new bigram();
                          bigram_obj.word = node1_word;
                          bigram_obj.type = node1.data.get(7);
                          bigram_obj.parent = node1_parent_word;
                          bigram_obj.node = node1;
                          bigram_obj.match = false;
                          
                          bigramList.add(bigram_obj);
                       }
                  
                   }      
           
               }
               if(centerNode.children.size() > 0)
               {
                   for (int j = 0; j < centerNode.children.size(); j++) {
                       
                       TreeNode<ArrayList<String>> tmpNode1 = centerNode.children.get(j);
                       
                       String tmpNode_word = stemmer.stem(tmpNode1.data.get(0));//.toLowerCase();
                       String tmpNode_parent_word = stemmer.stem(tmpNode1.parent.data.get(0));//.toLowerCase();

                       if(!unimportantType(tmpNode1.data.get(7)))//true)//!tmpNode1.data.get(2).equals("prep") && !tmpNode1.data.get(2).equals("det"))
                       {
                        
                           bigram bigram_obj = new bigram();
                           bigram_obj.word = tmpNode_word;
                           bigram_obj.type = tmpNode1.data.get(7);
                           bigram_obj.parent = tmpNode_parent_word;
                           bigram_obj.node = tmpNode1;
                           bigram_obj.match = false;
                          
                           bigramList.add(bigram_obj);
                           
                       }
                      
                      
                       createBigrams(centerNode.children.get(j), bigramList, stemmer, false);
                   }
               }              
           }
          
        return bigramList;
    }
        public static double computeBigramSimilarity(ArrayList<bigram>  bigramList1, ArrayList<bigram>  bigramList2, List<String> first_sentence_terms, List first_sentence_tfidf, List<String> second_sentence_terms, List second_sentence_tfidf)
  {
  
      double sim =0.0;
      double denom1 = 0.0, denom2 = 0.0;

      PorterStemmer stemmer = new PorterStemmer();

      for (int j = 0; j < bigramList1.size(); j++) {

          double max_sim = 0, temp_sim = 0;
          
          for (int l = 0; l < bigramList2.size(); l++) {
              
              temp_sim = similarity(1.6,3,bigramList1.get(j), bigramList2.get(l), first_sentence_terms, first_sentence_tfidf, second_sentence_terms, second_sentence_tfidf);
              sim += temp_sim;//similarity(1.6,3,bigramList1.get(j), bigramList2.get(l), first_sentence_terms, first_sentence_tfidf, second_sentence_terms, second_sentence_tfidf);
              
             // sim += temp_sim;
              if(bigramList1.get(j).word.toLowerCase().equals(bigramList2.get(l).word.toLowerCase()))// && bigramList1.get(j).parent.toLowerCase().equals(bigramList2.get(l).parent.toLowerCase()))
                  sim += recursiveChildren( bigramList1.get(j).node, bigramList2.get(l).node, stemmer, first_sentence_tfidf, first_sentence_terms);
              if(temp_sim > max_sim)
                  max_sim = temp_sim;
          }
          
          sim += 4*max_sim;

      }
     // denominators
      for (int l = 0; l < bigramList1.size(); l++) {

            String dep1 = bigramList1.get(l).word;
            String head1 = bigramList1.get(l).parent;
            
                  if(first_sentence_terms.contains(dep1.toLowerCase()))
                  {
                     double temp = (double) first_sentence_tfidf.get(first_sentence_terms.indexOf(dep1.toLowerCase()));                
                     denom1 = denom1 +  Math.pow(temp,2); 
                  }
                  else
                  {
                      denom1 += 0.5*0.5;
                  }
                  if(first_sentence_terms.contains(head1.toLowerCase()))
                  {
                     double temp = (double) first_sentence_tfidf.get(first_sentence_terms.indexOf(head1.toLowerCase()));
                     denom1 = denom1 +  Math.pow(temp,2);
                  }
                  else
                  {
                      denom1 += 0.5*0.5;
                  }
                 
      }   
       for (int l = 0; l < bigramList2.size(); l++) {

            String dep2 = bigramList2.get(l).word;
            String head2 = bigramList2.get(l).parent;
            
                  if(second_sentence_terms.contains(dep2.toLowerCase()))
                  {
                     double temp = (double) second_sentence_tfidf.get(second_sentence_terms.indexOf(dep2.toLowerCase()));                
                     denom2 = denom2 +  Math.pow(temp,2); 
                  }
                  else
                  {
                      denom2 += 0.5*0.5;
                  }
                  if(second_sentence_terms.contains(head2.toLowerCase()))
                  {
                     double temp = (double) second_sentence_tfidf.get(second_sentence_terms.indexOf(head2.toLowerCase()));
                     denom2 = denom2 +  Math.pow(temp,2);
                  }
                  else
                  {
                      denom2 += 0.5*0.5;
                  }
                 
      }   

//      if(bigramList1.size() > 0)
//          sim = (double) sim/bigramList1.size();
        if((Math.sqrt(denom1) * Math.sqrt(denom2))!=0)
            sim = sim / (Math.sqrt(denom1) * Math.sqrt(denom2));
//      if(bigramList1.size() + bigramList2.size() > 0)
//          sim = sim / (bigramList1.size() + bigramList2.size());
      
      return sim ;
  }
        
        public static double similarity(double u, double t, bigram bigram1, bigram bigram2, List<String> first_sentence_terms, List first_sentence_tfidf, List<String> second_sentence_terms, List second_sentence_tfidf)
        {
            String dep1 = bigram1.word;
            String type1 = bigram1.type;
            String head1 = bigram1.parent;
          
            
          
          
            String dep2 = bigram2.word;
            String type2 = bigram2.type;
            String head2 = bigram2.parent;

            double dep_temp = 0, head_temp = 0;
            boolean dep_flag = false, head_flag = false;
            if(dep1.toLowerCase().equals(dep2.toLowerCase()))//tfidf1
            {      
                double dep1val = 0.5;
            if(first_sentence_terms.contains(dep1.toLowerCase()))
            {
                dep1val = (double) first_sentence_tfidf.get(first_sentence_terms.indexOf(dep1.toLowerCase()));
            
            }
                  double temp = dep1val;//(double) first_sentence_tfidf.get(first_sentence_terms.indexOf(dep1.toLowerCase()));
                  dep_temp = temp *temp;// * (double) second_sentence_tfidf.get(second_sentence_terms.indexOf(dep2.toLowerCase()));
            
            
                  
                  if(Character.isUpperCase(dep1.charAt(0)))
                  {
                      dep_temp = dep_temp * u;
                  }  
                  
                 
                  dep_flag = true;
            }
            
          
            if(head1.toLowerCase().equals(head2.toLowerCase()))//tfidf1
            {
                 double head1val = 0.5;
            if(first_sentence_terms.contains(head1.toLowerCase()))// && second_sentence_terms.contains(head2.toLowerCase()))
            {
                head1val = (double) first_sentence_tfidf.get(first_sentence_terms.indexOf(head1.toLowerCase()));
            }
                  double temp = head1val;//(double) first_sentence_tfidf.get(first_sentence_terms.indexOf(head1.toLowerCase()));
                  head_temp = temp * temp;// * (double) second_sentence_tfidf.get(second_sentence_terms.indexOf(head2.toLowerCase()));
            
            
                  if(Character.isUpperCase(head1.charAt(0)))  
                  {
                      head_temp = head_temp * u;                      
                  }               
                  head_flag = true;
            }

//
//            if(type1.equals(type2) && dep_flag && head_flag)//tfidf4
//                  return 3.5*(dep_temp + head_temp);
            if(type1.equals(type2))
                  return t*(dep_temp + head_temp);
            else
                  return (dep_temp + head_temp);
             
        }
        public static double recursiveChildren( TreeNode<ArrayList<String>> temp1,  TreeNode<ArrayList<String>> temp2,PorterStemmer stemmer, List first_sentence_tfidf, List<String> first_sentence_terms)
    {
        double dep1val = 0.5, head1val = 0.5, temp_sim = 0;
        
             
                
         if(temp1.children.size() > 0 && temp2.children.size() > 0)
         {

             for(TreeNode<ArrayList<String>> child1 : temp1.children)
             {
                 for(TreeNode<ArrayList<String>> child2 : temp2.children )
                 {
                     if(stemmer.stem(child1.data.get(0)).toLowerCase().equals(stemmer.stem(child2.data.get(0)).toLowerCase()) && child1.data.get(7).equals(child2.data.get(7)))
                     {

//                         if(first_sentence_terms.contains(stemmer.stem(child1.data.get(0)).toLowerCase()))
//                            dep1val = (double) first_sentence_tfidf.get(first_sentence_terms.indexOf(stemmer.stem(child1.data.get(0)).toLowerCase()));
//
//                         if(first_sentence_terms.contains(stemmer.stem(child1.parent.data.get(0)).toLowerCase()))// && second_sentence_terms.contains(head2.toLowerCase()))
//                            head1val = (double) first_sentence_tfidf.get(first_sentence_terms.indexOf(stemmer.stem(child1.parent.data.get(0)).toLowerCase()));
//
//                       //  temp_sim += 3.5*((dep1val*dep1val) + (head1val*head1val));
                         //temp_sim +=  2.5*(dep1val + head1val);
                         
                         temp_sim += 1;
                     }
                    //temp_sim += 0.5*recursiveChildren(child1, child2, stemmer, first_sentence_tfidf, first_sentence_terms);

                 }
             }

         }
         return temp_sim;
        
    }
    
}
