package triviyou.michal.com;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePasswordFragment extends Fragment {

   View view;
   Context context;
   Button bChangePassword;
   EditText etNewPassword, etRepeatNewPassword;
   Helper helper = new Helper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password, container, false); // קישור לXML

        context = getActivity();
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etRepeatNewPassword = view.findViewById(R.id.etRepeatNewPassword);
        bChangePassword = view.findViewById(R.id.bChangePassword);


        bChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isInternetAvailable(context)) {
                    helper.toasting(context, "אין חיבור לאינטרנט");
                    return;
                }
                String newPassword = etNewPassword.getText().toString();
                String repeatNewPassword = etRepeatNewPassword.getText().toString();

                if (!newPassword.equals(repeatNewPassword)) {
                    helper.toasting(context, getString(R.string.messageNotEqualPasswords));
                    return;
                }

                // בדיקת אם הסיסמה ריקה
                if (newPassword.equals("")) {
                    helper.toasting(context, getString(R.string.messageEmptyPassword));
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) { // updating password
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        helper.toasting(context, getString(R.string.passwordUpdatedSuccessfully));
                                    } else {
                                        helper.toasting(context, getString(R.string.shortPasswordChange));
                                    }
                                }
                            });
                }
            }
        });


        return view;
    }
}