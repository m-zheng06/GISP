
import java.io.*;
import java.util.List;
import java.util.Random;
import static java.lang.Math.pow;
/*
Note:

1.Please make sure that the above paper is cited if you use the code in your research.
2.The software is distributed for academic purposes only. If you wish to use this software for commercial applications, please contact the authors.
*/
//Heuristic Algorithms for GISP
public class GISPadapt {
    static int v_num;
    static int[] ifSolution;//Whether the vertex is in the solution
    static int[][] edge;//adjacency matrix
    static int[][] fixEdge;//Neighbors connected by permanent edges at each point
    static int[] fixEdgeNum;//Number of permanent edges at each vertex
    static int[][] deleteEdge;//Neighbors connected by removable edges at each point
    static int[] deleteEdgeNum;//Number of removable edges at each vertex
    static int[] edgeNumInS;//Number of edges connecting each vertex to a vertex in the solution
    static int[] address;//Position of vertices in addList and swapList
    static int[] tabuin;//tabu table
    static int[] addList;//The add operation set
    static int[] swapList;//The swap operation set
    static int[] swapObjInS;//Target vertices in the solution swapped by the swap operation
    static int addLen;//Number of elements in addlist
    static int swapLen;//Number of elements in swaplist
    static int[] weight;//vertex weight
    static int[] gainProfit;//Gains to the current solution from the addition of vertices
    static int bestProfit;//best value
    static int[] solution;//best solution
    static int solutionLen;//Number of vertices of the optimal solution
    static int Wf;//Container for calculating the value of the objective function during the iteration process
    static int[] TC1;//set of vertices whose markers are not forbidden and satisfy the condition
    static int Iter;
    static int[] FC1;//the set of vertices that are not forbidden and satisfy the condition
    static int len_best = 0;//the length of the current solution (local solution)
    static int TABUL = 7;//tabu tenure
    static int TABUL_init = 2;
    static int Titer;
    static double starting_time, finishing_time, avg_time, end_time;
    static int len_time;//search time
    static int len_improve;//Search Depth
    static int len_W;
    static int[] W_used = new int[100];//Record the weights and (i.e., the objective values of) the optimal solution for per run
    static int[] len_used = new int[100];//Record the optimal solution length for per run
    static double[] time_used = new double[100];//Record the computation time of per run
    static int[] Iteration = new int[100];//Record the number of iterations of per run
    static int[][] notAdjInS;//vertices in the solution that are not adjacent to a vertex
    static int[][] notAdjAddress;//Position in the array of vertices in the solution that are not adjacent to a vertex
    static int mWeight;//Intermediate target value
    static int[] solutionf;//Record the optimal solution
    static int solutionLenf;//Number of vertices of the optimal solution
    static String FileName;
    static int time;
    static int L;
    static List<String> fileList;
    static List<Result> resultList;
    static int N1;
    static int N2;
    static int N3;
    static int N4;
    public static  void Initializing(File file)
    {
        try {
            int nb_edg=-1, max_edg=0;
            int x1, x2, location, we;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            int i=0;
            while((s = br.readLine())!=null) {
                String[] str = s.split(" ");
                if (str[0].equals("p")) {
                    v_num = Integer.parseInt(str[2]);
                    nb_edg = Integer.parseInt(str[3]) + Integer.parseInt(str[4]);
                    System.out.println("Number of vertexes = " + v_num);
                    System.out.println("Number of edges = " + nb_edg);
                    ifSolution = new int [v_num];
                    edge = new int [v_num][v_num];
                    fixEdge = new int [v_num][v_num];
                    fixEdgeNum = new int[v_num];
                    deleteEdge = new int [v_num][v_num];
                    deleteEdgeNum = new int[v_num];
                    address= new int [v_num];
                    tabuin = new int [v_num];
                    edgeNumInS = new int[v_num];
                    addList = new int[v_num];
                    swapList = new int[v_num];
                    weight= new int[v_num];
                    gainProfit = new int[v_num];
                    solution = new int [v_num];
                    swapObjInS = new int [v_num];
                    TC1 = new int [v_num];
                    FC1 = new int [v_num];
                    notAdjInS = new int [v_num][v_num];

                    addLen = v_num;
                    solutionf = new int [v_num];

                    notAdjAddress = new int [v_num][v_num];
                    for ( int x=0; x<v_num; x++ )
                    {
                        addList[x] = x;
                        address[x] = x;
                        solution[x] = -1;
                        for ( int y=0; y<v_num; y++ ){
                            deleteEdge[x][y] = -1;
                            fixEdge[x][y] = -1;
                        }
                    }
                }else if (str[0].equals("e"))
                {
                    x1 = Integer.parseInt(str[1]);
                    x2 = Integer.parseInt(str[2]);
                    we = ((x1 + x2) % 200) + 1;

                    x1--; x2--;
                    if ( x1<0 || x2<0 || x1>=v_num || x2 >=v_num )
                    {
                        System.out.println("### Error of node : x1=" + x1 + ", x2=" + x2);
                    }
                    edge[x1][x2]=1;
                    edge[x2][x1]=1;
                    //fixEdge
                    location = fixEdgeNum[x1];
                    fixEdge[x1][location] = x2;
                    fixEdgeNum[x1] = location + 1;
                    location = fixEdgeNum[x2];
                    fixEdge[x2][location] = x1;
                    fixEdgeNum[x2] = location + 1;
                    max_edg++;
                }else if (str[0].equals("n")) {//weight
                    x1 = Integer.parseInt(str[1]) - 1;
                    x2 = Integer.parseInt(str[2]);
                    weight[x1] = x2;
                    gainProfit[x1] = x2;
                }else if (str[0].equals("not_e")) {//removeable edge
                    x1 = Integer.parseInt(str[1]) - 1;
                    x2 = Integer.parseInt(str[2]) - 1;
                    //记入deleteEdge
                    location = deleteEdgeNum[x1];
                    deleteEdge[x1][location] = x2;
                    deleteEdgeNum[x1] = location + 1;
                    location = deleteEdgeNum[x2];
                    deleteEdge[x2][location] = x1;
                    deleteEdgeNum[x2] = location + 1;
                    edge[x1][x2] = -1 * Integer.parseInt(str[3]);
                    edge[x2][x1] = -1 * Integer.parseInt(str[3]);
                    max_edg++;
                }
            }
            System.out.println("Density = " + (float) max_edg/(v_num*(v_num-1)));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
            System.out.println("fail in reading the file！");
        }
    }

