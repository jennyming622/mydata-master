package test;

import tw.gov.ndc.emsg.mydata.util.ValidateHelper;

public class ValidateTest {

    public static void main(String args[]) {

        Boolean check = ValidateHelper.isValidResidentPermit("S900000011");
        System.out.println(check);
    }
}
