package com.wm.Sort;

/**
 * Created by wangmiao on 2018/3/10.
 */
public class KuaiPai {
    public static void main(String[] args) {
        int arr[] = {23, 12, 46, 24, 87, 65, 18, 14, 43, 434, 65, 76};
        sortCore(arr,0,arr.length-1);
        for (int d = 0; d < arr.length; d++) {
            System.out.print(arr[d] + "\t");
        }
    }


    private static void sortCore(int[] array, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return;
        }
        int boundary = boundary(array, startIndex, endIndex);

        sortCore(array, startIndex, boundary - 1);
        sortCore(array, boundary + 1, endIndex);
    }

    /*
     * 交换并返回分界点
     *
     * @param array
     *      待排序数组
     * @param startIndex
     *      开始位置
     * @param endIndex
     *      结束位置
     * @return
     *      分界点
     */
    private static int boundary(int[] array, int startIndex, int endIndex) {
        int standard = array[startIndex]; // 定义标准
        int leftIndex = startIndex; // 左指针
        int rightIndex = endIndex; // 右指针

        while(leftIndex < rightIndex) {
            while(leftIndex < rightIndex && array[rightIndex] >= standard) {
                rightIndex--;
            }
            array[leftIndex] = array[rightIndex];

            while(leftIndex < rightIndex && array[leftIndex] <= standard) {
                leftIndex++;
            }
            array[rightIndex] = array[leftIndex];
        }

        array[leftIndex] = standard;
        return leftIndex;
    }
}
