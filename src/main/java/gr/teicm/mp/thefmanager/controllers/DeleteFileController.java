package gr.teicm.mp.thefmanager.controllers;

import gr.teicm.mp.thefmanager.DAO.FileDAO;
import gr.teicm.mp.thefmanager.DAO.IFileDAO;

import java.io.File;

/**
 * Created by Elias Myronidis on 3/4/2014.
 */
public class DeleteFileController implements IDeleteFileController {
    @Override
    public boolean deleteFile(File selectedFile){
        IFileDAO myFile = new FileDAO();
        boolean isDeleted = myFile.deleteFile(selectedFile);
        return isDeleted;
    }
}
