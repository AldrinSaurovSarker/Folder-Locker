import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            Process process = Runtime.getRuntime().exec("runas /user:Administrator cmd.exe");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final String SOFTWARE_TITLE = "Folder Locker";
        final String ICON_FILE_NAME = "icon.png";
        final String LOCKED_ICON_FILE_NAME = "lock.ico";
        final String PASSWORD_FILE_NAME = "PasswordDirectory.txt";
        final String INSTALL_PATH = System.getProperty("InstallationPath");
        final String ICON_PATH = INSTALL_PATH != null ? INSTALL_PATH + "/" + ICON_FILE_NAME : ICON_FILE_NAME;
        final String LOCKED_ICON_PATH = INSTALL_PATH != null ? INSTALL_PATH + "/" + LOCKED_ICON_FILE_NAME : LOCKED_ICON_FILE_NAME;
        final Integer WINDOW_WIDTH = 400;
        final Integer WINDOW_HEIGHT = 180;
        final String FOLDER_PATH = args[0];
        final String COMMAND = args[1];
        // final String FOLDER_PATH = "C:\\Users\\ASUS\\OneDrive\\Desktop\\test";
        // final String COMMAND = "locker2";

        FolderLocker folderlocker = new FolderLocker(
                SOFTWARE_TITLE,
                WINDOW_WIDTH,
                WINDOW_HEIGHT,
                INSTALL_PATH,
                FOLDER_PATH,
                PASSWORD_FILE_NAME,
                LOCKED_ICON_PATH
        );

        folderlocker.fieldResponse();
        folderlocker.addComponents(folderlocker.isLocked(), COMMAND);
        folderlocker.locker();

        Tray tray = new Tray(SOFTWARE_TITLE, ICON_PATH, "Protect your folder from unwanted access");
        tray.showTray();
    }
}