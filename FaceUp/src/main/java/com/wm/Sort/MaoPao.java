package com.wm.Sort;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class MaoPao {
    public static void main(String[] args) {
        int arr[] = {23, 12, 46, 24, 87, 65, 18, 14, 43, 434, 65, 76};
        //int arr[]={23,12,46,24,76};
        int k = 0;
        int length = arr.length-1;
        //冒泡排序
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (arr[j] < arr[j + 1]) {
                    int t = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = t;
                    k++;
                }
                System.out.print("i=" + i + "的第j=" + j + "次交换\t");
                for (int d = 0; d < arr.length; d++) {
                    System.out.print(arr[d] + "\t");
                }
                System.out.println();
            }
        }
        System.out.println("交换的次数为"+k);
    }
}
