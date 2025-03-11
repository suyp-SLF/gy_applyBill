package kd.cus.wb.report;

import java.util.List;

public class TreeNode {
	Object id;
	String num;
	List<TreeNode> child;
	TreeNode parent;
	
	int deep;

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public List<TreeNode> getChild() {
		return child;
	}

	public void setChild(List<TreeNode> child) {
		this.child = child;
	}

	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public int getDeep() {
		return deep;
	}

	public void setDeep(int deep) {
		this.deep = deep;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}
}
