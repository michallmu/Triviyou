package triviyou.michal.com;

public class GlobalData {
    private static GlobalData instance;
    private boolean notificationAppeared; // Replace with your variable type

    private GlobalData() {
    }

    public static synchronized GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }

    public static void setInstance(GlobalData instance) {
        GlobalData.instance = instance;
    }

    public boolean isNotificationAppeared() {
        return notificationAppeared;
    }

    public void setNotificationAppeared(boolean notificationAppeared) {
        this.notificationAppeared = notificationAppeared;
    }
}
