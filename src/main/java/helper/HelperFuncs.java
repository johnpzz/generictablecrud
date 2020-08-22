package helper;

import com.mysql.cj.core.util.StringUtils;

public class HelperFuncs {

    public static String toCamel(String input) {

        // ex: books_reviews
        String result = "";

        if (input.contains("_")) {
            String[] strings = input.split("_");

            for (String s : strings) {
                s = s.toLowerCase();
                s = upperCaseFirstLetter(s);
                result +=s ;
            }

        } else {
            result = upperCaseFirstLetter(input.toLowerCase());
        }
            return result;
    }


    public static String upperCaseFirstLetter(String input) {
        //error prone
        //char result = StringUtils.firstAlphaCharUc(input, 0);
       // return input.replace(input.charAt(0), result);
        return input.substring(0,1).toUpperCase() + input.substring(1);
    }

    public static void main(String[] args) {
        System.out.println(toCamel("books_reviews"));

    }
}
