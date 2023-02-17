import java.text.SimpleDateFormat;
import java.util.Date;

/*
   This class acts as a list node for the GameList class. It has 4 values to represent the data stored in the node.
   The constructor's include any variation as long as the "title", and "platform" fields are not empty. Validation of the data is left to the list before creation.
*/
public class Game {

   private String title;
   private String platform;
   private String release;
   private double rating;

   public Game(String t, String pl) {
      title = t;
      platform = pl;
   }

   public Game(String t, String pl, String re) {
      title = t;
      platform = pl;
      release = re;
   }

   public Game(String t, String pl, double ra) {
      title = t;
      platform = pl;
      rating = ra;
   }

   public Game(String t, String pl, String re, double ra) {
      title = t;
      platform = pl;
      release = re;
      rating = ra;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      if (!title.isEmpty())
         this.title = title;
   }

   public String getPlatform() {
      return this.platform;
   }

   public void setPlatform(String platform) {
      if (!platform.isEmpty())
         this.platform = platform;
   }

   public String getRelease() {
      return this.release;
   }

   public void setRelease(String release) {
      try {
         String[] date = release.split("/");
         Date newDate = new SimpleDateFormat("MM-dd-yyyy").parse(date[0] + "-" + date[1] + "-" + date[2]);
         SimpleDateFormat mdyFormat = new SimpleDateFormat("MM-dd-yyyy");
         this.release = mdyFormat.format(newDate);
      } catch (Exception e) {
         // Nothing is changed if there's an invalid input
         return;
      }
   }

   public double getRating() {
      return this.rating;
   }

   public void setRating(String rating) {
      try {
      double rate = Double.parseDouble(rating);
      if (rate <= 10 && rate >= 0)
         this.rating = Math.floor(rate * 100) / 100;
      } catch (Exception e) {
         // Nothing is changed if there's an invalid input
         return;
      }
   }
}
