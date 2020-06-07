package ai.fritz.camera;

import android.util.Pair;

import ai.fritz.vision.poseestimation.Skeleton;

public class FakeSkeleton extends Skeleton {
    public static String[] PART_NAMES = new String[]{"leftShoulder",  "rightShoulder", "rightElbow" , "leftElbow"};
    public static Pair[] CONNECTED_PART_NAMES = new Pair[]{new Pair("leftShoulder", "leftElbow"), new Pair("rightShoulder", "rightElbow")};
    public static Pair[] POSE_CHAIN = new Pair[]{new Pair("leftShoulder", "leftElbow"), new Pair("rightShoulder", "rightElbow") };

    public FakeSkeleton() {
        super("FakeHuman", PART_NAMES, CONNECTED_PART_NAMES, POSE_CHAIN);
    }
}
