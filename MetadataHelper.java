import java.io.File;
import com.mpatric.mp3agic.*;
import java.io.FileOutputStream;

public class MetadataHelper {

    public static void main(String[] args) {
        File testFile = new File("input\\E10 - Pimlico\\1-01 Big Finish Ident.mp3");

        ID3v2 meta = getFirstFileMetadata(testFile);
        
        //test to see if it gets the metadata
        if (meta != null) {
            System.out.println("Title: " + meta.getTitle());
            System.out.println("Artist: " + meta.getArtist());
            System.out.println("Album: " + meta.getAlbum());
            System.out.println("Year: " + meta.getYear());
        
            // Extract cover art if present
            byte[] coverData = meta.getAlbumImage();
            if (coverData != null) {
                try (FileOutputStream fos = new FileOutputStream("cover.jpg")) {
                    fos.write(coverData);
                    System.out.println("Cover art extracted to cover.jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
            else {
                System.out.println("No cover art found.");
            }
            } 
        else {
            System.out.println("No metadata found.");
        }
    }

    public static ID3v2 getFirstFileMetadata(File mp3File) {
        try {
            Mp3File mp3 = new Mp3File(mp3File);
            if (mp3.hasId3v2Tag()) {
                return mp3.getId3v2Tag(); //if its v2 it can do it all on one line

            } else if (mp3.hasId3v1Tag()) { //otherwise w have to do this
                ID3v1 v1 = mp3.getId3v1Tag();
                ID3v2 v2 = new ID3v24Tag();
                v2.setArtist(v1.getArtist());
                v2.setAlbum(v1.getAlbum());
                v2.setTitle(v1.getTitle());
                v2.setYear(v1.getYear());
                return v2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
