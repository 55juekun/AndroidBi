package com.aliyun.vodplayerview.Util;


import java.util.ArrayList;
import java.util.Iterator;

public class GetTree {
    public static ArrayList<MarkInfo> markInfos=new ArrayList<>();
    private static ArrayList<TreeElement> root=new ArrayList<>();

    public MarkInfo[] getMarkInfos() {
        MarkInfo[]markInfos1=new MarkInfo[markInfos.size()];
        return markInfos.toArray(markInfos1);
    }

    public ArrayList<TreeElement> getRoot(MarkInfo markInfo) {
        if (root == null || root.size() == 0) {
            root = new ArrayList<>();
            TreeElement line = new TreeElement(markInfo.getLine());
            root.add(line);
            TreeElement group = new TreeElement(markInfo.getGroup());
            line.addChild(group);
            TreeElement point = new TreeElement(markInfo.getPoint());
            group.addChild(point);
            TreeElement tree = new TreeElement(markInfo.getCameraId() , markInfo.getUseId() + "");
            point.addChild(tree);
            return root;
        } else {
            Iterator<TreeElement> lineIterator = root.iterator();
            while (lineIterator.hasNext()) {//线级
                TreeElement line = lineIterator.next();
                if (line.getOutlineTitle().equals(markInfo.getLine())) {
                    Iterator<TreeElement> groupIterator = line.getChildList().iterator();
                    while (groupIterator.hasNext()) {//组级
                        TreeElement group = groupIterator.next();
                        if (group.getOutlineTitle().equals((markInfo.getGroup()))) {
                            Iterator<TreeElement> pointIterator = group.getChildList().iterator();
                            while (pointIterator.hasNext()) {//点级
                                TreeElement point = pointIterator.next();
                                if (point.getOutlineTitle().equals(markInfo.getPoint())) {
                                    TreeElement tree = new TreeElement(markInfo.getCameraId() , markInfo.getUseId() + "");
                                    point.addChild(tree);
                                    return root;
                                }
                            }
                            TreeElement point = new TreeElement(markInfo.getPoint());
                            group.addChild(point);
                            TreeElement tree = new TreeElement(markInfo.getCameraId() , markInfo.getUseId() + "");
                            point.addChild(tree);
                            return root;
                        }
                    }
                    TreeElement group = new TreeElement(markInfo.getGroup());
                    line.addChild(group);
                    TreeElement point = new TreeElement(markInfo.getPoint());
                    group.addChild(point);
                    TreeElement tree = new TreeElement(markInfo.getCameraId() , markInfo.getUseId() + "");
                    point.addChild(tree);
                    return root;
                }
            }
            return addRoot(markInfo);
        }
    }

    public ArrayList<TreeElement> addRoot(MarkInfo markInfo){
        if (root==null||root.size()==0){
            return getRoot(markInfo);
        }else {
            TreeElement line=new TreeElement(markInfo.getLine());
            root.add(line);
            TreeElement group=new TreeElement(markInfo.getGroup());
            line.addChild(group);
            TreeElement point=new TreeElement(markInfo.getPoint());
            group.addChild(point);
            TreeElement tree=new TreeElement(markInfo.getCameraId(),markInfo.getUseId()+"");
            point.addChild(tree);
        }
        return root;
    }

    public ArrayList<TreeElement> getExample() {
        if (root!=null){
            root.clear();
        }
//        markInfos.clear();
        Iterator<MarkInfo> iterator=markInfos.iterator();
        while (iterator.hasNext()){
            getRoot(iterator.next());
        }
        return root;
    }
}
