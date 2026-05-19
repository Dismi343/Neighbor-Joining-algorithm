public class Njalgo{

private static int size = 5;
//-------------------------------------------------------------------------------------------
    public static void main(String[] args){
        //Initial labels
        String[] labels = {"A", "B", "C", "D", "E"};
        // Initialize the Q-matrix with the given values and assuming that A=0, B=1, C=2, D=3, E=4
        double Qmatrix[][] = {
            {0,10,12,8,7},
            {10,0,4,4,14},
            {12,4,0,6,16},
            {8,4,6,0,12},
            {7,14,16,12,0}
            };

        int nodeCounter = 0;

        System.out.println("Initialized Q-matrix:");
        //printing the Q-matrix
        for (int i=0;i<Qmatrix.length;i++){
            for (int j=0;j<Qmatrix[i].length;j++){
                System.out.print(Qmatrix[i][j] + " ");
            }
            System.out.println();
        }
        
        System.out.println();

        while (Qmatrix.length>2){
            double sValue =0;
            double minSValue = Double.MAX_VALUE;
            //these will be used to store the coordinates of the minimum S-value
            int x=0,y=0;
            double dxy=0;
            double minRi=0,minRj=0;


            System.out.println("Matrix with S-values:");
            for (int i=0;i<Qmatrix.length;i++){
                for (int j=0;j<Qmatrix[i].length;j++){
                    double Ri=Rvalue(Qmatrix,i);
                    double Rj=Rvalue(Qmatrix,j);
                    if(i==j){
                        sValue=0;
                    }
                    else{
                    sValue = SvalueCal(Qmatrix[i][j],Ri,Rj,Qmatrix.length);
                    }
                
                    if (sValue < minSValue && i!=j){
                        minSValue = sValue;
                        x=i;
                        y=j;
                        dxy=Qmatrix[i][j];
                        minRi=Ri;
                        minRj=Rj;
                    }
                    System.out.print(sValue + " ");
                    }
                    System.out.println();

                }
                System.out.println();
            
            System.out.println();
            System.out.println("Minimum S-value: " + minSValue);
            System.out.println("Coordinates of minimum S-value: (" + x + ", " + y + ")");
            System.out.println("Distance of minimum S-value: " + dxy);

            double distanceX = distanceofX(dxy,minRi,minRj,Qmatrix.length);
            System.out.println("Distance of x = " + x + " from z: " + distanceX);
            double distanceY = distanceofy(dxy,distanceX);
            System.out.println("Distance of y = " + y + " from z: " + distanceY);

            distanceX = Math.abs(distanceX);
            distanceY = Math.abs(distanceY);
            //creating Newick labels
            String newNodeLabel = "(" + labels[x] + ":" + distanceX + "," + labels[y] + ":" + distanceY + ")Node_" + nodeCounter++;

            //update the labels array
            labels = updateLabels(labels, x, y, newNodeLabel);


            System.out.println("---------------------------------------------------------------------------------");
            //update the Q-matrix
            Qmatrix = distanceMatrixUpdate(Qmatrix,x,y);
            System.out.println("Updated Q-matrix:");
            for (int i=0;i<Qmatrix.length;i++){
                for (int j=0;j<Qmatrix[i].length;j++){
                    System.out.print(Qmatrix[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println("---------------------------------------------------------------------------------");

        }

       
        //final step to join the last two nodes
        double finalDist = Qmatrix[0][1];
        double halfDist = finalDist / 2.0;
        String finalNewick = "(" + labels[0] + ":" + halfDist + "," + labels[1] + ":" + halfDist + ")Root;"; 

        System.out.println("Final Newick String:");
        System.out.println(finalNewick);

        NewickTree tree = NewickTree.readNewickFormat(finalNewick);
        System.out.println("\nTree Visualization:");
        tree.visualize();
    }
//-------------------------------------------------------------------------------------------



    //method to calculate the R values
    //-------------------------------------------------------------------------------------------
    static double Rvalue(double Qmatrix[][],int j){
        double sum = 0;
        for(int i=0;i<Qmatrix.length;i++){
            sum += Qmatrix[j][i];
        }
        return sum;
    }
    //-------------------------------------------------------------------------------------------
    
    //method to calculate the S values
    //-------------------------------------------------------------------------------------------
    static double SvalueCal(double d,double Ri,double Rj, int n){
        // int n=size;
        double sValue = (n-2)*d-Rj-Ri;
        return sValue;

    }
    //-------------------------------------------------------------------------------------------

    //calcualting the distance of x and y from z node
    //-------------------------------------------------------------------------------------------
    static double distanceofX(double dxy,double Ri,double Rj,int n){
        return dxy/2 +((Ri-Rj)/(2*(n-2)));
    }
     static double distanceofy(double dxy,double distanceX){
        return dxy - distanceX;

    }
    //-------------------------------------------------------------------------------------------
    
    //updating the distance matrix 
    //-------------------------------------------------------------------------------------------
    static double[][] distanceMatrixUpdate(double[][] Qmatrix, int x, int y) {
    int n = Qmatrix.length;
    double[][] newQmatrix = new double[n - 1][n - 1];
    
    //identify the lower and higher indices to skip when copying the old matrix
    int low = Math.min(x, y);
    int high = Math.max(x, y);

    int newI = 0;
    for (int i = 0; i < n; i++) {
        // Skip rows x and y, as they are being merged
        if (i == low || i == high) continue;

        int newJ = 0;
        for (int j = 0; j < n; j++) {
            // Skip columns x and y
            if (j == low || j == high) continue;
            
            // Copy existing distances between other nodes
            newQmatrix[newI][newJ] = Qmatrix[i][j];
            newJ++;
        }
        
        // Calculate distance from the NEW node (placed at the last index) to node 'i'
        // Distance formula: d(z, i) = [d(x, i) + d(y, i) - d(x, y)] / 2
        double distToZ = (Qmatrix[i][x] + Qmatrix[i][y] - Qmatrix[x][y]) / 2;
        newQmatrix[newI][n - 2] = distToZ;
        newQmatrix[n - 2][newI] = distToZ;
        
        newI++;
    }
    
    // Set the diagonal of the new node to 0
    newQmatrix[n - 2][n - 2] = 0;
    
    return newQmatrix;
    }
    //-------------------------------------------------------------------------------------------

    
    //method to update the labels
    //-------------------------------------------------------------------------------------------
    static String[] updateLabels(String[] oldLabels, int x, int y, String newLabel) {
        String[] nextLabels = new String[oldLabels.length - 1];
        int count = 0;
        
        int low = Math.min(x, y);
        int high = Math.max(x, y);

        for (int i = 0; i < oldLabels.length; i++) {
            if (i != low && i != high) {
                nextLabels[count++] = oldLabels[i];
            }
        }
        nextLabels[oldLabels.length - 2] = newLabel; // Put new node at the end
        return nextLabels;
    }
    //-------------------------------------------------------------------------------------------
} 