/*
 * Created by JFormDesigner on Wed Mar 12 12:44:13 EET 2014
 */

package gr.teicm.mp.thefmanager.gui.MainForm;

import gr.teicm.mp.thefmanager.DAO.IFileSystemDAO;
import gr.teicm.mp.thefmanager.DAO.LocalFileSystemDAO;
import gr.teicm.mp.thefmanager.controllers.fileoperations.*;
import gr.teicm.mp.thefmanager.controllers.filetable.TableFacade;
import gr.teicm.mp.thefmanager.controllers.filetree.FileSystemController;
import gr.teicm.mp.thefmanager.controllers.filetree.FileTreeCellRenderer;
import gr.teicm.mp.thefmanager.gui.PreferencesForm.PreferencesForm;
import gr.teicm.mp.thefmanager.models.filesystems.TableFileModel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;

public class MainForm extends JFrame {
    private FileSystemController treeFacade;
    private TableFacade tableFacade;
    private TableFileModel tableFileModel;
    private ArrayList<String> visitedItems = new ArrayList<>();
    private String selectedFilePath;
    private File selectedTableFile;
    private File fileToCopy;
    private IFileSystemDAO fileSystemDAO = new LocalFileSystemDAO();
    
    public MainForm() {
        treeFacade = new FileSystemController(fileSystemDAO);
        tableFacade = new TableFacade();

        initComponents();

        tableFileModel = new TableFileModel(filesTable);
        fileTree.setCellRenderer(new FileTreeCellRenderer());
        tableFacade.updateFileTable(fileSystemDAO.getHomeDirectory(), filesTable);
    }

    private void fileTreeItemSelect(TreeSelectionEvent e) {
        String currentPath = treeFacade.getSelectedItemPath(fileTree);
        fileInfoLabel.setText("Folder items: " + Integer.toString(treeFacade.getSelectedItemChildCount(fileTree)));
        showFilePosition(currentPath, true);
        tableFacade.updateFileTable(treeFacade.getSelectedFileItem(fileTree), filesTable);
    }

