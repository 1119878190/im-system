package com.study.im.common.route.algorithm.consistenthash;

import com.study.im.common.enums.UserErrorCode;
import com.study.im.common.exception.ApplicationException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * TreeMap 一致哈希
 *
 * @author lx
 * @date 2023/05/08
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private TreeMap<Long, String> treeMap = new TreeMap<>();


    private static final int NODE_SIZE = 2;

    @Override
    protected void add(long key, String value) {

        // 创建两个虚拟节点  为了防止数据倾斜
        for (int i = 0; i < NODE_SIZE; i++) {
            treeMap.put(super.hash("node" + key + i), value);
        }
        // 一个真实节点
        treeMap.put(key, value);


    }

    @Override
    protected String getFirstNodeValue(String key) {

        Long hash = super.hash(key);
        // 获取一个子集。其所有对象的 key 的值大于等于 hash
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if (!last.isEmpty()) {
            return last.get(last.firstKey());
        }

        if (treeMap.size() == 0) {
            throw new ApplicationException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }


        return treeMap.firstEntry().getValue();
    }

    @Override
    protected void processBefore() {
        treeMap.clear();
    }
}
