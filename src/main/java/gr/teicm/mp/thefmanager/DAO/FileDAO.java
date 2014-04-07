package gr.teicm.mp.thefmanager.DAO;

import java.io.File;

/**
 * Created by Elias Myronidis on 3/4/2014.
 */
public class FileDAO implements IFileDAO {
    @Override
    public boolean deleteFile(File selectedFile){

        if (selectedFile.delete())
            return true;
        else
            return false;

    }

    public boolean renameFile(File selectedFile, String newName){

        String newFileName = selectedFile.getParent() +"\\" + newName ;
        File newFile = new File(newFileName);


        if(selectedFile.renameTo(newFile))
            System.out.print("Success");
        else
            System.out.print("Fail");
        return true;
    }
}
