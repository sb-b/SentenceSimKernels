package tree;


import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tree.TreeNode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author betul
 */

public class createTree {
    
       public static ArrayList<TreeNode<ArrayList<String>>> createTrees(List<Collection<TypedDependency>> dependency_list, String[] string_list, ArrayList<List> taggedWordsList, List<List<String>> chunktags, List<String> nerList)
        {
            //array of trees to hold sentence  trees to be created
            ArrayList<TreeNode<ArrayList<String>>> trees = new ArrayList<TreeNode<ArrayList<String>>>();

            //for each sentence,
           // for(int k = 0; k<dependency_list.size();k++)
            //{    
                List taglist = taggedWordsList.get(0);
                //root node
                TreeNode<ArrayList<String>> root = new TreeNode<ArrayList<String>>(new ArrayList());
                //nodes of the root
                ArrayList<TreeNode<ArrayList<String>>> nodes = new ArrayList<TreeNode<ArrayList<String>>>(); 
               
                 for(int i = 0;i<string_list.length-1;i++) 
                        nodes.add(new TreeNode<ArrayList<String>>(new ArrayList()));
                 
                //Object[] list = dependency_list.get(k).toArray();
                Object[] list = string_list;
                //initializations
                int comma_index = 0, dash_index = 0, slash_index=0;//needed when parsing xyz(abc-1, dfg-5) etc.
                String search_word = "(ROOT"; //to find the root node
                TreeNode<ArrayList<String>> previous_node = root;//parent node, initialized as root
                boolean stop = false;//when to stop the main loop
                int j = 0;//node counter

                //while all list elements are not processed,
                while(!stop)
                {
                    for(int i = 0;i<list.length;i++)
                    {                              
                        if(list[i].toString().contains(search_word))
                        {
                            //this part is used only for the root node
                            if(search_word == "(ROOT")
                            {
                                //parse xyz(abc-1, dfg-2) etc. e.g, root(ROOT-0, moved-2)
                                //typed için
                                int par_index = list[i].toString().indexOf("(");
                                String dep_type = list[i].toString().substring(0, par_index);
                                
                                
                                comma_index = list[i].toString().indexOf(',');
                                dash_index = list[i].toString().lastIndexOf('-');
                                root.data.add(list[i].toString().substring(comma_index+2, dash_index));//moved
                                Pattern p = Pattern.compile("\\d+");
                                Matcher m = p.matcher(list[i].toString().substring(dash_index+1,list[i].toString().length()-1));
                                String num = "0";
                               // System.out.println(root.data.get(0));
                                while (m.find( ))
                               {
                                num = m.group();     
                                //System.out.println(num);                 
                                }
                                
                                root.data.add(num);//2
//                                //for my kernel
//                                root.data.add("");
//                                root.data.add("");
//                                root.data.add("");
//                                root.data.add("");
//                                root.data.add("");
                                
                               // root.data.add(dep_type); 
                                
                                int tag_ind = -1;
                                for(int ind=0;ind<taglist.size();++ind){
                                    slash_index = taglist.get(ind).toString().indexOf("/");
                                   
                                    if (slash_index!=-1 && taglist.get(ind).toString().substring(0,slash_index).equals(root.data.get(0))) {
                                       tag_ind = ind;
                                       break;
                                    }
                                }
                                
                                if(tag_ind != -1){
                                    slash_index = taglist.get(tag_ind).toString().indexOf(root.data.get(0)+"/");                               
                                    root.data.add(taglist.get(tag_ind).toString().substring(slash_index+root.data.get(0).length()+1));
                                    taglist.set(tag_ind, "");
                                }
                                else
                                    root.data.add("NoTag");
                                
                                root.data.add(getGeneralPOStag(root.data.get(2)));
                                root.data.add(getChunkTag(root.data.get(0),chunktags));
                                root.data.add(getHypernyms(root.data.get(0), root.data.get(3)));
                                  //root.data.add(getSynonyms(root.data.get(0)));
                                slash_index = nerList.get(0).indexOf(root.data.get(0)+"/");
                                String str =  nerList.get(0).substring(slash_index+root.data.get(0).length()+1);
                                String[] NER = str.split("(/| )", 2);
                                root.data.add(NER[0]);
                                root.data.add(dep_type); 
                                search_word = "("+root.data.get(0)+"-"+root.data.get(1)+",";//(moved
                                previous_node = root;//moved root

                                list[i]="";//clear that element
                                
                                //test if all elements of list are processed
                                    boolean end_flag = true;
                                    for(Object n: list)
                                    {
                                        if(n.toString() != "")
                                        {
                                           end_flag = false;
                                           break;
                                        }
                                    }
                                    //if yes, break the loop
                                    if(end_flag == true)
                                    {
                                        stop = true;
                                        break;
                                    }
                            }
                            else//this part is used for all nodes but root node
                            {   //e.g., subj(moved-2, forces-1)
                                //typed için
                                int par_index = list[i].toString().indexOf("(");
                                String dep_type = list[i].toString().substring(0, par_index);
                                
                                comma_index = list[i].toString().indexOf(',');
                                dash_index = list[i].toString().lastIndexOf('-');
                                nodes.get(j).data.add(list[i].toString().substring(comma_index+2, dash_index));//forces
                                Pattern p = Pattern.compile("\\d+");
                                Matcher m = p.matcher(list[i].toString().substring(dash_index+1,list[i].toString().length()-1));
                                String num = "0";
                                while (m.find( ))
                               {
                                num = m.group();     
                                //System.out.println(num);                 
                                }
                                
                                nodes.get(j).data.add(num);//2
                                //for my kernel
//                                nodes.get(j).data.add("");
//                                nodes.get(j).data.add("");
//                                nodes.get(j).data.add("");
//                                nodes.get(j).data.add("");
//                                nodes.get(j).data.add("");
                                //nodes.get(j).data.add(dep_type);
                                
                                int tag_ind = -1;
                                for(int ind=0;ind<taglist.size();++ind){
                                    
                                    slash_index = taglist.get(ind).toString().indexOf("/");
       
                                    if (slash_index!=-1 && taglist.get(ind).toString().substring(0,slash_index).equals(nodes.get(j).data.get(0))) {
                                        tag_ind = ind;
                                       break;
                                    }
                                }
                               
                                if(tag_ind!=-1){
                                    slash_index = taglist.get(tag_ind).toString().indexOf(nodes.get(j).data.get(0)+"/");
                                    nodes.get(j).data.add(taglist.get(tag_ind).toString().substring(slash_index+nodes.get(j).data.get(0).length()+1));//pos
                                    taglist.set(tag_ind, "");
                                }
                                else
                                    nodes.get(j).data.add("NoTag");
                                
                                nodes.get(j).data.add(getGeneralPOStag(nodes.get(j).data.get(2))); //general pos
                                nodes.get(j).data.add(getChunkTag(nodes.get(j).data.get(0),chunktags));
                                nodes.get(j).data.add(getHypernyms(nodes.get(j).data.get(0), nodes.get(j).data.get(3)));
                                 // nodes.get(j).data.add(getSynonyms(nodes.get(j).data.get(0)));
                                slash_index = nerList.get(0).indexOf(nodes.get(j).data.get(0)+"/");
                                String str =  nerList.get(0).substring(slash_index+nodes.get(j).data.get(0).length()+1);
                                String[] NER = str.split("(/| )", 2);
                                nodes.get(j).data.add(NER[0]);
                                nodes.get(j).data.add(dep_type);
                                previous_node.addChild(nodes.get(j));//parent : moved root
                                list[i]="";                                

                                search_word = "("+nodes.get(j).data.get(0)+"-"+nodes.get(j).data.get(1)+",";//(forces
                                j++;
                                Boolean include = false;
                                TreeNode <ArrayList<String>> current_node = nodes.get(j-1);//forces node
                                int flag = 0;
                                //check if there is an element that contains (forces, i.e., forces node has a child or not 
                                for(Object n : list)
                                {
                                    if(n.toString().contains(search_word))
                                    {
                                        include = true;
                                        break;
                                    }
                                }
                                //in a loop
                                do
                                { 
                                    current_node = current_node.parent;//moved node
                                    if(current_node == null)
                                    {
                                        stop = true;
                                        //end_flag = true;
                                        break;
                                    }
                                    //if no element that contains e.g. (forces. i.e., forces node has no child
                                    if(!include)
                                    {
                                        flag = 1;
                                        search_word = "("+current_node.data.get(0)+"-"+current_node.data.get(1)+",";//search word: (moved. go to other children of parent node
                                        include = false;
                                        //check if parent node has another child
                                        for(Object n : list)
                                        {
                                             if(n.toString().contains(search_word))
                                             {
                                              include = true;
                                              break;
                                             }
                                        }
                                        previous_node = current_node;//moved node
                                    }
                                    //if there is an element that contains e.g., (forces
                                    else
                                    {   //if we first go to lower levels and then go up again
                                        if(flag == 1)
                                           previous_node = current_node;//current node e.g. moved
                                        else
                                           previous_node = nodes.get(j-1);//forces node since it has its own children
                                        break;
                                    }
                                    //test if all elements of list are processed
                                    boolean end_flag = true;
                                    for(Object n: list)
                                    {
                                        if(n.toString() != "")
                                        {
                                           end_flag = false;
                                           break;
                                        }
                                    }
                                    //if yes, break the loop
                                    if(end_flag == true)
                                    {
                                        stop = true;
                                        break;
                                    }
                                }while(!include);//continue until we find a node with children
                            }//end if-else
                            i = -1;//start the loop of list from scratch
                        }//end if
                    }//end for
                }//end while        
                trees.add(root);//add current tree to trees list
           // }//end for each sentence loop
            
            return trees;
        }//end createTrees function

