package triviyou.michal.com.adapters;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import java.util.List;
import triviyou.michal.com.R;
import triviyou.michal.com.entities.Game;

public class GameAdapter extends ArrayAdapter<Game> {

    private Context context;
    private List<Game> games;

    public GameAdapter(Context context, List<Game> games) {
        super(context, R.layout.activity_game_item, games);
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_game_item, parent, false);
        }

        Game game = games.get(position);

        TextView txtGameName = convertView.findViewById(R.id.txtGameName);
        TextView txtGameDescription = convertView.findViewById(R.id.txtGameDescription);
        ImageView imgGame = convertView.findViewById(R.id.imgGame);


        txtGameDescription.setText(game.getDescription());

        if (game.isActive())
            txtGameName.setText(game.getName());
        else
            txtGameName.setText(String.format("%s - %s...", game.getName(), context.getString(R.string.soon)));

        Glide.with(context)
                .load(game.getImageUrl())
                .override(300, 300)
                .into(imgGame);

        return convertView;
    }
}