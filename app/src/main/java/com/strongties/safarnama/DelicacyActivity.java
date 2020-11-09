package com.strongties.safarnama;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.strongties.safarnama.user_classes.RV_Delicacy;

import java.util.Objects;

public class DelicacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delicacy);

        ImageView back_button = findViewById(R.id.act_delicacy_back);
        ImageView img_header = findViewById(R.id.act_delicacy_header_img);

        TextView name = findViewById(R.id.act_delicacy_name);
        TextView pronunciation = findViewById(R.id.act_delicacy_pronunciation);
        TextView famous = findViewById(R.id.act_delicacy_famous_in);
        TextView foodtype = findViewById(R.id.act_delicacy_foodtype);
        TextView price = findViewById(R.id.act_delicacy_price);
        TextView description = findViewById(R.id.act_delicacy_description);
        TextView preparation = findViewById(R.id.act_delicacy_preparation);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.act_delicacy_youtube_player);

        Glide.with(this)
                .load(R.drawable.ic_back)
                .placeholder(R.drawable.loading_image)
                .centerCrop()
                .into(back_button);

        back_button.setOnClickListener(view -> {
            onBackPressed();
        });


        String delicacy_id = Objects.requireNonNull(getIntent().getExtras()).getString("id");
        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(getString(R.string.collection_dalicacies))
                .document(getString(R.string.document_meta))
                .collection(getString(R.string.collection_all))
                .document(delicacy_id);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    RV_Delicacy delicacy = task.getResult().toObject(RV_Delicacy.class);

                    Glide.with(this)
                            .load(delicacy.getImg_desciption())
                            .placeholder(R.drawable.loading_image)
                            .centerCrop()
                            .into(img_header);
                    name.setText(delicacy.getName());
                    pronunciation.setText(delicacy.getPronunciation());
                    famous.setText(delicacy.getFamous_in().replace("\"", ""));
                    foodtype.setText(delicacy.getFoodtype());
                    price.setText(delicacy.getPrice());
                    description.setText(delicacy.getDescription().replace("\"", ""));

                    String[] prep_tokens = delicacy.getPreparation().split(":");
                    StringBuilder htmlString = new StringBuilder();
                    int count = 0;
                    for (String token : prep_tokens) {
                        count++;
                        token = token.replace("\"\"", "\"");
                        if (count == 1) {
                            if (token.charAt(0) == '"') {
                                token = token.substring(1);
                            }
                        }
                        if (count < prep_tokens.length) {
                            htmlString.append("&#8226;  ").append(token).append("<br/> <br/>");
                        } else {
                            if (token.charAt(token.length() - 1) == '"') {
                                token = token.substring(0, token.length() - 1);
                            } else if (token.charAt(token.length() - 2) == '"') {
                                token = token.substring(0, token.length() - 2);
                            }
                            htmlString.append("&#8226;  ").append(token).append("<br/>");
                        }

                    }
                    preparation.setText(Html.fromHtml(htmlString.toString()));


                    //preparation.setText(delicacy.getPreparation());

                    getLifecycle().addObserver(youTubePlayerView);

                    youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                            String[] list = delicacy.getVideo_link().split("=");
                            String videoId = list[1];
                            youTubePlayer.cueVideo(videoId, 0f); // Loads video but does not play
                            // youTubePlayer.loadVideo(videoId, 0f); // Loads and plays video
                        }
                    });


                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }

}