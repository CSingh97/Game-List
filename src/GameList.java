import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class GameList extends DefaultTableModel {
   private static String[] columnNames = { "Title", "Platform", "Release Date", "Rating" };
   private static String sortMethod = "Plat";
   private static LinkedList<Game> gameList = new LinkedList<>();

   public void importFile(String input) {
      // if the file is blank, we don't make a list
      if (input.equals(""))
         return;
      gameList = new LinkedList<>();
      try {
         BufferedReader file = new BufferedReader(new FileReader(input));
         String line = "";
         while ((line = file.readLine()) != null) {
            String[] in = line.split(", ");
            Game newInput = validateAndCreate(in);
            if (newInput == null) {
               continue;
            } else {
               if (sortMethod == "Plat")
                  addByPlat(newInput);
               else if (sortMethod == "Alph")
                  addByTitle(newInput);
            }
         }
         file.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   // hnadles exporting the list to a file
   public Boolean exportList(TableModel model, File file) {
      try {
         FileWriter output = new FileWriter(file);

         // put the column headers into the file first
         for (int i = 0; i < model.getColumnCount(); i++)
            output.write(model.getColumnName(i) + ",");

         output.write("\n");

         // iterate through the table, putting the data into the file while separating by
         // commas; this lets the user import the file to a spreadsheet editor
         for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
               if (j == model.getColumnCount())
                  output.write(model.getValueAt(i, j) + "");
               else
                  output.write(model.getValueAt(i, j) + ",");
            }
         }

         output.close();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return false;
   }

   public Game validateAndCreate(String[] input) {
      if (input[0].isEmpty())
         return null;

      if (input[1].isEmpty())
         return null;

      // assuming we have enough input parameters
      switch (input.length) {
         // if only required fields filled in
         case 2:
            return new Game(input[0], input[1]);

         // if one field is missing
         case 3:
            // if the rating is what's missing
            if (input[2].length() == 10) {
               String[] date = input[2].split("/");
               return new Game(input[0], input[1], parseDate(date[0] + "-" + date[1] + "-" + date[2]));
            } else if (validateRating(input[2])) {
               // if the date is what's missing
               return new Game(input[0], input[1], Double.parseDouble(input[2]));
            } else
               return null;

            // if all fields are filled in
         case 4:
            if (input[2].length() == 10 && validateRating(input[3])) {
               String[] date = input[2].split("/");
               return new Game(input[0], input[1], parseDate(date[0] + "/" + date[1] + "/" + date[2]),
                     Double.parseDouble(input[3]));
            } else
               return null;

         default:
            return null;
      }
   }

   // Used for the validation method
   private String parseDate(String date) {
      try {
         Date newDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
         SimpleDateFormat mdyFormat = new SimpleDateFormat("MM/dd/yyyy");
         String mdy = mdyFormat.format(newDate);
         return mdy;
      } catch (ParseException e) {
         return null;
      }
   }

   // Used for the validation method
   private Boolean validateRating(String rating) {
      try {
         double rate = Double.parseDouble(rating);
         if (rate > 10 || rate < 0)
            return false;
      } catch (NumberFormatException e) {
         return false;
      }
      return true;
   }

   // The two following methods are the insertion sort methods for the list. We
   // sort while inserting items into the list. We don't perform any other
   // operations during this time, so the runtime won't be too long even with a
   // sizable list.
   // This method sorts by the title field
   private void addByTitle(Game game) {
      if (gameList.size() == 0) {
         gameList.add(game);
      } else if (gameList.get(0).getTitle().compareToIgnoreCase(game.getTitle()) > 0) {
         gameList.add(0, game);
      } else if (gameList.get(gameList.size() - 1).getTitle().compareToIgnoreCase(game.getTitle()) < 0) {
         gameList.add(gameList.size(), game);
      } else {
         int spot = 0;
         while (gameList.get(spot).getTitle().compareToIgnoreCase(game.getTitle()) < 0) {
            spot++;
         }
         gameList.add(spot, game);
      }
   }

   // This method sorts by the platform field
   private void addByPlat(Game game) {
      if (gameList.size() == 0) {
         gameList.add(game);
      } else if (gameList.get(0).getPlatform().compareToIgnoreCase(game.getPlatform()) > 0) {
         gameList.add(0, game);
      } else if (gameList.get(gameList.size() - 1).getPlatform().compareToIgnoreCase(game.getPlatform()) < 0) {
         gameList.add(gameList.size(), game);
      } else {
         int spot = 0;
         while (gameList.get(spot).getPlatform().compareToIgnoreCase(game.getPlatform()) < 0)
            spot++;
         gameList.add(spot, game);
      }
   }

   public Game remove(int row) {
      return gameList.remove(row);
   }

   public LinkedList<Game> getGameList() {
      return gameList;
   }

   public void setGameList(LinkedList<Game> gameList) {
      this.gameList = gameList;
   }

   public void setSortMethod(String method) {
      sortMethod = method;
   }

   @Override
   public int getRowCount() {
      return gameList.size();
   }

   @Override
   public int getColumnCount() {
      return columnNames.length;
   }

   @Override
   public String getColumnName(int column) {
      return columnNames[column];
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      Game game = gameList.get(rowIndex);
      if (game == null) {
         return null;
      }
      switch (columnIndex) {
         case 0:
            return game.getTitle();
         case 1:
            return game.getPlatform();
         case 2:
            return game.getRelease();
         case 3:
            return game.getRating();
         default:
            return null;
      }
   }

   @Override
   public void setValueAt(Object obj, int row, int col) {
      switch (col) {
         case 1:
            gameList.get(row).setTitle((String) obj);
            break;
         case 2:
            gameList.get(row).setPlatform((String) obj);
            break;
         case 3:
            gameList.get(row).setRelease((String) obj);
            break;
         case 4:
            gameList.get(row).setRating((String) obj);
            break;
      }
      fireTableCellUpdated(row, col);
   }

   public void addRow(Game rowData) {
      if (rowData == null) {
         throw new IllegalArgumentException("rowData cannot be null");
      }
      if (sortMethod == "Plat")
         addByPlat(rowData);
      else
         addByTitle(rowData);
   }
}