    //random function
    public static int randomInt( int n )
    {
        Random random = new Random();
        return random.nextInt(n);
    }

    //Clear function
    public static void clearGamma()
    {
        int i;
        ifSolution = new int[v_num];
        edgeNumInS = new int[v_num];
        tabuin = new int[v_num];
        swapList = new int[v_num];
        swapObjInS = new int[v_num];
        gainProfit = new int[v_num];
        for( i = 0; i < v_num; i++ )
        {
            gainProfit[i] = weight[i];
            addList[ i ] = i;
            address[ i ] = i;
            solution[ i ] = -1;
        }
        addLen = v_num;
        swapLen = 0;
        solutionLen = 0;
        Wf = 0;
        mWeight = 0;
        bestProfit = 0;
        N1 = 10;
        N2 = 10;
        N3 = 10;
        N4 = 10;
    }

    //Construct initial solution by randomly selecting vertices from addlist
    public static int initSolution(){
        int i, k, l;
        l = 0;
        if( addLen > 30 )
        {
            k = randomInt( addLen );
            return k;
        }
        for( i = 0; i < addLen; i++ )
        {
            k = addList[ i ];
            if( tabuin[ k ] <= Iter )
                TC1[ l++ ] = i;
        }
        if( l == 0 )
            return -1;
        else
        {
            k = randomInt( l );
            k = TC1[ k ];
            return k;
        }
    }

