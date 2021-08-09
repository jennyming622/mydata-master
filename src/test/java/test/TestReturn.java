package test;

public class TestReturn {

	 public static void main(String[] args) {
		  System.out.println("Test 1 ...");
		  System.out.println(testReturn1(5));
		  System.out.println("Test 2 ...");
		  testReturn2(0);
		  System.out.println("Test 3 ...");
		  testReturn2(5);
		 }

		 public static int testReturn1(int pNum) {
		  int returnData = pNum + 1 + 2 + 3;
		  return returnData;
		 }

		 public static void testReturn2(int pNum) {
		  // 但傳入值為0，離開此方法。
		  if (0 == pNum)
		   return;
		  //
		  System.out.println(pNum / pNum);
		  // 預設在方法的最後都會有 return;
		 }

}
