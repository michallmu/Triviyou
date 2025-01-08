package triviyou.michal.com.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_game_item, parent, false);
        }

        Game game = games.get(position);

        TextView txtGameTitle = convertView.findViewById(R.id.txtGameTitle);
        TextView txtGameDescription = convertView.findViewById(R.id.txtGameDescription);
        ImageView imgGame = convertView.findViewById(R.id.imgGame);

        txtGameTitle.setText(game.getName());
        txtGameDescription.setText(game.getDescription());
        imgGame.setImageURI(Uri.parse(game.getImageUrl()));
        // Load the image using Glide (fetch from Firebase URL)
        Glide.with(context)
                .load(game.getImageUrl())
                //.placeholder(R.drawable.placeholder) // Optional placeholder image
                .into(imgGame);

        // Set clickable or non-clickable based on logic
        convertView.setEnabled(game.isActive());
        convertView.setAlpha(game.isActive() ? 1.0f : 0.5f);

        return convertView;
    }
}
