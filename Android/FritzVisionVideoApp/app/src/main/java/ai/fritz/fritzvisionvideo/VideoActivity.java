package ai.fritz.fritzvisionvideo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Locale;

import ai.fritz.fritzvisionvideo.strategies.VideoFilterStrategy;
import ai.fritz.vision.video.ExportVideoOptions;
import ai.fritz.vision.video.FritzVisionImageFilter;
import ai.fritz.vision.video.FritzVisionVideo;

public class VideoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    private VideoView videoView;
    private ProgressBar progressBar;
    private TextView progressText;
    private MenuItem exportButton;

    private VideoFilterStrategy strategy;

    private File exportFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = findViewById(R.id.video_view);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(1, 1);
                mediaPlayer.start();
            }
        });
        progressBar = findViewById(R.id.export_progress);
        progressText = findViewById(R.id.progress_text);

        // Retrieve the VideoFilterStrategy
        strategy = getIntent().getParcelableExtra(getResources().getString(R.string.filter_strategy_key));

        // Display the video picker
        Intent filePicker = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        filePicker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(filePicker, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Show a top menu bar with an export button
        getMenuInflater().inflate(R.menu.video_menu, menu);
        exportButton = menu.findItem(R.id.export_button);
        exportButton.setVisible(false);
        exportButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                try {
                    saveProcessedVideo();
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to save video.");
                }
                menuItem.setEnabled(false);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        File cacheDir = getCacheDir();
        File[] cachedFiles = cacheDir.listFiles();

        // Delete any cached files
        if (cachedFiles != null) {
            for (File file : cachedFiles) {
                file.delete();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Process the video after a video is selected
            startProcessing(data.getData());
        } else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starts processing a video.
     *
     * @param videoUri The URI of the video to process.
     */
    private void startProcessing(Uri videoUri) {
        try {
            // Create a temporary file to write the processed video to
            exportFile = File.createTempFile("tempExport", ".mp4", getCacheDir());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create a destination file.");
        }

        // Get filters to apply on the video
        FritzVisionImageFilter[] filters = strategy.getFilters();

        // Create the FritzVisionVideo object with the filter and selected video URI
        FritzVisionVideo fritzVideo = new FritzVisionVideo(videoUri, filters);

        // Create and set the processing parameters
        // Every third frame of the video will be processed
        ExportVideoOptions options = new ExportVideoOptions();
        options.frameInterval = 3;

        // Uncomment following line to enable audio at the cost of increased processing time
        // options.copyAudio = true;

        final String exportPath = exportFile.getAbsolutePath();

        // Start exporting and processing the video
        fritzVideo.export(exportPath, options, new FritzVisionVideo.ExportProgressCallback() {
            @Override
            public void onProgress(Float response) {
                // Update progress
                int progress = (int) (response * 100);
                updateProgress(progress);
            }

            @Override
            public void onComplete() {
                // Play the processed video
                Uri uri = Uri.fromFile(exportFile);
                displayVideo(uri);
            }
        });
    }

    /**
     * Updates progress when processing.
     */
    private void updateProgress(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                progressText.setText(String.format(Locale.US, "Processing %d%%", progress));
            }
        });
    }

    /**
     * Changes visibility of views when processing is complete.
     */
    private void displayVideo(final Uri videoUri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                progressText.setVisibility(View.INVISIBLE);
                exportButton.setVisible(true);
                videoView.setVideoURI(videoUri);
            }
        });
    }

    /**
     * Write the contents of the processed video to Photos.
     *
     * @throws IOException If there is a file error.
     */
    private void saveProcessedVideo() throws IOException {
        // Get the directory to save to
        File file = getExternalFilesDir(Environment.DIRECTORY_MOVIES);

        // Create the file to write to
        String exportPath = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4");
        File destFile = new File(exportPath);

        // Write contents of the processed video file to the destination file
        try (FileChannel source = new FileInputStream(exportFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }

        // Notify the user that the video has been saved
        Toast.makeText(
                VideoActivity.this,
                "Saved video to " + exportPath, Toast.LENGTH_LONG
        ).show();
    }
}