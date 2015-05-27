package com.wadidejla.utils;

import com.degla.restful.models.RestfulFile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by snouto on 23/05/15.
 */
public class FilesUtils {




    public static void prepareFiles(List<RestfulFile> files)
    {

        //prepare the files
        prepare(files);

        //then arrange them according to the cabin Id
        Collections.sort(files, new Comparator<RestfulFile>() {
            @Override
            public int compare(RestfulFile restfulFile, RestfulFile t1) {

                char firstCabinId = restfulFile.getCabinetId().charAt(restfulFile.getCabinetId().length()-1);
                char secondCabinId = t1.getCabinetId().charAt(t1.getCabinetId().length()-1);

                if(firstCabinId > secondCabinId)
                    return 1;
                else if (firstCabinId == secondCabinId) return 0;
                else return -1;

            }
        });
    }

    private static void prepare(List<RestfulFile> files)
    {
        for(RestfulFile file : files)
        {
            file.prepare();
        }
    }
}
