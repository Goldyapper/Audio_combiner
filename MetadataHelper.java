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
    
    public static void applyMetadata(String filePath, ID3v2 metadata) {
        try {
            Mp3File mp3 = new Mp3File(filePath);
            ID3v2 tag = null;

            if (mp3.hasId3v2Tag()) {
                tag = mp3.getId3v2Tag();
            } else {
                tag = new ID3v24Tag();
                mp3.setId3v2Tag(tag);
            }

            tag.setTitle(metadata.getTitle());
            tag.setArtist(metadata.getArtist());
            tag.setAlbum(metadata.getAlbum());
            tag.setYear(metadata.getYear());

            byte[] cover = metadata.getAlbumImage();
            if (cover != null) {
                tag.setAlbumImage(cover, metadata.getAlbumImageMimeType());
            }

            String tempFile = filePath.replace(".mp3", "_temp.mp3");
            mp3.save(tempFile);

            // Replace original file with new file
            new File(filePath).delete();
            new File(tempFile).renameTo(new File(filePath));

            System.out.println("Metadata applied to combined file.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
