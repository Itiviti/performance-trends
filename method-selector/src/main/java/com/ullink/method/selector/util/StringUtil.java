package com.ullink.method.selector.util;

public class StringUtil
{
    public static boolean isEmptyOrNull(final String stringToTest)
    {
        if (stringToTest != null && stringToTest.length() > 0)
        {
            for (int i = 0; i < stringToTest.length(); i++)
            {
                if ((stringToTest.charAt(i)) != ' ')
                {
                    return false;
                }
            }
        }
        return true;
    }
}
