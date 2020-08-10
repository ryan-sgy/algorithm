import java.util.*;

public class main {
    public static void main(String[] args) {
        System.out.println("Enter:");
        Scanner sc = new Scanner(System.in);
        String s = sc.next();

        String[] arrS = s.split(",");
        int[] arr = new int[arrS.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Integer.parseInt(arrS[i]);
        }
        quicksort(arr,0,arr.length-1);
        System.out.println("The ascending order:");
        for(int i:arr){
            System.out.print(i+",");
        }

    }

    public static int getIndex(int[] arr, int low, int high) {
        int temp = arr[low];
        while(low<high){
            while(low<high && arr[high] >= temp){
                high--;
            }
            arr[low] = arr[high];
            while(low<high && arr[low] <= temp){
                low++;
            }
            arr[high] = arr[low];
        }
        arr[low] = temp;
        return low;
    }

    public static int[] quicksort(int[] arr,int low, int high){
        if(low<high){
            int index = getIndex(arr,low,high);
            quicksort(arr,low,index-1);
            quicksort(arr,index+1,high);
        }
        return arr;
    }

    // //descending order
    // public static int getIndex(int[]arr, int low, int high){
    //     int temp = arr[low];
    //     while(low<high){
    //         while(low<high && arr[high] <= temp){
    //             high--;
    //         }
    //         arr[low] = arr[high];
    //         while(low<high && arr[low] >= temp){
    //             low++;
    //         }
    //         arr[high] = arr[low];
    //     }
    //     arr[low] = temp;
    //     return low;
    // }
}
