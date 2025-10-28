import java.io.*;
import java.util.*;
import com.mpatric.mp3agic.ID3v2;

public class AudioBatchCombiner {

    public static void main(String[] args) {
        String folderPath = "input/E10 - Pimlico";
        String outputFolder = "output";

        // Step 1: Get all short audio files
        List<File> shortAudioFiles = AudioFileFinder.getshortAudioFiles(folderPath, 20);

        if (shortAudioFiles.isEmpty()) {
            System.out.println("No short audio files found.");
            return;
        }

        // Step 2: Dynamically group by base name
        Map<String, List<File>> groupedFiles = groupByBaseName(shortAudioFiles);

        // Step 3: Combine each group
        for (Map.Entry<String, List<File>> entry : groupedFiles.entrySet()) {
            String groupName = entry.getKey();
            List<File> groupFiles = entry.getValue();

            groupFiles.sort(Comparator.comparing(File::getName));
            String outputFilePath = outputFolder + "/" + groupName + ".mp3";

            System.out.println("\nCombining group: " + groupName);
            combineGroup(groupFiles, outputFilePath);
        }
    }

    /**
     * Dynamically groups audio files based on the shared part of their name.
     * Example: "1-02 Pimlico Track 01.mp3" → group "pimlico"
     */
    private static Map<String, List<File>> groupByBaseName(List<File> files) {
        Map<String, List<File>> groups = new HashMap<>();

        for (File f : files) {
            String Name = f.getName();
            // Remove track numbers, punctuation, and extension
            String cleaned = Name
                .replaceAll("(?i)\\.mp3$", "")
                .replaceAll("\\d+,", "")           // remove numbers
                .replaceAll("Track", "")          // remove 'track'
                .replaceAll("[^a-zA-Z ]", " ")       // remove non-letters
                .replaceAll("\\s+", " ")          // normalize spaces
                .trim();

            // Use leftover words as the grouping key
            String groupKey = cleaned;

            // Add file to its group
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(f);
        }

        return groups;
    }

    /**
     * Combines all files in a group into one MP3 and applies metadata.
     */
    private static void combineGroup(List<File> files, String outputFilePath) {
        if (files.isEmpty()) return;

        try {
            ID3v2 metadata = MetadataHelper.getFirstFileMetadata(files.get(0));

            // Create temporary ffmpeg file list
            File listFile = new File("filelist.txt");
            try (PrintWriter writer = new PrintWriter(listFile)) {
                for (File mp3 : files) {
                    writer.println("file '" + mp3.getAbsolutePath().replace("\\", "/") + "'");
                }
            }

            // Run ffmpeg to combine
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-f", "concat", "-safe", "0",
                "-i", listFile.getAbsolutePath(),
                "-c", "copy", outputFilePath
            );
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();

            listFile.delete(); //deletes the temp file

            if (metadata != null) {
                MetadataHelper.applyMetadata(outputFilePath, metadata);
            }

            System.out.println("Combined: " + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
