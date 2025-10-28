import java.io.File;
import java.util.*;
import com.mpatric.mp3agic.*;

public class AudioFileFinder {
    
    public static List<File> getshortAudioFiles(String folderPath, int MaxLength){
        List<File> shortFiles = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()){  //checks if folder is real
            System.out.println("Invalid folder:" + folderPath);
            return shortFiles;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));//sorts out the mp3 files
        if (files == null){
            return shortFiles;
        }

        for (File file: files){
            try{
                Mp3File mp3 = new Mp3File(file);//get each file
                long seconds = mp3.getLengthInSeconds();
                double mins = seconds/60;

                if (mins < MaxLength){
                    shortFiles.add(file); //if the file is smaller than max length then it is added to the short file list
                }
            }
            catch (Exception e) {
                System.err.println("Error reading: " + file.getName());  
            }
        }
    return shortFiles;
    }

    public static void main(String[] args) {
        String folderPath = "input/E10 - Pimlico"; 
        List<File> shortAudioFiles = getshortAudioFiles(folderPath, 20); // 20 minutes 

        if (shortAudioFiles.isEmpty()) {
            System.out.println("No audio files shorter than 20 minutes were found.");
        } else {
            System.out.println("Audio files under 20 minutes:");
            for (File f : shortAudioFiles) {
                System.out.println(" - " + f.getName());
            }
        }
    }
}