package triviyou.michal.com;

import android.content.Context;
import android.widget.Toast;

public  class Helper {

    public void toasting(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
