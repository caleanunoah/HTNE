package ai.fritz.fritzvisionvideo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ai.fritz.core.Fritz;
import ai.fritz.fritzvisionvideo.strategies.MaskCutStrategy;
import ai.fritz.fritzvisionvideo.strategies.ObjectPoseStrategy;
import ai.fritz.fritzvisionvideo.strategies.PoseDoubleMaskStrategy;
import ai.fritz.fritzvisionvideo.strategies.StylizeBackgroundStrategy;
import ai.fritz.fritzvisionvideo.strategies.StylizeHairStrategy;
import ai.fritz.fritzvisionvideo.ui.DemoAdapter;
import ai.fritz.fritzvisionvideo.ui.DemoItem;
import ai.fritz.fritzvisionvideo.ui.SeparatorDecoration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fritz.configure(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.demo_list_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager rvLinearLayoutMgr = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rvLinearLayoutMgr);

        // Add a divider
        SeparatorDecoration decoration = new SeparatorDecoration(this, Color.GRAY, 1);
        recyclerView.addItemDecoration(decoration);

        // Add the adapter
        DemoAdapter adapter = new DemoAdapter(getDemoItems());
        recyclerView.setAdapter(adapter);
        recyclerView.setClickable(true);
    }

    private List<DemoItem> getDemoItems() {
        List<DemoItem> demoItems = new ArrayList<>();

        demoItems.add(new DemoItem(
                getString(R.string.starry_night_hair_title),
                getString(R.string.starry_night_hair_description),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, VideoActivity.class);
                        // Send a VideoFilterStrategy as a Parcelable
                        intent.putExtra(getResources().getString(R.string.filter_strategy_key), new StylizeHairStrategy());
                        context.startActivity(intent);
                    }
                }));

        demoItems.add(new DemoItem(
                getString(R.string.stylize_background_title),
                getString(R.string.stylize_background_description),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, VideoActivity.class);
                        // Send a VideoFilterStrategy as a Parcelable
                        intent.putExtra(getResources().getString(R.string.filter_strategy_key), new StylizeBackgroundStrategy());
                        context.startActivity(intent);
                    }
                }));

        demoItems.add(new DemoItem(
                getString(R.string.object_pose_title),
                getString(R.string.object_pose_description),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, VideoActivity.class);
                        // Send a VideoFilterStrategy as a Parcelable
                        intent.putExtra(getResources().getString(R.string.filter_strategy_key), new ObjectPoseStrategy());
                        context.startActivity(intent);
                    }
                }));

        demoItems.add(new DemoItem(
                getString(R.string.pose_double_mask_title),
                getString(R.string.pose_double_mask_description),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, VideoActivity.class);
                        // Send a VideoFilterStrategy as a Parcelable
                        intent.putExtra(getResources().getString(R.string.filter_strategy_key), new PoseDoubleMaskStrategy());
                        context.startActivity(intent);
                    }
                }));

        demoItems.add(new DemoItem(
                getString(R.string.mask_cut_title),
                getString(R.string.mask_cut_description),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, VideoActivity.class);
                        // Send a VideoFilterStrategy as a Parcelable
                        intent.putExtra(getResources().getString(R.string.filter_strategy_key), new MaskCutStrategy());
                        context.startActivity(intent);
                    }
                }));

        return demoItems;
    }
}
