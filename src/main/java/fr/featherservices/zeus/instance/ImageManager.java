package fr.featherservices.zeus.instance;

import java.io.File;

public class ImageManager {

    private static ImageManager instance;

    private File imagesFile;

    public ImageManager(File imagesFile) {
        instance = this;
    }

    public static ImageManager get() {
        return instance;
    }
}
