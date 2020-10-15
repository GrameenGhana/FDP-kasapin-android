package org.grameen.fdp.kasapin.syncManager;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UploadDataManagerTest {

    List<String> list = new ArrayList<>();
    int size = 0;
    int REQUEST_SIZE = 2;
    int INDEX = 0;
    int BATCH_SIZE = 3;
    @Before
    public void setUp() throws Exception {
        for(int i = 0; i < 10; i++)
            list.add("A" + (1+ i));

        size = list.size();

        System.out.println("List size is " + size);

    }

    @Test
    public void testSyncImagesInBatches() {
         for (int i = 0; i < REQUEST_SIZE; i++) {
            System.out.println("************* " + i +" **************");

            System.out.println("INDEX " + INDEX);

            List<String> subList;
            if(size <= BATCH_SIZE) {
                subList = list;
             }else {
                if (size - INDEX >= BATCH_SIZE) {
                    subList = list.subList(INDEX, BATCH_SIZE + INDEX);
                    INDEX += BATCH_SIZE;

                } else {
                    subList = list.subList(INDEX, size);
                    INDEX = size;
                }
            }
            System.out.println(subList.toString());
            System.out.println("END INDEX ==> " + INDEX);



            if (INDEX == 0 || INDEX >= size) {
                System.out.println("BREAK LOOP");
                break;
            } else
                testSyncImagesInBatches();

        }

        System.out.println("Loop End.");

    }

}