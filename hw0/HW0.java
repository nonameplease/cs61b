public class HW0{

     
    public static void main(String[] args){
        
	int[] array = {1,2,3,4,5,6,7,0};
        
	int max = whilemax(array);

	System.out.println(max);
	int max2 = formax(array);
	System.out.println(max2); 
	boolean dicision = threesum(array);
	System.out.println(dicision);    
	boolean ddicision = dthreesum(array);
	System.out.println(ddicision);
    }
     
     
    public static int whilemax(int[] a){
         
	int index = 0;
         
	int largest = a[index];
         
	while(index < a.length){
             
	    if(a[index] > largest){
                 
	        largest = a[index];
             
	    }
             
	    index+=1;
         
	}
         
    return largest;    
    }

    
    public static int formax(int[] a){
	int largest = a[0];
	for(int i = 0; i < a.length; i++){
	    if(a[i] > largest){
		largest = a[i];
	    }
	}
	return largest;
    }

    public static boolean threesum(int[] a){
	for(int i = 0; i < a.length; i++){
	    int sum1 = a[i];
	    for(int j = 0; j < a.length; j++){
		int sum2 = a[j];
		for(int k = 0; k < a.length; k++){
		    int sum3 = a[k];
		    if(sum1 + sum2 + sum3 == 0){
			return true;
		    }
		}
	    }
	}
	return false;
    }

    public static boolean dthreesum(int[] a){
	for(int i = 0; i < a.length; i++){
	    int sum1 = a[i];
	    for(int j = i + 1; j < a.length; j++){
		int sum2 = a[j];
		for(int k = j + 1; k < a.length; k++){
		    int sum3 = a[k];
		    if(sum1 + sum2 + sum3 == 0){
			return true;
		    }
		}
	    }
	}
	return false;
    }
}
