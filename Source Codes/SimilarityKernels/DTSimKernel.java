/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SimilarityKernels;

import static SimilarityKernels.DTBigramKernel.unimportantType;
import dragon.ir.index.IRDoc;
import dragon.ir.summarize.LexRankSummarizer;
import dragon.nlp.tool.PorterStemmer;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import tree.TreeNode;
import tree.createTree;

/**
 *
 * @author betul
 */
public class DTSimKernel {
    
        static double lambda = 0.5;
        
//        public static double computeSimilarityDT2(TreeNode<ArrayList<String>> node1, TreeNode<ArrayList<String>> node2,List<String> first_sentence_terms, List first_sentence_tfidf, List<String> second_sentence_terms, List second_sentence_tfidf)
//        {
//           double similarity_score = 0.0;
//            
//            if(matching(node1,node2) == 0)
//                return 0;
//            else
//            {
//                double d ;
//                similarity_score += similarity2(node1,node2, first_sentence_terms, first_sentence_tfidf, second_sentence_terms, second_sentence_tfidf);
//                int i, j=0;
//                for(i = 0; i < node1.children.size(); i++)
//                {
//                   
//                    for(j = 0; j < node2.children.size(); j++)
//                    {
//                        double a = Math.pow(lambda, (double)node1.children.size())*Math.pow(lambda, (double)node2.children.size())*computeSimilarityDT2(node1.children.get(i),node2.children.get(j),first_sentence_terms, first_sentence_tfidf, second_sentence_terms, second_sentence_tfidf);
//                        
//                        similarity_score += a;
//                                
//                    }
//                }
//                
//                return similarity_score;
//            }
//            
//            
//        }
//        
        public static double computeSimilarityDT(TreeNode<ArrayList<String>> node1, TreeNode<ArrayList<String>> node2)
        {
            double similarity_score = 0.0;
            
            if(matching(node1,node2) == 0)
                return 0;
            else
            {
                double d ;
                similarity_score += similarity(node1,node2);
                int i, j=0;
                for(i = 0; i < node1.children.size(); i++)
                {
                   
                    for(j = 0; j < node2.children.size(); j++)
                    {
                        double a = Math.pow(lambda, (double)node1.children.size())*Math.pow(lambda, (double)node2.children.size())*computeSimilarityDT(node1.children.get(i),node2.children.get(j));
                        
                        similarity_score += a;
                                
                    }
                }
                
                return similarity_score;
            }
            
        }
        
        
        private static int matching(TreeNode<ArrayList<String>> node1, TreeNode<ArrayList<String>> node2)
        {
            
            if(node1.data.get(3).equals(node2.data.get(3)) )//&& node1.data.get(7).equals(node2.data.get(7)))
                return 1;
            else
                return 0;
        }
        public static double similarity(TreeNode<ArrayList<String>> node1, TreeNode<ArrayList<String>> node2)
        {
                     
            PorterStemmer stemmer = new PorterStemmer();
            int sim = 0;
            
            
            for(int i = 0; i<node1.data.size()-1;i++)
            {
                
                if(i != 1 && i != 4)// && node1.data.get(7).equals(node2.data.get(7)))
                {
//                    if(!unimportantType(node1.data.get(7)) && !unimportantType(node2.data.get(7)))// != "prep" && node1.data.get(3) != "other"))
//                    {
                if(i == 5)
                {
                    //hypernym
                    List<String> node1_hypernyms = new ArrayList<String>(Arrays.asList(node1.data.get(5).split("#"))); 
                    List<String> node2_hypernyms = new ArrayList<String>(Arrays.asList(node2.data.get(5).split("#")));
                    if(node1_hypernyms.size() < node2_hypernyms.size())
                    {
                    node1_hypernyms.retainAll(node2_hypernyms);

                    if(node1_hypernyms.size() > 0)
                        sim = sim + 1;//2*node1_hypernyms.size();
                    }
                    else
                    {
                        node2_hypernyms.retainAll(node1_hypernyms);

                    if(node2_hypernyms.size() > 0)
                        sim = sim + 1;//2*node2_hypernyms.size();
                    }
                }
                else if(i == 0)
                {
                    if(stemmer.stem(node1.data.get(i)).toLowerCase().equals(stemmer.stem(node2.data.get(i)).toLowerCase()))
                    {
//                        if(node1.data.get(7).equals(node2.data.get(7)))
//                            sim++;
//                    if(node1.data.get(6).startsWith("PERSON") || node1.data.get(6).startsWith("LOCATION") || node1.data.get(6).startsWith("ORGANIZATION"))
//                           sim = sim + 1;
//                        else
                        sim += 1;
                    }
                }
//                else if(i == 6)
//                {
//                    if(node1.data.get(i).toString().equals(node2.data.get(i).toString()) && !node1.data.get(i).equals("O"))
//                        sim++;
//                }
                else if(node1.data.get(i).toString().equals(node2.data.get(i).toString()))// && !node1.data.get(i).equals("other"))
                {
                        sim = sim + 1;
                }
                
                }
                
           // }
            }

            
            //return (double) sim/node1.data.size();
            return sim;
        }
        //tfidf based
         public static double similarity2(TreeNode<ArrayList<String>> node1, TreeNode<ArrayList<String>> node2, List<String> first_sentence_terms, List first_sentence_tfidf, List<String> second_sentence_terms, List second_sentence_tfidf)
        {
        
                     
            PorterStemmer stemmer = new PorterStemmer();
            int sim = 0;
            
            
            for(int i = 0; i<node1.data.size()-1;i++)
            {
                
                if(i != 1 && i != 4)// && node1.data.get(7).equals(node2.data.get(7)))
                {
//                    if(!unimportantType(node1.data.get(7)) && !unimportantType(node2.data.get(7)))// != "prep" && node1.data.get(3) != "other"))
//                    {
                if(i == 5)
                {
                    //hypernym
                    List<String> node1_hypernyms = new ArrayList<String>(Arrays.asList(node1.data.get(5).split("#"))); 
                    List<String> node2_hypernyms = new ArrayList<String>(Arrays.asList(node2.data.get(5).split("#")));
                    if(node1_hypernyms.size() < node2_hypernyms.size())
                    {
                    node1_hypernyms.retainAll(node2_hypernyms);

                    if(node1_hypernyms.size() > 0)
                        sim = sim + 1;//2*node1_hypernyms.size();
                    }
                    else
                    {
                        node2_hypernyms.retainAll(node1_hypernyms);

                    if(node2_hypernyms.size() > 0)
                        sim = sim + 1;//2*node2_hypernyms.size();
                    }
                }
                else if(i == 0)
                {
                    if(stemmer.stem(node1.data.get(i)).toLowerCase().equals(stemmer.stem(node2.data.get(i)).toLowerCase()))
                    {
                        double dep1val = 0.05;
            if(first_sentence_terms.contains(node1.data.get(i).toLowerCase()))
                dep1val = (double) first_sentence_tfidf.get(first_sentence_terms.indexOf(node1.data.get(i).toLowerCase()));
       
//          
//                        if(node1.data.get(7).equals(node2.data.get(7)))
//                            sim++;
//                    if(node1.data.get(6).startsWith("PERSON") || node1.data.get(6).startsWith("LOCATION") || node1.data.get(6).startsWith("ORGANIZATION"))
//                           sim = sim + 1;
//                        else
                        sim += 2*dep1val;//*dep1val;
                    }
                }
//                else if(i == 6)
//                {
//                    if(node1.data.get(i).toString().equals(node2.data.get(i).toString()) && !node1.data.get(i).equals("O"))
//                        sim++;
//                }
                else if(node1.data.get(i).toString().equals(node2.data.get(i).toString()))// && !node1.data.get(i).equals("other"))
                {
                        sim = sim + 1;
                }
                
                }
                
            }
//            }

            
            //return (double) sim/node1.data.size();
            return sim;
        }
        public static TreeNode<ArrayList<String>> sentenceToDTtree(String sentence, LexicalizedParser lp, TokenizerFactory tf, PrintWriter dep_writer, String file_name, int sent_index, AbstractSequenceClassifier<CoreLabel> classifier, PrintWriter tag_writer, String tag_filename)
        {
            sentence = sentence.replace(".'''","");
            sentence = sentence.replace("''.","");
            sentence = sentence.replace("(", "");
            sentence = sentence.replace(")", "");
            sentence = sentence.replace("``", "");
            sentence = sentence.replace("''", "");
            sentence = sentence.replace(".", "");
            sentence = sentence.replace("\"","");
            sentence = sentence.replace("_"," ");
            sentence = sentence.replace("'''","");
            
            
            //PORBLEMATIC!!!!!!!!
           if(sentence.length() < 10) //tek kelimeyse demeye çalışıyorum
            {
                TreeNode<ArrayList<String>> tree = new TreeNode<ArrayList<String>>(new ArrayList());
                tree.data.add(sentence);
                tree.data.add("1");
                tree.data.add("NoTag");
                tree.data.add("");
                tree.data.add("");
                tree.data.add("");
                tree.data.add("");
                tree.data.add("");
                dep_writer.print("root(ROOT-0, "+sentence+"-1)#\n");
                tag_writer.print("NoTag/NoTag#\n");
                return tree;
            }
            if(sentence.equals("..."))
            {
                TreeNode<ArrayList<String>> tree = new TreeNode<ArrayList<String>>(new ArrayList());
                tree.data.add("...");
                tree.data.add("1");
                tree.data.add("...");
                tree.data.add("NoTag");
                tree.data.add("");
                tree.data.add("");
                tree.data.add("");
                tree.data.add("");
                dep_writer.print("root(ROOT-0, ...-1)#\n");
                tag_writer.print("NoTag/NoTag#\n");
                return tree;
            }
            List <Collection<TypedDependency>> dependency_list = new ArrayList <>();
            ArrayList<List> taggedWordsList = new ArrayList<>();
 
 
            // put tokens in a list:
//            List tokens = tf.getTokenizer(new StringReader(sentence)).tokenize(); 
//            lp.parse(tokens); // parse the tokens
//            Tree t = lp.parse(sentence); // get the best parse tree
////       
////                                
//            TreebankLanguagePack tlp = lp.getOp().tlpParams.treebankLanguagePack();
//            GrammaticalStructureFactory factory = tlp.grammaticalStructureFactory();
//            GrammaticalStructure depTree = factory.newGrammaticalStructure(t);
//
//            dependency_list.add(depTree.typedDependencies());
////           
////            
//            //deneme
//             
//             
//            
//                 
//                 Object[] list = depTree.typedDependencies().toArray();
//                 for(int i = 0; i < list.length; i++)
//                 {
//                     dep_writer.print(list[i].toString());
//                     dep_writer.print("#");
//                 }
//                 dep_writer.println();
//                 dep_writer.flush();
//          
//            //tag yazma
//            Object[] taglist = t.taggedYield().toArray();
//                 for(int i = 0; i < taglist.length; i++)
//                 {
//                     tag_writer.print(taglist[i].toString());
//                     tag_writer.print("#");
//                 }
//                 tag_writer.println();
//                 tag_writer.flush();
//            //tag yazma end
//           taggedWordsList.add(t.taggedYield());
           String[] tag_list = null;
          
           try {
        Scanner openSave = new Scanner(new FileReader("tags output/"+tag_filename));
        openSave.useDelimiter("\n");
        //System.out.println(sent_index);
        for(int i = 0; i < sent_index; i++)
        {
            String tags = openSave.next();
            //System.out.println(dependencies);
           // System.out.print(i);
        }
        //System.out.println();
        String tags = openSave.next();
       // System.out.println(sentence);
        //System.out.println(dependencies);
        tag_list = tags.split("#");
        taggedWordsList.add(Arrays.asList(tag_list));
        openSave.close();
    }
    catch (FileNotFoundException e) {
        
    }
            String[] list= null;
            try {
        Scanner openSave = new Scanner(new FileReader("dependency parser output/"+file_name));
        openSave.useDelimiter("\n");
        //System.out.println(sent_index);
        for(int i = 0; i < sent_index; i++)
        {
            String dependencies = openSave.next();
            //System.out.println(dependencies);
           // System.out.print(i);
        }
        //System.out.println();
        String dependencies = openSave.next();
       // System.out.println(sentence);
        //System.out.println(dependencies);
        list = dependencies.split("#");
        
        openSave.close();
    }
    catch (FileNotFoundException e) {
        
    }
         
    String[] string_list = new String[list.length-1];
    
    for(int i = 0; i< list.length-1;i++)
        string_list[i] = list[i];
            
            
           List<String> nerList = new ArrayList<>();
            nerList.add(classifier.classifyToString(sentence));
            
            //chunk tag
            List<Tree> sub_trees = new ArrayList<>();
            List<String> np_list = new ArrayList<>();
            List<String> vp_list = new ArrayList<>();
            List<String> adjp_list = new ArrayList<>();
            List<String> advp_list = new ArrayList<>();
            List<String> pp_list = new ArrayList<>();
            List<String> others_list = new ArrayList<>();
//            sub_trees = t.subTreeList();
////                                
//                                   
//           for (int i = sub_trees.size()-1;i>=0;i--)
//            {
//                List<Tree> leaves = sub_trees.get(i).getLeaves(); //leaves correspond to the tokens
//
//                for (Tree leaf : leaves){ 
//
//
//                    List<Word> words = leaf.yieldWords();
//                    for (Word word: words)
//                    {
//                        if(!contains_all(np_list, vp_list, adjp_list, advp_list, pp_list, word.word().toString()))
//                        {
//                        if(sub_trees.get(i).label().value().equals("NP") )
//                            np_list.add(word.word().toString());
//                        else if(sub_trees.get(i).label().value().equals("VP"))
//                            vp_list.add(word.word().toString());
//                        else if(sub_trees.get(i).label().value().equals("ADJP"))
//                            adjp_list.add(word.word().toString());
//                        else if(sub_trees.get(i).label().value().equals("ADVP"))
//                            advp_list.add(word.word().toString());
//                        else if(sub_trees.get(i).label().value().equals("PP"))
//                            pp_list.add(word.word().toString());
//                        else
//                            others_list.add(word.word().toString());
//                        }
//               
//                    }
//
//                }               
//
//              
//            }
            List<List<String>> chunk_tag_lists = new ArrayList<>();
            chunk_tag_lists.add(np_list);
            chunk_tag_lists.add(vp_list);
            chunk_tag_lists.add(adjp_list);
            chunk_tag_lists.add(advp_list);
            chunk_tag_lists.add(pp_list);
            chunk_tag_lists.add(others_list);
            //chunk tag end
            
            ArrayList<TreeNode<ArrayList<String>>> trees = createTree.createTrees(dependency_list, string_list, taggedWordsList, chunk_tag_lists, nerList);
           // ArrayList<TreeNode<ArrayList<String>>> trees = createTree.createTrees(dependency_list, taggedWordsList, chunk_tag_lists, nerList);
//            
            return trees.get(0);
        }
        
