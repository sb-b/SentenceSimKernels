/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SimilarityKernels;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import tree.createTree;

/**
 *
 * @author betul
 */
public class TDTKernel {
    
       public static List<String> extractImportantTypedDependencies(String sentence, LexicalizedParser lp, TokenizerFactory tf)
       {
            Collection<TypedDependency> dependency_list = new ArrayList <>();

            List tokens = tf.getTokenizer(new StringReader(sentence)).tokenize(); 
            lp.parse(tokens); // parse the tokens
            Tree t = lp.parse(sentence); // get the best parse tree
       
                                
            TreebankLanguagePack tlp = lp.getOp().tlpParams.treebankLanguagePack();
            GrammaticalStructureFactory factory = tlp.grammaticalStructureFactory();
            GrammaticalStructure depTree = factory.newGrammaticalStructure(t);

            dependency_list = depTree.typedDependencies();
            
//            List<String> nerList = new ArrayList<>();
//            nerList.add(classifier.classifyToString(sentence));
            Object[] list = dependency_list.toArray();
            List<String> dep_list = new ArrayList<>();
            
            for(Object dependency : list)
            {
                int par_index = dependency.toString().indexOf("(");
                String dep_type = dependency.toString().substring(0, par_index);
                if(dep_type.contains("subj") ||dep_type.contains("obj"))
                    dep_list.add(dependency.toString());
            }
            
            return dep_list;
                       
       }
       
       public static double computeSimilarity(List<String> sent1, List<String> sent2)
       {
           double similarity = 0.0, old_similarity=0.0;
           for(String dep1 : sent1)
           {
               
               for(String dep2 : sent2)
               {
                   old_similarity = similarity;
                   int par_ind = dep1.indexOf("(");
                   String type1 = dep1.substring(0, par_ind);
                   String other1 = dep1.substring(par_ind+1);
                   
                   par_ind = dep2.indexOf("(");
                   String type2 = dep2.substring(0, par_ind);
                   String other2 = dep2.substring(par_ind+1);
                   
                  // System.out.println(type1+" "+type2);
                   if(true)//type1.equals(type2))
                   {
                       int comma_ind = other1.indexOf(",");
                       String verb1 = other1.substring(0, comma_ind);
                       String dependent1 = other1.substring(comma_ind+1, other1.length()-1);
                       
                       int slash_ind = verb1.lastIndexOf("-");
                       verb1 = verb1.substring(0,slash_ind);
                       
                       slash_ind = dependent1.lastIndexOf("-");
                       dependent1 = dependent1.substring(0, slash_ind);
                       
                       
                       comma_ind = other2.indexOf(",");
                       String verb2 = other2.substring(0, comma_ind);
                       String dependent2 = other2.substring(comma_ind+1, other2.length()-1);
                       
                       slash_ind = verb2.lastIndexOf("-");
                       verb2 = verb2.substring(0,slash_ind);
                       
                       slash_ind = dependent2.lastIndexOf("-");
                       dependent2 = dependent2.substring(0, slash_ind);

                       String dep_hypernyms1 = createTree.getHypernyms(dependent1, "noun");
                       String dep_hypernyms2 = createTree.getHypernyms(dependent2, "noun");
                       
                       String hypernyms1 = createTree.getHypernyms(verb1, "verb");
                       String hypernyms2 = createTree.getHypernyms(verb2, "verb");
                       
                       if( verb1.equals(dependent2) || verb2.equals(dependent1))
                           similarity += 1000;
                       if(verb1.equals(verb2))
                       {
                           if(dependent1.equals(dependent2))
                               similarity += 10000;
                           else
                           {
                               if(!type1.equals("pobj") && !type2.equals("pobj"))
                               {
                               if(hypernymSimilarity(dep_hypernyms1, dep_hypernyms2) > 0  )
                                   similarity += 50;
                               else
                                   similarity += 30;
                               }
                               else
                               {
                                   if(hypernymSimilarity(dep_hypernyms1, dep_hypernyms2) > 0  )
                                   similarity += 7;
                               else
                                   similarity += 5;
                               
                               }
                                       
                           }
                       }
                       else
                       {
                           
                           if(hypernymSimilarity(hypernyms1, hypernyms2) > 0)
                           {
                               if(dependent1.equals(dependent2))
                                   similarity += 1000;
                               else
                               {

                                   if(!type1.equals("pobj") && !type2.equals("pobj"))
                                   {
                                   if(hypernymSimilarity(dep_hypernyms1, dep_hypernyms2) > 0)
                                       similarity += 10;
                                   else
                                       similarity += 5;
                                   }
                                   else
                                   {
                                       if(hypernymSimilarity(dep_hypernyms1, dep_hypernyms2) > 0)
                                       similarity += 2;
                                   else
                                       similarity += 1;
                                   }

                               }
                           
                           }
                           else
                           {
                               if(dependent1.equals(dependent2))
                                   similarity += 500;
                               else
                               {

                                   if(hypernymSimilarity(dep_hypernyms1, dep_hypernyms2) > 0)
                                       similarity += 1;
                                   else
                                       similarity += 0;

                               }   
                           }
                           
                       
                       }
                   }
                   //System.out.println(dep1 + " " + dep2 + " "+ (similarity-old_similarity));
               }
           }
           return similarity;
       }
       
       public static double hypernymSimilarity(String hypernyms1, String hypernyms2)
       {
           //hypernym similarity
           double similarity = 0.0;
                           List<String> node1_hypernyms = new ArrayList<String>(Arrays.asList(hypernyms1.split("#")));
                           List<String> node2_hypernyms = new ArrayList<String>(Arrays.asList(hypernyms2.split("#")));
                            if(node1_hypernyms.size() < node2_hypernyms.size())
                            {
                            node1_hypernyms.retainAll(node2_hypernyms);

                            if(node1_hypernyms.size() > 0)
                                similarity = similarity + 2*node1_hypernyms.size();
                            }
                            else
                            {
                                node2_hypernyms.retainAll(node1_hypernyms);

                                if(node2_hypernyms.size() > 0)
                                    similarity = similarity + 1;//2*node2_hypernyms.size();
                                
                            }
                            return similarity;
       }
}
