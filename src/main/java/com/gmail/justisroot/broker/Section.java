// Created by Justis Root. Released into the public domain.
// https://gist.github.com/justisr
//
// Source is licensed for any use, provided that this copyright notice is retained.
// Modifications not expressly accepted by the author should be noted in the license of any forks.
// No warranty for any purpose whatsoever is implied or expressed,
// and the author shall not be held liable for any losses, direct or indirect as a result of using this software.
package com.gmail.justisroot.broker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

class Section {

	private final Section parent;
	private int tab;
	private String key, value;
	private List<String> header;
	private final Map<String, Section> children = new LinkedHashMap<>();
	private List<String> footer;

	private Section(File file) {
		this(-1, null, file.getName().substring(0, file.getName().indexOf(".") > 0 ? file.getName().indexOf(".") : 0), "", new ArrayList<>());
	}

	private Section(int tab, Section parent, String key, String value, List<String> header) {
		this.tab = tab;
		this.parent = parent;
		this.key = key;
		this.value = value;
		this.header = header;
	}

	public void reload(Scanner input) {
		children.clear();
		List<String> header = new ArrayList<>();
		Section current = this;
		while (input.hasNextLine()) {
			Section returned = current.nextLine(input.nextLine(), header);
			if (current != returned) header = new ArrayList<>();
			current = returned;
		}
		if (this.children.isEmpty()) {
			this.header = header;
			this.footer = new ArrayList<>();
		} else footer = header;
		input.close();
	}

	private Section nextLine(String line, List<String> header) {
		if (isCommented(line)) {
			header.add(line);
			return this;
		} else {
			int keyIndex = 0;
			for (; keyIndex < line.length(); keyIndex++) {
				if (!Character.isWhitespace(line.charAt(keyIndex))) break;
			}
			String value = line.replaceFirst(".*?" + VALUE_SEPARATOR + "\\s?", "");
			String key;
			if (value.length() == line.length()) {
				value = line.substring(keyIndex);
				key = value;
			} else key = line.substring(keyIndex, line.length() - value.length() - 1);
			if (key.length() > 1 && key.charAt(key.length() - 1) == VALUE_SEPARATOR) key = key.substring(0, key.length() - 1);
			if (keyIndex > tab) return addChild(keyIndex, key, value, header);
			else return nearestParent(keyIndex).addChild(keyIndex, key, value, header);
		}
	}

	/**
	 * Get the value associated with this section
	 *
	 * @return value string associated with the section
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of this section
	 *
	 * @param value to set this section's value to
	 */
	public void setValue(Object value) {
		value = value.toString();
	}

	/**
	 * Get the key associated with this section
	 *
	 * @return key string associated with the section
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Get the number of tabs this section is from root
	 *
	 * @return tab count from root
	 */
	public int getTab() {
		return tab;
	}

	/**
	 * Adjust the tab position of the section and children sections by the selected amount.<br>
	 * Note that this will likely change the section's and effected children's parents.<br>
	 * Accepts positive and negative values.
	 *
	 * @throws IllegalArgumentException if tab would be adjusted below root.
	 * @param amt to adjust the tab by.
	 */
	public void adjustTab(int amt) {
		if (tab - amt < 0) throw new IllegalArgumentException("Tab cannot be adjusted below root.");
	}

	/**
	 * Set the tab position of the section and adjust children sections.<br>
	 * Note that this will likely change the section's and effected children's parents.<br>
	 * Accepts positive and negative values.
	 *
	 * @throws IllegalArgumentException if tab would be adjusted below root.
	 * @param amt of tabs to set the section to.
	 */
	public void setTab(int amt) {
		adjustTab(amt - tab);
	}

	/**
	 * Get the parent section of this section
	 *
	 * @return section parenting this section. Null if no parent exists.
	 */
	public Section getParent() {
		return parent;
	}

	/**
	 * Get the children of this section
	 *
	 * @return children
	 */
	public Map<String, Section> getChildren() {
		return children;
	}