    //Operates in conjunction with ADD, choosing vertice based on profit
    public static int selectAddList(){
        int i, k, l1, l2, w1, w2;
        l1 = 0;
        l2 = 0;
        w1 = 0;
        w2 = 0;

        for( i = 0; i < addLen; i++ )
        {
            k = addList[ i ];
            if( tabuin[ k ] <= Iter )
            {
                if( gainProfit[ k ] > w1 )
                {
                    l1 = 0;
                    w1 = gainProfit[ k ];
                    FC1[ l1++ ] = i;
                }
                else if ( gainProfit[ k ] >= w1 )
                {
                    FC1[ l1++ ] = i;
                }
            }
            else
            {
                if( gainProfit[ k ] > w2 )
                {
                    l2 = 0;
                    w2 = gainProfit[ k ];
                    TC1[ l2++ ] = i;
                }
                else if ( gainProfit[ k ] >= w2 )
                {
                    TC1[ l2++ ] = i;
                }
            }
        }

        if( (l2 > 0) && ( w2 > w1 ) && ((w2+Wf) > bestProfit) )
        {
            k = randomInt( l2 );
            k = TC1[ k ];
            return k;
        }
        else if( l1 > 0 )
        {
            k = randomInt( l1 );
            k = FC1[ k ];
            return k;
        }
        else
        {
            return -1;
        }
    }

    //Update the vertices at the location position in addList to the solution, then update all the data structures
    public static int addInSolution(int location){
        int i, j, k1, m, n, n1;
        m = addList[location];
        solution[solutionLen++] = m;
        ifSolution[m] = 1;
        Wf = Wf + gainProfit[m];
        addLen--;
        n1 = addList[addLen];
        k1 = address[m];
        addList[k1] = n1;
        address[n1] = k1;
        addList[addLen] = -1;
        //更新数据结构
        int max = Math.max(fixEdgeNum[m], deleteEdgeNum[m]);
        for (i = 0;  i< max; i++) {
            if (i < fixEdgeNum[m]){//更新add swap
                n = fixEdge[m][i];
                edgeNumInS[n]++;
                if (edgeNumInS[n] == 1){
                    addLen--;
                    n1 = addList[addLen];
                    k1 = address[n];
                    addList[k1] = n1;
                    address[n1] = k1;
                    addList[addLen] = -1;
                    swapList[swapLen] = n;
                    address[n] = swapLen;
                    swapLen++;
                    swapObjInS[n] = m;
                }else if (edgeNumInS[n] == 2){
                    swapLen--;
                    n1 = swapList[swapLen];
                    k1 = address[n];
                    swapList[k1] = n1;
                    address[n1] = k1;
                    swapList[swapLen] = -1;
                }
                j = edgeNumInS[n] - 1;
                notAdjInS[n][j] = m;
                notAdjAddress[n][m] = j;
            }
            if (i < deleteEdgeNum[m]){
                n1 = deleteEdge[m][i];
                gainProfit[n1] += edge[m][n1];
            }
        }
        if( Wf > mWeight)
        {
            mWeight = Wf;
        }
        if( Wf > bestProfit )
        {
            finishing_time = System.currentTimeMillis();
            bestProfit = Wf;
            len_best = solutionLen;
        }
        return 1;
    }
    //add V into solution
    public static int addV(int m){
        int i, j, k1, n, n1;
        solution[solutionLen++] = m;
        ifSolution[m] = 1;
        Wf = Wf + gainProfit[m];
        addLen--;
        n1 = addList[addLen];
        k1 = address[m];
        addList[k1] = n1;
        address[n1] = k1;
        addList[addLen] = -1;
        int max = Math.max(fixEdgeNum[m], deleteEdgeNum[m]);
        for (i = 0;  i< max; i++) {
            if (i < fixEdgeNum[m]){
                n = fixEdge[m][i];
                edgeNumInS[n]++;
                if (edgeNumInS[n] == 1){
                    addLen--;
                    n1 = addList[addLen];
                    k1 = address[n];
                    addList[k1] = n1;
                    address[n1] = k1;
                    addList[addLen] = -1;
                    swapList[swapLen] = n;
                    address[n] = swapLen;
                    swapLen++;
                    swapObjInS[n] = m;
                }else if (edgeNumInS[n] == 2){
                    swapLen--;
                    n1 = swapList[swapLen];
                    k1 = address[n];
                    swapList[k1] = n1;
                    address[n1] = k1;
                    swapList[swapLen] = -1;
                }
                j = edgeNumInS[n] - 1;
                notAdjInS[n][j] = m;
                notAdjAddress[n][m] = j;
            }
            if (i < deleteEdgeNum[m]){
                n1 = deleteEdge[m][i];
                gainProfit[n1] += edge[m][n1];
            }
        }
        if( Wf > mWeight)
        {
            mWeight = Wf;
        }
        if( Wf > bestProfit )
        {
            finishing_time = System.currentTimeMillis();
            bestProfit = Wf;
            len_best = solutionLen;
        }
        return 1;
    }

