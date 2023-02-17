import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
   Handles the main window of the software.
   First, the file (if any) that the user chose in the welcome screen is parsed and inserted into the list (the list keeps order).
   Then, we setup the UI adding all the needed sub-components for the user to interact with.
   Then, when those are done we setup the list for the user to see, we make make it a table so the user can view it in a more cohesive and uderstandable manner. This also helps us set the table to be more interactive so that when the suer uses a sub-component of the UI we can view the elements of the list by row-column instead of whole nodes that we have to access parts of.
   When the list is done setting up, the UI is packed and set to visible  for the user to see.
*/
public class GameCollectionWindow extends JFrame {
   GameList list;
   private JMenuBar menuBar;

   private JMenu menuFile;
   private JMenuItem menuFileExport;
   private JMenuItem menuFileOpen;
   private JMenuItem menuFileClose;

   private JMenu menuEdit;
   private JMenuItem menuEditAdd;
   private JMenu menuEditSort;
   private JMenuItem menuEditSortTitle;
   private JMenuItem menuEditSortPlat;
   private JMenuItem menuEditClear;

   private JPanel pnlSearch;
   private JTextField txtfldSearch;
   private JLabel lblSearch;

   private JTable tblFinishedGameList;

   private String filename = "";

   public GameCollectionWindow(String fn, String d) {
      // list setup (get the file then insert it's elements into the list)
      filename = fn;

      list = new GameList();
      if (!filename.isEmpty())
         list.importFile(filename);

      // Main Window setup
      setTitle("My Game Collection");
      try {
         // Sets the icon for this window to the specified path
         URL resource = this.getClass().getResource("/images/logo.png");
         BufferedImage image = ImageIO.read(resource);
         this.setIconImage(image);
      } catch (IOException e1) {
         e1.printStackTrace();
      }
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      getContentPane().setLayout(new BorderLayout());

      // Menu bar
      menuBar = new JMenuBar();
      menuBar.setOpaque(true);
      setJMenuBar(menuBar);

      // Adds a file menu
      // Can also be accessed with Alt+F
      menuFile = new JMenu("File");
      menuFile.setMnemonic(KeyEvent.VK_F);
      menuBar.add(menuFile);

      // Sets the menu item for the user to open new files
      // This menu item can also be accessed by pressing ctrl+o
      menuFileOpen = new JMenuItem("Open...");
      menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
      menuFileOpen.getAccessibleContext().setAccessibleDescription("Open a file");
      menuFileOpen.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            menuFileOpenPressed(evt);
         }
      });
      menuFile.add(menuFileOpen);

      // Sets the menu item for the user to export files.
      // This menu item can also be accessed by pressing ctrl+s
      menuFileExport = new JMenuItem("Export...");
      menuFileExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
      menuFileExport.getAccessibleContext().setAccessibleDescription("Export the list");
      menuFileExport.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            menuFileExportPressed(evt);
         }
      });
      menuFile.add(menuFileExport);

      menuFile.addSeparator();

      // Sets the menu item for the user to close the software
      menuFileClose = new JMenuItem("Close");
      menuFileClose.getAccessibleContext().setAccessibleDescription("Close the program");
      menuFileClose.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            System.exit(0);
         }
      });
      menuFile.add(menuFileClose);

      // adds a edit menu
      // Can also be accessed with Alt+E
      menuEdit = new JMenu("Edit");
      menuFile.setMnemonic(KeyEvent.VK_E);
      menuBar.add(menuEdit);

      // Sets the menu item for the user to add new entries to the list.
      // This menu item can also be accessed by pressing ctrl+insert
      menuEditAdd = new JMenuItem("Add");
      menuEditAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, ActionEvent.CTRL_MASK));
      menuEditAdd.getAccessibleContext().setAccessibleDescription("Add an entry");
      menuEditAdd.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            menuEditAddPressed(evt);
         }
      });
      menuEdit.add(menuEditAdd);

      // Sets a sub-menu for the user to sort the items in the list.
      menuEditSort = new JMenu("Sort");
      menuEditSort.getAccessibleContext().setAccessibleDescription("Change sort method");

      menuEditSortTitle = new JMenuItem("By title");
      menuEditSortTitle.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            menuEditSortPressed(evt, 1);
         }
      });
      menuEditSort.add(menuEditSortTitle);

      menuEditSortPlat = new JMenuItem("By platform");
      menuEditSortPlat.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            menuEditSortPressed(evt, 2);
         }
      });
      menuEditSort.add(menuEditSortPlat);

      menuEdit.add(menuEditSort);

      menuEdit.addSeparator();

      // Sets the menu item for the user to clear their list.
      // This menu item can also be accessed by pressing ctrl+del
      menuEditClear = new JMenuItem("Clear");
      menuEditClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));
      menuEditClear.getAccessibleContext().setAccessibleDescription("Clear the list");
      menuEditClear.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            menuEditClearPressed(evt);
         }
      });
      menuEdit.add(menuEditClear);

      setJMenuBar(menuBar);

      // Sets up the search bar that appears on top of the table
      pnlSearch = new JPanel();
      pnlSearch.setLayout(new GridBagLayout());
      GridBagConstraints bagData = new GridBagConstraints();

      // A label for the search bar
      // bagData is used to tell the gui what constraints to use for the GridBagLayout
      lblSearch = new JLabel("Search: ");
      bagData.anchor = GridBagConstraints.CENTER;
      bagData.gridx = 0;
      bagData.gridy = GridBagConstraints.CENTER;
      bagData.gridwidth = 2;
      bagData.fill = GridBagConstraints.HORIZONTAL;
      pnlSearch.add(lblSearch, bagData);

      txtfldSearch = new JTextField(50);
      txtfldSearch.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            menuEditSearchPressed(evt, txtfldSearch.getText());
         }
      });
      bagData.gridx = 2;
      bagData.gridwidth = 1;
      bagData.fill = GridBagConstraints.NONE;
      pnlSearch.add(txtfldSearch, bagData);

      getContentPane().add(pnlSearch, BorderLayout.NORTH);

      // Table setup
      // Set up the table using the DefaultTableModel
      // Have the table be selectable (helps with the search function), take up the space it's in, and be scrollable if too large
      tblFinishedGameList = new JTable(list);

      tblFinishedGameList.setColumnSelectionAllowed(true);
      tblFinishedGameList.setRowSelectionAllowed(true);
      tblFinishedGameList.setPreferredScrollableViewportSize(new Dimension(600, 700));
      tblFinishedGameList.setFillsViewportHeight(true);

      JScrollPane scrollPane = new JScrollPane(tblFinishedGameList);
      scrollPane.setBorder(BorderFactory.createTitledBorder("Game List"));
      scrollPane.setBounds(5, 218, 884, 700);

      getContentPane().add(scrollPane, BorderLayout.CENTER);

      // Pack the UI into a big enough window and have it centered on the user's
      // screen
      pack();
      setLocationRelativeTo(null);
   }

   // Method to let user import a new list
   protected void menuFileOpenPressed(ActionEvent evt) {
      // warn the user about opening a new list clearing the old one
      int n = JOptionPane.showConfirmDialog(this, "Are you sure? Your list will be lost.", "Warning",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

      if (n == JOptionPane.NO_OPTION)
         return;

      JFileChooser c = new JFileChooser();
      int rVal = c.showOpenDialog(this);

      if (rVal == JFileChooser.APPROVE_OPTION) {
         filename = c.getSelectedFile().getAbsolutePath();
      }

      if (rVal == JFileChooser.CANCEL_OPTION) {
         return;
      }

      list.importFile(filename);
      list.fireTableDataChanged();
      list.fireTableStructureChanged();
      tblFinishedGameList.revalidate();
      tblFinishedGameList.repaint();
   }

   // Method to let the user export their list to a file
   protected Boolean menuFileExportPressed(ActionEvent evt) {
      JFileChooser fileChooser = new JFileChooser();
      // limit the user to outputtioing the file in .csv or .txt format
      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("csv", "csv"));
      fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("txt", "txt"));
      fileChooser.setDialogTitle("Export...");

      if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
         if (list.exportList(tblFinishedGameList.getModel(), fileChooser.getSelectedFile()))
            JOptionPane.showMessageDialog(this, "Your file has been exported. Check the directory you uploaded from.");
         return true;
      }

      return false;
   }

   // Method to add a item to the list
   protected void menuEditAddPressed(ActionEvent evt) {
      JPanel input = new JPanel();
      input.setLayout(new GridLayout(0, 2, 2, 2));

      // set up the fields for the user to input
      JTextField titleField = new JTextField(5);
      JTextField platformField = new JTextField(5);
      JTextField releaseField = new JTextField(5);
      JTextField ratingField = new JTextField(5);

      input.add(new JLabel("Title: "));
      input.add(titleField);

      input.add(new JLabel("Platform: "));
      input.add(platformField);

      input.add(new JLabel("Release date*(mm/dd/yyyy): "));
      input.add(releaseField);

      input.add(new JLabel("Rating*: "));
      input.add(ratingField);

      input.add(new JLabel("* = optional"));

      int val = JOptionPane.showConfirmDialog(this, input, "Enter the information", JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE);

      if (val == JOptionPane.YES_OPTION) {
         if (titleField.getText().isEmpty() || platformField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ERROR: Title and platform field is required.");
            return;
         }

         // set up an arraylist to add the parameters to in order to check that the
         // required fields were filled out, and to see which optional fiields were blank
         // before calling the creation method
         ArrayList<String> args = new ArrayList<String>(2);
         args.add(titleField.getText());
         args.add(platformField.getText());
         if (!releaseField.getText().isEmpty())
            args.add(releaseField.getText());
         if (!ratingField.getText().isEmpty())
            args.add(ratingField.getText());

         String arg[] = new String[args.size()];
         for (int i = 0; i < args.size(); i++)
            arg[i] = args.get(i);

         Game in = list.validateAndCreate(arg);
         if (in == null) {
            JOptionPane.showMessageDialog(this, "ERROR: Check your inputs.");
            return;
         }

         list.addRow(in);

         list.fireTableDataChanged();
         list.fireTableStructureChanged();
         tblFinishedGameList.revalidate();
         tblFinishedGameList.repaint();
      }
   }

   // Method to change sorting method
   protected void menuEditSortPressed(ActionEvent evt, int method) {
      // Change the sorting method set in the GameList class
      if (method == 1)
         list.setSortMethod("Alph");
      if (method == 2)
         list.setSortMethod("Plat");

      // transfer from one list to the next (the next list will have the new sorting method)
      GameList newList = new GameList();
      for (int i = 0; i < list.getRowCount(); i++)
         newList.addRow(list.remove(i));

      // set the main list to the new one so the old one get's deleted
      list.setGameList(newList.getGameList());
      list.fireTableDataChanged();
      list.fireTableStructureChanged();
      tblFinishedGameList.revalidate();
      tblFinishedGameList.repaint();
   }

   protected void menuEditClearPressed(ActionEvent evt) {
      // warn the user about clearing the list
      int n = JOptionPane.showConfirmDialog(this, "Are you sure? Your list will be lost.", "Warning",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

      if (n == JOptionPane.NO_OPTION)
         return;

      list.getGameList().clear();
      list.fireTableDataChanged();
      list.fireTableStructureChanged();
      tblFinishedGameList.revalidate();
      tblFinishedGameList.repaint();
   }

   // Method to implement a search function
   protected void menuEditSearchPressed(ActionEvent evt, String term) {
      ArrayList<String> results = new ArrayList<String>();
      // This method uses a hashset to search through the list
      // Split the search field into an array of search terms separated by spaces (set the terms in lowercase to avoid being exact)
      String[] terms = term.toLowerCase().split(" ");

      // create hashsets for the search terms (initalized), and the row we're searching in (don't initialize yet)
      Set<String> searchFor = new HashSet<>();
      Set<String> searchIn;

      // Add the elements from the search trerm array
      for (String i : terms)
         searchFor.add(i);

      // Go through each row (since we only need to look up the title)
      for (int row = 0; row <= tblFinishedGameList.getRowCount() - 1; row++) {
         // Grab the row data for the title and put the title field into the hash (again all lowercase and split into title terms)
         String[] rowData = ((String) tblFinishedGameList.getValueAt(row, 0)).toLowerCase().split(" ");
         searchIn = new HashSet<>();

         for (String i : rowData)
            searchIn.add(i);

         // Go through the hash for the row and only retain a title if that matches any data in the search term array (if no data is matched, the hash becomes empty)
         searchIn.retainAll(searchFor);
         // if there's some row that has a string that matches some string in the search term array, we highlight that row within the table
         if (!searchIn.isEmpty()) {
            results.add((String) tblFinishedGameList.getValueAt(row, 0));
         }
      }

      // output the results to the user
      StringBuilder resultsOutput = new StringBuilder("<html>");
      resultsOutput.append("Following titles found in search: <br><br>");
      for (int i = 0; i < results.size(); i++) {
         resultsOutput.append(results.get(i));
         resultsOutput.append("<br>");
      }
      resultsOutput.append("</html>");
      JOptionPane.showMessageDialog(null, resultsOutput.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
   }

}
