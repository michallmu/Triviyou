package triviyou.michal.com;

// Singleton class to store global data accessible throughout the app
public class GlobalData {
    private static GlobalData instance; // single instance of GlobalData
    private boolean notificationAppeared; // flag to track notification status

    private GlobalData() { // private constructor to prevent direct instantiation
    }

    public static synchronized GlobalData getInstance() {
        // create instance if not exists
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }

    public static void setInstance(GlobalData instance) {
        // allow replacing the singleton instance if needed (rarely used)
        GlobalData.instance = instance;
    }

    public boolean isNotificationAppeared() {
        return notificationAppeared;
    }

    public void setNotificationAppeared(boolean notificationAppeared) {
        this.notificationAppeared = notificationAppeared;
    }
}
