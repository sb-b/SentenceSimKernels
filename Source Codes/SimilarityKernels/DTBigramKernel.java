/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SimilarityKernels;


import static SimilarityKernels.DTBigramKernel.createBigrams;
import dragon.ir.clustering.docdistance.CosineDocDistance;
import dragon.nlp.tool.PorterStemmer;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import tree.TreeNode;

/**
 *
 * @author betul
 */
public class DTBigramKernel {
    
    public static double computeDTBigramSimilarity(TreeNode<ArrayList<String>> node1, TreeNode<ArrayList<String>> node2)
    {
        double sim=0;
        ArrayList<String> bigramList1 = new ArrayList<>();
        ArrayList<String> bigramList2 = new ArrayList<>();
        
        PorterStemmer stemmer = new PorterStemmer();
       
        bigramList1 = createBigrams(node1, bigramList1, stemmer, true);
        bigramList2 = createBigrams(node2, bigramList2, stemmer, true);

        sim = computeBigramCosineSimilarity(bigramList1, bigramList2);
      
        return sim;
    }
    
    public static ArrayList<String> createBigrams(TreeNode<ArrayList<String>> node1, ArrayList<String> bigramList, PorterStemmer stemmer, boolean isFirstTime)
    {
        String node1_word = stemmer.stem(node1.data.get(0));
        
//         if(importantType(node1.data.get(7)))//true)//!node1.data.get(2).equals("prep") && !node1.data.get(2).equals("det"))
//         {
          //dep_ancestor code begin
//                  TreeNode<ArrayList<String>> temp = node1;
//                  
//                  if(!temp.data.get(7).equals("root"))
//                  {
//                  TreeNode<ArrayList<String>> temp_parent = temp.parent;
//                  
//                      String temp_parent_word = stemmer.stem(temp_parent.data.get(0));
//                      while(!temp_parent.data.get(7).equals("root"))
//                      {
//                          String temp_word = stemmer.stem(temp.data.get(0));
//
//                          temp_parent_word = stemmer.stem(temp_parent.data.get(0));
//
//                          if(!bigramList.contains(temp_word+"#"+temp_parent_word))
//                             bigramList.add(temp_word+"#"+temp_parent_word);
//                          temp_parent = temp_parent.parent;
//                      }
//                      if(!bigramList.contains(temp_parent_word+"#"+"ROOT"))
//                          bigramList.add(temp_parent_word+"#"+"ROOT");
//                  }
//                  else
//                      bigramList.add(node1_word+"#"+"ROOT");
//                  //dep_ancestor code end
//         }          
          if(node1.children.size() ==0)
               return bigramList;
           else
           {
               
               TreeNode<ArrayList<String>> centerNode = node1;
             
               if(isFirstTime)
               {

                  
                   if(node1.data.get(7).equals("root"))
                   {
                       
                       if(!unimportantType(node1.data.get(7)))
                       {
                           //bigramList.add(node1_word);   //W
                           //bigramList.add(node1_word+"#"+node1.data.get(7)+"#"+"ROOT");//DTH
                           bigramList.add(node1_word+"#"+"ROOT");
                           
                          // bigramList.add(node1_word+"#"+node1.data.get(7));
                          // bigramList.add(node1.data.get(7)+"#"+"ROOT");
                       
                       }
                       

                               
                   } 
                   else
                   {
                      String node1_parent_word = stemmer.stem(node1.parent.data.get(0));

                      if(!unimportantType(node1.data.get(7)))
                       {
                           //bigramList.add(node1_word);   //W
                         //  bigramList.add(node1_word+"#"+node1.data.get(7)+"#"+node1_parent_word);//DTH
                             bigramList.add(node1_word+"#"+node1_parent_word);
                             
                          //   bigramList.add(node1_word+"#"+node1.data.get(7));
                            // bigramList.add(node1.data.get(7)+"#"+node1_parent_word);
                           
                       }
                  
                   }      
               //} 
               }
               if(centerNode.children.size() > 0)
               {
                   for (int j = 0; j < centerNode.children.size(); j++) {
                       
                       TreeNode<ArrayList<String>> tmpNode1 = centerNode.children.get(j);
                       
                       String tmpNode_word = stemmer.stem(tmpNode1.data.get(0));
                       String tmpNode_parent_word = stemmer.stem(tmpNode1.parent.data.get(0));
                      
                       if(!unimportantType(tmpNode1.data.get(7)))
                       {
                         //  bigramList.add(tmpNode_word);   //W
                         // bigramList.add(tmpNode_word+"#"+tmpNode1.data.get(7)+"#"+tmpNode_parent_word);//DTH
                           bigramList.add(tmpNode_word+"#"+tmpNode_parent_word);//DH 
                           
                          // bigramList.add(tmpNode_word+"#"+tmpNode1.data.get(7));
                          // bigramList.add(tmpNode1.data.get(7)+"#"+tmpNode_parent_word);
                     
                       }
                      
                      
                       createBigrams(centerNode.children.get(j), bigramList, stemmer, false);
                   }
               }              
           }
          
        return bigramList;
    }
    
