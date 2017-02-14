package com.javachina.service;

import com.blade.jdbc.core.Take;
import com.blade.jdbc.model.Paginator;
import com.javachina.model.Node;
import com.javachina.model.NodeTree;

import java.util.List;
import java.util.Map;

public interface NodeService {
	
	Node getNode(Integer nid);
	
	Node getNode(Take take);
	
	Map<String, Object> getNodeDetail(Node node, Integer nid);
	
	List<Node> getNodeList(Take take);

	List<NodeTree> getTree();

	Paginator<Map<String, Object>> getPageList(Take take);

	void save(Node node) throws Exception;

	void delete(Integer nid) throws Exception;

	void updateCount(Integer nid, String type, int count) throws Exception;

	void update(Node node) throws Exception;

    List<Node> getHotNodes(int page, int limit);
}