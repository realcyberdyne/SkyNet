package com.cyberdyne.skynet.Services.Encription;

import java.util.UUID;

public class KeyGenerator
{

    //Get genrate key start
    public static String GenerateRandomKey()
    {
        return UUID.randomUUID().toString().replace("-","").substring(0,24);
    }
    //Get genrate key end

}
