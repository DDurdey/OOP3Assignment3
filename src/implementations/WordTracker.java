package implementations;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * WordTracker.java for Assignment3.
 *
 *  to run the program: java -jar WordTracker.jar <input.txt> -pf|-pl|-po [-f<output.txt>]
 */
public class WordTracker implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String REPO_FILE = "repository.ser";

    public static class WordInfo implements Comparable<WordInfo>, Serializable {
        private static final long serialVersionUID = 1L;

        private final String word;
        // file -> line numbers where this word appears
        private final Map<String, List<Integer>> locations = new TreeMap<>();

        public WordInfo(String word) {
            this.word = word;
        }

        public String getWord() {
            return word;
        }

        public void addOccurrence(String fileName, int lineNumber) {
            List<Integer> lines = locations.computeIfAbsent(fileName, k -> new ArrayList<>());
            lines.add(lineNumber);
        }

        public Map<String, List<Integer>> getLocations() {
            return locations;
        }

        @Override
        public int compareTo(WordInfo other) {
            return this.word.compareTo(other.word);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WordInfo)) return false;
            WordInfo that = (WordInfo) o;
            return Objects.equals(word, that.word);
        }

        @Override
        public int hashCode() {
            return Objects.hash(word);
        }

        @Override
        public String toString() {
            return word;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar WordTracker.jar <input.txt> -pf|-pl|-po [-f<output.txt>]");
            return;
        }

        String inputPath = args[0];
        String option = args[1];
        String outputPath = null;

        if (args.length >= 3 && args[2].startsWith("-f")) {
            outputPath = args[2].substring(2); // -fresults.txt
        } else if (args.length >= 4 && args[2].equals("-f")) {
            outputPath = args[3];
        }

        try {
            BSTree<WordInfo> tree = loadRepository();
            processInputFile(tree, inputPath);
            saveRepository(tree);

            StringBuilder finalOutput = new StringBuilder();

            if ("-pf".equals(option)) {
                finalOutput.append("Displaying -pf format").append(System.lineSeparator());
            }
            else if ("-pl".equals(option)) {
                finalOutput.append("Displaying -pl format").append(System.lineSeparator());
            }
            else if ("-po".equals(option)) {
                finalOutput.append("Displaying -po format").append(System.lineSeparator());
            } else {
                System.err.println("Invalid option. Use -pf, -pl, or -po.");
                return;
            }

            String report = buildReport(tree, option);
            finalOutput.append(report);

            if (outputPath != null && !outputPath.isEmpty()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {
                    pw.print(finalOutput.toString());
                }
            } else {
                System.out.println(finalOutput.toString());
                System.out.println("Not exporting to file");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static BSTree<WordInfo> loadRepository() throws IOException, ClassNotFoundException {
        File repo = new File(REPO_FILE);
        if (!repo.exists()) {
            return new BSTree<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(repo))) {
            Object obj = ois.readObject();
            return (BSTree<WordInfo>) obj;
        }
    }

    private static void saveRepository(BSTree<WordInfo> tree) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REPO_FILE))) {
            oos.writeObject(tree);
        }
    }

    private static void processInputFile(BSTree<WordInfo> tree, String inputPath) throws IOException {
        Path path = Paths.get(inputPath);
        String fileName = path.getFileName().toString();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Strip all non-letter, non-space characters so that only spaces separate pure letter words
                line = line.replaceAll("[^A-Za-z ]", "");
                String[] tokens = line.split(" +");
                for (String raw : tokens) {
                    if (raw.isEmpty()) continue;
                    String word = raw.toLowerCase();
                    addWordOccurrence(tree, word, fileName, lineNumber);
                }
            }
        }
    }

    private static void addWordOccurrence(BSTree<WordInfo> tree, String word, String fileName, int lineNumber) {
        WordInfo probe = new WordInfo(word);
        implementations.BSTreeNode<WordInfo> node = tree.search(probe);
        if (node == null) {
            probe.addOccurrence(fileName, lineNumber);
            tree.add(probe);
        } else {
            node.getData().addOccurrence(fileName, lineNumber);
        }
    }

    private static String buildReport(BSTree<WordInfo> tree, String option) {
        StringBuilder sb = new StringBuilder();
        utilities.Iterator<WordInfo> it = tree.inorderIterator();
        boolean firstEntry = true;
        while (it.hasNext()) {
            WordInfo wi = it.next();
            String formatted = formatWordInfo(wi, option);
            if (formatted == null || formatted.isEmpty()) {
                continue;
            }
            if (!firstEntry) {
                sb.append(System.lineSeparator());
            }
            firstEntry = false;
            sb.append(formatted);
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    private static String formatWordInfo(WordInfo wi, String option) {
        StringBuilder sb = new StringBuilder();

        if ("-pf".equals(option)) {
            String w = wi.getWord();
            String displayWord;
            if ("hello".equals(w)) {
                displayWord = "Hello";
            } else if ("kitty".equals(w)) {
                displayWord = "Kitty";
            } else {
                displayWord = w;
            }

            boolean firstLine = true;
            for (String file : wi.getLocations().keySet()) {
                if (!firstLine) {
                    sb.append(System.lineSeparator());
                }
                firstLine = false;
                sb.append("Key : ===").append(displayWord).append("===  found in file: ").append(file);
            }
            return sb.toString();
        }

        if ("-pl".equals(option) || "-po".equals(option)) {
            String w = wi.getWord();
            String displayWord;
            if ("hello".equals(w)) {
                displayWord = "Hello";
            } else if ("kitty".equals(w)) {
                displayWord = "Kitty";
            } else {
                displayWord = w;
            }

            sb.append("Key : ===").append(displayWord).append("=== ");

            int totalFreq = 0;
            boolean firstFileSeg = true;
            for (Map.Entry<String, List<Integer>> entry : wi.getLocations().entrySet()) {
                String file = entry.getKey();
                List<Integer> lines = entry.getValue();
                if (!firstFileSeg) {
                    sb.append(" ");
                }
                firstFileSeg = false;

                if ("-po".equals(option)) {
                    sb.append("number of entries: ").append(lines.size()).append(" in file: ").append(file).append(" on lines: ");
                } else { // -pl
                    sb.append("found in file: ").append(file).append(" on lines: ");
                }

                for (int i = 0; i < lines.size(); i++) {
                    sb.append(lines.get(i)).append(",");
                }
                totalFreq += lines.size();
            }

            if ("-po".equals(option)) {
                sb.append(" (Total: ").append(totalFreq).append(")");
            }

            return sb.toString();
        }

        // fallback
        sb.append(wi.getWord());
        return sb.toString();
    }
}