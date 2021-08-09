package test;

public class TestClassPath {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(TestClassPath.class.getName());
		System.out.println(TestClassPath.class.getClassLoader().getSystemClassLoader());
		System.out.println(TestClassPath.class.getClassLoader().getResource("").getPath());
		System.out.println(TestClassPath.class.getClassLoader().getResource("").getPath() );
		System.out.println(TestClassPath.class.getClass().getResource("/").getPath());
	}

}
