package com.example.dawid.wtistemple;

/**
 * Created by Dawid on 28.05.2017.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This program demonstrates a usage of the MultipartUtility class.
 * @author www.codejava.net
 *
 */
public class MultiPartFileUpload {

   public static void upload(String pathFile){
        String charset = "UTF-8";
        File uploadFile1 = new File(pathFile);

        String requestURL = "http://192.168.137.1:8000/api/upload/";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addHeaderField("token", GlobalValue.getTokenGlobal());
            multipart.addHeaderField("Test-Header", "Header-Value");

            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");

            multipart.addFilePart("fileUpload", uploadFile1);


            List<String> response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}