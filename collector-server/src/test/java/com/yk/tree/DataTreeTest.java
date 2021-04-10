package com.yk.tree;

import com.yk.bitcoin.manager.Address;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DataTreeTest {
    public static void main(String[] args) {

        List<DataTreeTest.TreeNode> list = new ArrayList<>();
        DataTreeTest.TreeNode node1 = new DataTreeTest.TreeNode(1, 0, "a1");
        DataTreeTest.TreeNode node2 = new DataTreeTest.TreeNode(2, 0, "a2");
        DataTreeTest.TreeNode node3 = new DataTreeTest.TreeNode(3, 1, "b3");
        DataTreeTest.TreeNode node4 = new DataTreeTest.TreeNode(4, 1, "b4");
        DataTreeTest.TreeNode node5 = new DataTreeTest.TreeNode(5, 2, "c6");
        DataTreeTest.TreeNode node6 = new DataTreeTest.TreeNode(6, 3, "d7");
        DataTreeTest.TreeNode node7 = new DataTreeTest.TreeNode(7, 3, "d8");
        list.add(node1);
        list.add(node2);
        list.add(node3);
        list.add(node4);
        list.add(node5);
        list.add(node6);
        list.add(node7);

        list.add(node7);
        list.add(node3);
        list.add(node4);

        list.add(node1);
        list.add(node2);
        list.add(node3);
        list.add(node4);
        list.add(node5);

        Set<DataTreeTest.TreeNode> rest = list.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getId()))), HashSet::new));

        for (DataTreeTest.TreeNode node : list) {

        }

        /**
         * 1 0 "a1"
         *
         * 2 0 "a2"
         *
         * 3 1    "b3"
         *   6 3    "d7"
         *
         *   7 3    "d8"
         *          *
         * 4 1    "b4"
         *
         * 5 2    "c6"
         */

        Map<Long, List<DataTreeTest.TreeNode>> map = rest.stream().collect(Collectors.groupingBy(t -> t.getPid()));

        List<DataTreeTest.TreeNode> tops = map.get(0L);


        sort(map, tops);


        System.out.println(tops);

        List<Long> res = new ArrayList<>();
        getDeeps(tops, res);
        System.out.println(res);
    }

    public static List<Long> getDeeps(List<DataTreeTest.TreeNode> tops, List<Long> res) {
        for (DataTreeTest.TreeNode node : tops) {
            if (node.getChildren() == null) {
                res.add(node.getId());
            } else {
                getDeeps(node.getChildren(), res);
            }
        }
        return res;
    }

    public static void sort(Map<Long, List<DataTreeTest.TreeNode>> map, List<DataTreeTest.TreeNode> currents) {

        for (DataTreeTest.TreeNode node : currents) {
            DataTreeTest.TreeNode top = node;
            List<DataTreeTest.TreeNode> temp = map.get(node.getId());
            if (null != temp && temp.size() != 0) {
                node.setChildren(temp);
                sort(map, temp);
            }
        }
    }

    private static class Option {
        private long id;

        private String name;

        private double score;
    }

    private static class TreeNode {
        private long id;

        private long pid;

        private String name;

        public TreeNode(long id, long pid, String name) {
            this.id = id;
            this.pid = pid;
            this.name = name;
        }

        private List<DataTreeTest.Option> options;

        private List<DataTreeTest.TreeNode> children;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getPid() {
            return pid;
        }

        public void setPid(long pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<DataTreeTest.TreeNode> getChildren() {
            return children;
        }

        public void setChildren(List<DataTreeTest.TreeNode> children) {
            this.children = children;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataTreeTest.TreeNode node = (DataTreeTest.TreeNode) o;
            return id == node.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