     public static double computeBigramCosineSimilarity(ArrayList<String>  bigramList1, ArrayList<String>  bigramList2)
  {
  
      double sim =0.0;
      List<String> common_bigrams = new ArrayList<String>();
      
      for (int j = 0; j < bigramList1.size(); j++) {
          
          
          String bigram1= bigramList1.get(j);
          
          for (int l = 0; l < bigramList2.size(); l++) {
              
              String bigram2 = bigramList2.get(l);
              
              if(bigram1.equals(bigram2))
              {
                  sim = sim + 1;
              }         
          }
          
          
      }
     // System.out.println(common_bigrams);
      //System.out.println("*******************************************************");
      if(bigramList1.size() + bigramList2.size() != 0)
         sim = sim / (bigramList1.size() + bigramList2.size());
     return sim;
  }
  
  public static boolean unimportantType(String type)
  {
      //unimp4
      if( type.equals("det") ||
         type.equals("expl") || type.equals("goeswith") || type.equals("possesive") || type.equals("preconj") ||
         type.equals("predet") || type.equals("prep") || type.equals("punct") || type.equals("ref") )
          return true;
      //unimp3
//      if(type.equals("dep") || type.equals("det") ||
//         type.equals("expl") || type.equals("goeswith") || type.equals("possesive") || type.equals("preconj") ||
//         type.equals("predet") || type.equals("prep") || type.equals("punct") || type.equals("ref") )
//          return true;
      //unimp2
//      if(type.equals("dep") || type.equals("det") || type.equals("aux") || type.equals("auxpass") ||
//         type.equals("expl") || type.equals("goeswith") || type.equals("possesive") || type.equals("preconj") ||
//         type.equals("predet") || type.equals("prep") || type.equals("punct") || type.equals("ref") )
//          return true;
      //unimp
//      if(type.equals("aux") ||type.equals("auxpass") || type.equals("cc") || type.equals("dep") ||
//              type.equals("det") ||type.equals("expl") || type.equals("goeswith") || type.equals("mark") ||
//              type.equals("mwe") ||type.equals("parataxis") || type.equals("pcomp") || type.equals("possesive") ||
//              type.equals("preconj") ||type.equals("pobj") || type.equals("predet") || type.equals("prep") ||
//              type.equals("punct") ||type.equals("ref"))
//          return true;
      else
          return false;
  }
  
   public static boolean importantType(String type)
  {
      if(type.equals("agent") ||type.equals("cop") || type.equals("root") || type.equals("dobj") ||
              type.equals("iobj") ||type.equals("nn") || type.equals("npadvmod") || type.equals("nsubj") ||
              type.equals("nsubjpass") ||type.equals("num") || type.equals("tmod") || type.equals("xsubj"))
          return true;
      else
          return false;
  }
  
}