    //In conjunction with the SWAP operation, consider the gains to choose the vertex
    public static int selectSwapList(){
        int i, j, k = 0, l, l1, l2, gain, w1, w2, m, n;
        l1 = 0;
        l2 = 0;
        w1 = -1000000;
        w2 = -1000000;
        l = 0;
        for( i = 0; i < swapLen; i++ ){
            m = swapList[i];
            n = swapObjInS[m];
            if( (ifSolution[n] == 1) && (edge[m][n] == 1) ){
                l++;
            }else{
                for( j = 0; j < solutionLen; j++ )
                {
                    k = solution[j];
                    if( edge[m][k] == 1 )
                        break;
                }
                swapObjInS[m] = k;
            }
        }
        for( i = 0; i < swapLen; i++ ){
            m = swapList[i];
            n = swapObjInS[m];
            gain = gainProfit[m] - gainProfit[n];
            if( tabuin[ m ] <= Iter )
            {
                if( gain > w1 )
                {
                    l1 = 0;
                    w1 = gain;
                    FC1[ l1++ ] = i;
                }
                else if ( gain >= w1 )
                {
                    FC1[ l1++ ] = i;
                }
            }
            else
            {
                if( gain > w2 )
                {
                    l2 = 0;
                    w2 = gain;
                    TC1[ l2++ ] = i;
                }
                else if ( gain >= w2 )
                {
                    TC1[ l2++ ] = i;
                }
            }
        }
        if( (l2 > 0) && ( w2 > w1 ) && ((w2+Wf)> bestProfit) )
        {
            k = randomInt( l2 );
            k = TC1[ k ];
            return k;
        }
        else if( l1 > 0 )
        {
            k = randomInt( l1 );
            k = FC1[ k ];
            return k;
        }
        else
        {
            return -1;
        }
    }