        //createINdent subfunction which is used in printing trees
        public static String createIndent(int depth) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < depth; i++) {
                        sb.append(' ');
                }
                return sb.toString();
        }
        
        public static String getGeneralPOStag(String tag)
        {
            if(tag.startsWith("NN") || tag.equals("PRP") || tag.startsWith("WP"))
                return "noun";
            else if(tag.startsWith("VB"))
                return "verb";
            else if(tag.startsWith("JJ") || tag.equals("PRP$") || tag.equals("WP$"))
                return "adj";
            else if(tag.startsWith("RB") || tag.startsWith("WRB"))
                return "adv";
            else if(tag.equals("IN"))
                return "prep";
            else
                return "other";
        }

         public static String getChunkTag(String word, List<List<String>> chunktags)
        {
            if(chunktags.get(0).contains(word))
                return "NP";
            else if(chunktags.get(1).contains(word))
                return "VP";
            else if(chunktags.get(2).contains(word))
                return "ADJP";
            else if(chunktags.get(3).contains(word))
                return "ADVP";
            else if(chunktags.get(4).contains(word))
                return "PP";
            else
                return "other";
        }
         
 public static String getHypernyms(String word, String POS)
     {
         System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");
                        
        NounSynset nounSynset;
        VerbSynset verbSynset;

        NounSynset[] noun_hypernyms;
        VerbSynset[] verb_hypernyms;

        WordNetDatabase database = WordNetDatabase.getFileInstance();
        
        Synset[] synsets;
        if(POS == "noun")
           synsets = database.getSynsets(word, SynsetType.NOUN);
        else if(POS.equals("verb"))
           synsets = database.getSynsets(word, SynsetType.VERB);
        else
            return "";
        String hyp_list = "";
        for(Synset synset : synsets)
        {
            SynsetType type = synsets[0].getType();//.getType();
            if(POS.equals("noun") && type.equals(SynsetType.NOUN))
            {
                nounSynset = (NounSynset) (synset);
               
                noun_hypernyms = nounSynset.getHypernyms();
                if(noun_hypernyms.length>0)
                {
                String[] hyp = noun_hypernyms[0].getWordForms();
                
                for(int i = 0; i< hyp.length; i++)
                {
                    hyp_list += hyp[i] + "#";
                }
               // return hyp_list;
                }
            }
            else if(POS.equals("verb") && type.equals(SynsetType.VERB))
            {
                verbSynset = (VerbSynset) (synset);
                verb_hypernyms = verbSynset.getHypernyms();
                if(verb_hypernyms.length>0)
                {
                String[] hyp = verb_hypernyms[0].getWordForms();
                //String hyp_list = "";
                for(int i = 0; i< hyp.length; i++)
                {
                    hyp_list += hyp[i] + "#";
                }
                //return hyp_list;
                }
            }
            break;
        }
        //System.out.println(hyp_list);
        return hyp_list;
                        
     }
 
 public static String getSynonyms(String word)
     {
         System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");
       

        WordNetDatabase database = WordNetDatabase.getFileInstance();
        
        Synset[] synsets = database.getSynsets(word);
        
        String synonyms = "";
        List<String> syn_list = new ArrayList<>();
        
        if (synsets.length > 0)
        {
                for (int i = 0; i < synsets.length; i++)
                {
                        //System.out.println("");
                        String[] wordForms = synsets[i].getWordForms();
                        for (int j = 0; j < 1;j++)//wordForms.length; j++)
                        {
                                
                                if(!syn_list.contains(wordForms[j]))
                                    syn_list.add(wordForms[j]);
                               
                        }
                        //System.out.println(": " + synsets[i].getDefinition());
                }
        }
        //System.out.println(syn_list);
        synonyms = StringUtils.join(syn_list, "#");
        //System.out.println(synonyms);
        return synonyms;
                        
     }
}
