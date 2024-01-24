import java.io.*;

import java.util.List;
//Exact Algorithm for GISP
public class GISP_final {
    static int v_num;//Number of vertices
    static int edgeNum;//number of edges
    static double edgWeight;//Lagrangian relaxation upper-bounding
    static int[][] edge;//adjacency matrix
    static int[] weight;//vertex weight
    static int[] currentV;//current solution
    //static int[] addList;
    static int[][] searchList;//Search List
    static int[] indexList;//Vertex positions after sorting
    //static int addLen;
    //static int size = 0;
    static int size_best = 0;
    static int bestProfit;//global best value
    static int profit;//current value
    static int count = 0;
    static double starting_time, finishing_time;
    static List<String> fileList;
    static String FileName;
    static int time;
    static List<Result> resultList;

    private static int[] quickSort(int[] keys) {
        int[] indices = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            indices[i] = i;
        }
        quickSort(keys, 0, keys.length-1, indices);
        return indices;
    }

    private static void quickSort(int[] keys, int begin, int end, int[] indices) {
        if (begin >= 0 && begin < keys.length && end >= 0 && end < keys.length && begin < end) {
            int i = begin, j = end;
            int vot = keys[i];
            int temp = indices[i];
            while (i != j) {
                while(i < j && keys[j] <= vot) j--;
                if(i < j) {
                    keys[i] = keys[j];
                    indices[i] = indices[j];
                    i++;
                }
                while(i < j && keys[i] >= vot)  i++;
                if(i < j) {
                    keys[j] = keys[i];
                    indices[j] = indices[i];
                    j--;
                }
            }
            keys[i] = vot;
            indices[i] = temp;
            quickSort(keys, begin, j-1, indices);
            quickSort(keys, i+1, end, indices);
        }
    }

    //Reading instance data
    public static void Initializing(File file)
    {
        try {
            int nb_edg=-1, max_edg=0, edg=0;
            int x1, x2, location, we;
            double weightMax = 0;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            int i=0;
            while((s = br.readLine())!=null) {
                String[] str = s.split(" ");
                if (str[0].equals("p")) {
                    v_num = Integer.parseInt(str[2]);
                    nb_edg = Integer.parseInt(str[3]);
                    System.out.println("Number of vertexes = " + v_num);
                    System.out.println("Number of edges = " + nb_edg);
                    edge = new int [v_num][v_num];
                    //addList = new int[v_num];
                    searchList = new int [v_num][v_num];
                    indexList = new int[v_num];
                    currentV = new int[v_num];
                    weight = new int[v_num];
                    //addLen = v_num;
                    for ( int x=0; x<v_num; x++ )
                    {
                        //addList[x] = x;
                        searchList[0][x] = x;
                        indexList[x] = 0;
                        edge[x][x] = 1;
                    }
                    //size = 1;
                    size_best = 1;
                }else if (str[0].equals("e"))
                {
                    x1 = Integer.parseInt(str[1]);
                    x2 = Integer.parseInt(str[2]);
                    x1--; x2--;
                    edge[x1][x2]=1;
                    edge[x2][x1]=1;
                    max_edg++;
                    edg++;
                }else if (str[0].equals("n")) {
                    x1 = Integer.parseInt(str[1]) - 1;
                    x2 = Integer.parseInt(str[2]);
                    weight[x1] = x2;
                }else if (str[0].equals("not_e")) {
                    x1 = Integer.parseInt(str[1]) - 1;
                    x2 = Integer.parseInt(str[2]) - 1;
                    edge[x1][x2] = -1 * Integer.parseInt(str[3]);
                    edge[x2][x1] = -1 * Integer.parseInt(str[3]);
                    max_edg++;
                }
            }
            indexList = quickSort(weight);
            //Calculating the Lagrangian Bound
            weightMax = weight[0];
            edgWeight += weightMax*((double)v_num/2);
            weightMax = weightMax/(v_num-1);
            for (int j = 0; j < v_num; j++) {
                for (int k = j+1; k < v_num; k++) {
                    if (edge[j][k] != 1){
                        if (weightMax + edge[j][k]> 0){
                            edgWeight += weightMax + edge[j][k];
                        }
                    }
                }
            }
            edgeNum = (v_num*(v_num-1)/2)-edg;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
            System.out.println("fail in reading the file！");
        }
    }
    //branch and bound
    public static void search(int addLen,int size, int profit, double edg_Weight){
        int addLen2 = 0;
        int profit2 = 0;
        int profit3 = 0;
        int size2;
        int profit_add = 0;
        double edg_Weight2 = 0;
        int edgeW = 0;
        double weigMax = 0;
        for (int i = 0; i < addLen; i++) {
            currentV[size] = searchList[size][i];
            profit2 = 0;
            size2 = size;
            profit_add = 0;
            addLen2 = 0;
            edg_Weight2 = 0;
            profit3 = 0;
            profit_add = (int)Math.floor(edg_Weight);
            if (profit + profit_add > bestProfit){//prune
                for (int k = i; k < addLen; k++) {
                    if (edge[indexList[searchList[size][i]]][indexList[searchList[size][k]]] != 1){
                        searchList[size+1][addLen2] = searchList[size][k];
                        profit3 += weight[searchList[size+1][addLen2]];
                        addLen2++;
                    }
                }
                weigMax = weight[searchList[size+1][0]];
                if (addLen2 > 1){
                    for (int j = 0; j < addLen2; j++) {
                        for (int k = j+1; k < addLen2; k++) {
                            edgeW = edge[indexList[searchList[size+1][j]]][indexList[searchList[size+1][k]]];
                            if (edgeW != 1){
                                if (weigMax/(addLen2-1)  + edgeW > 0)
                                    edg_Weight2 += weigMax/(addLen2-1)+edgeW;
                            }
                        }
                    }
                    edg_Weight2 += weigMax*((double) addLen2/2);
                }else if(addLen2 == 1) {
                    edg_Weight2 = weigMax;
                }else {
                    edg_Weight2 = 0;
                }
                size2++;
                profit2 = profit + weight[searchList[size][i]];
                for (int j = 0; j < size; j++) {
                    profit2 += edge[indexList[currentV[j]]][indexList[searchList[size][i]]];
                }

                if (profit2 > bestProfit){
                    bestProfit = profit2;
                }
                finishing_time = System.currentTimeMillis();
                if ((finishing_time - starting_time) / 1000 > 10800)
                    break;
                profit3 += profit2;
                if (addLen2 != 0 && profit3 > bestProfit){//prune
                    search(addLen2,size2,profit2,edg_Weight2);
                }
            }
        }
    }


    public static void main(String[] args) throws Exception{
        String filename = "F:\\instance\\C125.9_A_25.dat";
        FileName = filename.replace(".dat", "");
        File file = new File(filename);
        Initializing(file);
        System.out.println(filename);
        int addLen = v_num;
        bestProfit = 0;
        profit = 0;
        starting_time = System.currentTimeMillis();
        search(addLen,0,profit,edgWeight);
        finishing_time = System.currentTimeMillis();
        System.out.println("best = " + bestProfit);
        System.out.println("time = " + (finishing_time - starting_time) / 1000 + "s");
    }
}