	/**
	 * Add a child section of the current section.
	 *
	 * @param keyIndex The index of the first key character. Also the number of tab characters
	 * @param parent section of this new child
	 * @param key of the child section
	 * @param value of the child section
	 * @param comments for the child section
	 * @return a new child section of the current section with the applied settings.
	 */
	private Section addChild(int keyIndex, String key, String value, List<String> comments) {
		return addChild(new Section(keyIndex, this, key, value, comments));
	}

	/**
	 * Add a child section of the current section.
	 *
	 * @param child to add to the section
	 * @return section child that was added.
	 */
	private Section addChild(Section child) {
		children.put(child.key, child);
		return child;
	}

	/**
	 * Get the nearest possible parent of the line with the specific tab intend
	 *
	 * @param indent of line whose parent is being looked for
	 * @return the nearest possible parent section of the line with that indent
	 */
	private Section nearestParent(int indent) {
		return tab < indent ? this : parent.nearestParent(indent);
	}

	/**
	 * Replace the name of the specified section with a new one<br>
	 * <b>This will also move the section down to the bottom of whatever path branch it's on</b><br>
	 * Creates a new section with the specified name along the path if the section does not exist<br>
	 *
	 * @param key to rename the section to<br>
	 */
	private void setKey(String key) {
		parent.children.remove(this.key);
		parent.children.put(key, this);
		this.key = key;
	}

	/**
	 * Replace the name of the specified section with a new one<br>
	 * <b>This will also move the section down to the bottom of whatever path branch it's on</b><br>
	 * Creates a new section with the specified name along the path if the section does not exist
	 *
	 * @param path to section rename
	 * @param key to rename the section to
	 */
	private void rename(List<String> path, String key) {
		if (path.size() < 2) {
			if (!children.containsKey(path.get(0))) addChild(tab + 1, key, "", new ArrayList<>());
			else children.get(path.get(0)).setKey(key);
		} else {
			Section child = children.get(path.get(0));
			path.remove(0);
			if (child != null) child.rename(path, key);
			else addChild(tab + 1, key, "", new ArrayList<>()).rename(path, key);
		}
	}

	/**
	 * Remove a section and all inner sections
	 *
	 * @param path to the section to remove
	 */
	private void remove(List<String> path) {
		if (path.size() < 2) children.remove(path.get(0));
		Section child = children.get(path.get(0));
		if (child == null) return;
		path.remove(0);
		child.remove(path);
	}

	/**
	 * Set the comments at the specified path
	 *
	 * @param path to set the comments for
	 * @param value to set as the comments
	 */
	private void setComments(List<String> path, List<String> value) {
		Section sec = hardGet(path, "");
		sec.header = value;
	}

	/**
	 * Strip comments from this section and all of its child sections.
	 *
	 * @return The current {@link Section}
	 */
	public Section stripComments() {
		this.header.clear();
		for (Section child : children.values())
			child.stripComments();
		return this;
	}

	/**
	 * Convert an object into a string value, and set that value at the desired path<br>
	 * Converts arrays into a List-like string value. Otherwise uses #toString() on the object
	 *
	 * @param path to set the value at, [path, to.value]
	 * @param value to set
	 */
	private void setValue(List<String> path, Object value) {
		StringBuilder builder = new StringBuilder(value.toString());
		if (value instanceof Object[]) {
			builder.setLength(0);
			builder.append('[');
			Object[] o = (Object[]) value;
			for (int i = 0; i < o.length; i++)
				builder.append(o[i] + (i == o.length - 1 ? "]" : ", "));
		}
		hardGet(path, value).value = builder.toString();
	}

	/**
	 * Get the section at the specified path, or create one if it does not exist
	 *
	 * @param path to get the section from or create a section at
	 * @param value to set as the path's value if the path does not exist
	 * @return section at the specified path
	 */
	private Section hardGet(List<String> path, Object value) {
		String key = path.get(0);
		if (path.size() < 2) {
			if (children.containsKey(key)) return children.get(key);
			else return addChild(tab + 1, key, value.toString(), new ArrayList<>());
		} else {
			path.remove(0);
			if (children.containsKey(key)) return children.get(key).hardGet(path, value);
			else return addChild(tab + 1, key, value.toString(), new ArrayList<>()).hardGet(path, value);
		}
	}