    //Update the vertices at the location position in the swapList to the solution and then update all the data structures
    public static int swapInSolution(int location){
        int i, j, k1, m, m1, n, n1, ti;
        m = swapList[location];
        for(ti = 0; ti < solutionLen; ti++)
        {
            m1 = solution[ ti ];
            if( edge[ m1 ][ m ] == 1 )
                break;
        }

        m1 = swapObjInS[m];
        Wf += gainProfit[m] - gainProfit[m1];
        ifSolution[m] = 1;
        solution[solutionLen++] = m;
        k1 = address[m];
        swapLen--;
        n1 = swapList[swapLen];
        swapList[k1] = n1;
        address[n1] = k1;
        swapList[swapLen] = -1;
        int max = Math.max(fixEdgeNum[m], deleteEdgeNum[m]);
        for (i = 0;  i< max; i++) {
            if (i < fixEdgeNum[m]){
                n = fixEdge[m][i];
                edgeNumInS[n]++;
                if (edgeNumInS[n] == 1 && ifSolution[n] == 0){
                    addLen--;
                    n1 = addList[addLen];
                    k1 = address[n];
                    addList[k1] = n1;
                    address[n1] = k1;
                    addList[addLen] = -1;
                    swapList[swapLen] = n;
                    address[n] = swapLen;
                    swapLen++;
                    swapObjInS[n] = m;
                }else if (edgeNumInS[n] == 2){
                    swapLen--;
                    n1 = swapList[swapLen];
                    k1 = address[n];
                    swapList[k1] = n1;
                    address[n1] = k1;
                    swapList[swapLen] = -1;
                }
                j = edgeNumInS[n] - 1;
                notAdjInS[n][j] = m;
                notAdjAddress[n][m] = j;
            }
            if (i < deleteEdgeNum[m]){
                n1 = deleteEdge[m][i];
                gainProfit[n1] += edge[m][n1];
            }
        }
        ifSolution[m1] = 0;
        tabuin[ m1 ] = Iter + TABUL + randomInt(swapLen + 2);
        solutionLen--;
        solution[ti] = solution[solutionLen];
        swapList[swapLen] = m1;
        address[ m1 ] = swapLen;
        swapLen++;
        swapObjInS[m1] = m;

        max = Math.max(fixEdgeNum[m1], deleteEdgeNum[m1]);
        for (i = 0;  i< max; i++) {
            if (i < fixEdgeNum[m1]) {
                n = fixEdge[m1][i];
                edgeNumInS[n]--;
                if (edgeNumInS[n] == 0 && ifSolution[n] == 0){
                    k1 = address[n];
                    swapLen--;
                    n1 = swapList[swapLen];
                    swapList[k1] = n1;
                    address[n1] = k1;
                    swapList[swapLen] = -1;
                    addList[addLen] = n;
                    address[n] = addLen;
                    addLen++;
                }
                else if(edgeNumInS[n] == 1)
                {
                    swapList[swapLen] = n;
                    address[n] = swapLen;
                    swapLen++;
                }
                j = edgeNumInS[n];
                k1 = notAdjAddress[n][m1];
                n1 = notAdjInS[n][j];
                notAdjInS[n][k1] = n1;
                notAdjAddress[n][n1] = k1;
                notAdjInS[n][j] = -1;
            }
            if (i < deleteEdgeNum[m1]){
                n1 = deleteEdge[m1][i];
                gainProfit[n1] -= edge[m1][n1];
            }
        }
        if( Wf > mWeight)
        {
            mWeight = Wf;
        }
        if( Wf > bestProfit )
        {
            finishing_time = System.currentTimeMillis();
            bestProfit = Wf;
            len_best = solutionLen;
        }
        return 1;
    }

    //Select the vertex with the smallest weight in the current cluster to prepare for the DROP operation,
    // and if there are several points with the same and smallest weight,
    // then a random function is used to select one of them
    public static int selectDrop(){
        int i, k, l1, p;
        int w1 = 5000000;
        l1 = 0;
        p = randomInt(100);
        if (p < 50){
            for( i = 0; i < solutionLen; i++ )
            {
                k = solution[i];
                if( gainProfit[k] < w1 )
                {
                    l1 = 0;
                    w1 = gainProfit[k];
                    FC1[l1++] = i;
                }
                else if ( gainProfit[k] <= w1 )
                {
                    FC1[l1++] = i;
                }
            }

            if( l1 == 0 )
                return -1;
            k = randomInt(l1);
            k = FC1[k];
            return k;
        }else {
            k = randomInt(solutionLen);
            return k;
        }

    }

