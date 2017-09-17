/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trigram;
import java.util.ArrayList;
import tree.TreeNode;

/**
 *
 * @author hakime_asus
 */
public class TrigramUnit {
    
     TreeNode<ArrayList<String>> center;
     TreeNode<ArrayList<String>> left;
     TreeNode<ArrayList<String>> right;
    
    public TrigramUnit( TreeNode<ArrayList<String>> centerNode,  TreeNode<ArrayList<String>> leftNode,  TreeNode<ArrayList<String>> rightNode)
    {
        this.center = centerNode;
        this.left = leftNode;
        this.right = rightNode;
    }
    
 
      public TreeNode returnCenter()
      {
          return this.center;
      }
      
      public TreeNode returnLeft()
      {
          return this.left;
      }
      public TreeNode returnRight()
      {
          return this.right;
      }
}
