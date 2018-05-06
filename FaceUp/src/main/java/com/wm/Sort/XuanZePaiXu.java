package com.wm.Sort;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class XuanZePaiXu {
    public static void main(String[] args) {

        int []arr = new int[]{23, 12, 46, 24, 87, 65, 18, 14, 43, 434, 65, 76};
        //选择排序
        int l=0;
        for(int i=0;i<arr.length-1;i++){
            for(int j=i+1;j<arr.length-1;j++){
                if(arr[i]<arr[j]){
                    int t=arr[i];
                    arr[i]=arr[j];
                    arr[j]=t;
                    l++;
                }
                System.out.print("i="+i+"的第j="+j+"次交换\t");
                for(int d=0;d<arr.length;d++){
                    System.out.print(arr[d]+"\t");
                }
                System.out.println();
            }
        }
        for(int i=0;i<arr.length;i++){
            System.out.print(arr[i]+"\t");
        }
        System.out.println("交换的次数为"+l);
    }
}