    private void nextButtonMouseClicked(MouseEvent e) {
        String currentPath = filepathTextField.getText();
        int pathIndex = visitedItems.indexOf(currentPath);

        try {
            showFilePosition(visitedItems.get(pathIndex + 1), false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void previousButtonMouseClicked(MouseEvent e) {
        String currentPath = filepathTextField.getText();
        int pathIndex = visitedItems.indexOf(currentPath);

        try {
            showFilePosition(visitedItems.get(pathIndex - 1), false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showFilePosition(String filePath, boolean addToList) {
        filepathTextField.setText(filePath);

        if (addToList) {
            visitedItems.add(filePath);
        }
    }

    private void fileMenuItemOpenActionPerformed(ActionEvent e) {
        IFileOpenController fileOpen = new FileOpenController();
        int returnedCode = fileOpen.fileOpen(selectedTableFile);

        if (returnedCode == 0) {
            JOptionPane.showMessageDialog(this, "There is no App for this file or Desktop is not supported");
        }
    }

    private void filesTableMousePressed(MouseEvent e) {
        IFileOpenController fileOpen = new FileOpenController();
        if(e.isPopupTrigger()) {
            rightClickTableMenu.show(e.getComponent(),e.getX(),e.getY());
        }

        int selectedRow = filesTable.getSelectedRow();
        int pathColumn = 2;

        selectedFilePath = filesTable.getValueAt(selectedRow, pathColumn).toString();
        tableFacade = new TableFacade(selectedFilePath);
        selectedTableFile = tableFacade.getSelectedTableFile();
        fileIconLbl.setIcon(tableFacade.fileSystemView.getSystemIcon(selectedTableFile));
        fileName.setText(selectedTableFile.getName());
        filePath.setText(selectedTableFile.getPath());
        filepathTextField.setText(selectedTableFile.getPath());
        fileSize.setText(selectedTableFile.length() + " Bytes");
        readAttribute.setSelected(selectedTableFile.canRead());
        writeAttribute.setSelected(selectedTableFile.canWrite());
        executeAttribute.setSelected(selectedTableFile.canExecute());

        if(e.getClickCount() ==2){
            int returnedCode = fileOpen.fileOpen(selectedTableFile);
            if (returnedCode == 0) {
                JOptionPane.showMessageDialog(this, "There is no App for this file or Desktop is not supported");
            }
        }
    }

    private void fileMenuItemDeleteMousePressed(MouseEvent e) {
            IDeleteFileController myDelete = new DeleteFileController();
            boolean isDeleted = myDelete.deleteFile(selectedTableFile);
    }

    private void newFileMenuItemActionPerformed(ActionEvent e) {
        INewFileController newFile = new NewFileController();
        String fileName = JOptionPane.showInputDialog("Give the name of the file", "New File Name");
        boolean fileCreated = newFile.createNewFile(selectedTableFile,fileName);
        if(!fileCreated){
            JOptionPane.showMessageDialog(this, "File not created");
        }
    }

    private void newFolderMenuItemActionPerformed(ActionEvent e) {
        INewFolderController newFolder = new NewFolderController();
        String fileName = JOptionPane.showInputDialog("Give the name of the file", "New File Name");
        boolean fileCreated = newFolder.createNewFolder(selectedTableFile,fileName);
        if(!fileCreated){
            JOptionPane.showMessageDialog(this, "Folder not created");
        }
    }

    private void fileMenuItemRenameMousePressed(MouseEvent e) {
        IRenameFileController myRename = new RenameFileController();
        String newFileName = JOptionPane.showInputDialog("Enter new name", selectedTableFile.getName());

        boolean fileRenamed = myRename.renameFile(selectedTableFile,newFileName);
        if(!fileRenamed){
            JOptionPane.showMessageDialog(this, "File was not renamed!");
        }
    }
    
    private void settingsButtonActionPerformed(ActionEvent e) {
        PreferencesForm preferencesForm = new PreferencesForm();
        preferencesForm.setVisible(true);
    }

    private void filesTableMouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()){
            rightClickTableMenu.show(e.getComponent(),e.getX(),e.getY());
        }
    }

    private void CopyFileMousePressed(MouseEvent e) {
        tableFacade = new TableFacade(selectedFilePath);
        this.fileToCopy = tableFacade.getSelectedTableFile();

       // System.out.println("copy pressed! Name of file to copy:"+selectedTableFile.getName());
    }

    private void PasteMousePressed(MouseEvent e)  {
        ICopyFileController myCopyFile = new CopyFileController();
        boolean isCopied = myCopyFile.copyFile(this.fileToCopy,treeFacade.getSelectedFileItem(fileTree));
    }

    private void fileTreeMouseReleased(MouseEvent e) {
        if(e.isPopupTrigger()){
            rightClickTreeMenu.show(e.getComponent(),e.getX(),e.getY());
        }
    }

    private void fileTreeMousePressed(MouseEvent e) {
        if(e.isPopupTrigger()){
            rightClickTreeMenu.show(e.getComponent(),e.getX(),e.getY());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuBar2 = new JMenuBar();
        fileMenu = new JMenu();
        newMenu = new JMenu();
        newFileMenuItem = new JMenuItem();
        newFolderMenuItem = new JMenuItem();
        fileMenuItemOpen = new JMenuItem();
        fileMenuItemCopy = new JMenuItem();
        fileMenuItemPaste = new JMenuItem();
        fileMenuItemDelete = new JMenuItem();
        fileMenuItemRename = new JMenuItem();
        mgrToolbar = new JToolBar();
        previousButton = new JButton();
        nextButton = new JButton();
        filepathTextField = new JTextField();
        settingsButton = new JButton();
        fileInfoPane = new JPanel();
        rightClickTableMenu = new JPopupMenu();
        fileMenuItemOpen2 = new JMenuItem();
        CopyFile = new JMenuItem();
        fileMenuItemDelete2 = new JMenuItem();
        fileMenuItemRename2 = new JMenuItem();
        Exit = new JMenuItem();
        fileInfoLabel = new JLabel();
        fileNameLbl = new JLabel();
        fileIconLbl = new JLabel();
        fileName = new JLabel();
        filePathLbl = new JLabel();
        filePath = new JLabel();
        fileSizeLbl = new JLabel();
        fileSize = new JLabel();
        fileAttributesLbl = new JLabel();
        readAttribute = new JCheckBox();
        writeAttribute = new JCheckBox();
        executeAttribute = new JCheckBox();
        renameLabel = new JLabel();
        renameTextField = new JTextField();
        rightClickTreeMenu = new JPopupMenu();
        Paste = new JMenuItem();
        Exit2 = new JMenuItem();
        mgrSplitPane = new JSplitPane();
        fileTreeScroll = new JScrollPane();
        fileTree = new JTree(treeFacade.getFileSystemModel());
        tableScrollPane = new JScrollPane();
        filesTable = new JTable();

        //======== this ========
        setTitle("The F* manager");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar2 ========
        {

            //======== fileMenu ========
            {
                fileMenu.setText("File");

                //======== newMenu ========
                {
                    newMenu.setText("New");

                    //---- newFileMenuItem ----
                    newFileMenuItem.setText("File");
                    newFileMenuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            newFileMenuItemActionPerformed(e);
                        }
                    });
                    newMenu.add(newFileMenuItem);

                    //---- newFolderMenuItem ----
                    newFolderMenuItem.setText("Folder");
                    newFolderMenuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            newFolderMenuItemActionPerformed(e);
                        }
                    });
                    newMenu.add(newFolderMenuItem);
                }
                fileMenu.add(newMenu);

                //---- fileMenuItemOpen ----
                fileMenuItemOpen.setText("Open");
                fileMenuItemOpen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fileMenuItemOpenActionPerformed(e);
                    }
                });
                fileMenu.add(fileMenuItemOpen);

                //---- fileMenuItemCopy ----
                fileMenuItemCopy.setText("Copy");
                fileMenu.add(fileMenuItemCopy);

                //---- fileMenuItemPaste ----
                fileMenuItemPaste.setText("Paste");
                fileMenu.add(fileMenuItemPaste);

                //---- fileMenuItemDelete ----
                fileMenuItemDelete.setText("Delete");
                fileMenuItemDelete.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        fileMenuItemDeleteMousePressed(e);
                    }
                });
                fileMenu.add(fileMenuItemDelete);

                //---- fileMenuItemRename ----
                fileMenuItemRename.setText("Rename");
                fileMenuItemRename.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        fileMenuItemRenameMousePressed(e);
                    }
                });
                fileMenu.add(fileMenuItemRename);
            }
            menuBar2.add(fileMenu);
        }
        setJMenuBar(menuBar2);

        //======== mgrToolbar ========
        {
            mgrToolbar.setFloatable(false);
            mgrToolbar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

            //---- previousButton ----
            previousButton.setIcon(new ImageIcon(getClass().getResource("/images/actions/arrow-89-m-L.png")));
            previousButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    previousButtonMouseClicked(e);
                }
            });
            mgrToolbar.add(previousButton);

            //---- nextButton ----
            nextButton.setIcon(new ImageIcon(getClass().getResource("/images/actions/arrow-89-m-R.png")));
            nextButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    nextButtonMouseClicked(e);
                }
            });
            mgrToolbar.add(nextButton);

            //---- filepathTextField ----
            filepathTextField.setHorizontalAlignment(SwingConstants.LEFT);
            mgrToolbar.add(filepathTextField);

            //---- settingsButton ----
            settingsButton.setText("Settings");
            settingsButton.setIcon(new ImageIcon(getClass().getResource("/images/actions/settings-3-m.png")));
            settingsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    settingsButtonActionPerformed(e);
                }
            });
            mgrToolbar.add(settingsButton);
        }
        contentPane.add(mgrToolbar, BorderLayout.PAGE_START);

        //======== fileInfoPane ========
        {
            fileInfoPane.setBorder(new CompoundBorder(
                new SoftBevelBorder(SoftBevelBorder.RAISED),
                null));
            fileInfoPane.setPreferredSize(new Dimension(592, 70));
            fileInfoPane.setLayout(new FlowLayout());

            //======== rightClickTableMenu ========
            {
                rightClickTableMenu.setPreferredSize(new Dimension(70, 80));
                rightClickTableMenu.setMinimumSize(new Dimension(4, 5));

                //---- fileMenuItemOpen2 ----
                fileMenuItemOpen2.setText("Open");
                fileMenuItemOpen2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fileMenuItemOpenActionPerformed(e);
                    }
                });
                rightClickTableMenu.add(fileMenuItemOpen2);

                //---- CopyFile ----
                CopyFile.setText("Copy");
                CopyFile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        CopyFileMousePressed(e);
                    }
                });
                rightClickTableMenu.add(CopyFile);

                //---- fileMenuItemDelete2 ----
                fileMenuItemDelete2.setText("Delete");
                fileMenuItemDelete2.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        fileMenuItemDeleteMousePressed(e);
                    }
                });
                rightClickTableMenu.add(fileMenuItemDelete2);

                //---- fileMenuItemRename2 ----
                fileMenuItemRename2.setText("Rename");
                fileMenuItemRename2.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        fileMenuItemRenameMousePressed(e);
                    }
                });
                rightClickTableMenu.add(fileMenuItemRename2);

                //---- Exit ----
                Exit.setText("Exit");
                rightClickTableMenu.add(Exit);
            }
            fileInfoPane.add(rightClickTableMenu);

            //---- fileInfoLabel ----
            fileInfoLabel.setText("\u03c0\u03bb\u03b7\u03c1\u03bf\u03c6\u03bf\u03c1\u03af\u03b5\u03c2 \u03c3\u03c7\u03b5\u03c4\u03b9\u03ba\u03ac \u03bc\u03b5 \u03c4\u03bf \u03b1\u03c1\u03c7\u03ad\u03b9\u03bf / \u03c6\u03ac\u03ba\u03b5\u03bb\u03bf");
            fileInfoLabel.setFont(fileInfoLabel.getFont().deriveFont(fileInfoLabel.getFont().getStyle() & ~Font.BOLD, fileInfoLabel.getFont().getSize() - 3f));
            fileInfoLabel.setMaximumSize(new Dimension(133, 10));
            fileInfoLabel.setMinimumSize(new Dimension(133, 10));
            fileInfoLabel.setPreferredSize(new Dimension(160, 10));
            fileInfoPane.add(fileInfoLabel);

            //---- fileNameLbl ----
            fileNameLbl.setText("File : ");
            fileNameLbl.setFont(fileNameLbl.getFont().deriveFont(fileNameLbl.getFont().getStyle() | Font.BOLD));
            fileInfoPane.add(fileNameLbl);
            fileInfoPane.add(fileIconLbl);
            fileInfoPane.add(fileName);

            //---- filePathLbl ----
            filePathLbl.setText("Path :");
            filePathLbl.setFont(filePathLbl.getFont().deriveFont(filePathLbl.getFont().getStyle() | Font.BOLD));
            fileInfoPane.add(filePathLbl);
            fileInfoPane.add(filePath);

            //---- fileSizeLbl ----
            fileSizeLbl.setText("Size : ");
            fileSizeLbl.setFont(fileSizeLbl.getFont().deriveFont(fileSizeLbl.getFont().getStyle() | Font.BOLD));
            fileInfoPane.add(fileSizeLbl);
            fileInfoPane.add(fileSize);

            //---- fileAttributesLbl ----
            fileAttributesLbl.setText("File attributes :");
            fileAttributesLbl.setFont(fileAttributesLbl.getFont().deriveFont(fileAttributesLbl.getFont().getStyle() | Font.BOLD));
            fileInfoPane.add(fileAttributesLbl);

            //---- readAttribute ----
            readAttribute.setText("Read");
            fileInfoPane.add(readAttribute);

            //---- writeAttribute ----
            writeAttribute.setText("Write");
            fileInfoPane.add(writeAttribute);

            //---- executeAttribute ----
            executeAttribute.setText("Execute");
            fileInfoPane.add(executeAttribute);

            //---- renameLabel ----
            renameLabel.setText("Rename");
            renameLabel.setVisible(false);
            fileInfoPane.add(renameLabel);

            //---- renameTextField ----
            renameTextField.setToolTipText("insert new name");
            renameTextField.setMinimumSize(new Dimension(20, 22));
            renameTextField.setVisible(false);
            fileInfoPane.add(renameTextField);

            //======== rightClickTreeMenu ========
            {
                rightClickTreeMenu.setPreferredSize(new Dimension(70, 40));

                //---- Paste ----
                Paste.setText("Paste");
                Paste.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        PasteMousePressed(e);
                    }
                });
                rightClickTreeMenu.add(Paste);

                //---- Exit2 ----
                Exit2.setText("Exit");
                rightClickTreeMenu.add(Exit2);
            }
            fileInfoPane.add(rightClickTreeMenu);
        }
        contentPane.add(fileInfoPane, BorderLayout.PAGE_END);

        //======== mgrSplitPane ========
        {
            mgrSplitPane.setBackground(Color.white);
            mgrSplitPane.setOneTouchExpandable(true);
            mgrSplitPane.setResizeWeight(0.1);
            mgrSplitPane.setPreferredSize(new Dimension(539, 418));
            mgrSplitPane.setDividerLocation(150);
            mgrSplitPane.setLastDividerLocation(-1);

            //======== fileTreeScroll ========
            {
                fileTreeScroll.setPreferredSize(new Dimension(79, 324));

                //---- fileTree ----
                fileTree.addTreeSelectionListener(new TreeSelectionListener() {
                    @Override
                    public void valueChanged(TreeSelectionEvent e) {
                        fileTreeItemSelect(e);
                    }
                });
                fileTree.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        fileTreeMousePressed(e);
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        fileTreeMouseReleased(e);
                    }
                });
                fileTreeScroll.setViewportView(fileTree);
            }
            mgrSplitPane.setLeftComponent(fileTreeScroll);

            //======== tableScrollPane ========
            {

                //---- filesTable ----
                filesTable.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        filesTableMousePressed(e);
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        filesTableMouseReleased(e);
                    }
                });
                tableScrollPane.setViewportView(filesTable);
            }
            mgrSplitPane.setRightComponent(tableScrollPane);
        }
        contentPane.add(mgrSplitPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar2;
    private JMenu fileMenu;
    private JMenu newMenu;
    private JMenuItem newFileMenuItem;
    private JMenuItem newFolderMenuItem;
    private JMenuItem fileMenuItemOpen;
    private JMenuItem fileMenuItemCopy;
    private JMenuItem fileMenuItemPaste;
    private JMenuItem fileMenuItemDelete;
    private JMenuItem fileMenuItemRename;
    private JToolBar mgrToolbar;
    private JButton previousButton;
    private JButton nextButton;
    private JTextField filepathTextField;
    private JButton settingsButton;
    private JPanel fileInfoPane;
    private JPopupMenu rightClickTableMenu;
    private JMenuItem fileMenuItemOpen2;
    private JMenuItem CopyFile;
    private JMenuItem fileMenuItemDelete2;
    private JMenuItem fileMenuItemRename2;
    private JMenuItem Exit;
    private JLabel fileInfoLabel;
    private JLabel fileNameLbl;
    private JLabel fileIconLbl;
    private JLabel fileName;
    private JLabel filePathLbl;
    private JLabel filePath;
    private JLabel fileSizeLbl;
    private JLabel fileSize;
    private JLabel fileAttributesLbl;
    private JCheckBox readAttribute;
    private JCheckBox writeAttribute;
    private JCheckBox executeAttribute;
    private JLabel renameLabel;
    private JTextField renameTextField;
    private JPopupMenu rightClickTreeMenu;
    private JMenuItem Paste;
    private JMenuItem Exit2;
    private JSplitPane mgrSplitPane;
    private JScrollPane fileTreeScroll;
    private JTree fileTree;
    private JScrollPane tableScrollPane;
    private JTable filesTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
