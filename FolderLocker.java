import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Scanner;

public class FolderLocker {
    static String INSTALLATION_PATH;
    static String FOLDER_PATH;
    static String FILE_NAME;
    static String KEY_PATH;
    private final folderHidingHandler handler;

    JFrame frame = new JFrame();
    JLabel labelNewPassword = new JLabel("New Password      ");
    JLabel labelConfirmPassword = new JLabel("Confirm Password");
    JLabel labelEnterPassword = new JLabel("Enter Password  ");

    JLabel responseMessage = new JLabel();
    JLabel dirName = new JLabel();

    JPasswordField passFieldNew = new JPasswordField(20);
    JPasswordField passFieldConfirm = new JPasswordField(20);
    JPasswordField passFieldEnter = new JPasswordField(20);

    JButton lockButton = new JButton("Lock");
    JButton unlockButton = new JButton("Unlock");

    public FolderLocker(
            String title,
            int width,
            int height,
            String installPath,
            String folderDirectory,
            String passwordFileName,
            String keyPath) {
        INSTALLATION_PATH = installPath;
        FOLDER_PATH = folderDirectory;
        FILE_NAME = passwordFileName;
        KEY_PATH = keyPath;
        this.handler = new folderHidingHandler(folderDirectory, keyPath);
        frame.setTitle(title);
        frame.setLayout(new GridLayout(5, 1));
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes(), 0, password.length());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    public static void storePassword(String password) {
        try {
            File file = new File(INSTALLATION_PATH, FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            boolean directoryFound = false;
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("@");
                if (data[0].equals(FOLDER_PATH)) {
                    directoryFound = true;
                    line = FOLDER_PATH + "@" + encrypt(password);
                }
                sb.append(line);

                if (line.length() > 0)
                    sb.append("\n");
            }

            if (!directoryFound) {
                line = FOLDER_PATH + "@" + encrypt(password);
                sb.append(line).append("\n");
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(sb.toString());
            bw.close();
        } catch (IOException e) {
            System.err.println("An error occurred while storing password: " + e.getMessage());
        }
    }

    public static void removePassword() throws IOException {
        File file = new File(INSTALLATION_PATH, FILE_NAME);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            String[] data = line.split("@");
            if (data[0].equals(FOLDER_PATH)) {
                System.out.println("OK");
                line = "";
                break;
            }
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }

        bufferedReader.close();
        FileWriter fileWriter = new FileWriter(FILE_NAME);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(stringBuffer.toString());
        bufferedWriter.close();

        System.out.println("Password Removed!");
        System.exit(0);
    }

    public static String extractPassword() {
        try {
            File file = new File(INSTALLATION_PATH, FILE_NAME);
            try (Scanner Reader = new Scanner(file)) {
                while (Reader.hasNextLine()) {
                    String[] data = Reader.nextLine().split("@");

                    if (FOLDER_PATH.equals(data[0])) {
                        return data[1];
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean isLocked() {
        try {
            File file = new File(INSTALLATION_PATH, FILE_NAME);

            if (!file.exists()) {
                file.createNewFile();
            }
            try (Scanner Reader = new Scanner(file)) {
                while (Reader.hasNextLine()) {
                    String[] data = Reader.nextLine().split("@");

                    if (FOLDER_PATH.equals(data[0])) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void addComponents(boolean locked) {
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row1.add(new JLabel());
        row1.add(dirName);
        row1.add(new JLabel());
        frame.add(row1);
        dirName.setText(FOLDER_PATH);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (!locked) {
            row2.add(labelNewPassword);
            row2.add(passFieldNew);
            frame.add(row2);

            JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row3.add(labelConfirmPassword);
            row3.add(passFieldConfirm);
            frame.add(row3);

            JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            row4.add(lockButton);
            frame.add(row4);
        }

        else {
            row2.add(labelEnterPassword);
            row2.add(passFieldEnter);
            frame.add(row2);

            JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            row3.add(new JLabel());
            frame.add(row3);

            JPanel row4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            row4.add(unlockButton);
            frame.add(row4);
        }

        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row5.add(new JLabel());
        row5.add(responseMessage);
        row5.add(new JLabel());
        frame.add(row5);

        frame.setVisible(true);
    }

    public void fieldResponse() {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                colorBorder();
            }

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                colorBorder();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                colorBorder();
            }

            private void colorBorder() {
                responseMessage.setText(null);
                passFieldNew.setBorder(new JTextField().getBorder());
                passFieldConfirm.setBorder(new JTextField().getBorder());
                passFieldEnter.setBorder(new JTextField().getBorder());
            }
        };

        passFieldNew.getDocument().addDocumentListener(documentListener);
        passFieldConfirm.getDocument().addDocumentListener(documentListener);
        passFieldEnter.getDocument().addDocumentListener(documentListener);
    }

    private static void openFolder(String folderPath) {
        try {
            Process p = Runtime.getRuntime().exec(new String[] {"explorer.exe", "/root,", folderPath});
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void locker(boolean locked) {
        this.fieldResponse();
        this.addComponents(locked);

        lockButton.addActionListener((e) -> {
            String val1 = String.valueOf(passFieldNew.getPassword());
            String val2 = String.valueOf(passFieldConfirm.getPassword());

            if (val1.length() == 0 && val2.length() == 0) {
                responseMessage.setText("All Fields Must Be Filled");
                passFieldNew.setBorder(BorderFactory.createLineBorder(Color.RED));
                passFieldConfirm.setBorder(BorderFactory.createLineBorder(Color.RED));
            } else if (val1.length() == 0) {
                responseMessage.setText("All Fields Must Be Filled");
                passFieldNew.setBorder(BorderFactory.createLineBorder(Color.RED));
            } else if (val2.length() == 0) {
                responseMessage.setText("All Fields Must Be Filled");
                passFieldConfirm.setBorder(BorderFactory.createLineBorder(Color.RED));
            } else if (val1.equals(val2)) {
                FolderLocker.storePassword(val1);
                responseMessage.setText("Password Stored Successfully");
                SecretKey secretKey = handler.generateKey();
                handler.encrypt(secretKey);;
                System.exit(0);
            } else {
                responseMessage.setText("Password Doesn't Match");
            }
        });

        unlockButton.addActionListener((e) -> {
            if (Objects.equals(FolderLocker.extractPassword(), encrypt(String.valueOf(passFieldEnter.getPassword())))) {
                responseMessage.setText("Password Matched");
                SecretKey secretKey = handler.extractKey();
                handler.decrypt(secretKey);
                openFolder(FOLDER_PATH);
                System.exit(0);
            } else {
                responseMessage.setText("Password Doesn't Match");
            }
        });
    }
}
