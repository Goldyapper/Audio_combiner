import java.io.*;
import java.util.*;

import com.mpatric.mp3agic.ID3v2;

public class AudioCombiner {

    public static void combineAudioFiles(String inputFolderPath, String outputFilePath) {
        File folder = new File(inputFolderPath);
        File[] audioFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        
        //check if there is actually files in the folder
        if (audioFiles == null || audioFiles.length == 0) { 
            System.out.println("No audio files found in: " + inputFolderPath);
            return;
        }

        Arrays.sort(audioFiles); // in alphabetical order
        // Get metadata from the first file
        ID3v2 metadata = MetadataHelper.getFirstFileMetadata(audioFiles[0]);


        try {
            // Create a temporary file listing all MP3s
            File listFile = new File("filelist.txt");
            try (PrintWriter writer = new PrintWriter(listFile)) {
                for (File mp3 : audioFiles) {
                    writer.println("file '" + mp3.getAbsolutePath().replace("\\", "/") + "'");
                }
            }
            File outFile = new File(outputFilePath);
            outFile.getParentFile().mkdirs();

            // Run ffmpeg command to concatenate
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-f", "concat", "-safe", "0",
                "-i", listFile.getAbsolutePath(),
                "-c", "copy", outputFilePath
            );
            pb.inheritIO(); // Show ffmpeg output
            Process process = pb.start();
            process.waitFor();

            listFile.delete();


            // Apply metadata to combined file
            if (metadata != null) {
                MetadataHelper.applyMetadata(outputFilePath, metadata);
            }

            System.out.println("Combined MP3 saved to: " + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        combineAudioFiles("input/E10 - Pimlico", "output/combined.mp3");
    }
}