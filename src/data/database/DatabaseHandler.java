package data.database;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Fathurrohman on 17-Dec-15.
 */
public class DatabaseHandler {

    private static final String DATABASE_FOLDER = "database";
    private static final String CODEBOOK_FOLDER = "codebook";
    private static final String WORD_MODEL_FOLDER = "wordmodel";

    private static final String EXTENSION_CODEBOOK = ".cdb";
    private static final String EXTENSION_WORD_MODEL = ".mdl";

    /**
     * Save codebook object to files
     *
     * @param name   name of codebook or word
     * @param object codebook object
     */
    public static void saveCodebook(String name, Codebook object) {
        try {
            String path = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + CODEBOOK_FOLDER);
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String pathFile = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + CODEBOOK_FOLDER + "\\" + name + EXTENSION_CODEBOOK);
            File codebookFile = new File(pathFile);

            //FileOutputStream fileOutputStream = new FileOutputStream(path + "\\" + name + EXTENSION_CODEBOOK);
            FileOutputStream fileOutputStream = new FileOutputStream(codebookFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(object);
            out.close();
            fileOutputStream.close();
            System.out.println(name + " has been saved in " + path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save Word Model object to files
     *
     * @param name   name of word model
     * @param object word model object
     */
    public static void saveWordModel(String name, WordModel object) {
        try {
            String path = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + WORD_MODEL_FOLDER);
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(path + "\\" + name + EXTENSION_WORD_MODEL);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(object);
            out.close();
            fileOutputStream.close();
            System.out.println(name + " has been saved in " + path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load single codebook from database/files
     *
     * @param name name of codebook or word
     * @return Single codebook object
     */
    public static Codebook loadCodeBook(String name) {
        Codebook output = null;
        try {
            String path = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + CODEBOOK_FOLDER);
            FileInputStream fileInputStream = new FileInputStream(path + name + EXTENSION_CODEBOOK);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            output = (Codebook) in.readObject();
            System.out.println(name + " has been loaded " + path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Load single word model from database/files
     *
     * @param name name of word model or word
     * @return Single Word Model Object
     */
    public static WordModel loadWordModel(String name) {
        WordModel output = null;
        try {
            String path = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + WORD_MODEL_FOLDER);
            FileInputStream fileInputStream = new FileInputStream(path + name + EXTENSION_WORD_MODEL);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            output = (WordModel) in.readObject();
            System.out.println(name + " has been loaded " + path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Load all word model in database
     *
     * @return Array of Wordmodel object
     */
    public static WordModel[] loadAllWordModel() {
        System.out.println("Load All Word Model");
        ArrayList<WordModel> output = new ArrayList<WordModel>();
        int counter = 0;
        try {
            String path = new File("").getAbsolutePath().concat("\\" + DATABASE_FOLDER + "\\" + WORD_MODEL_FOLDER);
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles
                    ) {
                if (file.isFile()) {
                    System.out.println(counter++ + "." + file.getName());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileInputStream);
                    output.add((WordModel) in.readObject());
                }
            }
            System.out.println("Load Completed with " + counter + "data");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (WordModel[]) output.toArray();
    }
}