	/**
	 * Get the section at the specified path
	 *
	 * @param string list path to the section
	 * @return Section at the path, null if section does not exist
	 */
	private Section get(List<String> path) {
		if (path.size() < 2) return children.get(path.get(0));
		Section child = children.get(path.get(0));
		if (child == null) return null;
		path.remove(0);
		return child.get(path);
	}

	/**
	 * Get the trailing footer at the end of the file
	 *
	 * @return footer
	 */
	public List<String> getFooter() {
		return footer;
	}

	/**
	 * Get the sections' path/value and comments, as well as all of the paths/values and comments of nested sections
	 *
	 * @param contents list to append the contents to
	 * @param indent for the section's line
	 * @return List of the section comments, the path/value as well as all nested sections
	 */
	protected List<String> getContents(List<String> contents, int indent) {
		for (String head : header)
			contents.add(head);
		if (tab > -1) contents.add(new String(new char[indent++]).replace("\0", TAB) + key + VALUE_SEPARATOR + " " + value);
		for (Section sec : children.values())
			sec.getContents(contents, indent);
		return contents;
	}

	public HMFFWrapper getWrapper() {
		Section sec = this;
		while (sec != null && !(sec instanceof HMFFWrapper))
			sec = sec.parent;
		return (HMFFWrapper) sec;
	}

	/**
	 * Hierarchical Mapping File Format Wrapper
	 *
	 * @author Justis R
	 * @version 3.0.1
	 */
	static final class HMFFWrapper extends Section {

		private File file;

		/**
		 * Create a HMFFWrapper wrapper for the file at the specified path with the specified name within the program's running folder.<br>
		 *
		 * @param path from the program's running folder to the destination.<br>
		 *            Each parameter prior to the last being a folder.
		 */
		public static HMFFWrapper fromPath(String... path) {
			StringBuilder pathBuilder = new StringBuilder();
			for (int i = 0; i < path.length; i++)
				pathBuilder.append(path[i] + File.separator);
			return new HMFFWrapper(new File(pathBuilder.toString()));
		}

		/**
		 * Create a HMFFWrapper object for specified file.<br>
		 *
		 * @param file for which to wrap the HMFFWrapper object.
		 */
		public HMFFWrapper(File file) {
			super(file);
			this.file = file;
			reload();
		}

		/**
		 * Get the raw File object associated with this file wrapper instance.<br>
		 *
		 * @return File wrapped by this instance.
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Set the raw File object associated with this file wrapper instance.<br>
		 *
		 * @param file to wrap
		 */
		public void setFile(File file) {
			this.file = file;
		}

