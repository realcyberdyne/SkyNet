package com.cyberdyne.skynet;

import com.cyberdyne.skynet.Services.Config.Config;
import com.cyberdyne.skynet.Services.Encription.EncriptionCLS;
import com.cyberdyne.skynet.Services.Encription.KeyGenerator;
import com.cyberdyne.skynet.Services.VPN.Functions.Tunnel;

public class App
{
    public static void main( String[] args ) throws Exception
    {

        //Get config from file
        new Config();

        //Get genrate key
        String key=KeyGenerator.GenerateRandomKey();
        System.out.println("AES Key is : "+key);

        //Get begin tunnel
        new Tunnel();

    }
}