    public static int dropInSolution(){
        int i, j, m1, n, ti, k1, n1;
        ti = selectDrop();
        if( ti == -1 )
            return -1;
        m1 = solution[ti];
        Wf -= gainProfit[m1];
        ifSolution[m1] = 0;
        tabuin[m1] = Iter + TABUL;
        solutionLen--;
        solution[ti] = solution[solutionLen];
        addList[addLen] = m1;
        address[m1] = addLen;
        addLen++;
        int max = Math.max(fixEdgeNum[m1], deleteEdgeNum[m1]);
        for (i = 0;  i< max; i++) {
            if (i < fixEdgeNum[m1]) {
                n = fixEdge[m1][i];
                edgeNumInS[n]--;
                if (edgeNumInS[n] == 0 && ifSolution[n] == 0){
                    k1 = address[n];
                    swapLen--;
                    n1 = swapList[swapLen];
                    swapList[k1] = n1;
                    address[n1] = k1;
                    swapList[swapLen] = -1;
                    addList[addLen] = n;
                    address[n] = addLen;
                    addLen++;
                }
                else if( edgeNumInS[n] == 1 )
                {
                    swapList[swapLen] = n;
                    address[n] = swapLen;
                    swapLen++;
                }
                j = edgeNumInS[n];
                k1 = notAdjAddress[n][m1];
                n1 = notAdjInS[n][j];
                notAdjInS[n][k1] = n1;
                notAdjAddress[n][n1] = k1;
                notAdjInS[n][j] = -1;
            }
            if (i < deleteEdgeNum[m1]){
                n1 = deleteEdge[m1][i];
                gainProfit[n1] -= edge[m1][n1];
            }
        }
        return 1;
    }
    //Remove vertex v from the solution
    public static int dropV(int v){
        int i, j, m1, n, ti = -1, k1, n1;
        for (i = 0; i < solutionLen; i++) {
            if (solution[i] == v)
                ti = i;
        }
        if( ti == -1 )
            return -1;
        m1 = solution[ti];
        Wf -= gainProfit[m1];
        ifSolution[m1] = 0;
        tabuin[m1] = Iter + TABUL;
        solutionLen--;
        solution[ti] = solution[solutionLen];

        addList[addLen] = m1;
        address[m1] = addLen;
        addLen++;
        int max = Math.max(fixEdgeNum[m1], deleteEdgeNum[m1]);
        for (i = 0;  i< max; i++) {
            if (i < fixEdgeNum[m1]) {
                n = fixEdge[m1][i];
                edgeNumInS[n]--;
                if (edgeNumInS[n] == 0 && ifSolution[n] == 0){
                    k1 = address[n];
                    swapLen--;
                    n1 = swapList[swapLen];
                    swapList[k1] = n1;
                    address[n1] = k1;
                    swapList[swapLen] = -1;
                    addList[addLen] = n;
                    address[n] = addLen;
                    addLen++;
                }
                else if( edgeNumInS[n] == 1 )
                {
                    swapList[swapLen] = n;
                    address[n] = swapLen;
                    swapLen++;
                }
                j = edgeNumInS[n];
                k1 = notAdjAddress[n][m1];
                n1 = notAdjInS[n][j];
                notAdjInS[n][k1] = n1;
                notAdjAddress[n][n1] = k1;
                notAdjInS[n][j] = -1;
            }
            if (i < deleteEdgeNum[m1]){
                n1 = deleteEdge[m1][i];
                gainProfit[n1] -= edge[m1][n1];
            }
        }
        return 1;
    }

