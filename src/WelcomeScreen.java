import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
  This class is for the welcome screen that greets the user when they enter the software
  The user is greeted with a message prompting them to input a file if they have one or they can skip to the next window
*/
class WelcomeScreen extends JFrame {
   private String filename = "";
   private String dir = "";

   public WelcomeScreen() {
      try {
         // Sets the UI to look native to whatever machine the user is running the software on
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         // Sets the icon for the UI to the specified path
         URL resource = this.getClass().getResource("/images/logo.png");
         BufferedImage image = ImageIO.read(resource);
         this.setIconImage(image);
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
            | UnsupportedLookAndFeelException | IOException e1) {
         e1.printStackTrace();
      }

      // Sets up the popup welcome screen that the user is greeted with
      // The user is urged to read the readme.txt that is included in the software in order to understand the proogram better
      // If the user has a inpout file, they clock "open" if not, they click "skip" (clicking the "x" button in the window accomplishes trhe same thing)
      String[] options = { "Open", "Skip" };
      int n = JOptionPane.showOptionDialog(this,
            "Welcome! Please press \"Open\" if you have a file you'd like to import. If you don't, press \"Skip\".\n\nNOTE: Please read the readme.txt for information about how this program operates.",
            "Welcome!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);

      if (n == JOptionPane.YES_OPTION) {
         btnOpenClick();
         new GameCollectionWindow(filename, dir).setVisible(true);
         dispose();
      }

      if (n == JOptionPane.NO_OPTION) {
         new GameCollectionWindow(filename, dir).setVisible(true);
         dispose();
      }
   }

   // Handles the UI for retrieving a file for the software to import, if they click cancel here no file is used
   private void btnOpenClick() {
      JFileChooser c = new JFileChooser();
      int rVal = c.showOpenDialog(WelcomeScreen.this);

      if (rVal == JFileChooser.APPROVE_OPTION) {
         filename = c.getSelectedFile().getAbsolutePath();
         dir = c.getCurrentDirectory().toString();
      }

      if (rVal == JFileChooser.CANCEL_OPTION) {
         filename = "";
         dir = "";
      }
   }
}