		/**
		 * Gets the file from disk or generates one if it doesn't exist.<br>
		 * Save all of the file's contents, all the paths, values, everything, to memory.<br>
		 * Overwrites any and all existing contents of memory<br>
		 *
		 * @throws IOException when the directory does not exist.
		 */
		public void reload() {
			try {
				File direc = new File(file.getPath().replace(file.getName(), ""));
				if (!direc.exists()) direc.mkdirs();
				if (!file.exists()) file.createNewFile();
				super.reload(new Scanner(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Overwrites data in memory with the provided source.
		 *
		 * @param source String to use as the wrapper's HMFF data.
		 */
		public void reload(String source) {
			super.reload(new Scanner(source));
		}

		/**
		 * Save all the current contents of memory to disk.<br>
		 * Overwrites any and all existing contents of the file<br>
		 *
		 * @throws IOException when the directory does not exist.
		 */
		public void save() {
			save(getContent());
		}

		/**
		 * Save all of the provided content to disk.<br>
		 * Overwrites any and all existing content of the file<br>
		 *
		 * @throws IOException when the directory does not exist.
		 */
		public void save(String content) {
			try (FileWriter fw = new FileWriter(file, false)) {
				fw.write(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Get the contents of the entire file
		 *
		 * @return List of every line, in order
		 */
		public List<String> getContents() {
			List<String> contents = getContents(new ArrayList<>((getChildren().size() + getFooter().size()) * 3 / 2 + 1), 0);
			contents.addAll(getFooter());
			return contents;
		}

		/**
		 * Get the content of the entire file
		 *
		 * @return String representation of the file
		 */
		public String getContent() {
			return getContents().stream().collect(Collectors.joining("\n"));
		}

		/**
		 * Get the content of the entire file<br>
		 * Effectively {@link #getContent()}
		 *
		 * @return String representation fo the file
		 */
		@Override
		public String toString() {
			return getContent();
		}

		/**
		 * Get defaults from an InputStream and copy it into the file if the file is empty, doesn't exist, or if overwrite is true
		 *
		 * @param InputStream to copy from
		 * @param overwrite contents if they exist within the file
		 */
		public HMFFWrapper copyDefaults(InputStream is, boolean overwrite) {
			if (!overwrite && file.exists() && !isEmpty(file)) return this;
			if (is == null) System.out.println("[Warning] " + file.getName() + "'s .jar file has been modified! Please restart!");
			else try {
				Files.copy(is, file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reload();
			return this;
		}

		/**
		 * @return true if the file is empty of characters, otherwise false
		 * @throws FileNotFoundException If the file is not found
		 */
		public static boolean isEmpty(File file) {
			Scanner input;
			try {
				input = new Scanner(file);
				if (input.hasNextLine()) {
					input.close();
					return false;
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}

		/**
		 * @param folderLoc The path from the main program folder to the destination folder
		 * @return Set of all files contained in the destination folder
		 */
		public static Set<HMFFWrapper> getFolderContents(String... path) {
			Set<HMFFWrapper> files = new HashSet<>();
			StringBuilder pathBuilder = new StringBuilder();
			for (int i = 0; i < path.length; i++)
				pathBuilder.append(path[i] + File.separator);
			File direc = new File(pathBuilder.toString());
			if (!direc.exists()) direc.mkdirs();
			if (direc.isDirectory()) for (File f : direc.listFiles())
				files.add(new HMFFWrapper(f));
			return files;
		}
	}

	/**
	 * The whitespace to be used to represent a tab when generating the file from memory
	 */
	public static final String TAB = "  ";

	public static final char PATH_CHAR = '.', VALUE_SEPARATOR = ':', COMMENT_INDICATOR = '#';

	/**
	 * Replace the name of the specified section with a new one <br>
	 * This will also move the section down to the bottom of whatever path branch it's on<br>
	 * Creates a new section with the specified name along the path if the section does not exist
	 *
	 * @param path to section rename
	 * @param name to rename the section to.
	 */
	public void renameSection(String path, String name) {
		rename(splitPath(path), name);
	}

	/**
	 * Get the section at the specified path
	 *
	 * @param path to get the section from
	 * @return section at the path<br>
	 *         null if section does not exist.
	 */
	public Section getSection(String path) {
		return get(splitPath(path));
	}

	/**
	 * Get the section at the specified path, making one if it doesn't exist
	 *
	 * @param path to get the section from
	 * @return section at the path<br>
	 */
	public Section hardGetSection(String path) {
		return hardGet(splitPath(path), "");
	}

	/**
	 * If the section at the specified path exists, remove it and any existing subsections
	 *
	 * @param path to section to remove
	 */
	public void removeSection(String path) {
		remove(splitPath(path));
	}

	/**
	 * Returns true if the path specified is one that exists
	 *
	 * @param path to check for existence
	 * @return true if the path exists, otherwise false
	 */
	public boolean pathExists(String path) {
		return get(splitPath(path)) != null;
	}

	/**
	 * List all the available sections nested after the given path
	 *
	 * @param path to get the nested sections from
	 * @return Set of all the section names after the given path, null if the path does not exist
	 */
	public Set<String> listSections(String path) {
		if (path.isEmpty()) return children.keySet();
		Section sec = get(splitPath(path));
		if (sec == null) return null;
		return sec.children.keySet();
	}

	/**
	 * Set the comments of the section at the specified path
	 *
	 * @param path to the section to set the comments of
	 * @param comment strings to set as the comments for the section at the specified path
	 */
	public void setComments(String path, String... comments) {
		List<String> commented = new ArrayList<>();
		for (String cmt : comments)
			if (isCommented(cmt)) commented.add(cmt);
			else commented.add(COMMENT_INDICATOR + " " + cmt);
		setComments(splitPath(path), commented);
	}

	/**
	 * Get all the comments of the section at the specified path
	 *
	 * @param path to the section to get the comments of
	 * @return String list of comments for the section at the specified path
	 */
	public List<String> getComments(String path) {
		Section sec = get(splitPath(path));
		if (sec == null) return null;
		return sec.header;
	}

	/**
	 * Set the value at the specified path
	 *
	 * @param path to set the value of
	 * @param value to set
	 */
	public void set(String path, Object value) {
		setValue(splitPath(path), value == null ? "null" : value instanceof String ? ((String) value).replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n") : value);
	}

	/**
	 * Serialize an object into Base64 at the given path<br>
	 * Requires Java 8 or higher
	 *
	 * @param path to set the value of
	 * @param value to serialize and set
	 * @throws IOException if an I/O error occurs while writing object to output stream
	 * @throws IllegalArgumentException if object to be serialized is null
	 */
	public void setSerialized(String path, Serializable value) throws IOException {
		if (value == null) throw new IllegalArgumentException("Serializable object must not be null");
		else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
				oos.writeObject(value);
				setValue(splitPath(path), Base64.getEncoder().encodeToString(baos.toByteArray()));
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/**
	 * Get and deserialize an object from Base64 at the given path.
	 *
	 * @param path to get and deserialize the object from
	 * @return a deserialized object at the given path <br>
	 *         null if no object exists at the specified path or path doesn't exist
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found
	 * @throws IOException if an I/O error occurs while reading object from input stream
	 */
	public Object getDeserialized(String path) throws ClassNotFoundException, IOException {
		String value = getString(path);
		if (value == null || value.isEmpty()) return null;
		ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(value));
		try (ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		}
	}

	/**
	 * Get and deserialize an object from Base64 at the given path, create the path with a specific value if the path does not already exist
	 *
	 * @param path to get and deserialize the object from or set the backup value at if the path does not exist
	 * @return a deserialized object at the given path after hard get <br>
	 *         null if path exists and is empty
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found
	 * @throws IOException if an I/O error occurs while reading object from input stream
	 * @throws IllegalArgumentException if backup value needs to be used but is null
	 */
	public Object hardGetDeserialized(String path, Serializable backup) throws ClassNotFoundException, IOException {
		String value = getString(path);
		if (value == null) {
			if (backup == null) throw new IllegalArgumentException("Serializable object must not be null");
			else setSerialized(path, backup);
		} else if (value.isEmpty()) return null;
		ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(value));
		try (ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		}
	}

	/**
	 * Get the string value at a path location, and create the path with a specific value if the path does not already exist
	 *
	 * @param path to look for the value of or create if a value there does not already exist
	 * @param backup value to apply to the path if the path did not exist
	 * @return the value associated with the path after the hard get
	 */
	public String hardGetString(String path, String backup) {
		return hardGet(splitPath(path), backup).value;
	}

	/**
	 * Get the string value at the specified path
	 *
	 * @param path to get the string value at
	 * @return String value located at that path, or null if the path does not exist
	 */
	public String getString(String path) {
		Section sec = get(splitPath(path));
		if (sec == null) return null;
		return sec.value;
	}

	/**
	 * Get the Boolean value at the specified path
	 *
	 * @param path to get the boolean value at
	 * @return True if the value at that path equalsIgnoreCase("true"), null if the path does not exist
	 */
	public Boolean getBoolean(String path) {
		String value = getString(path);
		if (value == null) return null;
		return Boolean.parseBoolean(value.trim());
	}

	/**
	 * Get the Integer value at the specified path
	 *
	 * @param path to get the Integer value at
	 * @return Integer value located at that path, or null if the path does not exist
	 * @exception NumberFormatException if the value is not an integer
	 */
	public Integer getInt(String path) {
		String value = getString(path);
		if (value == null) return null;
		return Integer.parseInt(value.trim());
	}

	/**
	 * Get the Byte value at the specified path
	 *
	 * @param path to get the Byte value at
	 * @return Byte value located at that path, or null if the path does not exist
	 * @exception NumberFormatException if the value is not a byte
	 */
	public Byte getByte(String path) {
		String value = getString(path);
		if (value == null) return null;
		return Byte.parseByte(value.trim());
	}

	/**
	 * Get the Long value at the specified path
	 *
	 * @param path to get the Long value at
	 * @return Long value located at that path, or null if the path does not exist
	 * @exception NumberFormatException if the value is not a long
	 */
	public Long getLong(String path) {
		String value = getString(path);
		if (value == null) return null;
		return Long.parseLong(value.trim());
	}

	/**
	 * Get the Double value at the specified path
	 *
	 * @param path to get the Double value at
	 * @return Double value located at that path, or null if the path does not exist
	 * @exception NumberFormatException if the value is not a double
	 */
	public Double getDouble(String path) {
		String value = getString(path);
		if (value == null) return null;
		return Double.parseDouble(value.trim());
	}

	/**
	 * Get the Float value at the specified path
	 *
	 * @param path to get the Float value at
	 * @return Float value located at that path, or null if the path does not exist
	 * @exception NumberFormatException if the value is not a float
	 */
	public Float getFloat(String path) {
		String value = getString(path);
		if (value == null) return null;
		return Float.parseFloat(value.trim());
	}

	/**
	 * Get a list of strings at the specified path<br>
	 * If the format [s1, s2, s3] is followed, it will be used to parse, if not, the list elements will be parsed using whitespace
	 *
	 * @param path to the string list
	 * @return list of strings parsed at the specified path, or null if the path does not exist
	 */
	public List<String> getStringList(String path) {
		String value = getString(path);
		if (value == null) return null;
		value = value.trim();
		if (value.startsWith("[") && value.endsWith("]")) return Arrays.asList(value.substring(1, value.length() - 1).split(",\\s?"));
		return value.isEmpty() ? Arrays.asList() : Arrays.asList(value.split("\\s+"));
	}

	/**
	 * @param string to read for comments
	 * @return true if the line is whitespace or commented out with a pound sign
	 */
	private static boolean isCommented(String string) {
		return string.matches("\\s*?(" + COMMENT_INDICATOR + ".*)*");
	}

	/**
	 * Split a path string using the path char, into a string list
	 *
	 * @param string path to split
	 * @return String list of path member keys
	 */
	public static List<String> splitPath(String string) {
		int off = 0, next = 0;
		ArrayList<String> list = new ArrayList<>();
		while ((next = string.indexOf(PATH_CHAR, off)) != -1) {
			list.add(string.substring(off, next));
			off = next + 1;
		}
		if (off == 0) return Arrays.asList(string);
		list.add(string.substring(off, string.length()));
		int resultSize = list.size();
		while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
			resultSize--;
		}
		return list.subList(0, resultSize);
	}

	public Section copyTo(Section parent) {
		Section copied = new Section(parent.tab + 1, parent, key, value, new ArrayList<>(header));
		copied.footer = footer == null ? new ArrayList<>() : new ArrayList<>(footer);
		for (Entry<String, Section> child : children.entrySet())
			copied.children.put(child.getKey(), child.getValue().copyTo(copied));
		parent.addChild(copied);
		return copied;
	}
}