    public static int tabu(int Max_Iter){
        int k, l, am, am1, ww, ww1, ww2, ti, m1, currentProfit = 0, p, sum_N;
        Boolean ifCanDelete = false;
        Iter = 0;
        clearGamma();
        //Construct the initial solution
        while( true )
        {
            am = initSolution();
            if( am != -1 )
            {
                l = addInSolution( am );
                Iter++;
            }
            else
                break;
        }
        /*
	   Start Neighborhood Comparison
	    */
        while(Iter < Max_Iter){
            currentProfit = bestProfit;
            sum_N = N1 + N2 + N3 + N4;
            p = randomInt(sum_N);
            am = selectAddList();
            am1 = selectSwapList();
            if (p < N1 && am != -1){
                l = addInSolution(am);
                Iter++;
                if (bestProfit > currentProfit){
                    N1++;
                }
            }else if (p < N1 + N2 && am1 != -1){
                l = swapInSolution(am1);
                Iter++;
                if (bestProfit > currentProfit){
                    N2++;
                }
            }else if (p < N1 + N2 + N3){
                if(solutionLen == 0)
                    return bestProfit;
                k = dropInSolution();
                Iter++;
                if (bestProfit > currentProfit){
                    N3++;
                }
                if(k == -1)
                    return bestProfit;
            }else {
                if((am != -1) && (am1 != -1)){
                    ww = gainProfit[addList[am]];
                    ww1 = gainProfit[swapList[am1]] - gainProfit[swapObjInS[swapList[am1]]];
                    if(ww > ww1)
                    {
                        l = addInSolution(am);
                        Iter++;
                    }
                    else
                    {
                        l = swapInSolution(am1);
                        Iter++;
                    }
                }
                //If PA is not null and OM is null, the ADD operation is performed directly
                else if((am != -1) && (am1 == -1))
                {
                    l = addInSolution(am);
                    Iter++;
                }
                else if((am == -1) && (am1 != -1))
                {
                    ti = selectDrop();
                    m1 = solution[ti];
                    ww1 = gainProfit[swapList[am1]] - gainProfit[swapObjInS[swapList[am1]]];
                    ww2 = - gainProfit[m1];
                    if(ww1 > ww2)
                    {
                        l = swapInSolution(am1);
                        Iter++;
                    }
                    else
                    {
                        k = dropInSolution();
                        if(k == -1)
                            return bestProfit;
                        Iter++;
                    }

                }
                else if((am == -1) && (am1 == -1))
                {
                    if(solutionLen == 0)
                        return bestProfit;
                    k = dropInSolution();
                    if(k == -1)
                        return bestProfit;
                    Iter++;
                }
                if (bestProfit > currentProfit){
                    N4++;
                }
            }

            if((System.currentTimeMillis() - starting_time) > avg_time)
                break;
        }
        return bestProfit;
    }

    public static int Max_Tabu(){
        int i, l, lbest;
        lbest = 0;
        Titer = 0;
        int M_iter = 0;
        starting_time = System.currentTimeMillis();
        for(i = 0; i < len_time; i++)
        {
            l = tabu(len_improve);//Obtaining a local optimal solution, corresponding to the internal while loop in the paper
            M_iter = M_iter + Iter;
            if(l > lbest)
            {
                end_time = finishing_time;
                //If the local optimal solution is greater than the previous optimal solution, update the optimal solution
                lbest = l;
                Titer = M_iter;
                len_W = len_best;
            }
            if((System.currentTimeMillis() - starting_time) > avg_time)
                break;
        }
        return lbest;
    }


    public static void main(String[] args) throws Exception {
        String filename = "F:\\instance\\C125.9_A_25.dat";
        File file = new File(filename);
        System.out.println(filename);
        L =  (int) pow(10, 7);
        Initializing(file);
        System.out.println("finish reading data");
        int i, l;
        len_improve = 200000;
        len_time = 1000;
        System.out.println("len_time = " + len_time);
        time = 10;
        avg_time = 300 * 1000;
        for(i = 0; i < time; i++)
        {
            l = Max_Tabu();
            W_used[i] = l;//The sum of the weights of the current iteration, i.e., the optimal solution obtained for that instance run
            len_used[i] = len_W;//The solution length of the current iteration, i.e., the length of the optimal solution obtained in this instance run
            time_used[i] = end_time - starting_time;//The runtime of the current iteration, i.e. the time spent on that instance run
            Iteration[i] = Titer;//The number of iterations for the current iteration, i.e., the number of iterations for that instance run
            System.out.print("i = " + i + " l = " + l + " len_W = " + len_W);
            System.out.println("time："+(end_time - starting_time)/1000+"s");
            end_time = System.currentTimeMillis();
            System.out.println("Total time："+(end_time - starting_time)/1000+"s");
        }
        for(i = 0; i < time; i++)
            if(W_used[i] > bestProfit)
            {
                bestProfit = W_used[i];
            }
        System.out.println("bestProfit=" + bestProfit);

    }
}