        public static ArrayList<String> sentenceToBigram(String sentence, LexicalizedParser lp, TokenizerFactory tf)
        {
            sentence = sentence.replace("(", "");
            sentence = sentence.replace(")", "");
            sentence = sentence.replace("``", "");
            sentence = sentence.replace("''", "");
           
           
           
            List <Collection<TypedDependency>> dependency_list = new ArrayList <>();
            ArrayList<List> taggedWordsList = new ArrayList<>();
 
            // put tokens in a list:
            List tokens = tf.getTokenizer(new StringReader(sentence)).tokenize(); 
            lp.parse(tokens); // parse the tokens
            Tree t = lp.parse(sentence); // get the best parse tree
       
                                
            TreebankLanguagePack tlp = lp.getOp().tlpParams.treebankLanguagePack();
            GrammaticalStructureFactory factory = tlp.grammaticalStructureFactory();
            GrammaticalStructure depTree = factory.newGrammaticalStructure(t);

            dependency_list.add(depTree.typedDependencies());
            

            taggedWordsList.add(t.taggedYield());
            
            ArrayList<String> bigramList = new ArrayList<>();
            PorterStemmer stemmer = new PorterStemmer();
           
            Object[] list = depTree.typedDependencies().toArray();
            for(int j = 0;j<list.length;j++)
            {
                 int par_index = list[j].toString().indexOf("(");
                 String dep_type = list[j].toString().substring(0, par_index);
                 int comma_index = list[j].toString().indexOf(',');
                 int dash_index = list[j].toString().indexOf('-');
                 int last_dash_index = list[j].toString().lastIndexOf('-');

                 String head = stemmer.stem(list[j].toString().substring(par_index+1, dash_index));
                 String dependent = stemmer.stem(list[j].toString().substring(comma_index+2, last_dash_index));

                // System.out.println(dependent+"_"+dep_type+"_"+head);
                 
                 if(!unimportantType(dep_type))
                 {
                    // System.out.println(dep_type);
                 bigramList.add(dependent+"_"+dep_type+"_"+head);
                 bigramList.add(dependent);
                 }
            }

           
            
            
            return bigramList;
        }
         public static Boolean contains_all(List<String> np, List<String> vp, List<String> adjp, List<String> advp, List<String> pp,  String word)
        {
           if(!np.contains(word) && !vp.contains(word)&& !adjp.contains(word) && !advp.contains(word)&& !pp.contains(word) )
               return false;
           else
               return true;
        }
}
