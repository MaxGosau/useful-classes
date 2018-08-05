package udssr.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import javafx.scene.image.Image;

/**
 * Images
 * 
 * This class loads images. This class is from the Java FX Tutorial.
 * 
 * @author JavaFX Tutorial, modified by Team iRace
 *
 */
public class Images {

	/**
	 * Saves the only instance of this class (Singleton Design Pattern)
	 */
	private static Images instance;

	/**
	 * Saves all images.
	 */
	private Map<String, Image> imgs = new HashMap<String, Image>();

	/**
	 * Initializes the instance of this class, if this was not done before. Returns
	 * the instance afterwards.
	 * 
	 * @return The only instance of this class.
	 */
	public static Images getInstance() {
		if (instance == null) {
			instance = new Images();
			instance.preload();
		}
		return instance;
	}

	/**
	 * Loads all images (.png files) from the images directory.
	 */
	public void preload() {
		// Logger.getInstance().log("Loading images... ", MsgType.INFO);
		ArrayList<String> pre = preload(this.getClass(), "images", ".png");
		for (String s : pre) {
			loadImage(s, s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf(".")));
		}
	}

	/**
	 * Loads a given image from a given path.
	 * 
	 * @param path
	 *            The path to the image.
	 * @param name
	 *            The name of the image.
	 */
	private void loadImage(String path, String name) {
		// Logger.getInstance().log("Loading " + name + " from " + path, MsgType.INFO);
		Image image = new Image(Images.class.getResource(path).toExternalForm());
		imgs.put(name, image);
	}

	/**
	 * Returns an image with a given key.
	 * 
	 * @param key
	 *            The key of the image.
	 * @return The image with the given key.
	 */
	public Image getImage(final String key) {
		if (imgs.get(key) == null) {
			loadImage("images/" + key + ".png", key);
		}
		return imgs.get(key);
	}

	/**
	 * Returns the path to a given image.
	 * 
	 * @param key
	 *            The name of the image which path we want to get.
	 * @return The path to the image.
	 */
	public String getImagePath(final String key) {
		return Images.class.getResource("images/" + key + ".png").toExternalForm();
	}

	/**
	 * Returns a list of the names of all files with a given file extension in a
	 * given directory.
	 * 
	 * @param in
	 *            The class file from which the directory is starting.
	 * @param dir
	 *            The directory.
	 * @param exts
	 *            The file extension.
	 * @return A list of all files which match the given information.
	 */
	private ArrayList<String> preload(Class in, String dir, String... exts) {
		URI uri = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			uri = in.getResource(dir).toURI();
			Path p;
			if (uri.getScheme().equals("jar")) {
				try {
					FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
				} catch (FileSystemAlreadyExistsException e) {
					// File system already exists, do nothing.
				}
			}
			p = Paths.get(uri);

			Stream<Path> walk = Files.walk(p, 1);
			Iterator<Path> it = walk.iterator();
			while (it.hasNext()) {
				Path path = it.next();
				for (String ext : exts) {
					if (path.toString().endsWith(ext)) {
						String name = path.getFileName().toString();
						result.add(dir + "/" + name);
					}
				}
			}
			walk.close();
		} catch (URISyntaxException e) {
			Logger.getInstance().log(e);
		} catch (IOException e) {
			Logger.getInstance().log(e);
		}
		return result;
	}
}
